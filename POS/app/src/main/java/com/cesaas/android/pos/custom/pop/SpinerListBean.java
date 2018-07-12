package com.cesaas.android.pos.custom.pop;

import java.io.Serializable;

/**
 * Author FGB
 * Description
 * Created at 2017/8/29 9:21
 * Version 1.0
 */

public class SpinerListBean implements Serializable {
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
