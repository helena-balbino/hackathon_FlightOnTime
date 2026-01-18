# ----------------------------------------------------------------------------#
# Biblioteca do Projeto FlightOnTime
# ----------------------------------------------------------------------------#
# script.py

from __future__ import annotations

# ----------------------------------------------------------------------------#
# Bibliotecas
# ----------------------------------------------------------------------------#

import os
import glob
import json
import zipfile
import pickle
from dataclasses import dataclass
from typing import List, Tuple, Optional, Dict, Any

import numpy as np
import pandas as pd

from typing import Optional, List
import pandas as pd
import matplotlib.pyplot as plt

from sklearn.model_selection import train_test_split
from sklearn.compose import ColumnTransformer
from sklearn.base import BaseEstimator, TransformerMixin
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import StandardScaler, OneHotEncoder
from sklearn.impute import SimpleImputer
from sklearn.metrics import (
    classification_report,
    confusion_matrix,
    roc_auc_score,
)



# ----------------------------------------------------------------------------#
# Para SMOTE (classificação desbalanceada)
# ----------------------------------------------------------------------------#
try:
    from imblearn.over_sampling import SMOTE
    from imblearn.pipeline import Pipeline as ImbPipeline
except Exception:
    SMOTE = None
    ImbPipeline = None




# ----------------------------------------------------------------------------#
# Configurações Básicas
# ----------------------------------------------------------------------------#

COL_DT_BASE = "partida_prevista"
TARGET_COL = "atrasado"

DEFAULT_RENAME_MAP: Dict[str, str] = {
    "ICAO Empresa Aérea": "empresa_aerea",
    "Número Voo": "numero_voo",
    "Código Autorização (DI)": "codigo_autorizacao_di",
    "Código Tipo Linha": "codigo_tipo_linha",
    "ICAO Aeródromo Origem": "aerodromo_origem",
    "ICAO Aeródromo Destino": "aerodromo_destino",
    "Partida Prevista": "partida_prevista",
    "Partida Real": "partida_real",
    "Chegada Prevista": "chegada_prevista",
    "Chegada Real": "chegada_real",
    "Situação Voo": "situacao_voo",
    "Código Justificativa": "codigo_justificativa",
}


@dataclass
class FeatureConfig:
    # Colunas usadas como numéricas/categóricas no pipeline
    numeric_features: List[str]
    categorical_features: List[str]



# ----------------------------------------------------------------------------#
# Configuração Dataset Local
# ----------------------------------------------------------------------------#

DEFAULT_BASE_PATH = "/content/dados_vra/dados_vra"
PADRAO_CSV = "VRA_*.csv"

DATETIME_COLS = [
    "partida_prevista",
    "partida_real",
    "chegada_prevista",
    "chegada_real",
]

# ----------------------------------------------------------------------------#
# Ingestão dos Dados 
# ----------------------------------------------------------------------------#

def carregar_dataset_base(
    pasta: str = DEFAULT_BASE_PATH,
    sep: str = ";",
    encoding: str = "latin-1",
    skiprows: int = 1,
    renomear: bool = True,
    converter_datas: bool = True,
) -> pd.DataFrame:
    """
    
    Espera que os dados já estejam disponíveis localmente.
    
    """

    if not os.path.exists(pasta):
        raise FileNotFoundError(
            f"Pasta de dados não encontrada: {pasta}\n"
            "Verifique se os dados estão em /content/dados_vra"
        )

    arquivos = sorted(glob.glob(os.path.join(pasta, PADRAO_CSV)))

    if not arquivos:
        raise FileNotFoundError(
            f"Nenhum arquivo encontrado em {pasta} com padrão {PADRAO_CSV}"
        )

    dfs = []
    colunas_referencia = None

    for arquivo in arquivos:
        df_temp = pd.read_csv(
            arquivo,
            sep=sep,
            encoding=encoding,
            skiprows=skiprows,
	    low_memory=False
        )

        if colunas_referencia is None:
            colunas_referencia = list(df_temp.columns)
        else:
            if list(df_temp.columns) != colunas_referencia:
                raise ValueError(
                    f"Estrutura diferente no arquivo: {arquivo}"
                )

        dfs.append(df_temp)

    df = pd.concat(dfs, ignore_index=True)

    if renomear:
        df = renomear_colunas(df)

    if converter_datas:
        for col in DATETIME_COLS:
            if col in df.columns:
                df[col] = pd.to_datetime(df[col], errors="coerce")

    return df


