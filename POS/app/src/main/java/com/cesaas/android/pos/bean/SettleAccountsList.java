package com.cesaas.android.pos.bean;

import java.io.Serializable;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：结算对账列表Bean
 * 创建日期：2016/10/31 23:29
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class  SettleAccountsList implements Serializable {

    private int PayBizType;//6:正常 13：退款
    private int PayCategory;//0:正常，1：退款
    private double PayMent;//支付金额
    private int PayType;//支付类型
    private int OrderCount;//支付订单总数
    private int ShopId;

    public int getPayCategory() {
        return PayCategory;
    }

    public void setPayCategory(int payCategory) {
        PayCategory = payCategory;
    }

    public int getPayBizType() {
        return PayBizType;
    }

    public void setPayBizType(int payBizType) {
        PayBizType = payBizType;
    }

    public double getPayMent() {
        return PayMent;
    }

    public void setPayMent(double payMent) {
        PayMent = payMent;
    }

    public int getPayType() {
        return PayType;
    }

    public void setPayType(int payType) {
        PayType = payType;
    }

    public int getShopId() {
        return ShopId;
    }

    public void setShopId(int shopId) {
        ShopId = shopId;
    }

    public int getOrderCount() {
        return OrderCount;
    }

    public void setOrderCount(int orderCount) {
        OrderCount = orderCount;
    }
}
