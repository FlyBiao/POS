package com.cesaas.android.pos.inventory.bean;

import java.io.Serializable;

/**
 * Author FGB
 * Description
 * Created at 2017/8/29 16:44
 * Version 1.0
 */

public class InventoryRecordBean implements Serializable {

    private String no;
    private int count;

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
