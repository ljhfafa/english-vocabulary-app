package com.englishscenario.backend.service;

import com.englishscenario.backend.dto.HistoryResponse;
import com.englishscenario.backend.dto.HistoryResponse.HistoryListResponse;
import com.englishscenario.backend.dto.HistoryResponse.WordCategory;
import com.englishscenario.backend.entity.User;
import com.englishscenario.backend.entity.VocabularyRecord;
import com.englishscenario.backend.repository.UserRepository;
import com.englishscenario.backend.repository.VocabularyRecordRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 历史记录服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HistoryService {

    private final VocabularyRecordRepository vocabularyRecordRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取用户的历史记录
     */
    public HistoryListResponse getUserHistory(String username, int page, int size, String language) {
        log.info("Getting history for user: {}, page: {}, size: {}, language: {}",
                username, page, size, language);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 创建分页请求
        Pageable pageable = PageRequest.of(page, size);

        // 查询历史记录
        Page<VocabularyRecord> recordPage;
        if (language != null && !language.isEmpty()) {
            recordPage = vocabularyRecordRepository
                    .findByUserAndLanguageOrderByCreatedAtDesc(user, language, pageable);
        } else {
            recordPage = vocabularyRecordRepository
                    .findByUserOrderByCreatedAtDesc(user, pageable);
        }

        // 转换为DTO
        List<HistoryResponse> histories = recordPage.getContent().stream()
                .map(this::convertToHistoryResponse)
                .collect(Collectors.toList());

        // 构建响应
        return HistoryListResponse.builder()
                .records(histories)
                .currentPage(page)
                .totalPages(recordPage.getTotalPages())
                .totalElements(recordPage.getTotalElements())
                .hasNext(recordPage.hasNext())
                .hasPrevious(recordPage.hasPrevious())
                .build();
    }

    /**
     * 获取最近的历史记录
     */
    public List<HistoryResponse> getRecentHistory(String username, int limit) {
        log.info("Getting recent history for user: {}, limit: {}", username, limit);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 获取最近的记录
        List<VocabularyRecord> records = vocabularyRecordRepository
                .findTop10ByUserOrderByCreatedAtDesc(user);

        return records.stream()
                .limit(limit)
                .map(this::convertToHistoryResponse)
                .collect(Collectors.toList());
    }

    /**
     * 删除历史记录
     */
    public void deleteHistory(String username, Long recordId) {
        log.info("Deleting history record {} for user: {}", recordId, username);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 查找记录
        VocabularyRecord record = vocabularyRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        // 验证记录属于该用户
        if (!record.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to delete this record");
        }

        // 删除记录
        vocabularyRecordRepository.delete(record);
        log.info("History record deleted successfully");
    }

    /**
     * 获取用户统计信息
     */
    public UserStats getUserStats(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        long totalQueries = vocabularyRecordRepository.countByUser(user);

        return new UserStats(totalQueries);
    }

    /**
     * 转换实体为DTO
     */
    private HistoryResponse convertToHistoryResponse(VocabularyRecord record) {
        try {
            // 解析JSON词汇
            List<WordCategory> vocabulary = objectMapper.readValue(
                    record.getVocabularyJson(),
                    new TypeReference<List<WordCategory>>() {}
            );

            return HistoryResponse.builder()
                    .id(record.getId())
                    .scenario(record.getScenario())
                    .language(record.getLanguage())
                    .createdAt(record.getCreatedAt())
                    .vocabulary(vocabulary)
                    .build();
        } catch (Exception e) {
            log.error("Error converting vocabulary record", e);
            return HistoryResponse.builder()
                    .id(record.getId())
                    .scenario(record.getScenario())
                    .language(record.getLanguage())
                    .createdAt(record.getCreatedAt())
                    .build();
        }
    }

    /**
     * 用户统计信息
     */
    public record UserStats(long totalQueries) {}
}