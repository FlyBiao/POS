package com.cesaas.android.pos.net.xutils.net.value;

import android.content.Context;
import android.util.Log;

import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.net.xutils.BaseNet;
import com.cesaas.android.pos.net.xutils.BaseValueNet;
import com.cesaas.android.pos.storedvalue.bean.ResultCheckUnitBean;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;

import org.greenrobot.eventbus.EventBus;

/**
 * Author FGB
 * Description 店铺储值配置检查
 * Created at 2017/7/19 20:11
 * Version 1.0
 */

public class CheckUnitNet extends BaseValueNet {
    public CheckUnitNet(Context context) {
        super(context, true);
        this.uri="Wall/SW/Shop/CheckUnit";
    }


    public void setData() {
        try {
            data.put("UserTicket", AbPrefsUtil.getInstance().getString(Constant.SPF_TOKEN));

        } catch (Exception e) {
            e.printStackTrace();
        }
        mPostNet();
    }


    @Override
    protected void mSuccess(String rJson) {
        super.mSuccess(rJson);
        Gson gson=new Gson();
        ResultCheckUnitBean bean=gson.fromJson(rJson,ResultCheckUnitBean.class);
        EventBus.getDefault().post(bean);
    }

    @Override
    protected void mFail(HttpException e, String err) {
        super.mFail(e, err);
        Log.i(Constant.TAG,"**=HttpException="+e+"..=err="+err);
    }

}