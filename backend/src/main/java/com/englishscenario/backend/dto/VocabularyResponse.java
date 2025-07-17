package com.englishscenario.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 词汇生成响应DTO
 * 返回给前端的数据格式
 */
@Data
@Builder  // 构建者模式
@NoArgsConstructor
@AllArgsConstructor
public class VocabularyResponse {
    private String scenario;  // 场景
    private List<WordCategory> words;  // 词汇分类列表

    /**
     * 词汇分类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WordCategory {
        private String type;  // 类型（名词、动词、短语等）
        private List<String> items;  // 词汇列表
    }
}