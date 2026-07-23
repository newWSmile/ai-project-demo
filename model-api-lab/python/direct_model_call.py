import os
import time

import httpx


def required_environment(name: str) -> str:
    value = os.getenv(name)
    if not value:
        raise RuntimeError(f"Missing required environment variable: {name}")
    return value


def main() -> None:
    url = os.getenv(
        "MODEL_CHAT_COMPLETIONS_URL",
        "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions",
    )
    api_key = os.getenv("DASHSCOPE_API_KEY") or os.getenv("MODEL_API_KEY")
    if not api_key:
        raise RuntimeError("Missing DASHSCOPE_API_KEY (or MODEL_API_KEY)")
    model = os.getenv("MODEL_NAME", "qwen-plus")

    started_at = time.perf_counter()
    response = httpx.post(
        url,
        headers={"Authorization": f"Bearer {api_key}"},
        json={
            "model": model,
            "messages": [{"role": "user", "content": "用一句话解释什么是 Token。"}],
            "temperature": 0.2,
        },
        timeout=30,
    )
    response.raise_for_status()
    payload = response.json()

    print(payload["choices"][0]["message"]["content"])
    print("usage:", payload.get("usage", {}))
    print("duration_ms:", round((time.perf_counter() - started_at) * 1000))


if __name__ == "__main__":
    main()
