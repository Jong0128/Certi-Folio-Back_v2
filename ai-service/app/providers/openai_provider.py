from app.providers.base import LLMProvider
from app.schemas.analyze import AnalyzeRequest, AnalyzeResponse


class OpenAIProvider(LLMProvider):
    async def analyze(self, request: AnalyzeRequest) -> AnalyzeResponse:
        raise NotImplementedError("OpenAI provider will be implemented after the local mock flow is verified.")
