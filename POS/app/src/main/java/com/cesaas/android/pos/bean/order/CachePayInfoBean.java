package com.cesaas.android.pos.bean.order;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Author FGB
 * Description 缓存支付信息Bean
 * Created at 2017/11/15 15:43
 * Version 1.0
 */

public class CachePayInfoBean implements Serializable{
    private String PayNo;

    public String getPayNo() {
        return PayNo;
    }

    public void setPayNo(String payNo) {
        PayNo = payNo;
    }

}
