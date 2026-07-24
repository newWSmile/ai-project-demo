"""直接调用 DashScope 的命令行对比实验。"""

from model_api_lab.clients.dashscope import DashScopeClient
from model_api_lab.core.prompts import DEFAULT_USER_PROMPT


def main() -> None:
    """输出模型回答、Token Usage 和总耗时，便于与 Java 调用做控制变量对比。"""

    # 命令行脚本与 FastAPI 复用同一个 Client，防止请求协议和异常处理出现两份实现。
    with DashScopeClient.from_environment() as model_client:
        result = model_client.chat(DEFAULT_USER_PROMPT)

    print(result.content)
    print(
        "Token 用量：",
        {
            "prompt_tokens": result.prompt_tokens,
            "completion_tokens": result.completion_tokens,
            "total_tokens": result.total_tokens,
        },
    )
    print("总耗时（毫秒）：", result.duration_ms)


if __name__ == "__main__":
    main()

