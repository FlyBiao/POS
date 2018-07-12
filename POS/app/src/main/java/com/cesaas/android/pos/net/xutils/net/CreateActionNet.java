package com.cesaas.android.pos.net.xutils.net;

import android.content.Context;
import android.util.Log;

import com.cesaas.android.pos.bean.CreateActionBean;
import com.cesaas.android.pos.bean.PayLogBean;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.net.xutils.BaseNet;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.cesaas.android.pos.utils.JsonUtils;
import com.lidroid.xutils.exception.HttpException;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：pos    CreateAction
 * 创建日期：2016/12/13 10:25
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class CreateActionNet extends BaseNet {

    public CreateActionNet(Context context) {
        super(context, true);
        this.uri="Pos/Sw/Log/CreateAction";
    }

    public void setData(){
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
        CreateActionBean bean= JsonUtils.fromJson(rJson,CreateActionBean.class);
        EventBus.getDefault().post(bean);
    }

    @Override
    protected void mFail(HttpException e, String err) {
        super.mFail(e, err);
        Log.i(Constant.TAG,"**=HttpException="+e+"..=err="+err);
    }

}
