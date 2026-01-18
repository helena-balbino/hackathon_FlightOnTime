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
        self.is_fitted_ = True
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
        self.is_fitted_ = True
        
        return self

    def transform(self, X: pd.DataFrame) -> pd.DataFrame:
        from sklearn.utils.validation import check_is_fitted
        check_is_fitted(self, "is_fitted_")
        
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
    def fit(self, X, y=None): 
        self.is_fitted_ = True
        return self

    def transform(self, X):
        X_out = X.copy()
        # 1. Garantir colunas temporais
        X_out['partida_prevista'] = pd.to_datetime(X_out['partida_prevista'], errors='coerce')
        X_out['hora_dia'] = X_out['partida_prevista'].dt.hour
        
        # 2. Encoding Cíclico
        X_out['hora_sin'] = np.sin(2 * np.pi * X_out['hora_dia'] / 24)
        X_out['hora_cos'] = np.cos(2 * np.pi * X_out['hora_dia'] / 24)

        # 3. Sinal de Aeroportos de Alta Movimentação (Hubs Nacionais)
        hubs = ['SBGR', 'SBSP', 'SBGL', 'SBRJ', 'SBCF', 'SBKP']
        X_out['is_hub'] = (X_out['aerodromo_origem'].isin(hubs) |
                           X_out['aerodromo_destino'].isin(hubs)).astype(int)

        return X_out

class DropColumnsTransformer(BaseEstimator, TransformerMixin):
    """
    Remove explicitamente colunas indesejadas (ex.: leakage).
    Garante blindagem do pipeline.
    """

    def __init__(self, columns_to_drop: list[str]):
        self.columns_to_drop = columns_to_drop

    def fit(self, X, y=None):
        self.is_fitted_ = True
        return self

    def transform(self, X: pd.DataFrame) -> pd.DataFrame:
        X = X.copy()
        cols_existentes = [c for c in self.columns_to_drop if c in X.columns]
        return X.drop(columns=cols_existentes)

class FeatureEngineeringTransformer(BaseEstimator, TransformerMixin):
    """
    Aplica o Feature Engineering em sequência (mesma lógica do seu 'fe' atual),
    mas sem usar sklearn.Pipeline — necessário para funcionar dentro do ImbPipeline (SMOTE).
    """

    def __init__(
        self,
        drop: list[str],
        col_dt: str = "partida_prevista",
        col_atraso: str = "atraso_partida_min",
    ):
        self.drop = drop
        self.col_dt = col_dt
        self.col_atraso = col_atraso

        # mesmos transformers que você já usa
        self._datas = DatasFeaturesTransformer(col_dt=col_dt, col_atraso=col_atraso)
        self._ultimate = UltimateFeatureEngineer()
        self._medias = MediaAtrasoTransformer(col_atraso=col_atraso)
        self._drop = DropColumnsTransformer(drop)

    def fit(self, X: pd.DataFrame, y=None):
        X_ = X.copy()

        # 1) datas
        self._datas.fit(X_, y)
        X_ = self._datas.transform(X_)

        # 2) ultimate
        self._ultimate.fit(X_, y)
        X_ = self._ultimate.transform(X_)

        # 3) medias (precisa do X já enriquecido)
        self._medias.fit(X_, y)
        X_ = self._medias.transform(X_)

        # 4) drop leakage
        self._drop.fit(X_, y)

        self.is_fitted_ = True
        return self

    def transform(self, X: pd.DataFrame) -> pd.DataFrame:
        from sklearn.utils.validation import check_is_fitted
        check_is_fitted(self, "is_fitted_")

        X_ = X.copy()
        X_ = self._datas.transform(X_)
        X_ = self._ultimate.transform(X_)
        X_ = self._medias.transform(X_)
        X_ = self._drop.transform(X_)
        return X_
    
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

def criar_split_temporal_train_val_test(
    df: pd.DataFrame,
    time_col: str,
    train_size: float = 0.7,
    val_size: float = 0.1,
):
    """
    Realiza split temporal respeitando a ordem do tempo.
    
    Retorna DataFrames completos (features + target),
    compatíveis com treinar_classificador.
    """
    
    # 1) Garantir ordenação temporal
    df_sorted = (
        df.sort_values(time_col)
          .reset_index(drop=True)
          .copy()
    )

    # 2) Tamanhos dos splits
    n_total = len(df_sorted)
    train_end = int(n_total * train_size)
    val_end = int(n_total * (train_size + val_size))

    # 3) Split respeitando o tempo
    df_train = df_sorted.iloc[:train_end].copy()
    df_val = df_sorted.iloc[train_end:val_end].copy()
    df_test = df_sorted.iloc[val_end:].copy()

    # 4) Sanity check mínimo
    assert df_train[time_col].max() <= df_val[time_col].min(), "Vazamento temporal treino → validação"
    assert df_val[time_col].max() <= df_test[time_col].min(), "Vazamento temporal validação → teste"

    return df_train, df_val, df_test


