import os
import time

import httpx


SYSTEM_PROMPT = "你是一名回答简洁、准确的 AI 助手。如果无法确定答案，请明确说明不确定，不要编造信息。"
# 对比实验必须与 Java 请求逐字一致；末尾不加句号，避免引入额外 Token。
USER_PROMPT = "请用三句话解释什么是大语言模型"


def main() -> None:
    """使用 Python httpx 直接调用 DashScope，并输出模型回答、Token Usage 和总耗时。"""

    # 第 1 步：读取模型地址、API Key 和模型名称，禁止在源码中保存真实密钥。
    url = os.getenv(
        "MODEL_CHAT_COMPLETIONS_URL",
        "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions",
    )
    api_key = os.getenv("DASHSCOPE_API_KEY") or os.getenv("MODEL_API_KEY")
    if not api_key:
        raise RuntimeError("缺少 DASHSCOPE_API_KEY（或 MODEL_API_KEY）环境变量")
    model = os.getenv("MODEL_NAME", "qwen-plus")

    # 第 2 步：按照 OpenAI-compatible 协议组装 Header 和 messages 请求报文。
    started_at = time.perf_counter()
    response = httpx.post(
        url,
        headers={
            "Authorization": f"Bearer {api_key}",
            "Content-Type": "application/json; charset=UTF-8",
            "Accept": "application/json",
        },
        json={
            "model": model,
            "messages": [
                {"role": "system", "content": SYSTEM_PROMPT},
                {"role": "user", "content": USER_PROMPT},
            ],
            "temperature": 0.2,
            "stream": False,
        },
        timeout=30,
    )

    # 第 3 步：非 2xx 状态直接抛出异常，不能继续当作正常模型响应解析。
    response.raise_for_status()

    # 第 4 步：解析模型内容和实际 Usage；实际 Usage 是后续成本与预算校准的依据。
    payload = response.json()

    print(payload["choices"][0]["message"]["content"])
    print("Token 用量：", payload.get("usage", {}))
    print("总耗时（毫秒）：", round((time.perf_counter() - started_at) * 1000))


if __name__ == "__main__":
    main()
