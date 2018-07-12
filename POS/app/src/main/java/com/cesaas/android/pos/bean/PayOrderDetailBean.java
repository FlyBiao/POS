package com.cesaas.android.pos.bean;

import java.io.Serializable;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：支付账单Bean
 * 创建日期：2016/10/20 17:24
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class PayOrderDetailBean implements Serializable {

    private double ConsumeAmount;
    private String CreateTime;
    private String RetailId;
    private int PayType;
    private String TraceAuditNumber;
    private int PayCategory;
    private String Description;

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getPayCategory() {
        return PayCategory;
    }

    public void setPayCategory(int payCategory) {
        PayCategory = payCategory;
    }

    public double getConsumeAmount() {
        return ConsumeAmount;
    }

    public void setConsumeAmount(double consumeAmount) {
        ConsumeAmount = consumeAmount;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getRetailId() {
        return RetailId;
    }

    public void setRetailId(String retailId) {
        RetailId = retailId;
    }

    public int getPayType() {
        return PayType;
    }

    public void setPayType(int payType) {
        PayType = payType;
    }

    public String getTraceAuditNumber() {
        return TraceAuditNumber;
    }

    public void setTraceAuditNumber(String traceAuditNumber) {
        TraceAuditNumber = traceAuditNumber;
    }
}