def renomear_colunas(
    df: pd.DataFrame,
    rename_map: Dict[str, str] = DEFAULT_RENAME_MAP
) -> pd.DataFrame:
    """
    Padroniza nomes de colunas usando um dicionário de mapeamento.
    Corrige possíveis problemas de encoding.
    """
    df = df.copy()

    # Tentativa segura de correção de encoding
    try:
        df.columns = [c.encode("latin1").decode("utf-8") for c in df.columns]
    except Exception:
        pass

    return df.rename(columns=rename_map)


# ----------------------------------------------------------------------------#
# Analise Exploratória Básica 
# ----------------------------------------------------------------------------#

def eda_viz(
    df: pd.DataFrame,
    target: Optional[str] = None,
    sample_n: int = 200_000,
    random_state: int = 42,
    top_n_cat: int = 10,
    max_num_cols: int = 6,
    max_cat_cols: int = 6,
) -> pd.DataFrame:
    """
    EDA básica e visual:
    - Mostra uma tabela-resumo por coluna
    - Mostra gráficos simples (nulos, target, numéricas, categóricas)
    - Usa amostra para plots quando o dataset é grande

    Retorna:
    - DataFrame de resumo por coluna (para exibir no notebook).
    """

    # Amostra 
    if len(df) > sample_n:
        df_plot = df.sample(n=sample_n, random_state=random_state)
        sample_used = sample_n
    else:
        df_plot = df
        sample_used = len(df)

    print(f"Shape: {df.shape} | Amostra usada nos gráficos: {sample_used:,}")

    # Tabela-resumo por coluna
    def _example_values(s: pd.Series, k: int = 3) -> str:
        vals = s.dropna().astype(str).unique()[:k]
        return ", ".join(vals) if len(vals) else "-"

    summary = pd.DataFrame({
        "dtype": df.dtypes.astype(str),
        "pct_null": (df.isna().mean() * 100).round(2),
        "n_unique": df.nunique(dropna=True),
        "example_values": [ _example_values(df[c]) for c in df.columns ]
    }).sort_values(["pct_null", "n_unique"], ascending=[False, False])

    display(summary.head(30))  # mostra Top 30 mais “problemáticas”

    # Nulos
    missing = summary["pct_null"].sort_values(ascending=False).head(20)
    fig = plt.figure(figsize=(9, 6))
    ax = fig.add_subplot(111)
    ax.barh(missing.index[::-1], missing.values[::-1])
    ax.set_title("Percentual de nulos por coluna (Top 20)")
    ax.set_xlabel("% nulos")
    plt.show()

    # Target 
    if target and target in df.columns:
        vc = df[target].value_counts(dropna=False).head(20)
        fig = plt.figure(figsize=(10, 4))
        ax = fig.add_subplot(111)
        ax.bar(vc.index.astype(str), vc.values)
        ax.set_title(f"Distribuição do target (Top 20) — {target}")
        ax.set_ylabel("contagem")
        ax.tick_params(axis="x", rotation=45)
        plt.show()

    # Numéricas (histogramas)
    num_cols = df_plot.select_dtypes(include=[np.number]).columns.tolist()
    if num_cols:
        # escolhe as com menos nulos e mais variância (pra serem “interessantes”)
        num_info = pd.DataFrame({
            "col": num_cols,
            "pct_null": (df[num_cols].isna().mean() * 100).values,
            "var": df_plot[num_cols].var(numeric_only=True).values,
        }).sort_values(["pct_null", "var"], ascending=[True, False])

        chosen_num = num_info["col"].head(min(max_num_cols, len(num_info))).tolist()
        print("Numéricas plotadas:", chosen_num)

        for c in chosen_num:
            s = df_plot[c].dropna()
            if s.empty:
                continue
            fig = plt.figure(figsize=(10, 4))
            ax = fig.add_subplot(111)
            ax.hist(s.values, bins=50)
            ax.set_title(f"Histograma — {c} (amostra)")
            ax.set_ylabel("frequência")
            plt.show()

    # Categóricas
    cat_cols = df_plot.select_dtypes(include=["object", "category"]).columns.tolist()
    if cat_cols:
        cat_info = pd.DataFrame({
            "col": cat_cols,
            "n_unique": [df[c].nunique(dropna=True) for c in cat_cols],
            "pct_null": [df[c].isna().mean() * 100 for c in cat_cols],
        }).sort_values(["n_unique", "pct_null"], ascending=[True, True])

        chosen_cat = cat_info["col"].head(min(max_cat_cols, len(cat_info))).tolist()
        print("Categóricas plotadas:", chosen_cat)

        for c in chosen_cat:
            vc = df_plot[c].value_counts(dropna=False).head(top_n_cat)
            fig = plt.figure(figsize=(10, 4))
            ax = fig.add_subplot(111)
            ax.bar(vc.index.astype(str), vc.values)
            ax.set_title(f"Top {top_n_cat} categorias — {c} (amostra)")
            ax.set_ylabel("contagem")
            ax.tick_params(axis="x", rotation=45)
            plt.show()

    return summary


