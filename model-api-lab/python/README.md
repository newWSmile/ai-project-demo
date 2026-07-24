# Model API Lab Python 服务

本目录是 Python 3.13 辅助服务和跨语言实验，不采用平铺脚本结构。可复用业务代码位于 `src/model_api_lab`，命令行实验位于 `scripts`，自动化测试位于 `tests`。

## 目录职责

```text
python/
├─ pyproject.toml                    依赖、打包和 pytest 配置
├─ src/model_api_lab/
│  ├─ main.py                       FastAPI 装配、生命周期、全局异常映射
│  ├─ api/routes.py                 HTTP 路由和依赖获取
│  ├─ schemas/chat.py               Pydantic 请求/响应校验
│  ├─ services/chat_service.py      聊天用例编排入口
│  ├─ clients/base.py               模型客户端抽象协议
│  ├─ clients/dashscope.py          DashScope 协议适配与 HTTP 调用
│  ├─ core/config.py                环境变量解析和启动校验
│  ├─ core/exceptions.py            统一异常
│  ├─ core/prompts.py               统一中文提示词
│  └─ domain/models.py              供应商无关的领域响应
├─ scripts/
│  ├─ direct_model_call.py          Python 直接调用 DashScope
│  └─ call_java_service.py          Python 调用本地 Java 服务
└─ tests/test_fastapi_app.py        不消耗 Token 的接口自动化测试
```

依赖方向是 `API → Service → ModelClient`。DashScope 只是 `ModelClient` 的一种实现；未来新增其他模型供应商时，不需要修改 HTTP Schema 和路由。

## 安装

```powershell
cd E:\smile\ai-project-1\model-api-lab\python
python -m venv .venv
.\.venv\Scripts\python.exe -m pip install --upgrade pip
.\.venv\Scripts\python.exe -m pip install ".[dev]"
```

## 启动 FastAPI

先在当前终端配置密钥，密钥不要写入源码或 `.http` 文件：

```powershell
$env:DASHSCOPE_API_KEY = "你的真实Key"
$env:MODEL_NAME = "qwen-plus"
$env:MODEL_CHAT_COMPLETIONS_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions"

.\.venv\Scripts\python.exe -m uvicorn model_api_lab.main:app --host 127.0.0.1 --port 8000 --reload
```

可访问：

- 健康检查：`GET http://localhost:8000/health`
- 普通对话：`POST http://localhost:8000/api/chat`
- Swagger：`http://localhost:8000/docs`

IDEA 请求已记录在仓库根目录的 `httplog/model-api-lab.http`。

## 测试与实验脚本

```powershell
# 使用假模型客户端，不访问网络，不消耗 DashScope Token。
.\.venv\Scripts\python.exe -m pytest -q

# Python 直接调用真实 DashScope。
.\.venv\Scripts\python.exe scripts\direct_model_call.py

# Java 服务启动后，从 Python 调用 Java 接口。
.\.venv\Scripts\python.exe scripts\call_java_service.py
```

