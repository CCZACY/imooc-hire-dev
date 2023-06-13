package com.imooc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imooc.pojo.Admin;
import com.imooc.pojo.bo.AdminBO;
import com.imooc.pojo.bo.CreateAdminBO;
import com.imooc.utils.PagedGridResult;

/**
 * <p>
 * 慕聘网运营管理系统的admin账户表，仅登录，不提供注册 服务类
 * </p>
 *
 * @author 风间影月
 * @since 2023-03-31
 */
public interface AdminService {

    /**
     * 创建admin账户
     * @param createAdminBO
     */
    void createAdmin(CreateAdminBO createAdminBO);

    PagedGridResult getAdminList(String accountName, Integer page, Integer limit);

    void deleteAdmin(String username);
}
