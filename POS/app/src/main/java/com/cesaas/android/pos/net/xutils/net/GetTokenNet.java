package com.cesaas.android.pos.net.xutils.net;

import android.content.Context;
import android.util.Log;

import com.cesaas.android.pos.bean.ResultGetTokenBean;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.net.xutils.BaseNet;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;



/**
 * Author FGB
 * Description 获取融云Token
 * Created at 2017/9/6 17:39
 * Version 1.0
 */

public class GetTokenNet extends BaseNet {

    public GetTokenNet(Context context) {
        super(context, true);
        this.uri="User/Sw/User/GetToken";
    }

    public void setData() {
        try {
            data.put("UserTicket", AbPrefsUtil.getInstance().getString(Constant.SPF_TOKEN));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mPostNet(); // 开始请求网络
    }

    @Override
    protected void mSuccess(String rJson) {
        super.mSuccess(rJson);
        Log.i(Constant.TAG, "RongCloud"+rJson);
        Gson gson = new Gson();
        ResultGetTokenBean msg = gson.fromJson(rJson, ResultGetTokenBean.class);
        EventBus.getDefault().post(msg);
    }

    @Override
    protected void mFail(HttpException e, String err) {
        super.mFail(e, err);
        Log.i(Constant.TAG, "RongCloud"+e+"==err==="+err);
    }
}
