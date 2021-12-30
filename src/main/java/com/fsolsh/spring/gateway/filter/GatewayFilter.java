package com.fsolsh.spring.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.fsolsh.mining.service.AuthService;
import com.fsolsh.spring.gateway.bean.AuthProperties;
import com.fsolsh.spring.gateway.bean.MsgEntity;
import com.fsolsh.spring.gateway.bean.Result;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;

import java.nio.charset.StandardCharsets;

/**
 * 网关过滤器
 */
@Component
public class GatewayFilter implements GlobalFilter {

    @Autowired
    private AuthProperties authProperties;

    @DubboReference
    private AuthService authService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 如果未启用token验证，则向下传递
        if (!authProperties.getEnable()) {
            return chain.filter(exchange);
        }

        // 如果请求url在忽略token验证的url里，则向下传递
        String path = exchange.getRequest().getURI().getPath();
        String requestUrl = exchange.getRequest().getURI().getRawPath();
        if (ignore(path) || ignore(requestUrl)) {
            return chain.filter(exchange);
        }

        // 如果需要验证token，验证token是否有效
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest request = exchange.getRequest();
        String token = request.getHeaders().getFirst("Authorization");
        // request header没有携带token
        if (!StringUtils.hasText(token)) {
            return unauthorized(response, "request does not carry a token");
        }
        // 如果token验证失败，则直接返回
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
        //设置http状态码
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        //设置响应头信息
        response.getHeaders().add("Content-Type", "application/json");
        //设置返回json数据
        return response.writeAndFlushWith(Flux.just(ByteBufFlux.just(response.bufferFactory().wrap(JSON.toJSONString(Result.fail(MsgEntity.FAILURE.getCode(), msg)).getBytes(StandardCharsets.UTF_8)))));
    }

}
