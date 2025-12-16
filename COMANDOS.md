# 🚀 Guia Rápido de Comandos

## 📦 Setup Inicial

### 1. Clone o repositório
```bash
git clone <url-do-repositorio>
cd flight-ontime-api
```

### 2. Verifique se tem Java 17+
```bash
java -version
```

### 3. Verifique se tem Maven
```bash
mvn -version
```

---

## 🔨 Build e Execução

### Compilar o projeto
```bash
mvn clean install
```

### Executar a aplicação
```bash
mvn spring-boot:run
```

### Executar em background (Windows PowerShell)
```powershell
Start-Process mvn -ArgumentList "spring-boot:run" -NoNewWindow
```

---

## 🧪 Testes

### Executar todos os testes
```bash
mvn test
```

### Executar testes com cobertura
```bash
mvn test jacoco:report
```

### Ver relatório de cobertura
```bash
# Abre o arquivo: target/site/jacoco/index.html
```

---

## 🌐 Acessar a Aplicação

### API Base
```
http://localhost:8080
```

### Health Check
```
http://localhost:8080/api/health
```

### Swagger UI (Documentação Interativa)
```
http://localhost:8080/swagger-ui.html
```

### OpenAPI JSON
```
http://localhost:8080/api-docs
```

---

## 📨 Testar com cURL

### Health Check
```bash
curl http://localhost:8080/api/health
```

### Previsão - Voo Pontual
```bash
curl -X POST http://localhost:8080/api/predict \
  -H "Content-Type: application/json" \
  -d '{
    "companhia": "AZ",
    "origem": "GIG",
    "destino": "GRU",
    "data_partida": "2025-11-10T08:30:00",
    "distancia_km": 350
  }'
```

### Previsão - Voo Atrasado
```bash
curl -X POST http://localhost:8080/api/predict \
  -H "Content-Type: application/json" \
  -d '{
    "companhia": "LA",
    "origem": "GRU",
    "destino": "MAO",
    "data_partida": "2025-11-15T20:45:00",
    "distancia_km": 2850
  }'
```

### Teste de Validação (Erro Esperado)
```bash
curl -X POST http://localhost:8080/api/predict \
  -H "Content-Type: application/json" \
  -d '{
    "companhia": "",
    "origem": "GIG",
    "distancia_km": -100
  }'
```

---

## 📦 Gerar JAR

### Criar arquivo executável
```bash
mvn clean package
```

### Executar o JAR
```bash
java -jar target/flight-ontime-api-1.0.0.jar
```

---

## 🐳 Docker (Futuro - Semana 5)

### Build da imagem
```bash
docker build -t flight-ontime-api:1.0.0 .
```

### Executar container
```bash
docker run -p 8080:8080 flight-ontime-api:1.0.0
```

---

## 🔍 Análise de Código

### Verificar estilo de código (adicionar plugin antes)
```bash
mvn checkstyle:check
```

### Análise de dependências
```bash
mvn dependency:tree
```

### Atualizar dependências
```bash
mvn versions:display-dependency-updates
```

---

## 🧹 Limpeza

### Limpar target
```bash
mvn clean
```

### Limpar cache do Maven
```bash
mvn dependency:purge-local-repository
```

---

## 📊 Logs

### Ver logs em tempo real (Windows)
```powershell
Get-Content -Path "application.log" -Wait -Tail 50
```

---

## 🐛 Debug

### Executar em modo debug
```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

### Conectar debugger na porta 5005

---

## 📝 Git

### Inicializar repositório
```bash
git init
git add .
git commit -m "Initial commit - Walking Skeleton Week 1"
```

### Criar branch para desenvolvimento
```bash
git checkout -b feature/integracao-python
```

### Push para o GitHub
```bash
git remote add origin <url-do-repositorio>
git push -u origin main
```

---

## 🔧 Troubleshooting

### Porta 8080 já está em uso
```bash
# Descobrir qual processo está usando a porta (Windows)
netstat -ano | findstr :8080

# Matar o processo (substitua PID)
taskkill /PID <PID> /F

# OU mudar a porta no application.properties
server.port=8081
```

### Erro de compilação
```bash
# Limpar e recompilar
mvn clean install -U
```

### Problemas com cache do Maven
```bash
# Deletar pasta .m2/repository e redownload
mvn dependency:purge-local-repository
```

---

## 📱 Postman

### Importar collection
1. Abrir Postman
2. Import → File → Selecionar `postman/FlightOnTime_API.postman_collection.json`
3. Executar requests

### Executar todos os testes
1. Collections → FlightOnTime API
2. Run collection
3. Ver resultados

---

## ⚙️ Variáveis de Ambiente

### Definir porta customizada
```bash
# Linux/Mac
export SERVER_PORT=8081
mvn spring-boot:run

# Windows PowerShell
$env:SERVER_PORT=8081
mvn spring-boot:run
```

### Definir profile
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=dev
```

---

## 📚 Recursos Úteis

### Documentação Spring Boot
- https://docs.spring.io/spring-boot/docs/current/reference/html/

### Documentação Spring Web
- https://docs.spring.io/spring-framework/reference/web.html

### Documentação Swagger/OpenAPI
- https://springdoc.org/

### Tutorial Maven
- https://maven.apache.org/guides/getting-started/

---

## ✅ Checklist Rápido

Antes de fazer push:
- [ ] `mvn clean install` executa sem erros
- [ ] `mvn test` passa todos os testes
- [ ] `mvn spring-boot:run` inicia a aplicação
- [ ] Swagger está acessível em `/swagger-ui.html`
- [ ] Postman collection funciona
- [ ] README está atualizado
- [ ] Código está comentado
- [ ] Commits com mensagens claras

---

**Dúvidas?** Consulte o README.md ou entre em contato com o Tech Lead!
