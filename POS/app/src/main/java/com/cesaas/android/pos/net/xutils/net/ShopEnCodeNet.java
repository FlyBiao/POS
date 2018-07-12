package com.cesaas.android.pos.net.xutils.net;

import android.content.Context;
import android.util.Log;

import com.cesaas.android.pos.bean.ResultPayListBean;
import com.cesaas.android.pos.bean.ShopEnCodeBean;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.net.xutils.BaseNet;
import com.cesaas.android.pos.utils.ACache;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.cesaas.android.pos.utils.JsonUtils;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：获取店铺所属设备encode列表
 * 创建日期：2016/11/6 21:49
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class ShopEnCodeNet extends BaseNet{

    private ACache mCache;
    private AbPrefsUtil prefs;
    public ShopEnCodeNet(Context context,ACache mCache,AbPrefsUtil prefs) {
        super(context, true);
        this.uri = "pos/sw/log/ShopEnCode";
        this.mCache=mCache;
        this.prefs=prefs;
    }

    public void setData(){
        try {
            data.put("UserTicket", AbPrefsUtil.getInstance().getString(Constant.SPF_TOKEN));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mPostNet(); // 开始请求网络
    }

    @Override
    protected void mSuccess(String rJson) {
        super.mSuccess(rJson);
        Log.i(Constant.TAG, "==="+rJson);
        mCache.put(Constant.SHOP_EN_CODE,rJson);
        ShopEnCodeBean bean= JsonUtils.fromJson(mCache.getAsString(Constant.SHOP_EN_CODE),ShopEnCodeBean.class);
        if(bean.TModel!=null){
            if(bean.TModel.indexOf(prefs.getString("enCode"))!=-1){
                //属于当前设备encode
                mCache.put(Constant.POS_EN_CODE,"true");
                return;
            }else{
                //非当前设备encode
                mCache.put(Constant.POS_EN_CODE,"false");
                return;
            }
        }else{
            //非当前设备encode
            mCache.put(Constant.POS_EN_CODE,"false");
        }

    }

    @Override
    protected void mFail(HttpException e, String err) {
        super.mFail(e, err);
        Log.i(Constant.TAG, "CheckAccountsNet===" + e + "********=err==" + err);
    }

}
