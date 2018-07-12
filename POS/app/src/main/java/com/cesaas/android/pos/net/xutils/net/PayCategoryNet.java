package com.cesaas.android.pos.net.xutils.net;

import android.content.Context;
import android.util.Log;

import com.cesaas.android.pos.bean.UserInfoBean;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.net.xutils.BaseNet;
import com.cesaas.android.pos.storedvalue.bean.ResultPayCategoryBean;
import com.cesaas.android.pos.utils.ACache;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.cesaas.android.pos.utils.JsonUtils;
import com.lidroid.xutils.exception.HttpException;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：pos 支付方式Net
 * 创建日期：2016/12/13 10:25
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class PayCategoryNet extends BaseNet {

    private ACache mCache;
    public PayCategoryNet(Context context, ACache mCache) {
        super(context, true);
        this.uri="Pos/Sw/Retail/PayCategoryList";
        this.mCache=mCache;
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
        mCache.put("GetPayCategory",rJson);
        ResultPayCategoryBean bean= JsonUtils.fromJson(rJson,ResultPayCategoryBean.class);
        EventBus.getDefault().post(bean);
    }

    @Override
    protected void mFail(HttpException e, String err) {
        super.mFail(e, err);
        Log.i(Constant.TAG,"**=HttpException="+e+"..=err="+err);
    }

}
