package com.cesaas.android.pos.activity.cashier;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.activity.order.DownOrderActivity;
import com.cesaas.android.pos.activity.order.OrderDetailActivity;
import com.cesaas.android.pos.activity.order.WaitPayOrderDetailActivity;
import com.cesaas.android.pos.activity.user.LoginActivity;
import com.cesaas.android.pos.activity.user.SettingActivity;
import com.cesaas.android.pos.base.BaseActivity;
import com.cesaas.android.pos.base.BaseRecyclerView;
import com.cesaas.android.pos.bean.PayCallbackBean;
import com.cesaas.android.pos.bean.PayLogBean;
import com.cesaas.android.pos.bean.PosBean;
import com.cesaas.android.pos.bean.PosOrderIdBean;
import com.cesaas.android.pos.bean.PosPayLogBean;
import com.cesaas.android.pos.bean.ResultCreatePayBean;
import com.cesaas.android.pos.bean.ResultGetOrderBean;
import com.cesaas.android.pos.bean.ResultGetTokenBean;
import com.cesaas.android.pos.bean.ResultHomeCreateFromStoreBean;
import com.cesaas.android.pos.bean.ShopVipBean;
import com.cesaas.android.pos.bean.UserInfoBean;
import com.cesaas.android.pos.bean.printer.LatticePrinterBean;
import com.cesaas.android.pos.db.bean.PosPayBean;
import com.cesaas.android.pos.db.pay.PosSqliteDatabaseUtils;
import com.cesaas.android.pos.db.pay.Task;
import com.cesaas.android.pos.db.pay.TimerManager;
import com.cesaas.android.pos.global.App;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.global.Urls;
import com.cesaas.android.pos.gridview.CashierGridAdapter;
import com.cesaas.android.pos.gridview.MyGridView;
import com.cesaas.android.pos.inventory.activity.InventoryMainActivity;
import com.cesaas.android.pos.listener.OnItemClickListener;
import com.cesaas.android.pos.net.nohttp.HttpListener;
import com.cesaas.android.pos.net.xutils.net.CreatePayNet;
import com.cesaas.android.pos.net.xutils.net.GetTokenNet;
import com.cesaas.android.pos.net.xutils.net.PayFormStoreNet;
import com.cesaas.android.pos.net.xutils.net.PosPayLogNet;
import com.cesaas.android.pos.pos.adapter.PosPayCategoryAdapter;
import com.cesaas.android.pos.rongcloud.activity.ConversationListActivity;
import com.cesaas.android.pos.rongcloud.bean.ReceiveMessageBean;
import com.cesaas.android.pos.rongcloud.listener.InitListener;

import com.cesaas.android.pos.storedvalue.bean.ResultPayCategoryBean;
import com.cesaas.android.pos.storedvalue.ui.SummaryActivity;
import com.cesaas.android.pos.test.utils.SlidingMenu;
import com.cesaas.android.pos.utils.AbAppUtil;
import com.cesaas.android.pos.utils.AbDateUtil;
import com.cesaas.android.pos.utils.BankUtil;
import com.cesaas.android.pos.utils.CheckUtil;
import com.cesaas.android.pos.utils.DecimalFormatUtils;
import com.cesaas.android.pos.utils.JsonUtils;
import com.cesaas.android.pos.utils.OrderCashierPrinterTools;
import com.cesaas.android.pos.utils.RandomUtils;
import com.cesaas.android.pos.utils.SingleCashierPrinterTools;
import com.cesaas.android.pos.utils.Skip;
import com.cesaas.android.pos.utils.SoundPoolUtils;
import com.cesaas.android.pos.utils.ToastUtils;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.weipass.pos.sdk.IPrint;
import cn.weipass.pos.sdk.LatticePrinter;
import cn.weipass.pos.sdk.impl.WeiposImpl;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import scanner.CaptureActivity;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：POS收银首页
 * 创建日期：2016/10/26
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class CashierHomeActivity extends BaseActivity implements View.OnClickListener{

    private static final int CAMERA = 1;//相机权限

    private byte[] lock = new byte[1];
    int EVENT_NO_PAPER = 1;
    private final int LOCK_WAIT = 0;
    private final int LOCK_CONTINUE = 1;

    private int REQUEST_CONTACT = 20;
    final int RESULT_CODE = 101;
    private String scanCode;
    //8583协议中的参考号
    private String refNum;
    private PosCore pCore;
    private PosCallBack callBack;
    private String amount;
    private Integer money;
    private double singleCashierMoney=0.0;
    private int orderStatus;
    private String orderId;
    private int curSelectedPos=1001;//当前选择的收银【1001：独立收银，1002：扫描订单收银】

    private String vipMobile;//会员手机
    private String mobile;
    private int point;//积分
    private String orderShopId;//订单店铺ID
    private String userShopId;//用户店铺ID
    private String vipId;//vip ID
    private String icon;
    private int fansId;
    private String fansNickName;
    private String openId;//会员openId
    private double payMoney;//订单支付价格
    private double totalPrice;//原价

    private int mobilePayType;//移动支付类型
    private String WxAliTraceAuditNumber;//微信&支付宝凭证号
    private double mobilepayMoney;//移动支付金额
    private String mobileOrderNo;//移动支付订单号
    private int IsPractical;

    private String biao;

    private PosBean posBean;
    private String refNo;
    private PosOrderIdBean idBean;

    //独立收银订单号
    private String orderNo;
    private double discount;//会员折扣
    private double discountAfter;//折后金额
    private double originalPrice;//原价
    private String userName;//用户名【营业员】
    private String shopNameNick;//店铺名
    private int payType;
    private boolean isSuccess=false;

    PosCore.RXiaoFei rXiaoFei;
    private String referenceNumber;//参考号
    private String traceAuditNumber;//凭证号
    private String primaryAccountNumber;//卡号
    private String cardName=null;//银行卡名称
    private String bankName=null;////发卡行名称
    private int cardCategory=100;//卡类型

    private MyGridView gridView;//九宫格gridView
    private ImageView iv_add_vip,iv_add_scan_vip,iv_back_del;
    private EditText tv_show_amount,tv_show_discount,et_add_vip_mobile;
    private TextView tv_cashier_pay,tv_pos_more,tv_vip_name,tv_vip_mobile,tv_app_version,
            tv_vip_shop,tv_vip_point,tv_vip_discount,tv_vip_grade,tv_radix_point,tv_zero;
    private TextView tv_pay_success,tv_pay_trace_audit,tv_pay_original_price,tv_real_pay,tv_pay_discount,tv_pay_cad;

    private LinearLayout ll_weixin_pay,ll_ali_pay,ll_union_pay,ll_cash_pay,ll_query_vip,ll_query_info;
    private LinearLayout ll_single_weixin_pay,ll_single_ali_pay,ll_single_union_pay,ll_single_cash_pay,ll_cancel_cashier;
    private LinearLayout ll_pay_info,ll_pay_cashier_accounts,ll_vip_info_btn,ll_scan;
    private LinearLayout ll_check_accounts,ll_down_order,ll_settle_accounts,ll_inventory,llStoredValue,ll_app_setting,ll_convers;
    private LinearLayout rl_exit;

    private String st = "";
    private CustomSingleCashierDialog singleCashierDialog;
    private CustomAddVipDialog addVipDialog;

    private SlidingMenu mMenu;

    private LatticePrinter latticePrinter;// 点阵打印
    private LatticePrinterBean latticePrinterBean;
    private Handler handler;
    private int TIME = 2000;

    private PosPayLogBean posPayLogBean;
    private PosPayLogNet posPayLogNet;
    private CreatePayNet createPayNet;
    private String strJson;


    private ArrayList<ResultGetOrderBean.OrderDetailBean> orderList;//商品订单列表

    private GetTokenNet getTokenNet;

    private ArrayList<TextView> tvs=new ArrayList<TextView>();
    private Runnable mRunnable;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mHandler.removeCallbacks(mRunnable);
            //调用pos打印机
            setLatticePrinter();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashier_home);

        mMenu = (SlidingMenu) findViewById(R.id.id_menu);
        //通过EventBus订阅事件
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        if(prefs.getString("RongToken")!=null){
            connect(prefs.getString("RongToken"));
        }else{
            getTokenNet=new GetTokenNet(mContext);
            getTokenNet.setData();
        }

        if(mCache.getAsString("isOpenSound")==null){
            mCache.put("isOpenSound","true");
        }

        getUserInfo();

        checkAndRequestPermission();
        //初始化CoreApp连接对象
        initPosCore();
        initView();
        initGridView();
        getMobilePay();
