package com.cesaas.android.pos.bean.order;

import java.io.Serializable;

/**
 * Author FGB
 * Description
 * Created 2017/4/18 14:08
 * Version 1.0
 */
public class Item implements Serializable {

    private double PayMent;
    private String StyleNo;
    private boolean isDetached;
    private int ShopStyleId;
    private int Quantity;
    private int RetailSubId;
    private double ActualConsumption;
    private String BarcodeId;
    private int GetPoint;
    private double ListPrice;
    private int RetailType;
    private int RetailId;
    private int RetailSort;
    private boolean isError;
    private int RefundQuantity;
    private String StyleName;
    private String BarcodeNo;
    private double SellPrice;
    private double CostPrice;

    public void setPayMent(double PayMent) {
        this.PayMent = PayMent;
    }

    public void setStyleNo(String StyleNo) {
        this.StyleNo = StyleNo;
    }

    public void setIsDetached(boolean isDetached) {
        this.isDetached = isDetached;
    }

    public void setShopStyleId(int ShopStyleId) {
        this.ShopStyleId = ShopStyleId;
    }

    public void setQuantity(int Quantity) {
        this.Quantity = Quantity;
    }

    public void setRetailSubId(int RetailSubId) {
        this.RetailSubId = RetailSubId;
    }

    public void setActualConsumption(double ActualConsumption) {
        this.ActualConsumption = ActualConsumption;
    }

    public void setBarcodeId(String BarcodeId) {
        this.BarcodeId = BarcodeId;
    }

    public void setGetPoint(int GetPoint) {
        this.GetPoint = GetPoint;
    }

    public void setListPrice(double ListPrice) {
        this.ListPrice = ListPrice;
    }

    public void setRetailType(int RetailType) {
        this.RetailType = RetailType;
    }

    public void setRetailId(int RetailId) {
        this.RetailId = RetailId;
    }

    public void setRetailSort(int RetailSort) {
        this.RetailSort = RetailSort;
    }

    public void setIsError(boolean isError) {
        this.isError = isError;
    }

    public void setRefundQuantity(int RefundQuantity) {
        this.RefundQuantity = RefundQuantity;
    }

    public void setStyleName(String StyleName) {
        this.StyleName = StyleName;
    }

    public void setBarcodeNo(String BarcodeNo) {
        this.BarcodeNo = BarcodeNo;
    }

    public void setSellPrice(double SellPrice) {
        this.SellPrice = SellPrice;
    }

    public void setCostPrice(double CostPrice) {
        this.CostPrice = CostPrice;
    }

    public double getPayMent() {
        return PayMent;
    }

    public String getStyleNo() {
        return StyleNo;
    }

    public boolean isIsDetached() {
        return isDetached;
    }

    public int getShopStyleId() {
        return ShopStyleId;
    }

    public int getQuantity() {
        return Quantity;
    }

    public int getRetailSubId() {
        return RetailSubId;
    }

    public double getActualConsumption() {
        return ActualConsumption;
    }

    public String getBarcodeId() {
        return BarcodeId;
    }

    public int getGetPoint() {
        return GetPoint;
    }

    public double getListPrice() {
        return ListPrice;
    }

    public int getRetailType() {
        return RetailType;
    }

    public int getRetailId() {
        return RetailId;
    }

    public int getRetailSort() {
        return RetailSort;
    }

    public boolean isIsError() {
        return isError;
    }

    public int getRefundQuantity() {
        return RefundQuantity;
    }

    public String getStyleName() {
        return StyleName;
    }

    public String getBarcodeNo() {
        return BarcodeNo;
    }

    public double getSellPrice() {
        return SellPrice;
    }

    public double getCostPrice() {
        return CostPrice;
    }
}
