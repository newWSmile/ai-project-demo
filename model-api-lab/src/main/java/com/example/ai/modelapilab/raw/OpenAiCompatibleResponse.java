package com.example.ai.modelapilab.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * OpenAI-compatible 对话响应。
 * ignoreUnknown 保证供应商增加新字段时，当前客户端仍能完成基础解析。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
record OpenAiCompatibleResponse(
        List<Choice> choices,
        Usage usage
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    /** 一项模型候选结果，基础对话通常读取 choices[0]。 */
    record Choice(
            int index,
            Message message,
            @JsonProperty("finish_reason") String finishReason
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    /** 模型生成的消息。 */
    record Message(String role, String content) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    /** Token 用量字段在上游 JSON 中采用 snake_case，因此需要显式映射。 */
    record Usage(
            @JsonProperty("prompt_tokens") Integer promptTokens,
            @JsonProperty("completion_tokens") Integer completionTokens,
            @JsonProperty("total_tokens") Integer totalTokens
    ) {
    }
}
