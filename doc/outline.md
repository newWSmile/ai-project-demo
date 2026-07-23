# AI 应用开发工程师学习路线总纲

> 适用背景：8 年 Java 企业应用开发经验  
> 目标方向：AI 应用开发工程师，逐步具备企业级 AI 应用架构能力  
> 计划周期：24 周，建议每周投入 10～12 小时  
> 项目主线：Enterprise Copilot（企业知识与任务协同助手）

## 1. 最终目标

完成本路线后，应当能够独立完成以下工作：

- [ ] 理解大模型、Embedding、Transformer、RAG、Agent 的核心原理和适用边界。
- [ ] 使用 Java 构建支持流式输出、结构化输出、工具调用的 LLM 服务。
- [ ] 构建包含文档解析、混合检索、Rerank、权限过滤、引用和拒答的 RAG 系统。
- [ ] 构建带状态、人工审批、超时重试和幂等控制的 Agent 工作流。
- [ ] 建立离线评测集，对检索质量、答案质量、安全性和模型升级进行回归测试。
- [ ] 实现多租户、RBAC、审计、限流、降级、可观测性和成本治理。
- [ ] 使用 Docker Compose 一键部署企业级 Demo，并能说明架构选型和业务价值。

## 2. 技术基线

本项目固定使用以下学习基线，避免反复更换框架：

| 类别 | 选择 | 说明 |
|---|---|---|
| JDK | Java 21 LTS | 推荐基线；Java 17 也能运行 Boot 4/Spring AI 2 |
| Java 框架 | Spring Boot 4.1.x | 当前学习主线 |
| AI 框架 | Spring AI 2.0.x | Java 主框架 |
| Python | Python 3.13.14 | 用于 Notebook、评测和部分 AI 生态实验 |
| Python Web | FastAPI + Pydantic | 仅在 Java 不适合的场景使用 |
| 主数据库 | PostgreSQL + pgvector | 业务数据和向量数据起步方案 |
| 检索引擎 | OpenSearch/Elasticsearch | 第二阶段实现 BM25 与混合检索 |
| 缓存 | Redis | 会话、限流和缓存 |
| 对象存储 | MinIO | 保存原始文档和解析产物 |
| 可观测性 | Micrometer + OpenTelemetry + Prometheus + Grafana | 指标、Trace、Token 和成本 |
| 部署 | Docker Compose，Kubernetes 作为进阶 | 先完成可复现部署，再学习编排 |

说明：

1. Spring Boot 4.1 最低支持 Java 17，所以 Java 17 在技术上完全可用。
2. 新项目选择 Java 21，主要是因为它是成熟的 LTS 基线，并可使用虚拟线程等能力，而不是因为 Spring AI 2 强制要求 Java 21。
3. Python 与 Spring AI 没有运行时绑定。Python 3.13.14 可以作为独立工具链使用，但每个 Python 项目仍需锁定依赖并验证第三方库兼容性。
4. 不使用浮动的 `latest` 依赖；实际编码时通过 Maven BOM、锁文件和更新记录固定版本。

## 3. 24 周阶段任务总览

完成一个阶段后，在下面勾选，并在对应子目录保存代码、笔记、评测结果或截图。

- [ ] 第 1～2 周：[开发环境与双语言基础](./00-foundation/tasks.md)
- [ ] 第 3～5 周：[AI 与大模型基础理论](./01-ai-theory/tasks.md)
- [ ] 第 6～8 周：[LLM API 与 Prompt 工程](./02-llm-application/tasks.md)
- [ ] 第 9～13 周：[企业级 RAG](./03-rag/tasks.md)
- [ ] 第 14～17 周：[Tool、Workflow、Agent 与 MCP](./04-agent/tasks.md)
- [ ] 第 18～21 周：[企业工程化、评测与安全](./05-enterprise-engineering/tasks.md)
- [ ] 第 22～24 周：[Enterprise Copilot 交付](./06-enterprise-demo/tasks.md)
- [ ] 长期任务：[求职准备与持续学习](./07-career/tasks.md)

## 4. 学习内容地图

### 4.1 基础理论

- 机器学习基本流程：数据集、训练、验证、测试、损失函数、过拟合和泛化。
- 必要数学直觉：向量、矩阵、点积、余弦相似度、概率、交叉熵。
- 大模型原理：Token、Embedding、Transformer、Attention、位置编码、预训练和推理。
- 对齐和定制：SFT、LoRA、RLHF、DPO 的目的和区别。
- 推理参数：上下文窗口、Temperature、Top-P、Max Tokens、KV Cache、量化。
- 技术选型：Prompt、RAG、微调、工具调用分别解决什么问题。

