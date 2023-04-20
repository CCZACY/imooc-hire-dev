package com.imooc.controller;

import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@RestController
@RequestMapping("saas")
@Slf4j
public class SaasPassportController extends BaseInfoProperties {

    @Autowired
    private JWTUtils jwtUtils;

    @PostMapping("getQRToken")
    public GraceJSONResult getQRToken()  {

        // 生成扫码登录的token
        String qrToken = UUID.randomUUID().toString();
        // 把token存入redis，设置有效时限，超时未扫码则刷新获得新的二维码
        redis.set(SAAS_PLATFORM_LOGIN_TOKEN + ":" + qrToken, qrToken, 5*60);
        // 存入redis标记，当前的qrToken未被扫描读取
        redis.set(SAAS_PLATFORM_LOGIN_TOKEN_READ + ":" + qrToken, "0", 5*60);
        // 返回给前端，并让前端下一次请求的时候带上qrToken
        return GraceJSONResult.ok(qrToken);
    }

    @PostMapping("scanCode")
    public GraceJSONResult scanCode(@RequestParam String qrToken, HttpServletRequest request) {

        // 判空 qrToken
        if (StringUtils.isBlank(qrToken)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FAILED);
        }
        // 从redis中获得并判断qrToken是否有效
        String redisQRToken = redis.get(SAAS_PLATFORM_LOGIN_TOKEN + ":" + qrToken);
        if (!redisQRToken.equalsIgnoreCase(qrToken)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FAILED);
        }
        // 获得用户id以及jwt令牌
        String appUserId = request.getHeader("appUserId");
        String appUserToken = request.getHeader("appUserToken");
        // 判空 用户id 和 用户jwt令牌
        if (StringUtils.isBlank(appUserId) || StringUtils.isBlank(appUserToken)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.HR_TICKET_INVALID);
        }
        // 校验jwt
        String userJson = jwtUtils.checkJWT(appUserToken.split("@")[1]);
        if (StringUtils.isBlank(userJson)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.HR_TICKET_INVALID);
        }
        // 执行后续正常业务 生成预登录token
        String preToken = UUID.randomUUID().toString();
        // 把预登录token存入redis，来替换qrToken，设置有效时限，超时未扫码则刷新获得新的二维码
        redis.set(SAAS_PLATFORM_LOGIN_TOKEN + ":" + qrToken, preToken, 5*60);
        // 存入redis标记，当前的qrToken需要被读取并且失效覆盖，网页端标记二维码已被扫
        redis.set(SAAS_PLATFORM_LOGIN_TOKEN_READ + ":" + qrToken, "1,"+ preToken, 5*60);
        // 返回给手机端 app下次请求携带preToken
        return GraceJSONResult.ok(preToken);
    }
}
