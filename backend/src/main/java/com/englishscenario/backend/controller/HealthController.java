package com.englishscenario.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 * 用于测试后端服务是否正常运行
 */
@RestController  // 告诉Spring这是一个REST控制器
@RequestMapping("/api")  // 所有这个类的接口都以/api开头
public class HealthController {

    /**
     * 健康检查接口
     * GET请求: http://localhost:8080/api/health
     */
    @GetMapping("/health")
    public Map<String, Object> checkHealth() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "English Scenario Backend is running!");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    /**
     * 测试接口 - 返回简单的问候
     * GET请求: http://localhost:8080/api/hello
     */
    @GetMapping("/hello")
    public Map<String, String> sayHello() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello from Spring Boot!");
        response.put("description", "Your backend is working correctly!");
        return response;
    }
}