from fastapi import FastAPI

from app.core.config import get_settings
from app.core.logging import configure_logging
from app.routers import analyze, health

settings = get_settings()
configure_logging(settings.log_level)

app = FastAPI(title="CertiFolio AI Service", version="0.1.0")
app.include_router(health.router)
app.include_router(analyze.router)
