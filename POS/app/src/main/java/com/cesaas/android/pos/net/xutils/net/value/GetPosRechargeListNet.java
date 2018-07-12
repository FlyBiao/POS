package com.cesaas.android.pos.net.xutils.net.value;

import android.content.Context;
import android.util.Log;

import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.net.xutils.BaseValueNet;
import com.cesaas.android.pos.storedvalue.bean.ResultCheckUnitBean;
import com.cesaas.android.pos.storedvalue.bean.ResultGetPosRechargeListBean;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;

import org.greenrobot.eventbus.EventBus;

/**
 * Author FGB
 * Description POS充值查询
 * Created at 2017/7/29 15:46
 * Version 1.0
 */
public class GetPosRechargeListNet extends BaseValueNet {
    public GetPosRechargeListNet(Context context) {
        super(context, true);
        this.uri="Wall/SW/Shop/GetPosRechargeList";
    }

    public void setData(String MachineNo,String start_date,String end_date) {
        try {
            data.put("start_date",start_date);
            data.put("end_date",end_date);
            data.put("MachineNo",MachineNo);
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
        ResultGetPosRechargeListBean bean=gson.fromJson(rJson,ResultGetPosRechargeListBean.class);
        EventBus.getDefault().post(bean);
    }

    @Override
    protected void mFail(HttpException e, String err) {
        super.mFail(e, err);
        Log.i(Constant.TAG,"**=HttpException="+e+"..=err="+err);
    }

}
