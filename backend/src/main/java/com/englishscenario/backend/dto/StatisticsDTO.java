package com.englishscenario.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 统计相关DTO
 */
public class StatisticsDTO {

    /**
     * 仪表板数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardResponse {
        private UserStats userStats;
        private List<ScenarioCount> topScenarios;
        private List<DailyCount> dailyTrend;
        private Map<String, Long> languageStats;
    }

    /**
     * 用户统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserStats {
        private Long totalUsers;
        private Long todayNewUsers;
        private Long monthlyNewUsers;
        private Long weeklyActiveUsers;
    }

    /**
     * 场景统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScenarioCount {
        private String scenario;
        private Long count;
    }

    /**
     * 每日统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyCount {
        private LocalDate date;
        private Long count;
    }
}