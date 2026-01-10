"""
API de Integração Java ↔ Python
Adapta o contrato do Java para o formato esperado pelo modelo de ML
"""

from fastapi import FastAPI, HTTPException, Body
from pydantic import BaseModel, Field
from typing import Optional
from pathlib import Path
import pandas as pd
import pickle
from datetime import datetime

# ============================================================================
# Configuração
# ============================================================================

BASE_DIR = Path(__file__).resolve().parent
MODEL_PATH = BASE_DIR / "flightontime_pipeline.pkl"

# ============================================================================
# DTOs - Contrato com Java
# ============================================================================

class JavaPredictionRequest(BaseModel):
    """
    Formato que o Java envia
    """
    companhia_icao: str = Field(..., alias="companhia_icao", description="Código ICAO da companhia (ex: GLO, AZU)")
    origem_icao: str = Field(..., alias="origem_icao", description="Código ICAO origem (ex: SBGR)")
    destino_icao: str = Field(..., alias="destino_icao", description="Código ICAO destino (ex: SBGL)")
    data_partida: str = Field(..., alias="data_partida", description="Data/hora ISO (ex: 2025-12-25T10:30:00)")
    distancia_km: Optional[int] = Field(None, alias="distancia_km", description="Distância em km (opcional)")

    class Config:
        populate_by_name = True
        json_schema_extra = {
            "example": {
                "companhia_icao": "GLO",
                "origem_icao": "SBGR",
                "destino_icao": "SBGL",
                "data_partida": "2025-12-25T10:30:00",
                "distancia_km": 350
            }
        }


class JavaPredictionResponse(BaseModel):
    """
    Formato que o Java espera receber
    """
    previsao: str = Field(..., description="'Pontual' ou 'Atrasado'")
    probabilidade: float = Field(..., description="Probabilidade entre 0.0 e 1.0")
    modelo_versao: str = Field(default="v1.0", description="Versão do modelo")

    class Config:
        json_schema_extra = {
            "example": {
                "previsao": "Atrasado",
                "probabilidade": 0.78,
                "modelo_versao": "v1.0"
            }
        }


# ============================================================================
# Adaptador de Dados
# ============================================================================

class DataAdapter:
    """
    Converte dados do formato Java para o formato do modelo
    """
    
    @staticmethod
    def java_to_model_format(java_request: JavaPredictionRequest) -> dict:
        """
        Transforma request do Java no formato esperado pelo modelo
        """
        try:
            # Parseia data ISO
            dt = datetime.fromisoformat(java_request.data_partida)
            data_formatada = dt.strftime("%Y-%m-%d %H:%M:%S")
        except:
            # Fallback se o formato estiver incorreto
            data_formatada = java_request.data_partida
        
        # Monta payload no formato do modelo
        # O modelo espera: partida_prevista, empresa_aerea, aerodromo_origem, aerodromo_destino, situacao_voo
        modelo_payload = {
            "partida_prevista": data_formatada,
            "empresa_aerea": java_request.companhia_icao,
            "codigo_tipo_linha": "Regular",  # Assumindo Regular como padrão
            "aerodromo_origem": java_request.origem_icao,
            "aerodromo_destino": java_request.destino_icao,
            "situacao_voo": "Realizado"  # Assumindo Realizado para previsão
        }
        
        return {"dados": modelo_payload}
    
    @staticmethod
    def model_to_java_format(model_response: dict, modelo_versao: str = "v1.0") -> JavaPredictionResponse:
        """
        Transforma resposta do modelo no formato esperado pelo Java
        """
        # Modelo retorna: {"prediction": 0/1, "proba_atraso": float}
        prediction = model_response.get("prediction", 0)
        proba_atraso = model_response.get("proba_atraso", 0.5)
        
        # Converte para formato Java
        previsao = "Atrasado" if prediction == 1 else "Pontual"
        probabilidade = round(proba_atraso if prediction == 1 else (1 - proba_atraso), 2)
        
        return JavaPredictionResponse(
            previsao=previsao,
            probabilidade=probabilidade,
            modelo_versao=modelo_versao
        )


# ============================================================================
# API FastAPI
# ============================================================================

