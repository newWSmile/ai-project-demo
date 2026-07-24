package com.example.ai.modelapilab.python;

import com.example.ai.modelapilab.chat.ChatResult;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.http.HttpMethod.POST;

class PythonServiceClientTest {

    @Test
    void shouldMapPythonChatResponse() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://localhost:8000");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        PythonServiceClient client = new PythonServiceClient(builder.build());

        server.expect(once(), requestTo("http://localhost:8000/api/chat"))
                .andExpect(method(POST))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {"message":"解释什么是跨语言服务调用"}
                        """))
                .andRespond(withSuccess("""
                        {
                          "content": "测试回答",
                          "promptTokens": 12,
                          "completionTokens": 8,
                          "totalTokens": 20,
                          "durationMs": 35
                        }
                        """, MediaType.APPLICATION_JSON));

        ChatResult result = client.chat("解释什么是跨语言服务调用");

        assertThat(result.content()).isEqualTo("测试回答");
        assertThat(result.promptTokens()).isEqualTo(12);
        assertThat(result.completionTokens()).isEqualTo(8);
        assertThat(result.totalTokens()).isEqualTo(20);
        assertThat(result.durationMs()).isEqualTo(35);
        server.verify();
    }

    @Test
    void shouldConvertPythonErrorWithoutLeakingResponseBody() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://localhost:8000");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        PythonServiceClient client = new PythonServiceClient(builder.build());

        server.expect(once(), requestTo("http://localhost:8000/api/chat"))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"secretInternalDetail\":\"禁止泄露\"}"));

        assertThatThrownBy(() -> client.chat("触发模拟异常"))
                .isInstanceOfSatisfying(PythonServiceException.class, exception -> {
                    assertThat(exception.upstreamStatus()).isEqualTo(429);
                    assertThat(exception.getMessage()).doesNotContain("禁止泄露");
                });
        server.verify();
    }
}

