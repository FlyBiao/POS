package com.cesaas.android.pos.net.xutils.net;

import android.content.Context;
import android.util.Log;

import com.cesaas.android.pos.bean.ResultActivityResultBean;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.net.xutils.BaseNet;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：
 * 创建日期：2016/12/21 17:14
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class GetActivityResultNet extends BaseNet {
    public GetActivityResultNet(Context context) {
        super(context, true);
        this.uri = "Pos/Sw/PromotionActivity/GetActivityResult";
    }

    public void setData(int activityId,JSONArray styleArray) {
        try {
            data.put("ActivityId", activityId);
            data.put("Styles", styleArray);
            data.put("UserTicket", AbPrefsUtil.getInstance().getString(Constant.SPF_TOKEN));

        } catch (Exception e) {
            e.printStackTrace();
        }
        mPostNet();
    }

    @Override
    protected void mSuccess(String rJson) {
        super.mSuccess(rJson);
//        Log.i(Constant.TAG,"ActivityResult："+rJson);
        Gson gson = new Gson();
        ResultActivityResultBean lbean = gson.fromJson(rJson, ResultActivityResultBean.class);
        EventBus.getDefault().post(lbean);
    }

    @Override
    protected void mFail(HttpException e, String err) {
        super.mFail(e, err);
        Log.i(Constant.TAG, "营销活动=" + e + "..=err=" + err);
    }
}
