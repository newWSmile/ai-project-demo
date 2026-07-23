package com.example.ai.modelapilab.chat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChatRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void rejectsBlankMessage() {
        assertThat(validator.validate(new ChatRequest("   "))).isNotEmpty();
    }

    @Test
    void acceptsNormalMessage() {
        assertThat(validator.validate(new ChatRequest("Explain what a token is."))).isEmpty();
    }
}

