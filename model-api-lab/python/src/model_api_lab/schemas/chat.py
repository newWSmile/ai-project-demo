"""聊天接口的请求与响应 Schema。"""

from pydantic import BaseModel, ConfigDict, Field, field_validator


class HealthResponse(BaseModel):
    """健康检查响应。"""

    status: str
    service: str


class ChatRequest(BaseModel):
    """对外聊天请求，通过 Pydantic 完成长度和空白校验。"""

    message: str = Field(max_length=4_000)

    @field_validator("message")
    @classmethod
    def validate_message(cls, value: str) -> str:
        """拒绝空字符串和只包含空白字符的消息，并消除首尾无意义空格。"""

        normalized = value.strip()
        if not normalized:
            raise ValueError("message 不能为空")
        return normalized


class ChatResponse(BaseModel):
    """与 Java 服务保持一致的聊天响应 JSON。"""

    model_config = ConfigDict(populate_by_name=True)

    content: str
    prompt_tokens: int | None = Field(alias="promptTokens")
    completion_tokens: int | None = Field(alias="completionTokens")
    total_tokens: int | None = Field(alias="totalTokens")
    duration_ms: int = Field(alias="durationMs")

