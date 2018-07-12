package com.cesaas.android.pos.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：营销活动结果
 * 创建日期：2016/12/21 23:44
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class ResultActivityResultBean extends BaseBean{

    public ActivityBean TModel;


    public class ActivityBean{

        public int ActivityId;
        public double Discount;
        public int GiftQuantity;
        public ArrayList<Styles> Styles;
    }

    public class Styles implements Serializable{
        public String BarcodeId;
        public double PayMent;
        public int ShopStyleId;
        public int Quiantity;
        public double StylePrice;

    }
}
