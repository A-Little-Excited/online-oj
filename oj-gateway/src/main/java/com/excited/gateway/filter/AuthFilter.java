package com.excited.gateway.filter;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.excited.common.core.constants.CacheConstants;
import com.excited.common.core.constants.HttpConstants;
import com.excited.common.core.domain.LoginUser;
import com.excited.common.core.domain.R;
import com.excited.common.core.enums.ResultCode;
import com.excited.common.core.enums.UserIdentity;
import com.excited.common.core.utils.JwtUtils;
import com.excited.common.redis.service.RedisService;
import com.excited.gateway.properties.IgnoreWhiteProperties;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 用于校验 token 的全局过滤器
 */
@Component
@Slf4j
public class AuthFilter implements GlobalFilter, Ordered {

    @Autowired
    private IgnoreWhiteProperties ignoreWhite;

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private RedisService redisService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String url = request.getURI().getPath();
        // 1. 排除登录接口等无需进行校验的接口
        if (matches(ignoreWhite.getWhites(), url)) {
            return chain.filter(exchange);
        }

        // 2. 从 http 请求头中获取 token
        String token = getToken(request);
        // 对 token 进行判空
        if (StrUtil.isEmpty(token)) {
            return unauthorizedResponse(exchange, "令牌不可为空");
        }

        // 3，从 token 中获取 payload 部分
        Claims claims;
        try {
            claims = JwtUtils.parseToken(token, secret);

            if(claims == null) {
                return unauthorizedResponse(exchange, "令牌校验出现错误");
            }
        } catch (Exception e) {
            return unauthorizedResponse(exchange, "令牌校验出现错误");
        }

        // 4. 从 payload 部分中解析出预先存储的信息: 用户ID 和 用户敏感信息的唯一标识
        String userId = JwtUtils.getUserId(claims);
        // 对 用户ID 进行判空
        if (StrUtil.isEmpty(userId)) {
            return unauthorizedResponse(exchange, "令牌验证失败");
        }
        String userKey = JwtUtils.getUserKey(claims);
        // 对 用户敏感信息的唯一标识 进行判空
        if (StrUtil.isEmpty(userKey)) {
            return unauthorizedResponse(exchange, "令牌验证失败");
        }
        // 5. 从 Redis 中查询用户敏感信息是否过期
        String redisKey = getRedisKey(userKey);
        boolean isLogin = redisService.hasKey(redisKey);
        if (!isLogin) {
            // 用户敏感信息过期说明 token 也应该过期
            return unauthorizedResponse(exchange, "登录状态过期");
        }

        // 6. 获取用户敏感信息进行身份判断
        LoginUser loginUser = redisService.getCacheObject(redisKey, LoginUser.class);
        // 访问 system 服务只能是管理员用户
        if (url.contains(HttpConstants.SYSTEM_URL_PREFIX)
                && !loginUser.getIdentity().equals(UserIdentity.ADMIN.getValue())) {
            return unauthorizedResponse(exchange, "令牌验证失败");
        }
        // 访问 friend 服务只能是普通用户
        if (url.contains(HttpConstants.FRIEND_URL_PREFIX)
                && !loginUser.getIdentity().equals(UserIdentity.ORDINARY.getValue())) {
            return unauthorizedResponse(exchange, "令牌验证失败");
        }

        return chain.filter(exchange);
    }

    /**
     * 校验接口是否匹配于白名单列表中的任意一个选项
     * @param patternList 白名单列表
     * @param url 接口路径
     * @return 是否匹配
     */
    private boolean matches(List<String> patternList, String url) {
        // 对参数进行判空
        if (StrUtil.isEmpty(url) || CollectionUtils.isEmpty(patternList)) {
            return false;
        }

        for (String pattern: patternList) {
            if (isMatch(pattern, url)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断 url 是否和匹配规则相匹配, 匹配规则中可能出现的特殊字符如下
     * 1) ? : 匹配单个字符;
     * 2) * : 匹配任意个字符, 但不可跨层级;
     * 3) ** : 匹配任意个字符和层级
     * @param pattern 匹配规则
     * @param url 待匹配路径
     * @return 是否匹配
     */
    private boolean isMatch(String pattern, String url) {
        AntPathMatcher matcher = new AntPathMatcher();
        return matcher.match(pattern, url);
    }

    /**
     * 从 http 请求头中获取 token
     * @param request http 请求
     * @return token
     */
    private String getToken(ServerHttpRequest request) {
        // 获取 HttpConstants.AUTHORIZATION 字段的第一个值
        String token = request.getHeaders().getFirst(HttpConstants.AUTHORIZATION);
        // 如果前端存放 Jwt 时设置了前缀, 则需要裁剪掉前缀
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.JWT_PREFIX)) {
            token = token.replaceFirst(HttpConstants.JWT_PREFIX, StrUtil.EMPTY);
        }

        return token;
    }

    /**
     * 获取缓存中敏感信息对应的 key
     * @param userKey 用户敏感信息的唯一标识
     * @return key
     */
    private String getRedisKey(String userKey) {
        // 前缀 + 用户敏感信息的唯一标识
        return CacheConstants.LOGIN_TOKEN_KEY_PREFIX + userKey;
    }

    /**
     * 当 token 校验过程出现错误时返回的响应模型
     * @param exchange HTTP 请求-响应交互契约, 可以看作是 HTTP 请求和响应的结合体
     * @param msg 错误信息
     * @return 响应
     */
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String msg) {
        log.error("[鉴权异常处理] 请求路径:{}", exchange.getRequest().getPath());
        return webFluxResponseWriter(exchange.getResponse(), ResultCode.FAILED_UNAUTHORIZED.getCode(), msg);
    }

    /**
     * 使用 WebFlux 模型组装响应
     * @param response Http 响应
     * @param code 响应状态码
     * @param msg 错误信息
     * @return 响应
     */
    private Mono<Void> webFluxResponseWriter(ServerHttpResponse response, int code, String msg) {
        // 原先使用全局异常处理的形式组装异常响应, 是只适用于 Spring MVC 下的请求
        // Spring Cloud Gateway 是基于 WebFlux 实现的, 因此需要根据 WebFlux 模型组装响应而不能采取 MVC 模型组装响应
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        // 即便使用 WebFlux 模型来封装响应, 但是响应的数据结构仍旧不变
        R<?> result = R.fail(code, msg);
        DataBuffer dataBuffer = response.bufferFactory().wrap(JSON.toJSONString(result).getBytes());
        return response.writeWith(Mono.just(dataBuffer));
    }

    /**
     * 获取该全局过滤器的优先级
     * @return 优先级大小
     */
    @Override
    public int getOrder() {
        // 数字越小, 优先级越高, 且正负敏感
        return -200;
    }
}
