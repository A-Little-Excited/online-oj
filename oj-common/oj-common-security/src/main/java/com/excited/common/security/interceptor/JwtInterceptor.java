package com.excited.common.security.interceptor;

import cn.hutool.core.util.StrUtil;
import com.excited.common.core.constants.HttpConstants;
import com.excited.common.security.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 拦截器, 主要负责拦截请求后对 token 的有效时间进行延长
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtService jwtService;

    @Value("${jwt.secret}")
    private String secret;    // 从哪个配置文件中读取具体值, 取决于管理当前拦截器这个 bean 对象的 Spring 容器属于哪个服务

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = getToken(request);
        if (StrUtil.isEmpty(token)) {
            return true;
        }

        jwtService.extendToken(token, secret);
        return true;
    }

    /**
     * 从 http 请求头中获取 token
     * @param request http 请求
     * @return token
     */
    private String getToken(HttpServletRequest request) {
        // 获取 HttpConstants.AUTHORIZATION 字段的第一个值
        String token = request.getHeader(HttpConstants.AUTHORIZATION);
        // 如果前端存放 Jwt 时设置了前缀, 则需要裁剪掉前缀
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.JWT_PREFIX)) {
            token = token.replaceFirst(HttpConstants.JWT_PREFIX, StrUtil.EMPTY);
        }

        return token;
    }
}
