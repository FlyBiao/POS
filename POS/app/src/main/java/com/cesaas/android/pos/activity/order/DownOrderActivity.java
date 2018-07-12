package com.cesaas.android.pos.activity.order;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cesaas.android.pos.POSMsgBean;
import com.cesaas.android.pos.R;
import com.cesaas.android.pos.activity.cashier.CashierHomeActivity;
import com.cesaas.android.pos.activity.cashier.WeiXinAndAliPayActivity;
import com.cesaas.android.pos.adapter.BarcodeShopAdapter;
import com.cesaas.android.pos.adapter.GetHangOrderListAdapter;
import com.cesaas.android.pos.adapter.listviewadapter.adapterlibrary.ViewHolder;
import com.cesaas.android.pos.adapter.listviewadapter.adapterlibrary.abslistview.CommonAdapter;
import com.cesaas.android.pos.base.BaseActivity;
import com.cesaas.android.pos.base.BaseRecyclerView;
import com.cesaas.android.pos.bean.DeleteEventBusMsg;
import com.cesaas.android.pos.bean.EventBusMsg;
import com.cesaas.android.pos.bean.GetByBarcodeCode;
import com.cesaas.android.pos.bean.GetUserTicketListBean;
import com.cesaas.android.pos.bean.GoodsArrayBean;
import com.cesaas.android.pos.bean.MarketingActivityBean;
import com.cesaas.android.pos.bean.OrderInvalidEventBus;
import com.cesaas.android.pos.bean.OrderItemBean;
import com.cesaas.android.pos.bean.POSBus;
import com.cesaas.android.pos.bean.PayCallbackBean;
import com.cesaas.android.pos.bean.ResultActivityResultBean;
import com.cesaas.android.pos.bean.ResultCreateFromStoreBean;
import com.cesaas.android.pos.bean.ResultGetByBarcodeCodeBean;
import com.cesaas.android.pos.bean.ResultGetOrderBean;
import com.cesaas.android.pos.bean.ResultGetUserTicketInfoBean;
import com.cesaas.android.pos.bean.ResultGetUserTicketListBean;
import com.cesaas.android.pos.bean.ResultMarketingActivityBean;
import com.cesaas.android.pos.bean.ResultTicketIsAvailableBean;
import com.cesaas.android.pos.bean.ResultWeatherBean;
import com.cesaas.android.pos.bean.ResultWorkShiftBean;
import com.cesaas.android.pos.bean.SetNumberEventBusMsg;
import com.cesaas.android.pos.bean.SetPriceEventBusMsg;
import com.cesaas.android.pos.bean.ShopVipBean;
import com.cesaas.android.pos.bean.Styles;
import com.cesaas.android.pos.bean.UserInfoBean;
import com.cesaas.android.pos.bean.printer.LatticePrinterOrderBean;
import com.cesaas.android.pos.db.order.DBConstant;
import com.cesaas.android.pos.db.order.OrderSQLiteDatabaseUtils;
import com.cesaas.android.pos.db.bean.GetOrderDataBean;
import com.cesaas.android.pos.db.bean.OrderDataBean;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.global.Urls;
import com.cesaas.android.pos.listview.ShopSilderListView;
import com.cesaas.android.pos.net.nohttp.HttpListener;
import com.cesaas.android.pos.net.xutils.net.DownOrderCashierNet;
import com.cesaas.android.pos.net.xutils.net.GetActivityResultNet;
import com.cesaas.android.pos.net.xutils.net.GetOrderNet;
import com.cesaas.android.pos.net.xutils.net.MarketingActivityNet;
import com.cesaas.android.pos.net.xutils.net.TicketIsAvailableNet;
import com.cesaas.android.pos.pos.adapter.PosPayCategoryAdapter;
import com.cesaas.android.pos.storedvalue.bean.ResultPayCategoryBean;
import com.cesaas.android.pos.utils.AbDateUtil;
import com.cesaas.android.pos.utils.DecimalFormatUtils;
import com.cesaas.android.pos.utils.JsonUtils;
import com.cesaas.android.pos.utils.MD5;
import com.cesaas.android.pos.utils.OrderCashierTicketPrinterTools;
import com.cesaas.android.pos.utils.RandomUtils;
import com.cesaas.android.pos.utils.SingleCashierPrinterTools;
import com.cesaas.android.pos.utils.Skip;
import com.cesaas.android.pos.utils.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.gson.Gson;
import com.jauker.widget.BadgeView;
import com.wangpos.pay.UnionPay.PosConfig;
import com.wangpos.poscore.IPosCallBack;
import com.wangpos.poscore.PosCore;
import com.wangpos.poscore.impl.PosCoreFactory;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;
import com.zhl.cbdialog.CBDialogBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.weipass.pos.sdk.IPrint;
import cn.weipass.pos.sdk.LatticePrinter;
import cn.weipass.pos.sdk.impl.WeiposImpl;
import scanner.CaptureActivity;


