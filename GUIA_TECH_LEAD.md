# 🎯 GUIA DO TECH LEAD - Walking Skeleton Semana 1

## ✅ O QUE FOI CRIADO

Parabéns! A estrutura completa do projeto backend está pronta seguindo a estratégia Walking Skeleton.

---

## 📦 ESTRUTURA CRIADA

### 1. **Configuração Base** ✅
- ✅ Projeto Maven configurado (pom.xml)
- ✅ Estrutura de pacotes organizada
- ✅ .gitignore configurado
- ✅ Dependencies: Spring Web, Validation, Swagger, Lombok

### 2. **Código Fonte** ✅
- ✅ FlightOnTimeApplication.java (classe principal)
- ✅ FlightController.java (endpoints REST)
- ✅ FlightPredictionService.java (lógica mockada)
- ✅ DTOs (Request, Response, Error)
- ✅ GlobalExceptionHandler.java (tratamento de erros)
- ✅ OpenApiConfig.java (Swagger)

### 3. **Testes** ✅
- ✅ FlightPredictionServiceTest.java (testes unitários)
- ✅ FlightOnTimeApplicationTests.java (teste de contexto)
- ✅ Collection do Postman com 6 requisições de teste

### 4. **Documentação** ✅
- ✅ README.md completo
- ✅ TAREFAS.md (divisão de trabalho)
- ✅ INTEGRACAO_DS.md (contrato com Data Science)
- ✅ COMANDOS.md (comandos úteis)
- ✅ INSTALACAO.md (guia de setup)
- ✅ ESTRUTURA.txt (organização do projeto)

---

## 🎯 PRÓXIMOS PASSOS (VOCÊ, TECH LEAD)

### 1. Instalar Ferramentas (Se não tiver)
```powershell
# Veja o arquivo INSTALACAO.md para instruções completas

# Verificar se tem Java 17+
java -version

# Verificar se tem Maven
mvn -version

# Se não tiver, siga as instruções em INSTALACAO.md
```

### 2. Compilar e Testar o Projeto
```bash
cd flight-ontime-api

# Compilar
mvn clean install

# Executar testes
mvn test

# Executar aplicação
mvn spring-boot:run
```

### 3. Verificar se Funciona
1. Acesse: http://localhost:8080/api/health
2. Acesse: http://localhost:8080/swagger-ui.html
3. Importe collection do Postman e teste

### 4. Subir para o GitHub
```bash
# Inicializar Git (se não foi feito)
git init

# Adicionar arquivos
git add .

# Primeiro commit
git commit -m "feat: Walking Skeleton - Semana 1

- Estrutura base do projeto Spring Boot
- Endpoint POST /api/predict com mock
- Validação de entradas
- Documentação Swagger
- Testes unitários básicos
- Collection do Postman

Equipe: Tech Lead + 4 desenvolvedores
Estratégia: Walking Skeleton Week 1"

# Criar repositório no GitHub e conectar
git remote add origin <URL_DO_SEU_REPOSITORIO>
git branch -M main
git push -u origin main
```

---

## 👥 COMO DELEGAR TAREFAS

### **Dupla "Gateway & Validação"** - [Nome 1] e [Nome 2]

**Reunião inicial** (15-20 min):
```
"Pessoal, vocês são a porta de entrada da nossa API. 
Vocês garantem que os dados chegam limpos e validados.

Já criei a estrutura base. Agora vocês vão:

1. REVISAR os DTOs criados:
   - FlightPredictionRequest.java
   - FlightPredictionResponse.java
   - ErrorResponse.java
   
2. REVISAR o FlightController.java:
   - Entender as validações
   - Testar no Postman
   - Melhorar documentação Swagger se necessário

3. ADICIONAR validações customizadas (Semana 2):
   - Validar códigos IATA (companhia: 2 chars, aeroporto: 3 chars)
   - Validar que data_partida é futura
   - Criar validadores customizados

📂 ARQUIVOS DE VOCÊS:
   - src/main/java/com/flightontime/api/dto/
   - src/main/java/com/flightontime/api/controller/
   - src/main/java/com/flightontime/api/config/

📝 TAREFAS IMEDIATAS:
   - [ ] Revisar e entender os DTOs
   - [ ] Testar endpoint no Postman (importar collection)
   - [ ] Verificar validações funcionando
   - [ ] Melhorar documentação Swagger
   - [ ] Criar issues no GitHub para melhorias

🎯 ENTREGÁVEL SEMANA 1:
   - Validações funcionando 100%
   - Swagger documentado e bonito
   - Testes no Postman passando
"
```

