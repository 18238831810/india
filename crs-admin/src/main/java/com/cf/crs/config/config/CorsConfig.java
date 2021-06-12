package com.cf.crs.config.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * @author frank
 * @description 跨域配置
 * @date 2019/8/13 16:06
 */
@Configuration
public class CorsConfig {

    public CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.setMaxAge(36000L);
        return corsConfiguration;
    }

    @Bean
    public CorsWebFilter corsFilter() {
        org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", buildConfig());
        return new CorsWebFilter(urlBasedCorsConfigurationSource);
    }
}
