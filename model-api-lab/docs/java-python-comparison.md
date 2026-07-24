# Java 与 Python 模型 API 调用对比

## 一、实验目的

使用相同模型、系统提示词、用户提示词和 temperature，对比 Java 与 Python 调用 DashScope OpenAI-compatible API 时的请求语义、Token Usage 和工程职责。

本实验主要验证跨语言协议一致性，不是严谨的语言性能基准测试。

## 二、控制变量

- 模型：`qwen-plus`
- temperature：`0.2`
- 调用方式：非流式
- 系统提示词：`你是一名回答简洁、准确的 AI 助手。如果无法确定答案，请明确说明不确定，不要编造信息。`
- 用户提示词：`请用三句话解释什么是大语言模型`
- DashScope 接口：`/compatible-mode/v1/chat/completions`

## 三、实验数据

| 实现 | 输入 Token | 输出 Token | 总 Token | 耗时 |
| --- | ---: | ---: | ---: | ---: |
| Java Spring AI，3 次平均 | 48 | 79.67 | 127.67 | 2048ms |
| Java 原生 HttpClient，3 次平均 | 48 | 81.33 | 129.33 | 2077.67ms |
| Python httpx，严格控制提示词 | 48 | 86 | 134 | 2787ms |

Python 当前只有 1 次严格控制变量样本，不能根据 `2787ms` 判断 Python 比 Java 慢。模型生成长度、网络状态、服务端排队和连接复用都会影响端到端耗时。

## 四、一次文本差异带来的 Token 变化

Python 第一次请求的用户提示词末尾多了中文句号：

```text
请用三句话解释什么是大语言模型。
```

其输入 Token 为 `49`。去掉句号，与 Java 提示词逐字一致后：

```text
请用三句话解释什么是大语言模型
```

输入 Token 变为 `48`。

这说明 Token 预算必须针对最终实际发送的文本计算，不能认为标点、空格或换行一定不占 Token。

## 五、协议层共同点

Java 和 Python 最终完成的是同一件事：

```text
读取环境变量中的 API Key
→ 构造 Authorization Header
→ 组装 model、messages、temperature、stream
→ POST /chat/completions
→ 检查 HTTP 状态码
→ 解析 choices 和 usage
→ 记录模型回答、Token 和耗时
```

输入 Token 都是 `48`，说明两种语言发送给模型的提示词语义在 Token 层面一致。模型能力由模型和输入决定，不由调用语言决定。

## 六、工程实现差异

| 维度 | Java | Python |
| --- | --- | --- |
| 请求与响应 | Java Record，字段类型明确 | 字典访问更直接，但字段错误通常运行时发现 |
| 代码量 | 原生调用较多，Spring AI 可大幅减少 | httpx 调用非常精简 |
| 编译检查 | 编译期发现较多类型问题 | 运行时更加灵活 |
| 企业系统集成 | 适合现有 Spring、数据库、消息队列和治理体系 | 适合 AI/数据生态和快速实验 |
| 模型抽象 | Spring AI 提供统一抽象 | 可选用官方 SDK 或 Python AI 框架 |
| 部署方式 | 作为企业主服务或微服务 | 作为独立辅助服务、任务或模型处理服务 |

## 七、当前项目为什么 Java 为主、Python 为辅

当前学习者已有多年 Java 企业开发经验，因此主业务继续使用 Java 可以复用：

- Spring Boot 和微服务经验；
- 权限、事务、数据库和消息队列能力；
- 日志、监控、限流、熔断和发布体系；
- 企业代码规范与团队协作经验。

Python 不需要替代 Java。它更适合承担 Java 不占优势的任务，例如：

- 使用 Python 优先发布的 AI、数据处理和模型工具；
- 文档解析、数据清洗、离线评测和实验脚本；
- 封装必须依赖 Python 生态的模型或推理能力；
- 作为独立 FastAPI 服务，通过 HTTP、消息队列或任务系统与 Java 集成。

推荐边界：

```text
Java 主服务
    负责业务流程、权限、事务、模型编排和企业治理
            ↓ HTTP / 消息队列
Python 辅助服务
    负责 Python 生态特有的 AI、数据和模型处理能力
```

## 八、结论

1. Java 和 Python 可以通过同一 OpenAI-compatible 协议调用相同模型。
2. 相同提示词产生相同输入 Token，调用语言不会改变模型能力。
3. 当前少量样本不能用于判断 Java 与 Python 的性能优劣。
4. Spring AI 适合 Java 企业应用主调用链，Python httpx 适合快速实验和辅助服务。
5. 技术选型应依据现有系统、团队能力、生态依赖和运维要求，而不是因为“AI 必须使用 Python”。
