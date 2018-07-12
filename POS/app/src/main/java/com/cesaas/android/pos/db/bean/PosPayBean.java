package com.cesaas.android.pos.db.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Author FGB
 * Description
 * Created at 2017/11/20 17:27
 * Version 1.0
 */

public class PosPayBean {

    private String Id;//序列ID
    private String ShopId;//店铺Id
    private String ShopName;//店铺店铺名称
    private String CreateName;//订单营业员
    private String Amount;//金额
    private String OrderNo;//订单号
    private String TraceAudit;//凭证号
    private String AccountNumber;//卡号
    private String PayType;//支付类型
    private String PayName;//支付名称
    private String IsPractical;//是否实销
    private String PayStatus;//支付状态【POS 提供】
    private String CreateTime;//创建时间
    private String Message;//消息
    private String EnCode;//设备编号
    private String IsSuccess;//是否成功
    private String OrderType;//订单类型 0：独立收银，1：订单推送,2:会员充值
    private String OrderStatus;//订单状态 0：异常单，1：正常

    public PosPayBean(){}
    public PosPayBean(String shopId, String shopName, String createName, String orderNo, String amount
                    , String traceAudit, String payType, String payName, String isPractical, String payStatus,
                      String createTime, String message, String enCode, String accountNumber, String isSuccess
                     ,String OrderType,String OrderStatus) {
        this.ShopId = shopId;
        this.ShopName = shopName;
        this.CreateName = createName;
        this.OrderNo = orderNo;
        this.Amount = amount;
        this.TraceAudit = traceAudit;
        this.PayType = payType;
        this.PayName = payName;
        this.IsPractical = isPractical;
        this.PayStatus = payStatus;
        this.CreateTime = createTime;
        this.Message = message;
        this.EnCode = enCode;
        this.AccountNumber = accountNumber;
        this.IsSuccess = isSuccess;
        this.OrderType=OrderType;
        this.OrderStatus=OrderStatus;
    }


    public String getOrderType() {
        return OrderType;
    }

    public void setOrderType(String orderType) {
        OrderType = orderType;
    }

    public String getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        OrderStatus = orderStatus;
    }

    public String getIsSuccess() {
        return IsSuccess;
    }

    public void setIsSuccess(String isSuccess) {
        IsSuccess = isSuccess;
    }

    public String getAccountNumber() {
        return AccountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        AccountNumber = accountNumber;
    }

    public String getEnCode() {
        return EnCode;
    }

    public void setEnCode(String enCode) {
        EnCode = enCode;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getShopId() {
        return ShopId;
    }

    public void setShopId(String shopId) {
        ShopId = shopId;
    }

    public String getShopName() {
        return ShopName;
    }

    public void setShopName(String shopName) {
        ShopName = shopName;
    }

    public String getCreateName() {
        return CreateName;
    }

    public void setCreateName(String createName) {
        CreateName = createName;
    }

    public String getOrderNo() {
        return OrderNo;
    }

    public void setOrderNo(String orderNo) {
        OrderNo = orderNo;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getTraceAudit() {
        return TraceAudit;
    }

    public void setTraceAudit(String traceAudit) {
        TraceAudit = traceAudit;
    }

    public String getPayType() {
        return PayType;
    }

    public void setPayType(String payType) {
        PayType = payType;
    }

    public String getPayName() {
        return PayName;
    }

    public void setPayName(String payName) {
        PayName = payName;
    }

    public String getIsPractical() {
        return IsPractical;
    }

    public void setIsPractical(String isPractical) {
        IsPractical = isPractical;
    }

    public String getPayStatus() {
        return PayStatus;
    }

    public void setPayStatus(String payStatus) {
        PayStatus = payStatus;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }
}
