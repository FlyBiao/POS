package com.cesaas.android.pos.net.xutils.net;

import android.content.Context;
import android.util.Log;

import com.cesaas.android.pos.bean.PayCallbackBean;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.net.xutils.BaseNet;
import com.cesaas.android.pos.net.xutils.BasePayNet;
import com.cesaas.android.pos.net.xutils.TestBaseNet;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.cesaas.android.pos.utils.JsonUtils;
import com.lidroid.xutils.exception.HttpException;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：pos 支付回调
 * 创建日期：2016/12/13 10:25
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class PayFormStoreNet extends BaseNet {

    public PayFormStoreNet(Context context) {
        super(context, true);
        this.uri="Pos/Sw/Retail/PayFromStore";
    }

    public void setData(String RetrievalReferenceNumber,String TraceAuditNumber,double ConsumeAmount,String OrderId,int PayType,int IsPractical,String enCode){
        try {
            data.put("RetrievalReferenceNumber",RetrievalReferenceNumber);//参考号
            data.put("TraceAuditNumber",TraceAuditNumber);//凭证号
            data.put("ConsumeAmount",ConsumeAmount);//消费金额
            data.put("RetailId",OrderId);//支付订单号
            data.put("PayType",PayType);//支付类型
            data.put("IsPractical",IsPractical);//是否实销
            data.put("EnCode",enCode);//设备EN号
            data.put("UserTicket", AbPrefsUtil.getInstance().getString(Constant.SPF_TOKEN));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mPostNet(); // 开始请求网络
    }

    @Override
    protected void mSuccess(String rJson) {
        super.mSuccess(rJson);
        Log.i("test", "支付回调:"+rJson);
        PayCallbackBean bean= JsonUtils.fromJson(rJson,PayCallbackBean.class);
        EventBus.getDefault().post(bean);
    }

    @Override
    protected void mFail(HttpException e, String err) {
        super.mFail(e, err);
        Log.i(Constant.TAG,"**=HttpException="+e+"..=err="+err);
    }

}