def salvar_json(dados: Dict[str, Any], caminho: str) -> None:
    os.makedirs(os.path.dirname(caminho) or ".", exist_ok=True)
    with open(caminho, "w", encoding="utf-8") as f:
        json.dump(dados, f, ensure_ascii=False, indent=2)




# ----------------------------------------------------------------------------#
# Feature Engineering
# ----------------------------------------------------------------------------#

def criar_flags_qualidade_basicas(df: pd.DataFrame) -> pd.DataFrame:
    """
    Exemplo básico. Ajuste as regras conforme seu dataset.
    Converte datas e cria flags de consistência.
    """
    df = df.copy()

    # Converte datas
    for col in ["partida_prevista", "partida_real", "chegada_prevista", "chegada_real"]:
        if col in df.columns:
            df[col] = pd.to_datetime(df[col], errors="coerce")

    df["flag_partida_prevista_ausente"] = df.get("partida_prevista").isna() if "partida_prevista" in df else False
    df["flag_partida_real_ausente"] = df.get("partida_real").isna() if "partida_real" in df else False

    # Datas fora de faixa 
    if "partida_prevista" in df:
        df["flag_data_fora_periodo"] = df["partida_prevista"].notna() & (
            (df["partida_prevista"].dt.year < 2021) | (df["partida_prevista"].dt.year > 2025)
        )
    else:
        df["flag_data_fora_periodo"] = False

    return df


def criar_target_atrasado(df: pd.DataFrame, limite_min: int = 15) -> pd.DataFrame:
    """
    Cria target binário 'atrasado': 1 se atraso de partida > limite_min.
    Requer colunas datetime: partida_prevista, partida_real
    """
    df = df.copy()

    if "partida_prevista" not in df.columns or "partida_real" not in df.columns:
        raise ValueError("Colunas necessárias não encontradas: 'partida_prevista' e 'partida_real'.")

    df["atraso_partida_min"] = (df["partida_real"] - df["partida_prevista"]).dt.total_seconds() / 60

    # Se existirem flags, podemos filtrar 
    filtros = pd.Series(True, index=df.index)
    for flag in ["flag_partida_prevista_ausente", "flag_partida_real_ausente", "flag_data_fora_periodo"]:
        if flag in df.columns:
            filtros &= ~df[flag].fillna(False)

    df = df[filtros].copy()
    df[TARGET_COL] = (df["atraso_partida_min"] > limite_min).astype(int)
    return df


