# 🚀 Guia Rápido - Comandos Docker

## ⚡ Comandos Essenciais

### Iniciar Aplicação
```bash
docker-compose up -d
```

### Ver Logs
```bash
# Todos os serviços
docker-compose logs -f

# Apenas Java
docker-compose logs -f java-backend

# Apenas Python
docker-compose logs -f python-api
```

### Parar Aplicação
```bash
docker-compose stop
```

### Parar e Remover
```bash
docker-compose down
```

### Rebuild
```bash
docker-compose build --no-cache
docker-compose up -d
```

---

## 🔍 Status e Monitoramento

### Ver Status
```bash
docker-compose ps
```

### Ver Recursos
```bash
docker stats
```

### Health Checks
```bash
curl http://localhost:8080/api/health
curl http://localhost:5000/health
```

---

## 🧪 Testes

### Teste Backend
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

### Teste Python Direto
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

---

## 🐛 Debug

### Entrar no Container
```bash
# Java
docker exec -it flightontime-backend sh

# Python
docker exec -it flightontime-python bash
```

### Ver Logs Específicos
```bash
# Últimas 100 linhas
docker-compose logs --tail=100 java-backend

# Desde horário específico
docker-compose logs --since 2026-01-10T10:00:00
```

---

## 🧹 Limpeza

### Remover Containers Parados
```bash
docker container prune
```

### Remover Imagens Não Usadas
```bash
docker image prune -a
```

### Limpeza Completa
```bash
docker system prune -a --volumes
```

---

## 📍 URLs Importantes

- Backend: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html
- Python API: http://localhost:5000
- Python Docs: http://localhost:5000/docs

---

## 🔧 Comandos PowerShell (Windows)

### Usar Script Interativo
```powershell
.\docker-deploy.ps1
```

### Comandos Manuais
```powershell
# Iniciar
docker-compose up -d

# Ver logs
docker-compose logs -f

# Parar
docker-compose down

# Status
docker-compose ps
```

---

## 💡 Dicas

1. **Primeira execução**: Aguarde ~2 minutos para download e build
2. **Logs**: Use `-f` para acompanhar em tempo real
3. **Rebuild**: Use `--no-cache` quando mudar dependências
4. **Resources**: Aumente memória do Docker se necessário (Settings → Resources)

---

## ⚠️ Problemas Comuns

### Porta já em uso
```bash
# Ver o que está usando a porta
netstat -ano | findstr :8080  # Windows
lsof -i :8080                 # Linux/Mac

# Mudar porta no docker-compose.yml
ports:
  - "8081:8080"  # Usar 8081 externamente
```

### Espaço em disco
```bash
# Verificar uso
docker system df

# Limpar
docker system prune -a
```

### Container não inicia
```bash
# Ver erro específico
docker-compose logs java-backend

# Forçar rebuild
docker-compose build --no-cache java-backend
docker-compose up -d
```

---

## 📖 Mais Informações

- **Guia completo**: [DOCKER_GUIDE.md](DOCKER_GUIDE.md)
- **README geral**: [README.md](README.md)
- **Python API**: [data_science/semana_02/scripts/README.md](data_science/semana_02/scripts/README.md)
