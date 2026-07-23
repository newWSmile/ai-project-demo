import os

import httpx


def main() -> None:
    base_url = os.getenv("LAB_SERVICE_URL", "http://localhost:8080")
    response = httpx.post(
        f"{base_url}/api/chat",
        json={"message": "用一句话解释什么是 Embedding。"},
        timeout=60,
    )
    response.raise_for_status()
    print(response.json())


if __name__ == "__main__":
    main()

