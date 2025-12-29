#!/usr/bin/env python3
"""
Mock Python Service - Plano B para Semana 2
Simula a API do time de Data Science
"""

from flask import Flask, request, jsonify
from datetime import datetime
import random

app = Flask(__name__)

@app.route('/predict', methods=['POST'])
def predict():
    """
    Endpoint de previsão (mock)
    Retorna dados simulados para não bloquear desenvolvimento
    """
    try:
        data = request.get_json()
        
        # Log da requisição
        print(f"📥 Requisição recebida: {data}")
        
        # Validação básica
        required_fields = ['companhia_icao', 'origem_icao', 'destino_icao', 
                          'data_partida', 'distancia_km']
        for field in required_fields:
            if field not in data:
                return jsonify({
                    'error': f'Campo obrigatório ausente: {field}'
                }), 400
        
        # Lógica mock simples
        distancia = data.get('distancia_km', 0)
        data_partida = data.get('data_partida', '')
        
        # Parseia hora da partida
        try:
            dt = datetime.fromisoformat(data_partida)
            hora = dt.hour
        except:
            hora = 12  # Default
        
        # Regras simples para mock
        if distancia > 1000 or hora >= 18:
            previsao = "Atrasado"
            prob_base = 0.7
        else:
            previsao = "Pontual"
            prob_base = 0.3
        
        # Adiciona aleatoriedade
        probabilidade = round(prob_base + random.uniform(-0.1, 0.2), 2)
        probabilidade = max(0.1, min(0.95, probabilidade))  # Limita entre 0.1 e 0.95
        
        response = {
            "previsao": previsao,
            "probabilidade": probabilidade,
            "modelo_versao": "mock-v1.0"
        }
        
        print(f"📤 Resposta enviada: {response}")
        return jsonify(response), 200
        
    except Exception as e:
        print(f"❌ Erro: {str(e)}")
        return jsonify({
            'error': 'Erro interno do servidor',
            'details': str(e)
        }), 500


@app.route('/health', methods=['GET'])
def health():
    """
    Health check endpoint
    """
    return jsonify({
        "status": "UP",
        "message": "Mock Python Service is running",
        "version": "1.0.0"
    }), 200


@app.route('/', methods=['GET'])
def index():
    """
    Página inicial
    """
    return """
    <h1>🐍 Mock Python Service</h1>
    <p>Serviço mock para desenvolvimento da API Java</p>
    <h3>Endpoints:</h3>
    <ul>
        <li>POST /predict - Previsão de voo</li>
        <li>GET /health - Health check</li>
    </ul>
    <p><strong>Status:</strong> ✅ Rodando</p>
    """, 200


if __name__ == '__main__':
    print("=" * 50)
    print("🐍 Mock Python Service")
    print("=" * 50)
    print("📍 URL: http://localhost:5000")
    print("📖 Endpoints:")
    print("   - POST /predict")
    print("   - GET  /health")
    print("=" * 50)
    
    app.run(
        host='0.0.0.0',
        port=5000,
        debug=True
    )
