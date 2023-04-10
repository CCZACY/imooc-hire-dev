package com.imooc.controller;


import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.Users;
import com.imooc.pojo.bo.RegistLoginBo;
import com.imooc.service.UsersService;
import com.imooc.utils.IPUtil;
import com.imooc.utils.SMSUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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

    @Autowired
    private UsersService usersService;

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

    @PostMapping("login")
    public GraceJSONResult login(@Valid  @RequestBody RegistLoginBo registLoginBo, HttpServletRequest request) throws Exception {

        // 参数校验
        String smsCode = registLoginBo.getSmsCode();
        String mobile = registLoginBo.getMobile();

        // 1.从redis中查询验证码是否存在和匹配
        String redisSmsCode = redis.get(MOBILE_SMSCODE + ":" + mobile);
        if (StringUtils.isBlank(redisSmsCode) || !redisSmsCode.equalsIgnoreCase(smsCode)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }


        // 2.查询用户是否已经存在
        Users users = usersService.queryMobilesIsExist(mobile);
        if (users != null) {
            return GraceJSONResult.errorMsg("当前手机号已存在，请勿重复注册！");
        }
        users = usersService.createUsers(mobile);
        // 3.用户登陆注册以后，删除redis中的短信验证码
        redis.del(MOBILE_SMSCODE + ":" + mobile);

        // 4.返回用户的信息给前端
        return GraceJSONResult.ok(users);
    }

}

