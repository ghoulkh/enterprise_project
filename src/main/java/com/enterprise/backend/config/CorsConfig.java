package com.enterprise.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Cho phép truy cập từ tất cả các đường dẫn
                .allowedOrigins("*") // Cấp quyền truy cập từ nguồn gốc 'http://localhost:3000'
                .allowedMethods("*") // Cho phép sử dụng tất cả các phương thức (GET, POST, PUT, DELETE, ...)
                .allowedHeaders("*"); // Cho phép sử dụng tất cả các headers
    }
}