class DatasFeaturesTransformer(BaseEstimator, TransformerMixin):
    """
    Cria features temporais a partir de 'partida_prevista'.
    Usa a mesma lógica da equipe (hora/dia/mes + período + fim de semana + alta temporada).
    """

    def __init__(
        self,
        col_dt: str = "partida_prevista",
        col_atraso: str = "atraso_partida_min",
    ):
        self.col_dt = col_dt
        self.col_atraso = col_atraso

    @staticmethod
    def _classificar_periodo(hora: int) -> str:
        if 5 <= hora < 12:
            return "Manha"
        if 12 <= hora < 18:
            return "Tarde"
        if 18 <= hora < 22:
            return "Noite"
        return "Madrugada"

    def fit(self, X, y=None):
        return self

    def transform(self, X: pd.DataFrame) -> pd.DataFrame:
        X = X.copy()

        # Garantir datetime
        X[self.col_dt] = pd.to_datetime(X[self.col_dt], errors="coerce")

        X["hora_dia"] = X[self.col_dt].dt.hour
        X["dia_semana"] = X[self.col_dt].dt.dayofweek
        X["mes_ano"] = X[self.col_dt].dt.month

        X["periodo_dia"] = X["hora_dia"].apply(self._classificar_periodo)
        X["fim_de_semana"] = X["dia_semana"].isin([4, 5, 6]).astype(int)
        X["alta_temporada"] = X["mes_ano"].isin([7, 12]).astype(int)

        # Transformações de atraso (se existir)
        if self.col_atraso in X.columns:
            X["atraso_log"] = np.log1p(np.maximum(X[self.col_atraso], 0))
            X["atraso_capped"] = np.clip(X[self.col_atraso], 0, 120)

        return X



class MediaAtrasoTransformer(BaseEstimator, TransformerMixin):
    """
    Cria três features numéricas com médias de atraso (em minutos):
    - media_atraso_empresa
    - media_atraso_origem
    - media_atraso_destino

    Aprende as médias no fit (treino) e reaplica no transform (teste/produção).
    """

    def __init__(
        self,
        col_atraso: str = "atraso_partida_min",
        col_empresa: str = "empresa_aerea",
        col_origem: str = "aerodromo_origem",
        col_destino: str = "aerodromo_destino",
    ):
        self.col_atraso = col_atraso
        self.col_empresa = col_empresa
        self.col_origem = col_origem
        self.col_destino = col_destino

        self.medias_empresa_ = {}
        self.medias_origem_ = {}
        self.medias_destino_ = {}
        self.media_global_ = 0.0

    def fit(self, X: pd.DataFrame, y=None):
        X = X.copy()

        if self.col_atraso not in X.columns:
            raise ValueError(f"Coluna '{self.col_atraso}' não encontrada para calcular médias.")

        self.medias_empresa_ = X.groupby(self.col_empresa)[self.col_atraso].mean().to_dict()
        self.medias_origem_ = X.groupby(self.col_origem)[self.col_atraso].mean().to_dict()
        self.medias_destino_ = X.groupby(self.col_destino)[self.col_atraso].mean().to_dict()
        self.media_global_ = float(X[self.col_atraso].mean())

        return self

    def transform(self, X: pd.DataFrame) -> pd.DataFrame:
        X = X.copy()

        X["media_atraso_empresa"] = (
            X[self.col_empresa].map(self.medias_empresa_).fillna(self.media_global_)
        )
        X["media_atraso_origem"] = (
            X[self.col_origem].map(self.medias_origem_).fillna(self.media_global_)
        )
        X["media_atraso_destino"] = (
            X[self.col_destino].map(self.medias_destino_).fillna(self.media_global_)
        )

        return X

