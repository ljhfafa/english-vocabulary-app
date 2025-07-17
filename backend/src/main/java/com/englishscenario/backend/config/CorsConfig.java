package com.englishscenario.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * CORS（跨域资源共享）配置
 * 允许前端应用（运行在不同端口）访问后端API
 */
@Configuration  // 标记这是一个配置类
public class CorsConfig {

    @Bean  // 将方法返回的对象注册为Spring Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // 允许前端的地址访问
        config.addAllowedOriginPattern("http://localhost:3000");  // React开发服务器
        config.addAllowedOriginPattern("http://localhost:*");     // 允许所有localhost端口

        // 允许的HTTP方法
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("OPTIONS");

        // 允许的请求头
        config.addAllowedHeader("*");

        // 是否允许发送Cookie
        config.setAllowCredentials(true);

        // 配置有效期
        config.setMaxAge(3600L);

        source.registerCorsConfiguration("/api/**", config);
        return new CorsFilter(source);
    }
}