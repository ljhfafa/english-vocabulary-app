package com.englishscenario.backend.dto;

import lombok.Data;

/**
 * 认证请求DTO（登录/注册）
 */
@Data
public class AuthRequest {
    private String username;
    private String password;

    /**
     * 验证请求是否有效
     */
    public boolean isValid() {
        return username != null && username.length() >= 6 &&
                password != null && password.length() >= 8 &&
                username.matches("^[a-zA-Z0-9_]+$");
    }
}