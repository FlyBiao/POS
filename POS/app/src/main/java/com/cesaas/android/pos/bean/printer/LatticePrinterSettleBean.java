package com.cesaas.android.pos.bean.printer;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：对账结算打印Bean
 * 创建日期：2016/11/7 15:15
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class LatticePrinterSettleBean {

    private String shopName;//店铺名称
    private String shopClerkName;//营业员
    private double currentTurnover;//当前累计营业额
    private int currentOrderCount;//当前订单数
    private String payType;//支付类型
    private double payMent;//实收金额
    private double refundAmount;//退款金额
    private int payOrderCount;//支付订单数
    private double refundMoney;//退款金额

    public double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(double refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getShopClerkName() {
        return shopClerkName;
    }

    public void setShopClerkName(String shopClerkName) {
        this.shopClerkName = shopClerkName;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public double getCurrentTurnover() {
        return currentTurnover;
    }

    public void setCurrentTurnover(double currentTurnover) {
        this.currentTurnover = currentTurnover;
    }

    public int getCurrentOrderCount() {
        return currentOrderCount;
    }

    public void setCurrentOrderCount(int currentOrderCount) {
        this.currentOrderCount = currentOrderCount;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public double getPayMent() {
        return payMent;
    }

    public void setPayMent(double payMent) {
        this.payMent = payMent;
    }

    public int getPayOrderCount() {
        return payOrderCount;
    }

    public void setPayOrderCount(int payOrderCount) {
        this.payOrderCount = payOrderCount;
    }

    public double getRefundMoney() {
        return refundMoney;
    }

    public void setRefundMoney(double refundMoney) {
        this.refundMoney = refundMoney;
    }
}
