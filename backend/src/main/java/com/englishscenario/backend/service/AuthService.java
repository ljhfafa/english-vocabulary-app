package com.englishscenario.backend.service;

import com.englishscenario.backend.dto.AuthRequest;
import com.englishscenario.backend.dto.AuthResponse;
import com.englishscenario.backend.entity.User;
import com.englishscenario.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 认证服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 用户注册
     */
    public AuthResponse register(AuthRequest request) {
        log.info("Processing registration for username: {}", request.getUsername());

        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            return AuthResponse.builder()
                    .success(false)
                    .message("用户名已存在")
                    .build();
        }

        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setIsActive(true);

        userRepository.save(user);

        log.info("User registered successfully: {}", request.getUsername());

        return AuthResponse.builder()
                .success(true)
                .username(user.getUsername())
                .message("注册成功")
                .build();
    }

    /**
     * 用户登录
     */
    public AuthResponse login(AuthRequest request) {
        log.info("Processing login for username: {}", request.getUsername());

        // 查找用户
        return userRepository.findByUsername(request.getUsername())
                .map(user -> {
                    // 验证密码
                    if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                        // 更新最后登录时间
                        user.setLastLogin(LocalDateTime.now());
                        userRepository.save(user);

                        // 生成简单的token（实际项目应使用JWT）
                        String token = "mock-token-" + UUID.randomUUID().toString();

                        log.info("Login successful for user: {}", request.getUsername());

                        return AuthResponse.builder()
                                .success(true)
                                .username(user.getUsername())
                                .token(token)
                                .message("登录成功")
                                .build();
                    } else {
                        return AuthResponse.builder()
                                .success(false)
                                .message("密码错误")
                                .build();
                    }
                })
                .orElse(AuthResponse.builder()
                        .success(false)
                        .message("用户不存在")
                        .build());
    }
}