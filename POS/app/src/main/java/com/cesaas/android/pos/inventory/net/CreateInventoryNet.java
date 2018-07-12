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
 * Description 新建盘点单Net
 * Created at 2017/8/31 9:40
 * Version 1.0
 */

public class CreateInventoryNet extends BaseNet {
    public CreateInventoryNet(Context context) {
        super(context, true);
        this.uri="Distribution/Sw/Inventory/Add";
    }

    public void setData(int shopId,String shopName,int type,String date){
        try {
            data.put("ShopId",shopId);
            data.put("ShopName",shopName);
            data.put("Type",type);
            data.put("Date",date);
            data.put("UserTicket", AbPrefsUtil.getInstance().getString(Constant.SPF_TOKEN));

        } catch (Exception e) {
            e.printStackTrace();
        }
        mPostNet();
    }

    @Override
    protected void mSuccess(String rJson) {
        super.mSuccess(rJson);
        Log.i(Constant.TAG,"新建盘点单："+rJson);
        ResultCreateInventoryBean bean= JsonUtils.fromJson(rJson,ResultCreateInventoryBean.class);
        EventBus.getDefault().post(bean);
    }

    @Override
    protected void mFail(HttpException e, String err) {
        super.mFail(e, err);
        Log.i(Constant.TAG,err);
    }
}
