package com.excited.common.security.config;

import com.excited.common.security.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Jwt 拦截器需要拦截所有非登录请求
        registry.addInterceptor(jwtInterceptor)
                .excludePathPatterns("/**/login", "/**/test/**")
                .addPathPatterns("/**");
    }
}
