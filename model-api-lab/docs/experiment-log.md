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
