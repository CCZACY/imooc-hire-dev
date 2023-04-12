package com.imooc.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class SecurityFilterJWT implements GlobalFilter, Ordered {

    @Autowired
    private ExcludeUrlProperties excludeUrlProperties;

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 首先从资源文件中获取可以直接被放行的接口路径
        List<String> urls = excludeUrlProperties.getUrls();
        // 获取进入过滤器的当前请求的接口路径
        String url = exchange.getRequest().getURI().getPath();
        log.info("url:" + url);
        // 判断排除可以直接被放行的接口路径
        if (urls.size() > 0 && urls.contains(url)) {
            // 放行
            return chain.filter(exchange);
        }
        log.info("被拦截了。。。。");

        // 默认放行
        return chain.filter(exchange);
    }

    /**
     * 多个过滤器执行顺序 数字越小优先级越大
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
