package com.cesaas.android.pos.db.bean;

/**
 * Author FGB
 * Description 扫描支付信息Bean
 * Created at 2017/11/25 17:05
 * Version 1.0
 */

public class ScanPayInfoBean {

    private String OrderNo;//订单号
    private String RetrievalReferenceNumber;//参考号
    private String ScanTime;//扫描时间
    private String Types;

    public String getScanTime() {
        return ScanTime;
    }

    public void setScanTime(String scanTime) {
        ScanTime = scanTime;
    }

    public String getTypes() {
        return Types;
    }

    public void setTypes(String types) {
        Types = types;
    }


    public String getOrderNo() {
        return OrderNo;
    }

    public void setOrderNo(String orderNo) {
        OrderNo = orderNo;
    }

    public String getRetrievalReferenceNumber() {
        return RetrievalReferenceNumber;
    }

    public void setRetrievalReferenceNumber(String retrievalReferenceNumber) {
        RetrievalReferenceNumber = retrievalReferenceNumber;
    }
}
