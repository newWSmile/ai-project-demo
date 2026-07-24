"""FastAPI 健康检查、请求校验和响应映射测试。"""

from fastapi.testclient import TestClient

from model_api_lab.core.exceptions import ModelProviderError
from model_api_lab.domain.models import ModelResult
from model_api_lab.main import create_app


class FakeModelClient:
    """测试替身：避免自动化测试消耗真实模型额度。"""

    def chat(self, user_message: str) -> ModelResult:
        return ModelResult(
            content=f"测试回答：{user_message}",
            prompt_tokens=10,
            completion_tokens=5,
            total_tokens=15,
            duration_ms=20,
        )

    def close(self) -> None:
        pass


class FailingModelClient:
    """异常测试替身：模拟上游限流且不携带任何真实供应商响应。"""

    def chat(self, _user_message: str) -> ModelResult:
        raise ModelProviderError("模拟上游限流", provider_status=429)

    def close(self) -> None:
        pass


def create_test_client() -> TestClient:
    """为每个测试创建带假模型客户端的独立应用。"""

    return TestClient(create_app(client_factory=FakeModelClient))


def test_health() -> None:
    with create_test_client() as client:
        response = client.get("/health")

    assert response.status_code == 200
    assert response.json() == {
        "status": "UP",
        "service": "model-api-lab-python",
    }


def test_chat_response_mapping() -> None:
    with create_test_client() as client:
        response = client.post("/api/chat", json={"message": "你好"})

    assert response.status_code == 200
    assert response.json() == {
        "content": "测试回答：你好",
        "promptTokens": 10,
        "completionTokens": 5,
        "totalTokens": 15,
        "durationMs": 20,
    }


def test_chat_rejects_blank_message() -> None:
    with create_test_client() as client:
        response = client.post("/api/chat", json={"message": "   "})

    assert response.status_code == 422


def test_chat_maps_provider_error_to_safe_response() -> None:
    """上游异常应映射为 502，且不能把供应商正文或 API Key 暴露给调用方。"""

    with TestClient(create_app(client_factory=FailingModelClient)) as client:
        response = client.post("/api/chat", json={"message": "触发模拟限流"})

    assert response.status_code == 502
    assert response.json() == {
        "error": "上游模型请求失败",
        "details": {
            "type": "ModelProviderError",
            "providerStatus": 429,
        },
    }