class UltimateFeatureEngineer(BaseEstimator, TransformerMixin):
    """
    Implementação de Features de Alta Densidade de Sinal:
    - Periodicidade Cíclica (Seno/Cosseno)
    - Identificação de Hubs Estratégicos
    - Sazonalidade de Negócio
    """
    def fit(self, X, y=None): return self

    def transform(self, X):
        X_out = X.copy()
        # 1. Garantir colunas temporais
        X_out['hora_dia'] = X_out['partida_prevista'].dt.hour

        # 2. Encoding Cíclico
        X_out['hora_sin'] = np.sin(2 * np.pi * X_out['hora_dia'] / 24)
        X_out['hora_cos'] = np.cos(2 * np.pi * X_out['hora_dia'] / 24)

        # 3. Sinal de Aeroportos de Alta Movimentação (Hubs Nacionais)
        hubs = ['SBGR', 'SBSP', 'SBGL', 'SBRJ', 'SBCF', 'SBKP']
        X_out['is_hub'] = (X_out['aerodromo_origem'].isin(hubs) |
                           X_out['aerodromo_destino'].isin(hubs)).astype(int)

        return X_out


# ----------------------------------------------------------------------------#
# Split
# ----------------------------------------------------------------------------#

def criar_split_estratificado(
    df: pd.DataFrame,
    coluna_target: str = TARGET_COL,
    test_size: float = 0.2,
    random_state: int = 42,
) -> Tuple[pd.DataFrame, pd.DataFrame]:
    """
    Split estratificado treino e/ou teste.
    """
    if coluna_target not in df.columns:
        raise ValueError(f"Target '{coluna_target}' não encontrado no DataFrame.")

    df_train, df_test = train_test_split(
        df,
        test_size=test_size,
        random_state=random_state,
        stratify=df[coluna_target],
    )
    return df_train, df_test


# ----------------------------------------------------------------------------#
# Pré-processamento (imputação + encoding + normalização)
# ----------------------------------------------------------------------------#

def montar_preprocessador(cfg: FeatureConfig) -> ColumnTransformer:
    """
    - Numéricas: imputação mediana + StandardScaler
    - Categóricas: imputação constante + OneHotEncoder
    Obs: remainder="drop" => qualquer coluna fora do cfg será descartada.
    """
    numeric_pipe = Pipeline(
        steps=[
            ("imputer", SimpleImputer(strategy="median")),
            ("scaler", StandardScaler()),
        ]
    )

    categorical_pipe = Pipeline(
        steps=[
            ("imputer", SimpleImputer(strategy="constant", fill_value="DESCONHECIDO")),
            ("ohe", OneHotEncoder(handle_unknown="ignore", min_frequency=10)),  # ajuste se quiser
        ]
    )

    pre = ColumnTransformer(
        transformers=[
            ("num", numeric_pipe, cfg.numeric_features),
            ("cat", categorical_pipe, cfg.categorical_features),
        ],
        remainder="drop",
    )
    return pre


# ----------------------------------------------------------------------------#
# Treinamento + SMOTE (classificação)
# ----------------------------------------------------------------------------#

