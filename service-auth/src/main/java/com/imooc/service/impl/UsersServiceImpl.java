package com.imooc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imooc.enums.Sex;
import com.imooc.enums.ShowWhichName;
import com.imooc.enums.UserRole;
import com.imooc.mapper.UsersMapper;
import com.imooc.pojo.Users;
import com.imooc.service.UsersService;
import com.imooc.utils.DesensitizationUtil;
import com.imooc.utils.LocalDateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author 风间影月
 * @since 2023-03-31
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {


    @Autowired
    private UsersMapper usersMapper;

    private static final String USER_FACE1 = "http://122.152.205.72:88/group1/M00/00/05/CpoxxF6ZUySASMbOAABBAXhjY0Y649.png";
    @Override
    public Users queryMobilesIsExist(String mobile) {

        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        return baseMapper.selectOne(queryWrapper.eq("mobile", mobile));
    }

    @Override
    @Transactional
    public Users createUsers(String mobile) {

        Users users = new Users();
        users.setMobile(mobile);
        users.setNickname("user：" + DesensitizationUtil.commonDisplay(mobile));
        users.setRealName("user：" + DesensitizationUtil.commonDisplay(mobile));
        users.setShowWhichName(ShowWhichName.nickname.type);

        users.setSex(Sex.secret.type);
        users.setFace(USER_FACE1);
        users.setEmail("");
        LocalDate birthday = LocalDateUtils.parseLocalDate("1980-01-01",
                LocalDateUtils.DATE_PATTERN);
        users.setBirthday(birthday);
        users.setCountry("中国");
        users.setProvince("");
        users.setCity("");
        users.setDistrict("");
        users.setDescription("这家伙很懒，什么都没留下~");

        users.setStartWorkDate(LocalDate.now());
        users.setPosition("Java开发工程师");
        users.setRole(UserRole.CANDIDATE.type);
        users.setHrInWhichCompanyId("");

        users.setCreatedTime(LocalDateTime.now());
        users.setUpdatedTime(LocalDateTime.now());

        usersMapper.insert(users);
        return users;
    }
}
