package com.imooc.controller;


import com.google.gson.Gson;
import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.Admin;
import com.imooc.pojo.bo.AdminBO;
import com.imooc.service.AdminService;
import com.imooc.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <p>
 * 慕聘网运营管理系统的admin账户表，仅登录，不提供注册 前端控制器
 * </p>
 *
 * @author 风间影月
 * @since 2023-03-31
 */
@RestController
@RequestMapping("/admin")
public class AdminController extends BaseInfoProperties {

    @Autowired
    private AdminService adminService;

    @Autowired
    private JWTUtils jwtUtils;

    @PostMapping("/login")
    public GraceJSONResult adminLogin(@Valid @RequestBody AdminBO adminBO) {

        // 执行登录验证用户是否存在
        boolean b = adminService.adminLogin(adminBO);
        if (!b) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_LOGIN_ERROR);
        }
        // 登录成功 获得用户信息
        Admin adminInfo = adminService.getAdminInfo(adminBO);
        // 创建登录用户JWT token
        String adminToken = jwtUtils.createJWTWithPrefix(new Gson().toJson(adminInfo), TOKEN_ADMIN_PREFIX);

        return GraceJSONResult.ok(adminToken);
    }

    @PostMapping("/logout")
    public GraceJSONResult adminLogout() {
        return GraceJSONResult.ok();
    }
}

