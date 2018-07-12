package com.cesaas.android.pos.net.xutils.net.value;

import android.content.Context;
import android.util.Log;

import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.net.xutils.BaseNet;
import com.cesaas.android.pos.net.xutils.BaseValueNet;
import com.cesaas.android.pos.storedvalue.bean.ResultAddTradeBean;
import com.cesaas.android.pos.storedvalue.bean.ResultTradeListBean;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;

/**
 * Author FGB
 * Description 充值记录
 * Created at 2017/7/20 9:09
 * Version 1.0
 */

public class GetTradeListNet extends BaseValueNet {
    public GetTradeListNet(Context context) {
        super(context, true);
        this.uri="Wall/SW/Vip/List";
    }

    public void setData(int PageIndex , JSONArray Filter,JSONArray Sort) {
        try {
            data.put("Filter",Filter);
            data.put("Sort",Sort);
            data.put("PageSize",50);
            data.put("PageIndex",PageIndex);
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
        ResultTradeListBean bean=gson.fromJson(rJson,ResultTradeListBean.class);
        EventBus.getDefault().post(bean);
    }

    @Override
    protected void mFail(HttpException e, String err) {
        super.mFail(e, err);
        Log.i(Constant.TAG,"**=HttpException="+e+"..=err="+err);
    }

}