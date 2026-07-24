"""模型调用领域对象。"""

from dataclasses import dataclass


@dataclass(frozen=True, slots=True)
class ModelResult:
    """项目内部统一的模型响应，避免上层直接依赖供应商 JSON。"""

    content: str
    prompt_tokens: int | None
    completion_tokens: int | None
    total_tokens: int | None
    duration_ms: int

