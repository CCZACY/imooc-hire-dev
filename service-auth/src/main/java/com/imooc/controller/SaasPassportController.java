package com.imooc.controller;

import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("saas")
@Slf4j
public class SaasPassportController extends BaseInfoProperties {

    @PostMapping("getQRToken")
    public GraceJSONResult getQRToken()  {

        // 生成扫码登录的token
        String qrToken = UUID.randomUUID().toString();
        // 把token存入redis，设置有效时限，超时未扫码则刷新获得新的二维码
        redis.set(SAAS_PLATFORM_LOGIN_TOKEN + ":" + qrToken, qrToken, 5*60);
        // 存入redis标记当前的qrToken未被扫描读取
        redis.set(SAAS_PLATFORM_LOGIN_TOKEN_READ + ":" + qrToken, "0", 5*60);
        // 返回给前端，并让前端下一次请求的时候带上qrToken
        return GraceJSONResult.ok(qrToken);
    }
}
