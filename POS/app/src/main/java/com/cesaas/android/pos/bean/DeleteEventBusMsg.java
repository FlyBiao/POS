package com.cesaas.android.pos.bean;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：
 * 创建日期：2016/12/26 17:37
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class DeleteEventBusMsg {
    private boolean isSuccess = false;
    private double price;
    private String BarcodeId;
    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getBarcodeId() {
        return BarcodeId;
    }

    public void setBarcodeId(String barcodeId) {
        BarcodeId = barcodeId;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