def treinar_classificador(
    df_train: pd.DataFrame,
    df_test: pd.DataFrame,
    cfg: FeatureConfig,
    model,
    target: str = TARGET_COL,
    use_smote: bool = False,
    smote_k_neighbors: int = 5,
) -> Dict[str, Any]:
    """
    Ordem do pipeline:
    1) Feature Engineering (datas + médias)
    2) Pré-processamento (imputação + scaler + one-hot)
    3) (Opcional) SMOTE
    4) Modelo
    """

    # Não selecione só cfg aqui, pois o FE precisa de colunas extras (ex: partida_prevista).
    X_train = df_train.drop(columns=[target]).copy()
    y_train = df_train[target].astype(int).copy()

    X_test = df_test.drop(columns=[target]).copy()
    y_test = df_test[target].astype(int).copy()

    # 1) Feature Engineering (trabalho da equipe)
    fe = Pipeline(steps=[
        ("datas", DatasFeaturesTransformer(
            col_dt="partida_prevista",
            col_atraso="atraso_partida_min"  # se isso gerar vazamento no seu caso, remova do transformer
        )),
        ("ultimate", UltimateFeatureEngineer()),
        ("medias", MediaAtrasoTransformer(
            col_atraso="atraso_partida_min"
        )),
    ])

    # 2) Pré-processador (usa só as features do cfg)
    pre = montar_preprocessador(cfg)

    # 3) Pipeline final
    if use_smote:
        if SMOTE is None or ImbPipeline is None:
            raise ImportError("imblearn não disponível. Instale: pip install imbalanced-learn")

        pipe = ImbPipeline(steps=[
            ("fe", fe),
            ("pre", pre),
            ("smote", SMOTE(k_neighbors=smote_k_neighbors, random_state=42)),
            ("model", model),
        ])
    else:
        pipe = Pipeline(steps=[
            ("fe", fe),
            ("pre", pre),
            ("model", model),
        ])

    pipe.fit(X_train, y_train)

    y_pred = pipe.predict(X_test)

    resultados = {
        "classification_report": classification_report(y_test, y_pred, output_dict=True),
        "confusion_matrix": confusion_matrix(y_test, y_pred).tolist(),
    }

    if hasattr(pipe, "predict_proba"):
        proba = pipe.predict_proba(X_test)[:, 1]
        try:
            resultados["roc_auc"] = float(roc_auc_score(y_test, proba))
        except Exception:
            resultados["roc_auc"] = None

    return {
        "pipeline": pipe, 
        "metrics": resultados,
        "y_test": y_test.to_numpy(),
        "y_pred": y_pred
        }

# ----------------------------------------------------------------------------#
# Avaliação do Modelo
# ----------------------------------------------------------------------------#

def extrair_metricas(out: dict, positive_label="1") -> dict:
    m = out["metrics"]
    rep = m["classification_report"]  # dict
    cm = np.array(m["confusion_matrix"])

    # Suporte/label pode ser "1" ou 1 dependendo do sklearn
    cls = rep.get(positive_label, rep.get(int(positive_label), {}))

    return {
        "accuracy": rep.get("accuracy", np.nan),
        "precision_pos": cls.get("precision", np.nan),
        "recall_pos": cls.get("recall", np.nan),
        "f1_pos": cls.get("f1-score", np.nan),
        "support_pos": cls.get("support", np.nan),
        "f1_macro": rep.get("macro avg", {}).get("f1-score", np.nan),
        "f1_weighted": rep.get("weighted avg", {}).get("f1-score", np.nan),
        "roc_auc": m.get("roc_auc", np.nan),
        "cm": cm,
    }

def plot_cm_2x2(cm_dict, title="Matrizes de Confusão (2×2)"):
    """
    cm_dict: dict {nome_modelo: cm(np.array)}
    Plota em painel 2x2 com labels e contagens.
    """
    nomes = list(cm_dict.keys())
    n = len(nomes)

    # força 2x2 (se tiver menos de 4, deixa os vazios)
    fig, axes = plt.subplots(2, 2, figsize=(12, 9))
    axes = axes.ravel()

    for i, ax in enumerate(axes):
        if i >= n:
            ax.axis("off")
            continue

        nome = nomes[i]
        cm = cm_dict[nome]

        disp = ConfusionMatrixDisplay(confusion_matrix=cm, display_labels=["0", "1"])
        disp.plot(ax=ax, values_format="d", colorbar=False)
        ax.set_title(nome)

        # melhora legibilidade
        ax.set_xlabel("Predito")
        ax.set_ylabel("Real")

    fig.suptitle(title, fontsize=14)
    plt.tight_layout()
    plt.show()

