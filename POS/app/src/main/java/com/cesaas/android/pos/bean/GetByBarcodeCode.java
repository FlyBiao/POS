package com.cesaas.android.pos.bean;

import java.io.Serializable;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：在线下单订单商品
 * 创建日期：2016/10/10 21:38
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class GetByBarcodeCode implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String Title;//商品名称
    public BarcodeInfo BarcodeInfo;//商品尺码描述信息
    private int ShopStyleId;//商品id
    private double Price;//商品价格
    private double PayMent;//商品支付金额
    private String StyleCode;//商品款号
    private String BarcodeCode;//商品sku
    private String BarcodeId;//商品sku_id
    private String ImageUrl;//商品图片
    private int ShopCount=1;//商品总数

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


    public class BarcodeInfo {

        public String Name1;
        public String Value1;
        public String getName1() {
            return Name1;
        }
        public void setName1(String name1) {
            Name1 = name1;
        }
        public String getValue1() {
            return Value1;
        }
        public void setValue1(String value1) {
            Value1 = value1;
        }



    }
}
