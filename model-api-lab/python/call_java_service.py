import os

import httpx


def main() -> None:
    """调用本地 Java 模型服务，用于验证 Python 与 Java 服务之间的 HTTP 集成。"""

    # 第 1 步：从环境变量读取 Java 服务地址，未配置时使用本地默认端口。
    base_url = os.getenv("LAB_SERVICE_URL", "http://localhost:8080")

    # 第 2 步：发送 UTF-8 JSON 请求；API Key 由 Java 服务自身的环境变量管理。
    response = httpx.post(
        f"{base_url}/api/chat",
        headers={
            "Content-Type": "application/json; charset=UTF-8",
            "Accept": "application/json",
        },
        json={"message": "用一句话解释什么是 Embedding。"},
        timeout=60,
    )

    # 第 3 步：先校验 HTTP 状态，再解析 Java 服务返回的 JSON。
    response.raise_for_status()
    print(response.json())


if __name__ == "__main__":
    main()
