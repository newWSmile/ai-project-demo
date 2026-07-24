package com.example.ai.modelapilab.python;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.time.Duration;

/**
 * Python FastAPI 服务连接配置。
 * 使用类型安全配置集中管理地址和超时，避免客户端代码散落字符串常量。
 */
@Validated
@ConfigurationProperties("python-service")
public record PythonServiceProperties(
        @NotNull URI baseUrl,
        @NotNull Duration connectTimeout,
        @NotNull Duration readTimeout
) {
}

