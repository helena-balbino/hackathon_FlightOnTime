# 🔗 Guia de Integração com Data Science

## 📋 Contrato de API (JSON)

### Alinhamento com Time DS

Este documento define o contrato da API entre Backend (Java) e Data Science (Python).

---

## 🎯 Endpoint do Microserviço Python

**URL (será fornecida pelo time DS):**
```
POST http://<ip-ou-dominio>:<porta>/predict
```

**Exemplo:**
```
POST http://localhost:5000/predict
```

---

## 📨 Request (Backend → Python)

### Headers
```
Content-Type: application/json
```

### Body
```json
{
  "companhia": "AZ",
  "origem": "GIG",
  "destino": "GRU",
  "data_partida": "2025-11-10T14:30:00",
  "distancia_km": 350
}
```

### Validações (Backend garante antes de enviar)
- ✅ `companhia`: String não vazia (código IATA 2 caracteres)
- ✅ `origem`: String não vazia (código IATA 3 caracteres)
- ✅ `destino`: String não vazia (código IATA 3 caracteres)
- ✅ `data_partida`: ISO 8601 DateTime (formato: yyyy-MM-dd'T'HH:mm:ss)
- ✅ `distancia_km`: Integer positivo

---

## 📤 Response (Python → Backend)

### Sucesso (200 OK)
```json
{
  "previsao": "Atrasado",
  "probabilidade": 0.78
}
```

### Campos
- `previsao`: String - "Pontual" ou "Atrasado"
- `probabilidade`: Double - Valor entre 0.0 e 1.0 (probabilidade da previsão)

### Erro (400 Bad Request)
```json
{
  "error": "Dados inválidos",
  "message": "Campo 'companhia' é obrigatório"
}
```

### Erro (500 Internal Server Error)
```json
{
  "error": "Erro no modelo",
  "message": "Falha ao carregar o modelo preditivo"
}
```

---

## 🔄 Fluxo de Integração

### Semana 1 (Atual) - Desenvolvimento Independente
```
Cliente → Backend Java → MOCK → Backend Java → Cliente
```

### Semana 2+ - Integração Real
```
Cliente → Backend Java → HTTP Request → Python FastAPI → Modelo ML → Response → Backend Java → Cliente
```

---

## 🛠️ Implementação Backend (Semana 2)

### Adicionar dependência no pom.xml
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

### Criar PredictionClient.java
```java
@Service
public class PredictionClient {
    
    private final WebClient webClient;
    
    public PredictionClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
            .baseUrl("http://localhost:5000") // URL do microserviço Python
            .build();
    }
    
    public FlightPredictionResponse predict(FlightPredictionRequest request) {
        return webClient.post()
            .uri("/predict")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(FlightPredictionResponse.class)
            .block();
    }
}
```

### Atualizar FlightPredictionService.java
```java
@Service
public class FlightPredictionService {
    
    private final PredictionClient predictionClient;
    private final boolean useMock = false; // Toggle para testar
    
    public FlightPredictionResponse predict(FlightPredictionRequest request) {
        if (useMock) {
            return predictMock(request); // Lógica atual
        } else {
            return predictionClient.predict(request); // Chamada real
        }
    }
}
```

---

## 🧪 Cenários de Teste

### Teste 1: Voo Pontual
```json
{
  "companhia": "AZ",
  "origem": "GIG",
  "destino": "GRU",
  "data_partida": "2025-11-10T08:30:00",
  "distancia_km": 350
}
```
**Esperado**: `previsao: "Pontual"`, `probabilidade: < 0.5`

### Teste 2: Voo Atrasado
```json
{
  "companhia": "LA",
  "origem": "GRU",
  "destino": "MAO",
  "data_partida": "2025-11-15T20:45:00",
  "distancia_km": 2850
}
```
**Esperado**: `previsao: "Atrasado"`, `probabilidade: > 0.5`

### Teste 3: Erro de Validação
```json
{
  "companhia": "",
  "origem": "GIG",
  "destino": "GRU",
  "data_partida": "2025-11-10T14:30:00",
  "distancia_km": -100
}
```
**Esperado**: Status 400 com lista de erros

---

## 📝 Checklist de Integração

### Time Backend (Java)
- [x] Contrato JSON definido
- [x] DTOs criados e validados
- [x] Mock funcionando
- [ ] WebClient configurado
- [ ] Tratamento de timeout
- [ ] Tratamento de erro do serviço Python
- [ ] Testes de integração

### Time Data Science (Python)
- [ ] FastAPI/Flask configurado
- [ ] Endpoint `/predict` criado
- [ ] Modelo `.pkl` carregado corretamente
- [ ] Validação de entrada implementada
- [ ] Resposta no formato JSON acordado
- [ ] Tratamento de erros
- [ ] Deploy com IP/URL acessível

---

## 🚨 Pontos de Atenção

### Timeout
- Backend deve ter timeout de **5 segundos** máximo
- Se Python não responder, retornar erro 503 (Service Unavailable)

### Retry
- Implementar 2 tentativas em caso de falha
- Backoff de 1 segundo entre tentativas

### Logging
- Backend deve logar:
  - Request enviado ao Python
  - Response recebido
  - Tempo de resposta
  - Erros ocorridos

### Fallback
- Se Python estiver indisponível, backend pode:
  - Retornar erro 503
  - OU usar o mock como fallback (decisão do time)

---

## 🔍 Monitoramento

### Métricas a acompanhar
- Taxa de sucesso das chamadas ao Python
- Tempo médio de resposta
- Taxa de timeout
- Taxa de erros 4xx/5xx

---

## 📞 Responsáveis

**Backend**: Tech Lead + Dupla "Business Logic & Mock"

**Data Science**: Líder DS + Time Python

**Ponto de Contato**: Tech Leads de ambos os times

---

## 🗓️ Timeline

- **Semana 1**: Desenvolvimento independente (MOCK)
- **Semana 2**: Integração + Testes
- **Semana 3**: Resiliência + Tratamento de falhas
- **Semana 4+**: Refinamentos

---

## ✅ Critérios de Aceitação

A integração está completa quando:
- ✅ Backend consegue chamar Python com sucesso
- ✅ Response está no formato correto
- ✅ Erros são tratados adequadamente
- ✅ Timeout está configurado
- ✅ Logs estão funcionando
- ✅ Testes passam com sucesso
