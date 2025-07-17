package com.englishscenario.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 历史记录响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryResponse {
    private Long id;
    private String scenario;
    private String language;
    private LocalDateTime createdAt;
    private List<WordCategory> vocabulary;

    /**
     * 历史记录列表响应
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryListResponse {
        private List<HistoryResponse> records;
        private int currentPage;
        private int totalPages;
        private long totalElements;
        private boolean hasNext;
        private boolean hasPrevious;
    }

    /**
     * 词汇分类（复用VocabularyResponse中的定义）
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WordCategory {
        private String type;
        private List<String> items;
    }
}