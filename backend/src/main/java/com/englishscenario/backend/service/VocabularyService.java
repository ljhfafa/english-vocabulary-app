package com.englishscenario.backend.service;

import com.englishscenario.backend.dto.VocabularyRequest;
import com.englishscenario.backend.dto.VocabularyResponse;
import com.englishscenario.backend.dto.VocabularyResponse.WordCategory;
import com.englishscenario.backend.entity.User;
import com.englishscenario.backend.entity.VocabularyRecord;
import com.englishscenario.backend.repository.UserRepository;
import com.englishscenario.backend.repository.VocabularyRecordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 词汇生成服务
 * 处理词汇生成的业务逻辑
 */
@Service  // 标记为服务类
@RequiredArgsConstructor
@Slf4j  // Lombok的日志注解
public class VocabularyService {

    private final VocabularyRecordRepository vocabularyRecordRepository;
    private final UserRepository userRepository;
    private final DeepSeekService deepSeekService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 生成场景相关的词汇
     */
    @Transactional
    public VocabularyResponse generateVocabulary(String scenario, String language, String username) {
        log.info("Generating vocabulary for scenario: {} in language: {} for user: {}",
                scenario, language, username);

        VocabularyResponse response;

        try {
            // 使用DeepSeek API生成词汇
            response = deepSeekService.generateVocabulary(scenario, language);

            // 如果AI生成失败或返回空结果，使用备用方案
            if (response.getWords() == null || response.getWords().isEmpty()) {
                log.warn("DeepSeek returned empty result, using fallback");
                response = getBasicVocabulary(scenario, language);
            }

        } catch (Exception e) {
            log.error("Error generating vocabulary with DeepSeek, using fallback", e);
            // 使用备用方案
            response = getBasicVocabulary(scenario, language);
        }

        // 保存历史记录
        if (username != null && response.getWords() != null && !response.getWords().isEmpty()) {
            saveVocabularyRecord(scenario, language, response, username);
        }

        log.debug("Generated vocabulary response: {}", response);
        return response;
    }

    /**
     * 保存词汇生成记录
     */
    private void saveVocabularyRecord(String scenario, String language,
                                      VocabularyResponse response, String username) {
        try {
            // 查找用户
            User user = userRepository.findByUsername(username).orElse(null);
            if (user == null) {
                log.warn("User not found: {}", username);
                return;
            }

            // 创建记录
            VocabularyRecord record = new VocabularyRecord();
            record.setScenario(scenario);
            record.setLanguage(language);
            record.setUser(user);

            // 将词汇转换为JSON字符串
            String vocabularyJson = objectMapper.writeValueAsString(response.getWords());
            record.setVocabularyJson(vocabularyJson);

            // 保存
            vocabularyRecordRepository.save(record);
            log.info("Vocabulary record saved for user: {}", username);

        } catch (Exception e) {
            log.error("Error saving vocabulary record", e);
        }
    }

    /**
     * 基础词汇生成（备用方案）
     * 当AI服务不可用时使用
     */
    private VocabularyResponse getBasicVocabulary(String scenario, String language) {
        List<WordCategory> words = new ArrayList<>();

        if ("japanese".equals(language)) {
            words.add(WordCategory.builder()
                    .type("基本語彙")
                    .items(Arrays.asList(
                            "場面（ばめん）(场景)",
                            "状況（じょうきょう）(情况)",
                            "練習（れんしゅう）(练习)"
                    ))
                    .build());
            words.add(WordCategory.builder()
                    .type("お知らせ")
                    .items(Arrays.asList(
                            "AIサービスが一時的に利用できません。(AI服务暂时不可用。)",
                            "基本的な語彙を表示しています。(显示基本词汇。)"
                    ))
                    .build());
        } else {
            words.add(WordCategory.builder()
                    .type("Basic Vocabulary")
                    .items(Arrays.asList(
                            "scenario (场景)",
                            "vocabulary (词汇)",
                            "practice (练习)"
                    ))
                    .build());
            words.add(WordCategory.builder()
                    .type("Notice")
                    .items(Arrays.asList(
                            "AI service is temporarily unavailable.",
                            "Showing basic vocabulary."
                    ))
                    .build());
        }

        return VocabularyResponse.builder()
                .scenario(scenario)
                .words(words)
                .build();
    }
}