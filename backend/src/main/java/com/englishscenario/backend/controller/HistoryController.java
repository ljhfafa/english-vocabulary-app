package com.englishscenario.backend.controller;

import com.englishscenario.backend.dto.HistoryResponse;
import com.englishscenario.backend.dto.HistoryResponse.HistoryListResponse;
import com.englishscenario.backend.service.HistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 历史记录控制器
 */
@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
@Slf4j
public class HistoryController {

    private final HistoryService historyService;

    /**
     * 获取用户历史记录（分页）
     * GET /api/history?page=0&size=10&language=english
     */
    @GetMapping
    public ResponseEntity<HistoryListResponse> getUserHistory(
            @RequestParam String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String language) {

        log.info("Getting history for user: {}", username);

        try {
            HistoryListResponse response = historyService.getUserHistory(username, page, size, language);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting history", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取最近的历史记录
     * GET /api/history/recent?username=xxx&limit=5
     */
    @GetMapping("/recent")
    public ResponseEntity<List<HistoryResponse>> getRecentHistory(
            @RequestParam String username,
            @RequestParam(defaultValue = "5") int limit) {

        log.info("Getting recent history for user: {}", username);

        try {
            List<HistoryResponse> histories = historyService.getRecentHistory(username, limit);
            return ResponseEntity.ok(histories);
        } catch (Exception e) {
            log.error("Error getting recent history", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 删除历史记录
     * DELETE /api/history/{id}?username=xxx
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHistory(
            @PathVariable Long id,
            @RequestParam String username) {

        log.info("Deleting history {} for user: {}", id, username);

        try {
            historyService.deleteHistory(username, id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error deleting history", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取用户统计信息
     * GET /api/history/stats?username=xxx
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getUserStats(@RequestParam String username) {
        log.info("Getting stats for user: {}", username);

        try {
            var stats = historyService.getUserStats(username);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting user stats", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}