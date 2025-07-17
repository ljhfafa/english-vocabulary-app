package com.englishscenario.backend.dto;

import lombok.Data;

/**
 * 词汇生成请求DTO
 * 前端发送的请求格式
 */
@Data
public class VocabularyRequest {
    private String scenario;  // 场景描述
    private String language = "english";  // 语言，默认英语

    /**
     * 验证请求是否有效
     */
    public boolean isValid() {
        return scenario != null && !scenario.trim().isEmpty();
    }

    /**
     * 是否为日语请求
     */
    public boolean isJapanese() {
        return "japanese".equalsIgnoreCase(language);
    }
}