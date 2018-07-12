package com.cesaas.android.pos.net.xutils.net;

import android.content.Context;
import android.util.Log;

import com.cesaas.android.pos.bean.PosOrderListBean;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.net.xutils.BaseNet;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：查账Net
 * 创建日期：2016/11/6 21:49
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class CheckAccountsNet extends BaseNet{

    public CheckAccountsNet(Context context) {
        super(context, true);
        this.uri = "Pos/Sw/Retail/PosOrderList";
    }

    public void setData(String start_date,String end_date,int page){
        try {
            data.put("PageIndex", page);
            data.put("PageSize", 50);
            data.put("start_date", start_date);//开始时间
            data.put("end_date", end_date);//结束时间
//            data.put("Sort",dateArray);
            data.put("UserTicket", AbPrefsUtil.getInstance().getString(Constant.SPF_TOKEN));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mPostNet(); // 开始请求网络
    }


    public void setData(String start_date,String end_date,int page,JSONArray dateArray){
        try {
            data.put("PageIndex", page);
            data.put("PageSize", 50);
            data.put("start_date", start_date);//开始时间
            data.put("end_date", end_date);//结束时间
            data.put("Sort",dateArray);
            data.put("UserTicket", AbPrefsUtil.getInstance().getString(Constant.SPF_TOKEN));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mPostNet(); // 开始请求网络
    }

    public void setData(int page,int refundStatus){
        try {
            data.put("PageIndex", page);
            data.put("PageSize", 50);
            data.put("refundStatus", refundStatus);
            data.put("UserTicket", AbPrefsUtil.getInstance().getString(Constant.SPF_TOKEN));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mPostNet(); // 开始请求网络
    }

    @Override
    protected void mSuccess(String rJson) {
        super.mSuccess(rJson);
        Gson gson=new Gson();
        PosOrderListBean lbean=gson.fromJson(rJson,PosOrderListBean.class);
        if(lbean.IsSuccess){
            EventBus.getDefault().post(lbean);
        }else{
            EventBus.getDefault().post(lbean);
        }
    }

    @Override
    protected void mFail(HttpException e, String err) {
        super.mFail(e, err);
        Log.i(Constant.TAG, "CheckAccountsNet===" + e + "********=err==" + err);
    }

}
