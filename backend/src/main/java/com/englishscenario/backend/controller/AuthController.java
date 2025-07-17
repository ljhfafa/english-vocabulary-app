package com.englishscenario.backend.controller;

import com.englishscenario.backend.dto.AuthRequest;
import com.englishscenario.backend.dto.AuthResponse;
import com.englishscenario.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 处理用户登录和注册
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * 用户注册
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request) {
        log.info("Register request received for username: {}", request.getUsername());

        // 验证请求
        if (!request.isValid()) {
            return ResponseEntity.badRequest()
                    .body(AuthResponse.builder()
                            .success(false)
                            .message("用户名至少6位，密码至少8位，用户名只能包含字母、数字和下划线")
                            .build());
        }

        try {
            AuthResponse response = authService.register(request);
            return response.isSuccess()
                    ? ResponseEntity.ok(response)
                    : ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Registration error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AuthResponse.builder()
                            .success(false)
                            .message("注册失败，请稍后重试")
                            .build());
        }
    }

    /**
     * 用户登录
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        log.info("Login request received for username: {}", request.getUsername());

        // 验证请求
        if (!request.isValid()) {
            return ResponseEntity.badRequest()
                    .body(AuthResponse.builder()
                            .success(false)
                            .message("用户名或密码格式不正确")
                            .build());
        }

        try {
            AuthResponse response = authService.login(request);
            return response.isSuccess()
                    ? ResponseEntity.ok(response)
                    : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            log.error("Login error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AuthResponse.builder()
                            .success(false)
                            .message("登录失败，请稍后重试")
                            .build());
        }
    }
}