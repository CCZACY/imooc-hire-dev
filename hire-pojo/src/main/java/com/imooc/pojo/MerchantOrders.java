package com.imooc.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 商户订单表，用于在支付中心存储查询，并且校验订单的支付状态
 * </p>
 *
 * @author 风间影月
 * @since 2022-08-04
 */
public class MerchantOrders implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 商户订单号
     */
    private String merchantOrderId;

    /**
     * 商户方的发起用户的用户主键id
     */
    private String merchantUserId;

    /**
     * 商户方的发起用户所在的企业主键id
     */
    private String merchantCompanyId;

    /**
     * 实际支付总金额（包含商户所支付的订单费邮费总额）
     */
    private Integer amount;

    /**
     * 支付方式
     */
    private Integer payMethod;

    /**
     * 支付状态 10：未支付 20：已支付 30：支付失败 40：已退款
     */
    private Integer payStatus;

    /**
     * 从哪一端来的，比如从慕聘网这门实战过来的，注明是哪个项目的
     */
    private String comeFrom;

    /**
     * 支付成功后的通知地址，这个是开发者那一段的，不是第三方支付通知的地址
     */
    private String returnUrl;

    /**
     * 逻辑删除状态;1: 删除 0:未删除
     */
    private Integer isDelete;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMerchantOrderId() {
        return merchantOrderId;
    }

    public void setMerchantOrderId(String merchantOrderId) {
        this.merchantOrderId = merchantOrderId;
    }

    public String getMerchantUserId() {
        return merchantUserId;
    }

    public void setMerchantUserId(String merchantUserId) {
        this.merchantUserId = merchantUserId;
    }

    public String getMerchantCompanyId() {
        return merchantCompanyId;
    }

    public void setMerchantCompanyId(String merchantCompanyId) {
        this.merchantCompanyId = merchantCompanyId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(Integer payMethod) {
        this.payMethod = payMethod;
    }

    public Integer getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }

    public String getComeFrom() {
        return comeFrom;
    }

    public void setComeFrom(String comeFrom) {
        this.comeFrom = comeFrom;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    @Override
    public String toString() {
        return "MerchantOrders{" +
        "id=" + id +
        ", merchantOrderId=" + merchantOrderId +
        ", merchantUserId=" + merchantUserId +
        ", merchantCompanyId=" + merchantCompanyId +
        ", amount=" + amount +
        ", payMethod=" + payMethod +
        ", payStatus=" + payStatus +
        ", comeFrom=" + comeFrom +
        ", returnUrl=" + returnUrl +
        ", isDelete=" + isDelete +
        ", createdTime=" + createdTime +
        ", updatedTime=" + updatedTime +
        "}";
    }
}
