package com.cesaas.android.pos.net.xutils.net;

import android.content.Context;
import android.util.Log;

import com.cesaas.android.pos.bean.ResultGetOrderBean;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.net.xutils.BaseNet;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;



/**
 * 订单详情
 * @author FGB
 *
 */
public class GetOrderNet extends BaseNet {

	public GetOrderNet(Context context) {
		super(context, true);
		this.uri="Pos/Sw/Order/GetOrder";
	}
	
	public void setData(String TradeId){
		try {
			data.put("TradeId",TradeId);
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
		ResultGetOrderBean lbean = gson.fromJson(rJson, ResultGetOrderBean.class);

		if (lbean.IsSuccess) {
			EventBus.getDefault().post(lbean);
		} else {
			EventBus.getDefault().post(lbean);
		}
	}

	@Override
	protected void mFail(HttpException e, String err) {
		super.mFail(e, err);
		Log.i(Constant.TAG,"**=HttpException="+e+"..=err="+err);
	}

}
