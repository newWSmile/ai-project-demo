"""模型聊天应用服务。"""

from model_api_lab.clients.base import ModelClient
from model_api_lab.domain.models import ModelResult


class ChatService:
    """编排聊天用例，使 API 层不直接依赖具体模型供应商。"""

    def __init__(self, model_client: ModelClient) -> None:
        self._model_client = model_client

    def chat(self, user_message: str) -> ModelResult:
        """执行一次聊天；后续记忆、Token 预算和审计将在此处编排。"""

        return self._model_client.chat(user_message)