def criar_split_temporal_train_val_test_mod(
    df: pd.DataFrame,
    time_col: str,
    cutoff_train: str = "2022-12-31",
    cutoff_val: str = "2023-12-31",
):
    """
    Realiza split temporal respeitando datas de corte definidas.

    Parâmetros
    ----------
    df : pd.DataFrame
        DataFrame completo com features + target.
    time_col : str
        Nome da coluna datetime usada para ordenação temporal (ex: 'partida_prevista').
    cutoff_train : str
        Data limite do conjunto de treino (formato 'YYYY-MM-DD').
    cutoff_val : str
        Data limite do conjunto de validação (val = cutoff_train < data <= cutoff_val).

    Retorna
    -------
    df_train, df_val, df_test : pd.DataFrame
        DataFrames completos (features + target)
    """

    # Converter os cutoffs para datetime
    cutoff_train = pd.to_datetime(cutoff_train)
    cutoff_val = pd.to_datetime(cutoff_val)


    # Garantir que a coluna de tempo é datetime
    df_sorted = df.copy()
    df_sorted = df_sorted.sort_values(time_col).reset_index(drop=True)
    if not np.issubdtype(df_sorted[time_col].dtype, np.datetime64):
        df_sorted[time_col] = pd.to_datetime(df_sorted[time_col])

    # 1) Split por cutoff
    df_train = df_sorted[df_sorted[time_col] <= cutoff_train].copy()
    df_val   = df_sorted[(df_sorted[time_col] > cutoff_train) & (df_sorted[time_col] <= cutoff_val)].copy()
    df_test  = df_sorted[df_sorted[time_col] > cutoff_val].copy()

    # 2) Sanity checks mínimos
    assert df_train[time_col].max() <= df_val[time_col].min(), "Vazamento temporal treino → validação"
    assert df_val[time_col].max() <= df_test[time_col].min(), "Vazamento temporal validação → teste"


    return df_train, df_val, df_test


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
            ("ohe", OneHotEncoder(handle_unknown="ignore", min_frequency=500)),  
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
    drop: list | None = None,
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

    if drop is None:
        drop = cols_removida_modelagem

    # 1) Feature Engineering (trabalho da equipe)
    fe = FeatureEngineeringTransformer(
        drop=drop,
        col_dt="partida_prevista",
        col_atraso="atraso_partida_min",
        )

    # 2) Pré-processador (usa só as features do cfg)
    pre = montar_preprocessador(cfg)

    # 3) Pipeline final
    if use_smote:
        if SMOTE is None or ImbPipeline is None:
            raise ImportError("imblearn não disponível. Instale: pip install imbalanced-learn")

        # ajusta os steps do fe 
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
    print("-"*75, "\n", model, "\n", "-"*75)

    pipe.fit(X_train, y_train)
    print("Fit OK. Pipeline fitted.")
    
    X_train_fe = pipe.named_steps["fe"].transform(X_train)
    faltando_cfg = [c for c in (cfg.numeric_features + cfg.categorical_features) if c not in X_train_fe.columns]
    print("Faltando para o preprocessador:", faltando_cfg[:20])
    
    from sklearn.utils.validation import check_is_fitted
    check_is_fitted(pipe)
    check_is_fitted(pipe.named_steps["fe"])
    check_is_fitted(pipe.named_steps["pre"])
    print("Check de fitagem OK.")
    
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
    from sklearn.metrics import ConfusionMatrixDisplay
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

def metrics_from_preds(y_true, y_pred, y_proba=None):
    """Calcula métricas no mesmo formato do seu scr.extrair_metricas (sem precisar 'na mão')."""
    from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score, roc_auc_score
    out = {
        "accuracy": float(accuracy_score(y_true, y_pred)),
        "precision_pos": float(precision_score(y_true, y_pred, pos_label=1, zero_division=0)),
        "recall_pos": float(recall_score(y_true, y_pred, pos_label=1, zero_division=0)),
        "f1_pos": float(f1_score(y_true, y_pred, pos_label=1, zero_division=0)),
        "f1_macro": float(f1_score(y_true, y_pred, average="macro", zero_division=0)),
        "f1_weighted": float(f1_score(y_true, y_pred, average="weighted", zero_division=0)),
        "roc_auc": np.nan,
    }
    if y_proba is not None:
        try:
            out["roc_auc"] = float(roc_auc_score(y_true, y_proba))
        except Exception:
            out["roc_auc"] = np.nan
    return out

