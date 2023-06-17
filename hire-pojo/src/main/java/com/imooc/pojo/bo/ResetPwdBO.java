package com.imooc.pojo.bo;

import com.imooc.exceptions.GraceException;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.ar.AdminAR;
import com.imooc.utils.MD5Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ResetPwdBO {

    private  String adminId;
    private  String password;
    private  String rePassword;

    public void modifyPwd() {

        // 校验
        validate();

        // 重置密码 加盐salt
        // 生成盐(随机数字或字母)
        String salt = (int)((Math.random() * 9 + 1) * 100000) + "";
        String pwd = MD5Utils.encrypt(password, salt);
        AdminAR adminAR = new AdminAR();
        adminAR.setId(adminId);
        adminAR.setPassword(pwd);
        adminAR.setSlat(salt);
        adminAR.setUpdatedTime(LocalDateTime.now());
        adminAR.updateById();

    }

    public void validate() {
        checkPwd();
    }

    public void checkPwd() {
        if (StringUtils.isBlank(password))
            GraceException.display(ResponseStatusEnum.ADMIN_PASSWORD_NULL_ERROR);
        if (StringUtils.isBlank(rePassword))
            GraceException.display(ResponseStatusEnum.ADMIN_PASSWORD_NULL_ERROR);
        if (!StringUtils.equalsIgnoreCase(password, rePassword))
            GraceException.display(ResponseStatusEnum.ADMIN_PASSWORD_ERROR);
    }

    public void checkAdminId() {
        if (StringUtils.isBlank(adminId))
            GraceException.display(ResponseStatusEnum.ADMIN_NOT_EXIST);
        AdminAR adminAR = new AdminAR();
        adminAR.setId(adminId);
        adminAR = adminAR.selectById();
        if (adminAR == null)
            GraceException.display(ResponseStatusEnum.ADMIN_NOT_EXIST);
    }
}
