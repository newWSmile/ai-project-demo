package com.example.ai.modelapilab.python;

import com.example.ai.modelapilab.chat.ChatRequest;
import com.example.ai.modelapilab.chat.ChatResult;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

/**
 * Python FastAPI 服务适配器。
 * Java 业务层只依赖自己的 ChatRequest/ChatResult，不直接感知 Python 内部结构。
 */
@Component
public class PythonServiceClient {

    private final RestClient restClient;

    public PythonServiceClient(
            @Qualifier("pythonServiceRestClient") RestClient restClient
    ) {
        this.restClient = restClient;
    }

    /** 调用 Python 普通聊天接口，并将其 JSON 响应映射为 Java 统一 ChatResult。 */
    public ChatResult chat(String message) {
        try {
            // 第 1 步：沿用 Java 对外请求对象，确保两端 message 校验契约保持一致。
            ChatResult result = restClient.post()
                    .uri("/api/chat")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(new ChatRequest(message))
                    .retrieve()
                    .body(ChatResult.class);

            // 第 2 步：HTTP 200 但没有响应体同样属于上游协议异常，不能返回空结果。
            if (result == null) {
                throw new PythonServiceException("Python 服务返回了空响应", null, null);
            }
            return result;
        } catch (RestClientResponseException exception) {
            // 不透传 Python 响应正文，避免上游堆栈、提示词或内部信息泄露给调用方。
            throw new PythonServiceException(
                    "Python 服务返回了非成功状态码",
                    exception.getStatusCode().value(),
                    exception
            );
        } catch (RestClientException exception) {
            throw new PythonServiceException("Python 服务网络请求失败", null, exception);
        }
    }
}

