package com.imooc.filter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@PropertySource("classpath:excludeUrlPath.properties")
@ConfigurationProperties(prefix = "exclude")
public class ExcludeUrlProperties {

//    @Value("${exclude.urls[]}")
    private List<String> urls;
}
