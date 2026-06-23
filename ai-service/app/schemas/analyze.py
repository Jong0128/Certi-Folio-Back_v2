from pydantic import BaseModel, Field


class Profile(BaseModel):
    school: str | None = None
    major: str | None = None
    grade: int | None = None


class Activity(BaseModel):
    title: str | None = None
    name: str | None = None
    type: str | None = None
    role: str | None = None
    description: str | None = None
    result: str | None = None
    startedAt: str | None = None
    endedAt: str | None = None
    startMonth: str | None = None
    endMonth: str | None = None


class Project(BaseModel):
    title: str | None = None
    name: str | None = None
    type: str | None = None
    role: str | None = None
    description: str | None = None
    techStacks: list[str] | None = None
    techStack: str | None = None
    githubLink: str | None = None
    demoLink: str | None = None
    result: str | None = None
    startDate: str | None = None
    endDate: str | None = None


class Certificate(BaseModel):
    name: str | None = None
    issuedAt: str | None = None
    issuer: str | None = None
    acquisitionDate: str | None = None


class Algorithm(BaseModel):
    platform: str | None = None
    handle: str | None = None
    bojHandle: str | None = None
    tier: int | None = None
    rating: int | None = None
    solvedCount: int | None = None


class AnalyzeRequest(BaseModel):
    userId: int
    targetRole: str | None = None
    profile: Profile | None = None
    activities: list[Activity] = Field(default_factory=list)
    projects: list[Project] = Field(default_factory=list)
    certificates: list[Certificate] = Field(default_factory=list)
    algorithms: list[Algorithm] = Field(default_factory=list)
    careers: list[dict] = Field(default_factory=list)
    educations: list[dict] = Field(default_factory=list)


class Recommendation(BaseModel):
    title: str
    description: str
    priority: str


class AnalyzeResponse(BaseModel):
    summary: str
    strengths: list[str]
    weaknesses: list[str]
    recommendations: list[Recommendation]
    suggestedPortfolioText: str
    confidence: float = Field(ge=0, le=1)
