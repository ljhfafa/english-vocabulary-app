package com.englishscenario.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * DeepSeek API配置
 */
@Configuration
@ConfigurationProperties(prefix = "deepseek.api")
@Data
public class DeepSeekConfig {
    private String key;
    private String url = "https://api.deepseek.com/v1/chat/completions";
    private String model = "deepseek-chat";
    private Double temperature = 0.7;
    private Integer maxTokens = 2000;
}