package com.cesaas.android.pos.inventory.net;

import android.content.Context;
import android.util.Log;

import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.inventory.bean.ResultShelfDetailsBean;
import com.cesaas.android.pos.net.xutils.BaseNet;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.cesaas.android.pos.utils.JsonUtils;
import com.lidroid.xutils.exception.HttpException;

import org.greenrobot.eventbus.EventBus;


/**
 * Author FGB
 * Description 获取货架信息Net
 * Created at 2017/8/31 10:10
 * Version 1.0
 */

public class ShelfDetailsNet extends BaseNet {
    public ShelfDetailsNet(Context context) {
        super(context, true);
        this.uri="";
    }

    public void setData(int Id,int ShelvesId){
        try {
            data.put("Id",Id);
            data.put("ShelvesId",ShelvesId);
            data.put("UserTicket", AbPrefsUtil.getInstance().getString(Constant.SPF_TOKEN));

        } catch (Exception e) {
            e.printStackTrace();
        }
        mPostNet();
    }

    @Override
    protected void mSuccess(String rJson) {
        super.mSuccess(rJson);
        ResultShelfDetailsBean bean= JsonUtils.fromJson(rJson,ResultShelfDetailsBean.class);
        EventBus.getDefault().post(bean);
    }

    @Override
    protected void mFail(HttpException e, String err) {
        super.mFail(e, err);
        Log.i(Constant.TAG,err);
    }
}
