# Semana 02 – Implementação do Pipeline e Microserviço (baseline)

Este repositório contém o microserviço responsável por realizar a previsão de atraso de voos. O serviço expõe uma API em Python que recebe os dados de um voo, executa todo o pré-processamento necessário por meio de um pipeline reprodutível e retorna a previsão de atraso e sua respectiva probabilidade.

---

## Visão Geral da Arquitetura

Cliente (Postman / API Java)
API Python (FastAPI)
Pipeline serializado (pré-processamento + modelo)
Resposta JSON

---

## Como executar a API

1. Criar ambiente virtual: python -m venv venv

2. Instalar dependências: pip install -r requirements.txt

3. Executar a API: uvicorn api_app:app --host 0.0.0.0 --port 8000 --reload

---

## Endpoints

GET /health  
POST /predict

---

## Exemplo de JSON de entrada

{
  "dados": {
    "partida_prevista": "2025-12-25 10:30:00",
    "empresa_aerea": "GOL",
    "codigo_tipo_linha": "Regular",
    "aerodromo_origem": "SBSP",
    "aerodromo_destino": "SBGL",
    "situacao_voo": "Realizado"
  }
}

---

## Exemplo de JSON de saída

{
  "prediction": 0,
  "proba_atraso": 0.48
}

---

## Arquivos principais

api_app.py  
flightontime_pipeline.pkl  
requirements.txt  
examples/
