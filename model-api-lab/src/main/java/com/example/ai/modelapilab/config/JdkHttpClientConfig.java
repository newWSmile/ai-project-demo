package com.example.ai.modelapilab.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class JdkHttpClientConfig {

    /**
     * 创建可复用的 JDK HttpClient。
     * 连接超时只限制建立连接的时间，单次模型请求的总超时在构建 HttpRequest 时设置。
     */
    @Bean
    HttpClient dashScopeHttpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .version(HttpClient.Version.HTTP_2)
                .build();
    }
}
