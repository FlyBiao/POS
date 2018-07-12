package com.cesaas.android.pos.inventory.net;

import android.content.Context;
import android.util.Log;

import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.inventory.bean.ResultCreateInventoryBean;
import com.cesaas.android.pos.net.xutils.BaseNet;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.cesaas.android.pos.utils.JsonUtils;
import com.lidroid.xutils.exception.HttpException;

import org.greenrobot.eventbus.EventBus;


/**
 * Author FGB
 * Description 修改盘点单Net
 * Created at 2017/8/31 16:12
 * Version 1.0
 */

public class UpdateInventoryNet extends BaseNet {
    public UpdateInventoryNet(Context context) {
        super(context, true);
        this.uri="Distribution/Sw/Inventory/Update";
    }

    public void setData(int Id,int  ShopId,String ShopName,int Type,String Date){
        try {
            data.put("Id",Id);
            data.put("ShopId",ShopId);
            data.put("ShopName",ShopName);
            data.put("Type",Type);
            data.put("Date",Date);
            data.put("UserTicket", AbPrefsUtil.getInstance().getString(Constant.SPF_TOKEN));

        } catch (Exception e) {
            e.printStackTrace();
        }
        mPostNet();
    }

    @Override
    protected void mSuccess(String rJson) {
        super.mSuccess(rJson);
        Log.i(Constant.TAG,"修改盘点单Net"+rJson);
        ResultCreateInventoryBean bean= JsonUtils.fromJson(rJson,ResultCreateInventoryBean.class);
        EventBus.getDefault().post(bean);
    }

    @Override
    protected void mFail(HttpException e, String err) {
        super.mFail(e, err);
        Log.i(Constant.TAG,err);
    }
}
