package com.cesaas.android.pos.inventory.net;

import android.content.Context;
import android.util.Log;

import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.inventory.bean.ResultAddInventoryGoodsBean;
import com.cesaas.android.pos.net.xutils.BaseNet;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.cesaas.android.pos.utils.JsonUtils;
import com.lidroid.xutils.exception.HttpException;

import org.greenrobot.eventbus.EventBus;


/**
 * Author FGB
 * Description 添加盘点商品信息
 * Created at 2017/8/31 18:19
 * Version 1.0
 */

public class AddGoodNet extends BaseNet {
    public AddGoodNet(Context context) {
        super(context, true);
        this.uri="Distribution/Sw/Inventory/AddGood";
    }

    public void setData(int Id,int ShelvesId,String Code,int Type,int Num){
        try {
            data.put("Id",Id);
            data.put("ShelvesId",ShelvesId);
            data.put("Code",Code);
            data.put("Type",Type);//0装入1装出
            data.put("Num",Num);
            data.put("UserTicket", AbPrefsUtil.getInstance().getString(Constant.SPF_TOKEN));

        } catch (Exception e) {
            e.printStackTrace();
        }
        mPostNet();
    }

    @Override
    protected void mSuccess(String rJson) {
        super.mSuccess(rJson);
        Log.i(Constant.TAG,"添加盘点商品信息："+rJson);
        ResultAddInventoryGoodsBean bean= JsonUtils.fromJson(rJson,ResultAddInventoryGoodsBean.class);
        EventBus.getDefault().post(bean);
    }

    @Override
    protected void mFail(HttpException e, String err) {
        super.mFail(e, err);
        Log.i(Constant.TAG,err);
    }
}
