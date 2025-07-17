package com.englishscenario.backend.service;

import com.englishscenario.backend.dto.StatisticsDTO.*;
import com.englishscenario.backend.repository.UserRepository;
import com.englishscenario.backend.repository.VocabularyRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理员统计服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminStatisticsService {

    private final UserRepository userRepository;
    private final VocabularyRecordRepository vocabularyRecordRepository;

    /**
     * 获取仪表板数据
     */
    public DashboardResponse getDashboardData() {
        log.info("Fetching dashboard statistics");

        return DashboardResponse.builder()
                .userStats(getUserStatistics())
                .topScenarios(getTopScenarios(10))
                .dailyTrend(getDailyTrend(30))
                .languageStats(getLanguageStatistics())
                .build();
    }

    /**
     * 获取用户统计
     */
    private UserStats getUserStatistics() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime monthStart = now.toLocalDate().withDayOfMonth(1).atStartOfDay();
        LocalDateTime weekStart = now.toLocalDate().minusDays(7).atStartOfDay();

        return UserStats.builder()
                .totalUsers(userRepository.count())
                .todayNewUsers(userRepository.countByCreatedAtBetween(todayStart, now))
                .monthlyNewUsers(userRepository.countByCreatedAtAfter(monthStart))
                .weeklyActiveUsers(userRepository.countActiveUsersSince(weekStart))
                .build();
    }

    /**
     * 获取热门场景
     */
    private List<ScenarioCount> getTopScenarios(int limit) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Object[]> results = vocabularyRecordRepository.findTopScenarios(
                thirtyDaysAgo,
                PageRequest.of(0, limit)
        );

        return results.stream()
                .map(row -> ScenarioCount.builder()
                        .scenario((String) row[0])
                        .count(((Number) row[1]).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 获取每日查询趋势
     */
    private List<DailyCount> getDailyTrend(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<Object[]> results = vocabularyRecordRepository.findDailyQueryCount(startDate);

        return results.stream()
                .map(row -> DailyCount.builder()
                        .date(((Date) row[0]).toLocalDate())
                        .count(((Number) row[1]).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 获取语言使用统计
     */
    private Map<String, Long> getLanguageStatistics() {
        List<Object[]> results = vocabularyRecordRepository.findLanguageStatistics();
        Map<String, Long> stats = new HashMap<>();

        for (Object[] row : results) {
            String language = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            stats.put(language, count);
        }

        return stats;
    }

    /**
     * 获取用户增长趋势（最近30天）
     */
    public List<DailyCount> getUserGrowthTrend() {
        // 这里可以实现用户增长趋势的统计
        // 需要额外的查询来获取每日新增用户数
        return null;
    }
}