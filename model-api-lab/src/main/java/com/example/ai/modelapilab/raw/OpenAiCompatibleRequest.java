package com.example.ai.modelapilab.raw;

import java.util.List;

/** OpenAI-compatible 对话请求，只保留当前实验需要的字段。 */
record OpenAiCompatibleRequest(
        String model,
        List<Message> messages,
        double temperature,
        boolean stream
) {
    /** 单条消息由角色和文本内容组成。 */
    record Message(String role, String content) {
    }
}
