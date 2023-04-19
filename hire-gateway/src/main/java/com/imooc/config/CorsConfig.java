package com.imooc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * 通过代码的方式 在网关中解决 前后端调用产生的跨域问题（一般不采用这种方式，而是直接在网关yaml文件中进行配置 如下：）
 * spring:
 *   cloud:
 *     gateway:
 *       globalcors:
 *         cors-configurations:
 *           '[/**]':
 *             allowedOriginPatterns: "*"
 *             allowedHeaders: "*"
 *             allowedMethods: "*"
 *             allowCredentials: true
 */
//@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        // 1.添加cors配置信息
        CorsConfiguration config = new CorsConfiguration();
        // 允许所有域名进行跨域调用
        config.addAllowedOriginPattern("*");
        // 设置是否发送cookie信息
        config.setAllowCredentials(true);
        // 设置允许的请求方式
        config.addAllowedMethod("*");
        // 设置允许的header
        config.addAllowedHeader("*");
        // 2.为url添加映射路径
        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        corsConfigurationSource.registerCorsConfiguration("/**", config);
        // 3.返回重新定义好的资源
        return new CorsWebFilter(corsConfigurationSource);
    }
}
