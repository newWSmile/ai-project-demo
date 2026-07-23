# AI 应用开发学习项目

本仓库用于一名具有多年 Java 企业开发经验的工程师，系统学习 AI 应用开发并逐步构建具备企业级落地能力的 Demo。

当前主线项目是 `model-api-lab`：使用 Java 21、Spring Boot 4、Spring AI 2 和阿里云百炼 DashScope，学习模型 API、流式响应、供应商适配、Token Usage、对话记忆和后续 RAG/Agent 工程化能力。

## 一、重要入口

| 内容 | 位置 | 说明 |
| --- | --- | --- |
| 24 周学习总纲 | [`doc/outline.md`](./doc/outline.md) | 学习目标、阶段安排、技术选型和完成标准 |
| 项目宪法 | [`AGENTS.md`](./AGENTS.md) | 所有开发者和 AI 工具必须遵守的仓库级规范 |
| 基础阶段任务 | [`doc/00-foundation/tasks.md`](./doc/00-foundation/tasks.md) | 环境、模型 API、Java/Python 基础调用任务 |
| LLM 应用阶段任务 | [`doc/02-llm-application/tasks.md`](./doc/02-llm-application/tasks.md) | Prompt、会话、工具调用和模型适配任务 |
| HTTP 调试记录 | [`httplog/model-api-lab.http`](./httplog/model-api-lab.http) | 可在 IntelliJ IDEA 中直接执行的请求 |
| 实验日志 | [`model-api-lab/docs/experiment-log.md`](./model-api-lab/docs/experiment-log.md) | Token、耗时、假设、观察和实验结论 |
| 子项目说明 | [`model-api-lab/README.md`](./model-api-lab/README.md) | `model-api-lab` 的详细配置和使用说明 |

## 二、项目目录

```text
ai-project-1/
├─ AGENTS.md                         项目宪法，AI 工具和开发者的强制规范
├─ README.md                         仓库总入口
├─ doc/
│  ├─ outline.md                     24 周学习总纲
│  ├─ 00-foundation/                 基础环境与模型 API
│  ├─ 01-ai-theory/                  AI 与大模型基础理论
│  ├─ 02-llm-application/            Prompt、会话、记忆和模型适配
│  ├─ 03-rag/                        RAG 与知识库
│  ├─ 04-agent/                      Agent 与工具调用
│  ├─ 05-enterprise-engineering/     可靠性、安全、监控和成本
│  ├─ 06-enterprise-demo/            企业级综合 Demo
│  └─ 07-career/                     求职、作品集和面试准备
├─ httplog/
│  ├─ README.md                      HTTP 调用记录规范
│  └─ model-api-lab.http             当前项目接口调用记录
└─ model-api-lab/                    第一个模型 API 学习项目
```

## 三、技术栈

| 分类 | 技术 | 当前用途 |
| --- | --- | --- |
| Java | Java 21.0.12 LTS | AI 应用主开发语言 |
| 构建工具 | Maven | 依赖管理、编译和测试 |
| Web 框架 | Spring Boot 4.1.0 | REST API、配置、校验和 Actuator |
| AI 框架 | Spring AI 2.0.0 | 模型调用、同步和流式输出 |
| 原生模型调用 | Java 21 `java.net.http.HttpClient` | 理解 OpenAI-compatible 底层协议 |
| JSON | Jackson 3 | 请求序列化和响应 Record 映射 |
| 响应式流 | Reactor `Flux` | SSE 流式内容输出 |
| 模型供应商 | 阿里云百炼 DashScope | OpenAI-compatible Chat API |
| 默认模型 | `qwen-plus` | 当前模型实验基线 |
| Python | Python 3.13.14 | 后续 AI 生态和辅助服务实验 |
| 接口测试 | IntelliJ IDEA HTTP Client / Postman | 手工调用、问题复现和回归验证 |

## 四、环境要求

- 本机可以继续使用系统级 Java 8，禁止为了本仓库修改其他项目的全局 Java 环境。
- `model-api-lab` 通过项目脚本或 IDEA 项目配置单独使用 Java 21。
- 需要 Maven 3.8.9 或更高版本。
- 调用模型需要有效的阿里云百炼 DashScope API Key。
- Python 3.13.14 仅在运行 Python 实验时需要。

