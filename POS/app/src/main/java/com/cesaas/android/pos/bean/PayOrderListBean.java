package com.cesaas.android.pos.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：账单列表Bean
 * 创建日期：2017/1/24 11:05
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class PayOrderListBean extends BaseBean{

    public ArrayList<OrderList> TModel;

    public class OrderList implements Serializable{

        public double ConsumeAmount;
        public String CreateTime;
        public int PayCategory;
        public int PayType;
        public String RetailId;
    }

}
