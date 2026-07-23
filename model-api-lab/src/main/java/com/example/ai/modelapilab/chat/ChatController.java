package com.example.ai.modelapilab.chat;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final String UTF_8_EVENT_STREAM = "text/event-stream;charset=UTF-8";

    private final ChatClient chatClient;

    public ChatController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @PostMapping
    public ChatResult chat(@Valid @RequestBody ChatRequest request) {
        // 记录完整调用耗时，包括网络传输、模型生成和响应解析。
        long startedAt = System.nanoTime();

        // call() 会等待模型生成完毕，再一次性返回完整响应。
        ChatResponse response = chatClient.prompt()
                .user(request.message())
                .call()
                .chatResponse();

        if (response == null || response.getResult() == null) {
            throw new IllegalStateException("模型返回了空响应");
        }

        // Usage 是模型提供商返回的 Token 使用量，不应根据字符串长度自行估算。
        Usage usage = response.getMetadata().getUsage();
        long durationMs = (System.nanoTime() - startedAt) / 1_000_000;

        return new ChatResult(
                response.getResult().getOutput().getText(),
                usage.getPromptTokens(),
                usage.getCompletionTokens(),
                usage.getTotalTokens(),
                durationMs
        );
    }

    @PostMapping(value = "/stream", produces = UTF_8_EVENT_STREAM)
    public Flux<String> stream(
            @Valid @RequestBody ChatRequest request,
            HttpServletResponse response
    ) {
        // IDEA HTTP Client 依赖响应头判断字符集，因此在 Servlet 响应层明确声明 UTF-8。
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(UTF_8_EVENT_STREAM);

        // stream() 返回 Flux，模型每生成一段内容就通过 SSE 向客户端推送一段。
        return chatClient.prompt()
                .user(request.message())
                .stream()
                .content();
    }
}
