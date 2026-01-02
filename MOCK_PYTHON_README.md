# 🐍 Mock Server Python - Plano B

Este é um servidor Python simples que imita a API real do time de Data Science.
Use este mock enquanto o serviço real não estiver pronto.

## 🚀 Como Usar

### 1. Instalar Flask (se não tiver)
```bash
pip install flask
```

### 2. Rodar o servidor
```bash
python mock_python_service.py
```

Servidor vai subir em: `http://localhost:5000`

### 3. Configurar Java para usar o mock
No `application.properties`:
```properties
prediction.service.use-mock=false
prediction.service.url=http://localhost:5000
```

## 📝 Endpoints Disponíveis

### POST /predict
Previsão de atraso de voo

**Request**:
```json
{
  "companhia_icao": "GLO",
  "origem_icao": "SBGR",
  "destino_icao": "SBGL",
  "data_partida": "2025-11-10T14:30:00",
  "distancia_km": 350
}
```

**Response**:
```json
{
  "previsao": "Atrasado",
  "probabilidade": 0.75,
  "modelo_versao": "mock-v1.0"
}
```

### GET /health
Health check do serviço

**Response**:
```json
{
  "status": "UP",
  "message": "Mock Python Service is running"
}
```

## 🧪 Testando Diretamente

```bash
# Via curl
curl -X POST http://localhost:5000/predict \
  -H "Content-Type: application/json" \
  -d '{
    "companhia_icao": "GLO",
    "origem_icao": "SBGR",
    "destino_icao": "SBGL",
    "data_partida": "2025-11-10T14:30:00",
    "distancia_km": 350
  }'

# Health check
curl http://localhost:5000/health
```

## 📊 Lógica do Mock

O mock retorna:
- **"Atrasado"** se distância > 1000km OU hora >= 18h
- **"Pontual"** caso contrário
- Probabilidade aleatória entre 0.6 e 0.9

**Objetivo**: Simular comportamento realista para testes!
