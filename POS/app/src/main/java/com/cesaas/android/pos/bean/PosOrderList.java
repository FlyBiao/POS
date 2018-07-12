package com.cesaas.android.pos.bean;

import java.io.Serializable;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：賬單列表Bean
 * 创建日期：2016/10/20 11:47
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class PosOrderList implements Serializable {

    private String RetailId;//支付订单号
    private String PayDate;//支付时间
    private String CreateTime;//订单创建时间
    private String CreateName;//订单创建人员名称
    private double PayAmount;//消费金额
    private int OrderStatus;//订单交易状态
    private int IsRefund;//是否已退款  0：正常，1：已退款
    private int RetailCheck;//0：正常，1：退款
    private int RetailFrom;//零售单来源【0:PC端下单,1:Pos机】

    public String getCreateName() {
        return CreateName;
    }

    public void setCreateName(String createName) {
        CreateName = createName;
    }

    public int getRetailFrom() {
        return RetailFrom;
    }

    public void setRetailFrom(int retailFrom) {
        RetailFrom = retailFrom;
    }

    public String getRetailId() {
        return RetailId;
    }

    public void setRetailId(String retailId) {
        RetailId = retailId;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public int getRetailCheck() {
        return RetailCheck;
    }

    public void setRetailCheck(int retailCheck) {
        RetailCheck = retailCheck;
    }

    public String getPayDate() {
        return PayDate;
    }

    public void setPayDate(String payDate) {
        PayDate = payDate;
    }

    public double getPayAmount() {
        return PayAmount;
    }

    public void setPayAmount(double payAmount) {
        PayAmount = payAmount;
    }

    public int getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        OrderStatus = orderStatus;
    }

    public int getIsRefund() {
        return IsRefund;
    }

    public void setIsRefund(int isRefund) {
        IsRefund = isRefund;
    }
}
