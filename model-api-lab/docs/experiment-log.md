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
