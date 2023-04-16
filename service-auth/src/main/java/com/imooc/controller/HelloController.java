package com.imooc.controller;

import com.google.gson.Gson;
import com.imooc.api.intercept.JWTCurrentUserInterceptor;
import com.imooc.base.BaseInfoProperties;
import com.imooc.pojo.Admin;
import com.imooc.pojo.Users;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@RestController
@RequestMapping("auth")
@Slf4j
public class HelloController extends BaseInfoProperties {

    @GetMapping("test")
    public Object hello(HttpServletRequest request) throws UnsupportedEncodingException {

        String header = request.getHeader(APP_USER_JSON);
        String decodeUserInfo = URLDecoder.decode(header, "UTF-8");
        log.info("user3:" + decodeUserInfo);
        Users users = new Gson().fromJson(decodeUserInfo, Users.class);
        log.info("description:" + users.getDescription());
        log.info("JWTUserInfo:" + users.toString());

        Users users1 = JWTCurrentUserInterceptor.currentUser.get();
//        Admin admin = JWTCurrentUserInterceptor.adminUser.get();
        log.info("users1:" + users1.toString());
//        log.info("admin:" + admin.toString());

        return "Hello AuthService~~~";
    }
}
