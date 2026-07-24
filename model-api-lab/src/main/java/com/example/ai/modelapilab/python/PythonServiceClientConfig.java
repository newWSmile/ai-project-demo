package com.example.ai.modelapilab.python;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/** 为 Python 服务创建专用 RestClient，防止其超时设置影响其他上游客户端。 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(PythonServiceProperties.class)
public class PythonServiceClientConfig {

    /**
     * 使用 JDK HttpClient 请求工厂创建同步客户端。
     * AI 生成时间通常长于普通 REST 请求，因此连接超时和读取超时必须分开配置。
     */
    @Bean("pythonServiceRestClient")
    RestClient pythonServiceRestClient(
            RestClient.Builder builder,
            PythonServiceProperties properties
    ) {
        HttpClientSettings settings = HttpClientSettings.defaults()
                .withConnectTimeout(properties.connectTimeout())
                .withReadTimeout(properties.readTimeout());
        ClientHttpRequestFactory requestFactory = ClientHttpRequestFactoryBuilder.jdk()
                .build(settings);

        return builder
                .baseUrl(properties.baseUrl().toString())
                .requestFactory(requestFactory)
                .build();
    }
}

