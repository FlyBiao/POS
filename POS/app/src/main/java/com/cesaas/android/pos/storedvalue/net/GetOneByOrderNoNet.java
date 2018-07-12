package com.cesaas.android.pos.storedvalue.net;

import android.content.Context;
import android.util.Log;

import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.net.xutils.BaseValueNet;
import com.cesaas.android.pos.storedvalue.bean.ResultOrderNoBean;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;

import org.greenrobot.eventbus.EventBus;

/**
 * Author FGB
 * Description 查询单条储值记录
 * Created at 2017/7/19 20:11
 * Version 1.0
 */

public class GetOneByOrderNoNet extends BaseValueNet {
    public GetOneByOrderNoNet(Context context) {
        super(context, true);
        this.uri="Wall/SW/Vip/GetOneByOrderNo";
    }

    public void setData(String id) {
        try {
            data.put("OrderNo",id);
            data.put("UserTicket", AbPrefsUtil.getInstance().getString(Constant.SPF_TOKEN));

        } catch (Exception e) {
            e.printStackTrace();
        }
        mPostNet();
    }

    @Override
    protected void mSuccess(String rJson) {
        super.mSuccess(rJson);
        Log.i(Constant.TAG,rJson);
        Gson gson=new Gson();
        ResultOrderNoBean bean=gson.fromJson(rJson,ResultOrderNoBean.class);
        EventBus.getDefault().post(bean);
    }

    @Override
    protected void mFail(HttpException e, String err) {
        super.mFail(e, err);
        Log.i(Constant.TAG,"**=HttpException="+e+"..=err="+err);
    }

}