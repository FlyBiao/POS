package com.cesaas.android.pos.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：排序Bean
 * 创建日期：2016/11/11 16:46
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class SortBean {

    private String Field;//排序时间
    private String Value;//用 DESC 表示按倒序排序(即:从大到小排序) 用 ACS 表示按正序排序(即:从小到大排序)

    public JSONObject getSort(){
        JSONObject jb = new JSONObject();
        try {
            jb.put("Field", getField());
            jb.put("Value",getValue());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jb;
    }

    public String getField() {
        return Field;
    }

    public void setField(String field) {
        Field = field;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }
}
