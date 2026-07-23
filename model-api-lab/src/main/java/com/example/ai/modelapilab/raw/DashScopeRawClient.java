package com.example.ai.modelapilab.raw;

import com.example.ai.modelapilab.chat.ChatResult;
import com.example.ai.modelapilab.config.PromptConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

@Component
public class DashScopeRawClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;
    private final double temperature;
    private final URI chatCompletionsUri;

    public DashScopeRawClient(
            HttpClient dashScopeHttpClient,
            ObjectMapper objectMapper,
            @Value("${spring.ai.openai.api-key}") String apiKey,
            @Value("${spring.ai.openai.base-url}") String baseUrl,
            @Value("${spring.ai.openai.chat.model}") String model,
            @Value("${spring.ai.openai.chat.temperature}") double temperature
    ) {
        this.httpClient = dashScopeHttpClient;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.model = model;
        this.temperature = temperature;
        this.chatCompletionsUri = resolveChatCompletionsUri(baseUrl);
    }

    public ChatResult chat(String userMessage) {
        // 第 1 步：在发送请求前检查密钥，避免携带占位值请求外部服务。
        validateApiKey();
        long startedAt = System.nanoTime();

        // 第 2 步：按照 OpenAI-compatible 协议组装模型、消息、温度和流式开关。
        OpenAiCompatibleRequest requestBody = new OpenAiCompatibleRequest(
                model,
                List.of(
                        new OpenAiCompatibleRequest.Message("system", PromptConstants.DEFAULT_SYSTEM_PROMPT),
                        new OpenAiCompatibleRequest.Message("user", userMessage)
                ),
                temperature,
                false
        );

        // 第 3 步：构建标准 HTTP 请求。API Key 只进入 Authorization Header，禁止写入日志。
        HttpRequest request = HttpRequest.newBuilder(chatCompletionsUri)
                .timeout(Duration.ofSeconds(60))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json; charset=UTF-8")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(writeJson(requestBody), StandardCharsets.UTF_8))
                .build();

        // 第 4 步：同步发送请求，并在读取响应体时明确使用 UTF-8。
        HttpResponse<String> response = send(request);

        // 第 5 步：先检查 HTTP 状态码。非 2xx 响应不能按正常模型结果解析。
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new ModelProviderException(
                    "DashScope 返回了非成功状态码",
                    response.statusCode()
            );
        }

        // 第 6 步：将 JSON 映射为 Java Record，并校验 choices 中是否存在助手消息。
        OpenAiCompatibleResponse modelResponse = readResponse(response.body());
        if (modelResponse.choices() == null
                || modelResponse.choices().isEmpty()
                || modelResponse.choices().getFirst().message() == null) {
            throw new ModelProviderException("DashScope 响应中没有助手消息");
        }

        // 第 7 步：统一转换为项目自己的返回类型，避免 Controller 依赖供应商响应结构。
        OpenAiCompatibleResponse.Usage usage = modelResponse.usage();
        long durationMs = (System.nanoTime() - startedAt) / 1_000_000;

        return new ChatResult(
                modelResponse.choices().getFirst().message().content(),
                usage == null ? null : usage.promptTokens(),
                usage == null ? null : usage.completionTokens(),
                usage == null ? null : usage.totalTokens(),
                durationMs
        );
    }

    private HttpResponse<String> send(HttpRequest request) {
        try {
            return httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );
        }
        catch (InterruptedException exception) {
            // 捕获 InterruptedException 后必须恢复中断标记，让上层线程池能够感知取消信号。
            Thread.currentThread().interrupt();
            throw new ModelProviderException("DashScope 请求被中断", exception);
        }
        catch (IOException exception) {
            throw new ModelProviderException("DashScope 网络请求失败", exception);
        }
    }

    /** 将 Java Record 序列化为 OpenAI-compatible 请求 JSON。 */
    private String writeJson(OpenAiCompatibleRequest request) {
        try {
            return objectMapper.writeValueAsString(request);
        }
        catch (Exception exception) {
            throw new ModelProviderException("DashScope 请求序列化失败", exception);
        }
    }

    /** 将 DashScope 返回的 JSON 反序列化为 Java Record。 */
    private OpenAiCompatibleResponse readResponse(String responseBody) {
        try {
            return objectMapper.readValue(responseBody, OpenAiCompatibleResponse.class);
        }
        catch (Exception exception) {
            throw new ModelProviderException("DashScope 响应反序列化失败", exception);
        }
    }

    /** 校验 API Key 是否已经通过环境变量或 IDEA 启动配置注入。 */
    private void validateApiKey() {
        if (apiKey == null || apiKey.isBlank() || "change-me".equals(apiKey)) {
            throw new ModelProviderException("尚未配置 DASHSCOPE_API_KEY");
        }
    }

    /**
     * 将 Spring AI 使用的 Base URL 转换成原生 HTTP 调用所需的完整地址。
     * 同时兼容用户直接传入完整 /chat/completions 地址的情况。
     */
    private static URI resolveChatCompletionsUri(String baseUrl) {
        String normalized = baseUrl.endsWith("/")
                ? baseUrl.substring(0, baseUrl.length() - 1)
                : baseUrl;
        String endpoint = normalized.endsWith("/chat/completions")
                ? normalized
                : normalized + "/chat/completions";
        return URI.create(endpoint);
    }
}
