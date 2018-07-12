package com.cesaas.android.pos.net.xutils.net;

import android.content.Context;
import android.util.Log;

import com.cesaas.android.pos.bean.PayLogBean;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.net.xutils.BaseNet;
import com.cesaas.android.pos.net.xutils.TestBaseNet;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.cesaas.android.pos.utils.JsonUtils;
import com.lidroid.xutils.exception.HttpException;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：pos 支付日志
 * 创建日期：2016/12/13 10:25
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class PosPayLogNet extends BaseNet {

    public PosPayLogNet(Context context) {
        super(context, true);
        this.uri="Pos/Sw/Log/LogAsync";
    }

    public void setData(String LogType, String obj, String remark){
        try {
            data.put("LogType",LogType);
            data.put("Description",obj);
            data.put("Remark",remark);
            data.put("UserTicket", AbPrefsUtil.getInstance().getString(Constant.SPF_TOKEN));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mPostNet(); // 开始请求网络
    }

    @Override
    protected void mSuccess(String rJson) {
        super.mSuccess(rJson);
        Log.i("test", "支付日志:"+rJson);
        PayLogBean bean= JsonUtils.fromJson(rJson,PayLogBean.class);
        EventBus.getDefault().post(bean);
    }

    @Override
    protected void mFail(HttpException e, String err) {
        super.mFail(e, err);
        Log.i(Constant.TAG,"**=HttpException="+e+"..=err="+err);
    }

}
