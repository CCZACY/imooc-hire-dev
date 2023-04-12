package com.imooc.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Data
@PropertySource("classpath:jwt.properties")
//@ConfigurationProperties(prefix = "tencent.cloud")
public class JWTProperties {

    @Value("${auth.key}")
    private String key;
}