## 五、配置 DashScope

项目默认配置：

```text
Base URL: https://dashscope.aliyuncs.com/compatible-mode/v1
Model: qwen-plus
```

必须提供环境变量：

```text
DASHSCOPE_API_KEY=你的 DashScope API Key
```

可选环境变量：

```text
MODEL_NAME=qwen-plus
MODEL_BASE_URL=https://dashscope.aliyuncs.com/compatible-mode/v1
MODEL_TEMPERATURE=0.2
SERVER_PORT=8080
```

真实 API Key 只能配置在环境变量或 IDEA 私有启动配置中，禁止写入源码、`.http` 文件、日志或 Git。

### IntelliJ IDEA 配置

打开：

```text
Run / Debug Configurations
→ ModelApiLabApplication
→ Environment variables
```

至少添加：

```text
DASHSCOPE_API_KEY=你的真实Key
MODEL_NAME=qwen-plus
```

项目 SDK 和运行 JRE 选择 Java 21，然后启动：

```text
com.example.ai.modelapilab.ModelApiLabApplication
```

## 六、使用项目脚本启动

PowerShell：

```powershell
cd E:\smile\ai-project-1\model-api-lab

$env:DASHSCOPE_API_KEY = "你的真实Key"
$env:MODEL_NAME = "qwen-plus"

.\mvn-jdk21.ps1 clean test
.\mvn-jdk21.ps1 spring-boot:run
```

CMD：

```bat
cd /d E:\smile\ai-project-1\model-api-lab
set DASHSCOPE_API_KEY=你的真实Key
set MODEL_NAME=qwen-plus

mvn-jdk21.cmd clean test
mvn-jdk21.cmd spring-boot:run
```

项目脚本只在当前 Maven 进程中切换到 Java 21，执行结束后不会永久修改系统 Java 8、`JAVA_HOME` 或 PATH。

## 七、验证服务

健康检查：

```http
GET http://localhost:8080/actuator/health
```

当前接口：

| 接口 | 实现 | 用途 |
| --- | --- | --- |
| `POST /api/chat` | Spring AI | 普通非流式模型调用，返回 Usage 和总耗时 |
| `POST /api/chat/stream` | Spring AI + SSE | 流式返回模型生成内容 |
| `POST /api/chat/raw` | Java 21 原生 HttpClient | 不经过 Spring AI，直接调用 DashScope |

推荐在 IntelliJ IDEA 中打开并执行：

```text
httplog/model-api-lab.http
```

常规调用只记录请求和用途；只有真实发生过的异常或边界问题才增加针对性回归断言。

## 八、当前已完成能力

- Java 21 项目级运行，不影响系统 Java 8。
- Spring AI 普通对话调用。
- Spring AI SSE 流式输出和 UTF-8 中文处理。
- Java 21 原生 HttpClient 调用 DashScope。
- OpenAI-compatible 请求和响应 Record 映射。
- Token Usage 和端到端耗时记录。
- Spring AI 与原生 HttpClient 控制变量对比实验。
- 项目宪法、学习任务、HTTP 调用记录和实验日志。

## 九、知识文档

- [模型供应商适配层与对话记忆](./doc/02-llm-application/model-adapter-and-conversation-memory.md)
- [大模型 Token 预算管理](./doc/02-llm-application/token-budget.md)
- [Spring AI 与 Java 原生 HttpClient 对比实验](./model-api-lab/docs/java-client-comparison.md)

## 十、项目宪法摘要

完整规则以 [`AGENTS.md`](./AGENTS.md) 为准，核心要求包括：

- 重要类、重要方法和关键步骤必须有说明目的、原因或风险的中文注释。
- 所有源代码、配置、文档、脚本和 `.http` 文件使用 UTF-8。
- 每个可运行项目维护 `httplog/<项目名>.http`。
- 常规调用不机械添加断言，异常场景才添加针对性断言。
- API Key 等敏感信息必须通过环境变量或私有配置注入。
- 新功能完成后需要编译、测试、更新任务清单和实验日志。
- 未经实际验证的任务不得提前标记完成。

Claude Code、GitHub Copilot 和 Cursor 的规则入口也已配置，但根目录 `AGENTS.md` 是本仓库的唯一权威规范。
