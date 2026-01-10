# 🐍 Python API - FlightOnTime

API FastAPI para previsão de atrasos em voos usando Machine Learning.

---

## 📋 Visão Geral

Esta API foi desenvolvida pelo time de Data Science para fornecer previsões de atrasos de voos através de modelos de Machine Learning treinados com dados históricos da ANAC.

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

### POST `/predict`

Realiza previsão de atraso do voo.

**Request:**
```json
{
  "companhia_icao": "GLO",
  "origem_icao": "SBGR",
  "destino_icao": "SBGL",
  "data_partida": "2025-12-25T10:30:00",
  "distancia_km": 350
}
```

**Response:**
```json
{
  "previsao": "Atrasado",
  "probabilidade": 0.78,
  "modelo_versao": "v1.0"
}
```

---

## 📁 Arquivos Principais

| Arquivo | Descrição |
|---------|-----------|
| `java_integration_api.py` | API FastAPI com contrato Java |
| `api_app.py` | API alternativa (formato original) |
| `flight_delay_pipeline.py` | Pipeline ML e transformadores |
| `flightontime_pipeline.pkl` | Modelo treinado serializado |
| `requirements.txt` | Dependências Python |
| `Dockerfile` | Imagem Docker da API |
| `test_api.py` | Testes da API |

---

## 🧪 Testes

### Teste Manual

```bash
# Health check
curl http://localhost:5000/health

# Predição
curl -X POST http://localhost:5000/predict \
  -H "Content-Type: application/json" \
  -d '{
    "companhia_icao": "GLO",
    "origem_icao": "SBGR",
    "destino_icao": "SBGL",
    "data_partida": "2025-12-25T10:30:00",
    "distancia_km": 350
  }'
```

### Testes Automatizados

```bash
# Executar suite de testes
python test_api.py

# Ou com pytest
pytest test_api.py -v
```

---

## 🔧 Configuração

### Variáveis de Ambiente

| Variável | Padrão | Descrição |
|----------|--------|-----------|
| `PORT` | 5000 | Porta do servidor |
| `PYTHONUNBUFFERED` | 1 | Logs em tempo real |

### Modo Mock

Se o modelo não puder ser carregado, a API entra automaticamente em **modo mock** e retorna previsões baseadas em heurísticas simples:

- Voos após 18h → Maior chance de atraso
- Voos com distância > 1000km → Maior chance de atraso
- Outros → Pontuais

---

## 🤖 Pipeline de ML

### Transformações Aplicadas

1. **Feature Engineering**
   - Extração de hora, dia da semana, mês
   - Classificação de período (manhã, tarde, noite)
   - Indicadores de fim de semana e alta temporada

2. **Médias Históricas**
   - Taxa de atraso por companhia
   - Taxa de atraso por aeroporto origem
   - Taxa de atraso por aeroporto destino

3. **Pré-processamento**
   - Imputação de valores nulos
   - Normalização (StandardScaler)
   - Encoding (OneHotEncoder)

4. **Modelo**
   - XGBoost otimizado
   - ROC-AUC: 0.87
   - Acurácia: 79.4%

### Estrutura do Pipeline

```python
Pipeline(
  steps=[
    ('fe', FeatureEngineeringTransformer),
    ('pre', ColumnTransformer),
    ('model', XGBClassifier)
  ]
)
```

---

## 📊 Performance

| Métrica | Valor |
|---------|-------|
| Tempo médio de resposta | ~180ms |
| P95 | <320ms |
| P99 | <450ms |
| Throughput | ~120 req/s |

---

## 🔗 Integração com Java

### Adaptador de Dados

A API possui um `DataAdapter` que converte automaticamente entre os formatos:

**Java → Modelo:**
```python
{
  "companhia_icao": "GLO",
  "origem_icao": "SBGR",
  ...
}
```

**Convertido para:**
```python
{
  "empresa_aerea": "GLO",
  "aerodromo_origem": "SBGR",
  "partida_prevista": "2025-12-25 10:30:00",
  "codigo_tipo_linha": "Regular",
  "situacao_voo": "Realizado"
}
```

---

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

## 👥 Time Data Science

- **Semana 1**: Análise exploratória (EDA)
- **Semana 2**: Pipeline + Feature Engineering + API
- **Semana 3**: Otimização e integração

---

## 📞 Contato

Para dúvidas sobre a API Python, entre em contato com o time de Data Science.

---

**Versão**: 2.0  
**Última atualização**: Janeiro 2026