/**
 * ================================================
 * 作    者：FGB
 * 描    述：下單
 * 创建日期：2016/10/10 13:52
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class DownOrderActivity extends BaseActivity implements View.OnClickListener{

    private LinearLayout ll_pos_down_order_back,ll_down_order_cashier,ll_hang_order,ll_take_order,ll_discount_order,ll_coupons_order;
    private LinearLayout ll_pos_activity;

    private TextView tv_pos_orders_invalid,tv_add_vip,tv_add_barcode_shop,tv_finally_price,tv_total_price,tv_order_pay_back,tv_activity,tv_pos_grade;
    private TextView tv_barcode_order_no,tv_barcode_order_date,tv_pos_sales,tv_pos_vips,tv_pos_points,tv_barcode_shop_sum,tv_pos_order_shop_name;
    private TextView tv_workshift,tv_pos_weather;

    private EditText et_vip_mobile,et_add_discount,et_add_coupons;

    private ImageView iv_add_scan_coupons,iv_activity,iv_ticket,iv_get_order;//添加扫描优惠价

    private String orderNo;//单号
    private String nickName;
    private String mobile;
    private String vipId;//会员ID
    private String MemberId;
    private String vipGrade;//会员等级
    private int vipGradeId;//会员等级
    private String fansId;//会员Fans ID
    private int vipPoint;
    private String openId;

    PosCore.RXiaoFei  rXiaoFei;
    final int RESULT_CODE = 101;
    private int REQUEST_CONTACT = 20;
    private String scanCode;

    private byte[] lock = new byte[1];
    int EVENT_NO_PAPER = 1;
    private final int LOCK_WAIT = 0;
    private final int LOCK_CONTINUE = 1;
    //8583协议中的参考号
    private String refNum;
    private PosCore pCore;
    private PosCallBack callBack;
    private LatticePrinter latticePrinter;// 点阵打印
    private LatticePrinterOrderBean latticePrinterOrderBean;//订单打印Bean
    private EditText tv_show_msg;
    private int money;
    private double payMoney;
    private String amount;
    private String couponsScanCode;//优惠价扫描码
    //下单收银请求
    private DownOrderCashierNet orderCashierNet;
    //验证优惠券是否可用
    private TicketIsAvailableNet ticketIsAvailableNet;

    public List<GetByBarcodeCode> barcodeCodeList=new ArrayList<GetByBarcodeCode>();
    private List<OrderItemBean> mOrderItemBeanList=new ArrayList<>();
    private ArrayList<ResultGetOrderBean.OrderDetailBean> orderList;//商品订单列表
    private BarcodeShopAdapter barcodeShopAdapter;
    private GetByBarcodeCode shopBean;
    private ShopSilderListView slv_post_hang_order_list;
    private String shopStyleId;//商品id
    private double payTotalPrice=0.0;//商品支付总价
    private double payMent=0.0;//商品支付金额
    private int goodsCount=0;//商品数量
    private double discountPrice=0.0;//折后价格
    private boolean isDiscount=false;//是否打折扣
    private String goodsDiscount;//是否打折扣
    private double discount;//全局商品折扣
    private int orderStatus;//订单状态
    private int payType;//订单支付类型
    private int IsPractical;
    private String orderId;//订单号
    private String userName;//用户名【营业员】
    private String shopNameNick;//店铺名
    private String referenceNumber;//参考号
    private String traceAuditNumber;//凭证号

    private String couponId;//优惠券Id
    private int giftQuantity;
    private int giftQuantitySum;
    private String giftsShopBarcodeId;
    private int setShopQuantity;
    private String setShopQuantityBarcodeId;
    private String delShopBarcodeId;
    private double shopPriceBarcodeId;
    private double giftsShopBarcodePrice;

    private GetByBarcodeCode prices=null;
    private GetOrderNet getOrderNet;

    private CustomOrderPayDialog dialog;//订单支付Dialog
    private CustomOrderDiscountDialog customOrderDiscountDialog;//订单折扣Dialog
    private CustomOrderCouponsDialog customOrderCouponsDialog;//优惠价Dialog
    private CustomGetOrderDialog customGetOrderDialog;//取单Dialog
    private CustomMarketingActivityDialog customMarketingActivityDialog;//自定义营销活动Dialog
    private CustomGetUseTicketDialog customGetUseTicketDialog;//自定义获取获取优惠券List

    private BadgeView getOrderBadgeView;
    private BadgeView activityBadgeView;
    private BadgeView ticketBadgeView;
    private RecyclerView mRecyclerView;
    private RecyclerView mGetUseTicketRecyclerView;
    private ActivityAdapter adapter;
    private GetUseTicketAdapter getUseTicketAdapter;
    private MarketingActivityNet marketingActivityNet;
    private List<MarketingActivityBean> marketingActivityBeen=new ArrayList<MarketingActivityBean>();
    private List<GetUserTicketListBean> getUserTicketInfoBeanList=new ArrayList<>();

    //营销活动结果集
    private List<ResultActivityResultBean.Styles> activityStylesArrayList;
    private double activityResultPayMent;//营销活动支付金额
    private double promotionAmount;//优惠金额
    private double activityStyleTotalPrice;
    private boolean activityIsSuccess=false;
    private boolean ticketIsSuccess=false;
    private String ticketTitle;
    private double ticketMoney=0.0;
    private int activityId;//营销活动ID
    private String activityPlanName;//活动方案名称

    private ListView lvWeather;//天气
    private ListView lvWorkShift;//班次
    private String weather;
    private String workShift;

    private POSBus posBus;

    private CustomWorkShiftDialog customWorkShiftDialog;
    private CustomWeatherDialog customWeatherDialog;
    private ArrayList<ResultWorkShiftBean.WorkShiftBean> workShiftList;
    private ArrayList<ResultWeatherBean.WeatherBean> weatherList;

    private GetActivityResultNet getActivityResultNet;
    private JSONArray styleJsonArray;
    public JSONArray styleArray=new JSONArray();

    private JSONArray barcodeArray;
    public JSONArray shopBarcodeArray=new JSONArray();

    private JSONArray goodsListJson;
    public JSONArray goodsArray=new JSONArray();

    private JSONArray orderJsonArray;
    public JSONArray jsonArray=new JSONArray();


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //3s后执行代码
            //调用pos打印机
            setLatticePrinter();
            //刷新当前页面
            clear();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_order);
        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            userName=bundle.getString("userName");
        }

        initView();
        addClickListener();

        //初始化CoreApp连接对象
        initPosCore();

        Request<String> request = NoHttp.createStringRequest(Urls.USER_INFO, RequestMethod.POST);
        commonNet.requestNetTask(request,userInfoListener);

        //营销活动
        marketingActivityNet=new MarketingActivityNet(mContext);
        marketingActivityNet.setData();

        createSqliteDB();
    }

    //创建sqlite数据库
    private void createSqliteDB(){
        OrderSQLiteDatabaseUtils.createDB(mContext, DBConstant.DB, DBConstant.VERSION);
//        OrderSQLiteDatabaseUtils.getSelectData(mContext);
    }

    /**
     * 初始化CoreApp连接对象
     *
     * @return
     */
    private void initPosCore() {
        if (pCore == null) {
            // 配置数据为开发阶段的数据
            HashMap<String, String> init_params = new HashMap<String,String>();
            init_params.put(PosConfig.Name_EX + "1053", "CoreApp签购单台头");// 签购单小票台头
            init_params.put(PosConfig.Name_EX + "1100", "cn.weipass.cashier");// 核心APP 包名
            init_params.put(PosConfig.Name_EX + "1101", "com.wangpos.cashiercoreapp.services.CoreAppService");// 核心APP 类名
            init_params.put(PosConfig.Name_EX + "1093", "2");// 是否需要打印三联签购单 1.需要 2.不需要
            init_params.put(PosConfig.Name_EX + "1012", "1");// 华势通道

            init_params.put(PosConfig.Name_MerchantName, "coreApp");

            pCore = PosCoreFactory.newInstance(this, init_params);
            callBack = new PosCallBack(pCore);
        }
    }

    /**
     * 接收营销活动消息
     * @param msg 消息实体类
     */
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(ResultMarketingActivityBean msg) {
        if(msg.isSuccess()==true && msg.TModel!=null){
            //设置活动红点数量
            activityBadgeView=new BadgeView(this);
            activityBadgeView.setTargetView(iv_activity);
            activityBadgeView.setBadgeCount(msg.TModel.size());
        }
    }

    /**
     * 接收设置赠品消息
     * @param msg 消息实体类
     */
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(EventBusMsg msg) {
        giftsShopBarcodeId=msg.getBarcodeId();
        //设置活动商品款号
        giftQuantitySum+=giftQuantity;
        activityResultPayMent=payTotalPrice - msg.getGiftsPrice();
        tv_finally_price.setText(DecimalFormatUtils.decimalToFormat(activityResultPayMent)+"");
        tv_total_price.setText(DecimalFormatUtils.decimalToFormat(activityResultPayMent)+"");
    }

    /**
     * 接收商品数量消息
     * @param msg 消息实体类
     */
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(SetNumberEventBusMsg msg) {
        int count=0;
        payTotalPrice=0.0;
        payMent=0.0;
        jsonArray=new JSONArray();
        mOrderItemBeanList=new ArrayList<>();

        for (int i=0;i<barcodeCodeList.size();i++){
            if(barcodeCodeList.get(i).getBarcodeId().equals(msg.getBarcodeId())){
                barcodeCodeList.get(i).setShopCount(msg.ShopCount);
//                barcodeCodeList.get(i).setPayMent(barcodeCodeList.get(i).getPayMent() *barcodeCodeList.get(i).getShopCount());
//                break;
            }
            payMent+=barcodeCodeList.get(i).getPayMent() * barcodeCodeList.get(i).getShopCount();
            payTotalPrice+=barcodeCodeList.get(i).getPrice() * barcodeCodeList.get(i).getShopCount();
            count+=barcodeCodeList.get(i).getShopCount();

            //设置订单数据
            OrderItemBean itemBean=new OrderItemBean();
            itemBean.setBarcodeId(barcodeCodeList.get(i).getBarcodeId());
            itemBean.setTitle(barcodeCodeList.get(i).getTitle());
            itemBean.setBarcodeNo(barcodeCodeList.get(i).getBarcodeCode());
            itemBean.setShopStyleId(barcodeCodeList.get(i).getShopStyleId());
            itemBean.setSaleType(0);
            itemBean.setStylePrice(barcodeCodeList.get(i).getPrice());
            itemBean.setPayMent(barcodeCodeList.get(i).getPayMent() * barcodeCodeList.get(i).getShopCount());
            itemBean.setQuantity(barcodeCodeList.get(i).getShopCount());

            mOrderItemBeanList.add(itemBean);
            jsonArray.put(itemBean.getOrderItem());
        }

        orderJsonArray=jsonArray;
        Log.i("codeGoodsList","修改数量:"+orderJsonArray);

        //实例化Adapter数据适配器
        barcodeShopAdapter=new BarcodeShopAdapter(mContext,mActivity,barcodeCodeList);
        //将Adapter添加到ListView中
        slv_post_hang_order_list.setAdapter(barcodeShopAdapter);

        tv_barcode_shop_sum.setText(count+"件商品");
        tv_finally_price.setText(DecimalFormatUtils.decimalToFormat(payMent)+"");
        tv_total_price.setText(DecimalFormatUtils.decimalToFormat(payTotalPrice)+"");
    }

    /**
     * 接收活动使用 消息通知
     * @param bean 消息实体类
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(ResultActivityResultBean bean) {

        if(bean.isSuccess()==true && bean.TModel!=null){
            giftQuantity=bean.TModel.GiftQuantity;

            activityStylesArrayList=new ArrayList<ResultActivityResultBean.Styles>();
            activityStylesArrayList.addAll(bean.TModel.Styles);

            activityResultPayMent=0.0;//营销活动支付金额
//            payTotalPrice=0.0;//商品总价
            JSONArray jsonArray=new JSONArray();
            orderJsonArray=new JSONArray();
            //获取活动支付金额和总金额
            for (int i=0;i<activityStylesArrayList.size();i++){
                activityResultPayMent+=Double.parseDouble(DecimalFormatUtils.decimalToFormat(activityStylesArrayList.get(i).PayMent));
//                activityResultPayMent=activityResultPayMent * activityStylesArrayList.get(i).Quiantity;
//                payTotalPrice=Double.parseDouble(DecimalFormatUtils.decimalToFormat(activityStylesArrayList.get(i).StylePrice));
//                payTotalPrice=payTotalPrice * activityStylesArrayList.get(i).Quiantity;

                for (int j=0; j<barcodeCodeList.size(); j++){
                    //根据使用活动返回的商品条码ID和原来商品条码ID是否相等，相等则把活动最终加个设置给原来价格
                    if(barcodeCodeList.get(j).getBarcodeId() .contains(activityStylesArrayList.get(i).BarcodeId)){
                        barcodeCodeList.get(j).setPayMent(activityStylesArrayList.get(i).PayMent );//支付价格
                    }
                }
            }

            for (int j=0; j<barcodeCodeList.size(); j++){
                OrderItemBean itemBean=new OrderItemBean();
                itemBean.setBarcodeId(barcodeCodeList.get(j).getBarcodeId());
                itemBean.setTitle(barcodeCodeList.get(j).getTitle());
                itemBean.setBarcodeNo(barcodeCodeList.get(j).getBarcodeCode());
                itemBean.setStylePrice(barcodeCodeList.get(j).getPrice());
                itemBean.setPayMent(barcodeCodeList.get(j).getPayMent());
                itemBean.setShopStyleId(barcodeCodeList.get(j).getShopStyleId());
                itemBean.setQuantity(barcodeCodeList.get(j).getShopCount());
                itemBean.setSaleType(0);

                jsonArray.put(itemBean.getOrderItem());
            }
            orderJsonArray=jsonArray;
//
//            //实例化Adapter数据适配器
            barcodeShopAdapter=new BarcodeShopAdapter(mContext,mActivity,barcodeCodeList);
            //将Adapter添加到ListView中
            slv_post_hang_order_list.setAdapter(barcodeShopAdapter);

            tv_finally_price.setText(DecimalFormatUtils.decimalToFormat(activityResultPayMent));//结算
            tv_total_price.setText(DecimalFormatUtils.decimalToFormat(payTotalPrice));//总价

            activityIsSuccess=true;
            ToastUtils.show("使用使用优惠活动成功!");
        }else{
            ToastUtils.show("使用失败："+bean.getMessage());
        }
    }

    /**
     * 接收商品价格消息
     * @param msg 消息实体类
     */
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(SetPriceEventBusMsg msg) {
        payTotalPrice=0.0;
        payMent=0.0;
        jsonArray=new JSONArray();
        mOrderItemBeanList=new ArrayList<>();

        for (int i=0;i<barcodeCodeList.size();i++){
            if(barcodeCodeList.get(i).getBarcodeId().contains(msg.getBarcodeId())){
                barcodeCodeList.get(i).setPrice(msg.getPrice());
                barcodeCodeList.get(i).setPayMent(msg.getPrice());
            }
            payMent+=barcodeCodeList.get(i).getPayMent() * barcodeCodeList.get(i).getShopCount();
            payTotalPrice+=barcodeCodeList.get(i).getPrice() * barcodeCodeList.get(i).getShopCount();

            //设置订单数据
            OrderItemBean itemBean=new OrderItemBean();
            itemBean.setBarcodeId(barcodeCodeList.get(i).getBarcodeId());
            itemBean.setTitle(barcodeCodeList.get(i).getTitle());
            itemBean.setBarcodeNo(barcodeCodeList.get(i).getBarcodeCode());
            itemBean.setShopStyleId(barcodeCodeList.get(i).getShopStyleId());
            itemBean.setSaleType(0);
            itemBean.setStylePrice(barcodeCodeList.get(i).getPrice());
            itemBean.setPayMent(barcodeCodeList.get(i).getPayMent() * barcodeCodeList.get(i).getShopCount());
            itemBean.setQuantity(barcodeCodeList.get(i).getShopCount());

            mOrderItemBeanList.add(itemBean);
            jsonArray.put(itemBean.getOrderItem());
        }
        orderJsonArray=jsonArray;
        Log.i("codeGoodsList","修改价格:"+orderJsonArray);

        //实例化Adapter数据适配器
        barcodeShopAdapter=new BarcodeShopAdapter(mContext,mActivity,barcodeCodeList);
        //将Adapter添加到ListView中
        slv_post_hang_order_list.setAdapter(barcodeShopAdapter);

        tv_finally_price.setText(DecimalFormatUtils.decimalToFormat(payTotalPrice)+"");
        tv_total_price.setText(DecimalFormatUtils.decimalToFormat(payTotalPrice)+"");
    }

    /**
     * 接收删除商品消息
     * @param msg 消息实体类
     */
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(DeleteEventBusMsg msg) {
        int count=0;
        payTotalPrice=0.0;
        payMent=0.0;
        jsonArray=new JSONArray();
        mOrderItemBeanList=new ArrayList<>();

        barcodeCodeList.remove(msg.getPosition());
        //实例化Adapter数据适配器
        barcodeShopAdapter=new BarcodeShopAdapter(mContext,mActivity,barcodeCodeList);
        //将Adapter添加到ListView中
        slv_post_hang_order_list.setAdapter(barcodeShopAdapter);
        //刷新Adapter列表
        barcodeShopAdapter.notifyDataSetChanged();

        for (int i=0;i<barcodeCodeList.size();i++){
            payTotalPrice+=barcodeCodeList.get(i).getPrice() * barcodeCodeList.get(i).getShopCount();
            payMent+=barcodeCodeList.get(i).getPayMent() * barcodeCodeList.get(i).getShopCount();
            count+=barcodeCodeList.get(i).getShopCount();
//
//            //设置订单数据
            OrderItemBean itemBean=new OrderItemBean();
            itemBean.setBarcodeId(barcodeCodeList.get(i).getBarcodeId());
            itemBean.setTitle(barcodeCodeList.get(i).getTitle());
            itemBean.setBarcodeNo(barcodeCodeList.get(i).getBarcodeCode());
            itemBean.setShopStyleId(barcodeCodeList.get(i).getShopStyleId());
            itemBean.setSaleType(0);
            itemBean.setStylePrice(barcodeCodeList.get(i).getPrice());
            itemBean.setPayMent(barcodeCodeList.get(i).getPayMent() * barcodeCodeList.get(i).getShopCount());
            itemBean.setQuantity(barcodeCodeList.get(i).getShopCount());
            mOrderItemBeanList.add(itemBean);
            jsonArray.put(itemBean.getOrderItem());
        }
        orderJsonArray=jsonArray;
        Log.i("codeGoodsList","删除商品::"+orderJsonArray);

        tv_barcode_shop_sum.setText(count+"件商品");
        tv_finally_price.setText(DecimalFormatUtils.decimalToFormat(payMent)+"");
        tv_total_price.setText(DecimalFormatUtils.decimalToFormat(payTotalPrice)+"");

    }

    /**
     * 接收整单作废消息
     * @param msg 消息实体类
     */
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(OrderInvalidEventBus msg) {
        if(msg.isSuccess()==true){
            barcodeCodeList.clear();
            barcodeCodeList=new ArrayList<>();
            mOrderItemBeanList=new ArrayList<>();

            //实例化Adapter数据适配器
            barcodeShopAdapter=new BarcodeShopAdapter(mContext,mActivity,barcodeCodeList);
            //将Adapter添加到ListView中
            slv_post_hang_order_list.setAdapter(barcodeShopAdapter);

            payMent=0.0;
            payTotalPrice=0.0;
            goodsCount=0;
            goodsCount=0;

            jsonArray=new JSONArray();
            orderJsonArray=null;
            barcodeCodeList=new ArrayList<>();
            tv_barcode_shop_sum.setText("0件商品");
            tv_finally_price.setText(DecimalFormatUtils.decimalToFormat(payMent)+"");
            tv_total_price.setText(DecimalFormatUtils.decimalToFormat(payTotalPrice)+"");

            String order=AbDateUtil.getCurrentTimeAsNumber()+RandomUtils.getFourRandom();
            tv_barcode_order_no.setText(order);
            tv_pos_grade.setText("");
            tv_pos_vips.setText("");
            tv_pos_points.setText("");
            tv_finally_price.setText("");
            tv_total_price.setText("");

            ToastUtils.getLongToast(mContext,"订单作废成功,请重新下单！");
        }
    }

    /**
     * 初始化控件
     */
    public void initView(){

        //通过EventBus订阅事件
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        //初始化视图控件
        slv_post_hang_order_list=(ShopSilderListView) findViewById(R.id.slv_post_hang_order_list);
        ll_pos_down_order_back= (LinearLayout) findViewById(R.id.ll_pos_down_order_back);
        ll_hang_order= (LinearLayout) findViewById(R.id.ll_hang_order);
        ll_take_order= (LinearLayout) findViewById(R.id.ll_take_order);
        ll_discount_order= (LinearLayout) findViewById(R.id.ll_discount_order);
        ll_coupons_order= (LinearLayout) findViewById(R.id.ll_coupons_order);
        ll_down_order_cashier= (LinearLayout) findViewById(R.id.ll_down_order_cashier);
        ll_pos_activity= (LinearLayout) findViewById(R.id.ll_pos_activity);
        tv_pos_orders_invalid= (TextView) findViewById(R.id.tv_pos_orders_invalid);
        tv_add_vip= (TextView) findViewById(R.id.tv_add_vip);
        tv_add_barcode_shop= (TextView) findViewById(R.id.tv_add_barcode_shop);
        iv_ticket= (ImageView) findViewById(R.id.iv_ticket);
        iv_get_order= (ImageView) findViewById(R.id.iv_get_order);

        tv_pos_grade= (TextView) findViewById(R.id.tv_pos_grade);
        tv_barcode_order_no= (TextView) findViewById(R.id.tv_barcode_order_no);
        String order=AbDateUtil.getCurrentTimeAsNumber()+RandomUtils.getFourRandom();
        tv_barcode_order_no.setText(order);
        tv_barcode_order_date= (TextView) findViewById(R.id.tv_pos_date);
        tv_barcode_order_date.setText(AbDateUtil.getCurrentTate());
        tv_pos_sales= (TextView) findViewById(R.id.tv_pos_sales);
        tv_pos_sales.setText(userName);
        tv_pos_vips= (TextView) findViewById(R.id.tv_pos_vips);
        tv_pos_points= (TextView) findViewById(R.id.tv_pos_points);
        tv_barcode_shop_sum=(TextView) findViewById(R.id.tv_barcode_shop_sum);
        tv_pos_order_shop_name= (TextView) findViewById(R.id.tv_pos_order_shop_name);
        tv_activity= (TextView) findViewById(R.id.tv_activity);
        iv_activity= (ImageView) findViewById(R.id.iv_activity);
        tv_pos_weather= (TextView) findViewById(R.id.tv_pos_weather);
        tv_workshift= (TextView) findViewById(R.id.tv_workshift);

        tv_show_msg= (EditText) findViewById(R.id.tv_show_msg);
        tv_finally_price= (TextView) findViewById(R.id.tv_finally_price);
        tv_total_price= (TextView) findViewById(R.id.tv_total_price);

    }

    /**
     * 添加点击监听
     */
    public void addClickListener(){
        ll_pos_down_order_back.setOnClickListener(this);
        ll_hang_order.setOnClickListener(this);
        ll_take_order.setOnClickListener(this);
        ll_discount_order.setOnClickListener(this);
        ll_coupons_order.setOnClickListener(this);
        ll_down_order_cashier.setOnClickListener(this);
        tv_pos_orders_invalid.setOnClickListener(this);
        tv_add_vip.setOnClickListener(this);
        tv_add_barcode_shop.setOnClickListener(this);
        ll_pos_activity.setOnClickListener(this);
        tv_workshift.setOnClickListener(this);
        tv_pos_weather.setOnClickListener(this);

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_pos_down_order_back://返回
                Skip.mBack(mActivity);
                break;

            case R.id.tv_pos_orders_invalid://整单作废
                orderInvalid();
                break;

            case R.id.tv_workshift://班次
                customWorkShiftDialog=new CustomWorkShiftDialog(mContext, R.style.dialog, R.layout.custom_workshift_dialog);
                customWorkShiftDialog.show();

                Request<String> request = NoHttp.createStringRequest(Urls.GET_WorkSHIFT, RequestMethod.GET);
                commonNet.requestNetTask(request,getWorkShiftListener);
                break;

            case R.id.tv_pos_weather://天气
                customWeatherDialog=new CustomWeatherDialog(mContext, R.style.dialog, R.layout.custom_weather_dialog);
                customWeatherDialog.show();

                Request<String> requesth = NoHttp.createStringRequest(Urls.GET_WEATHER, RequestMethod.GET);
                commonNet.requestNetTask(requesth,getWeatherListener);
                break;

            case R.id.tv_add_vip://添加会员
                new AddVipDialog(mContext,mActivity).mInitShow();
                break;

            case R.id.tv_add_barcode_shop://添加扫描商品
                Skip.mScanShopActivityResult(mActivity, CaptureActivity.class, REQUEST_CONTACT);
                break;

            case R.id.ll_hang_order://挂单
                if(barcodeCodeList.size()!=0){
                    hangOrder();
                }else{
                    ToastUtils.show("请添加订单信息再执行！");
                }
                break;

            case R.id.ll_take_order://取单
                OrderSQLiteDatabaseUtils.selectData(mContext);
                break;

            case R.id.ll_pos_activity://营销活动
//                Skip.mNext(mActivity, MarketingActivityList.class);
//                if(activityIsSuccess==true){
//                    ToastUtils.getLongToast(mContext,"已使用了活动，不能重复使用！");
//
//                }else{
                customMarketingActivityDialog=new CustomMarketingActivityDialog(mContext,R.style.dialog,R.layout.custom_marketing_activity_dialog);
                customMarketingActivityDialog.show();
                customMarketingActivityDialog.setCancelable(false);
                getActivityListener();
//                }
                break;

            case R.id.ll_discount_order://打折
                if(activityIsSuccess==true){
                    ToastUtils.getLongToast(mContext,"已使用活动优惠，不能再打折扣！");
                }else {
                    customOrderDiscountDialog=new CustomOrderDiscountDialog(mContext,R.style.dialog,R.layout.item_discount_dialog);
                    customOrderDiscountDialog.show();
                    customOrderDiscountDialog.setCancelable(false);
                }
                break;

            case R.id.ll_coupons_order://优惠劵
                if(vipId!=null){
                    if(barcodeCodeList.size()!=0){
                        if(activityIsSuccess==true){
                            ToastUtils.getLongToast(mContext,"已使用活动优惠，不能再使用优惠券！");
                        }else{
                            if(vipId!=null){
                                getUserTickerListListener(vipId);
                            }
                            else{
                                ToastUtils.getLongToast(mContext,"请添加会员再使用优惠券！");
                            }
                        }

                    }else{
                        ToastUtils.getLongToast(mContext,"请先添加商品再使用！");
                    }
                }else{
                    ToastUtils.getLongToast(mContext,"请先添加会员再使用！");
                }
                break;

            case R.id.ll_down_order_cashier://下单收银
                if(barcodeCodeList.size()!=0){
                    //调用下单收银接口
                    orderCashier();
                }else{
                    ToastUtils.getLongToast(mContext,"请先添加商品再收银！");
                }
                break;
        }
    }

    /**
     * 班次回调
     */
    private HttpListener<String> getWorkShiftListener = new HttpListener<String>()
            {
                @Override
                public void onSucceed(int what, Response<String> response)
                {
                    Gson gson=new Gson();
                    ResultWorkShiftBean bean=gson.fromJson(response.get(),ResultWorkShiftBean.class);
                    workShiftList=new ArrayList<>();
                    workShiftList.addAll(bean.TModel);
                    setWorkShiftAdapter();
                    workshiftItemClick();
                }
                @Override
                public void onFailed(int what, Response<String> response)
                {
                    ToastUtils.show(response.get());
                }
            };

    /**
     * 天气回调
     */
    private HttpListener<String> getWeatherListener = new HttpListener<String>()
    {
        @Override
        public void onSucceed(int what, Response<String> response)
        {
            Gson gson=new Gson();
            ResultWeatherBean bean = gson.fromJson(response.get(), ResultWeatherBean.class);
            weatherList=new ArrayList<>();
            weatherList.addAll(bean.TModel);
            setWeatherAdapter();
            weatherItemClick();
        }
        @Override
        public void onFailed(int what, Response<String> response)
        {
            ToastUtils.show(response.get());
        }
    };

    /**
     * 营销活动监听
     */
    public void getActivityListener(){
        Request<String> request = NoHttp.createStringRequest(Urls.MARKETING_ACTIVITY_LIST, RequestMethod.POST);
        commonNet.requestNetTask(request,getActivityListener);
    }

    /**
     * 营销活动回调
     */
    private HttpListener<String> getActivityListener = new HttpListener<String>()
    {
        @Override
        public void onSucceed(int what, Response<String> response)
        {
            Log.i(Constant.TAG,"营销活动回调:"+response.get());
            marketingActivityBeen=new ArrayList<MarketingActivityBean>();
            ResultMarketingActivityBean bean = gson.fromJson(response.get(), ResultMarketingActivityBean.class);
            if(bean.isSuccess()==true && bean.TModel!=null){
                marketingActivityBeen.addAll(bean.TModel);
                initAdapter();
            }else{
                ToastUtils.getLongToast(mContext,bean.getMessage());
            }
        }
        @Override
        public void onFailed(int what, Response<String> response)
        {
            ToastUtils.show(response.get());
        }
    };

    /**
     * 挂单方法
     */
    public void hangOrder(){
        new CBDialogBuilder(mActivity)
                .setTouchOutSideCancelable(true)
                .showCancelButton(true)
                .setTitle("挂单提醒！")
                .setMessage("请确认该订单是否需要挂单？")
                .setConfirmButtonText("确定")
                .setCancelButtonText("取消")
                .setDialogAnimation(CBDialogBuilder.DIALOG_ANIM_SLID_BOTTOM)
                .setButtonClickListener(true, new CBDialogBuilder.onDialogbtnClickListener() {
                    @Override
                    public void onDialogbtnClick(Context context, Dialog dialog, int whichBtn) {
                        switch (whichBtn) {
                            case BUTTON_CONFIRM:
                                //执行挂单操作
                                for (int i=0;i<barcodeCodeList.size();i++){
                                    OrderSQLiteDatabaseUtils.insterData(mContext,
                                            barcodeCodeList.get(i).getShopCount(),
                                            barcodeCodeList.get(i).getShopStyleId(),
                                            tv_barcode_order_no.getText().toString(),
                                            barcodeCodeList.get(i).getTitle(),
                                            barcodeCodeList.get(i).getBarcodeCode(),
                                            barcodeCodeList.get(i).getPayMent(),
                                            barcodeCodeList.get(i).getPrice(),
                                            tv_pos_weather.getText().toString(),
                                            tv_barcode_order_date.getText().toString(),
                                            tv_pos_grade.getText().toString(),
                                            tv_pos_sales.getText().toString(),
                                            tv_workshift.getText().toString(),
                                            tv_pos_vips.getText().toString(),
                                            tv_pos_points.getText().toString()
                                    );
                                }
                                break;
                            case BUTTON_CANCEL:
                                ToastUtils.show("已取消退出");
                                break;
                            default:
                                break;
                        }
                    }
                })
                .create().show();
    }

    /**
     * 设置班次Adapter
     */
    public void setWorkShiftAdapter(){
        lvWorkShift.setAdapter(new CommonAdapter<ResultWorkShiftBean.WorkShiftBean>(mContext,R.layout.item_workshift,workShiftList) {
            @Override
            public void convert(ViewHolder holder, ResultWorkShiftBean.WorkShiftBean workShiftBean, int postion) {
                holder.setText(R.id.tv_workshift_name,workShiftBean.getWorkShiftName());
            }
        });
    }

    /**
     * 设置天气Adapter
     */
    public void setWeatherAdapter(){
        lvWeather.setAdapter(new CommonAdapter<ResultWeatherBean.WeatherBean>(mContext,R.layout.item_weather,weatherList) {
            @Override
            public void convert(ViewHolder holder, ResultWeatherBean.WeatherBean weatherBean, int postion) {
                holder.setText(R.id.tv_weather_name,weatherBean.getWeatherType());
            }
        });
    }

    public void workshiftItemClick(){
        lvWorkShift.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tv_workshift.setText(workShiftList.get(position).getWorkShiftName());
                workShift=tv_workshift.getText().toString();
                customWorkShiftDialog.dismiss();
            }
        });
    }
    public void weatherItemClick(){
        lvWeather.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tv_pos_weather.setText(weatherList.get(position).getWeatherType());
                weather=tv_pos_weather.getText().toString();
                customWeatherDialog.dismiss();
            }
        });
    }

    /**
     * 初始化优惠券列表数据适配器
     */
    public void initGetUseTicketAdapter(){
        getUseTicketAdapter=new GetUseTicketAdapter(R.layout.item_getuseticket_activity,getUserTicketInfoBeanList);
        getUseTicketAdapter.openLoadAnimation();
        mGetUseTicketRecyclerView.setHasFixedSize(true);
        mGetUseTicketRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mGetUseTicketRecyclerView.setAdapter(getUseTicketAdapter);

        //点击使用优惠券
        mGetUseTicketRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void SimpleOnItemClick(BaseQuickAdapter baseQuickAdapter, View view, int position) {
                if(getUserTicketInfoBeanList.get(position).IsUsed==0){//未使用
                    //验证优惠券是否可用
                    ticketIsAvailableNet=new TicketIsAvailableNet(mContext);
                    ticketIsAvailableNet.setData(getUserTicketInfoBeanList.get(position).Id,discount,goodsListJson);

                    ticketTitle=getUserTicketInfoBeanList.get(position).Title;
                    ticketMoney=getUserTicketInfoBeanList.get(position).Money;
                    couponId=getUserTicketInfoBeanList.get(position).Id;

                    //关闭优优惠券Dialog
                    customGetUseTicketDialog.dismiss();

                }else{//已使用
                    ToastUtils.getLongToast(mContext,"该优惠券已使用！");
                }

            }
        });
    }

    /**
     * 接收挂单列表消息
     * @param msg 挂单列表
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(List<OrderDataBean> msg) {
        if(msg.size()!=0){
            customGetOrderDialog=new CustomGetOrderDialog(mContext,R.style.dialog,R.layout.item_get_order_dialog,msg);
            customGetOrderDialog.show();

        }else{
            ToastUtils.show("没有找到取单信息！");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(ArrayList<GetOrderDataBean> bean) {
        getOrderBadgeView=new BadgeView(this);
        getOrderBadgeView.setTargetView(iv_get_order);
        getOrderBadgeView.setBadgeCount(bean.size());
    }

    /**
     * 接收验证优惠券是否可用消息
     * @param bean 消息实体类
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(ResultTicketIsAvailableBean bean) {

        if(bean.isSuccess()==true && bean.TModel.isUse()==true){

            payTotalPrice=0.0;
            payMent=0.0;
            jsonArray=new JSONArray();
            mOrderItemBeanList=new ArrayList<>();

            //优惠券验证可用
            ticketIsSuccess=true;
            double orderItemPayMent=Double.parseDouble(DecimalFormatUtils.decimalToFormat(ticketMoney/barcodeCodeList.size()));

            for (int i=0;i<barcodeCodeList.size();i++){
                //设置订单数据
                OrderItemBean itemBean=new OrderItemBean();
                itemBean.setBarcodeId(barcodeCodeList.get(i).getBarcodeId());
                itemBean.setTitle(barcodeCodeList.get(i).getTitle());
                itemBean.setBarcodeNo(barcodeCodeList.get(i).getBarcodeCode());
                itemBean.setShopStyleId(barcodeCodeList.get(i).getShopStyleId());
                itemBean.setSaleType(0);
                itemBean.setStylePrice(barcodeCodeList.get(i).getPrice());
                itemBean.setPayMent(barcodeCodeList.get(i).getPayMent() * barcodeCodeList.get(i).getShopCount() - orderItemPayMent);
                itemBean.setQuantity(barcodeCodeList.get(i).getShopCount());

                mOrderItemBeanList.add(itemBean);
                jsonArray.put(itemBean.getOrderItem());
            }

            for (int i=0;i<mOrderItemBeanList.size();i++){
                payMent+=mOrderItemBeanList.get(i).getPayMent() * mOrderItemBeanList.get(i).getQuantity();
                payTotalPrice+=mOrderItemBeanList.get(i).getStylePrice() * mOrderItemBeanList.get(i).getQuantity();
            }

            orderJsonArray=jsonArray;
            Log.i("codeGoodsList","使用优惠券:"+orderJsonArray);

            //实例化Adapter数据适配器
            barcodeShopAdapter=new BarcodeShopAdapter(mContext,mActivity,barcodeCodeList);
            //将Adapter添加到ListView中
            slv_post_hang_order_list.setAdapter(barcodeShopAdapter);

            tv_total_price.setText(Double.parseDouble(DecimalFormatUtils.decimalToFormat(payMent))+"");
            tv_finally_price.setText(Double.parseDouble(DecimalFormatUtils.decimalToFormat(payTotalPrice))+"");

        }else{
            ToastUtils.show("验证优惠券失败！"+bean.getMessage());
        }
    }

    /**
     * 营销活动数据适配器
     */
    public void initAdapter(){
        adapter=new ActivityAdapter(R.layout.item_marketing_activity,marketingActivityBeen);
        adapter.openLoadAnimation();
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);

        //设置item点击事件，使用选择的营销方案
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener( ){

            @Override
            public void SimpleOnItemClick(BaseQuickAdapter adapter, View view, int position) {

//                    if(barcodeCodeList.size()!=0){
                activityPlanName=marketingActivityBeen.get(position).PlanName;//获取所使用的活动方案名称
                styleArray=new JSONArray();//这里需要重新实例化一次，不然商品信息会重复
                Styles style=new Styles();
                if(giftsShopBarcodeId!=null){
                    for (int i=0;i<barcodeCodeList.size();i++){
                        style.setBarcodeId(barcodeCodeList.get(i).getBarcodeId());//条形码ID
                        style.setPayMent(barcodeCodeList.get(i).getPayMent() * barcodeCodeList.get(i).getShopCount());//支付金额
                        style.setQuantity(barcodeCodeList.get(i).getShopCount());//商品数量
                        style.setStylePrice(barcodeCodeList.get(i).getPrice() );//支付价格
                        style.setShopStyleId(barcodeCodeList.get(i).getShopStyleId());//商品ID价格
                        //判断设置赠品的条码id是否和改商品条码ID相等，相等则设置为赠品，默认销售商品
                        if(giftsShopBarcodeId.contains(barcodeCodeList.get(i).getBarcodeId())){
                            style.setStyleType(3);
                        }
                        styleArray.put(style.getStyleArray());
                    }

                }else{
                    for (int i=0;i<barcodeCodeList.size();i++){
                        style.setBarcodeId(barcodeCodeList.get(i).getBarcodeId());//条形码ID
                        style.setPayMent(barcodeCodeList.get(i).getPayMent() * barcodeCodeList.get(i).getShopCount());//支付金额
                        style.setQuantity(barcodeCodeList.get(i).getShopCount());//商品数量
                        style.setStylePrice(barcodeCodeList.get(i).getPrice() );//支付价格
                        style.setShopStyleId(barcodeCodeList.get(i).getShopStyleId());//商品ID价格
                        styleArray.put(style.getStyleArray());
                    }
                }
                //设置活动商品款号数据
                styleJsonArray=styleArray;

                //判断该活动是否限制会员使用
//                        if(marketingActivityBeen.get(position).IsVip==true){
//                            ToastUtils.getLongToast(mContext,"该活动仅限会员使用！");
//
//                        }else{
                //使用营销活动
                getActivityResultNet=new GetActivityResultNet(mContext);
                getActivityResultNet.setData(marketingActivityBeen.get(position).ActivityId,styleJsonArray);
                activityId=marketingActivityBeen.get(position).ActivityId;

                //取消dialog
                customMarketingActivityDialog.cancel();
//                        }
//                    }else{
//                        ToastUtils.getLongToast(mContext,"请先添加商品再使用活动！");
//                    }
            }
        });

    }

    /**
     * 营销活动Adapter
     */
    public class ActivityAdapter extends BaseQuickAdapter<MarketingActivityBean> {
        public ActivityAdapter(int layoutResId, List<MarketingActivityBean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, MarketingActivityBean bean) {
            //判断该活动方案是否属于当前会员
//            if(vipGradeId!=0){
//
//            }else{
//
//            }

            baseViewHolder.setText(R.id.tv_activity_name,bean.Description);
            baseViewHolder.setText(R.id.tv_activity_plan_name,bean.PlanName);
            if(bean.EndDate!=null){//活动限制时间
                baseViewHolder.getView(R.id.tv_activity_enddate).setVisibility(View.VISIBLE);
                baseViewHolder.setText(R.id.tv_activity_enddate,bean.EndDate);
            }else{
                baseViewHolder.getView(R.id.tv_activity_enddate).setVisibility(View.GONE);
            }
        }
    }

    /**
     * 优惠券列表Adapter
     */
    public class GetUseTicketAdapter extends BaseQuickAdapter<GetUserTicketListBean> {
        public GetUseTicketAdapter(int layoutResId, List<GetUserTicketListBean> data) {
            super(layoutResId, data);
        }
        @Override
        protected void convert(BaseViewHolder baseViewHolder, GetUserTicketListBean bean) {
            baseViewHolder.setText(R.id.tv_useticket_name,bean.Title);
            baseViewHolder.setText(R.id.tv_activity_desc,bean.UseRule);
            baseViewHolder.setText(R.id.tv_activity_time,"活动时间:"+bean.DateActive);

            if(bean.IsUsed==1){//已使用
                baseViewHolder.getView(R.id.cdv_bg).setBackgroundResource(R.color.line);
                baseViewHolder.setText(R.id.tv_useticket_isused,"已使用");
            }else{//未使用
                baseViewHolder.getView(R.id.cdv_bg).setBackgroundResource(R.color.refresh_color_2);
            }
        }
    }

    /**
     * 整单作废
     */
    public void orderInvalid(){
        new CBDialogBuilder(DownOrderActivity.this)
                .setTouchOutSideCancelable(true)
                .showCancelButton(true)
                .setTitle("订单作废")
                .setMessage("是否需要把该订单作废？")
                .setConfirmButtonText("确定")
                .setCancelButtonText("取消")
                .setDialogAnimation(CBDialogBuilder.DIALOG_ANIM_SLID_BOTTOM)
                .setButtonClickListener(true, new CBDialogBuilder.onDialogbtnClickListener() {
                    @Override
                    public void onDialogbtnClick(Context context, Dialog dialog, int whichBtn) {
                        switch (whichBtn) {
                            case BUTTON_CONFIRM:
                                OrderInvalidEventBus msg=new OrderInvalidEventBus();
                                msg.setSuccess(true);
                                EventBus.getDefault().post(msg);
                                break;
                            case BUTTON_CANCEL:
                                ToastUtils.show("已取消操作。");
                                break;
                            default:
                                break;
                        }
                    }
                })
                .create().show();
    }

    /**
     * 订单收银
     */
    public void orderCashier(){
        orderCashierNet=new DownOrderCashierNet(mContext);
        if(vipId!=null){//会员
            if(vipId!=null && ticketIsSuccess==true){//会员&优惠券
                orderCashierNet.setData(vipId, mobile, MemberId,nickName,couponId,ticketMoney,Double.parseDouble(DecimalFormatUtils.decimalToFormat(payMent)),payTotalPrice, orderNo, weather,"备注",new MD5().toMD5("SwApp"+orderNo), workShift,userName,orderJsonArray);
            }else{
                if(discountPrice==0.0){
                    if(activityIsSuccess==true){//使用了营销活动未使用折扣
                        orderCashierNet.setData(vipId, mobile, MemberId,nickName, activityResultPayMent,payTotalPrice, orderNo, weather,"备注",new MD5().toMD5("SwApp"+orderNo), workShift,userName,activityId+"",orderJsonArray);

                    }else{
                        orderCashierNet.setData(vipId, mobile, MemberId,nickName, Double.parseDouble(DecimalFormatUtils.decimalToFormat(payMent)),payTotalPrice, orderNo, weather,"备注",new MD5().toMD5("SwApp"+orderNo), workShift,userName,"",orderJsonArray);
                    }
                }else{//使用折扣未使用营销活动
                    orderCashierNet.setData(vipId, mobile, MemberId,nickName, Double.parseDouble(DecimalFormatUtils.decimalToFormat(payMent)),payTotalPrice, orderNo, weather,"备注",new MD5().toMD5("SwApp"+orderNo), workShift,userName,"",orderJsonArray);
                }
            }

        }else{//非会员
            if(discountPrice==0.0){
                if(activityIsSuccess==true) {//使用了营销活动
                    String payOrderNo="LS"+"459280"+AbDateUtil.getCurrentTimeAsNumber()+RandomUtils.getFourRandom();
                    orderCashierNet.setData(Double.parseDouble(DecimalFormatUtils.decimalToFormat(activityResultPayMent)),payTotalPrice,payOrderNo,weather,"备注",new MD5().toMD5("SwApp"+payOrderNo),workShift,userName,activityId+"",orderJsonArray);
                }else{
                    String payOrderNo="LS"+"459280"+AbDateUtil.getCurrentTimeAsNumber()+RandomUtils.getFourRandom();
                    orderCashierNet.setData(Double.parseDouble(DecimalFormatUtils.decimalToFormat(payMent)),payTotalPrice,payOrderNo,weather,"备注",new MD5().toMD5("SwApp"+payOrderNo),workShift,userName,"",orderJsonArray);
                }

            }else{//payMent需修改
                String payOrderNo="LS"+"459280"+AbDateUtil.getCurrentTimeAsNumber()+RandomUtils.getFourRandom();
//                orderCashierNet.setData(discountPrice,payTotalPrice,payOrderNo,weather,"备注",new MD5().toMD5("SwApp"+payOrderNo),workShift,userName,"",orderJsonArray);
                orderCashierNet.setData(Double.parseDouble(DecimalFormatUtils.decimalToFormat(payMent)),payTotalPrice,payOrderNo,weather,"备注",new MD5().toMD5("SwApp"+payOrderNo),workShift,userName,"",orderJsonArray);
                Log.i(Constant.TAG,"payMent:"+Double.parseDouble(DecimalFormatUtils.decimalToFormat(payMent)));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(ResultCreateFromStoreBean bean) {

        if(bean.isSuccess()==true && bean.TModel!=null){
            orderId=bean.TModel.RetailId+"";
            //查询下单信息
            getOrderNet=new GetOrderNet(mContext);
            getOrderNet.setData(bean.TModel.SyncCode);

        }else{
            ToastUtils.show("下单失败！"+bean.getMessage());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(ResultGetOrderBean bean) {

        orderList=new ArrayList<ResultGetOrderBean.OrderDetailBean>();

        if(bean.isSuccess()==true && bean.TModel!=null){
            orderList.addAll(bean.TModel);

            for (int i=0;i<bean.TModel.size();i++){
                payMoney=bean.TModel.get(i).PayPrice;
                orderStatus=bean.TModel.get(i).OrderStatus;
            }

            if(orderStatus!=30 || orderStatus!=40 || orderStatus!=100){
                dialog=new CustomOrderPayDialog(mContext, R.style.dialog, R.layout.item_custom_single_cashier_dialog);
                dialog.show();
                dialog.setCancelable(false);

            }else{
                ToastUtils.show("该订单已支付。");
            }
        }else{
            ToastUtils.show(bean.getMessage());
        }
    }

    /**
     * 自定义取单Dialog
     */
    public class CustomGetOrderDialog extends Dialog {

        int layoutRes;//布局文件
        Context context;
        private GetHangOrderListAdapter getHangOrderListAdapter;
        private RecyclerView rv_get_hang_order_list;//
        private TextView tv_remove;
        private List<OrderDataBean> orderDataBeanArrayList;

        /**
         * 自定义收银主题及布局的构造方法
         * @param context
         * @param theme
         * @param resLayout
         */
        public CustomGetOrderDialog(Context context, int theme,int resLayout,List<OrderDataBean> orderDataBeanArray){
            super(context, theme);
            this.context = context;
            this.layoutRes=resLayout;
            this.orderDataBeanArrayList=orderDataBeanArray;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.setContentView(layoutRes);
            initView();
            initAdapter();
        }

        /**
         * 初始化数据适配器
         */
        public void initAdapter(){
            getHangOrderListAdapter=new GetHangOrderListAdapter(R.layout.item_get_hang_order,orderDataBeanArrayList);
            getHangOrderListAdapter.openLoadAnimation();
            rv_get_hang_order_list.setHasFixedSize(true);
            rv_get_hang_order_list.setLayoutManager(new LinearLayoutManager(context));
            rv_get_hang_order_list.setAdapter(getHangOrderListAdapter);
            //添加RecyclerView 列表Item点击事件
            rv_get_hang_order_list.addOnItemTouchListener(new OnItemClickListener(){
                @Override
                public void SimpleOnItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                    //添加所点击的取单号到pos商品列表中
                    tv_barcode_order_date.setText(orderDataBeanArrayList.get(i).getDate());
                    tv_pos_weather.setText(orderDataBeanArrayList.get(i).getWeather());
                    tv_workshift.setText(orderDataBeanArrayList.get(i).getShift());
                    tv_barcode_order_no.setText(orderDataBeanArrayList.get(i).getOrderNo());
                    tv_pos_grade.setText(orderDataBeanArrayList.get(i).getMobile());
                    tv_pos_sales.setText(orderDataBeanArrayList.get(i).getSalesMan());
                    tv_pos_vips.setText(orderDataBeanArrayList.get(i).getLevel());
                    tv_pos_points.setText(orderDataBeanArrayList.get(i).getPoint());
                    tv_finally_price.setText(orderDataBeanArrayList.get(i).getPayPrice()+"");
                    tv_total_price.setText(orderDataBeanArrayList.get(i).getSalesPrice()+"");

                    GetByBarcodeCode shopBean=new GetByBarcodeCode();
                    shopBean.setBarcodeCode(orderDataBeanArrayList.get(i).getBarcodeCode());
                    shopBean.setStyleCode(orderDataBeanArrayList.get(i).getBarcodeCode());
                    shopBean.setPrice(orderDataBeanArrayList.get(i).getSalesPrice());
                    shopBean.setPayMent(orderDataBeanArrayList.get(i).getPayPrice());
                    shopBean.setShopStyleId(orderDataBeanArrayList.get(i).getId());
                    shopBean.setBarcodeId(orderDataBeanArrayList.get(i).getBarcodeCode());
                    shopBean.setTitle(orderDataBeanArrayList.get(i).getShopName());
                    shopBean.setShopCount(orderDataBeanArrayList.get(i).getShopCount());

                    barcodeCodeList=new ArrayList<GetByBarcodeCode>();
                    barcodeCodeList.add(shopBean);

                    //实例化Adapter数据适配器
                    barcodeShopAdapter=new BarcodeShopAdapter(mContext,mActivity,barcodeCodeList);
                    //将Adapter添加到ListView中
                    slv_post_hang_order_list.setAdapter(barcodeShopAdapter);

                    OrderSQLiteDatabaseUtils.deleteById(mContext,orderDataBeanArrayList.get(i).getId());

                    customGetOrderDialog.dismiss();
                }
            });
        }

        /**
         * 初始化视图控件
         */
        public void initView(){
            rv_get_hang_order_list= (RecyclerView) findViewById(R.id.rv_get_hang_order_list);
            tv_remove= (TextView) findViewById(R.id.tv_remove);
            //清空挂单列表信息
            tv_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OrderSQLiteDatabaseUtils.delete(mContext);
                    OrderSQLiteDatabaseUtils.selectData(mContext);
                }
            });
        }
    }

    /**
     * 自定义商品优惠卷Dialog
     */
    public class CustomOrderCouponsDialog extends Dialog implements View.OnClickListener{

        int layoutRes;//布局文件
        Context context;

        private LinearLayout ll_sure_add_coupons,ll_cancel_add_coupons;

        /**
         * 自定义收银主题及布局的构造方法
         * @param context
         * @param theme
         * @param resLayout
         */
        public CustomOrderCouponsDialog(Context context, int theme,int resLayout){
            super(context, theme);
            this.context = context;
            this.layoutRes=resLayout;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.setContentView(layoutRes);
            initView();
        }

        public void initView(){
            et_add_coupons= (EditText) findViewById(R.id.et_add_coupons);
            iv_add_scan_coupons= (ImageView) findViewById(R.id.iv_add_scan_coupons);
            ll_sure_add_coupons= (LinearLayout) findViewById(R.id.ll_sure_add_coupons);
            ll_cancel_add_coupons= (LinearLayout) findViewById(R.id.ll_cancel_add_coupons);

            ll_sure_add_coupons.setOnClickListener(this);
            ll_cancel_add_coupons.setOnClickListener(this);
            iv_add_scan_coupons.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.iv_add_scan_coupons://添加扫描优惠券
                    Skip.mAddCouponsActivityResult(mActivity, CaptureActivity.class, REQUEST_CONTACT);
                    break;

                case R.id.ll_sure_add_coupons://确定添加优惠券
                    if(!TextUtils.isEmpty(et_add_coupons.getText().toString())){
                        //调用优惠券查询方法
                        getUserTickerInfoListener(et_add_coupons.getText().toString());
                        customOrderCouponsDialog.dismiss();

                    }else{
                        ToastUtils.show("请输入优惠劵码！");
                    }

                    break;

                case R.id.ll_cancel_add_coupons://取消添加优惠券
                    ToastUtils.show(mContext,"已取消添加优惠券！",ToastUtils.CENTER);
                    customOrderCouponsDialog.dismiss();
                    break;
            }
        }
    }

    /**
     *优惠卷查询回调方法
     * @param UniqueCode 优惠券唯一码
     */
    public void getUserTickerInfoListener(String UniqueCode){
        Request<String> request = NoHttp.createStringRequest(Urls.GET_USER_TICKER_INFO, RequestMethod.POST);
        request.add("UniqueCode",UniqueCode);//参考号
        commonNet.requestNetTask(request,getUserTickerInfoListener);
    }

    /**
     * 优惠券查询监听
     */
    private HttpListener<String> getUserTickerInfoListener = new HttpListener<String>()
    {
        @Override
        public void onSucceed(int what, Response<String> response)
        {
            ResultGetUserTicketInfoBean callbackBean=gson.fromJson(response.get(),ResultGetUserTicketInfoBean.class);
            if(callbackBean.isSuccess()==true && callbackBean.TModel!=null){

            }else{
                ToastUtils.getLongToast(mContext,""+callbackBean.getMessage());
            }
        }
        @Override
        public void onFailed(int what, Response<String> response)
        {
            ToastUtils.show(response.get());
        }
    };

    /**
     *优惠券列表回调方法
     * @param FansId 粉丝的id
     */
    public void getUserTickerListListener(String FansId){
        Request<String> request = NoHttp.createStringRequest(Urls.GET_USER_TICKET, RequestMethod.POST);
        request.add("FansId",FansId);//参考号
        commonNet.requestNetTask(request,getUserTickerListListener,1);
    }

    /**
     * 优惠券查询监听【获取优惠券Count】
     */
    private HttpListener<String> getUserTickerCountListener = new HttpListener<String>()
    {
        @Override
        public void onSucceed(int what, Response<String> response)
        {
            ResultGetUserTicketListBean callbackBean=gson.fromJson(response.get(),ResultGetUserTicketListBean.class);
            if(callbackBean.isSuccess()==true && callbackBean.TModel!=null){
                //设置活动红点数量
                ticketBadgeView=new BadgeView(mContext);
                ticketBadgeView.setTargetView(iv_ticket);
                //设置可用优惠卷数量
                ticketBadgeView.setBadgeCount(callbackBean.TModel.size());

            }else{
                ToastUtils.getLongToast(mContext,""+callbackBean.getMessage());
            }
        }
        @Override
        public void onFailed(int what, Response<String> response)
        {
            ToastUtils.show(response.get());
        }
    };

    /**
     * 优惠券查询监听
     */
    private HttpListener<String> getUserTickerListListener = new HttpListener<String>()
    {
        @Override
        public void onSucceed(int what, Response<String> response)
        {
            Log.d(Constant.TAG,"优惠券列表监听result:"+response.get());
            ResultGetUserTicketListBean callbackBean=gson.fromJson(response.get(),ResultGetUserTicketListBean.class);
            if(callbackBean.isSuccess()==true && callbackBean.TModel!=null){
                getUserTicketInfoBeanList=new ArrayList<>();
                getUserTicketInfoBeanList.addAll(callbackBean.TModel);
                //弹出优惠券Dialog
                customGetUseTicketDialog=new CustomGetUseTicketDialog(mContext,R.style.dialog,R.layout.custom_getuseticket_activity_dialog);
                customGetUseTicketDialog.show();
                customGetUseTicketDialog.setCancelable(false);

                initGetUseTicketAdapter();

            }else{
                ToastUtils.getLongToast(mContext,""+callbackBean.getMessage());
            }
        }
        @Override
        public void onFailed(int what, Response<String> response)
        {
            ToastUtils.show(response.get());
        }
    };

    /**
     * 自定义获取优惠券列表Dialog
     */
    public class CustomGetUseTicketDialog extends Dialog implements View.OnClickListener{
        private int layoutRes;//布局文件
        private LinearLayout ll_back_useticket_activity;
        Context context;

        public CustomGetUseTicketDialog(Context context, int theme,int resLayout){
            super(context, theme);
            this.context = context;
            this.layoutRes=resLayout;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.setContentView(layoutRes);
            initView();
        }

        public void initView(){
            ll_back_useticket_activity= (LinearLayout) findViewById(R.id.ll_back_useticket_activity);
            ll_back_useticket_activity.setOnClickListener(this);
            mGetUseTicketRecyclerView= (RecyclerView) findViewById(R.id.rv_useticket_list);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ll_back_useticket_activity://返回
                    customGetUseTicketDialog.dismiss();
                    break;
            }
        }
    }

    /**
     * 自定义营销活动Dialog
     */
    public class CustomMarketingActivityDialog extends Dialog implements View.OnClickListener{
        private int layoutRes;//布局文件
        private LinearLayout ll_back_activity;
        Context context;
        public CustomMarketingActivityDialog(Context context, int theme,int resLayout){
            super(context, theme);
            this.context = context;
            this.layoutRes=resLayout;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.setContentView(layoutRes);
            initView();
        }

        /**
         * 初始化视图控件
         */
        public void initView(){
            ll_back_activity= (LinearLayout) findViewById(R.id.ll_back_activity);
            ll_back_activity.setOnClickListener(this);
            mRecyclerView= (RecyclerView) findViewById(R.id.rv_marketing_activity_list);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ll_back_activity://返回
                    customMarketingActivityDialog.dismiss();
                    break;
            }
        }
    }

    /**
     * 自定义订单折扣Dialog
     */
    public class CustomOrderDiscountDialog extends Dialog implements View.OnClickListener{
        int layoutRes;//布局文件
        Context context;

        private LinearLayout ll_sure_add_discount,ll_cancel_add_discount;

        /**
         * 自定义收银主题及布局的构造方法
         * @param context
         * @param theme
         * @param resLayout
         */
        public CustomOrderDiscountDialog(Context context, int theme,int resLayout){
            super(context, theme);
            this.context = context;
            this.layoutRes=resLayout;
        }
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.setContentView(layoutRes);
            initView();
        }

        public void initView(){
            et_add_discount= (EditText) findViewById(R.id.et_add_discount);
            ll_sure_add_discount= (LinearLayout) findViewById(R.id.ll_sure_add_discount);
            ll_cancel_add_discount= (LinearLayout) findViewById(R.id.ll_cancel_add_discount);

            ll_sure_add_discount.setOnClickListener(this);
            ll_cancel_add_discount.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ll_sure_add_discount://确定添加折扣

                    isDiscount=true;
                    payMoney=0.0;
                    goodsDiscount=et_add_discount.getText().toString();
                    discount=Double.parseDouble(et_add_discount.getText().toString());
                    discountPrice=payTotalPrice * (discount/10);
                    tv_finally_price.setText(DecimalFormatUtils.decimalToFormat(discountPrice));
                    tv_total_price.setText(DecimalFormatUtils.decimalToFormat(payTotalPrice));

                    payMoney=discountPrice;
                    //初始化orderjsonArray
                    jsonArray=new JSONArray();
                    orderJsonArray=new JSONArray();
                    payMent=0.0;

                    for(int i=0;i<mOrderItemBeanList.size();i++){
                        //重新生成OrderJsonArray
                        OrderItemBean itemBean=new OrderItemBean();
                        double discountPayPrice=Double.parseDouble(DecimalFormatUtils.decimalToFormat(mOrderItemBeanList.get(i).getStylePrice() * mOrderItemBeanList.get(i).getQuantity() * (discount/10)));
                        itemBean.setBarcodeId(mOrderItemBeanList.get(i).getBarcodeId());
                        itemBean.setTitle(mOrderItemBeanList.get(i).getTitle());
                        itemBean.setBarcodeNo(mOrderItemBeanList.get(i).getBarcodeNo());
                        itemBean.setShopStyleId(mOrderItemBeanList.get(i).getShopStyleId());
                        itemBean.setStylePrice(mOrderItemBeanList.get(i).getStylePrice());
                        itemBean.setPayMent(discountPayPrice);
                        itemBean.setQuantity(mOrderItemBeanList.get(i).getQuantity());
                        itemBean.setSaleType(0);
                        jsonArray.put(itemBean.getOrderItem());
                        payMent+=Double.parseDouble(DecimalFormatUtils.decimalToFormat(discountPayPrice));
                    }

                    orderJsonArray=jsonArray;//支付订单Array
                    Log.i("codeGoodsList","orderJsonArray:"+orderJsonArray+"==payMent=="+payMent);
                    customOrderDiscountDialog.dismiss();//
                    break;

                case R.id.ll_cancel_add_discount://取消添加折扣
                    ToastUtils.show(mContext,"已取消添加折扣！",ToastUtils.CENTER);
                    customOrderDiscountDialog.dismiss();
                    break;
            }
        }
    }

    /**
     * 自定义支付Dialog
     */
    public class CustomOrderPayDialog extends Dialog{
        int layoutRes;//布局文件
        Context context;
        SwipeMenuRecyclerView rvView;
        LinearLayout ll_cancel_pos_cashier;

        List<ResultPayCategoryBean.PayCategoryBea> categoryBeaList;
        PosPayCategoryAdapter posPayCategoryAdapter;

        /**
         * 自定义收银主题及布局的构造方法
         *
         * @param context
         * @param theme
         * @param resLayout
         */
        public CustomOrderPayDialog(Context context, int theme, int resLayout) {
            super(context, theme);
            this.context = context;
            this.layoutRes = resLayout;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.setContentView(layoutRes);
            initView();
            initPayData();
        }

        public void initPayData() {
            ResultPayCategoryBean bean = JsonUtils.fromJson(mCache.getAsString("GetPayCategory"), ResultPayCategoryBean.class);
            categoryBeaList = bean.TModel;

            posPayCategoryAdapter = new PosPayCategoryAdapter(categoryBeaList,100);
            posPayCategoryAdapter.setOnItemClickListener(onItemClickListener);
            rvView.setAdapter(posPayCategoryAdapter);
        }

        public void initView() {
            ll_cancel_pos_cashier = (LinearLayout) findViewById(R.id.ll_cancel_pos_cashier);
            rvView = (SwipeMenuRecyclerView) findViewById(R.id.rv_view);
            BaseRecyclerView.initRecyclerView(getContext(), rvView, false);
            ll_cancel_pos_cashier.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }

        private com.cesaas.android.pos.listener.OnItemClickListener onItemClickListener = new com.cesaas.android.pos.listener.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                switch (categoryBeaList.get(position).getCategoryType()) {
                    case 2://微信支付
                        if(isDiscount==true){
                            bundle.putDouble("PayMoney",discountPrice);
                            bundle.putDouble("payTotalPrice",payTotalPrice);
                            bundle.putString("goodsDiscount",goodsDiscount);
                            bundle.putInt("Pay",categoryBeaList.get(position).getCategoryType());
                            bundle.putInt("OrderStatus",orderStatus);
                            bundle.putInt("IsPractical",categoryBeaList.get(position).getIsPractical());
                            bundle.putString("OrderId",orderId);
                            bundle.putString("shopNameNick",shopNameNick);
                            bundle.putString("userName",userName);
                            Skip.mNextFroData(mActivity,WeiXinAndAliPayActivity.class,bundle);
                        }else{
                            payMoney=payMent;
                            if(activityId!=0){////使用活动
                                bundle.putDouble("PayMoney",activityResultPayMent);
                            }else{//未使用活动
                                bundle.putDouble("PayMoney",payMoney);
                            }

                            bundle.putDouble("payTotalPrice",payTotalPrice);
                            bundle.putInt("Pay",categoryBeaList.get(position).getCategoryType());
                            bundle.putInt("OrderStatus",orderStatus);
                            bundle.putString("OrderId",orderId);
                            bundle.putInt("IsPractical",categoryBeaList.get(position).getIsPractical());
                            bundle.putString("shopNameNick",shopNameNick);
                            bundle.putString("userName",userName);
                            Skip.mNextFroData(mActivity,WeiXinAndAliPayActivity.class,bundle);
                        }

                        dialog.dismiss();
                        break;

                    case 3://支付宝
                        if(isDiscount==true){
                            bundle.putDouble("PayMoney",discountPrice);
                            bundle.putDouble("payTotalPrice",payTotalPrice);
                            bundle.putString("goodsDiscount",goodsDiscount);
                            bundle.putInt("Pay",categoryBeaList.get(position).getCategoryType());
                            bundle.putInt("OrderStatus",orderStatus);
                            bundle.putString("OrderId",orderId);
                            bundle.putString("shopNameNick",shopNameNick);
                            bundle.putString("userName",userName);
                            bundle.putInt("IsPractical",categoryBeaList.get(position).getIsPractical());
                            Skip.mNextFroData(mActivity,WeiXinAndAliPayActivity.class,bundle);
                        }else{
                            payMoney=payMent;
                            if(activityId!=0){//使用活动
                                bundle.putDouble("PayMoney",activityResultPayMent);
                            }else{//未使用活动
                                bundle.putDouble("PayMoney",payMoney);
                            }
                            bundle.putDouble("payTotalPrice",payTotalPrice);
                            bundle.putInt("Pay",categoryBeaList.get(position).getCategoryType());
                            bundle.putInt("OrderStatus",orderStatus);
                            bundle.putString("OrderId",orderId);
                            bundle.putString("shopNameNick",shopNameNick);
                            bundle.putString("userName",userName);
                            bundle.putInt("IsPractical",categoryBeaList.get(position).getIsPractical());
                            Skip.mNextFroData(mActivity,WeiXinAndAliPayActivity.class,bundle);
                        }
                        dialog.dismiss();
                        break;

                    case 4://银联支付
                        if(isDiscount==true){
                            payMoney=discountPrice;
                            IsPractical=categoryBeaList.get(position).getIsPractical();
                            payType=categoryBeaList.get(position).getCategoryType();
                            //启动银联收银
                            amount = "2";
                            lock[0] = LOCK_WAIT;
                            doConsumeHasTemplate(amount,scanCode);
                        }else{
                            payMoney=payMent;
                            IsPractical=categoryBeaList.get(position).getIsPractical();
                            payType=categoryBeaList.get(position).getCategoryType();
                            //启动银联收银
                            amount = "2";
                            lock[0] = LOCK_WAIT;
                            doConsumeHasTemplate(amount,scanCode);
                        }
                        dialog.dismiss();
                        break;

                    case 5://现金支付
                        payMoney=payMent;
                        payType=categoryBeaList.get(position).getCategoryType();
                        if(orderStatus!=30 || orderStatus!=40 || orderStatus!=100) {
                            //随机生成12位参考号【规则：当前时间+2位随机数】
                            referenceNumber = RandomUtils.getCurrentTimeAsNumber() + RandomUtils.getToFourRandom();
                            //随机生成6位凭证号【规则：当月+4位随机数】
                            traceAuditNumber = RandomUtils.getCurrentTimeMM() + RandomUtils.getFourRandom();

                            if(openId!=null){
                                Log.i(Constant.TAG,"openId:"+referenceNumber+"="+traceAuditNumber+payMoney+"=order="+orderId+"=="+5+"encode:"+prefs.getString("enCode"));
                                getPayListener(referenceNumber,traceAuditNumber,payMoney,orderId,5,IsPractical);
                            }else{
                                Log.i(Constant.TAG,"NotVipId:"+referenceNumber+"="+traceAuditNumber+payMoney+"=order="+orderId+"=="+5+"encode:"+prefs.getString("enCode"));
                                getPayListener(referenceNumber,traceAuditNumber,payMoney,orderId,5,IsPractical);
                            }
                            dialog.dismiss();

                        }else{
                            ToastUtils.show("改订单已支付！");
                            dialog.dismiss();
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }

    public void getPayListener(String RetrievalReferenceNumber,String TraceAuditNumber,double ConsumeAmount,String OrderId,int PayType,int IsPractical){
        Request<String> request = NoHttp.createStringRequest(Urls.PAY_FROM_STORE, RequestMethod.POST);
        request.add("RetrievalReferenceNumber",RetrievalReferenceNumber);//参考号
        request.add("TraceAuditNumber",TraceAuditNumber);//凭证号
        request.add("ConsumeAmount",ConsumeAmount);//消费金额
        request.add("RetailId",OrderId);//支付订单号
        request.add("PayType",PayType);
        request.add("EnCode",prefs.getString("enCode"));//设备EN号
        request.add("IsPractical",IsPractical);//设备EN号
//        request.add("OpenId",openId);//
        commonNet.requestNetTask(request,getPayListener,1);
    }

    /**
     * 收银支付回调方法
     * @param RetrievalReferenceNumber
     * @param TraceAuditNumber
     * @param ConsumeAmount
     * @param OrderId
     * @param PayType
     */
    public void getPayYinLListener(String RetrievalReferenceNumber,String TraceAuditNumber,double ConsumeAmount,String OrderId,int PayType){
        Request<String> request = NoHttp.createStringRequest(Urls.PAY_FROM_STORE, RequestMethod.POST);
        request.add("RetrievalReferenceNumber",RetrievalReferenceNumber);//参考号
        request.add("TraceAuditNumber",TraceAuditNumber);//凭证号
        request.add("ConsumeAmount",ConsumeAmount);//消费金额
        request.add("RetailId",OrderId);//支付订单号
        request.add("PayType",PayType);
        request.add("EnCode",prefs.getString("enCode"));//设备EN号
//        request.add("OpenId",openId);//
        commonNet.requestNetTask(request,getPayYinLListener,1);
    }

    //支付成功回调监听
    private HttpListener<String> getPayYinLListener = new HttpListener<String>()
    {

        @Override
        public void onSucceed(int what, Response<String> response)
        {

            Log.i(Constant.TAG,response.get());
            PayCallbackBean callbackBean=gson.fromJson(response.get(),PayCallbackBean.class);
            if(callbackBean.isSuccess()==true){
                ToastUtils.getLongToast(mContext,"支付成功");

            }else{
                ToastUtils.getLongToast(mContext,"支付失败!"+callbackBean.getMessage());
                Log.i(Constant.TAG,"支付失败!"+callbackBean.getMessage());
            }

        }
        @Override
        public void onFailed(int what, Response<String> response)
        {
            ToastUtils.show(response.get());
        }
    };

    //支付成功回调监听
    private HttpListener<String> getPayListener = new HttpListener<String>()
    {
        @Override
        public void onSucceed(int what, Response<String> response)
        {
            Log.i(Constant.TAG,"支付成功回调监听!"+response.get());

            PayCallbackBean callbackBean=gson.fromJson(response.get(),PayCallbackBean.class);
            if(callbackBean.isSuccess()==true){
                ToastUtils.getLongToast(mContext,"支付成功，订单打印中...");
                setLatticePrinter();

                Runnable mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        mHandler.sendEmptyMessage(1);
                    }
                };
                mHandler .postDelayed(mRunnable, 3000); // 在Handler中执行子线程并延迟3s。

            }else{
                ToastUtils.getLongToast(mContext,"支付失败!"+callbackBean.getMessage());
            }

        }
        @Override
        public void onFailed(int what, Response<String> response)
        {
            ToastUtils.show(response.get());
        }
    };

    /**
     * s设置点阵打印方法
     */
    public void setLatticePrinter(){
        try {
            // 设备可能没有打印机，open会抛异常
            latticePrinter = WeiposImpl.as().openLatticePrinter();
        } catch (Exception e) {
            // TODO: handle exception
        }

        //点阵打印
        if (latticePrinter == null) {
            Toast.makeText(DownOrderActivity.this, "尚未初始化点阵打印sdk，请稍后再试", Toast.LENGTH_SHORT).show();
            return;
        }else{
            // 打印内容赋值
            latticePrinter.setOnEventListener(new IPrint.OnEventListener() {

                @Override
                public void onEvent(final int what, String in) {
                    final String info = in;
                    // 回调函数中不能做UI操作，所以可以使用runOnUiThread函数来包装一下代码块
                    runOnUiThread(new Runnable() {
                        public void run() {
                            final String message = SingleCashierPrinterTools.getPrintErrorInfo(what, info);
                            if (message == null || message.length() < 1) {
                                return;
                            }
                            showResultInfo("打印", "打印结果信息", message);
                        }
                    });
                }
            });

            //以下是设置pos打印信息内容
            latticePrinterOrderBean=new LatticePrinterOrderBean();
            latticePrinterOrderBean.setShopName(shopNameNick);
            latticePrinterOrderBean.setOrderId(orderId);
            latticePrinterOrderBean.setCounterName(shopNameNick);
            latticePrinterOrderBean.setShopClerkName(userName);
            latticePrinterOrderBean.setTraceAuditNumber(traceAuditNumber);
            latticePrinterOrderBean.setPayTitleName("现金支付");

            if(activityIsSuccess==true){//使用活动
                latticePrinterOrderBean.setActivityPlan(activityPlanName);
                latticePrinterOrderBean.setDiscountPrice(activityResultPayMent);
                latticePrinterOrderBean.setTotalPrice(activityResultPayMent);

            }else{//未使用活动
                if(isDiscount==true){
                    latticePrinterOrderBean.setActivityPlan("暂无");
                    latticePrinterOrderBean.setDiscountPrice(payMent);
                    latticePrinterOrderBean.setTotalPrice(payMent);
                }else{
                    latticePrinterOrderBean.setActivityPlan("暂无");
                    latticePrinterOrderBean.setDiscountPrice(payTotalPrice);
                    latticePrinterOrderBean.setTotalPrice(payTotalPrice);
                }

            }

            if(vipId!=null && ticketIsSuccess==true){//会员使用优惠券
                latticePrinterOrderBean.setActivityPlan(ticketTitle);
                latticePrinterOrderBean.setDiscountPrice(ticketMoney);
                latticePrinterOrderBean.setTotalPrice(ticketMoney);
                latticePrinterOrderBean.setVipMobile(mobile);
            }else{//未使用优惠券
                latticePrinterOrderBean.setActivityPlan("暂无");
            }

            if(vipId!=null){//会员
                latticePrinterOrderBean.setVipMobile(mobile);
            }else{//非会员
                latticePrinterOrderBean.setVipMobile("暂无卡号");
            }
            latticePrinterOrderBean.setOriginalPrice(payTotalPrice);

            //打印
            OrderCashierTicketPrinterTools.printLattice(DownOrderActivity.this,latticePrinter,latticePrinterOrderBean,discount);

        }
    }

    /**
     * 清空当前页面数据
     */
    public void clear(){
        barcodeCodeList.clear();
        barcodeCodeList=new ArrayList<>();
        mOrderItemBeanList=new ArrayList<>();
        //实例化Adapter数据适配器
        barcodeShopAdapter=new BarcodeShopAdapter(mContext,mActivity,barcodeCodeList);
        //将Adapter添加到ListView中
        slv_post_hang_order_list.setAdapter(barcodeShopAdapter);

        payMent=0.0;
        payTotalPrice=0.0;
        payMoney=0.0;
        discountPrice=0.0;
        goodsCount=0;

        jsonArray=new JSONArray();
        orderJsonArray=null;

        barcodeCodeList=new ArrayList<>();
        tv_barcode_shop_sum.setText("0件商品");

        String order="LS"+"459280"+AbDateUtil.getCurrentTimeAsNumber()+RandomUtils.getFourRandom();
        tv_barcode_order_no.setText(order);
        tv_pos_grade.setText("");
        tv_pos_vips.setText("");
        tv_pos_points.setText("");
        tv_finally_price.setText("");
        tv_total_price.setText("");
    }

        /**
         * pos打印显示结果信息
         * @param operInfo
         * @param titleHeader
         * @param info
         */
    private void showResultInfo(String operInfo, String titleHeader, String info) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(titleHeader + ":" + info);
        builder.setTitle(operInfo);
        builder.setPositiveButton("确认", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    private void showMsg(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_show_msg.setText(msg);
            }
        });
    }


    /**
     * 添加会员dialog
     *
     * @author FGB
     *
     */
    public class AddVipDialog extends Dialog implements android.view.View.OnClickListener {

        private ImageView iv_add_scan_vip_mobile;
        private LinearLayout ll_sure_vip_mobile,ll_cancel_vip_mobile;
        public String mobile;

        private int REQUEST_CONTACT = 20;
        private Activity activity;

        public AddVipDialog(Context context, Activity activity) {
            this(context, R.style.dialog);
            this.activity=activity;
        }

        public AddVipDialog(Context context, int dialog) {
            super(context, dialog);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            setContentView(R.layout.add_vip_dialog);

            initView();
        }
        public void initView(){
            iv_add_scan_vip_mobile=(ImageView) findViewById(R.id.iv_add_scan_vip_mobile);
            et_vip_mobile=(EditText) findViewById(R.id.et_vip_mobile);
            ll_sure_vip_mobile= (LinearLayout) findViewById(R.id.ll_sure_vip_mobile);
            ll_cancel_vip_mobile= (LinearLayout) findViewById(R.id.ll_cancel_vip_mobile);

            iv_add_scan_vip_mobile.setOnClickListener(this);
            ll_sure_vip_mobile.setOnClickListener(this);
            ll_cancel_vip_mobile.setOnClickListener(this);
        }
        public void mInitShow() {
            show();
        }

        public void hid(){
            dismiss();
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_add_scan_vip_mobile://扫描会员手机号
                    Skip.mAddVipActivityResult(activity, CaptureActivity.class, REQUEST_CONTACT);
                    break;
                case R.id.ll_sure_vip_mobile://确定添加
                    if(!TextUtils.isEmpty(et_vip_mobile.getText().toString())){
                        mobile=et_vip_mobile.getText().toString();
                        if(mobile!=null){
                            Request<String> request = NoHttp.createStringRequest(Urls.QUERY_VIP, RequestMethod.POST);
                            request.add("Type",0);//Type:0 手机号, 1:VIpId
                            request.add("Val",mobile);
                            commonNet.requestNetTask(request,getVipListener,0);
                            dismiss();
                        }
                    }else{
                        ToastUtils.show("请输入手机号!");
                    }
                    break;
                case R.id.ll_cancel_vip_mobile:
//                    cancel();
                    dismiss();
                    ToastUtils.show("已取消添加会员");
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 处理扫描Activity返回的数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode==RESULT_CODE) {
            if(data.getStringExtra("mAddVipResult")!=null && data.getStringExtra("mAddVipResult").equals("011")){
                scanCode= data.getStringExtra("resultCode");
                if(scanCode!=null){
                    et_vip_mobile.setText(scanCode);
                }else{
                    ToastUtils.show("获取扫描结果失败!");
                }
            }

            if(data.getStringExtra("mScanShopResult")!=null && data.getStringExtra("mScanShopResult").equals("009")){
                scanCode= data.getStringExtra("resultCode");
                if(scanCode!=null){
                    Request<String> request = NoHttp.createStringRequest(Urls.GET_BY_BARCODE_CODE, RequestMethod.POST);
                    request.add("BarcodeCode",scanCode);
                    commonNet.requestNetTask(request,getByBarcodeCodeListener);
                }else{
                    ToastUtils.show("获取扫描结果失败!");
                }
            }

            if(data.getStringExtra("mAddCouponsResult")!=null && data.getStringExtra("mAddCouponsResult").equals("017")){
                scanCode= data.getStringExtra("resultCode");
                if(scanCode!=null){
                    couponsScanCode=scanCode;
                    et_add_coupons.setText(couponsScanCode);
                }else{
                    ToastUtils.show("获取扫描结果失败!");
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //vip会员回调监听
    private HttpListener<String> getVipListener = new HttpListener<String>()
    {
        @Override
        public void onSucceed(int what, Response<String> response)
        {
            ShopVipBean bean=gson.fromJson(response.get(),ShopVipBean.class);
            if(bean.isSuccess()==true && bean.TModel!=null){
                AddVipDialog addVipDialog=new AddVipDialog(mContext,mActivity);
                addVipDialog.hid();
                nickName=bean.TModel.getFANS_NAME();
                mobile=bean.TModel.getFANS_MOBILE();
                vipId=bean.TModel.getFANS_ID()+"";
                vipGrade=bean.TModel.getFANS_MOBILE();
                vipGradeId=bean.TModel.getFANS_GRADEID();
                openId=bean.TModel.getFANS_OPENID();
                vipPoint=bean.TModel.getFANS_POINT();
                discount=bean.TModel.getFANS_DISCOUNT();
                MemberId=bean.TModel.getMEMBER_ID()+"";

                tv_pos_grade.setText(vipGrade);
                tv_pos_vips.setText(bean.TModel.getFANS_GRADE());
                tv_pos_points.setText(vipPoint+"");

                //订单号生成规则：LS+VipId+MMddHHmm+4位随机数
                orderNo="LS"+vipId+ AbDateUtil.getCurrentTimeAsNumber()+ RandomUtils.getFourRandom();
                tv_barcode_order_no.setText(orderNo);

                //查询该会员是否有优惠券
                Request<String> request = NoHttp.createStringRequest(Urls.GET_USER_TICKET, RequestMethod.POST);
                request.add("FansId",vipId);//参考号
                commonNet.requestNetTask(request,getUserTickerCountListener,1);

            }else{
                ToastUtils.getLongToast(mContext,"该会员不存在。");
            }
        }
        @Override
        public void onFailed(int what, Response<String> response)
        {
            ToastUtils.show(response.getException().getMessage());
        }
    };

    //获取商品条码订单回调监听
    private HttpListener<String> getByBarcodeCodeListener = new HttpListener<String>()
    {
        @Override
        public void onSucceed(int what, Response<String> response)
        {
            ResultGetByBarcodeCodeBean resultBean= gson.fromJson(response.get(),ResultGetByBarcodeCodeBean.class);
            //设置数据
            shopBean=new GetByBarcodeCode();
            if(resultBean.TModel!=null){
                shopStyleId=resultBean.TModel.getShopStyleId()+"";
                shopBean.setBarcodeCode(resultBean.TModel.getBarcodeCode());
                shopBean.setStyleCode(resultBean.TModel.getStyleCode());
                shopBean.setPrice(resultBean.TModel.getPrice());
                shopBean.setPayMent(resultBean.TModel.getPrice());
                shopBean.setShopStyleId(resultBean.TModel.getShopStyleId());
                shopBean.setBarcodeId(resultBean.TModel.getBarcodeId());
                shopBean.setTitle(resultBean.TModel.getTitle());
                shopBean.setShopCount(resultBean.TModel.getShopCount());
                shopBean.setImageUrl(resultBean.TModel.getImageUrl());

                if(barcodeCodeList.size()!=0){
                    for (int i=0; i<barcodeCodeList.size(); i++) {
                        //判断扫描添加的商品条码ID是否和已有商品条码ID相等
                        if (barcodeCodeList.get(i).getBarcodeId().equals(resultBean.TModel.getBarcodeId())) {
                            barcodeCodeList.get(i).setShopCount(barcodeCodeList.get(i).getShopCount() + 1);
                            break;// 中断循环
                        }else{
                            //代表循环到最后的时候，if 不相等 走else ，再添加一行数据
                            if((i+1) == barcodeCodeList.size()){
                                barcodeCodeList.add(shopBean);
                                break;// 中断循环
                            }
                        }
                    }
                }else{
                    barcodeCodeList.add(shopBean);
                }

                //实例化Adapter数据适配器
                barcodeShopAdapter=new BarcodeShopAdapter(mContext,mActivity,barcodeCodeList);
                //将Adapter添加到ListView中
                slv_post_hang_order_list.setAdapter(barcodeShopAdapter);

                GoodsArrayBean goodsBean=new GoodsArrayBean();
                goodsBean.setId(resultBean.TModel.getShopStyleId());//商品id
                goodsBean.setCount(resultBean.TModel.getShopCount());//商品数量
                goodsBean.setPrice(resultBean.TModel.getPrice());//商品支付价
                goodsBean.setPriceOriginal(resultBean.TModel.getPrice());//商品原价

                OrderItemBean itemBean=new OrderItemBean();
                itemBean.setBarcodeId(shopBean.getBarcodeId());
                itemBean.setTitle(shopBean.getTitle());
                itemBean.setBarcodeNo(shopBean.getBarcodeCode());
                itemBean.setShopStyleId(shopBean.getShopStyleId());
                itemBean.setStylePrice(shopBean.getPrice());
                itemBean.setPayMent(shopBean.getPayMent());
                itemBean.setQuantity(shopBean.getShopCount());
                itemBean.setSaleType(0);

                if(mOrderItemBeanList.size()!=0){
                    for(int i=0;i<mOrderItemBeanList.size();i++){
                        if(mOrderItemBeanList.get(i).getBarcodeId().equals(resultBean.TModel.getBarcodeId())){
                            mOrderItemBeanList.get(i).setQuantity(mOrderItemBeanList.get(i).getQuantity() + 1);
                            break;// 中断循环
                        }else{
                            //代表循环到最后的时候，if 不相等 走else ，再添加一行数据
                            if((i+1) == mOrderItemBeanList.size()){
                                mOrderItemBeanList.add(itemBean);
                                break;//中断循环
                            }
                        }
                    }
                }else{
                    mOrderItemBeanList.add(itemBean);
                }

                jsonArray.put(itemBean.getOrderItem());
                goodsArray.put(goodsBean.getGoodsArray());
//
                payTotalPrice+=Double.parseDouble(DecimalFormatUtils.decimalToFormat(shopBean.getPrice() * shopBean.getShopCount()));
                payMent+=Double.parseDouble(DecimalFormatUtils.decimalToFormat(shopBean.getPayMent() * shopBean.getShopCount()));
                goodsCount+=shopBean.getShopCount();

                orderJsonArray=jsonArray;//支付订单Array
                goodsListJson=goodsArray;//优惠劵商品Array
                barcodeArray=shopBarcodeArray;//商品条形码

                tv_barcode_shop_sum.setText(goodsCount+"件商品");
                tv_finally_price.setText(DecimalFormatUtils.decimalToFormat(payMent)+"");
                tv_total_price.setText(DecimalFormatUtils.decimalToFormat(payTotalPrice)+"");
            }else{
                ToastUtils.getLongToast(mContext,"找不到该商品信息。");
            }
        }
        @Override
        public void onFailed(int what, Response<String> response)
        {
            ToastUtils.show(response.getException().getMessage());
        }
    };

    //UserInfo回调监听
    private HttpListener<String> userInfoListener = new HttpListener<String>()
    {
        @Override
        public void onSucceed(int what, Response<String> response)
        {
            UserInfoBean bean= gson.fromJson(response.get(),UserInfoBean.class);
            if(bean.isSuccess()==true && bean.TModel!=null){
                shopNameNick=bean.TModel.getShopName();
                tv_pos_order_shop_name.setText(bean.TModel.getShopName());
//                vipId=bean.TModel.getVipId();
            }
        }
        @Override
        public void onFailed(int what, Response<String> response)
        {
            ToastUtils.show(response.getException().getMessage());
        }
    };

    /**
     * 消费
     */
//    private void doConsumeHasTemplate(final String amount ,final String orderNo) {
//        new Thread() {
//            public void run() {
//                try {
//                    HashMap<String, String> map = new HashMap<String, String>();
//                    map.put("myOrderNo", orderNo);
//                    PosCore.RXiaoFei rXiaoFei = pCore.xiaoFei(amount, map, callBack);
////                    showMsg(
////                            "消费成功:>>>>\n卡号:" + rXiaoFei.primaryAccountNumber + "\n"
////                                    + "参考号:" + rXiaoFei.retrievalReferenceNumber
////                                    + "\n凭证号:" + rXiaoFei.systemTraceAuditNumber
////                                    + "\n消费金额:" + rXiaoFei.amounOfTransactions);
//                    refNum = rXiaoFei.retrievalReferenceNumber;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
//    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(POSMsgBean bean) {
        //银联支付失败消息
        ToastUtils.getLongToast(mActivity,bean.getErrorMsg());
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(POSBus bean) {
        if(bean.getIsPos()==101){

            //调用后台支付接口
            getPayYinLListener(rXiaoFei.retrievalReferenceNumber,rXiaoFei.systemTraceAuditNumber,payMent,orderId,4);

            barcodeCodeList.clear();
            barcodeCodeList=new ArrayList<>();
            mOrderItemBeanList=new ArrayList<>();
            //实例化Adapter数据适配器
            barcodeShopAdapter=new BarcodeShopAdapter(mContext,mActivity,barcodeCodeList);
            //将Adapter添加到ListView中
            slv_post_hang_order_list.setAdapter(barcodeShopAdapter);

            payMent=0.0;
            payTotalPrice=0.0;
            payMoney=0.0;
            discountPrice=0.0;
            goodsCount=0;

            jsonArray=new JSONArray();
            orderJsonArray=null;
            barcodeCodeList=new ArrayList<>();
            tv_barcode_shop_sum.setText("0件商品");

            String order="LS"+"459280"+AbDateUtil.getCurrentTimeAsNumber()+RandomUtils.getFourRandom();
            tv_barcode_order_no.setText(order);
            tv_pos_grade.setText("");
            tv_pos_vips.setText("");
            tv_pos_points.setText("");
            tv_finally_price.setText("");
            tv_total_price.setText("");

        }else{
            Skip.mNext(mActivity, CashierHomeActivity.class);
        }
    }


    /**
     * 消费
     */
    private void doConsumeHasTemplate(final String amount ,final String orderNo) {
        new Thread() {
            public void run() {
                try {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("myOrderNo", orderNo);
                    rXiaoFei= pCore.xiaoFei(amount, map, callBack);

                    refNum = rXiaoFei.retrievalReferenceNumber;

                    posBus=new POSBus();
                    posBus.setIsPos(101);

                    ////直接发布
//                    EventBus.getDefault().post(posBus);
//


                } catch (Exception e) {
                    e.printStackTrace();
                    POSMsgBean posMsgBean=new POSMsgBean();
                    posMsgBean.setErrorMsg(e.getLocalizedMessage());
                    EventBus.getDefault().post(posMsgBean);

                    showMsg(e.getLocalizedMessage());
//                    Log.d(Constant.TAG,"银联ERROR：="+e.getLocalizedMessage());
                }
            }
        }.start();
    }



    /**
     * 显示消费对话框
     * @param core
     * @throws Exception
     */
    private void showConsumeDialog(final PosCore core) throws Exception {
        lock[0] = LOCK_WAIT;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View view = getLayoutInflater().inflate(R.layout.consume_dialog, null);
                final AlertDialog dialog = new AlertDialog.Builder(DownOrderActivity.this).setView(view).setCancelable(false).create();
                dialog.show();

                Button btn_confirm = (Button) view.findViewById(R.id.btn_consume_confiem);
                Button btn_cancel = (Button) view.findViewById(R.id.btn_consume_cancel);
                final EditText ed_consumen_amount = (EditText) view.findViewById(R.id.ed_consume_amount);
                ed_consumen_amount.setText(payMoney+"");//设置支付金额
                btn_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        synchronized (lock) {
                            double amount=Double.parseDouble(ed_consumen_amount.getText().toString());
                            money = (int)(amount*100);
                            lock[0] = LOCK_CONTINUE;
                            lock.notify();
                        }
                        dialog.dismiss();
                    }
                });

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        synchronized (lock) {
                            lock[0] = LOCK_CONTINUE;
                            lock.notify();
                        }
                        dialog.dismiss();
                    }
                });
            }
        });

        // 等待输入
        synchronized (lock) {
            while (true) {
                if (lock[0] == LOCK_WAIT) {
                    try {
                        lock.wait(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }
        }
        core.setXiaoFeiAmount(money+"");//设置消费金额
    }

    /**
     * 收银回调
     */
    class PosCallBack implements IPosCallBack {
        private final PosCore core;

        PosCallBack(PosCore core) {
            this.core = core;
        }

        @Override
        public void onInfo(String s) {
            showMsg(s);
        }

        @Override
        public void onEvent(int eventID, Object[] params) throws Exception {
            switch (eventID) {
                case 110:
                    showMsg("打印票据" + params[0]);

                    break;

                case EVENT_Setting:{
                    core.reprint(refNum);
                    showMsg("doSetting:完成");
                    break;
                }

                case EVENT_Task_start: {
                    showMsg("任务进程开始执行");
                    break;
                }
                case EVENT_Task_end: {
                    showMsg("任务进程执行结束");
                    break;
                }
                case EVENT_CardID_start: {
                    showMsg("读取银行卡信息");
                    break;
                }
                case EVENT_CardID_end: {
                    String cardNum = (String) params[0];
                    if (!TextUtils.isEmpty(cardNum)) {
                        Log.w(Constant.TAG, "卡号为:" + params[0]);
                        showConsumeDialog(core);
                    }
                    break;
                }
                case EVENT_Comm_start: {
                    showMsg("开始网络通信");
                    break;
                }
                case EVENT_Comm_end: {
                    showMsg("网络通信完成");
                    break;
                }
                case EVENT_DownloadPlugin_start: {
                    showMsg("开始下载插件");
                    break;
                }
                case EVENT_DownloadPlugin_end: {
                    showMsg("插件下载完成");
                    break;
                }
                case EVENT_InstallPlugin_start: {
                    showMsg("开始安装插件");
                    break;
                }
                case EVENT_InstallPlugin_end: {
                    showMsg("插件安装完成");
                    break;
                }
                case EVENT_RunPlugin_start: {
                    showMsg("开始启动插件");
                    break;
                }
                case EVENT_RunPlugin_end: {
                    showMsg("插件启动完成");
                    break;
                }

                case EVENT_AutoPrint_start:{
                    showMsg("参考号:" + params[0]);
                    break;
                }

                case EVENT_AutoPrint_end://打印完成
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //执行
                            if(posBus.getIsPos()==101){

                                //调用后台支付接口
                                getPayYinLListener(rXiaoFei.retrievalReferenceNumber,rXiaoFei.systemTraceAuditNumber,payMent,orderId,4);

                                barcodeCodeList.clear();
                                barcodeCodeList=new ArrayList<>();
                                mOrderItemBeanList=new ArrayList<>();
                                //实例化Adapter数据适配器
                                barcodeShopAdapter=new BarcodeShopAdapter(mContext,mActivity,barcodeCodeList);
                                //将Adapter添加到ListView中
                                slv_post_hang_order_list.setAdapter(barcodeShopAdapter);

                                payMent=0.0;
                                payTotalPrice=0.0;
                                payMoney=0.0;
                                discountPrice=0.0;
                                goodsCount=0;

                                jsonArray=new JSONArray();
                                orderJsonArray=null;
                                barcodeCodeList=new ArrayList<>();
                                tv_barcode_shop_sum.setText("0件商品");

                                String order="LS"+"459280"+AbDateUtil.getCurrentTimeAsNumber()+RandomUtils.getFourRandom();
                                tv_barcode_order_no.setText(order);
                                tv_pos_grade.setText("");
                                tv_pos_vips.setText("");
                                tv_pos_points.setText("");
                                tv_finally_price.setText("");
                                tv_total_price.setText("");

                            }else{
                                Skip.mNext(mActivity, CashierHomeActivity.class);
                            }
                        }
                    });
                    break;

                case IPosCallBack.ERR_InTask:{
                    if ((Integer) params[0] == EVENT_NO_PAPER) {
	                        showRePrintDialog();
                    }
                }

                default: {
                    showMsg("Event:" + eventID);
                    break;
                }
            }

        }
    }

    private boolean needRePrint;

    /**
     * 显示重打印按钮
     */
    private void showRePrintDialog() {
        lock[0] = LOCK_WAIT;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder dialog = new AlertDialog.Builder(DownOrderActivity.this);
                dialog.setMessage("打印机缺纸");
                dialog.setPositiveButton("重打印", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        synchronized (lock) {
                            needRePrint = true;
                            lock[0] = LOCK_CONTINUE;
                            lock.notify();
                        }
                    }
                });

                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        synchronized (lock) {
                            needRePrint = false;
                            lock[0] = LOCK_CONTINUE;
                            lock.notify();
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                   //执行
                                    if(posBus.getIsPos()==101){

                                        //调用后台支付接口
                                        getPayYinLListener(rXiaoFei.retrievalReferenceNumber,rXiaoFei.systemTraceAuditNumber,payMent,orderId,4);

                                        barcodeCodeList.clear();
                                        barcodeCodeList=new ArrayList<>();
                                        mOrderItemBeanList=new ArrayList<>();
                                        //实例化Adapter数据适配器
                                        barcodeShopAdapter=new BarcodeShopAdapter(mContext,mActivity,barcodeCodeList);
                                        //将Adapter添加到ListView中
                                        slv_post_hang_order_list.setAdapter(barcodeShopAdapter);

                                        payMent=0.0;
                                        payTotalPrice=0.0;
                                        payMoney=0.0;
                                        discountPrice=0.0;
                                        goodsCount=0;

                                        jsonArray=new JSONArray();
                                        orderJsonArray=null;
                                        barcodeCodeList=new ArrayList<>();
                                        tv_barcode_shop_sum.setText("0件商品");

                                        String order="LS"+"459280"+AbDateUtil.getCurrentTimeAsNumber()+RandomUtils.getFourRandom();
                                        tv_barcode_order_no.setText(order);
                                        tv_pos_grade.setText("");
                                        tv_pos_vips.setText("");
                                        tv_pos_points.setText("");
                                        tv_finally_price.setText("");
                                        tv_total_price.setText("");

                                    }else{
                                        Skip.mNext(mActivity, CashierHomeActivity.class);
                                    }
                                }
                            });
                        }
                    }
                });
                dialog.setCancelable(false);
                dialog.show();
            }
        });


        // 等待输入
        synchronized (lock) {
            while (true) {
                if (lock[0] == LOCK_WAIT) {
                    try {
                        lock.wait(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }
        }


        try {
            pCore.printContinue(needRePrint);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 自定义天气Dialog
     * @author FGB
     *
     */
    public class CustomWeatherDialog extends Dialog{

        int layoutRes;//布局文件
        Context context;

        public CustomWeatherDialog(Context context,int theme,int resLayout) {
            super(context, theme);
            this.context = context;
            this.layoutRes=resLayout;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.setContentView(layoutRes);
            lvWeather=(ListView) findViewById(R.id.lv_custom_weather);
        }

    }

    /**
     * 自定义班次Dialog
     * @author FGB
     *
     */
    public class CustomWorkShiftDialog extends Dialog{

        int layoutRes;//布局文件
        Context context;

        public CustomWorkShiftDialog(Context context,int theme,int resLayout) {
            super(context, theme);
            this.context = context;
            this.layoutRes=resLayout;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.setContentView(layoutRes);
            lvWorkShift=(ListView) findViewById(R.id.lv_custom_workshift);
        }

    }

    @Override
    protected void onDestroy() {
        //取消EventBus订阅
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
