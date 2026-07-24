# Experiment Log

每次实验复制下面的模板，禁止只记录“效果不错”。

## Experiment YYYY-MM-DD-NN

- Hypothesis:
- Provider and model:
- Prompt version:
- Parameters:
- Input:
- Output:
- Prompt tokens:
- Completion tokens:
- First-token latency:
- Total latency:
- HTTP status:
- Cost estimate:
- Observation:
- Conclusion:

## Experiment 2026-07-23-01

- Hypothesis: Spring AI's OpenAI-compatible adapter can call DashScope successfully.
- Provider and model: Alibaba Cloud DashScope / qwen-plus
- Prompt version: v1
- Parameters: Project defaults
- Input: 请用三句话解释什么是大语言模型
- Output: Returned a three-sentence Chinese explanation covering training data, Transformer-based tasks, and probabilistic generation.
- Prompt tokens: 40
- Completion tokens: 87
- Total tokens: 127
- First-token latency: Not measured (non-streaming request)
- Total latency: 1767 ms
- HTTP status: 200
- Cost estimate: Pending pricing calculation
- Observation: Usage metadata was returned correctly; prompt tokens plus completion tokens equals total tokens (40 + 87 = 127).
- Conclusion: Java 21 -> Spring AI 2 -> DashScope OpenAI-compatible API -> qwen-plus call succeeded.

## Experiment 2026-07-23-02

- Hypothesis: The Java service can expose DashScope output as an UTF-8 SSE stream.
- Provider and model: Alibaba Cloud DashScope / qwen-plus
- Prompt version: v1
- Parameters: Project defaults
- Input: 请分五点介绍Java开发者学习AI应用开发的优势
- Output: Chinese content was returned incrementally as SSE `data:` chunks.
- Prompt tokens: Not exposed by the current streaming endpoint
- Completion tokens: Not exposed by the current streaming endpoint
- Total tokens: Not exposed by the current streaming endpoint
- First-token latency: About 1438 ms (client-observed response start)
- Total latency: Not measured
- HTTP status: 200
- Content-Type: text/event-stream;charset=UTF-8
- Cost estimate: Pending pricing calculation
- Observation: Postman and IntelliJ IDEA both displayed Chinese correctly after the response explicitly declared UTF-8.
- Conclusion: The synchronous chat and SSE streaming paths are both operational.

## Experiment 2026-07-23-03

- Hypothesis: Java 21 JDK HttpClient can call the DashScope OpenAI-compatible API without Spring AI.
- Provider and model: Alibaba Cloud DashScope / qwen-plus
- Prompt version: raw-http-v1
- Parameters: temperature 0.2, non-streaming
- Input: 请用三句话解释 Java 原生 HttpClient 调用大模型需要处理哪些事情
- Output: Returned three points covering HTTP request construction, authentication, response parsing, and error handling.
- Prompt tokens: 60
- Completion tokens: 176
- Total tokens: 236
- First-token latency: Not measured (non-streaming request)
- Total latency: 4430 ms
- HTTP status: 200
- Content-Type: application/json
- Cost estimate: Pending pricing calculation
- Observation: JDK HttpClient completed request serialization, Bearer authentication, UTF-8 transport, status validation, and Jackson response mapping successfully.
- Conclusion: The direct Java 21 HttpClient -> DashScope call path is operational without Spring AI.

## Experiment 2026-07-23-04

- Hypothesis: 在相同输入条件下，Spring AI 与原生 HttpClient 的模型输入和端到端耗时应处于同一量级。
- Provider and model: Alibaba Cloud DashScope / qwen-plus
- Prompt version: controlled-comparison-v1
- Parameters: temperature 0.2, non-streaming, each implementation repeated 3 times
- Input: 请用三句话解释什么是大语言模型
- Spring AI prompt tokens: 48, 48, 48
- Spring AI completion tokens: 80, 79, 80
- Spring AI total latency: 2666ms, 1800ms, 1678ms
- Raw HttpClient prompt tokens: 48, 48, 48
- Raw HttpClient completion tokens: 79, 86, 79
- Raw HttpClient total latency: 1952ms, 2303ms, 1978ms
- HTTP status: All requests returned 200
- Observation: 两种实现的输入 Token 完全一致；平均耗时分别为 2048ms 和 2077.67ms，仅相差约 29.67ms。
- Conclusion: 当前小样本实验没有显示 Spring AI 存在明显性能损失；主耗时来自模型生成和网络，框架选择应优先考虑工程能力与可维护性。

## Experiment 2026-07-24-01

- Hypothesis: Python 3.13 + httpx can call the same DashScope OpenAI-compatible API used by Java.
- Provider and model: Alibaba Cloud DashScope / qwen-plus
- Prompt version: python-http-v1
- Parameters: temperature 0.2, non-streaming
- Input: 请用三句话解释什么是大语言模型。
- Prompt tokens: 49
- Completion tokens: 84
- Total tokens: 133
- First-token latency: Not measured (non-streaming request)
- Total latency: 2338ms
- HTTP status: 200
- Observation: Python virtual environment, setuptools packaging, httpx request, UTF-8 Chinese output, and Usage parsing all worked correctly. The Python prompt had one trailing Chinese full stop that was absent from the Java comparison prompt.
- Conclusion: Python can complete the direct model call; a second run with byte-for-byte identical prompt text is required for a strict Java/Python comparison.

## Experiment 2026-07-24-02

- Hypothesis: Removing the trailing Chinese full stop will make the Python prompt Token count match the Java controlled experiment.
- Provider and model: Alibaba Cloud DashScope / qwen-plus
- Prompt version: java-python-controlled-v1
- Parameters: temperature 0.2, non-streaming
- Input: 请用三句话解释什么是大语言模型
- Prompt tokens: 48
- Completion tokens: 86
- Total tokens: 134
- First-token latency: Not measured (non-streaming request)
- Total latency: 2787ms
- HTTP status: 200
- Observation: Removing one trailing Chinese full stop reduced prompt tokens from 49 to 48, exactly matching both Java implementations.
- Conclusion: Java and Python delivered Token-equivalent input when model parameters and prompt text were controlled byte for byte.

## Experiment 2026-07-24-03

- Hypothesis: 使用 `src-layout` 分层的 FastAPI 服务可以正确打包、启动，并在不调用真实模型的情况下验证参数校验和异常映射。
- Provider and model: 本实验未调用模型供应商
- Prompt version: 不适用
- Parameters: Python 3.13.14, FastAPI 0.139.2, Uvicorn 0.51.0
- Input: `GET /health`；自动化测试覆盖正常聊天、空消息和模拟上游 429
- Output: 健康检查返回 `{"status":"UP","service":"model-api-lab-python"}`；pytest 4/4 通过
- Prompt tokens: 0
- Completion tokens: 0
- First-token latency: 不适用
- Total latency: 自动化测试 0.29 秒
- HTTP status: 健康检查 200；模拟上游异常映射为 502
- Cost estimate: 0（测试使用假模型客户端）
- Observation: `api / schema / service / client / core / domain` 分层可以正确构建为 wheel；真实 Uvicorn 进程可启动并正常执行生命周期关闭。
- Conclusion: Python 最小辅助服务已具备可运行的项目结构、请求校验、健康检查和安全的上游异常映射，可以进入 Java 调用 FastAPI 的下一项实验。
