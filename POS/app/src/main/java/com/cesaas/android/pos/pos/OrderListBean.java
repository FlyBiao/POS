package com.cesaas.android.pos.pos;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：
 * 创建日期：2016/11/9 17:17
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class OrderListBean {

    public String BarcodeCode;
    public String Title;
    public String ShopStyleId;
    public double Price;
    public int ShopCount;

    public String getBarcodeCode() {
        return BarcodeCode;
    }

    public void setBarcodeCode(String barcodeCode) {
        BarcodeCode = barcodeCode;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getShopStyleId() {
        return ShopStyleId;
    }

    public void setShopStyleId(String shopStyleId) {
        ShopStyleId = shopStyleId;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public int getShopCount() {
        return ShopCount;
    }

    public void setShopCount(int shopCount) {
        ShopCount = shopCount;
    }
}
