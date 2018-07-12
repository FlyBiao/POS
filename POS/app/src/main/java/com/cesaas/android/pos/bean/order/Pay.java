package com.cesaas.android.pos.bean.order;

import java.io.Serializable;

/**
 * Author FGB
 * Description
 * Created at 2017/9/11 15:13
 * Version 1.0
 */

public class Pay implements Serializable {


    /**
     * RetailId : 10951
     * ConsumeAmount : 140.0
     * RetrievalReferenceNumber : 5a38ea9b-4459-49f8-a9ed-4acc485f91d5
     * TraceAuditNumber : b5c8101c-9209-4471-9b43-d5f2967ab9e9
     * PayType : 4
     * CreateTime : 2017-08-31 10:39:01
     * PayCategory : 0
     */

    private int RetailId;
    private double ConsumeAmount;
    private String RetrievalReferenceNumber;
    private String TraceAuditNumber;
    private int PayType;
    private String CreateTime;
    private int PayCategory;

    public void setRetailId(int RetailId) {
        this.RetailId = RetailId;
    }

    public void setConsumeAmount(double ConsumeAmount) {
        this.ConsumeAmount = ConsumeAmount;
    }

    public void setRetrievalReferenceNumber(String RetrievalReferenceNumber) {
        this.RetrievalReferenceNumber = RetrievalReferenceNumber;
    }

    public void setTraceAuditNumber(String TraceAuditNumber) {
        this.TraceAuditNumber = TraceAuditNumber;
    }

    public void setPayType(int PayType) {
        this.PayType = PayType;
    }

    public void setCreateTime(String CreateTime) {
        this.CreateTime = CreateTime;
    }

    public void setPayCategory(int PayCategory) {
        this.PayCategory = PayCategory;
    }

    public int getRetailId() {
        return RetailId;
    }

    public double getConsumeAmount() {
        return ConsumeAmount;
    }

    public String getRetrievalReferenceNumber() {
        return RetrievalReferenceNumber;
    }

    public String getTraceAuditNumber() {
        return TraceAuditNumber;
    }

    public int getPayType() {
        return PayType;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public int getPayCategory() {
        return PayCategory;
    }
}
