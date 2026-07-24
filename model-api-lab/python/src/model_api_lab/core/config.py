"""应用配置及环境变量解析。"""

from dataclasses import dataclass
import os

from model_api_lab.core.exceptions import ConfigurationError


@dataclass(frozen=True, slots=True)
class Settings:
    """启动后保持不变的模型连接配置。"""

    api_key: str
    chat_completions_url: str
    model: str
    temperature: float

    @classmethod
    def from_environment(cls) -> "Settings":
        """集中解析并校验环境变量，避免配置读取散落在业务代码中。"""

        api_key = os.getenv("DASHSCOPE_API_KEY") or os.getenv("MODEL_API_KEY") or ""
        if not api_key:
            raise ConfigurationError("缺少 DASHSCOPE_API_KEY（或 MODEL_API_KEY）环境变量")

        temperature_text = os.getenv("MODEL_TEMPERATURE", "0.2")
        try:
            temperature = float(temperature_text)
        except ValueError as exception:
            raise ConfigurationError("MODEL_TEMPERATURE 必须是数字") from exception

        if not 0 <= temperature <= 2:
            raise ConfigurationError("MODEL_TEMPERATURE 必须在 0 到 2 之间")

        return cls(
            api_key=api_key,
            chat_completions_url=os.getenv(
                "MODEL_CHAT_COMPLETIONS_URL",
                "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions",
            ),
            model=os.getenv("MODEL_NAME", "qwen-plus"),
            temperature=temperature,
        )

