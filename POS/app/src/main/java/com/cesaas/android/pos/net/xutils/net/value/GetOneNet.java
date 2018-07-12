package com.cesaas.android.pos.net.xutils.net.value;

import android.content.Context;
import android.util.Log;

import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.net.xutils.BaseNet;
import com.cesaas.android.pos.net.xutils.BaseValueNet;
import com.cesaas.android.pos.storedvalue.bean.ResultVipInfoBean;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;

import org.greenrobot.eventbus.EventBus;

/**
 * Author FGB
 * Description 获取会员储值账户
 * Created at 2017/7/19 17:56
 * Version 1.0
 */

public class GetOneNet extends BaseValueNet {
    public GetOneNet(Context context) {
        super(context, true);
        this.uri="Wall/SW/Vip/GetOne";
    }


    public void setData(String VipMobile) {
        try {
            data.put("VipMobile", VipMobile);
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
        ResultVipInfoBean bean=gson.fromJson(rJson,ResultVipInfoBean.class);
        EventBus.getDefault().post(bean);
    }

    @Override
    protected void mFail(HttpException e, String err) {
        super.mFail(e, err);
        Log.i(Constant.TAG,"**=HttpException="+e+"..=err="+err);
    }

}
