package com.cesaas.android.pos.net.xutils.net;

import android.content.Context;
import android.util.Log;

import com.cesaas.android.pos.activity.cashier.CheckAccountsListActivity;
import com.cesaas.android.pos.bean.PosOfflineRefundBean;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.net.xutils.BaseNet;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.cesaas.android.pos.utils.Skip;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Author FGB
 * Description
 * Created at 2017/6/3 15:40
 * Version 1.0
 */

public class ReturnGoodsNet extends BaseNet {
    public ReturnGoodsNet(Context context) {
        super(context, true);
        this.uri="Pos/Sw/Retail/OfflineRefund";
    }

    public void setData(String TraceAuditNumber, double ConsumeAmount, String OrderId, int PayType, String enCode, JSONArray SubOrderId){
        try {
            data.put("TraceAuditNumber",TraceAuditNumber);//凭证号
            data.put("ConsumeAmount",ConsumeAmount);//消费金额
            data.put("RetailId",OrderId);//支付订单号
            data.put("PayType",PayType);//支付方式
            data.put("EnCode",enCode);//设备EN号
            data.put("SubOrderId",SubOrderId);
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
        PosOfflineRefundBean bean=gson.fromJson(rJson,PosOfflineRefundBean.class);
        EventBus.getDefault().post(bean);
    }

    @Override
    protected void mFail(HttpException e, String err) {
        super.mFail(e, err);
        Log.i(Constant.TAG,"err=="+err);
    }
}