//        initTask();
    }

//    private void initTask(){
//        //时间间隔(一天)
//        new TimerManager(24 * 60 * 60 * 1000);
//    }

    /**
     * 获取移动支付信息并且通过pos打印
     */
    public void getMobilePay(){
        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            mobilePayType=bundle.getInt("mobilePayType");
            WxAliTraceAuditNumber=bundle.getString("WxAliTraceAuditNumber");
            mobilepayMoney=bundle.getDouble("mobilepayMoney");
            mobileOrderNo=bundle.getString("mobileOrderNo");
            discount=bundle.getDouble("discount");
            shopNameNick=bundle.getString("shopNameNick");
            userName=bundle.getString("userName");
            point=bundle.getInt("point");
            originalPrice=bundle.getDouble("originalPrice");
            discountAfter=bundle.getDouble("discountAfter");
            mobile=bundle.getString("mobile");
            isSuccess=bundle.getBoolean("isSuccess");
            if(mobilePayType==2 || mobilePayType==3){//微信&支付宝

                if(mobilePayType==2){
                    tv_pay_success.setText("微信消费成功");
                }else{
                    tv_pay_success.setText("支付宝消费成功");
                }

                ll_pay_cashier_accounts.setVisibility(View.GONE);
                ll_pay_info.setVisibility(View.VISIBLE);
                tv_pay_trace_audit.setText("凭证号:"+WxAliTraceAuditNumber);
                tv_pay_original_price.setText("原价:"+DecimalFormatUtils.decimalFormatRound(originalPrice));
                if(discount!=0.0){
                    //获取折后金额
                    tv_pay_discount.setVisibility(View.VISIBLE);
                    tv_pay_discount.setText("折扣"+discount);
                    tv_real_pay.setText("实付:"+DecimalFormatUtils.decimalFormatRound(mobilepayMoney));
                }else{
                    tv_pay_discount.setVisibility(View.GONE);
                    tv_real_pay.setText("实付:"+DecimalFormatUtils.decimalFormatRound(mobilepayMoney));
                }

                posPayLogBean=new PosPayLogBean();
                posPayLogBean.setOrderId(mobileOrderNo);//支付订单
                posPayLogBean.setPayAmount(mobilepayMoney);//支付金额
                posPayLogBean.setTraceAuditNumber(WxAliTraceAuditNumber);//支付凭证号
                posPayLogBean.setPayType(mobilePayType);//支付类型
                posPayLogBean.setRemark("独立收银");
                posPayLogBean.setEnCode(prefs.getString("enCode"));//设备EN号
                strJson=gson.toJson(posPayLogBean);

                getPayListener("",WxAliTraceAuditNumber,mobilepayMoney,mobileOrderNo,mobilePayType,"notFansVip",IsPractical);

                if(isSuccess==true){
                    //调用pos打印机
                    setLatticePrinter();

                    mRunnable = new Runnable() {
                        @Override
                        public void run() {
                            mHandler.sendEmptyMessage(1);
                        }
                    };
                    mHandler .postDelayed(mRunnable, 3000); // 在Handler中执行子线程并延迟3s。
                }
            }
        }
    }

    /**
     * 初始化视图控件
     */
    public void initView(){
        tv_app_version= (TextView) findViewById(R.id.tv_app_version);
        tv_app_version.setText("V"+ AbAppUtil.getAppVersion(this));

        ll_pay_cashier_accounts= (LinearLayout) findViewById(R.id.ll_pay_cashier_accounts);
        ll_pay_info= (LinearLayout) findViewById(R.id.ll_pay_info);
        ll_scan= (LinearLayout) findViewById(R.id.ll_scan);
        tv_pay_success= (TextView) findViewById(R.id.tv_pay_success);
        tv_pay_trace_audit= (TextView) findViewById(R.id.tv_pay_trace_audit);
        tv_pay_original_price= (TextView) findViewById(R.id.tv_pay_original_price);
        tv_real_pay= (TextView) findViewById(R.id.tv_real_pay);
        tv_pay_discount= (TextView) findViewById(R.id.tv_pay_discount);
        tv_pay_cad= (TextView) findViewById(R.id.tv_pay_cad);

        tv_radix_point= (TextView) findViewById(R.id.tv_radix_point);
        tv_zero= (TextView) findViewById(R.id.tv_zero);
        iv_back_del= (ImageView) findViewById(R.id.iv_back_del);

        tv_radix_point.setOnClickListener(this);
        tv_zero.setOnClickListener(this);
        iv_back_del.setOnClickListener(this);
        ll_scan.setOnClickListener(this);

        tv_show_amount= (EditText) findViewById(R.id.tv_show_amount);
        tv_show_discount= (EditText) findViewById(R.id.tv_show_discount);
        tv_cashier_pay= (TextView) findViewById(R.id.tv_cashier_pay);
        tv_pos_more= (TextView) findViewById(R.id.tv_pos_more);
        gridView= (MyGridView) findViewById(R.id.gridview);

        iv_add_vip= (ImageView) findViewById(R.id.iv_add_vip);

        tv_cashier_pay.setOnClickListener(this);
        tv_pos_more.setOnClickListener(this);

        rl_exit= (LinearLayout) findViewById(R.id.rl_exit);

        ll_inventory= (LinearLayout) findViewById(R.id.ll_inventory);
        ll_down_order= (LinearLayout) findViewById(R.id.ll_down_order);
        ll_check_accounts= (LinearLayout) findViewById(R.id.ll_check_accounts);
        ll_settle_accounts= (LinearLayout) findViewById(R.id.ll_settle_accounts);
        llStoredValue= (LinearLayout) findViewById(R.id.ll_stored_value);
        ll_app_setting= (LinearLayout) findViewById(R.id.ll_app_setting);
        ll_convers= (LinearLayout) findViewById(R.id.ll_convers);

        //设置点击监听
        rl_exit.setOnClickListener(this);
        ll_inventory.setOnClickListener(this);
        ll_down_order.setOnClickListener(this);
        ll_check_accounts.setOnClickListener(this);
        iv_add_vip.setOnClickListener(this);
        ll_settle_accounts.setOnClickListener(this);
        llStoredValue.setOnClickListener(this);
        ll_app_setting.setOnClickListener(this);
        ll_convers.setOnClickListener(this);

    }

    /**
     * 检查权限【Android6.0动态申请运行时权限】
     */
    private void checkAndRequestPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                Log.i(Constant.TAG, "hi! everybody, I really need ths permission for better service. thx!");

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, CAMERA);
            }
        }
    }

    /**
     *  处理授权请求回调
     使用onRequestPermissionsResult(int ,String , int[])方法处理回调，
     上面说到了根据requestPermissions()方法中的requestCode，
     就可以在回调方法中区分授权请求
     */
    @SuppressLint("Override")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(Constant.TAG, "PERMISSION_GRANTED: Thx, I will give u a better service!");
                } else {
                    Log.i(Constant.TAG, "PERMISSION_DENIED: Sorry, without this permission I can't do next work for u");
                }
                return;
            }
        }
    }

    public void CONTENT(String number){
        StringBuffer sb = new StringBuffer(st);
//        if (st.equals("0")){
//            st = number;
//        }else {
            sb.append(number);
            st = sb.toString();
//        }
        if (st.indexOf(".")!=-1){
            String newSt = st.substring(st.indexOf("."),st.length());
            if (newSt.length()>3){
                st = st.substring(0,st.length()-1);
            }
        }
    }

    public void initGridView(){
        gridView.setAdapter(new CashierGridAdapter(mContext));
        //九宫格点击事件
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                switch (position) {
                    case 0:
                        CONTENT("1");
                        hidAndShow();
                        break;
                    case 1:
                        CONTENT("2");
                        hidAndShow();
                        break;
                    case 2:
                        CONTENT("3");
                        hidAndShow();
                        break;
                    case 3:
                        CONTENT("4");
                        hidAndShow();
                        break;
                    case 4:
                        CONTENT("5");
                        hidAndShow();
                        break;
                    case 5:
                        CONTENT("6");
                        hidAndShow();
                        break;
                    case 6:
                        CONTENT("7");
                        hidAndShow();
                        break;
                    case 7:
                        CONTENT("8");
                        hidAndShow();
                        break;
                    case 8:
                        CONTENT("9");
                        hidAndShow();
                        break;
                    case 9:
                        if(TextUtils.isEmpty(tv_show_amount.getText().toString())){

                        }else{
                            CONTENT(".");
                        }
                        hidAndShow();
                        tv_show_amount.setText(st);
                        break;
                    case 10:
                        CONTENT("0");
                        hidAndShow();
                        tv_show_amount.setText(st);
                        break;
                    case 11:
                        if(st.equals("")){

                        }else{
                            st = st.substring(0,st.length()-1);
                        }
                        hidAndShow();
                        break;

                    default:
                        break;
                }
                    tv_show_amount.setText(st);
            }
        });
    }

    public void hidAndShow(){
        if(isSuccess==true){
            ll_pay_cashier_accounts.setVisibility(View.VISIBLE);
            ll_pay_info.setVisibility(View.GONE);
        }
    }

    /**
     * 接收支付流水结果信息
     * @param bean
     */
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(ResultCreatePayBean bean) {
        if(bean.isSuccess()!=false){
            if(payType==5){
                isSuccess=true;
                ll_pay_cashier_accounts.setVisibility(View.GONE);
                ll_pay_info.setVisibility(View.VISIBLE);
                tv_pay_success.setText("现金消费成功");
                tv_pay_trace_audit.setText("凭证号:"+traceAuditNumber);
//                if(curSelectedPos==1001){
                tv_pay_original_price.setText("原价:"+DecimalFormatUtils.decimalFormatRound(Double.parseDouble(tv_show_amount.getText().toString())));
                originalPrice=DecimalFormatUtils.decimalFormatRound(Double.parseDouble(tv_show_amount.getText().toString()));
                payMoney=DecimalFormatUtils.decimalFormatRound(Double.parseDouble(tv_show_amount.getText().toString()));
                totalPrice=DecimalFormatUtils.decimalFormatRound(Double.parseDouble(tv_show_amount.getText().toString()));

                posPayLogBean=new PosPayLogBean();
                posPayLogBean.setOrderId(orderId);//支付订单
                posPayLogBean.setPayAmount(totalPrice);//支付金额
                posPayLogBean.setTraceAuditNumber(referenceNumber);//支付凭证号
                posPayLogBean.setPayType(5);//支付类型
                posPayLogBean.setRemark("独立收银");
                posPayLogBean.setEnCode(prefs.getString("enCode"));//设备EN号
                strJson=gson.toJson(posPayLogBean);

                tv_real_pay.setText("实付:" +DecimalFormatUtils.decimalFormatRound(Double.parseDouble(tv_show_amount.getText().toString())));

                //调用pos打印机
                setLatticePrinter();
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        mHandler.sendEmptyMessage(1);
                    }
                };
                mHandler .postDelayed(mRunnable, 3000); // 在Handler中执行子线程并延迟3s。

            }else if(payType==4){
                if(payType==4){
                    isSuccess=true;
                    PosPayBean bean1=new PosPayBean(prefs.getString("userShopId"),shopNameNick,userName,orderId,payMoney+"",rXiaoFei.retrievalReferenceNumber,"4","银联支付",IsPractical+"","3",AbDateUtil.getCurrentDate(),"消费成功...",prefs.getString("enCode"),rXiaoFei.primaryAccountNumber,"true","0","1");
                    insertData(bean1);

                    tv_pay_success.setText("银联消费成功");
                    tv_pay_cad.setVisibility(View.VISIBLE);
                    tv_pay_cad.setText("卡号:" + rXiaoFei.primaryAccountNumber );
                    tv_pay_trace_audit.setText("凭证号:" + rXiaoFei.systemTraceAuditNumber);
                    if(curSelectedPos==1001){
                        tv_pay_original_price.setText("原价:"+DecimalFormatUtils.decimalFormatRound(Double.parseDouble(biao)));

                    }else{
                        tv_pay_original_price.setText("原价:"+DecimalFormatUtils.decimalFormatRound(payMoney));
                    }

                    if(discount!=0.0){
                        discountAfter=Double.parseDouble(biao)*(discount/10);
                        tv_pay_discount.setVisibility(View.VISIBLE);
                        tv_pay_discount.setText("折扣"+discount);
                        tv_real_pay.setText("实付:"+DecimalFormatUtils.decimalFormatRound(discountAfter));

                    }else{
                        tv_pay_discount.setVisibility(View.GONE);
                        if(curSelectedPos==1001) {//独立收银
                            tv_real_pay.setText("实付:" + DecimalFormatUtils.decimalFormatRound(Double.parseDouble(biao)));

                        }else{//扫描订单收银
                            tv_real_pay.setText("实付:" +DecimalFormatUtils.decimalFormatRound(payMoney));
                        }
                    }
                }
            }
        }else{
//            ToastUtils.getToast(mContext,"创建支付流水失败："+bean.getMessage());
//            Log.d(Constant.TAG,"支付error:"+bean.getMessage());

            //记录pos 支付日志
            if(payType==1){//积分支付退款回调

            }else if(payType==2){//微信支付退款回调
                payLog("PayFromStore",strJson,"微信支付");
            }else if(payType==3){//支付宝退款回调
                payLog("PayFromStore",strJson,"支付宝");
            }else if(payType==4){//银联支付退款回调

                payLog("PayFromStore",strJson,"银联支付");
                auditOrder();
            }else if(payType==5){//现金支付退款回调
                payLog("PayFromStore",strJson,"现金支付");
                ;
            }
        }
    }

    private void insertData(PosPayBean bean){
        PosSqliteDatabaseUtils.insterData(mContext,bean);
    }

    public void auditOrder(){
        new CBDialogBuilder(CashierHomeActivity.this)
                .setTouchOutSideCancelable(true)
                .showCancelButton(true)
                .setTitle("温馨提示！")
                .setMessage("该订单审核失败，请重新审核！")
                .setConfirmButtonText("审核")
                .setCancelButtonText("取消")
                .setDialogAnimation(CBDialogBuilder.DIALOG_ANIM_SLID_BOTTOM)
                .setButtonClickListener(true, new CBDialogBuilder.onDialogbtnClickListener() {
                    @Override
                    public void onDialogbtnClick(Context context, Dialog dialog, int whichBtn) {
                        switch (whichBtn) {
                            case BUTTON_CONFIRM:
                                createPayNet=new CreatePayNet(mContext);
                                if(mCache.getAsString("PayInfo")!=null){
                                    createPayNet.setData(mCache.getAsString("PayInfo"),Double.parseDouble(biao),4,mCache.getAsString("PayInfo"),rXiaoFei.retrievalReferenceNumber,primaryAccountNumber,"银联刷卡",rXiaoFei.systemTraceAuditNumber,userShopId,bankName,2,cardCategory);
                                }else{
                                    createPayNet.setData(primaryAccountNumber,Double.parseDouble(biao),4,primaryAccountNumber,rXiaoFei.retrievalReferenceNumber,primaryAccountNumber,"银联刷卡",primaryAccountNumber,userShopId,bankName,2,cardCategory);
                                }
                                break;
                            case BUTTON_CANCEL:
                                ToastUtils.show("已取消审核，请联系管理员！");
                                break;
                            default:
                                break;
                        }
                    }
                })
                .create().show();
    }


    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(ResultHomeCreateFromStoreBean bean) {
        if(bean.isSuccess()==true && bean.TModel!=null){//订单号不为null，显示支付方式dialog
            orderNo=bean.TModel.RetailId+"";
            singleCashierDialog=new CustomSingleCashierDialog(mContext, R.style.dialog, R.layout.item_custom_single_cashier_dialog);
            singleCashierDialog.show();
            singleCashierDialog.setCancelable(false);
        }else{
            ToastUtils.show("下单失败！"+bean.getMessage());
        }
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.tv_radix_point://小数点
                CONTENT(".");
                tv_show_amount.setText(st);
                break;
            case R.id.tv_zero://0
                CONTENT("0");
                tv_show_amount.setText(st);
                break;
            case R.id.iv_back_del://回删
                if(st.equals("")){

                }else{
                    st = st.substring(0,st.length()-1);
                }
                break;

            case R.id.iv_add_vip://添加会员
                if(TextUtils.isEmpty(tv_show_amount.getText().toString()) || tv_show_amount.getText().toString().equals("￥0.0") || tv_show_amount.getText().toString().equals("0") || tv_show_amount.getText().toString().equals("0.0")){
                    ToastUtils.show("请先输入收款金额!");
                }else{
                    addVipDialog=new CustomAddVipDialog(mContext,R.style.dialog,R.layout.item_custom_add_vip_dialog);
                    addVipDialog.show();
                    addVipDialog.setCancelable(false);
                }
                break;
            case R.id.ll_scan://扫描支付订单
                curSelectedPos=1002;
                Skip.mScanOrderActivityResult(mActivity, CaptureActivity.class, REQUEST_CONTACT);
                break;
            case R.id.tv_cashier_pay://独立收银
                curSelectedPos=1001;
                if(!TextUtils.isEmpty(tv_show_amount.getText().toString()) && !tv_show_amount.getText().toString().equals("￥0.0")){
                    singleCashierDialog=new CustomSingleCashierDialog(mContext, R.style.dialog, R.layout.item_custom_single_cashier_dialog);
                    singleCashierDialog.show();
                    singleCashierDialog.setCancelable(false);
                }else{
                    ToastUtils.show("请输入收款金额在收银！");
                }

                break;
            case R.id.tv_pos_more://更多
                mMenu.toggle();
                break;
            case R.id.ll_check_accounts://查单
                mMenu.closeMenu();
//                Skip.mNext(mActivity, CheckAccountsActivity.class);
                Skip.mNext(mActivity, CheckAccountsListActivity.class);
                break;
            case R.id.ll_settle_accounts://结算对账
                mMenu.closeMenu();
                bundle.putString("shopName",shopNameNick);
                bundle.putString("userName",userName);
                Skip.mNextFroData(mActivity,SettleAccountsActivity.class,bundle);
                break;
            case R.id.ll_down_order://下单
                mMenu.closeMenu();
                bundle.putString("userName",userName);
                Skip.mNextFroData(mActivity, DownOrderActivity.class,bundle);

                break;
            case R.id.ll_stored_value://充值
                Skip.mNext(mActivity, SummaryActivity.class);
                break;

            case R.id.ll_inventory://盘点
                mMenu.closeMenu();
                Skip.mNext(mActivity,InventoryMainActivity.class);
                break;
            case R.id.rl_exit://退出
                mMenu.closeMenu();
                exit();
                break;
            case R.id.ll_app_setting:
                Skip.mNext(mActivity,SettingActivity.class);
                break;
            case R.id.ll_convers:
                Skip.mNext(mActivity,ConversationListActivity.class);
                break;
        }
    }

    /**
     * 退出
     */
    public void exit(){
        new CBDialogBuilder(CashierHomeActivity.this)
                .setTouchOutSideCancelable(true)
                .showCancelButton(true)
                .setTitle("退出登录")
                .setMessage("是否退出登录，退出后将不能做任何操作！")
                .setConfirmButtonText("确定")
                .setCancelButtonText("取消")
                .setDialogAnimation(CBDialogBuilder.DIALOG_ANIM_SLID_BOTTOM)
                .setButtonClickListener(true, new CBDialogBuilder.onDialogbtnClickListener() {
                    @Override
                    public void onDialogbtnClick(Context context, Dialog dialog, int whichBtn) {
                        switch (whichBtn) {
                            case BUTTON_CONFIRM:
                                prefs.cleanAll();
                                mCache.remove("GetPayCategory");
                                Skip.mNext(mActivity, LoginActivity.class, true);
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
     * 读取用户信息
     */
    public void getUserInfo(){
        Request<String> request = NoHttp.createStringRequest(Urls.USER_INFO, RequestMethod.POST);
        commonNet.requestNetTask(request,userInfoListener);

        if(mCache.getAsString("GetPayCategory")!=null){

        }else{
            Request<String> requestPay = NoHttp.createStringRequest(Urls.GET_PAY_CATEGORY, RequestMethod.POST);
            commonNet.requestNetTask(requestPay, getPayList);
        }
    }

    //待支付订单详情回调监听
    public HttpListener<String> getPayList = new HttpListener<String>()
    {
        @Override
        public void onSucceed(int what, Response<String> response)
        {
            Log.i("test","value:"+response.get());
            mCache.put("GetPayCategory",response.get());
        }
        @Override
        public void onFailed(int what, Response<String> response)
        {
            Log.d(Constant.TAG,"Failed:"+response.getException());
        }
    };

    //UserInfo回调监听
    private HttpListener<String> userInfoListener = new HttpListener<String>()
    {
        @Override
        public void onSucceed(int what, Response<String> response)
        {
            UserInfoBean bean= gson.fromJson(response.get(),UserInfoBean.class);
            Log.i(Constant.TAG,"userinfo"+response.get().toString());
            if(bean.TModel!=null){
                shopNameNick=bean.TModel.getShopName();
                userName=bean.TModel.getName();
                userShopId=bean.TModel.getShopId();
                icon=bean.TModel.getIcon();
                vipId=bean.TModel.getVipId();
                prefs.putString("userName",userName);
                prefs.putString("userShopId",userShopId);
                prefs.putString("shopNameNick",shopNameNick);
                prefs.putString("ShopAddress",bean.TModel.getShopAddress());
                prefs.putString("ShopArea",bean.TModel.getShopArea());

            }else{
                ToastUtils.show(""+bean.getMessage());
            }
        }
        @Override
        public void onFailed(int what, Response<String> response)
        {
            ToastUtils.show(response.getException().getMessage());
        }
    };

    /**
     * 会员监听
     */
    public void getVipListener(String mobile,int type){
        Request<String> request = NoHttp.createStringRequest(Urls.QUERY_VIP, RequestMethod.POST);
        request.add("Type",type);//Type:0 手机号, 1:VIpId
        request.add("Val",mobile);
        commonNet.requestNetTask(request,getStatisticsListener,1);
    }

    /**
     * 会员监听回调
     */
    private HttpListener<String> getStatisticsListener = new HttpListener<String>()
    {
        @Override
        public void onSucceed(int what, Response<String> response)
        {
//            Log.i("info","会员result:"+response.get());
            ShopVipBean bean=gson.fromJson(response.get(),ShopVipBean.class);
            if(bean.TModel!=null){
                ll_query_vip.setVisibility(View.GONE);
                ll_query_info.setVisibility(View.VISIBLE);
                ll_vip_info_btn.setVisibility(View.VISIBLE);

                discount=bean.TModel.getFANS_DISCOUNT();
                mobile=bean.TModel.getFANS_MOBILE();
                point=bean.TModel.getFANS_POINT();
                openId=bean.TModel.getFANS_OPENID();
                fansId=bean.TModel.getFANS_ID();
                fansNickName=bean.TModel.getFANS_NAME();

                tv_vip_name.setText(bean.TModel.getFANS_NAME());
                tv_vip_mobile.setText(bean.TModel.getFANS_MOBILE());
                tv_vip_shop.setText(bean.TModel.getFANS_SHOPNAME());
                tv_vip_point.setText(bean.TModel.getFANS_POINT()+"");
                tv_vip_discount.setText(bean.TModel.getFANS_DISCOUNT()+"");
                tv_vip_grade.setText(bean.TModel.getFANS_GRADE());

            }else{
                ToastUtils.show("未找到改会员,请核实输入信息！");
            }
        }
        @Override
        public void onFailed(int what, Response<String> response)
        {
            ToastUtils.show(response.get());
        }
    };


    /**
     * 自定义添加会员Dialog
     */
    public class CustomAddVipDialog extends Dialog implements View.OnClickListener{

        int layoutRes;//布局文件
        Context context;
        private LinearLayout ll_sure_query_vip,ll_cancel_back_cashier,ll_sure_add_vip,ll_cancel_back_query;

        /**
         * 自定义添加会员D主题及布局的构造方法
         * @param context
         * @param theme
         * @param resLayout
         */
        public CustomAddVipDialog(Context context, int theme,int resLayout){
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

            ll_query_vip= (LinearLayout) findViewById(R.id.ll_query_vip);
            ll_query_info= (LinearLayout) findViewById(R.id.ll_query_info);
            ll_sure_query_vip= (LinearLayout) findViewById(R.id.ll_sure_query_vip);
            ll_cancel_back_cashier= (LinearLayout) findViewById(R.id.ll_cancel_back_cashier);
            ll_sure_add_vip= (LinearLayout) findViewById(R.id.ll_sure_add_vip);
            ll_cancel_back_query= (LinearLayout) findViewById(R.id.ll_cancel_back_query);
            ll_vip_info_btn= (LinearLayout) findViewById(R.id.ll_vip_info_btn);

            iv_add_scan_vip= (ImageView) findViewById(R.id.iv_add_scan_vip);
            et_add_vip_mobile= (EditText) findViewById(R.id.et_add_vip_mobile);

            tv_vip_name= (TextView) findViewById(R.id.tv_vip_name);
            tv_vip_mobile= (TextView) findViewById(R.id.tv_vip_mobile);
            tv_vip_shop= (TextView) findViewById(R.id.tv_vip_shop);
            tv_vip_point= (TextView) findViewById(R.id.tv_vip_point);
            tv_vip_discount= (TextView) findViewById(R.id.tv_vip_discount);
            tv_vip_grade= (TextView) findViewById(R.id.tv_vip_grade);

            ll_sure_query_vip.setOnClickListener(this);
            ll_cancel_back_cashier.setOnClickListener(this);
            ll_sure_add_vip.setOnClickListener(this);
            ll_cancel_back_query.setOnClickListener(this);
            iv_add_scan_vip.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {

            switch (v.getId()){
                case R.id.iv_add_scan_vip://扫描会员
                    Skip.mScanAddVipActivityResult(mActivity, CaptureActivity.class, REQUEST_CONTACT);
                    break;

                case R.id.ll_sure_query_vip://查询会员
                    if(CheckUtil.phoneVerify(mContext,et_add_vip_mobile.getText().toString())){
                        vipMobile=et_add_vip_mobile.getText().toString();
                        getVipListener(vipMobile,0);
                    }
                    break;
                case R.id.ll_cancel_back_cashier://返回
                    ToastUtils.show("已取消查询会员");
                    addVipDialog.dismiss();
                    break;
                case R.id.ll_sure_add_vip://添加vip
                    iv_add_vip.setImageResource(R.mipmap.vip02);
                    addVipDialog.dismiss();
                    tv_show_discount.setVisibility(View.VISIBLE);

                    //获取折后金额
                    discountAfter=Double.parseDouble(tv_show_amount.getText().toString())*(discount/10);
                    tv_show_discount.setText(DecimalFormatUtils.decimalFormatRound(discountAfter)+"");

                    //设置原价和折扣价不可编辑状态；
                    tv_show_amount.setFocusable(false);tv_show_amount.setFocusableInTouchMode(false);
                    tv_pay_discount.setFocusable(false);tv_pay_discount.setFocusableInTouchMode(false);
                    break;
                case R.id.ll_cancel_back_query://返回
                    ToastUtils.show("已取消添加会员");
                    addVipDialog.dismiss();
                    break;
            }
        }
    }

    /**
     * 自定义独立收银dialog
     */
    public class CustomSingleCashierDialog extends Dialog{
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
        public CustomSingleCashierDialog(Context context, int theme, int resLayout) {
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

        private OnItemClickListener onItemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                switch (categoryBeaList.get(position).getCategoryType()) {
                    case 2://微信支付
                        payType = categoryBeaList.get(position).getCategoryType();
                        if (!TextUtils.isEmpty(tv_show_discount.getText().toString())) {//折后价格非等于null
                            bundle.putDouble("PayMoney", Double.parseDouble(tv_show_discount.getText().toString()));

                        } else {//折后金额为空
                            bundle.putDouble("PayMoney", Double.parseDouble(tv_show_amount.getText().toString()));
                        }
                        //随机生成6位凭证号【规则：当月+4位随机数】
                        traceAuditNumber = RandomUtils.getCurrentTimeMM() + RandomUtils.getFourRandom();
                        bundle.putInt("Pay", categoryBeaList.get(position).getCategoryType());
                        bundle.putInt("IsPractical", categoryBeaList.get(position).getIsPractical());
                        bundle.putString("userShopId", userShopId);
                        bundle.putString("OrderNo", traceAuditNumber);
                        bundle.putDouble("discount", discount);
                        bundle.putString("shopNameNick", shopNameNick);
                        bundle.putString("userName", userName);
                        bundle.putInt("point", point);
                        bundle.putDouble("originalPrice", Double.parseDouble(tv_show_amount.getText().toString()));
                        bundle.putDouble("discountAfter", discountAfter);
                        bundle.putString("mobile", mobile);
                        bundle.putBoolean("isSuccess", isSuccess);
                        Skip.mNextFroData(mActivity, WeiXinAndAliPaySingleActivity.class, bundle);
                        singleCashierDialog.dismiss();

                        break;
                    case 3://支付宝
                        payType = categoryBeaList.get(position).getCategoryType();
                        if (!TextUtils.isEmpty(tv_show_discount.getText().toString())) {//折后价格非等于null
                            bundle.putDouble("PayMoney", Double.parseDouble(tv_show_discount.getText().toString()));

                        } else {//折后金额为空
                            bundle.putDouble("PayMoney", Double.parseDouble(tv_show_amount.getText().toString()));
                        }
                        //随机生成6位凭证号【规则：当月+4位随机数】
                        traceAuditNumber = RandomUtils.getCurrentTimeMM() + RandomUtils.getFourRandom();
                        bundle.putInt("IsPractical", categoryBeaList.get(position).getIsPractical());
                        bundle.putInt("Pay", categoryBeaList.get(position).getCategoryType());
                        bundle.putString("OrderNo", traceAuditNumber);
                        bundle.putDouble("discount", discount);
                        bundle.putString("shopNameNick", shopNameNick);
                        bundle.putString("userShopId", userShopId);
                        bundle.putString("userName", userName);
                        bundle.putInt("point", point);
                        bundle.putDouble("originalPrice", Double.parseDouble(tv_show_amount.getText().toString()));
                        bundle.putDouble("discountAfter", discountAfter);
                        bundle.putString("mobile", mobile);
                        bundle.putBoolean("isSuccess", isSuccess);
                        Skip.mNextFroData(mActivity, WeiXinAndAliPaySingleActivity.class, bundle);
                        singleCashierDialog.dismiss();
                        break;
                    case 4://银联支付
                        IsPractical=categoryBeaList.get(position).getIsPractical();
                        singleCashierDialog.dismiss();
                        payType = categoryBeaList.get(position).getCategoryType();
                        if (!TextUtils.isEmpty(tv_show_discount.getText().toString())) {//折后价格非等于null
                            biao = tv_show_discount.getText().toString();

                        } else {//折后金额为空
                            biao = tv_show_amount.getText().toString();
                        }
                        //启动银联收银
                        amount = "2";
                        lock[0] = LOCK_WAIT;
                        doConsumeHasTemplate(amount, orderNo);
                        break;
                    case 5://现金支付
                        new CBDialogBuilder(CashierHomeActivity.this)
                                .setTouchOutSideCancelable(true)
                                .showCancelButton(true)
                                .setTitle("温馨提示！")
                                .setMessage("请确认该订单是否使用现金支付？")
                                .setConfirmButtonText("确定")
                                .setCancelButtonText("取消")
                                .setDialogAnimation(CBDialogBuilder.DIALOG_ANIM_SLID_BOTTOM)
                                .setButtonClickListener(true, new CBDialogBuilder.onDialogbtnClickListener() {
                                    @Override
                                    public void onDialogbtnClick(Context context, Dialog dialog, int whichBtn) {
                                        switch (whichBtn) {
                                            case BUTTON_CONFIRM:
                                                dismiss();
                                                IsPractical=categoryBeaList.get(position).getIsPractical();
                                                singleCashierDialog.dismiss();
                                                payType = categoryBeaList.get(position).getCategoryType();
                                                if (!TextUtils.isEmpty(tv_show_discount.getText().toString())) {//折后价格非等于null
                                                    singleCashierMoney = Double.parseDouble(tv_show_discount.getText().toString());

                                                } else {//折后金额为空
                                                    singleCashierMoney = Double.parseDouble(tv_show_amount.getText().toString());
                                                }
                                                    //随机生成12位参考号【规则：当前时间+2位随机数】
                                                    referenceNumber = RandomUtils.getCurrentTimeAsNumber() + RandomUtils.getToFourRandom();
                                                    //随机生成6位凭证号【规则：当月+4位随机数】
                                                    traceAuditNumber = RandomUtils.getCurrentTimeMM() + RandomUtils.getFourRandom();
                                                    orderNo=RandomUtils.getCurrentTimeMM() + RandomUtils.getFourRandom()+"";
                                                    //getPayListener(referenceNumber, traceAuditNumber, singleCashierMoney, orderNo, 5, "notFansVip",IsPractical);

                                                    double money=Double.parseDouble(tv_show_amount.getText().toString());
                                                    //订单号生成规则：LS+VipId+MMddHHmm+4位随机数
                                                    String orderNo="LS"+vipId+ AbDateUtil.getCurrentTimeAsNumber()+RandomUtils.getFourRandom();
                                                    createPayNet=new CreatePayNet(mContext);
                                                    createPayNet.setData(orderNo,money,5,referenceNumber,"","","独立收银",orderNo,userShopId,"",2,100);

                                                break;
                                            case BUTTON_CANCEL:
                                                ToastUtils.show("已取消支付");
                                                dismiss();
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                })
                                .create().show();
                        break;
                    default:
                        break;
                }
            }
        };
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

            init_params.put(PosConfig.Name_EX + "1053", shopNameNick);// 签购单小票台头

            init_params.put(PosConfig.Name_EX + "1100", "cn.weipass.cashier");// 核心APP 包名
            init_params.put(PosConfig.Name_EX + "1101", "com.wangpos.cashiercoreapp.services.CoreAppService");// 核心APP 类名
            init_params.put(PosConfig.Name_EX + "1092", "1");//  是否开启订单生成，并且上报服务器 1.开启 0.不开启
            init_params.put(PosConfig.Name_EX + "1093", "2");// 是否需要打印三联签购单 1.需要 2.不需要
            init_params.put(PosConfig.Name_EX + "1012", "1");// 华势通道

            init_params.put(PosConfig.Name_MerchantName, "coreApp");

            pCore = PosCoreFactory.newInstance(this, init_params);
            callBack = new PosCallBack(pCore);
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

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            posPayLogBean=new PosPayLogBean();
                            posPayLogBean=new PosPayLogBean();
                            posPayLogBean.setOrderId(orderId);//支付订单
                            posPayLogBean.setTraceAuditNumber(rXiaoFei.retrievalReferenceNumber);//支付凭证号
                            posPayLogBean.setPayType(4);//支付类型
                            posPayLogBean.setRemark("零售单");
                            posPayLogBean.setEnCode(prefs.getString("enCode"));//设备EN号
                            posPayLogBean.setAccountNumber(primaryAccountNumber);
                            posPayLogBean.setPayAmount(Double.parseDouble(biao));//支付金额
                            strJson=gson.toJson(posPayLogBean);

                            ll_pay_cashier_accounts.setVisibility(View.GONE);
                            ll_pay_info.setVisibility(View.VISIBLE);

                            createPayNet=new CreatePayNet(mContext);
                            if(rXiaoFei.systemTraceAuditNumber!=null && !"".equals(rXiaoFei.systemTraceAuditNumber)){
                                createPayNet.setData(rXiaoFei.retrievalReferenceNumber,Double.parseDouble(biao),4,rXiaoFei.retrievalReferenceNumber,rXiaoFei.retrievalReferenceNumber,primaryAccountNumber,"银联刷卡",rXiaoFei.systemTraceAuditNumber,userShopId,bankName,2,cardCategory);
                            }else{
                                if(mCache.getAsString("PayInfo")!=null){
                                    createPayNet.setData(mCache.getAsString("PayInfo"),Double.parseDouble(biao),4,mCache.getAsString("PayInfo"),rXiaoFei.retrievalReferenceNumber,primaryAccountNumber,"银联刷卡",rXiaoFei.systemTraceAuditNumber,userShopId,bankName,2,cardCategory);
                                }else{
                                    createPayNet.setData(primaryAccountNumber,Double.parseDouble(biao),4,primaryAccountNumber,rXiaoFei.retrievalReferenceNumber,primaryAccountNumber,"银联刷卡",primaryAccountNumber,userShopId,bankName,2,cardCategory);
                                }
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    showMsg(e.getLocalizedMessage());
                    Log.d(Constant.TAG,"银联ERROR：="+e.getLocalizedMessage());
                }
            }
        }.start();
    }

    /**
     * 处理扫描Activity返回的数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode==RESULT_CODE) {
            if(data.getStringExtra("mScanOrderResult")!=null && data.getStringExtra("mScanOrderResult").equals("012")){
                scanCode= data.getStringExtra("resultCode");
                bundle.putString("OrderId",scanCode);
                Skip.mNextFroData(mActivity, WaitPayOrderDetailActivity.class,bundle);
            }

            if(data.getStringExtra("mScanAddVipResult")!=null && data.getStringExtra("mScanAddVipResult").equals("016")){
                scanCode= data.getStringExtra("resultCode");
                if(scanCode!=null){
                    et_add_vip_mobile.setText(scanCode);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 支付回调
     * @param RetrievalReferenceNumber
     * @param TraceAuditNumber
     * @param ConsumeAmount
     * @param OrderId
     * @param PayType
     * @param OpenId
     */
    public void getPayListener(String RetrievalReferenceNumber,String TraceAuditNumber,double ConsumeAmount,String OrderId,int PayType,String OpenId,int IsPractical){
        Request<String> request = NoHttp.createStringRequest(Urls.PAY_FROM_STORE, RequestMethod.POST);
        request.add("RetrievalReferenceNumber",RetrievalReferenceNumber);//参考号
        request.add("TraceAuditNumber",TraceAuditNumber);//凭证号
        request.add("ConsumeAmount",ConsumeAmount);//消费金额
        request.add("RetailId",OrderId);//支付订单号
        request.add("IsPractical",IsPractical);//支付订单号
        request.add("PayType",PayType);
        request.add("EnCode",prefs.getString("enCode"));//设备EN号
//        request.add("OpenId",OpenId);//
        commonNet.requestNetTask(request,getPayListener,1);
    }

    //银联支付成功回调监听
    private HttpListener<String> getPayListener = new HttpListener<String>()
    {
        @Override
        public void onSucceed(int what, Response<String> response)
        {
            PayCallbackBean callbackBean=gson.fromJson(response.get(),PayCallbackBean.class);
            if(callbackBean.isSuccess()==true){
                if(payType==5){
                    isSuccess=true;
                    ll_pay_cashier_accounts.setVisibility(View.GONE);
                    tv_pay_cad.setVisibility(View.GONE);
                    ll_pay_info.setVisibility(View.VISIBLE);
                    tv_pay_success.setText("现金消费成功");
                    tv_pay_trace_audit.setText("凭证号:"+traceAuditNumber);
                    if(curSelectedPos==1001){
                        tv_pay_original_price.setText("原价:"+DecimalFormatUtils.decimalFormatRound(originalPrice));

                    }else{
                        tv_pay_original_price.setText("原价:"+DecimalFormatUtils.decimalFormatRound(payMoney));
                    }

                    if(discount!=0.0){
                        //获取折后金额
                        discountAfter=originalPrice*(discount/10);
                        tv_pay_discount.setVisibility(View.VISIBLE);
                        tv_pay_discount.setText("折扣"+discount);
                        tv_real_pay.setText("实付:"+DecimalFormatUtils.decimalFormatRound(discountAfter));

                    }else{
                        tv_pay_discount.setVisibility(View.GONE);
                        if(curSelectedPos==1001) {
                            tv_real_pay.setText("实付:" + DecimalFormatUtils.decimalFormatRound(singleCashierMoney));
                        }else{
                            tv_real_pay.setText("实付:"+DecimalFormatUtils.decimalFormatRound(payMoney));
                        }
                    }
                        //调用pos打印机
                        setLatticePrinter();

                      mRunnable = new Runnable() {
                        @Override
                        public void run() {
                            mHandler.sendEmptyMessage(1);
                        }
                    };
                    mHandler .postDelayed(mRunnable, 3000); // 在Handler中执行子线程并延迟3s。

                }else if(payType==4){
                    isSuccess=true;
                    tv_pay_success.setText("银联消费成功");
                    tv_pay_cad.setVisibility(View.VISIBLE);
                    tv_pay_cad.setText("卡号:" + rXiaoFei.primaryAccountNumber );
                    tv_pay_trace_audit.setText("凭证号:" + rXiaoFei.systemTraceAuditNumber);
                    if(curSelectedPos==1001){
                        tv_pay_original_price.setText("原价:"+DecimalFormatUtils.decimalFormatRound(Double.parseDouble(biao)));

                    }else{
                        tv_pay_original_price.setText("原价:"+DecimalFormatUtils.decimalFormatRound(payMoney));
                    }

                    if(discount!=0.0){
                        discountAfter=Double.parseDouble(biao)*(discount/10);
                        tv_pay_discount.setVisibility(View.VISIBLE);
                        tv_pay_discount.setText("折扣"+discount);
                        tv_real_pay.setText("实付:"+DecimalFormatUtils.decimalFormatRound(discountAfter));

                    }else{
                        tv_pay_discount.setVisibility(View.GONE);
                        if(curSelectedPos==1001) {//独立收银
                            tv_real_pay.setText("实付:" + DecimalFormatUtils.decimalFormatRound(Double.parseDouble(biao)));

                        }else{//扫描订单收银
                            tv_real_pay.setText("实付:" +DecimalFormatUtils.decimalFormatRound(payMoney));
                        }
                    }

                    ll_pay_cashier_accounts.setVisibility(View.GONE);
                    ll_pay_info.setVisibility(View.VISIBLE);
                }
                }else{

            }
        }
        @Override
        public void onFailed(int what, Response<String> response)
        {
            ToastUtils.show(response.get());
        }
    };

    public void payLog(String LogType, String obj,String remark){
        //Pay Log
        posPayLogNet=new PosPayLogNet(mContext);
        posPayLogNet.setData(LogType,obj,remark);
    }

    /**
     * 设置独立收银点阵打印方法
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
            Toast.makeText(CashierHomeActivity.this, "尚未初始化点阵打印sdk，请稍后再试", Toast.LENGTH_SHORT).show();
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

            //以下是设置pos打印信息
            latticePrinterBean=new LatticePrinterBean();
            if(payType==5){//现金
                if (curSelectedPos==1001){
                    latticePrinterBean.setOrderId(orderNo);
                    latticePrinterBean.setOriginalPrice(DecimalFormatUtils.decimalFormatRound(originalPrice));
                }else{
                    latticePrinterBean.setOrderId(scanCode);
                    latticePrinterBean.setOriginalPrice(DecimalFormatUtils.decimalFormatRound(totalPrice));
                }

                latticePrinterBean.setShopName(shopNameNick);
                latticePrinterBean.setCounterName(shopNameNick);
                latticePrinterBean.setShopClerkName(userName);
                latticePrinterBean.setTotalPoint(point);
                latticePrinterBean.setTraceAuditNumber(traceAuditNumber);
                latticePrinterBean.setPayTitleName("现金支付");
                if(discount!=0.0){//折扣价格
                    latticePrinterBean .setVipMobile(mobile);
                    if (curSelectedPos==1001){
                        latticePrinterBean.setDiscountPrice(DecimalFormatUtils.decimalFormatRound(payMoney));
                        latticePrinterBean.setTotalPrice(DecimalFormatUtils.decimalFormatRound(payMoney));
                    }else{
                        latticePrinterBean.setDiscountPrice(DecimalFormatUtils.decimalFormatRound(payType));
                        latticePrinterBean.setTotalPrice(DecimalFormatUtils.decimalFormatRound(totalPrice));
                    }

                }else{//原价
                    latticePrinterBean .setVipMobile("非会员");
                    if (curSelectedPos==1001){
                        latticePrinterBean.setDiscountPrice(DecimalFormatUtils.decimalFormatRound(payMoney));
                        latticePrinterBean.setTotalPrice(DecimalFormatUtils.decimalFormatRound(payMoney));

                    }else{
                        latticePrinterBean.setDiscountPrice(DecimalFormatUtils.decimalFormatRound(payMoney));
                        latticePrinterBean.setTotalPrice(DecimalFormatUtils.decimalFormatRound(payMoney));
                    }
                }
            }else if(mobilePayType==2){//微信
                if (curSelectedPos==1001){
                    latticePrinterBean.setOrderId(mobileOrderNo);
                    latticePrinterBean.setOriginalPrice(DecimalFormatUtils.decimalFormatRound(originalPrice));
                }else{
                    latticePrinterBean.setOrderId(scanCode);
                    latticePrinterBean.setOriginalPrice(DecimalFormatUtils.decimalFormatRound(totalPrice));
                }
                latticePrinterBean.setShopName(shopNameNick);
                latticePrinterBean.setCounterName(shopNameNick);
                latticePrinterBean.setShopClerkName(userName);
                latticePrinterBean.setTotalPoint(point);
                latticePrinterBean.setTraceAuditNumber(WxAliTraceAuditNumber);
                latticePrinterBean.setPayTitleName("微信支付");
                if(discount!=0.0){//折扣价格
                    latticePrinterBean .setVipMobile(mobile);
                    latticePrinterBean.setDiscountPrice(DecimalFormatUtils.decimalFormatRound(discountAfter));
                    latticePrinterBean.setTotalPrice(DecimalFormatUtils.decimalFormatRound(discountAfter));

                }else{//原价
                    latticePrinterBean .setVipMobile("暂无会员");
                    if (curSelectedPos==1001){
                        latticePrinterBean.setDiscountPrice(DecimalFormatUtils.decimalFormatRound(originalPrice));
                        latticePrinterBean.setTotalPrice(DecimalFormatUtils.decimalFormatRound(originalPrice));

                    }else{
                        latticePrinterBean.setDiscountPrice(DecimalFormatUtils.decimalFormatRound(totalPrice));
                        latticePrinterBean.setTotalPrice(DecimalFormatUtils.decimalFormatRound(totalPrice));
                    }
                }
            }else{//支付宝
                if (curSelectedPos==1001){
                    latticePrinterBean.setOrderId(mobileOrderNo);
                    latticePrinterBean.setOriginalPrice(DecimalFormatUtils.decimalFormatRound(originalPrice));
                }else{
                    latticePrinterBean.setOrderId(scanCode);
                    latticePrinterBean.setOriginalPrice(DecimalFormatUtils.decimalFormatRound(totalPrice));
                }
                latticePrinterBean.setShopName(shopNameNick);
                latticePrinterBean.setCounterName(shopNameNick);
                latticePrinterBean.setShopClerkName(userName);
                latticePrinterBean.setTotalPoint(point);
                latticePrinterBean.setTraceAuditNumber(WxAliTraceAuditNumber);
                latticePrinterBean.setPayTitleName("支付宝");
                if(discount!=0.0){//折扣价格
                    latticePrinterBean .setVipMobile(mobile);
                    latticePrinterBean.setDiscountPrice(DecimalFormatUtils.decimalFormatRound(discountAfter));
                    latticePrinterBean.setTotalPrice(DecimalFormatUtils.decimalFormatRound(discountAfter));

                }else{//原价
                    latticePrinterBean .setVipMobile("暂无会员");
                    if (curSelectedPos==1001){
                        latticePrinterBean.setDiscountPrice(DecimalFormatUtils.decimalFormatRound(originalPrice));
                        latticePrinterBean.setTotalPrice(DecimalFormatUtils.decimalFormatRound(originalPrice));

                    }else{
                        latticePrinterBean.setDiscountPrice(DecimalFormatUtils.decimalFormatRound(totalPrice));
                        latticePrinterBean.setTotalPrice(DecimalFormatUtils.decimalFormatRound(totalPrice));
                    }

                }
            }

            if(curSelectedPos==1001){//独立收银打印
                SingleCashierPrinterTools.printLattice(CashierHomeActivity.this, latticePrinter,latticePrinterBean,discount);

            }else{//扫描订单收银打印
                OrderCashierPrinterTools.printLattice(CashierHomeActivity.this, latticePrinter,latticePrinterBean,orderList,discount);
            }

            //清空输入金额天和折扣金额
            st="";
            tv_show_discount.setText("");
        }
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

    public void showMsg(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_show_amount.setText("");
                tv_show_amount.setTextSize(20);
                tv_show_amount.setText(msg);
            }
        });
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
                final AlertDialog dialog = new AlertDialog.Builder(CashierHomeActivity.this).setView(view).setCancelable(false).create();
                dialog.show();

                Button btn_confirm = (Button) view.findViewById(R.id.btn_consume_confiem);
                Button btn_cancel = (Button) view.findViewById(R.id.btn_consume_cancel);
                final EditText ed_consumen_amount = (EditText) view.findViewById(R.id.ed_consume_amount);
                ed_consumen_amount.setFocusable(false);ed_consumen_amount.setFocusableInTouchMode(false);//设置不可编辑状态；
                if(!TextUtils.isEmpty(ed_consumen_amount.getText().toString())){
                    ed_consumen_amount.setText("");//设置支付金额
                }
                if(curSelectedPos==1001){
                    ed_consumen_amount.setText(biao);//设置支付金额

                }else{
                    ed_consumen_amount.setText(payMoney+"");//设置支付金额
                }

                btn_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {//确定支付

                        synchronized (lock) {
                            money=(int)(payMoney*100);
                            lock[0] = LOCK_CONTINUE;
                            lock.notify();
                        }
                        dialog.dismiss();
                    }
                });

                btn_cancel.setOnClickListener(new View.OnClickListener() {//取消支付
                    @Override
                    public void onClick(View v) {
                        synchronized (lock) {
                            ed_consumen_amount.setText("");//设置支付金额
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

        //判断是否独立收银金额和扫描订单金额
        if(curSelectedPos==1001){
            double money=Double.parseDouble(biao);
            Integer dd=(int)(money*100);
            core.setXiaoFeiAmount(dd+"");

        }else{
            core.setXiaoFeiAmount(money+"");//设置消费金额
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
                    Log.i("test", "读取银行卡信息" + params[0]);
                    showMsg("读取银行卡信息");
                    break;
                }
                case EVENT_CardID_end: {
                    String cardNum = (String) params[0];
                    if (!TextUtils.isEmpty(cardNum)) {
                        Log.w(Constant.TAG, "卡号为:" + params[0]);
                        try{
                            primaryAccountNumber=params[0]+"";
                            //获取发卡行，卡种名称
                            cardName= BankUtil.getNameOfBank(primaryAccountNumber);
                            bankName=cardName.substring(0, cardName.indexOf("·"));
                            if(cardName.contains("贷记卡")){
                                cardCategory=3;

                            }else if(cardName.contains("信用卡")){
                                cardCategory=2;

                            }else{//借记卡
                                cardCategory=1;
                            }
                            showConsumeDialog(core);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        ToastUtils.getLongToast(mContext,"获取银行卡号为空！");
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
                    setCachePayInfo(params[0]+"");
                    break;
                }
                case EVENT_AutoPrint_end://打印完成

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


    /**
     * 缓存支付信息
     * @param refNum
     */
    private void setCachePayInfo(String refNum){
        if(mCache.getAsString("PayInfo")!=null && !"".equals(mCache.getAsString("PayInfo"))){
            mCache.remove("PayInfo");
            mCache.put("PayInfo",refNum);
        }else{
            mCache.put("PayInfo",refNum);
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
                AlertDialog.Builder dialog = new AlertDialog.Builder(CashierHomeActivity.this);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(ResultGetTokenBean msg) {
        if(msg.IsSuccess==true){
            prefs.putString("RongToken", msg.TModel.token+"");
            connect(msg.TModel.token);
        }else{
            ToastUtils.getLongToast(mContext,"获取融云ToKen失败！");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(ReceiveMessageBean msg) {
        if(msg.getContent()!=null){
            Log.i("test","新的消息"+msg.getContent());
            if(mCache.getAsString("isOpenSound")!=null && mCache.getAsString("isOpenSound").equals("true")){
                //初始化声音播放
                SoundPoolUtils.initMediaPlayer(mContext);
//                Skip.mNext(mActivity,ConversationListActivity.class);
                Skip.mNext(mActivity, CheckAccountsListActivity.class);
            }
        }
    }


    /**
     * <p>连接服务器，在整个应用程序全局，只需要调用一次，需在 {@link #//init(Context)} 之后调用。</p>
     * <p>如果调用此接口遇到连接失败，SDK 会自动启动重连机制进行最多10次重连，分别是1, 2, 4, 8, 16, 32, 64, 128, 256, 512秒后。
     * 在这之后如果仍没有连接成功，还会在当检测到设备网络状态变化时再次进行重连。</p>
     *
     * @param token    从服务端获取的用户身份令牌（Token）。
     * @param //callback 连接回调。
     * @return RongIM  客户端核心类的实例。
     */
    private void connect(String token) {

        if (getApplicationInfo().packageName.equals(App.getCurProcessName(getApplicationContext()))) {

            RongIM.connect(token, new RongIMClient.ConnectCallback() {

                /**
                 * Token 错误。可以从下面两点检查 1.  Token 是否过期，如果过期您需要向 App Server 重新请求一个新的 Token
                 *                  2.  token 对应的 appKey 和工程里设置的 appKey 是否一致
                 */
                @Override
                public void onTokenIncorrect() {
                    Log.d(Constant.TAG, "--onTokenIncorrect" );
                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token 对应的用户 id
                 */
                @Override
                public void onSuccess(String userid) {
                    Log.d(Constant.TAG, "--连接融云成功" );
                    InitListener.init(userid,mContext,mActivity);
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    Log.d(Constant.TAG, "--ErrorCode" +errorCode);
                }
            });
        }
    }

    /**
     * Author FGB
     * Description 任务管理
     * Created at 2017/11/20 22:47
     * Version 1.0
     */

    public class TimerManager {

        public TimerManager(final long PERIOD_DAY ) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 1); //凌晨1点
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            Date date=calendar.getTime(); //第一次执行定时任务的时间
            //如果第一次执行定时任务的时间 小于当前的时间
            //此时要在 第一次执行定时任务的时间加一天，以便此任务在下个时间点执行。如果不加一天，任务会立即执行。
            if (date.before(new Date())) {
                date = this.addDay(date, 1);
            }
            Timer timer = new Timer();
            Task task = new Task();
            //安排指定的任务在指定的时间开始进行重复的固定延迟执行。
            timer.schedule(task,date,PERIOD_DAY);
        }
        // 增加或减少天数
        public Date addDay(Date date, int num) {
            Calendar startDT = Calendar.getInstance();
            startDT.setTime(date);
            startDT.add(Calendar.DAY_OF_MONTH, num);
            return startDT.getTime();
        }
    }

//    public class Task extends TimerTask {
//        public void run() {
//            //执行定时任务 查询当天订单数据
//            PosSqliteDatabaseUtils.selectData(mContext);
//        }
//    }

//    private List<PosPayBean> posPayBeanList;
//    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
//    public void onDataSynEvent(List<PosPayBean> posPayBeanArrayList) {
//        if(posPayBeanArrayList.size()!=0){
//            posPayBeanList=new ArrayList<>();
//            posPayBeanList=posPayBeanArrayList;
//            strJson= JsonUtils.toJson(posPayBeanList);
////            payLog("PayLog",strJson,AbDateUtil.getCurrentDate());//上线记得开启日志上传
//        }else{
//            ToastUtils.getLongToast(mContext,"当前没有订单记录可上传！");
//        }
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
//    public void onDataSynEvent(PayLogBean bean) {
//        if(bean.isSuccess()!=false){
//            ToastUtils.getLongToast(mContext,"上传日志成功！");
//        }else{
//            ToastUtils.getLongToast(mContext,"上传日志失败！");
//        }
//    }

}
