package com.cesaas.android.pos.bean;

import com.cesaas.android.pos.bean.order.Item;
import com.cesaas.android.pos.bean.order.Pay;
import com.cesaas.android.pos.bean.order.Retail;

import java.util.List;

/**
 * Author FGB
 * Description 待支付订单结果Bean
 * Created 2017/3/30 18:48
 * Version 1.0
 */
public class ResultWaitPayOrderDetailBean extends BaseBean{

    public TModel TModel;

    public class TModel{
        public List<Item> Item;
        public List<com.cesaas.android.pos.bean.order.Pay> Pay;
        public Retail Retail;
    }
}