**Material para eles:**
- [TAREFAS.md](TAREFAS.md) - Seção "Dupla Gateway & Validação"
- Collection do Postman em `postman/`
- Acesso ao Swagger: http://localhost:8080/swagger-ui.html

---

### **Dupla "Business Logic & Mock"** - [Nome 3] e [Nome 4]

**Reunião inicial** (15-20 min):
```
"Vocês são o motor da aplicação. 
Vocês cuidam da lógica de negócio e tratamento de erros.

Já criei a estrutura base. Agora vocês vão:

1. REVISAR o FlightPredictionService.java:
   - Entender a lógica mockada
   - Ver as heurísticas usadas
   - Pensar em melhorias

2. REVISAR o GlobalExceptionHandler.java:
   - Entender tratamento de erros
   - Testar cenários de erro no Postman

3. IMPLEMENTAR testes unitários:
   - Executar FlightPredictionServiceTest.java
   - Adicionar mais casos de teste
   - Garantir cobertura > 80%

4. PREPARAR para integração com Python (Semana 2):
   - Estudar WebClient do Spring
   - Preparar estrutura para substituir mock

📂 ARQUIVOS DE VOCÊS:
   - src/main/java/com/flightontime/api/service/
   - src/main/java/com/flightontime/api/exception/
   - src/test/java/com/flightontime/api/

📝 TAREFAS IMEDIATAS:
   - [ ] Revisar e entender o Service
   - [ ] Executar testes unitários: mvn test
   - [ ] Adicionar mais testes
   - [ ] Testar erros no Postman
   - [ ] Estudar INTEGRACAO_DS.md

🎯 ENTREGÁVEL SEMANA 1:
   - Testes unitários com > 80% cobertura
   - Tratamento de erros robusto
   - Entendimento da integração futura
"
```

**Material para eles:**
- [TAREFAS.md](TAREFAS.md) - Seção "Dupla Business Logic & Mock"
- [INTEGRACAO_DS.md](INTEGRACAO_DS.md)
- Collection do Postman (casos de erro)

---

## 📅 CRONOGRAMA SEMANAL SUGERIDO

### **Segunda-feira**
- 🎯 Reunião de alinhamento (30 min)
- Explicar Walking Skeleton
- Delegar tarefas
- Tirar dúvidas sobre a estrutura

### **Terça a Quinta**
- 💻 Desenvolvimento das duplas
- Daily stand-up (15 min/dia)
- Tech Lead disponível para dúvidas

### **Sexta-feira**
- 📊 Demo da semana
- Code review
- Retrospectiva
- Planejamento Semana 2

---

## 🎬 SCRIPT DA PRIMEIRA REUNIÃO

### Abertura (5 min)
```
"Pessoal, bem-vindos ao FlightOnTime!

Nosso objetivo: criar uma API que prevê atrasos em voos.

Vamos usar a estratégia 'Walking Skeleton':
- Semana 1: API funcional com MOCK (é aqui que estamos!)
- Semana 2: Integração com Data Science
- Semanas 3-6: Robustez, testes, deploy

Por que mock primeiro?
- Não dependemos do time de DS
- Frontend pode começar a testar
- Aprendemos a estrutura antes de complicar
"
```

### Divisão de Times (10 min)
```
"Temos 2 duplas:

DUPLA 1 - Gateway & Validação:
Vocês são a porta de entrada. Garantem dados limpos.
Foco em: DTOs, Controller, Swagger

DUPLA 2 - Business Logic & Mock:
Vocês são o motor. Lógica e tratamento de erros.
Foco em: Service, Exceptions, Testes

Eu (Tech Lead):
- Arquitetura
- Integração com DS
- Code reviews
- Ajuda quando travarem
"
```

### Estrutura do Projeto (10 min)
```
"Vou compartilhar tela e mostrar:
- Estrutura de pastas
- Arquivos principais
- Como executar
- Como testar
- Swagger

Depois vocês vão explorar por conta própria."
```

