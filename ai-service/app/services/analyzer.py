import logging
import time

from app.core.config import Settings
from app.providers.base import LLMProvider
from app.providers.mock import MockProvider
from app.providers.openai_provider import OpenAIProvider
from app.schemas.analyze import AnalyzeRequest, AnalyzeResponse

logger = logging.getLogger(__name__)


def create_provider(settings: Settings) -> LLMProvider:
    if settings.ai_provider == "mock":
        return MockProvider()
    if settings.ai_provider == "openai":
        return OpenAIProvider()
    raise ValueError(f"Unsupported AI provider: {settings.ai_provider}")


class AnalyzerService:
    def __init__(self, provider: LLMProvider):
        self.provider = provider

    async def analyze(self, request: AnalyzeRequest, request_id: str | None) -> AnalyzeResponse:
        started = time.perf_counter()
        try:
            response = await self.provider.analyze(request)
            duration_ms = round((time.perf_counter() - started) * 1000, 2)
            logger.info(
                "analysis completed request_id=%s user_id=%s duration_ms=%s",
                request_id,
                request.userId,
                duration_ms,
            )
            return response
        except Exception:
            duration_ms = round((time.perf_counter() - started) * 1000, 2)
            logger.exception(
                "analysis failed request_id=%s user_id=%s duration_ms=%s",
                request_id,
                request.userId,
                duration_ms,
            )
            raise
