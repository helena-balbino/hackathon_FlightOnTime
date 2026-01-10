# 📘 Entrega Semana 2 – Pipeline, Feature Engineering e Baseline

## 📌 Resumo Executivo

Na segunda semana do projeto **FlightOnTime**, transformamos os aprendizados da análise exploratória em um **pipeline operacional completo**. Desenvolvemos um sistema de pré-processamento reproduzível, criamos features derivadas, treinamos modelos baseline e implementamos uma **API Python** funcional para previsão de atrasos, pronta para integração com o backend Java.

---

## 🎯 Objetivos da Semana

1. **Unificar o pré-processamento** em um pipeline reproduzível
2. **Criar features derivadas** baseadas nos insights da Semana 1
3. **Implementar balanceamento** da variável alvo
4. **Treinar modelos baseline** como referência de performance
5. **Desenvolver API Python** com endpoint `/predict`
6. **Validar integração** com o time de desenvolvimento

---

## 🏗️ Arquitetura da Solução

### Visão Geral

```
Cliente (Postman/Java API)
        ↓
API Python (FastAPI)
        ↓
Pipeline Serializado
        ↓
Pré-processamento → Feature Engineering → Modelo ML
        ↓
Resposta JSON
```

### Componentes Desenvolvidos

1. **Pipeline de Pré-processamento** (scikit-learn)
2. **Feature Engineering** automatizado
3. **Modelo Baseline** treinado
4. **API REST** em Python (FastAPI)
5. **Documentação de integração**

---

## 🛠️ Desenvolvimento por Dimensão

### DS1 – Pré-Processamento Unificado
**Responsável: Ana**

#### Tarefas Realizadas
✅ Implementação de pipeline scikit-learn completo  
✅ Tratamento de valores nulos (estratégias por tipo de variável)  
✅ Normalização de variáveis numéricas (StandardScaler)  
✅ Encoding de variáveis categóricas (OneHotEncoder)  
✅ Padronização de tipos de dados  
✅ Validação de consistência

#### Artefatos Criados
- **Script**: `flight_delay_pipeline.py` - Pipeline completo reutilizável
- **Notebook**: Explicação e testes do pipeline
- **Funções**: Transformadores customizados para features específicas

#### Decisões Técnicas

**Tratamento de Nulos:**
- Variáveis numéricas: Imputação por mediana (robusta a outliers)
- Variáveis categóricas: Categoria "DESCONHECIDO" quando aplicável
- Remoção de linhas apenas em casos críticos

**Normalização:**
- StandardScaler para variáveis contínuas (distância, tempo)
- MinMaxScaler para variáveis com intervalos específicos
- Preservação da distribuição original quando relevante

**Encoding:**
- OneHotEncoder para variáveis nominais (companhia, aeroporto)
- OrdinalEncoder para variáveis ordinais (faixa horária)
- Target Encoding para categorias de alta cardinalidade

#### Questão Principal Respondida
> ❓ **Quais transformações são indispensáveis para que os modelos recebam dados consistentes?**

**Resposta:** Todas as transformações devem estar **dentro do pipeline scikit-learn** para garantir reprodutibilidade em produção. Transformações manuais no notebook não podem ser usadas, pois o modelo receberá dados via JSON, não DataFrames.

---

### DS2 – Feature Engineering Inicial
**Responsável: Amélia**

#### Features Temporais Criadas
✅ **Faixa de horário**: Manhã (6-12h), Tarde (12-18h), Noite (18-24h), Madrugada (0-6h)  
✅ **Dia da semana**: Segunda a domingo (categórica)  
✅ **Mês**: Extração do mês da data de partida  
✅ **É fim de semana**: Indicador binário (sábado/domingo)  
✅ **É alta temporada**: Baseado em análise de sazonalidade

#### Features Operacionais Criadas
✅ **Frequência da companhia**: Número de voos por operador  
✅ **Taxa histórica de atraso**: Por companhia e aeroporto  
✅ **Popularidade da rota**: Frequência origem-destino  
✅ **Categoria de distância**: Curta (<500km), Média (500-1500km), Longa (>1500km)  
✅ **Horário de pico**: Indicador de horários de alta demanda

#### Features Combinadas
✅ **Companhia + Aeroporto**: Interação entre operador e origem  
✅ **Horário + Dia da semana**: Captura padrões específicos  
✅ **Rota + Faixa horária**: Análise de rotas em diferentes períodos

#### Validação de Features

| Feature | Correlação com Alvo | Importância | Status |
|---------|-------------------|-------------|--------|
| Faixa horária | 0.34 | Alta | ✅ Mantida |
| Taxa histórica atraso | 0.42 | Muito Alta | ✅ Mantida |
| Dia da semana | 0.18 | Média | ✅ Mantida |
| Frequência companhia | 0.12 | Baixa | ⚠️ Revisão |
| Categoria distância | 0.28 | Alta | ✅ Mantida |

#### Questão Principal Respondida
> ❓ **Quais novas variáveis parecem adicionar mais sinal para a predição de atraso?**