### 4.2 LLM 应用开发

- 消息角色、Prompt 模板、Few-shot、结构化输出、流式响应和多轮会话。
- Function/Tool Calling、参数 Schema、输出校验和异常处理。
- Token、延迟、费用、限流、超时、重试和模型切换。
- Prompt 版本管理、测试和 Prompt Injection 基础防护。

### 4.3 RAG

- 文档解析、清洗、切片、元数据、Embedding 和索引。
- 向量检索、BM25、混合检索、Query Rewrite、Multi-Query 和 Rerank。
- 父子文档、上下文压缩、引用、拒答、增量索引和文档版本。
- 租户/部门/用户权限过滤。
- Recall@K、Precision@K、MRR、NDCG、相关性、忠实度和引用准确率。

### 4.4 Agent 与工作流

- Function Calling → 确定性 Workflow → 单 Agent → 多 Agent 的渐进路线。
- ReAct、状态、短期/长期记忆、任务拆解和计划执行。
- 超时、重试、幂等、补偿、人工审批、最小权限和死循环防护。
- MCP 的 Tool、Resource、Prompt、Client、Server 和安全边界。

### 4.5 企业级能力

- 多模型适配、模型网关、路由、限流、熔断、降级和语义缓存。
- 多租户、RBAC、知识权限、敏感数据脱敏、审计和内容安全。
- Prompt Injection、间接注入、工具越权、数据泄漏和输出校验。
- 离线评测、在线反馈、回归测试、红队测试和灰度发布。
- 指标、日志、Trace、Token、成本、延迟、成功率和告警。

## 5. 每周执行规则

每周建议安排：

- 2 小时：理论和原理。
- 2 小时：官方文档与示例。
- 6 小时：Demo 编码。
- 1 小时：评测和问题复盘。
- 1 小时：README、架构图和技术决策记录。

任务完成规则：

- 只有“读完/看完”不能算完成。
- 每个知识点至少要有代码、实验记录、对比结果或原理说明之一。
- 每周代码必须可运行，命令和环境变量必须记录。
- 每个阶段至少进行一次演示和一次复盘。
- 未达到子任务中的“完成标准”时，不勾选阶段任务。

## 6. 企业级 Demo 范围

Enterprise Copilot 的最终业务闭环：

1. 管理员上传制度、产品文档和技术手册。
2. 系统完成解析、清洗、切片、向量化和混合索引。
3. 用户登录后，只能检索自己有权限访问的知识。
4. 回答必须引用原文；证据不足时拒答。
5. Agent 可以调用只读业务工具，并生成待执行的任务草稿。
6. 写操作必须经过人工确认，执行过程可追踪、可重试、可审计。
7. 后台展示质量、延迟、Token、成本和错误趋势。
8. 模型、Prompt、检索策略更新后，自动执行离线回归评测。

最终验收指标：

- [ ] 建立不少于 50 条人工标注问答数据。
- [ ] 记录并说明 Recall@5、引用准确率和拒答准确率。
- [ ] 不同租户之间无法检索彼此数据。
- [ ] 所有高风险写工具均需人工确认。
- [ ] 模型超时、限流或失败时存在重试、熔断或降级策略。
- [ ] 能查询单次请求 Token、耗时和估算成本。
- [ ] 完成不少于 20 条安全攻击与越权测试。
- [ ] `docker compose up` 可以启动演示环境。
- [ ] README 包含架构、部署、评测、选型、限制和演示步骤。

## 7. 官方资料基线

- Spring Boot 系统要求：https://docs.spring.io/spring-boot/system-requirements.html
- Spring AI 入门：https://docs.spring.io/spring-ai/reference/getting-started.html
- Spring AI API：https://docs.spring.io/spring-ai/reference/api/
- Spring AI Evaluation：https://docs.spring.io/spring-ai/reference/api/testing.html
- Spring AI Observability：https://docs.spring.io/spring-ai/reference/observability/index.html
- MCP 架构：https://modelcontextprotocol.io/docs/learn/architecture
- LangChain4j 文档（用于对比学习）：https://docs.langchain4j.dev/
- Python 发布信息：https://www.python.org/downloads/

