package com.imooc.controller;

import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.bo.CreateAdminBO;
import com.imooc.service.AdminService;
import com.imooc.utils.PagedGridResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("admininfo")
@Slf4j
public class AdminInfoController extends BaseInfoProperties {

    @Autowired
    private AdminService adminService;

    @PostMapping("create")
    public GraceJSONResult create(@Valid @RequestBody CreateAdminBO createAdminBO) {

        adminService.createAdmin(createAdminBO);
        return GraceJSONResult.ok();
    }

    @PostMapping("list")
    public GraceJSONResult list(@RequestParam("accountName") String accountName,
                                @RequestParam("page") Integer page,
                                @RequestParam("limit") Integer limit) {

        if(page == null) page = 1;
        if (limit == null) limit = 10;

        PagedGridResult result = adminService.getAdminList(accountName, page, limit);
        return GraceJSONResult.ok(result);
    }

    @PostMapping("delete")
    public GraceJSONResult delete(@RequestParam("username") String username) {

        adminService.deleteAdmin(username);
        return GraceJSONResult.ok();

    }


}