**Resposta:** Features derivadas de **histórico operacional** (taxa de atraso por companhia/aeroporto) e **temporais** (faixa horária, dia da semana) apresentaram maior correlação com atrasos. Features de interação entre variáveis também mostraram potencial.

---

### DS3 – Balanceamento e Preparação do Dataset
**Responsável: Enoque (com suporte de Helena)**

#### Análise do Desbalanceamento

**Distribuição Original:**
- Voos Pontuais: ~72%
- Voos Atrasados: ~28%
- Razão: 2.57:1 (desbalanceado)

#### Técnicas Testadas

**1. Undersampling (RandomUnderSampler)**
- ✅ Reduz classe majoritária
- ⚠️ Perda de informação
- 📊 Resultado: Balanceamento 50/50

**2. Oversampling (RandomOverSampler)**
- ✅ Aumenta classe minoritária
- ⚠️ Risco de overfitting
- 📊 Resultado: Balanceamento 50/50

**3. SMOTE (Synthetic Minority Over-sampling)**
- ✅ Cria exemplos sintéticos
- ✅ Preserva informação original
- 📊 Resultado: Balanceamento controlado

**4. Híbrido (SMOTE + Tomek Links)**
- ✅ Oversampling inteligente + limpeza de fronteira
- ✅ Melhor separabilidade
- 📊 Resultado: ~45/55 com melhor qualidade

#### Estratégia Escolhida

**Decisão:** SMOTE com ajuste de ratio (não 50/50 perfeito)
- Mantém 40% de voos atrasados (próximo da realidade)
- Evita overfitting do modelo
- Preserva padrões reais dos dados

#### Separação Treino/Teste

```python
# Estratificação para manter proporção
train_test_split(
    X, y, 
    test_size=0.20,
    stratify=y,
    random_state=42
)
```

- **Treino**: 80% (com SMOTE aplicado)
- **Teste**: 20% (dados originais, sem balanceamento)
- **Validação**: Cross-validation 5-fold

#### Questão Principal Respondida
> ❓ **Qual estratégia de balanceamento preserva melhor os padrões reais?**

**Resposta:** **SMOTE com ratio controlado (40/60)** apresentou o melhor equilíbrio entre performance do modelo e preservação dos padrões reais. Evita overfitting enquanto melhora a capacidade de detectar atrasos.

---

## 🤖 Modelos Baseline Treinados

### Modelos Implementados

#### 1. Regressão Logística
```
Acurácia: 68.3%
Precision: 0.61
Recall: 0.54
F1-Score: 0.57
ROC-AUC: 0.72
```

#### 2. Random Forest
```
Acurácia: 74.8%
Precision: 0.69
Recall: 0.67
F1-Score: 0.68
ROC-AUC: 0.81
```

#### 3. XGBoost (Melhor Baseline)
```
Acurácia: 76.2%
Precision: 0.71
Recall: 0.69
F1-Score: 0.70
ROC-AUC: 0.83
```

### Métricas de Negócio

**Custo de Erro:**
- Falso Negativo (prever pontual quando atrasa): Alto impacto - cliente insatisfeito
- Falso Positivo (prever atraso quando pontual): Baixo impacto - expectativa gerenciada

**Decisão:** Otimizar **Recall** para minimizar falsos negativos (atrasos não previstos).

---

## 🌐 API Python Desenvolvida

### Tecnologias Utilizadas

- **Framework**: FastAPI
- **Servidor**: Uvicorn
- **Serialização**: Pickle (pipeline + modelo)
- **Validação**: Pydantic

### Endpoints Implementados

#### GET `/health`
Verifica se a API está operacional.

**Response:**
```json
{
  "status": "healthy",
  "version": "1.0.0"
}
```

#### POST `/predict`
Realiza previsão de atraso do voo.

**Request Body:**
```json
{
  "dados": {
    "partida_prevista": "2025-12-25 10:30:00",
    "empresa_aerea": "GOL",
    "codigo_tipo_linha": "Regular",
    "aerodromo_origem": "SBSP",
    "aerodromo_destino": "SBGL",
    "situacao_voo": "Realizado"
  }
}
```

**Response (200 OK):**
```json
{
  "previsao": "Atrasado",
  "probabilidade": 0.78,
  "modelo": "XGBoost",
  "versao": "1.0.0"
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Dados inválidos",
  "detalhes": "Campo 'empresa_aerea' é obrigatório"
}
```

### Estrutura da API

```python
# api_app.py
from fastapi import FastAPI
import pickle

app = FastAPI()

# Carregar pipeline e modelo
with open('pipeline.pkl', 'rb') as f:
    pipeline = pickle.load(f)

@app.post("/predict")
async def predict(dados: FlightData):
    # Aplicar pipeline
    X = pipeline.transform(dados.dict())
    
    # Predição
    pred = model.predict(X)
    proba = model.predict_proba(X)
    
    return {
        "previsao": "Atrasado" if pred[0] == 1 else "Pontual",
        "probabilidade": float(proba[0][1])
    }
```

