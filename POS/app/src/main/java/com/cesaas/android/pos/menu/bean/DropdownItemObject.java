package com.cesaas.android.pos.menu.bean;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：
 * 创建日期：2016/10/14 14:17
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class DropdownItemObject {
    public int id;
    public String text;
    public String value;
    public String suffix;

    public DropdownItemObject(String text,int id,  String value) {
        this.text = text;
        this.id = id;
        this.value = value;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
