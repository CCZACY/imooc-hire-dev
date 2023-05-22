package com.imooc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imooc.pojo.Admin;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 慕聘网运营管理系统的admin账户表，仅登录，不提供注册 Mapper 接口
 * </p>
 *
 * @author 风间影月
 * @since 2023-03-31
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {

}
