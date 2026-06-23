from fastapi.testclient import TestClient

from app.main import app


def test_analyze_returns_mock_result():
    client = TestClient(app)

    response = client.post(
        "/ai/analyze",
        headers={"X-Internal-Api-Key": "local-secret"},
        json={
            "userId": 1,
            "targetRole": "backend developer",
            "projects": [
                {
                    "name": "CertiFolio",
                    "role": "backend",
                    "techStack": "Spring Boot, MySQL",
                }
            ],
        },
    )

    assert response.status_code == 200
    body = response.json()
    assert body["confidence"] > 0
    assert body["strengths"]


def test_analyze_rejects_invalid_internal_key():
    client = TestClient(app)

    response = client.post(
        "/ai/analyze",
        headers={"X-Internal-Api-Key": "wrong"},
        json={"userId": 1},
    )

    assert response.status_code == 401
