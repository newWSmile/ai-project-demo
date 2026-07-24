"""模型供应商客户端抽象。"""

from typing import Protocol

from model_api_lab.domain.models import ModelResult


class ModelClient(Protocol):
    """上层依赖的最小模型能力，替换供应商时只需实现该协议。"""

    def chat(self, user_message: str) -> ModelResult:
        """发送一次非流式聊天请求。"""

        ...

    def close(self) -> None:
        """释放供应商客户端持有的网络资源。"""

        ...

