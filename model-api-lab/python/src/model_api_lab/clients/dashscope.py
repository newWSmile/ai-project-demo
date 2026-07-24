"""DashScope OpenAI-compatible HTTP 客户端。"""

import time
from types import TracebackType

import httpx

from model_api_lab.core.config import Settings
from model_api_lab.core.exceptions import ModelProviderError
from model_api_lab.core.prompts import SYSTEM_PROMPT
from model_api_lab.domain.models import ModelResult


class DashScopeClient:
    """负责 DashScope 鉴权、请求组装、状态检查和供应商响应转换。"""

    def __init__(self, settings: Settings) -> None:
        self._settings = settings

        # 复用 Client 可以复用连接池，避免每次请求都重新建立网络连接。
        self._client = httpx.Client(
            headers={
                "Authorization": f"Bearer {settings.api_key}",
                "Content-Type": "application/json; charset=UTF-8",
                "Accept": "application/json",
            },
            timeout=httpx.Timeout(30.0, connect=10.0),
        )

    @classmethod
    def from_environment(cls) -> "DashScopeClient":
        """从环境变量创建客户端，确保真实 API Key 不进入源码和日志。"""

        return cls(Settings.from_environment())

    def chat(self, user_message: str) -> ModelResult:
        """发送一次非流式模型请求，并转换为供应商无关的统一响应。"""

        # 第 1 步：按照 OpenAI-compatible 协议组装系统消息和用户消息。
        request_body = {
            "model": self._settings.model,
            "messages": [
                {"role": "system", "content": SYSTEM_PROMPT},
                {"role": "user", "content": user_message},
            ],
            "temperature": self._settings.temperature,
            "stream": False,
        }

        # 第 2 步：同步发送请求；FastAPI 的同步路由会在线程池中执行，避免阻塞事件循环。
        started_at = time.perf_counter()
        try:
            response = self._client.post(self._settings.chat_completions_url, json=request_body)
        except httpx.TimeoutException as exception:
            raise ModelProviderError("DashScope 请求超时") from exception
        except httpx.RequestError as exception:
            raise ModelProviderError("DashScope 网络请求失败") from exception

        # 第 3 步：非 2xx 响应不能按正常模型结果解析，也不能向上层泄露供应商正文。
        if not 200 <= response.status_code < 300:
            raise ModelProviderError(
                "DashScope 返回了非成功状态码",
                provider_status=response.status_code,
            )

        # 第 4 步：逐层校验 JSON，防止状态码成功但响应结构缺失时产生难以定位的异常。
        try:
            payload = response.json()
            if not isinstance(payload, dict):
                raise ModelProviderError("DashScope 响应不是 JSON 对象")

            choices = payload.get("choices")
            if not isinstance(choices, list) or not choices or not isinstance(choices[0], dict):
                raise ModelProviderError("DashScope 响应中没有候选结果")

            message = choices[0].get("message")
            if not isinstance(message, dict) or not isinstance(message.get("content"), str):
                raise ModelProviderError("DashScope 响应中没有助手消息")

            usage = payload.get("usage") or {}
            if not isinstance(usage, dict):
                raise ModelProviderError("DashScope 响应中的 Usage 格式错误")
        except ModelProviderError:
            raise
        except (TypeError, ValueError) as exception:
            raise ModelProviderError("DashScope 响应 JSON 解析失败") from exception

        # 第 5 步：供应商字段只在 Client 层出现，上层统一使用 ModelResult。
        return ModelResult(
            content=message["content"],
            prompt_tokens=usage.get("prompt_tokens"),
            completion_tokens=usage.get("completion_tokens"),
            total_tokens=usage.get("total_tokens"),
            duration_ms=round((time.perf_counter() - started_at) * 1000),
        )

    def close(self) -> None:
        """关闭底层连接池，在服务停止时释放网络资源。"""

        self._client.close()

    def __enter__(self) -> "DashScopeClient":
        return self

    def __exit__(
        self,
        exception_type: type[BaseException] | None,
        exception: BaseException | None,
        traceback: TracebackType | None,
    ) -> None:
        self.close()

