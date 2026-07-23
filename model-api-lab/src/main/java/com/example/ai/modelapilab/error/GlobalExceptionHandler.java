package com.example.ai.modelapilab.error;

import com.example.ai.modelapilab.raw.ModelProviderException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 将模型供应商或网络错误统一转换为 502，避免向调用方泄露上游响应正文。 */
    @ExceptionHandler(ModelProviderException.class)
    ResponseEntity<ApiError> handleModelProvider(ModelProviderException exception) {
        Map<String, String> details = exception.providerStatus() == null
                ? Map.of("type", exception.getClass().getSimpleName())
                : Map.of(
                        "type", exception.getClass().getSimpleName(),
                        "providerStatus", exception.providerStatus().toString()
                );

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ApiError(
                Instant.now(),
                HttpStatus.BAD_GATEWAY.value(),
                "上游模型请求失败",
                details
        ));
    }

    /** 汇总请求字段校验错误并返回 400。 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception) {
        Map<String, String> details = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> details.putIfAbsent(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(new ApiError(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "请求参数校验失败",
                details
        ));
    }

    /** 兜底处理未预期异常，响应中只暴露异常类型，不返回堆栈和敏感信息。 */
    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> handleUnexpected(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiError(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "模型请求处理失败",
                Map.of("type", exception.getClass().getSimpleName())
        ));
    }
}
