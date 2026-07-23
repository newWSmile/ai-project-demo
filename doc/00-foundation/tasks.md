# 第 1～2 周：开发环境与双语言基础

## 阶段目标

建立 Java 21、Spring Boot 4、Spring AI 2 和 Python 3.13 的可复现开发环境，完成 Java/Python 对同一模型的基本调用。

## 第 1 周：环境与模型 API

### 环境

- [x] 安装 JDK 21，并记录 `java -version`（已验证：Java 21.0.12 LTS）。
- [ ] 了解 JDK 17 与 21 的主要差异：Record、sealed class、模式匹配、虚拟线程。
- [ ] 安装 Maven，并理解 BOM 和依赖管理。
- [x] 安装 Python 3.13.14，并记录 `python --version`（已验证：Python 3.13.14）。
- [ ] 选择并掌握一种 Python 环境/依赖工具：`uv` 优先，也可使用 `venv + pip`。
- [ ] 安装 Docker 与 Docker Compose。
- [x] 创建 `.env.example`，禁止将真实 API Key 提交到仓库。

### 第一次模型调用

- [x] 使用 Postman/curl 调用一个 OpenAI-compatible Chat API。
- [x] 记录请求模型、输入/输出 Token、延迟、HTTP 状态码。
- [ ] 用 Java 原生 HTTP Client 完成相同调用。
- [ ] 用 Python SDK 或 HTTP Client 完成相同调用。
- [ ] 分别实现同步和流式输出。
- [ ] 对超时、401、429、5xx 编写最小异常处理。

### 第 1 周产物

- [x] `model-api-lab` 可运行代码。
- [ ] 环境安装和启动说明。
- [ ] 一份 Java/Python 调用方式对比记录。

## 第 2 周：Spring AI 与 Python 辅助服务

- [ ] 使用 Spring Boot 4.1.x 创建项目。
- [ ] 使用 Spring AI 2.0.x BOM 管理依赖。
- [ ] 接入第一个 ChatModel。
- [ ] 实现普通对话接口和 SSE 流式接口。
- [ ] 将模型响应映射为 Java Record，而不是手工解析字符串 JSON。
- [x] 获取并记录模型 Usage 元数据。
- [ ] 创建最小 FastAPI 服务，完成请求参数校验和健康检查。
- [ ] 编写 Java 服务调用 FastAPI 的示例。
- [ ] 使用 Docker Compose 启动 Java 服务和 Python 服务。

## 完成标准

- [ ] 新环境按照 README 可在 30 分钟内完成启动。
- [ ] Java 与 Python 均能完成模型调用。
- [ ] API Key 不出现在代码、日志和 Git 历史中。
- [ ] 能解释为什么主业务使用 Java，而 Python 作为辅助生态工具。
