from pydantic import BaseModel


class ErrorResponse(BaseModel):
    code: str
    message: str
    detail: str | None = None


class HealthResponse(BaseModel):
    status: str
    service: str
    version: str
