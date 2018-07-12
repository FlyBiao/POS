package com.cesaas.android.pos.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：款号
 * 创建日期：2016/12/21 14:05
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class Styles {

    public int ShopStyleId;//商品款号ID
    public double StylePrice;//款号价格
    public double PayMent;//支付金额
    public int Quantity;//数量
    public String BarcodeId;//条形码ID
    private int StyleType=2;//商品类型 1:积分商品，2:销售商品，3:赠品。

    public JSONObject getStyleArray() {
        JSONObject json = new JSONObject();
        try {
            json.put("ShopStyleId", getShopStyleId());
            json.put("StylePrice",getStylePrice());
            json.put("PayMent", getPayMent());
            json.put("Quantity", getQuantity());
            json.put("BarcodeId",getBarcodeId());
            json.put("StyleType",getStyleType());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    public int getStyleType() {
        return StyleType;
    }

    public void setStyleType(int styleType) {
        StyleType = styleType;
    }

    public int getShopStyleId() {
        return ShopStyleId;
    }

    public void setShopStyleId(int shopStyleId) {
        ShopStyleId = shopStyleId;
    }

    public double getStylePrice() {
        return StylePrice;
    }

    public void setStylePrice(double stylePrice) {
        StylePrice = stylePrice;
    }

    public double getPayMent() {
        return PayMent;
    }

    public void setPayMent(double payMent) {
        PayMent = payMent;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public String getBarcodeId() {
        return BarcodeId;
    }

    public void setBarcodeId(String barcodeId) {
        BarcodeId = barcodeId;
    }
}
