# 🐳 Guia de Containerização - FlightOnTime

## 📋 Visão Geral

Este guia explica como construir, executar e gerenciar os containers Docker para a aplicação FlightOnTime.

---

## 🏗️ Arquitetura

```
┌─────────────────────────────────────────┐
│         Docker Compose Network          │
│                                         │
│  ┌──────────────┐    ┌──────────────┐  │
│  │ Java Backend │◄───┤ Python API   │  │
│  │ Spring Boot  │    │ FastAPI + ML │  │
│  │  Port 8080   │    │  Port 5000   │  │
│  └──────────────┘    └──────────────┘  │
│         ▲                    ▲          │
└─────────┼────────────────────┼──────────┘
          │                    │
     HTTP Requests         HTTP Requests
          │                    │
    ┌─────┴────────────────────┴─────┐
    │         Postman/Browser        │
    └────────────────────────────────┘
```

---

## 🚀 Quick Start

### Pré-requisitos

- Docker Desktop 20.10+
- Docker Compose 2.0+
- 4GB RAM disponível
- 5GB espaço em disco

### Iniciar Aplicação Completa

```bash
# Clonar repositório
git clone <url-do-repositorio>
cd flight-ontime-api

# Iniciar todos os serviços
docker-compose up -d

# Verificar status
docker-compose ps

# Ver logs
docker-compose logs -f
```

**Aguarde ~2 minutos para inicialização completa.**

### Acessar Serviços

- **Backend Java**: http://localhost:8080
- **Swagger Java**: http://localhost:8080/swagger-ui.html
- **API Python**: http://localhost:5000
- **Docs Python**: http://localhost:5000/docs

---

## 🔨 Build Manual

### Backend Java

```bash
# Build da imagem
docker build -t flightontime-backend:latest .

# Executar container
docker run -d \
  --name flightontime-backend \
  -p 8080:8080 \
  -e PREDICTION_SERVICE_URL=http://python-api:5000 \
  -e PREDICTION_SERVICE_USE_MOCK=false \
  flightontime-backend:latest

# Ver logs
docker logs -f flightontime-backend
```

### API Python

```bash
# Build da imagem
docker build \
  -f data_science/semana_02/scripts/Dockerfile \
  -t flightontime-python:latest \
  .

# Executar container
docker run -d \
  --name flightontime-python \
  -p 5000:5000 \
  flightontime-python:latest

# Ver logs
docker logs -f flightontime-python
```

---

## 🧪 Testes

### Health Checks

```bash
# Backend Java
curl http://localhost:8080/api/health

# API Python
curl http://localhost:5000/health
```

### Teste de Predição

```bash
curl -X POST http://localhost:8080/api/predict \
  -H "Content-Type: application/json" \
  -d '{
    "companhia": "AZ",
    "origem": "GIG",
    "destino": "GRU",
    "data_partida": "2025-12-25T14:30:00",
    "distancia_km": 350
  }'
```

**Resposta esperada:**
```json
{
  "previsao": "Atrasado",
  "probabilidade": 0.78
}
```

---

## 🔧 Comandos Úteis

### Docker Compose

```bash
# Iniciar serviços
docker-compose up -d

# Parar serviços
docker-compose stop

# Parar e remover containers
docker-compose down

# Rebuild forçado
docker-compose build --no-cache
docker-compose up -d

# Ver logs de serviço específico
docker-compose logs -f java-backend
docker-compose logs -f python-api

# Reiniciar serviço específico
docker-compose restart java-backend

# Ver uso de recursos
docker-compose stats
```

### Docker

```bash
# Listar containers
docker ps
docker ps -a  # incluindo parados

# Ver logs
docker logs flightontime-backend
docker logs -f flightontime-python  # follow mode

# Entrar no container
docker exec -it flightontime-backend sh
docker exec -it flightontime-python bash

# Ver uso de recursos
docker stats

# Limpar recursos não utilizados
docker system prune -a
docker volume prune
```

---

## 🐛 Troubleshooting

### Backend Java não inicia

**Sintoma**: Container reiniciando continuamente

```bash
# Ver logs
docker-compose logs java-backend

# Verificar saúde
docker inspect flightontime-backend | grep -A 10 Health
```

**Soluções comuns:**
- Aumentar memória: `JAVA_OPTS=-Xmx1024m` no docker-compose.yml
- Verificar se Python API está rodando
- Verificar porta 8080 disponível

### Python API não responde

**Sintoma**: Timeout nas requisições

```bash
# Verificar se está rodando
docker-compose ps python-api

# Ver logs
docker-compose logs python-api

# Testar health interno
docker exec flightontime-python curl http://localhost:5000/health
```

