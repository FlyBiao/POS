package com.cesaas.android.pos.inventory.net;

import android.content.Context;
import android.util.Log;

import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.inventory.bean.ResultUpdateGoodsBean;
import com.cesaas.android.pos.net.xutils.BaseNet;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.cesaas.android.pos.utils.JsonUtils;
import com.lidroid.xutils.exception.HttpException;

import org.greenrobot.eventbus.EventBus;


/**
 * Author FGB
 * Description 修改商品盘点数量
 * Created at 2017/9/1 10:54
 * Version 1.0
 */

public class UpdateGoodsNumNet extends BaseNet {
    public UpdateGoodsNumNet(Context context) {
        super(context, true);
        this.uri="Distribution/Sw/Inventory/UpdateGoodsNum";
    }

    public void setData(int ShelvesId,int Id,String Code,int Num){
        try {
            data.put("ShelvesId",ShelvesId);
            data.put("Id",Id);
            data.put("Code",Code);
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
        Log.i(Constant.TAG,"修改商品盘点数量："+rJson);
        ResultUpdateGoodsBean bean= JsonUtils.fromJson(rJson,ResultUpdateGoodsBean.class);
        EventBus.getDefault().post(bean);
    }

    @Override
    protected void mFail(HttpException e, String err) {
        super.mFail(e, err);
        Log.i(Constant.TAG,err);
    }
}
