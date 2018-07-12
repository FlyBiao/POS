package com.cesaas.android.pos.net.xutils.net;

import android.content.Context;
import android.util.Log;

import com.cesaas.android.pos.bean.ResultMarketingActivityBean;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.net.xutils.BaseNet;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;

import org.greenrobot.eventbus.EventBus;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：
 * 创建日期：2016/12/21 11:19
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class MarketingActivityNet extends BaseNet {
    public MarketingActivityNet(Context context) {
        super(context, true);
        this.uri="Pos/Sw/PromotionActivity/GetList";
    }

    public void setData(){
        try {
            data.put("UserTicket", AbPrefsUtil.getInstance().getString(Constant.SPF_TOKEN));

        }catch (Exception e){
            e.printStackTrace();
        }
        mPostNet();
    }

    @Override
    protected void mSuccess(String rJson) {
        super.mSuccess(rJson);
        Gson gson=new Gson();
        ResultMarketingActivityBean lbean = gson.fromJson(rJson, ResultMarketingActivityBean.class);
        EventBus.getDefault().post(lbean);
    }

    @Override
    protected void mFail(HttpException e, String err) {
        super.mFail(e, err);
        Log.i(Constant.TAG,"营销活动="+e+"..=err="+err);
    }
}
