package com.cesaas.android.pos.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：在线下单OrderItemBean
 * 创建日期：2016/10/10 21:53
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class OrderItemBean {
    private int ShopStyleId;//商品ID
    private String BarcodeId;//条形码ID
    private String BarcodeNo;//条形码编号
    private int Quantity;//数量
    private double PayMent;//支付金额
    private double StylePrice;//商品价格
    private int SaleType;//销售类型 【0正常销售，1赠品，2退货】
    private String Title;//商品标题
//    private String Attr;//商品规格

    public JSONObject getOrderItem() {
        JSONObject json = new JSONObject();
        try {
            json.put("ShopStyleId", getShopStyleId());
            json.put("BarcodeId", getBarcodeId());
            json.put("BarcodeNo",getBarcodeNo());
            json.put("Quantity", getQuantity());
            json.put("PayMent", getPayMent());
            json.put("StylePrice", getStylePrice());
            json.put("SaleType", getSaleType());
            json.put("Title", getTitle());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }


    public int getSaleType() {
        return SaleType;
    }

    public void setSaleType(int saleType) {
        SaleType = saleType;
    }

    public String getBarcodeId() {
        return BarcodeId;
    }


    public void setBarcodeId(String barcodeId) {
        BarcodeId = barcodeId;
    }


    public String getBarcodeNo() {
        return BarcodeNo;
    }


    public void setBarcodeNo(String barcodeNo) {
        BarcodeNo = barcodeNo;
    }


    public int getQuantity() {
        return Quantity;
    }


    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public double getStylePrice() {
        return StylePrice;
    }

    public void setStylePrice(double stylePrice) {
        StylePrice = stylePrice;
    }

    public int getShopStyleId() {
        return ShopStyleId;
    }


    public void setShopStyleId(int shopStyleId) {
        ShopStyleId = shopStyleId;
    }


//    public String getAttr() {
//        return Attr;
//    }
//
//
//    public void setAttr(String attr) {
//        Attr = attr;
//    }


    public double getPayMent() {
        return PayMent;
    }


    public void setPayMent(double payMent) {
        PayMent = payMent;
    }


    public String getTitle() {
        return Title;
    }


    public void setTitle(String title) {
        Title = title;
    }


}
