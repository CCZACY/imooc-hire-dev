package com.imooc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imooc.pojo.Admin;
import com.imooc.pojo.bo.AdminBO;

/**
 * <p>
 * 慕聘网运营管理系统的admin账户表，仅登录，不提供注册 服务类
 * </p>
 *
 * @author 风间影月
 * @since 2023-03-31
 */
public interface AdminService extends IService<Admin> {

    /**
     * admin登录
     * @param adminBO
     * @return boolean
     */
    public boolean adminLogin(AdminBO adminBO);

    public Admin getAdminInfo(AdminBO adminBO);

}
