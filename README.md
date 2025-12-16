# ✈️ FlightOnTime API

API REST para previsão de atrasos em voos desenvolvida em **Java 17 + Spring Boot 3**.

---

## 📋 Sobre o Projeto

O **FlightOnTime** é uma solução preditiva que estima se um voo vai decolar no horário ou com atraso. A API recebe informações do voo (companhia, origem, destino, horário, distância) e retorna uma previsão com probabilidade associada.

Este projeto foi desenvolvido durante o hackathon seguindo a estratégia **Walking Skeleton**, permitindo desenvolvimento incremental e independente entre os times de Backend e Data Science.

---

## 🎯 Estratégia de Desenvolvimento (Walking Skeleton)

### Semana 1 - ATUAL ✅
- ✅ Estrutura base do projeto configurada
- ✅ Endpoint `/api/predict` funcional com dados **MOCKADOS**
- ✅ Validação de entradas implementada
- ✅ Documentação Swagger/OpenAPI disponível
- ✅ Tratamento de erros padronizado

### Semana 2 - PRÓXIMA
- 🔄 Integração com microserviço Python (Data Science)
- 🔄 Substituir mock por chamadas reais via WebClient/RestTemplate

### Semanas 3-6
- 🔜 Tratamento de falhas e resiliência
- 🔜 Testes unitários e de integração
- 🔜 Dockerização
- 🔜 Deploy na Oracle Cloud

---

## 🚀 Como Executar

### Pré-requisitos

- **Java 17** ou superior
- **Maven 3.8+**
- **IDE** ( VS Code com extensões Java)

### Passo a Passo

1. **Clone o repositório**
```bash
git clone <url-do-repositorio>
cd flight-ontime-api
```

2. **Compile o projeto**
```bash
mvn clean install
```

3. **Execute a aplicação**
```bash
mvn spring-boot:run
```

4. **Acesse a documentação Swagger**
```
http://localhost:8080/swagger-ui.html
```

5. **Teste o endpoint de health check**
```bash
curl http://localhost:8080/api/health
```

---

## 📡 Endpoints Disponíveis

### 🎯 POST `/api/predict`

Realiza a previsão de atraso do voo.

**Request Body:**
```json
{
  "companhia": "AZ",
  "origem": "GIG",
  "destino": "GRU",
  "data_partida": "2025-11-10T14:30:00",
  "distancia_km": 350
}
```

**Response (200 OK):**
```json
{
  "previsao": "Atrasado",
  "probabilidade": 0.78
}
```

**Response (400 Bad Request) - Validação:**
```json
{
  "timestamp": "2025-12-16T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Dados de entrada inválidos",
  "path": "/api/predict",
  "errors": [
    "companhia: Companhia aérea é obrigatória",
    "distancia_km: Distância deve ser um valor positivo"
  ]
}
```

### 🏥 GET `/api/health`

Verifica se a API está rodando.

**Response:**
```
FlightOnTime API is running! ✈️
```

---

## 🧪 Testando com Postman

Importe a collection do Postman localizada em:
```
postman/FlightOnTime_API.postman_collection.json
```

A collection contém 3 exemplos prontos para teste:
1. ✈️ Voo Pontual (manhã, curta distância)
2. ⏰ Voo Atrasado (noite, longa distância)
3. ❌ Requisição Inválida (teste de validação)

---

## 🏗️ Arquitetura do Projeto

```
src/
├── main/
│   ├── java/com/flightontime/api/
│   │   ├── FlightOnTimeApplication.java      # Classe principal
│   │   ├── controller/
│   │   │   └── FlightController.java         # Endpoints REST
│   │   ├── service/
│   │   │   └── FlightPredictionService.java  # Lógica de negócio
│   │   ├── dto/
│   │   │   ├── FlightPredictionRequest.java  # DTO de entrada
│   │   │   ├── FlightPredictionResponse.java # DTO de saída
│   │   │   └── ErrorResponse.java            # DTO de erro
│   │   ├── exception/
│   │   │   └── GlobalExceptionHandler.java   # Tratamento global de erros
│   │   └── config/
│   │       └── OpenApiConfig.java            # Configuração Swagger
│   └── resources/
│       └── application.properties            # Configurações da aplicação
└── test/
    └── java/com/flightontime/api/            # Testes (a implementar)
```

---

## 👥 Equipe Backend

### Tech Lead
- **Responsabilidades**: Arquitetura, integração com DS, code reviews, deploy

### Dupla "Gateway & Validação"
- **Foco**: DTOs, Controller, Validações, Swagger
- **Arquivos**: `FlightController.java`, DTOs, `OpenApiConfig.java`

### Dupla "Business Logic & Mock"
- **Foco**: Service, Lógica de negócio, Tratamento de erros, Testes
- **Arquivos**: `FlightPredictionService.java`, `GlobalExceptionHandler.java`

---

## 🔗 Integração com Data Science

### Contrato JSON (Alinhado com time DS)

O time de Data Science está desenvolvendo um microserviço Python (FastAPI/Flask) que expõe o endpoint `/predict`.

**Fluxo atual (Semana 1):**
```
Cliente → Backend Java → Mock (dados simulados) → Cliente
```

**Fluxo futuro (Semana 2+):**
```
Cliente → Backend Java → Microserviço Python → Modelo ML → Backend Java → Cliente
```

---

## 📦 Dependências Principais

- **Spring Boot 3.2.0** - Framework base
- **Spring Web** - REST APIs
- **Spring Validation** - Validação de dados
- **SpringDoc OpenAPI** - Documentação Swagger
- **Lombok** - Redução de boilerplate
- **Spring DevTools** - Hot reload

---

## 🛠️ Próximos Passos

- [ ] Implementar WebClient para chamada ao microserviço Python
- [ ] Adicionar configuração de timeout e retry
- [ ] Implementar circuit breaker (Resilience4j)
- [ ] Criar testes unitários (JUnit 5 + Mockito)
- [ ] Adicionar Docker e docker-compose
- [ ] Configurar CI/CD (GitHub Actions)
- [ ] Deploy na Oracle Cloud

---

## 📝 Notas Importantes

### ⚠️ Dados Mockados (Semana 1)

A lógica atual usa **heurísticas simples** para simular previsões:
- Voos de manhã têm menor probabilidade de atraso
- Voos à noite têm maior probabilidade de atraso
- Fins de semana são mais pontuais
- Voos curtos (<500km) são mais pontuais

**Isso será substituído pela integração real com o modelo de ML na Semana 2.**

---

## 📞 Contato

Para dúvidas ou sugestões, entre em contato com o **Tech Lead** do time Backend.

---

## 📄 Licença

Este projeto foi desenvolvido para fins educacionais durante o hackathon FlightOnTime.
