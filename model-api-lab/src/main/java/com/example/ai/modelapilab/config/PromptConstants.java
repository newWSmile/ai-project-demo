package com.example.ai.modelapilab.config;

/**
 * 集中管理实验项目使用的提示词，保证不同调用方式采用相同的测试条件。
 */
public final class PromptConstants {

    /** 默认系统提示词：约束模型的回答风格，并要求明确表达不确定性。 */
    public static final String DEFAULT_SYSTEM_PROMPT =
            "你是一名回答简洁、准确的 AI 助手。如果无法确定答案，请明确说明不确定，不要编造信息。";

    private PromptConstants() {
    }
}