def ensure_best_pipe(best_pipe=None, gs=None):
    if best_pipe is not None:
        return best_pipe
    if gs is not None and hasattr(gs, "best_estimator_"):
        return gs.best_estimator_
    raise ValueError("Não foi encontrado best_pipe nem gs.best_estimator_. Rode o GridSearch antes ou passe best_pipe.")

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
# Explicabilidade das Features
# ----------------------------------------------------------------------------#
def explicar_local_xgb(
    pipeline: Pipeline,
    x_raw: pd.DataFrame,
    top_k: int = 8,
) -> dict:
    """
    Explicabilidade local para XGBoost usando contribuições nativas (pred_contribs=True).

    Retorna:
      {
        "top_features": [
          {"feature": "...", "contribution": 0.12, "direction": "increase", "value": 1.0},
          ...
        ],
        "bias": -0.34
      }
    """
    # steps
    fe = pipeline.named_steps.get("fe")
    pre = pipeline.named_steps.get("pre")
    model = pipeline.named_steps.get("model")

    if fe is None or pre is None or model is None:
        raise ValueError("Pipeline precisa ter steps: 'fe', 'pre', 'model'.")

    # garante DataFrame
    if not isinstance(x_raw, pd.DataFrame):
        x_raw = pd.DataFrame(x_raw)

    # featuring engereneering and pre-processamento
    x_fe = fe.transform(x_raw)
    Xp = pre.transform(x_fe)  

    # 2features finais
    try:
        feature_names = pre.get_feature_names_out()
    except Exception:
        feature_names = np.array([f"f{i}" for i in range(Xp.shape[1])])

    # contribuições do modelo final
    try:
        import xgboost as xgb
    except ImportError as e:
        raise ImportError("xgboost não instalado no ambiente.") from e

    booster = model.get_booster() if hasattr(model, "get_booster") else None
    if booster is None:
        raise ValueError("Modelo não parece ser XGBoost sklearn (não tem get_booster).")

    dmat = xgb.DMatrix(Xp, feature_names=list(feature_names))
    contrib = booster.predict(dmat, pred_contribs=True)  

    # separa bias e features
    contrib = np.asarray(contrib)
    bias = float(contrib[0, -1])
    contrib_feats = contrib[0, :-1]

    # valores da linha
    def _get_value(idx: int) -> float:
        try:
            return float(Xp[0, idx])
        except Exception:
            return float(Xp[0].toarray()[0, idx])

    # top_k por contribuições
    order = np.argsort(np.abs(contrib_feats))[::-1][:top_k]
    top = []
    for idx in order:
        c = float(contrib_feats[idx])
        top.append({
            "feature": str(feature_names[idx]),
            "contribution": c,
            "direction": "increase" if c >= 0 else "decrease",
            "value": _get_value(int(idx)),
        })

    return {"top_features": top, "bias": bias}


def explicar_global_xgb(
    pipeline: Pipeline,
    top_n: int = 30,
    importance_type: str = "gain",
) -> pd.DataFrame:
    """
    Explicabilidade global modelo final.
    Retorna DataFrame com as top_n features.
    """
    pre = pipeline.named_steps.get("pre")
    model = pipeline.named_steps.get("model")

    if pre is None or model is None:
        raise ValueError("Pipeline precisa ter steps: 'pre' e 'model'.")

    try:
        feature_names = list(pre.get_feature_names_out())
    except Exception:
        feature_names = None

    booster = model.get_booster() if hasattr(model, "get_booster") else None
    if booster is None:
        raise ValueError("Modelo é XGBoost sklearn (não tem get_booster).")

    score_dict = booster.get_score(importance_type=importance_type)
    
    # mapeamento das features
    rows = []
    for k, v in score_dict.items():
        if k.startswith("f") and k[1:].isdigit() and feature_names is not None:
            idx = int(k[1:])
            fname = feature_names[idx] if idx < len(feature_names) else k
        else:
            fname = k
        rows.append((fname, float(v)))

    df_imp = pd.DataFrame(rows, columns=["feature", "importance"]).sort_values("importance", ascending=False)
    
    return df_imp.head(top_n).reset_index(drop=True)


def _get_steps(pipeline: Pipeline):
    fe = pipeline.named_steps.get("fe")
    pre = pipeline.named_steps.get("pre")
    model = pipeline.named_steps.get("model")
    if fe is None or pre is None or model is None:
        raise ValueError("Pipeline precisa ter steps: 'fe', 'pre', 'model'.")
    return fe, pre, model


