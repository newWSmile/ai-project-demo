package com.example.ai.modelapilab.python;

import com.example.ai.modelapilab.chat.ChatRequest;
import com.example.ai.modelapilab.chat.ChatResult;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Java 调用 Python FastAPI 的跨语言服务集成入口。 */
@RestController
@RequestMapping("/api/chat/python")
public class PythonChatController {

    private final PythonServiceClient pythonServiceClient;

    public PythonChatController(PythonServiceClient pythonServiceClient) {
        this.pythonServiceClient = pythonServiceClient;
    }

    @PostMapping
    public ChatResult chat(@Valid @RequestBody ChatRequest request) {
        // Controller 仅负责 Java 入参校验，跨服务协议和异常转换由 Client 集中处理。
        return pythonServiceClient.chat(request.message());
    }
}

