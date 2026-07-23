package com.example.ai.modelapilab.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequest(
        @NotBlank(message = "message 不能为空")
        @Size(max = 4_000, message = "message 不能超过 4000 个字符")
        String message
) {
}
