package com.cesaas.android.pos.inventory.net;

import android.content.Context;
import android.util.Log;

import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.inventory.bean.ResultConfirmDiffBean;
import com.cesaas.android.pos.net.xutils.BaseNet;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.cesaas.android.pos.utils.JsonUtils;
import com.lidroid.xutils.exception.HttpException;

import org.greenrobot.eventbus.EventBus;


/**
 * Author FGB
 * Description 确认差异
 * Created at 2017/9/1 16:23
 * Version 1.0
 */

public class ConfirmDiffNet extends BaseNet {
    public ConfirmDiffNet(Context context) {
        super(context, true);
        this.uri="Distribution/Sw/Inventory/Confirm";
    }

    public void setData(int Id){
        try {
            data.put("Id",Id);
            data.put("UserTicket", AbPrefsUtil.getInstance().getString(Constant.SPF_TOKEN));

        } catch (Exception e) {
            e.printStackTrace();
        }
        mPostNet();
    }

    @Override
    protected void mSuccess(String rJson) {
        super.mSuccess(rJson);
        Log.i(Constant.TAG,"确认差异："+rJson);
        ResultConfirmDiffBean bean= JsonUtils.fromJson(rJson,ResultConfirmDiffBean.class);
        EventBus.getDefault().post(bean);
    }

    @Override
    protected void mFail(HttpException e, String err) {
        super.mFail(e, err);
        Log.i(Constant.TAG,err);
    }
}
