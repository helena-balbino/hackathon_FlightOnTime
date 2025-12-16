# 📋 Divisão de Tarefas - Time Backend

## 🎯 Estratégia Walking Skeleton - Semana 1

### ✅ Tech Lead (Você)
- [x] Setup inicial do repositório
- [x] Arquitetura base do projeto
- [x] Configuração do pom.xml
- [x] Criação da estrutura de pacotes
- [ ] Code reviews
- [ ] Ponte com time de Data Science (alinhamento do contrato JSON)
- [ ] Documentação de integração

---

## 👥 Dupla "Gateway & Validação"

**Responsáveis**: [Nome 1] e [Nome 2]

**Foco**: Porta de entrada da aplicação - garantir que dados chegam limpos e validados

### Tarefas Principais:
- [x] ✅ Criar DTOs (Request/Response/Error)
  - `FlightPredictionRequest.java`
  - `FlightPredictionResponse.java`
  - `ErrorResponse.java`

- [x] ✅ Criar FlightController
  - Endpoint POST `/api/predict`
  - Endpoint GET `/api/health`
  - Anotações de validação

- [x] ✅ Configurar Swagger/OpenAPI
  - `OpenApiConfig.java`
  - Documentação dos endpoints
  - Schemas dos DTOs

### Próximos Passos (Semana 2):
- [ ] Adicionar validações customizadas (ex: validar códigos IATA)
- [ ] Implementar CORS se necessário para frontend
- [ ] Criar validador de formato de datas
- [ ] Adicionar mais exemplos no Swagger

### Arquivos de Responsabilidade:
```
src/main/java/com/flightontime/api/
├── dto/
│   ├── FlightPredictionRequest.java    ✅
│   ├── FlightPredictionResponse.java   ✅
│   └── ErrorResponse.java              ✅
├── controller/
│   └── FlightController.java           ✅
└── config/
    └── OpenApiConfig.java              ✅
```

---

## 👥 Dupla "Business Logic & Mock"

**Responsáveis**: [Nome 3] e [Nome 4]

**Foco**: Motor da aplicação - lógica de negócio e tratamento de erros

### Tarefas Principais:
- [x] ✅ Criar FlightPredictionService
  - Lógica mockada com heurísticas
  - Preparar estrutura para integração futura
  - Logs adequados

- [x] ✅ Criar GlobalExceptionHandler
  - Tratamento de MethodArgumentNotValidException
  - Tratamento de IllegalArgumentException
  - Tratamento de Exception genérica

- [x] ✅ Criar testes unitários
  - `FlightPredictionServiceTest.java`
  - Testes de validação

- [x] ✅ Criar Collection do Postman
  - 3+ exemplos de requisições
  - Testes automatizados
  - Exemplos de erros

### Próximos Passos (Semana 2):
- [ ] Implementar WebClient/RestTemplate para chamar microserviço Python
- [ ] Adicionar configuração de timeout e retry
- [ ] Criar mais testes de integração
- [ ] Implementar cache (opcional)

### Arquivos de Responsabilidade:
```
src/main/java/com/flightontime/api/
├── service/
│   └── FlightPredictionService.java    ✅
└── exception/
    └── GlobalExceptionHandler.java     ✅

src/test/java/com/flightontime/api/
└── service/
    └── FlightPredictionServiceTest.java ✅

postman/
└── FlightOnTime_API.postman_collection.json ✅
```

---

## 🔄 Fluxo de Trabalho Semanal

### Semana 1 (ATUAL) ✅
- Setup completo
- Endpoint funcional com mock
- Validações implementadas
- Documentação Swagger
- Testes básicos

### Semana 2 (PRÓXIMA)
**Tech Lead:**
- Receber URL do microserviço Python do time DS
- Implementar WebClient

**Dupla Gateway & Validação:**
- Validações customizadas
- Refinamento do Swagger

**Dupla Business Logic & Mock:**
- Substituir mock por integração real
- Testes de integração

### Semana 3
**Todos:**
- Tratamento de falhas (circuit breaker)
- Testes de resiliência
- Tratamento de timeout

### Semana 4
**Todos:**
- Testes unitários completos
- Cobertura de código
- Code review final

### Semana 5
**Tech Lead + 1:**
- Dockerização
- Deploy Oracle Cloud

**Outros:**
- Documentação final
- Exemplos de uso

### Semana 6
- Code freeze
- Apenas correções críticas
- Preparação da demo

---

## 📝 Checklist de Entregáveis - Semana 1

### Repositório
- [x] Estrutura Maven configurada
- [x] `.gitignore` configurado
- [x] README.md completo
- [x] Código fonte organizado

### API
- [x] Endpoint POST `/api/predict` funcional
- [x] Endpoint GET `/api/health` funcional
- [x] Validação de entrada implementada
- [x] Resposta padronizada em JSON
- [x] Tratamento de erros global

### Documentação
- [x] Swagger disponível em `/swagger-ui.html`
- [x] README com instruções de execução
- [x] Exemplos de request/response

### Testes
- [x] Collection do Postman com 3+ exemplos
- [x] Testes unitários básicos
- [x] Teste de contexto Spring

### Qualidade
- [x] Código comentado
- [x] Logs implementados
- [x] Convenções de nomenclatura seguidas
- [x] Arquitetura em camadas (Controller → Service)

---

## 🚀 Como Começar

### Para a Dupla "Gateway & Validação":
1. Revisar os DTOs criados
2. Testar validações no Postman
3. Melhorar documentação Swagger
4. Adicionar novos casos de validação

### Para a Dupla "Business Logic & Mock":
1. Entender a lógica mockada atual
2. Executar testes unitários
3. Importar Collection no Postman e testar
4. Pensar em melhorias para o mock

---

## 📞 Comunicação

- **Daily Stand-up**: Compartilhar progresso e bloqueios
- **Code Review**: Pull requests devem ser revisados pelo Tech Lead
- **Dúvidas**: Criar issues no GitHub ou comunicar no grupo

---

## 🎯 Meta da Semana 1

> **"API funcional com dados mockados que permite desenvolvimento independente do frontend e preparação para integração com Data Science"**

✅ **Status: CONCLUÍDO**

---

## 📌 Próxima Reunião

**Agenda**:
- Revisão do código da Semana 1
- Demonstração do endpoint funcionando
- Alinhamento com time de DS sobre integração
- Planejamento da Semana 2
