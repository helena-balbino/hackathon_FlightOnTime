# 🧪 Guia de Testes - Integração Java ↔ Python

## ✅ Status Atual

A API de integração está **funcionando em MODO MOCK** porque o modelo treinado tem dependências do módulo `script_v3` que não foram incluídas no pickle.

### O que está funcionando:
- ✅ API FastAPI rodando na porta 5000
- ✅ Contrato Java compatível (ICAO, timestamps)
- ✅ Adaptador de dados (Java format ↔ Model format)
- ✅ Previsões MOCK baseadas em heurísticas simples
- ✅ Documentação interativa (Swagger)

### O que precisa ajuste:
- ⚠️ Modelo treinado não carrega (falta dependência `script_v3`)
- 🔧 Solução: Retreinar modelo OU usar código MOCK

---

## 🚀 Como Testar

### Opção 1: Script de Teste Automatizado

```bash
# Terminal 1: Inicie a API
cd c:\Users\alves\OneDrive\Documentos\Projetos\Hackaton\flight-ontime-api\data_science\semana_02\scripts
python java_integration_api.py

# Terminal 2: Execute os testes
python test_api.py
```

### Opção 2: Testes Manuais com cURL

```bash
# 1. Health Check
curl http://localhost:5000/health

# 2. Previsão de voo
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

### Opção 3: PowerShell (Windows)

```powershell
# 1. Health Check
Invoke-WebRequest -Uri "http://localhost:5000/health" -UseBasicParsing

# 2. Previsão
$body = @{
    companhia_icao = "GLO"
    origem_icao = "SBGR"
    destino_icao = "SBGL"
    data_partida = "2025-12-25T10:30:00"
    distancia_km = 350
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:5000/predict" -Method POST -Body $body -ContentType "application/json"
```

### Opção 4: Swagger UI (Recomendado para testes visuais)

1. Inicie a API: `python java_integration_api.py`
2. Abra no navegador: http://localhost:5000/docs
3. Use a interface interativa para testar

---

## 📊 Exemplos de Request/Response

### Request (Contrato Java)
```json
{
  "companhia_icao": "GLO",
  "origem_icao": "SBGR",
  "destino_icao": "SBGL",
  "data_partida": "2025-12-25T10:30:00",
  "distancia_km": 350
}
```

### Response (Modo MOCK)
```json
{
  "previsao": "Pontual",
  "probabilidade": 0.65,
  "modelo_versao": "mock-v1.0"
}
```

### Lógica MOCK Atual:
- **Atrasado** (72% confiança): Se horário ≥ 18h OU distância > 1000km
- **Pontual** (65% confiança): Caso contrário

---

## 🔧 Integração com Java

### 1. Configure a API Java

Edite `src/main/resources/application.properties`:

```properties
# Modo de operação
prediction.service.use-mock=false

# URL do microserviço Python
python.service.url=http://localhost:5000
python.service.timeout=10000
```

### 2. Inicie ambos os serviços

```bash
# Terminal 1: API Python
cd data_science/semana_02/scripts
python java_integration_api.py

# Terminal 2: API Java
cd flight-ontime-api
mvn spring-boot:run
```

### 3. Teste via Postman

**Request para Java (porta 8080):**
```
POST http://localhost:8080/api/v1/flights/predict
Content-Type: application/json

{
  "companhia_icao": "GLO",
  "origem_icao": "SBGR",
  "destino_icao": "SBGL",
  "data_partida": "2025-12-25T10:30:00",
  "distancia_km": 350
}
```

**Fluxo completo:**
```
Postman → Java API (8080) → Python API (5000) → Java API → Postman
```

---

## 🐛 Troubleshooting

### API não inicia
```bash
# Verifique se a porta está livre
netstat -ano | findstr :5000

# Mate processo se necessário
taskkill /PID <PID> /F
```

### Erro "No module named 'script_v3'"
✅ **NORMAL** - A API continua funcionando em modo MOCK

Para resolver definitivamente:
1. Retreine o modelo sem dependências externas
2. OU use apenas modo MOCK (já está implementado)

### Java não conecta
```bash
# Verifique se ambos serviços estão UP
curl http://localhost:5000/health  # Python
curl http://localhost:8080/actuator/health  # Java
```

---

## 📝 Próximos Passos

### Para usar modelo real:
1. Retreinar pipeline sem dependências `script_v3`
2. Salvar novo pickle: `flightontime_pipeline_v2.pkl`
3. Atualizar `MODEL_PATH` em `java_integration_api.py`

### Para melhorar MOCK:
- Adicionar mais heurísticas (dia da semana, feriados, etc.)
- Usar dados históricos estáticos
- Implementar modelo leve (RandomForest simples)

---

## 📚 Documentação Completa

- **Arquitetura**: [INTEGRACAO_JAVA_PYTHON.md](../../../INTEGRACAO_JAVA_PYTHON.md)
- **API Java**: [INSTALACAO.md](../../../INSTALACAO.md)
- **Postman Collection**: [postman/FlightOnTime_API.postman_collection.json](../../../postman/)
