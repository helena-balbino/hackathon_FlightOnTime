# 🔗 Guia de Integração Java ↔ Python

## 📋 Visão Geral

Este guia explica como integrar o backend Java com o modelo de Machine Learning em Python.

---

## 🎯 Arquitetura

```
Postman/Frontend
      ↓
   Java API (8080)
      ↓
 Python API (5000)
      ↓
  Modelo ML (pickle)
```

---

## 🚀 Como Rodar

### 1️⃣ Preparar Ambiente Python

```bash
cd data_science/semana_02/scripts

# Instalar dependências
pip install fastapi uvicorn pydantic pandas scikit-learn numpy

# Se precisar do modelo completo:
pip install imbalanced-learn
```

### 2️⃣ Iniciar API Python

**Opção A: API de Integração (RECOMENDADO)**
```bash
python java_integration_api.py
```

**Opção B: API Original**
```bash
python api_app.py
```

A API estará disponível em: `http://localhost:5000`

### 3️⃣ Configurar Java

Edite `application.properties`:

```properties
# Modo Python (integração real)
prediction.service.use-mock=false
prediction.service.url=http://localhost:5000
```

### 4️⃣ Iniciar API Java

```bash
cd ../../../  # Voltar para raiz
mvn spring-boot:run
```

A API Java estará em: `http://localhost:8080`

---

## 📊 Contratos de API

### REQUEST Java → Python

**Java envia:**
```json
{
  "companhia_icao": "GLO",
  "origem_icao": "SBGR",
  "destino_icao": "SBGL",
  "data_partida": "2025-12-25T10:30:00",
  "distancia_km": 350
}
```

**Mapeamento Interno (Python):**
```json
{
  "dados": {
    "partida_prevista": "2025-12-25 10:30:00",
    "empresa_aerea": "GLO",
    "codigo_tipo_linha": "Regular",
    "aerodromo_origem": "SBGR",
    "aerodromo_destino": "SBGL",
    "situacao_voo": "Realizado"
  }
}
```

### RESPONSE Python → Java

**Python retorna:**
```json
{
  "previsao": "Atrasado",
  "probabilidade": 0.78,
  "modelo_versao": "v1.0"
}
```

**Conversão:**
- `prediction: 0` → `"previsao": "Pontual"`
- `prediction: 1` → `"previsao": "Atrasado"`
- `proba_atraso: 0.78` → `"probabilidade": 0.78`

---

## 🧪 Testar Integração

### 1. Testar Python Diretamente

```bash
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

### 2. Testar Java (que chama Python)

```bash
curl -X POST http://localhost:8080/api/flights/predict \
  -H "Content-Type: application/json" \
  -d '{
    "companhia": "G3",
    "origem": "GRU",
    "destino": "GIG",
    "data_partida": "2025-12-25T14:30:00",
    "distancia_km": 350
  }'
```

### 3. Health Checks

```bash
# Python
curl http://localhost:5000/health

# Java
curl http://localhost:8080/actuator/health
```

---

## ⚠️ Troubleshooting

### Erro: "Connection refused"

**Causa:** Python não está rodando
**Solução:**
```bash
cd data_science/semana_02/scripts
python java_integration_api.py
```

### Erro: "Modelo não disponível"

**Causa:** Arquivo `flightontime_pipeline.pkl` não encontrado
**Solução:**
1. Treinar o modelo rodando o notebook `Consolidado_S02.ipynb`
2. Salvar o pipeline com: `salvar_pickle(pipeline, "flightontime_pipeline.pkl")`
3. Copiar o arquivo para `data_science/semana_02/scripts/`

### Erro: "Formato de data inválido"

**Causa:** Data não está no formato ISO
**Solução:** Usar formato `YYYY-MM-DDTHH:MM:SS`
```json
"data_partida": "2025-12-25T14:30:00"  ✅
"data_partida": "25/12/2025 14:30"     ❌
```

### Java usa Mock ao invés de Python

**Causa:** Flag `use-mock=true`
**Solução:**
```properties
# application.properties
prediction.service.use-mock=false
```

---

## 📁 Estrutura de Arquivos

```
data_science/semana_02/scripts/
├── java_integration_api.py          ← API de integração (USAR ESTE)
├── api_app.py                        ← API original (legado)
├── flight_delay_pipeline.py          ← Funções do modelo
├── flightontime_pipeline.pkl         ← Modelo treinado
└── request_examples/
    ├── request_correto.json
    ├── request_invalid.json
    └── response_ok.json
```

---

## 🔄 Fluxo Completo

1. **Usuário** faz request no Postman → `localhost:8080/api/flights/predict`
2. **Java** valida dados e converte IATA → ICAO
3. **Java** envia para Python → `localhost:5000/predict`
4. **Python** adapta formato Java → Modelo
5. **Modelo ML** faz previsão
6. **Python** adapta resposta Modelo → Java
7. **Java** retorna ao usuário

---

## 📊 Logs Úteis

### Python (FastAPI)
```
INFO:     127.0.0.1:xxxxx - "POST /predict HTTP/1.1" 200 OK
✅ Previsão: Atrasado | Probabilidade: 0.78
```

### Java (Spring Boot)
```
🐍 MODO PYTHON ativado - Chamando microserviço
📤 Enviando requisição para Python: SBGR → SBGL
📥 Resposta do Python: Previsão=Atrasado, Probabilidade=0.78
```

---

## 🎓 Próximos Passos

1. ✅ Treinar modelo e gerar `.pkl`
2. ✅ Iniciar API Python
3. ✅ Configurar Java (`use-mock=false`)
4. ✅ Testar integração end-to-end
5. 🔜 Deploy em produção (Docker)

---

## 🆘 Suporte

- **Python Issues:** Verificar logs do FastAPI
- **Java Issues:** Verificar logs do Spring Boot
- **Integração:** Testar cada serviço separadamente primeiro
