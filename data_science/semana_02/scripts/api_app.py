import data_science.semana_02.scripts.flight_delay_pipeline as scr

MODEL_PATH = "artifacts/flightontime_pipeline.pkl"
app = scr.criar_app_fastapi(MODEL_PATH)
