import flight_delay_pipeline as scr
from pathlib import Path

BASE_DIR = Path(__file__).resolve().parent
MODEL_PATH = BASE_DIR / "flightontime_pipeline.pkl"

app = scr.criar_app_fastapi(str(MODEL_PATH))