def _ensure_df(x_raw) -> pd.DataFrame:
    if isinstance(x_raw, pd.DataFrame):
        return x_raw
    return pd.DataFrame(x_raw)


def _transform_1row(pipeline: Pipeline, x_raw: pd.DataFrame):
    fe, pre, _ = _get_steps(pipeline)
    x_fe = fe.transform(x_raw)
    Xp = pre.transform(x_fe)
    return Xp


def _get_feature_names(pre, n_features: int) -> np.ndarray:
    try:
        names = np.array(pre.get_feature_names_out())
        if len(names) == n_features:
            return names
    except Exception:
        pass
    return np.array([f"f{i}" for i in range(n_features)])


def _get_value_from_Xp(Xp, idx: int) -> float:
    # Xp pode ser numpy, scipy sparse, etc.
    try:
        return float(Xp[0, idx])
    except Exception:
        return float(Xp[0].toarray()[0, idx])


def _is_xgboost_model(model) -> bool:
    return hasattr(model, "get_booster")


def _is_sklearn_tree_model(model) -> bool:
    # RandomForest, ExtraTrees, GradientBoosting, etc.
    return hasattr(model, "feature_importances_")


# Explicabilidade local unificada
def explicar_local_unificado(
    pipeline: Pipeline,
    x_raw: pd.DataFrame,
    top_k: int = 8,
    *,
    class_index: int | None = None,
) -> dict:
    """
    Explicabilidade local unificada:
      - XGBoost sklearn: contribuições nativas via pred_contribs=True
      - Árvores sklearn (RF/ET/GBDT): SHAP TreeExplainer

    Retorna:
      {
        "method": "xgboost_pred_contribs" | "shap_treeexplainer",
        "top_features": [
          {"feature": "...", "contribution": 0.12, "direction": "increase", "value": 1.0},
          ...
        ],
        "bias": <float>
      }
    """
    fe, pre, model = _get_steps(pipeline)

    x_raw = _ensure_df(x_raw)
    Xp = _transform_1row(pipeline, x_raw)

    n_features = Xp.shape[1]
    feature_names = _get_feature_names(pre, n_features)

    
    if _is_xgboost_model(model):
        try:
            import xgboost as xgb
        except ImportError as e:
            raise ImportError("xgboost não instalado no ambiente.") from e

        booster = model.get_booster()
        dmat = xgb.DMatrix(Xp, feature_names=list(feature_names))
        contrib = booster.predict(dmat, pred_contribs=True)

        contrib = np.asarray(contrib)
        bias = float(contrib[0, -1])
        contrib_feats = contrib[0, :-1]

        order = np.argsort(np.abs(contrib_feats))[::-1][:top_k]
        top = []
        for idx in order:
            c = float(contrib_feats[idx])
            top.append({
                "feature": str(feature_names[idx]),
                "contribution": c,
                "direction": "increase" if c >= 0 else "decrease",
                "value": _get_value_from_Xp(Xp, int(idx)),
            })

        return {"method": "xgboost_pred_contribs", "top_features": top, "bias": bias}

    
    if _is_sklearn_tree_model(model):
        try:
            import shap
        except ImportError as e:
            raise ImportError("shap não instalado no ambiente. Instale com: pip install shap") from e

        explainer = shap.TreeExplainer(model)
        sv = explainer.shap_values(Xp)
        expected = explainer.expected_value

        # Normaliza para um vetor (n_features,)
        if isinstance(sv, list):
            # classificação: lista por classe
            if class_index is None:
                # binário: normalmente 2 classes -> pega a classe 1
                # multiclasse: pega a última por padrão (você pode setar class_index)
                class_index = 1 if len(sv) == 2 else (len(sv) - 1)

            shap_vec = np.asarray(sv[class_index])[0]
            if isinstance(expected, (list, np.ndarray)):
                bias = float(np.asarray(expected)[class_index])
            else:
                bias = float(expected)
        else:
            # regressão (ou alguns casos de classificação dependendo da lib)
            shap_vec = np.asarray(sv)[0]
            if isinstance(expected, (list, np.ndarray)):
                bias = float(np.asarray(expected).ravel()[0])
            else:
                bias = float(expected)

        order = np.argsort(np.abs(shap_vec))[::-1][:top_k]
        top = []
        for idx in order:
            c = float(shap_vec[idx])
            top.append({
                "feature": str(feature_names[idx]),
                "contribution": c,
                "direction": "increase" if c >= 0 else "decrease",
                "value": _get_value_from_Xp(Xp, int(idx)),
            })

        return {"method": "shap_treeexplainer", "top_features": top, "bias": bias}

    raise ValueError(
        "Modelo não suportado pela explicabilidade unificada. "
        "Suportado: XGBoost sklearn (get_booster) ou árvore sklearn (feature_importances_)."
    )


