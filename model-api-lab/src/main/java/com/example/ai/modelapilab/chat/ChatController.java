package com.example.ai.modelapilab.chat;

import jakarta.validation.Valid;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

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

    @PostMapping(value = "/stream", produces = "text/event-stream;charset=UTF-8")
    public Flux<String> stream(@Valid @RequestBody ChatRequest request) {
        return chatClient.prompt()
                .user(request.message())
                .stream()
                .content();
    }
}
