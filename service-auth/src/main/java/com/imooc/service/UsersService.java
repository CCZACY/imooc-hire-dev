package com.imooc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imooc.pojo.Users;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author 风间影月
 * @since 2023-03-31
 */
public interface UsersService extends IService<Users> {

    /**
     * 判断用户是否已经存在
     * @param nobile
     * @return
     */
    public Users queryMobilesIsExist(String nobile);

    /**
     * 创建新用户
     * @param mobile
     * @return
     */
    public Users createUsers(String mobile);

}
