# 🐍 Python API - FlightOnTime (FastAPI + ML + Explainability)

API FastAPI para previsão de atrasos em voos usando Machine Learning.

---

## 📋 Visão Geral

Esta API foi desenvolvida pelo time de Data Science para fornecer previsões de atrasos de voos através de modelos de Machine Learning treinados com dados históricos da ANAC.

✅ **Modelo:** Pipeline Scikit-learn + XGBoost  
✅ **Predição:** atraso vs no_prazo  
✅ **Explicabilidade Global:** via arquivo `explain_global.json`  
✅ **Explicabilidade Local:** Top features contribuidoras para a previsão (XGBoost contribs)

---

## 🚀 Como Executar

### Opção 1: Docker (Recomendado)

```bash
# Da raiz do projeto
docker-compose up python-api -d

# Ver logs
docker-compose logs -f python-api
```

### Opção 2: Local

```bash
# Criar ambiente virtual
python -m venv venv

# Ativar ambiente
source venv/bin/activate  # Linux/Mac
venv\Scripts\activate     # Windows

# Instalar dependências
pip install -r requirements.txt

# Executar API
uvicorn java_integration_api:app --host 0.0.0.0 --port 5000 --reload

# Ou usar o script
python java_integration_api.py
```

**Acessar:**
- API: http://localhost:5000
- Documentação interativa: http://localhost:5000/docs
- Health check: http://localhost:5000/health

---

## 📡 Endpoints

### GET `/health`

Verifica status da API e se o modelo está carregado.

**Response:**
```json
{
  "status": "UP",
  "message": "Java Integration API is running",
  "modelo_carregado": true,
  "version": "2.0"
}
```
### GET `/explain/global`

Retorna a explicabilidade global carregada do arquivo `explain_global.json`.

**Response:**
```json
{
  "explain_global": { }
}

⚠️ Se o arquivo não existir: retorna 404

```

### POST `/predict`

Realiza previsão de atraso do voo usando o contrato oficial do projeto.

**Request:**
```json
{
  "dados": {
    "partida_prevista": "2025-12-25 10:30:00",
    "empresa_aerea": "GLO",
    "aerodromo_origem": "SBSP",
    "aerodromo_destino": "SBGL",
    "codigo_tipo_linha": "N"
  },
  "topk": 8
}

📌 O campo `topk`, retorna a quantidade de features mais importantes na explicabilidade global, é opcional e está configurado como default = 8

```

**Response:**
```json
{
  "prediction": 1,
  "label": "atrasado",
  "proba_atraso": 0.72,
  "explain_local": {
    "top_features": [
      {
        "feature": "num_mes_ano",
        "contribution": 0.57,
        "direction": "increase",
        "value": 1.68
      }
    ]
  }
}

📌 Onde:
 - prediction → 1 = atrasado / 0 = no_prazo
 - label → versão textual
 - proba_atraso → probabilidade do atraso
 - explain_local → explicação local com top contribuições do modelo

```

---

## 📁 Arquivos Principais

| Arquivo | Descrição |
|---------|-----------|
| `api_app.py` | API oficial do projeto (contrato final) |
| `flight_delay_pipeline.py` | Pipeline ML e transformadores |
| `flightontime_pipeline.pkl` | Modelo treinado serializado (pipeline final) |
| `explain_global.json` | Explicabilidade global do modelo |
| `requirements.txt` | Dependências Python |
| `Dockerfile` | Imagem Docker da API |

---

## 🧪 Testes

### Teste Manual

```bash
# Health check
curl http://localhost:5000/health

# Predição
curl -X POST "http://localhost:5000/predict" \
  -H "Content-Type: application/json" \
  -d '{
    "dados": {
      "partida_prevista": "2025-12-25 10:30:00",
      "empresa_aerea": "GLO",
      "aerodromo_origem": "SBSP",
      "aerodromo_destino": "SBGL",
      "codigo_tipo_linha": "N"
    },
    "topk": 8
  }'

```
### Testes Automatizados

```bash
# Executar suite de testes
python api_app.py

# Ou com pytest
pytest api_app.py -v

```

---

## 🔧 Configuração

### Variáveis de Ambiente

| Variável | Padrão | Descrição |
|----------|--------|-----------|
| `PORT` | 5000 | Porta do servidor |
| `PYTHONUNBUFFERED` | 1 | Logs em tempo real |

## 🐛 Troubleshooting

### Modelo não carrega

**Problema**: API inicia em modo mock

**Solução:**
1. Verificar se `flightontime_pipeline.pkl` existe
2. Verificar se o arquivo não está corrompido
3. Verificar se todas as dependências estão instaladas

```bash
# Recriar ambiente
rm -rf venv
python -m venv venv
source venv/bin/activate
pip install -r requirements.txt
```

### Erro de importação

**Problema**: `ModuleNotFoundError: No module named 'flight_delay_pipeline'`

**Solução:**
```bash
# Adicionar diretório ao PYTHONPATH
export PYTHONPATH="${PYTHONPATH}:$(pwd)"

# Ou executar do diretório correto
cd data_science/semana_02/scripts
python java_integration_api.py
```

### Timeout nas requisições

**Problema**: Modelo muito lento

**Solução:**
1. Verificar recursos disponíveis
2. Reduzir complexidade do modelo
3. Aumentar timeout no backend Java

---

## 📚 Dependências

### Core
- **FastAPI** 0.104.1 - Framework web
- **Uvicorn** 0.24.0 - Servidor ASGI
- **Pydantic** 2.5.0 - Validação de dados

### Machine Learning
- **Pandas** 2.1.3 - Manipulação de dados
- **NumPy** 1.26.2 - Operações numéricas
- **Scikit-learn** 1.3.2 - Pipeline e transformadores
- **XGBoost** 2.0.2 - Modelo de ML
- **Imbalanced-learn** 0.11.0 - SMOTE

### Desenvolvimento
- **Matplotlib** 3.8.2 - Visualizações
- **Seaborn** 0.13.0 - Gráficos estatísticos

---

## 📖 Documentação Adicional

- **Pipeline ML**: `flight_delay_pipeline.py` - Código completo documentado
- **Testes**: `README_TESTES.md` - Guia de testes
- **Integração**: `INTEGRACAO_JAVA_PYTHON.md` - Contrato de API
- **Execução**: `execucao_api.md` - Guia de execução

---

## 📞 Contato

Para dúvidas sobre a API Python, entre em contato com o time de Data Science.

---

**Versão**: 2.0  
**Última atualização**: Janeiro 2026
