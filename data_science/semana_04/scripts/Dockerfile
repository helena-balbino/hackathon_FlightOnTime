# ============================================================================
# Dockerfile - API Python (FastAPI + Machine Learning)
# ============================================================================
# Imagem otimizada para FastAPI com modelo de ML
# ============================================================================

FROM python:3.11-slim

# Metadados
LABEL maintainer="FlightOnTime Data Science Team"
LABEL description="API Python FastAPI - Previsão de Atrasos com ML"
LABEL version="1.0.0"

# Variáveis de build
ARG DEBIAN_FRONTEND=noninteractive

# Instalar dependências do sistema
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    build-essential \
    && rm -rf /var/lib/apt/lists/*

# Criar usuário não-root
RUN useradd -m -u 1001 -s /bin/bash appuser

# Diretório de trabalho
WORKDIR /app

# Copiar requirements primeiro (cache de dependências)
COPY data_science/semana_04/scripts/requirements.txt requirements.txt

# Instalar dependências Python
RUN pip install --no-cache-dir --upgrade pip && \
    pip install --no-cache-dir -r requirements.txt

# Copiar código da aplicação
COPY data_science/semana_04/scripts/*.py ./
COPY data_science/semana_04/scripts/*.pkl ./

# Criar diretório para modelos se não existir
RUN mkdir -p /app/models

# Mudar ownership para usuário não-root
RUN chown -R appuser:appuser /app

# Usar usuário não-root
USER appuser

# Variáveis de ambiente
ENV PYTHONUNBUFFERED=1 \
    PYTHONDONTWRITEBYTECODE=1 \
    PORT=5000

# Expor porta
EXPOSE 5000

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=20s --retries=3 \
    CMD curl -f http://localhost:5000/health || exit 1

# Comando de inicialização
CMD ["uvicorn", "java_integration_api:app", "--host", "0.0.0.0", "--port", "5000"]

# ============================================================================
# Como construir e executar:
# ============================================================================
# docker build -f data_science/semana_04/scripts/Dockerfile -t flightontime-python:latest .
# docker run -p 5000:5000 flightontime-python:latest
# ============================================================================