# Explicabilidade global unificada
def explicar_global_unificado(
    pipeline: Pipeline,
    top_n: int = 30,
    *,
    importance_type: str = "gain",  # usado apenas em XGBoost
) -> pd.DataFrame:
    """
    Explicabilidade global unificada:
      - XGBoost sklearn: booster.get_score(importance_type=...)
      - Árvores sklearn (RF/ET/GBDT): feature_importances_

    Retorna DataFrame com top_n features: feature | importance
    """
    _, pre, model = _get_steps(pipeline)

    # tenta nomes
    try:
        feature_names = list(pre.get_feature_names_out())
    except Exception:
        feature_names = None

    if _is_xgboost_model(model):
        booster = model.get_booster()
        score_dict = booster.get_score(importance_type=importance_type)

        rows = []
        for k, v in score_dict.items():
            # mapeia f0,f1,... para nomes reais quando possível
            if k.startswith("f") and k[1:].isdigit() and feature_names is not None:
                idx = int(k[1:])
                fname = feature_names[idx] if idx < len(feature_names) else k
            else:
                fname = k
            rows.append((fname, float(v)))

        df = (
            pd.DataFrame(rows, columns=["feature", "importance"])
              .sort_values("importance", ascending=False)
              .head(top_n)
              .reset_index(drop=True)
        )
        df["method"] = f"xgboost_{importance_type}"
        return df[["feature", "importance", "method"]]

    
    if _is_sklearn_tree_model(model):
        importances = np.asarray(model.feature_importances_, dtype=float)
        if feature_names is None or len(feature_names) != len(importances):
            feature_names = [f"f{i}" for i in range(len(importances))]

        df = (
            pd.DataFrame({"feature": np.array(feature_names).astype(str), "importance": importances})
              .sort_values("importance", ascending=False)
              .head(top_n)
              .reset_index(drop=True)
        )
        df["method"] = "sklearn_feature_importances"
        return df[["feature", "importance", "method"]]

    raise ValueError(
        "Modelo não suportado pela explicabilidade unificada. "
        "Suportado: XGBoost sklearn (get_booster) ou árvore sklearn (feature_importances_)."
    )

# ----------------------------------------------------------------------------#
# Micro Serviço Básico
# ----------------------------------------------------------------------------#

def criar_app_fastapi(modelo_path: str, explain_global_path: str | None = None):
    from fastapi import FastAPI, HTTPException, Body
    import pandas as pd
    import json
    from pathlib import Path

    app = FastAPI(title="FlightOnTime - API", version="0.1")

    pipeline = carregar_pickle(modelo_path)

    # carrega explicabilidade global (1 vez)
    explain_global = None
    if explain_global_path is not None:
        p = Path(explain_global_path)
        if p.exists():
            explain_global = json.loads(p.read_text(encoding="utf-8"))

    REQUIRED_RAW_COLS = [
        "partida_prevista",
        "empresa_aerea",
        "aerodromo_origem",
        "aerodromo_destino",
        "codigo_tipo_linha",
    ]

    @app.get("/health")
    def health():
        return {"status": "ok"}

    @app.get("/explain/global")
    def get_explain_global():
        if explain_global is None:
            raise HTTPException(status_code=404, detail="Explicabilidade global não carregada.")
        return {"explain_global": explain_global}

    @app.post("/predict")
    def predict(payload: dict = Body(...)):
        if "dados" not in payload:
            raise HTTPException(status_code=400, detail="Payload deve conter a chave 'dados'.")

        x = pd.DataFrame([payload["dados"]])

        faltando = [c for c in REQUIRED_RAW_COLS if c not in x.columns]
        if faltando:
            raise HTTPException(status_code=400, detail=f"Faltando colunas obrigatórias: {faltando}")

        pred = int(pipeline.predict(x)[0])
        proba = float(pipeline.predict_proba(x)[0, 1]) if hasattr(pipeline, "predict_proba") else None

        # explicabilidade local (5 fatores)
        topk = 5 
        explain_local = explicar_local_xgb(pipeline, x, top_k=topk)  

        resp = {
            "prediction": pred,
            "label": "atrasado" if pred == 1 else "no_prazo",
            "proba_atraso": proba,
            "explain_local":explicar_local_xgb(pipeline, x, top_k=topk)
        }

        # incluir global junto se você quiser
        if explain_global is not None:
            resp["explain_global"] = explain_global

        return resp

    return app
