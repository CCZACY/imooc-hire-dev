package com.imooc.filter;

import com.google.gson.Gson;
import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.utils.IPUtil;
import com.imooc.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Slf4j
public class IPLimitFilter extends BaseInfoProperties implements GlobalFilter, Ordered {

    @Autowired
    private ExcludeUrlProperties excludeUrlProperties;

    @Value("${blackIP.continueCounts}")
    private Integer continueCounts;

    @Value("${blackIP.timeInterval}")
    private Integer timeInterval;

    @Value("${blackIP.limitTimes}")
    private Integer limitTimes;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 首先从资源文件中获取需要进行ip限流的接口路径列表
        List<String> urls = excludeUrlProperties.getIpLimitUrls();
        log.info("urls:" + urls);
//        List<String> urls = this.urls;
        // 获取进入过滤器的当前请求的接口路径
        String url = exchange.getRequest().getURI().getPath();
        log.info("url:" + url);
        // 判断需要被限制的接口路径
        if (urls.size() > 0 && urls.contains(url)) {
            // ip限流
            log.info("IPLimitFilter-拦截到需要进行ip限流校验的请求:" + url);
            return doLimit(exchange, chain);
        }

        // 默认放行
        return chain.filter(exchange);
    }

    // ip限流操作：判断同一ip在20s内请求次数是否超过3次
    public Mono<Void> doLimit(ServerWebExchange exchange, GatewayFilterChain chain) {

        //根据request获得请求ip
        ServerHttpRequest request = exchange.getRequest();
        String ip = IPUtil.getIP(request);

        // 正常的ip
        final String ipRedisKey = "gateway-ip:" + ip;
        // 被拦截的黑名单，若存在 则表示目前被关小黑屋
        final String ipRedisLimitedKey = "gateway-ip:limit:" + ip;
        // 获得当前黑名单ip剩余的小黑屋时间（过期时间剩余）
        Long ttl = redis.ttl(ipRedisLimitedKey);
        if (ttl > 0) {
            // 终止请求 返回错误提示
            return renderErrorMsg(exchange, ResponseStatusEnum.SYSTEM_ERROR_BLACK_IP);
        }
        // 在redis中获得ip的请求次数
        Long increment = redis.increment(ipRedisKey, 1);
        // 从判断若果是第一次进来 也就是从零开始的 则初期访问就是1 需要设置间隔的时间 也就是连续请求的间隔时间
        if (increment == 1) {
            redis.expire(ipRedisKey, timeInterval);
        }
        // 若果还能获得请求的次数 说明用户的连续请求落在了限定的时间区间之内
        // 一旦超过限定的连续请求次数 则需要限制当前ip
        if (increment > continueCounts) {
            redis.set(ipRedisLimitedKey, ipRedisLimitedKey, limitTimes);
            // 终止请求 返回错误提示
            return renderErrorMsg(exchange, ResponseStatusEnum.SYSTEM_ERROR_BLACK_IP);
        }
        return chain.filter(exchange);
    }

    /**
     * 包装并返回错误信息
     * @param exchange
     * @param statusEnum
     * @return Mono<Void>
     */
    public Mono<Void> renderErrorMsg(ServerWebExchange exchange, ResponseStatusEnum statusEnum) {

        // 1.获得response
        ServerHttpResponse response = exchange.getResponse();

        // 2.构建jsonResult
        GraceJSONResult jsonResult = GraceJSONResult.exception(statusEnum);

        // 3.修改response的code值为500
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);

        // 4.设置header类型
        if (!response.getHeaders().containsKey("Content_Type")) {
            response.getHeaders().add("Content_Type", MimeTypeUtils.APPLICATION_JSON_VALUE);
        }

        // 5.转换json 并且向response中写入数据
        String resultJson = new Gson().toJson(jsonResult);
        DataBuffer dataBuffer = response
                .bufferFactory()
                .wrap(resultJson.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(dataBuffer));

    }

    @Override
    public int getOrder() {
        return 1;
    }
}
