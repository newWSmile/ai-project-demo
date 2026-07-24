"""应用统一异常。"""


class ConfigurationError(RuntimeError):
    """环境变量缺失或格式不合法，服务应在启动阶段快速失败。"""


class ModelProviderError(RuntimeError):
    """模型供应商状态异常、网络异常或响应解析异常。"""

    def __init__(self, message: str, provider_status: int | None = None) -> None:
        super().__init__(message)
        self.provider_status = provider_status

