package com.imooc.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Data
@PropertySource("classpath:tencentCloud.properties")
//@ConfigurationProperties(prefix = "tencent.cloud")
public class TencentCloudProperties {

    @Value("${tencent.cloud.secretId}")
    private String SecretId;
    @Value("${tencent.cloud.secretKey}")
    private String SecretKey;

}
