package com.cesaas.android.pos.net.xutils.net;

import android.content.Context;
import android.util.Log;

import com.cesaas.android.pos.bean.ResultCreatePayBean;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.net.xutils.BankBaseNet;
import com.cesaas.android.pos.net.xutils.BasePayNet;
import com.cesaas.android.pos.pos.bank.ResultBankInfoBean;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.cesaas.android.pos.utils.JsonUtils;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;


/**
 * 查询银行卡信息
 * @author FGB
 *
 */
public class BankCardInfoNet extends BankBaseNet {
	private String bankcard;
	public BankCardInfoNet(Context context,String bankcard) {
		super(context, true);
		this.bankcard=bankcard;
		this.uri="bankcardinfo/query?key=6e20cacde1d00204c0029a17f7659576&bankcard="+bankcard;
	}
	
	public void setData(){
		mPostNet(); // 开始请求网络
	}

	@Override
	protected void mSuccess(String rJson) {
		super.mSuccess(rJson);
		ResultBankInfoBean bean= JsonUtils.fromJson(rJson,ResultBankInfoBean.class);
		EventBus.getDefault().post(bean);
	}

	@Override
	protected void mFail(HttpException e, String err) {
		super.mFail(e, err);
		Log.i(Constant.TAG,"**=HttpException="+e+"..=err="+err);
	}

}
