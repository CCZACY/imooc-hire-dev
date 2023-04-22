package com.imooc.controller;

import com.google.gson.Gson;
import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.Users;
import com.imooc.pojo.vo.SaasUserVO;
import com.imooc.service.UsersService;
import com.imooc.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("saas")
@Slf4j
public class SaasPassportController extends BaseInfoProperties {

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private UsersService usersService;

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

    /**
     * SAAS网页端每隔一段时间（3秒）定时查询QRToken是否被读取 用于页面的展示标记判断
     * 前端处理：限制用户在页面不操作而频繁发起调用：【页面失效，请刷新后再执行扫描登录】
     * 注意：如果使用websocket或者netty 可以在app扫描之后 在上一个接口直接通信浏览器（H5）进行页面扫码的状态标记
     * @param qrToken
     * @return
     */
    @PostMapping("codeHasBeenRead")
    public GraceJSONResult codeHasBeenRead(@RequestParam String qrToken)  {

        String readStr = redis.get(SAAS_PLATFORM_LOGIN_TOKEN_READ + ":" + qrToken);
        List list = new ArrayList();
        if (StringUtils.isNotBlank(readStr)) {
            String[] readArr = readStr.split(",");
            if (readArr.length >= 2) {
                list.add(Integer.valueOf(readArr[0]));
                list.add(readArr[1]);
            } else {
                list.add(0);
            }
        }

        return GraceJSONResult.ok(list);
    }

    /**
     * 手机端点击确认登录 携带preToken与后端进行判断 如果校验ok则登录成功
     * @param userId
     * @param qrToken
     * @param preToken
     * @return
     */
    @PostMapping("goQRLogin")
    public GraceJSONResult goQRLogin(@RequestParam String userId,
                                     @RequestParam String qrToken,
                                     @RequestParam String preToken)  {

        String preTokenRedisArr = redis.get(SAAS_PLATFORM_LOGIN_TOKEN_READ + ":" + qrToken);
        if (StringUtils.isNotBlank(preTokenRedisArr)) {
            String preTokenRedis = preTokenRedisArr.split(",")[1];
            if (preTokenRedis.equalsIgnoreCase(preToken)) {
                // 根据用户id获取用户信息
                Users hrUser = usersService.getById(userId);
                if (hrUser == null) {
                    return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
                }
                // 存入用户信息到redis中, 因为H5在未登陆的情况新拿不到用户id
                // 如果使用websocket是可以直接通信H5获得用户id 则无此问题
                redis.set(REDIS_SAAS_USER_INFO + ":temp:" + preToken, new Gson().toJson(hrUser), 5 * 60);
            }
        }

        return GraceJSONResult.ok();
    }

    /**
     * 页面登录跳转
     * @param preToken
     * @return
     */
    @PostMapping("checkLogin")
    public GraceJSONResult checkLogin(@RequestParam String preToken)  {

        if (StringUtils.isBlank(preToken)) {
            return GraceJSONResult.ok("");
        }
        // 获得用户临时信息
        String userJson = redis.get(REDIS_SAAS_USER_INFO + ":temp:" + preToken);
        if (StringUtils.isBlank(userJson)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }
        // 确认执行登录后 生成saas用户token 并长期有效
        String saasUserToken = jwtUtils.createJWTWithPrefix(userJson, TOKEN_SAAS_PREFIX);
        // 存入用户信息长期有效
        redis.set(REDIS_SAAS_USER_INFO + ":" + saasUserToken, userJson);

        return GraceJSONResult.ok(saasUserToken);
    }

    /**
     * 页面登录跳转
     * @param token
     * @return
     */
    @GetMapping("info")
    public GraceJSONResult info(@RequestParam String token)  {

        String userJson = redis.get(REDIS_SAAS_USER_INFO + ":" + token);
        Users saasUser = new Gson().fromJson(userJson, Users.class);

        SaasUserVO saasUserVO = new SaasUserVO();
        BeanUtils.copyProperties(saasUser, saasUserVO);

        return GraceJSONResult.ok(saasUserVO);
    }

    /**
     * 页面登出
     * @param token
     * @return
     */
    @PostMapping("logout")
    public GraceJSONResult logout(@RequestParam String token)  {

        return GraceJSONResult.ok();
    }
}
