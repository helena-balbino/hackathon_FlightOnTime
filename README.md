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

### Semanas 3-6 ✅
- ✅ Tratamento de falhas e resiliência
- ✅ Testes unitários e de integração
- ✅ **Dockerização completa**
- 🔜 Deploy na Oracle Cloud

---

## 🚀 Como Executar

### Opção 1: Docker (Recomendado) 🐳

**Pré-requisitos:**
- Docker Desktop 20.10+
- Docker Compose 2.0+

**Execução rápida:**
```bash
# Iniciar toda a aplicação (Backend + Python API)
docker-compose up -d

# Ver logs
docker-compose logs -f

# Parar
docker-compose down
```

**Script interativo (Windows):**
```powershell
.\docker-deploy.ps1
```

**Script interativo (Linux/Mac):**
```bash
chmod +x docker-deploy.sh
./docker-deploy.sh
```

**Acessar:**
- Backend: http://localhost:8080
- Python API: http://localhost:5000
- Swagger: http://localhost:8080/swagger-ui.html

📖 **Documentação completa**: [DOCKER_GUIDE.md](DOCKER_GUIDE.md)

---

### Opção 2: Execução Local (Desenvolvimento)

**Pré-requisitos:**
- **Java 17** ou superior
- **Maven 3.8+**
- **Python 3.11+** (para API Python)
- **IDE** (VS Code com extensões Java)

**Backend Java:**
```bash
# Clone o repositório
git clone <url-do-repositorio>
cd flight-ontime-api

# Compile o projeto
mvn clean install

# Execute a aplicação
mvn spring-boot:run

# Acesse Swagger
# http://localhost:8080/swagger-ui.html
```

**API Python (opcional):**
```bash
cd data_science/semana_02/scripts

# Criar ambiente virtual
python -m venv venv
source venv/bin/activate  # Linux/Mac
venv\Scripts\activate     # Windows

# Instalar dependências
pip install -r requirements.txt

# Executar API
uvicorn java_integration_api:app --host 0.0.0.0 --port 5000
```

**Health Checks:**
```bash
# Backend Java
curl http://localhost:8080/api/health

# Python API
curl http://localhost:5000/health
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

- [x] Implementar WebClient para chamada ao microserviço Python
- [x] Adicionar configuração de timeout e retry
- [x] Implementar circuit breaker (Resilience4j)
- [x] Criar testes unitários (JUnit 5 + Mockito)
- [x] **Adicionar Docker e docker-compose**
- [ ] Configurar CI/CD (GitHub Actions)
- [ ] Deploy na Oracle Cloud

---

## 🐳 Docker

A aplicação está completamente containerizada com:

### Arquivos Docker
- **`Dockerfile`** - Backend Java (multi-stage build)
- **`data_science/semana_02/scripts/Dockerfile`** - API Python
- **`docker-compose.yml`** - Orquestração completa
- **`DOCKER_GUIDE.md`** - Documentação detalhada

### Características
✅ Multi-stage build (otimização de tamanho)  
✅ Usuários não-root (segurança)  
✅ Health checks configurados  
✅ Network isolada para comunicação  
✅ Scripts de automação (PowerShell e Bash)  
✅ Hot reload para desenvolvimento  

### Quick Start
```bash
docker-compose up -d
```

**Mais detalhes**: [DOCKER_GUIDE.md](DOCKER_GUIDE.md)

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
