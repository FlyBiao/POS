package com.cesaas.android.pos.bean;

import java.io.Serializable;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：下单二维码Bean
 * 创建日期：2016/10/10 22:08
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class ResultCreateFromStoreBean extends BaseBean{

    public CreateOrderBean TModel;

    public class CreateOrderBean implements Serializable{

        public double PayMent;//支付价格
        public double TotalPrice;//总价格
        public int RetailId;//OrderId
        public String SyncCode;//单号
    }
}