app = FastAPI(
    title="FlightOnTime - Java Integration API",
    version="2.0",
    description="API de integração entre Java (Backend) e Python (Data Science)"
)

# Carrega pipeline
try:
    # Adiciona o diretório atual ao path para imports do modelo
    import sys
    sys.path.insert(0, str(BASE_DIR))
    
    # Tenta importar o módulo necessário
    try:
        import flight_delay_pipeline as script_v3
    except ImportError:
        print("⚠️ flight_delay_pipeline não encontrado")
        print("   A API continuará funcionando em modo MOCK")
        pipeline = None
        script_v3 = None
    else:
        # Só tenta carregar o pickle se conseguiu importar o módulo
        with open(MODEL_PATH, "rb") as f:
            pipeline = pickle.load(f)
        print(f"✅ Modelo carregado: {MODEL_PATH}")
except Exception as e:
    print(f"⚠️ Erro ao carregar modelo: {e}")
    print("   A API continuará funcionando em modo MOCK")
    pipeline = None


@app.get("/health")
def health():
    """
    Health check do serviço
    """
    return {
        "status": "UP" if pipeline is not None else "DOWN",
        "message": "Java Integration API is running",
        "modelo_carregado": pipeline is not None,
        "version": "2.0"
    }


@app.post("/predict", response_model=JavaPredictionResponse)
def predict(request: JavaPredictionRequest):
    """
    Endpoint de previsão compatível com o contrato Java
    
    **Request (Java):**
    ```json
    {
        "companhia_icao": "GLO",
        "origem_icao": "SBGR",
        "destino_icao": "SBGL",
        "data_partida": "2025-12-25T10:30:00",
        "distancia_km": 350
    }
    ```
    
    **Response (Java):**
    ```json
    {
        "previsao": "Atrasado",
        "probabilidade": 0.78,
        "modelo_versao": "v1.0"
    }
    ```
    """
    
    # MODO MOCK se modelo não carregou
    if pipeline is None:
        print("⚠️ Usando MOCK - modelo não disponível")
        
        # Lógica mock simples baseada na data
        try:
            dt = datetime.fromisoformat(request.data_partida)
            hora = dt.hour
            
            # Heurística simples
            if hora >= 18 or (request.distancia_km and request.distancia_km > 1000):
                return JavaPredictionResponse(
                    previsao="Atrasado",
                    probabilidade=0.72,
                    modelo_versao="mock-v1.0"
                )
            else:
                return JavaPredictionResponse(
                    previsao="Pontual",
                    probabilidade=0.65,
                    modelo_versao="mock-v1.0"
                )
        except:
            return JavaPredictionResponse(
                previsao="Pontual",
                probabilidade=0.50,
                modelo_versao="mock-v1.0"
            )
    
    try:
        # 1. Adapta formato Java → Modelo
        modelo_payload = DataAdapter.java_to_model_format(request)
        
        # 2. Prepara DataFrame
        x = pd.DataFrame([modelo_payload["dados"]])
        
        # 3. Faz previsão
        pred = int(pipeline.predict(x)[0])
        
        # Monta resposta do modelo
        model_response = {"prediction": pred}
        
        if hasattr(pipeline, "predict_proba"):
            model_response["proba_atraso"] = float(pipeline.predict_proba(x)[0, 1])
        else:
            model_response["proba_atraso"] = 0.5  # Fallback
        
        # 4. Adapta formato Modelo → Java
        java_response = DataAdapter.model_to_java_format(model_response)
        
        return java_response
        
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Erro ao processar previsão: {str(e)}"
        )


@app.get("/")
def root():
    return {
        "message": "FlightOnTime - Java Integration API",
        "docs": "/docs",
        "health": "/health",
        "predict": "/predict (POST)"
    }


# ============================================================================
# Execução
# ============================================================================

if __name__ == "__main__":
    import uvicorn
    print("🚀 Iniciando API de Integração Java ↔ Python")
    print(f"📂 Modelo: {MODEL_PATH}")
    print("📍 URL: http://localhost:5000")
    print("📖 Docs: http://localhost:5000/docs")
    
    uvicorn.run(
        app,
        host="0.0.0.0",
        port=5000,
        log_level="info"
    )
