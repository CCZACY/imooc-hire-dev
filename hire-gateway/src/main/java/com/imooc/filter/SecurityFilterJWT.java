package com.imooc.filter;

import com.google.gson.Gson;
import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.utils.JWTUtils;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Slf4j
public class SecurityFilterJWT extends BaseInfoProperties implements GlobalFilter, Ordered {

    @Autowired
    private ExcludeUrlProperties excludeUrlProperties;

    @Autowired
    private JWTUtils jwtUtils;

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    public static final String HEADER_USER_TOKEN = "headerUserToken";

    /**
     * 各接口判断是否登录的网关过滤器
     * @param exchange 请求信息
     * @param chain 网关链路
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 首先从资源文件中获取可以直接被放行的接口路径
        List<String> urls = excludeUrlProperties.getUrls();
        log.info("urls:" + urls);
//        List<String> urls = this.urls;
        // 获取进入过滤器的当前请求的接口路径
        String url = exchange.getRequest().getURI().getPath();
        log.info("url:" + url);
        // 判断排除可以直接被放行的接口路径
        if (urls.size() > 0 && urls.contains(url)) {
            // 放行
            return chain.filter(exchange);
        }
        log.info("被拦截了。。。。");
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String userToken = headers.getFirst(HEADER_USER_TOKEN);
        // 判断header中的JWT令牌
        if (StringUtils.isNotBlank(userToken)) {
            String[] tokenArr = userToken.split(JWTUtils.AT);
            if (tokenArr.length < 2) {
                return renderErrorMsg(exchange, ResponseStatusEnum.UN_LOGIN);
            }
            // 获得jwt令牌以及前缀
            String prefix = tokenArr[0];
            String jwt = tokenArr[1];

            if (prefix.equalsIgnoreCase(TOKEN_USER_PREFIX)) {
                return dealJWT(exchange, chain, APP_USER_JSON, jwt);
            } else if(prefix.equalsIgnoreCase(TOKEN_SAAS_PREFIX)) {
                return dealJWT(exchange, chain, SAAS_USER_JSON, jwt);
            } else if (prefix.equalsIgnoreCase(TOKEN_ADMIN_PREFIX)) {
                return dealJWT(exchange, chain, ADMIN_USER_JSON, jwt);
            }

        }
        return renderErrorMsg(exchange, ResponseStatusEnum.UN_LOGIN);
//        GraceException.display(ResponseStatusEnum.UN_LOGIN);
//        return chain.filter(exchange);
    }

    public Mono<Void> dealJWT(ServerWebExchange exchange, GatewayFilterChain chain, String key, String jwt) {

        try {
            String userJson = jwtUtils.checkJWT(jwt);
            log.info("user2:" + userJson);
            ServerWebExchange serverWebExchange = setNewHeader(exchange, key, userJson);
            return chain.filter(serverWebExchange);
        } catch (ExpiredJwtException e) {
            e.printStackTrace();
            return renderErrorMsg(exchange, ResponseStatusEnum.JWT_EXPIRE_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            return renderErrorMsg(exchange, ResponseStatusEnum.JWT_SIGNATURE_ERROR);
        }
    }

    /**
     * 重新构建 新请求头的request
     * 需要注意的是 在ServerWebExchange中设置增加 带有中文信息的请求头或响应头时 可能会出现中文乱码的问题
     * 因此在设置header信息时 应该先对数据进行URL编码 然后再后面的controller中解码即可
     * 编解码使用到的字符集为：UTF-8
     * @param headerKey
     * @param headerValue
     * @return ServerWebExchange
     */
    public ServerWebExchange setNewHeader(ServerWebExchange exchange, String headerKey, String headerValue){

        String header = null;
        try {
            header = URLEncoder.encode(headerValue, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 构造新的request
        ServerHttpRequest newRequest = exchange.getRequest()
                .mutate()
                .header(headerKey, header)
                .build();

        // 替换原来的request
        return exchange.mutate().request(newRequest).build();
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

    /**
     * 多个过滤器执行顺序 数字越小优先级越大
     * @return int
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
