package com.fsolsh.spring.gateway.filter;

import com.fsolsh.mining.service.AuthService;
import com.fsolsh.spring.gateway.bean.AuthProperties;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;

import java.nio.charset.StandardCharsets;

@Component
public class AuthFilter implements GlobalFilter {

    @Autowired
    private AuthProperties authProperties;

    @DubboReference
    private AuthService authService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 如果未启用网关验证，则跳过
        if (!authProperties.getEnable()) {
            return chain.filter(exchange);
        }

        // 如果在忽略的url里，则跳过
        String path = exchange.getRequest().getURI().getPath();
        String requestUrl = exchange.getRequest().getURI().getRawPath();
        if (ignore(path) || ignore(requestUrl)) {
            return chain.filter(exchange);
        }

        // 验证token是否有效
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest request = exchange.getRequest();
        String token = request.getHeaders().getFirst("Authorization");
        if (token == null) {
            return unauthorized(response, "request does not carry a token");
        }

        if (!authService.isValidJWT(token)) {
            return unauthorized(response, "token is invalid or expired");
        }

        return chain.filter(exchange);
    }

    /**
     * 检查是否忽略url
     *
     * @param path 路径
     * @return boolean
     */
    private boolean ignore(String path) {
        return authProperties.getIgnoreUrl().stream()
                .map(url -> url.replace("/**", ""))
                .anyMatch(path::startsWith);
    }

    private Mono<Void> unauthorized(ServerHttpResponse response, String msg) {
        //设置http响应状态码
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        //设置响应头信息Content-Type类型
        response.getHeaders().add("Content-Type", "application/json");
        //设置返回json数据
        return response.writeAndFlushWith(Flux.just(ByteBufFlux.just(response.bufferFactory().wrap(msg.getBytes(StandardCharsets.UTF_8)))));
    }

}
