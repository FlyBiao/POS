package com.cesaas.android.pos.bean;

import org.json.JSONObject;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：pos pay Bean
 * 创建日期：2016/10/19 16:03
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class PosPayLogBean {

    public String AccountNumber;//卡号
    public String ReferenceNumber;//参考号
    public String TraceAuditNumber;//凭证号
    public String AliPayrefNo;//支付宝交易号
    public String WeiXinPayrefNo;//微信交易号
    public String OrderId;//支付订单号
    public double PayAmount;//支付金额
    public int PayType;//支付方式 2：微信，3：支付宝，4银联，5现金
    public String PayQrCode;//支付二维码
    private String Remark;
    public String enCode;//设备EN号

    public JSONObject getPayLog(){
        JSONObject obj=new JSONObject();
        try {
            obj.put("OrderId","13345095960447747474");
            obj.put("PayAmount",0.01);
            obj.put("PayType",3);
            obj.put("TraceAuditNumber","1000485882016484884");
            obj.put("EnCode","c734f9u9");

        }catch (Exception e){

        }

        return obj;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getEnCode() {
        return enCode;
    }

    public void setEnCode(String enCode) {
        this.enCode = enCode;
    }

    public String getAccountNumber() {
        return AccountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        AccountNumber = accountNumber;
    }

    public String getReferenceNumber() {
        return ReferenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        ReferenceNumber = referenceNumber;
    }

    public String getTraceAuditNumber() {
        return TraceAuditNumber;
    }

    public void setTraceAuditNumber(String traceAuditNumber) {
        TraceAuditNumber = traceAuditNumber;
    }

    public String getAliPayrefNo() {
        return AliPayrefNo;
    }

    public void setAliPayrefNo(String aliPayrefNo) {
        AliPayrefNo = aliPayrefNo;
    }

    public String getWeiXinPayrefNo() {
        return WeiXinPayrefNo;
    }

    public void setWeiXinPayrefNo(String weiXinPayrefNo) {
        WeiXinPayrefNo = weiXinPayrefNo;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public double getPayAmount() {
        return PayAmount;
    }

    public void setPayAmount(double payAmount) {
        PayAmount = payAmount;
    }

    public int getPayType() {
        return PayType;
    }

    public void setPayType(int payType) {
        PayType = payType;
    }

    public String getPayQrCode() {
        return PayQrCode;
    }

    public void setPayQrCode(String payQrCode) {
        PayQrCode = payQrCode;
    }
}
