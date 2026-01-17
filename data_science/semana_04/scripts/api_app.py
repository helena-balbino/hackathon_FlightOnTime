from pathlib import Path
import json
import pandas as pd
from fastapi import FastAPI, HTTPException, Body

hooking = "ok"

import flight_delay_pipeline as scr

BASE_DIR = Path(__file__).resolve().parent
MODEL_PATH = BASE_DIR / "flightontime_pipeline.pkl"
EXPLAIN_GLOBAL_PATH = BASE_DIR / "explain_global.json"

app = FastAPI(title="FlightOnTime API", version="2.0")
API_VERSION = "2.0"
# carrega pipeline
pipeline = scr.carregar_pickle(str(MODEL_PATH))

# carrega explicabilidade global (json)
explain_global = None
if EXPLAIN_GLOBAL_PATH.exists():
    with open(EXPLAIN_GLOBAL_PATH, "r", encoding="utf-8") as f:
        explain_global = json.load(f)

REQUIRED_RAW_COLS = [
    "partida_prevista",
    "empresa_aerea",
    "aerodromo_origem",
    "aerodromo_destino",
    "codigo_tipo_linha",
]

@app.get("/health")
def health():
    modelo_carregado = "pipeline" in globals() and globals().get("pipeline") is not None
    modelo_path_ok = "MODEL_PATH" in globals() and Path(str(globals().get("MODEL_PATH"))).exists()

    return {
        "status": "UP",
        "message": "FlightOnTime API is running",
        "modelo_carregado": bool(modelo_carregado),
        "modelo_path_ok": bool(modelo_path_ok),
        "version": API_VERSION
    }

@app.get("/explain/global")
def explain_global_endpoint():
    if explain_global is None:
        raise HTTPException(status_code=404, detail="Arquivo de explicabilidade global não encontrado.")
    return {"explain_global": explain_global}

@app.post("/predict")
def predict(payload: dict = Body(...)):
    """
    payload esperado:
    {
      "dados": {
        "partida_prevista": "2024-03-01 10:30:00",
        "empresa_aerea": "GLO",
        "aerodromo_origem": "SBGR",
        "aerodromo_destino": "SBRJ",
        "codigo_tipo_linha": "N"
      },
      "topk": 8
    }
    """
    if "dados" not in payload:
        raise HTTPException(status_code=400, detail="Payload deve conter a chave 'dados'.")

    topk = int(payload.get("topk", 8))

    x = pd.DataFrame([payload["dados"]])

    faltando = [c for c in REQUIRED_RAW_COLS if c not in x.columns]
    if faltando:
        raise HTTPException(status_code=400, detail=f"Faltando colunas obrigatórias: {faltando}")

    pred = int(pipeline.predict(x)[0])

    resp = {
        "prediction": pred,
        "label": "atrasado" if pred == 1 else "no_prazo",
    }

    if hasattr(pipeline, "predict_proba"):
        resp["proba_atraso"] = float(pipeline.predict_proba(x)[0, 1])

    # explicabilidade local (XGBoost contribs)
    try:
        resp["explain_local"] = scr.explicar_local_xgb(pipeline, x, top_k=topk)
    except Exception as e:
        resp["explain_local_error"] = str(e)

    return resp
