package com.cesaas.android.pos.bean;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：
 * 创建日期：2016/12/26 21:54
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class SetPriceEventBusMsg {

    private double price;
    private String BarcodeId;

    public String getBarcodeId() {
        return BarcodeId;
    }

    public void setBarcodeId(String barcodeId) {
        BarcodeId = barcodeId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
