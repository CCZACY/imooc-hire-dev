package com.imooc.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Data
//@PropertySource("classpath:jwt.properties")
//@ConfigurationProperties(prefix = "tencent.cloud")
@RefreshScope
public class JWTProperties {

    @Value("${jwt.key}")
    private String key;
}
