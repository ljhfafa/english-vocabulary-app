package com.englishscenario.backend.service;

import com.englishscenario.backend.config.DeepSeekConfig;
import com.englishscenario.backend.dto.DeepSeekDTO.*;
import com.englishscenario.backend.dto.VocabularyResponse;
import com.englishscenario.backend.dto.VocabularyResponse.WordCategory;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * DeepSeek API服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeepSeekService {

    private final DeepSeekConfig deepSeekConfig;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 生成场景词汇
     */
    public VocabularyResponse generateVocabulary(String scenario, String language) {
        log.info("Calling DeepSeek API for scenario: {} in language: {}", scenario, language);

        try {
            // 构建提示词
            String prompt = buildPrompt(scenario, language);

            // 创建请求
            ChatRequest request = ChatRequest.builder()
                    .model(deepSeekConfig.getModel())
                    .messages(List.of(
                            Message.builder()
                                    .role("system")
                                    .content(getSystemPrompt(language))
                                    .build(),
                            Message.builder()
                                    .role("user")
                                    .content(prompt)
                                    .build()
                    ))
                    .temperature(deepSeekConfig.getTemperature())
                    .maxTokens(deepSeekConfig.getMaxTokens())
                    .responseFormat(ResponseFormat.builder()
                            .type("json_object")
                            .build())
                    .build();

            // 调用API
            WebClient webClient = webClientBuilder
                    .baseUrl(deepSeekConfig.getUrl())
                    .defaultHeader("Authorization", "Bearer " + deepSeekConfig.getKey())
                    .defaultHeader("Content-Type", "application/json")
                    .build();

            ChatResponse response = webClient
                    .post()
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ChatResponse.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();

            // 解析响应
            if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                String content = response.getChoices().get(0).getMessage().getContent();
                log.debug("DeepSeek response: {}", content);

                // 解析JSON响应
                VocabularyResponse vocabularyResponse = parseResponse(content, scenario);
                log.info("Successfully generated vocabulary using DeepSeek");
                return vocabularyResponse;
            }

        } catch (Exception e) {
            log.error("Error calling DeepSeek API", e);
        }

        // 如果失败，返回空响应
        return VocabularyResponse.builder()
                .scenario(scenario)
                .words(new ArrayList<>())
                .build();
    }

    /**
     * 获取系统提示词
     */
    private String getSystemPrompt(String language) {
        if ("japanese".equals(language)) {
            return """
                你是一个专业的日语教学助手。你的任务是根据用户提供的场景，生成相关的日语词汇和短语。
                请严格按照以下JSON格式返回，不要包含任何其他内容：
                {
                  "words": [
                    {
                      "type": "名词（めいし）",
                      "items": ["日语词汇 (中文翻译)", ...]
                    },
                    {
                      "type": "动词（どうし）",
                      "items": ["日语词汇 (中文翻译)", ...]
                    },
                    {
                      "type": "形容词（けいようし）",
                      "items": ["日语词汇 (中文翻译)", ...]
                    },
                    {
                      "type": "实用句子",
                      "items": ["日语句子 (中文翻译)", ...]
                    }
                  ]
                }
                
                要求：
                1. 每个类别至少包含5个词汇或短语
                2. 词汇要实用且与场景高度相关
                3. 包含假名标注（如果需要）
                4. 必须返回有效的JSON格式
                """;
        } else {
            return """
                你是一个专业的英语教学助手。你的任务是根据用户提供的场景，生成相关的英语词汇和短语。
                请严格按照以下JSON格式返回，不要包含任何其他内容：
                {
                  "words": [
                    {
                      "type": "名词",
                      "items": ["english word (中文翻译)", ...]
                    },
                    {
                      "type": "动词",
                      "items": ["english word (中文翻译)", ...]
                    },
                    {
                      "type": "形容词",
                      "items": ["english word (中文翻译)", ...]
                    },
                    {
                      "type": "常用句",
                      "items": ["English sentence. (中文翻译)", ...]
                    }
                  ]
                }
                
                要求：
                1. 每个类别至少包含5个词汇或短语
                2. 词汇要实用且与场景高度相关
                3. 短语和句子要地道自然
                4. 必须返回有效的JSON格式
                """;
        }
    }

    /**
     * 构建用户提示词
     */
    private String buildPrompt(String scenario, String language) {
        if ("japanese".equals(language)) {
            return String.format("请为以下场景生成日语词汇和短语：%s", scenario);
        } else {
            return String.format("请为以下场景生成英语词汇和短语：%s", scenario);
        }
    }

    /**
     * 解析响应
     */
    private VocabularyResponse parseResponse(String content, String scenario) {
        try {
            // 尝试解析JSON
            AIVocabularyResponse aiResponse = objectMapper.readValue(content, AIVocabularyResponse.class);

            return VocabularyResponse.builder()
                    .scenario(scenario)
                    .words(aiResponse.getWords())
                    .build();

        } catch (Exception e) {
            log.error("Error parsing DeepSeek response", e);

            // 返回空结果
            return VocabularyResponse.builder()
                    .scenario(scenario)
                    .words(new ArrayList<>())
                    .build();
        }
    }

    /**
     * AI响应格式
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class AIVocabularyResponse {
        private List<WordCategory> words;
    }
}