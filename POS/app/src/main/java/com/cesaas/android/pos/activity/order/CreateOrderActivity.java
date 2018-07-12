package com.cesaas.android.pos.activity.order;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.activity.cashier.WeiXinAndAliPayActivity;
import com.cesaas.android.pos.adapter.listviewadapter.adapterlibrary.ViewHolder;
import com.cesaas.android.pos.adapter.listviewadapter.adapterlibrary.abslistview.CommonAdapter;
import com.cesaas.android.pos.adapter.order.BarcodeOrderAdapter;
import com.cesaas.android.pos.base.BaseActivity;
import com.cesaas.android.pos.bean.GetByBarcodeCode;
import com.cesaas.android.pos.bean.GoodsArrayBean;
import com.cesaas.android.pos.bean.MarketingActivityBean;
import com.cesaas.android.pos.bean.OrderInvalidEventBus;
import com.cesaas.android.pos.bean.OrderItemBean;
import com.cesaas.android.pos.bean.PayCallbackBean;
import com.cesaas.android.pos.bean.ResultActivityResultBean;
import com.cesaas.android.pos.bean.ResultCreateFromStoreBean;
import com.cesaas.android.pos.bean.ResultGetByBarcodeCodeBean;
import com.cesaas.android.pos.bean.ResultMarketingActivityBean;
import com.cesaas.android.pos.bean.ResultWeatherBean;
import com.cesaas.android.pos.bean.ResultWorkShiftBean;
import com.cesaas.android.pos.bean.ShopVipBean;
import com.cesaas.android.pos.bean.Styles;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.global.Urls;
import com.cesaas.android.pos.listener.OnItemClickListener;
import com.cesaas.android.pos.net.nohttp.HttpListener;
import com.cesaas.android.pos.net.xutils.net.DownOrderCashierNet;
import com.cesaas.android.pos.net.xutils.net.GetActivityResultNet;
import com.cesaas.android.pos.net.xutils.net.MarketingActivityNet;
import com.cesaas.android.pos.utils.AbDateUtil;
import com.cesaas.android.pos.utils.DecimalFormatUtils;
import com.cesaas.android.pos.utils.MD5;
import com.cesaas.android.pos.utils.RandomUtils;
import com.cesaas.android.pos.utils.Skip;
import com.cesaas.android.pos.utils.ToastUtils;
import com.cesaas.android.pos.view.ListViewDecoration;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.wangpos.poscore.IPosCallBack;
import com.wangpos.poscore.PosCore;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
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

