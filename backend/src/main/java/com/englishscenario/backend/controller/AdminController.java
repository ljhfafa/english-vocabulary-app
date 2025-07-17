package com.englishscenario.backend.controller;

import com.englishscenario.backend.dto.StatisticsDTO.DashboardResponse;
import com.englishscenario.backend.service.AdminStatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员控制器
 * 提供统计数据接口
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000") // 开发环境允许跨域
public class AdminController {

    private final AdminStatisticsService statisticsService;

    /**
     * 获取仪表板数据
     * GET /api/admin/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard() {
        log.info("Admin dashboard data requested");

        try {
            DashboardResponse dashboard = statisticsService.getDashboardData();
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            log.error("Error fetching dashboard data", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 简单的权限验证
     * 实际生产环境应该使用更完善的权限系统
     */
    @GetMapping("/check")
    public ResponseEntity<String> checkAdminAccess(
            @RequestParam(required = false) String adminCode) {

        // 简单的管理员密码验证（生产环境请使用更安全的方式）
        if ("admin123".equals(adminCode)) {
            return ResponseEntity.ok("Access granted");
        }

        return ResponseEntity.status(403).body("Access denied");
    }
}