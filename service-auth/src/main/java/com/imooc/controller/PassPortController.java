package com.imooc.controller;


import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.utils.IPUtil;
import com.imooc.utils.SMSUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author liuao
 * @since 2023-04-05
 */
@RestController
@RequestMapping("passport")
@Slf4j
public class PassPortController extends BaseInfoProperties {

    @Autowired
    private SMSUtils smsUtils;

    @GetMapping("getSMSCode")
    public GraceJSONResult getSNSCode(String mobile, HttpServletRequest request) throws Exception {

        if (StringUtils.isBlank(mobile)) {
            return GraceJSONResult.errorMsg("手机号信息不不能为空");
        }
        // 限制用户在60s内只能获得一次验证码
        String userIp = IPUtil.getRequestIp(request);
        log.info("userIp值：" + userIp);
        redis.setnx60s(MOBILE_SMSCODE + ":" + userIp, mobile);

        String code = (int)((Math.random() * 9 + 1) * 100000) + "";
        log.info("验证码code值：" + code);
        // 将验证码存入redis，用于后续登录注册校验
        redis.set(MOBILE_SMSCODE + ":" + mobile, code, 30 * 60);
        smsUtils.sendSMS(mobile, code);
        return GraceJSONResult.ok();
    }

}

