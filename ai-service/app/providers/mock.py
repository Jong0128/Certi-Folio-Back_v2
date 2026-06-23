from app.providers.base import LLMProvider
from app.schemas.analyze import AnalyzeRequest, AnalyzeResponse, Recommendation


class MockProvider(LLMProvider):
    async def analyze(self, request: AnalyzeRequest) -> AnalyzeResponse:
        project_count = len(request.projects)
        activity_count = len(request.activities)
        certificate_count = len(request.certificates)
        algorithm = request.algorithms[0] if request.algorithms else None
        solved_count = algorithm.solvedCount if algorithm else None

        strengths = []
        if project_count:
            strengths.append(f"{project_count}개의 프로젝트 경험을 보유하고 있습니다.")
        if activity_count:
            strengths.append(f"{activity_count}개의 활동 경험을 통해 실행 경험을 보여줍니다.")
        if certificate_count:
            strengths.append(f"{certificate_count}개의 자격증/인증 정보를 보유하고 있습니다.")
        if solved_count:
            strengths.append(f"알고리즘 문제 {solved_count}개 풀이 기록이 있습니다.")
        if not strengths:
            strengths.append("기본 프로필 분석이 가능하지만, 추가 스펙 입력이 필요합니다.")

        weaknesses = [
            "배포, 테스트, 관측성 경험이 명확히 드러나면 포트폴리오 설득력이 높아집니다.",
            "각 경험에 문제 상황, 본인 역할, 결과 지표를 더 구체적으로 작성하는 것이 좋습니다.",
        ]

        recommendations = [
            Recommendation(
                title="프로젝트 성과 정량화",
                description="주요 프로젝트마다 성능 개선, 사용자 흐름, 장애 해결 같은 결과를 수치나 사례로 정리하세요.",
                priority="high",
            ),
            Recommendation(
                title="운영 경험 추가",
                description="Docker, 배포 자동화, 로그/모니터링 경험을 하나의 미니 프로젝트로 보강하세요.",
                priority="medium",
            ),
        ]

        target = request.targetRole or "지원 직무"
        summary = f"{target} 관점에서 프로젝트 {project_count}개, 활동 {activity_count}개를 중심으로 분석했습니다."
        portfolio_text = "Spring Boot 기반 서비스 구현 경험을 중심으로, 문제 해결 과정과 결과를 함께 제시할 수 있습니다."

        return AnalyzeResponse(
            summary=summary,
            strengths=strengths,
            weaknesses=weaknesses,
            recommendations=recommendations,
            suggestedPortfolioText=portfolio_text,
            confidence=0.72,
        )
