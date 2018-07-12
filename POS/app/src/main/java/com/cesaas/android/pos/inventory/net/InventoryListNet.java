package com.cesaas.android.pos.inventory.net;

import android.content.Context;
import android.util.Log;

import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.inventory.bean.ResultInventoryListBean;
import com.cesaas.android.pos.net.xutils.BaseNet;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.cesaas.android.pos.utils.JsonUtils;
import com.lidroid.xutils.exception.HttpException;

import org.greenrobot.eventbus.EventBus;


/**
 * Author FGB
 * Description 盘点列表Net
 * Created at 2017/8/31 9:40
 * Version 1.0
 */

public class InventoryListNet extends BaseNet {
    public InventoryListNet(Context context) {
        super(context, true);
        this.uri="Distribution/Sw/Inventory/GetList";
    }

    public void setData(int PageIndex){
        try {
            data.put("PageIndex",PageIndex);
            data.put("PageSize",30);
            data.put("UserTicket", AbPrefsUtil.getInstance().getString(Constant.SPF_TOKEN));

        } catch (Exception e) {
            e.printStackTrace();
        }
        mPostNet();
    }

    @Override
    protected void mSuccess(String rJson) {
        super.mSuccess(rJson);
        Log.i(Constant.TAG,"盘点单："+rJson);
        ResultInventoryListBean bean= JsonUtils.fromJson(rJson,ResultInventoryListBean.class);
        EventBus.getDefault().post(bean);
    }

    @Override
    protected void mFail(HttpException e, String err) {
        super.mFail(e, err);
        Log.i(Constant.TAG,err);
    }
}
