from fastapi import APIRouter, Depends, Header, HTTPException, Request, status

from app.core.config import Settings, get_settings
from app.schemas.analyze import AnalyzeRequest, AnalyzeResponse
from app.schemas.common import ErrorResponse
from app.services.analyzer import AnalyzerService, create_provider

router = APIRouter(prefix="/ai", tags=["ai"])


def verify_internal_api_key(
    x_internal_api_key: str | None = Header(default=None),
    settings: Settings = Depends(get_settings),
) -> None:
    if settings.internal_api_key and x_internal_api_key != settings.internal_api_key:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail=ErrorResponse(
                code="UNAUTHORIZED",
                message="Invalid internal API key.",
            ).model_dump(),
        )


def get_analyzer(settings: Settings = Depends(get_settings)) -> AnalyzerService:
    return AnalyzerService(create_provider(settings))


@router.post(
    "/analyze",
    response_model=AnalyzeResponse,
    responses={
        401: {"model": ErrorResponse},
        500: {"model": ErrorResponse},
    },
    dependencies=[Depends(verify_internal_api_key)],
)
async def analyze(
    payload: AnalyzeRequest,
    request: Request,
    analyzer: AnalyzerService = Depends(get_analyzer),
) -> AnalyzeResponse:
    request_id = request.headers.get("X-Request-Id")
    try:
        return await analyzer.analyze(payload, request_id)
    except NotImplementedError as exc:
        raise HTTPException(
            status_code=status.HTTP_502_BAD_GATEWAY,
            detail=ErrorResponse(
                code="AI_PROVIDER_NOT_IMPLEMENTED",
                message="Selected AI provider is not implemented.",
                detail=str(exc),
            ).model_dump(),
        ) from exc
    except Exception as exc:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=ErrorResponse(
                code="AI_ANALYSIS_FAILED",
                message="Failed to analyze user profile.",
                detail=str(exc),
            ).model_dump(),
        ) from exc
