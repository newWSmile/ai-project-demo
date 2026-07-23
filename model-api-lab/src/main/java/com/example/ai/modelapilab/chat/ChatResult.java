package com.example.ai.modelapilab.chat;

public record ChatResult(
        String content,
        Integer promptTokens,
        Integer completionTokens,
        Integer totalTokens,
        long durationMs
) {
}

