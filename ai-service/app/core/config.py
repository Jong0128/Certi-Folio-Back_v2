from functools import lru_cache

from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8")

    ai_provider: str = "mock"
    ai_request_timeout_seconds: int = 20
    internal_api_key: str = "local-secret"
    log_level: str = "INFO"
    openai_api_key: str | None = None
    openai_model: str | None = None


@lru_cache
def get_settings() -> Settings:
    return Settings()
