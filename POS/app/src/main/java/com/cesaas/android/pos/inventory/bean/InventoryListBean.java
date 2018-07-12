package com.cesaas.android.pos.inventory.bean;

import java.io.Serializable;

/**
 * Author FGB
 * Description
 * Created at 2017/8/28 9:35
 * Version 1.0
 */

public class InventoryListBean implements Serializable {

    private int Id;
    private String No;
    private String ShopId;
    private String ShopName;
    private String InvertoryDay;
    private int Num;
    private int InvertoryType;
    private int IsDiff;
    private String CRName;
    private int DiffNum;
    private int Status;


    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public int getDiffNum() {
        return DiffNum;
    }

    public void setDiffNum(int diffNum) {
        DiffNum = diffNum;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getShopId() {
        return ShopId;
    }

    public void setShopId(String shopId) {
        ShopId = shopId;
    }

    public String getShopName() {
        return ShopName;
    }

    public void setShopName(String shopName) {
        ShopName = shopName;
    }

    public String getInvertoryDay() {
        return InvertoryDay;
    }

    public void setInvertoryDay(String invertoryDay) {
        InvertoryDay = invertoryDay;
    }

    public int getNum() {
        return Num;
    }

    public void setNum(int num) {
        Num = num;
    }

    public int getInvertoryType() {
        return InvertoryType;
    }

    public void setInvertoryType(int invertoryType) {
        InvertoryType = invertoryType;
    }

    public int getIsDiff() {
        return IsDiff;
    }

    public void setIsDiff(int isDiff) {
        IsDiff = isDiff;
    }

    public String getCRName() {
        return CRName;
    }

    public void setCRName(String CRName) {
        this.CRName = CRName;
    }

    public String getNo() {
        return No;
    }

    public void setNo(String no) {
        No = no;
    }

}
