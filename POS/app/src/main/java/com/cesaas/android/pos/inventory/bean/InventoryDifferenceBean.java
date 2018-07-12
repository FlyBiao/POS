package com.cesaas.android.pos.inventory.bean;

import com.cesaas.android.pos.bean.BaseBean;

/**
 * Author FGB
 * Description
 * Created at 2017/8/28 14:42
 * Version 1.0
 */

public class InventoryDifferenceBean extends BaseBean {

    private String no;
    private int type;
    private String date;
    private String address;
    private String receiveAddress;
    private int count;

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getReceiveAddress() {
        return receiveAddress;
    }

    public void setReceiveAddress(String receiveAddress) {
        this.receiveAddress = receiveAddress;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