### Primeiras Tarefas (5 min)
```
"Hoje/Amanhã:

TODOS:
1. Instalar Java 17 + Maven (ver INSTALACAO.md)
2. Clonar/baixar o projeto
3. Executar: mvn clean install
4. Executar: mvn spring-boot:run
5. Acessar: http://localhost:8080/swagger-ui.html
6. Importar Postman collection
7. LER o README.md e TAREFAS.md

DUPLA 1:
- Revisar DTOs e Controller
- Testar no Postman

DUPLA 2:
- Revisar Service e Exception Handler
- Executar testes: mvn test

Qualquer problema, me chamem!"
```

---

## 📞 COMUNICAÇÃO COM DATA SCIENCE

### Reunião com Tech Lead de DS
```
Assunto: Alinhamento de Contrato API

Pauta:
1. Apresentar nosso JSON (ver INTEGRACAO_DS.md)
2. Confirmar que eles concordam com o formato
3. Alinhar prazos:
   - Quando terão o microserviço Python pronto?
   - Qual será a URL/IP?
4. Combinar testes conjuntos na Semana 2

Levar:
- Arquivo INTEGRACAO_DS.md
- Exemplos de Request/Response
- Collection do Postman (para eles testarem)
```

---

## ✅ CHECKLIST SEMANA 1 (TECH LEAD)

### Setup
- [ ] Ferramentas instaladas (Java, Maven, IDE)
- [ ] Projeto compilando sem erros
- [ ] Aplicação executando
- [ ] Swagger acessível

### Delegação
- [ ] Duplas definidas
- [ ] Reunião de kickoff realizada
- [ ] Tarefas claras para cada dupla
- [ ] Material distribuído (README, TAREFAS.md, etc.)

### Comunicação
- [ ] Canal de comunicação definido (WhatsApp, Slack, Discord)
- [ ] Daily stand-up agendado
- [ ] Reunião com Tech Lead DS agendada

### Repositório
- [ ] GitHub criado
- [ ] Código commitado
- [ ] README atualizado
- [ ] Acesso dado aos membros do time

### Validação
- [ ] Endpoint /predict funcionando
- [ ] Validações testadas
- [ ] Swagger documentado
- [ ] Postman collection testada

---

## 🚨 POSSÍVEIS PROBLEMAS E SOLUÇÕES

### "Maven não funciona"
→ Ver [INSTALACAO.md](INSTALACAO.md)
→ Verificar PATH
→ Reiniciar terminal

### "Porta 8080 em uso"
→ Ver [COMANDOS.md](COMANDOS.md) - seção Troubleshooting
→ Matar processo ou mudar porta

### "Dependências não baixam"
→ Verificar internet
→ Tentar: mvn clean install -U

### "Time está perdido"
→ Fazer pair programming
→ Revisar TAREFAS.md juntos
→ Mostrar na prática como funciona

---

## 📊 MÉTRICAS DE SUCESSO - SEMANA 1

✅ **Todos os membros conseguem:**
- Executar o projeto localmente
- Fazer requisições no Postman
- Ver o Swagger funcionando

✅ **Dupla 1:**
- Validações funcionando 100%
- Swagger bem documentado

✅ **Dupla 2:**
- Testes unitários passando
- Cobertura > 70%

✅ **Time completo:**
- Todos entendem o fluxo da aplicação
- Todos sabem onde estão seus arquivos
- Code review feito pelo Tech Lead

---

## 🎯 MENSAGEM FINAL PARA O TIME

```
"Pessoal, essa é a base do nosso projeto.

Nas próximas 6 semanas, vamos transformar isso em uma
aplicação completa, integrada com IA, com testes robustos
e deployada na nuvem.

Semana 1 é sobre FUNDAÇÃO.
Entendam bem essa estrutura agora, porque vamos construir
em cima dela.

Qualquer dúvida, estou aqui.
Vamos fazer acontecer! 🚀"
```

---

## 📁 ARQUIVOS IMPORTANTES PARA REVISAR

1. **README.md** - Visão geral do projeto
2. **TAREFAS.md** - Divisão de trabalho detalhada
3. **INTEGRACAO_DS.md** - Contrato com Data Science
4. **COMANDOS.md** - Comandos úteis
5. **INSTALACAO.md** - Setup de ferramentas

---

**Boa sorte, Tech Lead! Você tem tudo para liderar esse time com sucesso! 💪**

Se precisar de ajuda, revisite esses documentos ou peça apoio ao time.

**Let's build something amazing! ✈️🚀**
