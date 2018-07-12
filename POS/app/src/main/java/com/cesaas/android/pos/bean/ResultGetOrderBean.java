package com.cesaas.android.pos.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：获取订单详情Bean
 * 创建日期：2016/10/10 09:58
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class ResultGetOrderBean extends BaseBean{

    public ArrayList<OrderDetailBean> TModel;

    //订单详情对象
    public class OrderDetailBean implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public int ExpressType;//取货方式：0快递，1到店自提
        public int OrderStatus;//订单状态
        public String CreateDate;//订单创建时间
        public String ShopId;
        public String Mobile;//手机
        public String NickName;//昵称
        public String OrderId;//订单号
        public String OrderRemark;//备注
        public String ReserveDate;//取货时间
        public double PayPrice;//支付价格[最终折扣价格（包括折扣）]
        public double TotalPrice;//总价格
        public String Address;//收货地址
        public String City;//城市
        public String ConsigneeName;//收货人
        public String District;//区
        public String Province;//省
        public String ExpressShipNo;//物流快递订单号
        public String ExpressId;//快递物流ID
        public String SendDate;//发货时间

        public ArrayList<OrderItemBean> OrderItem;

    }

    // 订单Item数据对象
    public class OrderItemBean implements Serializable {
        private static final long serialVersionUID = 1L;
        public String Attr;// 商品规格
        public String ImageUrl;// 图片url
        public String OId;//
        public String OrderId;// 订单id
        public int OrderStatus;// 订单状态
        public double Price;// 商品价格
        public String Quantity;// 数量
        public String ShopStyleId;// 商品类型ID
        public String Title;// 商品名称
        public String StyleCode;//款号
        public String BarcodeCode;//条码

        private boolean isSuccess=false;

        public boolean isSuccess() {
            return isSuccess;
        }

        public void setSuccess(boolean isSuccess) {
            this.isSuccess = isSuccess;
        }


    }

}