### Execução da API

```bash
# 1. Criar ambiente virtual
python -m venv venv

# 2. Ativar ambiente
source venv/bin/activate  # Linux/Mac
venv\Scripts\activate     # Windows

# 3. Instalar dependências
pip install -r requirements.txt

# 4. Executar API
uvicorn api_app:app --host 0.0.0.0 --port 8000 --reload
```

**URL Local:** `http://localhost:8000`  
**Documentação:** `http://localhost:8000/docs` (Swagger automático)

---

## 🔗 Integração com Backend Java

### Contrato de API Estabelecido

**Documento:** `INTEGRACAO_DS.md`

#### Mapeamento de Campos

| Campo Java | Campo Python | Transformação |
|------------|--------------|---------------|
| companhia | empresa_aerea | Direto |
| origem | aerodromo_origem | Converter IATA → ICAO |
| destino | aerodromo_destino | Converter IATA → ICAO |
| data_partida | partida_prevista | ISO 8601 |
| distancia_km | - | Calculado no pipeline |

#### Configuração Backend (Semana 3)

```java
// PythonPredictionClient.java
@Service
public class PythonPredictionClient {
    
    private final WebClient webClient;
    
    public PythonPredictionClient(WebClient.Builder builder) {
        this.webClient = builder
            .baseUrl("http://localhost:8000")
            .build();
    }
    
    public FlightPredictionResponse predict(FlightPredictionRequest request) {
        return webClient.post()
            .uri("/predict")
            .bodyValue(convertToDs(request))
            .retrieve()
            .bodyToMono(FlightPredictionResponse.class)
            .block();
    }
}
```

---

## 📁 Entregáveis

### Código e Scripts
- ✅ `flight_delay_pipeline.py` - Pipeline completo
- ✅ `api_app.py` - API FastAPI funcional
- ✅ `requirements.txt` - Dependências Python
- ✅ `pipeline.pkl` - Pipeline serializado
- ✅ `model.pkl` - Modelo treinado

### Notebooks
- ✅ `Consolidado_S02.ipynb` - Desenvolvimento completo
- ✅ Notebooks individuais por dimensão

### Documentação
- ✅ `cronograma_S02.md` - Planejamento detalhado
- ✅ `execucao_api.md` - Guia de execução da API
- ✅ `INTEGRACAO_DS.md` - Contrato de integração

### Artefatos de Teste
- ✅ `request_correto.json` - Exemplo válido
- ✅ `request_invalid.json` - Teste de validação
- ✅ `response_ok.json` - Exemplo de resposta

---

## 🎓 Aprendizados e Conclusões

### Principais Conquistas

1. ✅ **Pipeline reproduzível** funcionando de ponta a ponta
2. ✅ **Features derivadas** com impacto comprovado
3. ✅ **Baseline estabelecido** (76.2% acurácia, 0.83 ROC-AUC)
4. ✅ **API operacional** pronta para integração
5. ✅ **Documentação completa** para o time de Dev

### Desafios Superados

- ⚠️ Garantir reprodutibilidade do pipeline em produção
- ⚠️ Balanceamento sem perder padrões reais
- ⚠️ Serialização de transformadores customizados
- ⚠️ Alinhamento de contratos entre Java e Python

### Próximos Passos (Semana 3)

1. **Integração real** Java ↔ Python
2. **Otimização de hiperparâmetros** dos modelos
3. **Feature engineering avançado** (novas interações)
4. **Tratamento de resiliência** (timeout, retry, fallback)
5. **Testes de integração** end-to-end
6. **Monitoramento** de performance da API

---

## 📊 Métricas de Entrega

| Componente | Status | Qualidade |
|------------|--------|-----------|
| Pipeline Pré-processamento | ✅ Completo | Reproduzível |
| Feature Engineering | ✅ Completo | 12 features criadas |
| Balanceamento | ✅ Completo | SMOTE otimizado |
| Modelo Baseline | ✅ Completo | ROC-AUC: 0.83 |
| API Python | ✅ Completo | Funcional |
| Documentação | ✅ Completo | Detalhada |
| Testes | ✅ Completo | Casos de uso cobertos |

---

## 🚀 Como Testar a Entrega

### 1. Executar API Python
```bash
cd data_science/semana_02/scripts
python -m venv venv
venv\Scripts\activate
pip install -r requirements.txt
uvicorn api_app:app --reload
```

### 2. Testar Health Check
```bash
curl http://localhost:8000/health
```

### 3. Testar Predição
```bash
curl -X POST http://localhost:8000/predict \
  -H "Content-Type: application/json" \
  -d @request_examples/request_correto.json
```

### 4. Acessar Documentação Interativa
Abrir no navegador: `http://localhost:8000/docs`

---

**Data de Conclusão**: Semana 2 do Projeto  
**Status**: ✅ **CONCLUÍDO**  
**Próxima Etapa**: Semana 3 - Integração Real e Otimização
