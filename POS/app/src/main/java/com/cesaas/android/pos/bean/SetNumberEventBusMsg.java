package com.cesaas.android.pos.bean;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：
 * 创建日期：2016/12/26 21:52
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class SetNumberEventBusMsg {

    public int ShopCount;
    private String BarcodeId;
    public String getBarcodeId() {
        return BarcodeId;
    }

    public void setBarcodeId(String barcodeId) {
        BarcodeId = barcodeId;
    }

    public int getShopCount() {
        return ShopCount;
    }

    public void setShopCount(int shopCount) {
        ShopCount = shopCount;
    }
}
