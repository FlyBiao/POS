package com.cesaas.android.pos.data;

import android.util.Log;

import com.cesaas.android.pos.bean.PosOrderList;
import com.cesaas.android.pos.bean.PosOrderListBean;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.net.nohttp.HttpListener;
import com.google.gson.Gson;
import com.yolanda.nohttp.rest.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：数据服务
 * 创建日期：2016/10/15 23:02
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class DataServer {

    public static List<PosOrderList> orderlist = new ArrayList<>();


    //银联支付成功回调监听
    public static HttpListener<String> posOrderListener = new HttpListener<String>()
    {
        @Override
        public void onSucceed(int what, Response<String> response)
        {
            Log.d(Constant.TAG,"查账onSucceed:"+response.get());
            Gson gson=new Gson();
            PosOrderListBean bean=gson.fromJson(response.get(),PosOrderListBean.class);
            orderlist.addAll(bean.TModel);
        }
        @Override
        public void onFailed(int what, Response<String> response)
        {
            Log.d(Constant.TAG,"onFailed:"+response.getException());
        }
    };

    public static List<PosOrderList> getSampleData(){
        return orderlist;
    }

}
