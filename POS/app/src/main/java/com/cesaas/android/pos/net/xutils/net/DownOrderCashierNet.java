package com.cesaas.android.pos.net.xutils.net;

import android.content.Context;
import android.util.Log;

import com.cesaas.android.pos.bean.ResultCreateFromStoreBean;
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
 * 描    述：下单收银网络请求
 * 创建日期：2016/11/8 14:54
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class DownOrderCashierNet extends BaseNet {
    public DownOrderCashierNet(Context context) {
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

    //会员&优惠券
    public void setData(String VipId, String VipCard, String MemberId,String NickName,String couponId,double PromotionAmount, double PayMent, double TotalPrice, String SyncCode, String WorkShift, String Remark,String Unique, String Weather,String Cashier,JSONArray jsonArray){
        try{
            data.put("VipId",VipId);//会员ID
            data.put("VipCard",VipCard);//会员卡
            data.put("MemberId",MemberId);//会员ID
            data.put("NickName",NickName);//会员昵称
            data.put("CouponId",couponId);//优惠券ID
            data.put("CounponAmount",PromotionAmount);
            data.put("PayMent",PayMent);//支付金额
            data.put("TotalPrice",TotalPrice);//总金额
            data.put("SyncCode",SyncCode);//单号
            data.put("WorkShift",WorkShift);//班次
            data.put("Remark",Remark);//备注
            data.put("Unique",Unique);//加密唯一编码:(生成规则：SwApp+单号OrderSyncCode)
            data.put("Weather",Weather);//天气
            data.put("Cashier",Cashier);//收银员
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


    //==================================================================================
    public void setData(String OrderSyncCode,String Unique,int OrderType,double PayMent,double TotalPrice,JSONArray jsonArray,String NickName,int ActivityId,String Province,String District,String City,String Address){
        try {
            data.put("OrderSyncCode",OrderSyncCode);//单号
            data.put("Unique",Unique);//加密唯一编码:(生成规则：SwApp+单号OrderSyncCode)
            data.put("OrderType",OrderType);//订单类型:1积分订单 2付款订单
            data.put("PayMent",PayMent);//支付金额
            data.put("TotalPrice", TotalPrice);//总金额
            data.put("NickName", NickName);//会员昵称
            data.put("ActivityId",ActivityId);//营销活动ID省
            data.put("Province", Province);//
            data.put("District", District);//区
            data.put("City", City);//市
            data.put("Address", Address);//地址
            data.put("EnCode",AbPrefsUtil.getInstance().getString("enCode"));//设备EN号
            data.put("OrderItem", jsonArray);//OrderItem
            data.put("UserTicket",AbPrefsUtil.getInstance().getString(Constant.SPF_TOKEN));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mPostNet(); // 开始请求网络
    }

    public void setData(String OrderSyncCode,String Unique,int OrderType,double PayMent,double TotalPrice,JSONArray jsonArray,String NickName,String Province,String District,String City,String Address){
        try {
            data.put("OrderSyncCode",OrderSyncCode);//单号
            data.put("Unique",Unique);//加密唯一编码:(生成规则：SwApp+单号OrderSyncCode)
            data.put("OrderType",OrderType);//订单类型:1积分订单 2付款订单
            data.put("PayMent",PayMent);//支付金额
            data.put("TotalPrice", TotalPrice);//总金额
            data.put("NickName", NickName);//会员昵称
            data.put("Province", Province);//省
            data.put("District", District);//区
            data.put("City", City);//市
            data.put("Address", Address);//地址
            data.put("EnCode",AbPrefsUtil.getInstance().getString("enCode"));//设备EN号
            data.put("OrderItem", jsonArray);//OrderItem
            data.put("UserTicket",AbPrefsUtil.getInstance().getString(Constant.SPF_TOKEN));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mPostNet(); // 开始请求网络
    }

    public void setData(String OrderSyncCode,String Unique,double PayMent,
                        String NickName,double TotalPrice,String VipId,String OpenId,
                        String Mobile,String Province,String District,String City,String Address,
                        JSONArray jsonArray,int ActivityId){
        try {
            data.put("OrderSyncCode",OrderSyncCode);//单号
            data.put("Unique",Unique);//加密唯一编码:(生成规则：SwApp+单号OrderSyncCode)
            data.put("OrderType",2);//订单类型:1积分订单 2付款订单
//            data.put("CouponId", "73");//优惠券ID
            data.put("PayMent",PayMent);//支付金额
            data.put("NickName", NickName);//会员昵称
            data.put("TotalPrice", TotalPrice);//总金额
            data.put("VipId", VipId);//会员ID
            data.put("OpenId", OpenId);//会员No
            data.put("Mobile", Mobile);//会员手机号
            data.put("Province", Province);//省
            data.put("District", District);//区
            data.put("City", City);//市
            data.put("Address", Address);//地址
            data.put("OrderItem", jsonArray);//OrderArray
            data.put("EnCode",AbPrefsUtil.getInstance().getString("enCode"));//设备EN号
            data.put("ActivityId",ActivityId);//营销活动ID
            data.put("UserTicket", AbPrefsUtil.getInstance().getString(Constant.SPF_TOKEN));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mPostNet(); // 开始请求网络
    }

    public void setData(String OrderSyncCode,String Unique,double PayMent,
                        String NickName,double TotalPrice,String VipId,String OpenId,
                        String Mobile,String Province,String District,String City,String Address,
                        JSONArray jsonArray){
        try {
            data.put("OrderSyncCode",OrderSyncCode);//单号
            data.put("Unique",Unique);//加密唯一编码:(生成规则：SwApp+单号OrderSyncCode)
            data.put("OrderType",2);//订单类型:1积分订单 2付款订单
//            data.put("CouponId", "73");//优惠券ID
            data.put("PayMent",PayMent);//支付金额
            data.put("NickName", NickName);//会员昵称
            data.put("TotalPrice", TotalPrice);//总金额
            data.put("VipId", VipId);//会员ID
            data.put("OpenId", OpenId);//会员No
            data.put("Mobile", Mobile);//会员手机号
            data.put("Province", Province);//省
            data.put("District", District);//区
            data.put("City", City);//市
            data.put("Address", Address);//地址
            data.put("EnCode",AbPrefsUtil.getInstance().getString("enCode"));//设备EN号
            data.put("OrderItem", jsonArray);//OrderArray
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
        Log.i(Constant.TAG,"pos下单:"+rJson);
        ResultCreateFromStoreBean lbean=gson.fromJson(rJson, ResultCreateFromStoreBean.class);
        if(lbean.IsSuccess){
            EventBus.getDefault().post(lbean);
        }else{
            EventBus.getDefault().post(lbean);
        }

    }

    @Override
    protected void mFail(HttpException e, String err) {
        super.mFail(e, err);
        Log.i(Constant.TAG, "Fans===" + e + "********=err==" + err);
    }
}
