package com.cesaas.android.pos.net.xutils.net;

import android.content.Context;
import android.util.Log;

import com.cesaas.android.pos.bean.ResultHomeCreateFromStoreBean;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.net.xutils.BaseNet;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;


/**
 * 收银
 * @author FGB
 *
 */
public class CreateFromStoreNet extends BaseNet{

	private static final String TAG = "CreateFromStoreNet";

	public CreateFromStoreNet(Context context) {
		super(context, true);
		this.uri = "Pos/Sw/Retail/CreateOrder";
	}

	//会员
	public void setData(String VipId, String VipCard, String MemberId,String NickName, double PayMent, double TotalPrice, String SyncCode, String WorkShift, String Remark,String Unique, String Weather,String Cashier, String ActivityId, JSONArray jsonArray){
		try{
			data.put("VipId",VipId);//会员ID
			data.put("VipCard",VipCard);//会员卡
			data.put("MemberId",MemberId);//会员ID
			data.put("NickName",NickName);//会员昵称
			data.put("PayMent",PayMent);//支付金额
			data.put("TotalPrice",TotalPrice);//总金额
			data.put("SyncCode",SyncCode);//单号
			data.put("WorkShift",WorkShift);//班次
			data.put("Remark",Remark);//备注
			data.put("Unique",Unique);//加密唯一编码:(生成规则：SwApp+单号OrderSyncCode)
			data.put("Weather",Weather);//天气
			data.put("Cashier",Cashier);//收银员
			data.put("ActivityId",ActivityId);//营销活动ID省
			data.put("EnCode",AbPrefsUtil.getInstance().getString("enCode"));//设备EN号
			data.put("OrderItem", jsonArray);//OrderItem
			data.put("UserTicket",AbPrefsUtil.getInstance().getString(Constant.SPF_TOKEN));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mPostNet(); // 开始请求网络
	}

	//非会员
	public void setData(double PayMent, double TotalPrice, String SyncCode, String WorkShift, String Remark,String Unique, String Weather,String Cashier, String ActivityId, JSONArray jsonArray){
		try{
			data.put("PayMent",PayMent);//支付金额
			data.put("TotalPrice",TotalPrice);//总金额
			data.put("SyncCode",SyncCode);//单号
			data.put("WorkShift",WorkShift);//班次
			data.put("Remark",Remark);//备注
			data.put("Unique",Unique);//加密唯一编码:(生成规则：SwApp+单号OrderSyncCode)
			data.put("Weather",Weather);//天气
			data.put("Cashier",Cashier);//收银员
			data.put("ActivityId",ActivityId);//营销活动ID省
			data.put("EnCode",AbPrefsUtil.getInstance().getString("enCode"));//设备EN号
			data.put("OrderItem", jsonArray);//OrderItem
			data.put("UserTicket",AbPrefsUtil.getInstance().getString(Constant.SPF_TOKEN));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mPostNet(); // 开始请求网络
	}



	//================================
	public void setData(String OrderSyncCode,String Unique,int OrderType,double PayMent,double TotalPrice,JSONArray jsonArray){
		try {
			
			data.put("OrderSyncCode",OrderSyncCode);//单号
			data.put("Unique",Unique);//加密唯一编码:(生成规则：SwApp+单号OrderSyncCode)
			data.put("OrderType",OrderType);//订单类型:1积分订单 2付款订单
			data.put("PayMent",PayMent);//支付金额
			data.put("TotalPrice", TotalPrice);//总金额
			data.put("OrderItem", jsonArray);//OrderItem
			data.put("EnCode",AbPrefsUtil.getInstance().getString("enCode"));//设备EN号
			data.put("UserTicket", AbPrefsUtil.getInstance().getString(Constant.SPF_TOKEN));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		mPostNet(); // 开始请求网络
	}
	
	public void setData(String OrderSyncCode,String Unique,double PayMent,
			String NickName,double TotalPrice, String Mobile, JSONArray jsonArray){
		try {
			data.put("OrderSyncCode",OrderSyncCode);//单号
			data.put("Unique",Unique);//加密唯一编码:(生成规则：SwApp+单号OrderSyncCode)
			data.put("OrderType",2);//订单类型:1积分订单 2付款订单
			//data.put("CouponId", "73");//优惠券ID
			data.put("PayMent",PayMent);//支付金额
			data.put("NickName", NickName);//会员昵称
			data.put("TotalPrice", TotalPrice);//总金额
			data.put("VipId", "123456");//会员ID
			data.put("OpenId", "123456");//会员No
			data.put("Mobile", Mobile);//会员手机号
			data.put("Province", "广东省");//省
			data.put("District", "罗湖区");//区
			data.put("City", "深圳市");//市
			data.put("Address", "广东省深圳市罗湖区");//地址
			data.put("EnCode",AbPrefsUtil.getInstance().getString("enCode"));//设备EN号
			data.put("OrderItem", jsonArray);//OrderArray
			data.put("UserTicket",AbPrefsUtil.getInstance().getString(Constant.SPF_TOKEN));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		mPostNet(); // 开始请求网络
	}

	@Override
	protected void mSuccess(String rJson) {
		super.mSuccess(rJson);
		Gson gson=new Gson();
		Log.i(Constant.TAG,"pos下单:"+rJson);
		ResultHomeCreateFromStoreBean lbean=gson.fromJson(rJson, ResultHomeCreateFromStoreBean.class);
		if(lbean.IsSuccess){
			EventBus.getDefault().post(lbean);
		}else{
			EventBus.getDefault().post(lbean);
		}
		
	}

	@Override
	protected void mFail(HttpException e, String err) {
		super.mFail(e, err);
		Log.i(TAG, "Fans===" + e + "********=err==" + err);
	}

}
