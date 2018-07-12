package com.cesaas.android.pos.inventory.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Author FGB
 * Description
 * Created at 2017/8/28 15:46
 * Version 1.0
 */

public class InventoryDetailsBean implements Serializable{

    private String No;
    private String CRName;
    private String SubmitName;
    private String CheckName;
    private String CheckDate;
    private String SubmitDate;
    private String CRDate;
    private String InvertoryDay;
    private int ShopId;
    private String ShopName;

    public List<GoodsShelves> GoodsShelves;

    public int getShopId() {
        return ShopId;
    }

    public void setShopId(int shopId) {
        ShopId = shopId;
    }

    public String getShopName() {
        return ShopName;
    }

    public void setShopName(String shopName) {
        ShopName = shopName;
    }

    public String getNo() {
        return No;
    }

    public void setNo(String no) {
        No = no;
    }

    public String getCRName() {
        return CRName;
    }

    public void setCRName(String CRName) {
        this.CRName = CRName;
    }

    public String getSubmitName() {
        return SubmitName;
    }

    public void setSubmitName(String submitName) {
        SubmitName = submitName;
    }

    public String getCheckName() {
        return CheckName;
    }

    public void setCheckName(String checkName) {
        CheckName = checkName;
    }

    public String getCheckDate() {
        return CheckDate;
    }

    public void setCheckDate(String checkDate) {
        CheckDate = checkDate;
    }

    public String getSubmitDate() {
        return SubmitDate;
    }

    public void setSubmitDate(String submitDate) {
        SubmitDate = submitDate;
    }

    public String getCRDate() {
        return CRDate;
    }

    public void setCRDate(String CRDate) {
        this.CRDate = CRDate;
    }

    public String getInvertoryDay() {
        return InvertoryDay;
    }

    public void setInvertoryDay(String invertoryDay) {
        InvertoryDay = invertoryDay;
    }

    public class GoodsShelves implements Serializable{
        private String Name;
        private int ShelvesId;
        private int Num;


        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public int getShelvesId() {
            return ShelvesId;
        }

        public void setShelvesId(int shelvesId) {
            ShelvesId = shelvesId;
        }

        public int getNum() {
            return Num;
        }

        public void setNum(int num) {
            Num = num;
        }
    }
}