**Soluções comuns:**
- Verificar se modelo .pkl existe
- Verificar dependências instaladas
- Verificar porta 5000 disponível

### Erro "Cannot connect to Python service"

**Sintoma**: Backend não consegue chamar Python

```bash
# Verificar network
docker network ls
docker network inspect flightontime-network

# Testar conectividade
docker exec flightontime-backend curl http://python-api:5000/health
```

**Soluções:**
- Verificar se ambos containers estão na mesma rede
- Verificar `PREDICTION_SERVICE_URL` no backend
- Reiniciar docker-compose

### Modelo não carregado (Python)

**Sintoma**: API retorna 500 ou usa mock

```bash
# Verificar se arquivo existe
docker exec flightontime-python ls -la /app/*.pkl

# Ver logs de inicialização
docker-compose logs python-api | grep -i "modelo\|error"
```

**Solução:**
- Colocar arquivo `flightontime_pipeline.pkl` em `data_science/semana_02/scripts/`
- Rebuild: `docker-compose build python-api`

---

## 📦 Variáveis de Ambiente

### Backend Java

| Variável | Padrão | Descrição |
|----------|--------|-----------|
| `SERVER_PORT` | 8080 | Porta do servidor |
| `PREDICTION_SERVICE_URL` | http://python-api:5000 | URL da API Python |
| `PREDICTION_SERVICE_USE_MOCK` | false | Usar mock ou serviço real |
| `JAVA_OPTS` | -Xmx512m | Opções da JVM |
| `SPRING_PROFILES_ACTIVE` | docker | Profile do Spring |

### API Python

| Variável | Padrão | Descrição |
|----------|--------|-----------|
| `PORT` | 5000 | Porta do servidor |
| `PYTHONUNBUFFERED` | 1 | Logs em tempo real |

---

## 🔒 Segurança

### Boas Práticas Implementadas

✅ **Usuários não-root** em ambos containers  
✅ **Multi-stage build** para reduzir tamanho  
✅ **Health checks** configurados  
✅ **Minimal base images** (Alpine/Slim)  
✅ **No secrets em código**  

### Recomendações para Produção

- [ ] Usar secrets do Docker para senhas
- [ ] Implementar rate limiting
- [ ] Adicionar autenticação JWT
- [ ] Configurar HTTPS/TLS
- [ ] Usar Docker registry privado
- [ ] Scan de vulnerabilidades (Trivy, Snyk)

---

## 📊 Performance

### Recursos Recomendados

| Serviço | CPU | RAM | Disco |
|---------|-----|-----|-------|
| Java Backend | 1-2 cores | 512MB-1GB | 200MB |
| Python API | 1 core | 512MB | 500MB |
| **Total** | **2-3 cores** | **1-1.5GB** | **700MB** |

### Otimizações

```yaml
# docker-compose.yml
services:
  java-backend:
    deploy:
      resources:
        limits:
          cpus: '2'
          memory: 1G
        reservations:
          cpus: '0.5'
          memory: 512M
```

---

## 🚀 Deploy em Produção

### Oracle Cloud

```bash
# Build para múltiplas arquiteturas
docker buildx build --platform linux/amd64,linux/arm64 \
  -t <registry>/flightontime-backend:latest \
  --push .

# Deploy no Kubernetes
kubectl apply -f k8s/deployment.yaml
```

### Docker Swarm

```bash
# Inicializar swarm
docker swarm init

# Deploy stack
docker stack deploy -c docker-compose.yml flightontime

# Verificar serviços
docker service ls
```

---

## 📝 Checklist de Deploy

- [ ] Modelos ML atualizados
- [ ] Variáveis de ambiente configuradas
- [ ] Health checks funcionando
- [ ] Logs centralizados
- [ ] Backups configurados
- [ ] Monitoramento ativo
- [ ] SSL/TLS configurado
- [ ] Firewall configurado
- [ ] DNS apontando corretamente
- [ ] Testes de carga realizados

---

## 📚 Referências

- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [Docker Compose Reference](https://docs.docker.com/compose/compose-file/)
- [Spring Boot Docker](https://spring.io/guides/topicals/spring-boot-docker/)
- [FastAPI Docker](https://fastapi.tiangolo.com/deployment/docker/)

---

## 🆘 Suporte

**Issues**: Abrir issue no GitHub  
**Slack**: #flightontime-devops  
**Docs**: `/docs` na aplicação

---

**Última atualização**: Janeiro 2026  
**Versão**: 1.0.0
