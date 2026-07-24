"""健康检查和模型对话 HTTP 路由。"""

from typing import Annotated

from fastapi import APIRouter, Depends, Request

from model_api_lab.schemas.chat import ChatRequest, ChatResponse, HealthResponse
from model_api_lab.services.chat_service import ChatService

router = APIRouter()


def get_chat_service(request: Request) -> ChatService:
    """从应用状态取得共享服务，避免每个请求重复创建模型连接池。"""

    return request.app.state.chat_service


ChatServiceDependency = Annotated[ChatService, Depends(get_chat_service)]


@router.get("/health", response_model=HealthResponse)
def health() -> HealthResponse:
    """返回进程级健康状态；该接口不会调用付费模型。"""

    return HealthResponse(status="UP", service="model-api-lab-python")


@router.post("/api/chat", response_model=ChatResponse)
def chat(request_body: ChatRequest, chat_service: ChatServiceDependency) -> ChatResponse:
    """校验用户输入并执行同步模型调用，FastAPI 会在线程池中运行该同步路由。"""

    result = chat_service.chat(request_body.message)
    return ChatResponse(
        content=result.content,
        prompt_tokens=result.prompt_tokens,
        completion_tokens=result.completion_tokens,
        total_tokens=result.total_tokens,
        duration_ms=result.duration_ms,
    )

