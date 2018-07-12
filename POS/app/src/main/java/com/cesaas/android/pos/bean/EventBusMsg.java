package com.cesaas.android.pos.bean;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：
 * 创建日期：2016/12/23 10:03
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class EventBusMsg {

    public double giftsPrice;
    public boolean isSuccess=false;
    public int styleType=2;
    private String BarcodeId;

    public String getBarcodeId() {
        return BarcodeId;
    }

    public void setBarcodeId(String barcodeId) {
        BarcodeId = barcodeId;
    }

    public int getStyleType() {
        return styleType;
    }

    public void setStyleType(int styleType) {
        this.styleType = styleType;
    }

    public double getGiftsPrice() {
        return giftsPrice;
    }

    public void setGiftsPrice(double giftsPrice) {
        this.giftsPrice = giftsPrice;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