import scanner.CaptureActivity;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：创建订单
 * 创建日期：2017/2/7 10:52
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class CreateOrderActivity extends BaseActivity implements View.OnClickListener{

    private ListView lvWeather;//天气
    private ListView lvWorkShift;//班次
    private RecyclerView mRecyclerView;
    private SwipeMenuRecyclerView mSwipeMenuRecyclerView;
    private LinearLayout ll_pos_down_order_back,ll_create_order_cashier,ll_discount_order,ll_coupons_order,ll_take_order,ll_hang_order;
    private LinearLayout ll_order_weixin_pay,ll_order_ali_pay,ll_order_union_pay,ll_order_cash_pay,ll_pos_activity;
    private TextView tv_add_barcode_shop,tv_barcode_shop_sum,tv_total_price,tv_payment,tv_order_pay_back,tv_workshift,tv_weather,tv_add_vip;
    private TextView tv_pos_grade,tv_pos_vips,tv_pos_sales,tv_pos_points,tv_barcode_order_no,tv_barcode_order_date,tv_pos_orders_invalid;
    private TextView tv_activity;
    private EditText et_vip_mobile;

    final int RESULT_CODE = 101;
    private int REQUEST_CONTACT = 20;
    private byte[] lock = new byte[1];
    int EVENT_NO_PAPER = 1;
    private final int LOCK_WAIT = 0;
    private final int LOCK_CONTINUE = 1;
    //8583协议中的参考号
    private String refNum;
    private PosCore pCore;
    private PosCallBack callBack;
    private EditText tv_show_msg;
    private String money;
    private String amount;
    private String couponsScanCode;//优惠价扫描码
    private String scanCode;//扫描码
    private String weather;//天气
    private String workShift;//班次
    private String orderNo;//单号
    private String nickName;
    private String mobile;
    private String vipId;//会员ID
    private String vipGrade;//会员等级
    private String openId;
    private String orderId;//订单号
    private String userName;//用户名【营业员】
    private String shopNameNick;//店铺名
    private String referenceNumber;//参考号
    private String traceAuditNumber;//凭证号
    private String shopStyleId;//商品id
    private String giftsShopBarcodeId;//赠品ID
    private double payMoney;
    private double payTotalPrice=0.0;//Order支付总价
    private double payMent=0.0;//Order支付金额
    private double discountPrice=0.0;//折后价格
    private double activityResultPayMent;//营销活动支付金额
    private double activityStyleTotalPrice;
    private boolean activityIsSuccess=false;
    private double discount;//全局商品折扣
    private int vipGradeId;//会员等级
    private int vipPoint;
    private int orderStatus;//订单状态
    private int payType;//订单支付类型
    private int activityId;//营销活动ID
    private int giftQuantity;
    private int giftQuantitySum;

    private CustomWorkShiftDialog customWorkShiftDialog;//班次Dialog
    private CustomWeatherDialog customWeatherDialog;//天气Dialog
    private CustomMarketingActivityDialog customMarketingActivityDialog;//自定义营销活动Dialog

    private ActivityAdapter activityAdapter;
    private MarketingActivityNet marketingActivityNet;
    private List<MarketingActivityBean> marketingActivityBeen=new ArrayList<MarketingActivityBean>();

    private JSONArray orderJsonArray;
    private JSONArray styleJsonArray;
    public JSONArray goodsArray=new JSONArray();
    public JSONArray jsonArray=new JSONArray();
    public JSONArray styleArray=new JSONArray();

    private CustomOrderPayDialog dialog;//支付Dialog

    private GetActivityResultNet getActivityResultNet;
    private DownOrderCashierNet orderCashierNet;//下单收银请求接口
    private BarcodeOrderAdapter adapter;//条码订单Adapter
    private GetByBarcodeCode orderBean;//
    //条码订单集合
    private List<GetByBarcodeCode> barcodeCodeList = new ArrayList<>();
    //班次集合
    private ArrayList<ResultWorkShiftBean.WorkShiftBean> workShiftList;
    //天气集合
    private ArrayList<ResultWeatherBean.WeatherBean> weatherList;
    //营销活动结果集
    private List<ResultActivityResultBean.Styles> activityStylesArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);

        //营销活动
        marketingActivityNet=new MarketingActivityNet(mContext);
        marketingActivityNet.setData();

        initView();
    }

    /**
     * 菜单创建器。在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.item_height);

            // MATCH_PARENT 自适应高度，保持和内容一样高；也可以指定菜单具体高度，也可以用WRAP_CONTENT。
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            // 添加左侧的，如果不添加，则左侧不会出现菜单。
            {
                SwipeMenuItem addItem = new SwipeMenuItem(mContext)
                        .setBackgroundDrawable(R.drawable.selector_green)// 点击的背景。
                        .setImage(R.mipmap.ic_action_wechat) // 图标。
                        .setText("赠品")
                        .setTextColor(Color.WHITE)
                        .setWidth(width) // 宽度。
                        .setHeight(height); // 高度。
                swipeLeftMenu.addMenuItem(addItem); // 添加一个按钮到左侧菜单。
            }

            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(mContext)
                        .setBackgroundDrawable(R.drawable.selector_red)
                        .setImage(R.mipmap.ic_action_delete)
                        .setText("删除") // 文字，还可以设置文字颜色，大小等。。
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。

                SwipeMenuItem closeItem = new SwipeMenuItem(mContext)
                        .setBackgroundDrawable(R.drawable.selector_purple)
                        .setImage(R.mipmap.ic_action_wechat)
                        .setText("价格")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(closeItem); // 添加一个按钮到右侧菜单。

                SwipeMenuItem addItem = new SwipeMenuItem(mContext)
                        .setBackgroundDrawable(R.drawable.selector_green)
                        .setImage(R.mipmap.ic_action_add)
                        .setText("数量")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(addItem); // 添加一个按钮到右侧菜单。
            }
        }
    };

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            Toast.makeText(mContext, "我是第" + position + "条。", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 菜单点击监听。
     */
    private OnSwipeMenuItemClickListener menuItemClickListener = new OnSwipeMenuItemClickListener() {
        /**
         * Item的菜单被点击的时候调用。
         * @param closeable       closeable. 用来关闭菜单。
         * @param adapterPosition adapterPosition. 这个菜单所在的item在Adapter中position。
         * @param menuPosition    menuPosition. 这个菜单的position。比如你为某个Item创建了2个MenuItem，那么这个position可能是是 0、1，
         * @param direction       如果是左侧菜单，值是：SwipeMenuRecyclerView#LEFT_DIRECTION，如果是右侧菜单，值是：SwipeMenuRecyclerView#RIGHT_DIRECTION.
         */
        @Override
        public void onItemClick(Closeable closeable, int adapterPosition, int menuPosition, int direction) {
            closeable.smoothCloseMenu();// 关闭被点击的菜单。

            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
                if(menuPosition==0){//删除
                    barcodeCodeList.remove(adapterPosition);
                    adapter.notifyItemRemoved(adapterPosition);
                    Toast.makeText(mContext, "删除"+barcodeCodeList.get(adapterPosition).getTitle(), Toast.LENGTH_SHORT).show();
                }else if(menuPosition==1){
                    Toast.makeText(mContext, "擦XX"+barcodeCodeList.get(adapterPosition).getTitle(), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(mContext, "添加"+barcodeCodeList.get(adapterPosition).getTitle(), Toast.LENGTH_SHORT).show();
                }

            } else if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION) {
                // TODO 如果是删除：推荐调用Adapter.notifyItemRemoved(position)，不推荐Adapter.notifyDataSetChanged();
                if (menuPosition == 0) {// 赠品
                    Toast.makeText(mContext, "赠品"+barcodeCodeList.get(adapterPosition).getTitle(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    /**
     * 处理扫描Activity返回的数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //获取商品条码信息
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
    }

    //获取商品条码订单回调监听
    private HttpListener<String> getByBarcodeCodeListener = new HttpListener<String>()
    {
        @Override
        public void onSucceed(int what, Response<String> response)
        {
            ResultGetByBarcodeCodeBean resultBean= gson.fromJson(response.get(),ResultGetByBarcodeCodeBean.class);
           if(resultBean.isSuccess()==true && resultBean.TModel!=null){
               //实例化订单条码数据
               orderBean=new GetByBarcodeCode();
               shopStyleId=resultBean.TModel.getShopStyleId()+"";
               orderBean.setBarcodeCode(resultBean.TModel.getBarcodeCode());
               orderBean.setStyleCode(resultBean.TModel.getStyleCode());
               orderBean.setPrice(resultBean.TModel.getPrice());
               orderBean.setPayMent(resultBean.TModel.getPrice());
               orderBean.setShopStyleId(resultBean.TModel.getShopStyleId());
               orderBean.setBarcodeId(resultBean.TModel.getBarcodeId());
               orderBean.setTitle(resultBean.TModel.getTitle());
               orderBean.setShopCount(resultBean.TModel.getShopCount());
               orderBean.setImageUrl(resultBean.TModel.getImageUrl());

               //调用初始化条码订单数据方法
               initOrderData();

               GoodsArrayBean goodsBean=new GoodsArrayBean();
               goodsBean.setId(resultBean.TModel.getShopStyleId());//商品id
               goodsBean.setCount(resultBean.TModel.getShopCount());//商品数量
               goodsBean.setPriceOriginal(resultBean.TModel.getPrice());//商品原价

               //实例化OrderItem数据
               OrderItemBean itemBean=new OrderItemBean();
               itemBean.setBarcodeId(orderBean.getBarcodeId());
               itemBean.setTitle(orderBean.getTitle());
               itemBean.setBarcodeNo(orderBean.getBarcodeCode());
               itemBean.setShopStyleId(orderBean.getShopStyleId());
               itemBean.setStylePrice(orderBean.getPrice());
               itemBean.setPayMent(orderBean.getPayMent());
               itemBean.setQuantity(orderBean.getShopCount());
               itemBean.setSaleType(0);
               //
               jsonArray.put(itemBean.getOrderItem());
               goodsArray.put(goodsBean.getGoodsArray());

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

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(ResultCreateFromStoreBean bean) {

        if(bean.isSuccess()==true && bean.TModel!=null){
            //获取订单号
            orderId=bean.TModel.SyncCode;
            //显示订单支付方式
            dialog=new CustomOrderPayDialog(mContext, R.style.dialog, R.layout.item_custom_order_pay_dialog);
            dialog.show();
        }else{
            ToastUtils.show("下单失败！"+bean.getMessage());
        }
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
            adapter = new BarcodeOrderAdapter(barcodeCodeList);
            adapter.setOnItemClickListener(onItemClickListener);
            mSwipeMenuRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            tv_payment.setText(DecimalFormatUtils.decimalToFormat(activityResultPayMent));//结算
            tv_total_price.setText(DecimalFormatUtils.decimalToFormat(payTotalPrice));//总价

            activityIsSuccess=true;
            ToastUtils.show("使用使用优惠活动成功!");
        }else{
            ToastUtils.show("使用失败："+bean.getMessage());
        }
    }

    /**
     * 接收整单作废消息
     * @param msg 消息实体类
     */
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(OrderInvalidEventBus msg) {
        if(msg.isSuccess()==true){
            barcodeCodeList.clear();
//            initOrderData();
            //设置Adapter数据
            adapter = new BarcodeOrderAdapter(barcodeCodeList);
            adapter.setOnItemClickListener(onItemClickListener);
            mSwipeMenuRecyclerView.setAdapter(adapter);

            payMent=0.0;
            payTotalPrice=0.0;
            jsonArray=new JSONArray();
            orderJsonArray=null;

            tv_barcode_shop_sum.setText(barcodeCodeList.size()+"件商品");
            tv_payment.setText(DecimalFormatUtils.decimalToFormat(payMent)+"");
            tv_total_price.setText(DecimalFormatUtils.decimalToFormat(payTotalPrice)+"");

            ToastUtils.getLongToast(mContext,"订单作废成功,请重新下单！");
        }else{
            ToastUtils.getLongToast(mContext,"订单作废失败！");
        }
    }

    //支付成功回调监听
    private HttpListener<String> getPayListener = new HttpListener<String>()
    {
        @Override
        public void onSucceed(int what, Response<String> response)
        {
            PayCallbackBean callbackBean=gson.fromJson(response.get(),PayCallbackBean.class);
            if(callbackBean.isSuccess()==true){
                ToastUtils.getLongToast(mContext,"订单支付成功!");

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
                vipGrade=bean.TModel.getFANS_GRADE();
                vipGradeId=bean.TModel.getFANS_GRADEID();
                openId=bean.TModel.getFANS_OPENID();
                vipPoint=bean.TModel.getFANS_POINT();

                tv_pos_grade.setText(vipGrade);
                tv_pos_sales.setText(userName);
                tv_pos_vips.setText(bean.TModel.getFANS_NAME());
                tv_pos_points.setText(vipPoint+"");

                //订单号生成规则：LS+VipId+MMddHHmm+4位随机数
                orderNo="LS"+vipId+ AbDateUtil.getCurrentTimeAsNumber()+ RandomUtils.getFourRandom();
                tv_barcode_order_no.setText(orderNo);
                tv_barcode_order_date.setText(AbDateUtil.getCurrentTate());

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

    /**
     * 初始化视图控件
     */
    public void initView(){
        mSwipeMenuRecyclerView = (SwipeMenuRecyclerView) findViewById(R.id.recycler_view);
        mSwipeMenuRecyclerView.setLayoutManager(new LinearLayoutManager(this));// 布局管理器。
        mSwipeMenuRecyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        mSwipeMenuRecyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        mSwipeMenuRecyclerView.addItemDecoration(new ListViewDecoration());// 添加分割线。

        ll_pos_down_order_back= (LinearLayout) findViewById(R.id.ll_pos_down_order_back);
        ll_create_order_cashier= (LinearLayout) findViewById(R.id.ll_create_order_cashier);
        ll_pos_activity= (LinearLayout) findViewById(R.id.ll_pos_activity);
        ll_discount_order= (LinearLayout) findViewById(R.id.ll_discount_order);
        ll_coupons_order= (LinearLayout) findViewById(R.id.ll_coupons_order);
        ll_take_order= (LinearLayout) findViewById(R.id.ll_take_order);
        ll_hang_order= (LinearLayout) findViewById(R.id.ll_hang_order);

        tv_add_barcode_shop= (TextView) findViewById(R.id.tv_add_barcode_shop);
        tv_barcode_shop_sum= (TextView) findViewById(R.id.tv_barcode_shop_sum);
        tv_total_price= (TextView) findViewById(R.id.tv_total_price);
        tv_payment= (TextView) findViewById(R.id.tv_payment);
        tv_workshift= (TextView) findViewById(R.id.tv_workshift);
        tv_weather= (TextView) findViewById(R.id.tv_weather);
        tv_add_vip= (TextView) findViewById(R.id.tv_add_vip);
        tv_pos_grade= (TextView) findViewById(R.id.tv_pos_grade);
        tv_pos_vips= (TextView) findViewById(R.id.tv_pos_vips);
        tv_pos_sales= (TextView) findViewById(R.id.tv_pos_sales);
        tv_pos_points= (TextView) findViewById(R.id.tv_pos_points);
        tv_barcode_order_no= (TextView) findViewById(R.id.tv_barcode_order_no);
        tv_barcode_order_date=(TextView)findViewById(R.id.tv_pos_date);
        tv_pos_orders_invalid= (TextView) findViewById(R.id.tv_pos_orders_invalid);
        tv_activity= (TextView) findViewById(R.id.tv_activity);

        ll_hang_order.setOnClickListener(this);
        ll_take_order.setOnClickListener(this);
        ll_coupons_order.setOnClickListener(this);
        ll_discount_order.setOnClickListener(this);
        tv_add_barcode_shop.setOnClickListener(this);
        ll_pos_down_order_back.setOnClickListener(this);
        ll_create_order_cashier.setOnClickListener(this);
        tv_workshift.setOnClickListener(this);
        tv_weather.setOnClickListener(this);
        tv_add_vip.setOnClickListener(this);
        tv_pos_orders_invalid.setOnClickListener(this);
        tv_activity.setOnClickListener(this);
        ll_pos_activity.setOnClickListener(this);
    }

    /**
     * 初始化订单数据
     */
    public void initOrderData(){
        barcodeCodeList.add(orderBean);

        // 为SwipeRecyclerView的Item创建菜单
        // 设置订单创建器。
        mSwipeMenuRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
        // 设置订单Item点击监听。
        mSwipeMenuRecyclerView.setSwipeMenuItemClickListener(menuItemClickListener);
        //设置Adapter数据
        adapter = new BarcodeOrderAdapter(barcodeCodeList);
        adapter.setOnItemClickListener(onItemClickListener);
        mSwipeMenuRecyclerView.setAdapter(adapter);
        //设置商品件数
        tv_barcode_shop_sum.setText(barcodeCodeList.size()+"件商品");
        //总价
        payTotalPrice+=Double.parseDouble(DecimalFormatUtils.decimalToFormat(orderBean.getPrice() * orderBean.getShopCount()));
        tv_total_price.setText(payTotalPrice+"");
        //支付金额
        payMent+=Double.parseDouble(DecimalFormatUtils.decimalToFormat(orderBean.getPayMent() * orderBean.getShopCount()));
        tv_payment.setText(payMent+"");
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
                    PosCore.RXiaoFei rXiaoFei = pCore.xiaoFei(amount, map, callBack);
                    showMsg(
                            "消费成功:>>>>\n卡号:" + rXiaoFei.primaryAccountNumber + "\n"
                                    + "参考号:" + rXiaoFei.retrievalReferenceNumber
                                    + "\n凭证号:" + rXiaoFei.systemTraceAuditNumber
                                    + "\n消费金额:" + rXiaoFei.amounOfTransactions);
                    refNum = rXiaoFei.retrievalReferenceNumber;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 收银支付回调方法
     * @param RetrievalReferenceNumber
     * @param TraceAuditNumber
     * @param ConsumeAmount
     * @param OrderId
     * @param PayType
     */
    public void getPayListener(String RetrievalReferenceNumber,String TraceAuditNumber,double ConsumeAmount,String OrderId,int PayType,String openId){
        Request<String> request = NoHttp.createStringRequest(Urls.PAY_FROM_STORE, RequestMethod.POST);
        request.add("RetrievalReferenceNumber",RetrievalReferenceNumber);//参考号
        request.add("TraceAuditNumber",TraceAuditNumber);//凭证号
        request.add("ConsumeAmount",ConsumeAmount);//消费金额
        request.add("RetailId",OrderId);//支付订单号
        request.add("PayType",PayType);
        request.add("EnCode",prefs.getString("enCode"));//设备EN号
//        request.add("OpenId",openId);//
        commonNet.requestNetTask(request,getPayListener,1);
    }

    /**
     * 订单收银
     */
    public void orderCashier(){
        orderCashierNet=new DownOrderCashierNet(mContext);
        if(vipId!=null){//会员
            if(discountPrice==0.0){
                if(activityIsSuccess==true){//使用了营销活动//
                    orderCashierNet.setData(vipId, mobile, openId,nickName, activityResultPayMent,payTotalPrice, orderNo, weather,"备注",new MD5().toMD5("SwApp"+orderNo), workShift,userName,activityId+"",orderJsonArray);
                }else{
                    orderCashierNet.setData(vipId, mobile, openId,nickName, payMent,payTotalPrice, orderNo, weather,"备注",new MD5().toMD5("SwApp"+orderNo), workShift,userName,"",orderJsonArray);
                }

            }else{
                orderCashierNet.setData(vipId, mobile, openId,nickName, payMent,payTotalPrice, orderNo, weather,"备注",new MD5().toMD5("SwApp"+orderNo), workShift,userName,"",orderJsonArray);
            }

        }else{//非会员
            if(discountPrice==0.0){
                if(activityIsSuccess==true) {//使用了营销活动
                    String payOrderNo="LS"+"459280"+ AbDateUtil.getCurrentTimeAsNumber()+ RandomUtils.getFourRandom();
                    orderCashierNet.setData(activityResultPayMent,payTotalPrice,payOrderNo,weather,"备注",new MD5().toMD5("SwApp"+payOrderNo),workShift,userName,activityId+"",orderJsonArray);
                }else{
                    String payOrderNo="LS"+"459280"+AbDateUtil.getCurrentTimeAsNumber()+RandomUtils.getFourRandom();
                    orderCashierNet.setData(payMent,payTotalPrice,payOrderNo,weather,"备注",new MD5().toMD5("SwApp"+payOrderNo),workShift,userName,"",orderJsonArray);
                }

            }else{//payMent需修改
                String payOrderNo="LS"+"459280"+AbDateUtil.getCurrentTimeAsNumber()+RandomUtils.getFourRandom();
                orderCashierNet.setData(discountPrice,payTotalPrice,payOrderNo,weather,"备注",new MD5().toMD5("SwApp"+payOrderNo),workShift,userName,"",orderJsonArray);
            }
        }
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
                tv_weather.setText(weatherList.get(position).getWeatherType());
                weather=tv_weather.getText().toString();
                customWeatherDialog.dismiss();
            }
        });
    }

    /**
     * 整单作废
     */
    public void orderInvalid(){
        new CBDialogBuilder(CreateOrderActivity.this)
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

    private void showMsg(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_show_msg.setText(msg);
            }
        });
    }


    /**
     * Called when a view has been clicked.
     *点击事件处理
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_pos_down_order_back://返回
                Skip.mBack(mActivity);
                break;

            case R.id.tv_workshift://班次
                customWorkShiftDialog=new CustomWorkShiftDialog(mContext, R.style.dialog, R.layout.custom_workshift_dialog);
                customWorkShiftDialog.show();

                Request<String> request = NoHttp.createStringRequest(Urls.GET_WorkSHIFT, RequestMethod.GET);
                commonNet.requestNetTask(request,getWorkShiftListener);
                break;

            case R.id.tv_weather://天气
                customWeatherDialog=new CustomWeatherDialog(mContext, R.style.dialog, R.layout.custom_weather_dialog);
                customWeatherDialog.show();

                Request<String> requesth = NoHttp.createStringRequest(Urls.GET_WEATHER, RequestMethod.GET);
                commonNet.requestNetTask(requesth,getWeatherListener);
                break;

            case R.id.tv_add_vip://添加会员
                new AddVipDialog(mContext,mActivity).mInitShow();
                break;

            case R.id.tv_pos_orders_invalid://整单作废
                if(barcodeCodeList.size()!=0){
                    orderInvalid();
                }else{
                    ToastUtils.getLongToast(mContext,"找不到订单！");
                }
                break;

            case R.id.tv_add_barcode_shop://扫描添加商品
                Skip.mScanShopActivityResult(mActivity, CaptureActivity.class, REQUEST_CONTACT);
                break;

            case R.id.ll_pos_activity://活动

                customMarketingActivityDialog=new CustomMarketingActivityDialog(mContext,R.style.dialog,R.layout.custom_marketing_activity_dialog);
                customMarketingActivityDialog.show();
                customMarketingActivityDialog.setCancelable(false);
                getActivityListener();
                break;

            case R.id.ll_discount_order://折扣

                break;

            case R.id.ll_coupons_order://优惠券

                break;

            case R.id.ll_take_order://取单

                break;

            case R.id.ll_hang_order://挂单

                break;

            case R.id.ll_create_order_cashier://创建订单收银
                orderCashier();
                break;
        }
    }

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
            marketingActivityBeen=new ArrayList<MarketingActivityBean>();
            ResultMarketingActivityBean bean = gson.fromJson(response.get(), ResultMarketingActivityBean.class);
            if(bean.isSuccess()==true && bean.TModel!=null){
                marketingActivityBeen.addAll(bean.TModel);
                initActivityAdapter();
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
     * 营销活动数据适配器
     */
    public void initActivityAdapter(){
        activityAdapter=new ActivityAdapter(R.layout.item_marketing_activity,marketingActivityBeen);
        activityAdapter.openLoadAnimation();
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(activityAdapter);

        //设置item点击事件，使用选择的营销方案
        mRecyclerView.addOnItemTouchListener(new com.chad.library.adapter.base.listener.OnItemClickListener( ){
            @Override
            public void SimpleOnItemClick(BaseQuickAdapter adapter, View view, int position) {

//                    if(barcodeCodeList.size()!=0){

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
        }
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
                final AlertDialog dialog = new AlertDialog.Builder(CreateOrderActivity.this).setView(view).setCancelable(false).create();
                dialog.show();

                Button btn_confirm = (Button) view.findViewById(R.id.btn_consume_confiem);
                Button btn_cancel = (Button) view.findViewById(R.id.btn_consume_cancel);
                final EditText ed_consumen_amount = (EditText) view.findViewById(R.id.ed_consume_amount);
                ed_consumen_amount.setText(payMoney+"");//设置支付金额
                btn_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        synchronized (lock) {
                            money = ed_consumen_amount.getText().toString();
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
        core.setXiaoFeiAmount(money);//设置消费金额
    }

    /**
     * 自定义支付Dialog
     */
    public class CustomOrderPayDialog extends Dialog implements View.OnClickListener{
        int layoutRes;//布局文件
        Context context;

        /**
         * 自定义收银主题及布局的构造方法
         * @param context
         * @param theme
         * @param resLayout
         */
        public CustomOrderPayDialog(Context context, int theme,int resLayout){
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
            ll_order_weixin_pay= (LinearLayout) findViewById(R.id.ll_order_weixin_pay);
            ll_order_ali_pay= (LinearLayout) findViewById(R.id.ll_order_ali_pay);
            ll_order_union_pay= (LinearLayout) findViewById(R.id.ll_order_union_pay);
            ll_order_cash_pay= (LinearLayout) findViewById(R.id.ll_order_cash_pay);
            tv_order_pay_back= (TextView) findViewById(R.id.tv_order_pay_back);

            ll_order_weixin_pay.setOnClickListener(this);
            ll_order_ali_pay.setOnClickListener(this);
            ll_order_union_pay.setOnClickListener(this);
            ll_order_cash_pay.setOnClickListener(this);
            tv_order_pay_back.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_order_pay_back://返回
                    dialog.dismiss();
                    break;

                case R.id.ll_order_weixin_pay://微信支付
                    if(activityId!=0){////使用活动
                        bundle.putDouble("PayMoney",activityResultPayMent);
                    }else{//未使用活动
                        bundle.putDouble("PayMoney",payMoney);
                    }

                    bundle.putInt("Pay",2);
                    bundle.putInt("OrderStatus",orderStatus);
                    bundle.putString("OrderId",orderId);
                    bundle.putString("shopNameNick",shopNameNick);
                    bundle.putString("userName",userName);

                    Skip.mNextFroData(mActivity,WeiXinAndAliPayActivity.class,bundle);
                    dialog.dismiss();
                    break;

                case R.id.ll_order_ali_pay://支付宝
                    if(activityId!=0){//使用活动
                        bundle.putDouble("PayMoney",activityResultPayMent);
                    }else{//未使用活动
                        bundle.putDouble("PayMoney",payMoney);
                    }
                    bundle.putInt("Pay",3);
                    bundle.putInt("OrderStatus",orderStatus);
                    bundle.putString("OrderId",orderId);
                    bundle.putString("shopNameNick",shopNameNick);
                    bundle.putString("userName",userName);
                    Skip.mNextFroData(mActivity,WeiXinAndAliPayActivity.class,bundle);
                    dialog.dismiss();
                    break;

                case R.id.ll_order_union_pay://银联支付
                    dialog.dismiss();
                    payType=4;
                    //启动银联收银
                    amount = "2";
                    lock[0] = LOCK_WAIT;
                    doConsumeHasTemplate(amount,scanCode);
                    break;

                case R.id.ll_order_cash_pay://现金支付
                    payType=5;
                    if(orderStatus!=30 || orderStatus!=40 || orderStatus!=100) {
                        //随机生成12位参考号【规则：当前时间+2位随机数】
                        referenceNumber = RandomUtils.getCurrentTimeAsNumber() + RandomUtils.getToFourRandom();
                        //随机生成6位凭证号【规则：当月+4位随机数】
                        traceAuditNumber = RandomUtils.getCurrentTimeMM() + RandomUtils.getFourRandom();

                        if(openId!=null){
                            getPayListener(referenceNumber,traceAuditNumber,payMoney,orderId,5,openId);
                        }else{
                            getPayListener(referenceNumber,traceAuditNumber,payMoney,orderId,5,"NotVipId");
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
    }

    /**
     * 收银回调
     */
    public class PosCallBack implements IPosCallBack {
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

                case IPosCallBack.ERR_InTask:{
                    if ((Integer) params[0] == EVENT_NO_PAPER) {
//	                        showRePrintDialog();
                    }
                }

                default: {
                    showMsg("Event:" + eventID);
                    break;
                }
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
}
