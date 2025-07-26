package com.englishscenario.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // ✅ 添加生产环境的域名（支持 https）
        config.addAllowedOrigin("https://englishai.tech");
        config.addAllowedOrigin("https://www.englishai.tech");

        // ✅ 本地开发环境继续支持
        config.addAllowedOriginPattern("http://localhost:*");
        config.addAllowedOriginPattern("http://127.0.0.1:*");

        // ✅ 允许所有方法和请求头
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");

        // ✅ 允许携带 Cookie
        config.setAllowCredentials(true);

        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 作用范围你可以用 /** 或 /api/** 看你的项目路径
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}