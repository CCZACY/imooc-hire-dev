package com.imooc.pojo;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * <p>
 * 
 * </p>
 *
 * @author 风间影月
 * @since 2022-08-04
 */
public class UserPassport implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 用户在慕课网的用户id
     */
    private String imoocUserId;

    /**
     * 由风间影月老师分配给用户的密码，这个密码存入数据库需要加密
     */
    private String password;

    /**
     * 用户访问有效期
     */
    private LocalDate endDate;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImoocUserId() {
        return imoocUserId;
    }

    public void setImoocUserId(String imoocUserId) {
        this.imoocUserId = imoocUserId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "UserPassport{" +
        "id=" + id +
        ", imoocUserId=" + imoocUserId +
        ", password=" + password +
        ", endDate=" + endDate +
        "}";
    }
}
