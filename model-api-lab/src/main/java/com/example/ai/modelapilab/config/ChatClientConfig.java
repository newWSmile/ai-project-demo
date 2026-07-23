package com.example.ai.modelapilab.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    /**
     * 创建 Spring AI ChatClient，并为所有 Spring AI 调用设置统一的中文系统提示词。
     */
    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem(PromptConstants.DEFAULT_SYSTEM_PROMPT)
                .build();
    }
}
