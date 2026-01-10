"""
Script de teste para API de integração Java/Python
"""
import requests
import json
from datetime import datetime

BASE_URL = "http://localhost:5000"

def test_health():
    """Testa endpoint /health"""
    print("🔍 Testando /health...")
    try:
        response = requests.get(f"{BASE_URL}/health", timeout=5)
        print(f"✅ Status: {response.status_code}")
        print(f"📄 Resposta: {response.json()}")
        return True
    except Exception as e:
        print(f"❌ Erro: {e}")
        return False

def test_prediction():
    """Testa endpoint /predict"""
    print("\n🔍 Testando /predict...")
    
    # Payload de teste (contrato Java)
    payload = {
        "companhia_icao": "GLO",
        "origem_icao": "SBGR",
        "destino_icao": "SBGL",
        "data_partida": "2025-12-25T10:30:00",
        "distancia_km": 350
    }
    
    print(f"📤 Request:\n{json.dumps(payload, indent=2)}")
    
    try:
        response = requests.post(
            f"{BASE_URL}/predict",
            json=payload,
            headers={"Content-Type": "application/json"},
            timeout=10
        )
        print(f"✅ Status: {response.status_code}")
        print(f"📄 Resposta:\n{json.dumps(response.json(), indent=2)}")
        return True
    except Exception as e:
        print(f"❌ Erro: {e}")
        return False

if __name__ == "__main__":
    print("=" * 60)
    print("🧪 Testes da API de Integração Java ↔ Python")
    print("=" * 60)
    
    # Teste 1: Health check
    health_ok = test_health()
    
    # Teste 2: Previsão
    if health_ok:
        test_prediction()
    else:
        print("\n⚠️ API não está respondendo. Certifique-se de que está rodando.")
        print("Para iniciar: python java_integration_api.py")
    
    print("\n" + "=" * 60)
