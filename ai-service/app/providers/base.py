from abc import ABC, abstractmethod

from app.schemas.analyze import AnalyzeRequest, AnalyzeResponse


class LLMProvider(ABC):
    @abstractmethod
    async def analyze(self, request: AnalyzeRequest) -> AnalyzeResponse:
        raise NotImplementedError
