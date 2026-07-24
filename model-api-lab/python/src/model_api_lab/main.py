"""FastAPI 应用装配与生命周期管理。"""

from collections.abc import AsyncIterator, Callable
from contextlib import asynccontextmanager

from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse

from model_api_lab.api.routes import router
from model_api_lab.clients.base import ModelClient
from model_api_lab.clients.dashscope import DashScopeClient
from model_api_lab.core.exceptions import ModelProviderError
from model_api_lab.services.chat_service import ChatService


def create_app(
    client_factory: Callable[[], ModelClient] = DashScopeClient.from_environment,
) -> FastAPI:
    """创建应用并允许测试注入假客户端，避免自动化测试真实调用付费模型。"""

    @asynccontextmanager
    async def lifespan(application: FastAPI) -> AsyncIterator[None]:
        # 启动时只创建一次客户端和 Service，所有请求共享模型连接池。
        model_client = client_factory()
        application.state.chat_service = ChatService(model_client)
        try:
            yield
        finally:
            # 关闭阶段统一释放连接，避免测试或服务退出后遗留网络资源。
            model_client.close()

    application = FastAPI(
        title="Model API Lab Python Service",
        version="0.1.0",
        lifespan=lifespan,
    )

    @application.exception_handler(ModelProviderError)
    async def handle_model_provider_error(
        _request: Request,
        exception: ModelProviderError,
    ) -> JSONResponse:
        """将供应商异常转换为 502，且不向调用方泄露上游响应正文。"""

        details: dict[str, str | int] = {"type": type(exception).__name__}
        if exception.provider_status is not None:
            details["providerStatus"] = exception.provider_status

        return JSONResponse(
            status_code=502,
            content={"error": "上游模型请求失败", "details": details},
        )

    application.include_router(router)
    return application


app = create_app()

