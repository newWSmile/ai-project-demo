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
        long startedAt = System.nanoTime();
        ChatResponse response = chatClient.prompt()
                .user(request.message())
                .call()
                .chatResponse();

        if (response == null || response.getResult() == null) {
            throw new IllegalStateException("The model returned an empty response");
        }

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
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(UTF_8_EVENT_STREAM);

        return chatClient.prompt()
                .user(request.message())
                .stream()
                .content();
    }
}
