package com.example.ai.modelapilab.raw;

import com.example.ai.modelapilab.chat.ChatRequest;
import com.example.ai.modelapilab.chat.ChatResult;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat/raw")
public class RawChatController {

    private final DashScopeRawClient rawClient;

    public RawChatController(DashScopeRawClient rawClient) {
        this.rawClient = rawClient;
    }

    @PostMapping
    public ChatResult chat(@Valid @RequestBody ChatRequest request) {
        // Controller 只负责参数校验和协议转换，外部模型调用集中在 RawClient 中。
        return rawClient.chat(request.message());
    }
}
