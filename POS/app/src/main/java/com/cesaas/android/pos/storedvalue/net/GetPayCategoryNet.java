package com.cesaas.android.pos.storedvalue.net;

import android.content.Context;
import android.util.Log;

import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.net.xutils.BaseValueNet;
import com.cesaas.android.pos.storedvalue.bean.ResultPayCategoryBean;
import com.cesaas.android.pos.storedvalue.bean.ResultPayCategoryListBean;
import com.cesaas.android.pos.utils.ACache;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;

import org.greenrobot.eventbus.EventBus;

/**
 * Author FGB
 * Description
 * Created at 2017/9/12 11:50
 * Version 1.0
 */

public class GetPayCategoryNet extends BaseValueNet {
    private ACache aCache;
    private int type;
    public GetPayCategoryNet(Context context, ACache aCache) {
        super(context, true);
        this.uri="Order/Sw/Retail/PayCategoryList";
        this.aCache=aCache;
    }

    public GetPayCategoryNet(Context context, ACache aCache,int type) {
        super(context, true);
        this.uri="Order/Sw/Retail/PayCategoryList";
        this.aCache=aCache;
        this.type=type;
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
        aCache.put("GetPayCategory",rJson);
        if(type==102){
            ResultPayCategoryListBean bean= gson.fromJson(rJson,ResultPayCategoryListBean.class);
            EventBus.getDefault().post(bean);
        }else{
            ResultPayCategoryBean bean= gson.fromJson(rJson,ResultPayCategoryBean.class);
            EventBus.getDefault().post(bean);
        }
    }

    @Override
    protected void mFail(HttpException e, String err) {
        super.mFail(e, err);
        Log.i(Constant.TAG,"**=HttpException="+e+"..=err="+err);
    }
}
