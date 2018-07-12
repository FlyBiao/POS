package com.cesaas.android.pos.bean;

/**
 * Author FGB
 * Description
 * Created at 2017/12/1 0:50
 * Version 1.0
 */

public class CashPayBean {

    private int IsPractical;
    private int payType;
    private String orderNo;
    private double money;
    private String referenceNumber;
    private String traceAuditNumber;

    public int getIsPractical() {
        return IsPractical;
    }

    public void setIsPractical(int isPractical) {
        IsPractical = isPractical;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getTraceAuditNumber() {
        return traceAuditNumber;
    }

    public void setTraceAuditNumber(String traceAuditNumber) {
        this.traceAuditNumber = traceAuditNumber;
    }
}