def plot_cm_percent_2x2(cm_dict, title="Matrizes de Confusão (Percentual por classe real)"):
    """
    Mostra percentuais por linha (cada classe real soma 100%).
    """
    nomes = list(cm_dict.keys())
    n = len(nomes)

    fig, axes = plt.subplots(2, 2, figsize=(12, 9))
    axes = axes.ravel()

    for i, ax in enumerate(axes):
        if i >= n:
            ax.axis("off")
            continue

        nome = nomes[i]
        cm = cm_dict[nome].astype(float)
        row_sums = cm.sum(axis=1, keepdims=True)
        cm_pct = np.divide(cm, row_sums, out=np.zeros_like(cm), where=row_sums != 0) * 100

        im = ax.imshow(cm_pct)
        ax.set_title(nome)
        ax.set_xlabel("Predito")
        ax.set_ylabel("Real")
        ax.set_xticks([0, 1]); ax.set_yticks([0, 1])
        ax.set_xticklabels(["0", "1"]); ax.set_yticklabels(["0", "1"])

        # anota com % e contagem
        cm_counts = cm_dict[nome]
        for r in range(2):
            for c in range(2):
                ax.text(c, r, f"{cm_pct[r, c]:.1f}%\n({cm_counts[r, c]})",
                        ha="center", va="center")

        fig.colorbar(im, ax=ax, fraction=0.046, pad=0.04)

    fig.suptitle(title, fontsize=14)
    plt.tight_layout()
    plt.show()

# ----------------------------------------------------------------------------#
# Salvar e Carregar Modelo
# ----------------------------------------------------------------------------#

def salvar_pickle(obj, path: str) -> None:
    os.makedirs(os.path.dirname(path) or ".", exist_ok=True)
    with open(path, "wb") as f:
        pickle.dump(obj, f)


def carregar_pickle(path: str):
    with open(path, "rb") as f:
        return pickle.load(f)




# ----------------------------------------------------------------------------#
# Micro Serviço Básico
# ----------------------------------------------------------------------------#

def criar_app_fastapi(modelo_path: str):
    """
    Cria um app FastAPI que carrega o pipeline salvo e expõe /predict.
    Importante: o pipeline salvo inclui Feature Engineering,
    então o input do /predict deve conter as colunas brutas.
    """
    from fastapi import FastAPI, HTTPException, Body
    import pandas as pd

    app = FastAPI(title="FlightOnTime - API", version="0.1")

    # carrega pipeline treinado
    pipeline = carregar_pickle(modelo_path)

    # colunas mínimas exigidas pelo Feature Engineering
    REQUIRED_RAW_COLS = [
        "partida_prevista",
        "empresa_aerea",
        "aerodromo_origem",
        "aerodromo_destino",
        "situacao_voo",
    ]

    @app.get("/health")
    def health():
        return {"status": "ok"}

    @app.post("/predict")
    def predict(payload: dict = Body(...)):
        """
        Espera payload no formato:
        {
          "dados": {
            "partida_prevista": "...",
            "empresa_aerea": "...",
            "aerodromo_origem": "...",
            "aerodromo_destino": "...",
            "situacao_voo": "..."
          }
        }
        """

        if "dados" not in payload:
            raise HTTPException(
                status_code=400,
                detail="Payload deve conter a chave 'dados'."
            )

        x = pd.DataFrame([payload["dados"]])

        # valida colunas mínimas
        faltando = [c for c in REQUIRED_RAW_COLS if c not in x.columns]
        if faltando:
            raise HTTPException(
                status_code=400,
                detail=f"Faltando colunas obrigatórias no payload: {faltando}",
            )

        # previsão
        pred = int(pipeline.predict(x)[0])
        label_map = {
            0: "no_praso",
            1: "atrasado",
        }

        label = label_map.get(pred, str(pred))

        resp = {
            "prediction": pred,
            "label": label
                }

        if hasattr(pipeline, "predict_proba"):
            resp["proba_atraso"] = float(pipeline.predict_proba(x)[0, 1])

        return resp

    return app
