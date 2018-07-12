package com.cesaas.android.pos.net.xutils.net;

import android.content.Context;
import android.util.Log;

import com.cesaas.android.pos.bean.ResultTicketIsAvailableBean;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.net.xutils.BaseTicketNet;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：验证优惠券是否可用
 * 创建日期：2016/12/27 14:21
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class TicketIsAvailableNet extends BaseTicketNet {

    public TicketIsAvailableNet(Context context) {
        super(context, true);
        this.uri="Marketing/Sw/Ticket/TicketIsAvailable";
    }

    /**
     * 设置参数数据
     * @param CouponId 优惠券id
     * @param Discount 全局商品折扣
     * @param GoodsArray 商品列表
     */
    public void setData(String CouponId,double Discount,JSONArray GoodsArray){
        try {
            data.put("CouponId",CouponId);
            data.put("Discount",Discount);
            data.put("GoodsArray",GoodsArray);
            data.put("UserTicket", AbPrefsUtil.getInstance().getString(Constant.SPF_TOKEN));

        }catch (Exception e){
            e.printStackTrace();
        }
        //开始请求网络
        mPostNet();
    }

    @Override
    protected void mSuccess(String rJson) {
        super.mSuccess(rJson);
        Log.i(Constant.TAG,"优惠券是否可用"+rJson);
        Gson gson=new Gson();
        ResultTicketIsAvailableBean bean=gson.fromJson(rJson,ResultTicketIsAvailableBean.class);
        EventBus.getDefault().post(bean);
    }

    @Override
    protected void mFail(HttpException e, String err) {
        super.mFail(e, err);
        Log.i(Constant.TAG,"ERROR:"+e+"=err:"+err);
    }
}
