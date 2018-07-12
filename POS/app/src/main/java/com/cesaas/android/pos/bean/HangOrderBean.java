package com.cesaas.android.pos.bean;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：pos在线下单挂单Bean
 * 创建日期：2016/11/9 10:55
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class HangOrderBean {

    private String Title;//商品名称
    private int ShopStyleId;//商品id
    private double Price;//商品价格
    private String StyleCode;//商品款号
    private String BarcodeCode;//商品sku
    private String BarcodeId;//商品sku_id
    private String ImageUrl;//商品图片
    private int ShopCount;//商品总数

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public int getShopStyleId() {
        return ShopStyleId;
    }

    public void setShopStyleId(int shopStyleId) {
        ShopStyleId = shopStyleId;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public String getStyleCode() {
        return StyleCode;
    }

    public void setStyleCode(String styleCode) {
        StyleCode = styleCode;
    }

    public String getBarcodeCode() {
        return BarcodeCode;
    }

    public void setBarcodeCode(String barcodeCode) {
        BarcodeCode = barcodeCode;
    }

    public String getBarcodeId() {
        return BarcodeId;
    }

    public void setBarcodeId(String barcodeId) {
        BarcodeId = barcodeId;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public int getShopCount() {
        return ShopCount;
    }

    public void setShopCount(int shopCount) {
        ShopCount = shopCount;
    }
}
