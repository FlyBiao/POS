package com.cesaas.android.pos.activity.order;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.activity.cashier.CashierMainActivity;
import com.cesaas.android.pos.activity.cashier.WeiXinAndAliPayActivity;
import com.cesaas.android.pos.base.BaseActivity;
import com.cesaas.android.pos.base.BaseRecyclerView;
import com.cesaas.android.pos.bean.PayCallbackBean;
import com.cesaas.android.pos.bean.PosPayLogBean;
import com.cesaas.android.pos.bean.ResultCreatePayBean;
import com.cesaas.android.pos.bean.ResultWaitPayOrderDetailBean;
import com.cesaas.android.pos.bean.printer.LatticePrinterOrderBean;
import com.cesaas.android.pos.db.bean.PosPayBean;
import com.cesaas.android.pos.db.pay.PosSqliteDatabaseUtils;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.global.Urls;
import com.cesaas.android.pos.listener.OnItemClickListener;
import com.cesaas.android.pos.net.nohttp.HttpListener;
import com.cesaas.android.pos.net.xutils.net.BankCardInfoNet;
import com.cesaas.android.pos.net.xutils.net.CreatePayNet;
import com.cesaas.android.pos.net.xutils.net.PayCategoryDetailNet;
import com.cesaas.android.pos.net.xutils.net.PayFormStoreNet;
import com.cesaas.android.pos.net.xutils.net.PosPayLogNet;
import com.cesaas.android.pos.pos.adapter.PosPayCategoryDetailAdapter;
import com.cesaas.android.pos.pos.bank.ResultBankInfoBean;
import com.cesaas.android.pos.storedvalue.bean.ResultPayCategoryDetailBean;
import com.cesaas.android.pos.utils.AbDateUtil;
import com.cesaas.android.pos.utils.BankUtil;
import com.cesaas.android.pos.utils.CashierTicketPrinterTools;
import com.cesaas.android.pos.utils.DecimalFormatUtils;
import com.cesaas.android.pos.utils.JsonUtils;
import com.cesaas.android.pos.utils.SingleCashierPrinterTools;
import com.cesaas.android.pos.utils.Skip;
import com.cesaas.android.pos.utils.ToastUtils;
import com.google.gson.Gson;
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
import java.util.HashMap;
import java.util.List;

import cn.weipass.pos.sdk.IPrint;
import cn.weipass.pos.sdk.LatticePrinter;
import cn.weipass.pos.sdk.impl.WeiposImpl;

/**
 * 订单详情
 */
public class OrderDetailActivity extends BaseActivity implements View.OnClickListener{

    private LinearLayout ll_order_list_back,ll_again_print,ll_instantly_pay;
    private TextView tv_wait_pay_amount,tv_caeate_cashier_staff,tv_retail_id,tv_order_create_time,tv_wait_order_total_amount,tv_nott_pay_amount;
    private TextView tv_refund,tv_instantly_pay;
    private EditText tv_show_msg;

    //8583协议中的参考号
    private String refNum=null;
    private PosCore pCore;
    private PosCallBack callBack;
    private byte[] lock = new byte[1];
    int EVENT_NO_PAPER = 1;
    private final int LOCK_WAIT = 0;
    private final int LOCK_CONTINUE = 1;
    private PosCore.RXiaoFei rXiaoFei;

    private Integer amount;
    private int retailCheck;//0：未支付，1：已支付
    private int payType;//订单支付类型
    private String traceAuditNumber=null;//凭证号
    private String primaryAccountNumber=null;//银行卡号
    private String cardName=null;//银行卡名称
    private String bankName=null;////发卡行名称
    private int cardCategory=100;//卡类型
    private String cardNum;
    private LatticePrinter latticePrinter;// 点阵打印
    private LatticePrinterOrderBean latticePrinterOrderBean;//票据打印Bean

    private int SaleId;
    private int IsPractical;
    private String orderId;
    private double payMoney;
    private double amounOfTransactions=0;
    private Integer money;
    private String retailCashier;

    private CreatePayNet createPayNet;
    private PayFormStoreNet payFormStoreNet;

    private double pay;//已支付
    private double notPay;//未支付
    private String payName="";
    private String message;

    private List<Integer> orderPayType;
    private CustomSingleCashierDialog singleCashierDialog;

    private PosPayLogBean posPayLogBean;
    private String strJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        Bundle bundle=getIntent().getExtras();
        orderId=bundle.getString("OrderId");

        //初始化CoreApp连接对象
        initPosCore();

        initView();
        initData();
    }

    public void initData(){
        Request<String> request = NoHttp.createStringRequest(Urls.WAIT_PAY_ORDER, RequestMethod.POST);
        request.add("RetailId",orderId);
        commonNet.requestNetTask(request, waitPayOrderDetailListener);
//
//        payFormStoreNet=new PayFormStoreNet(mContext);
//        payFormStoreNet.setData("10002183062018051300000006","10002183062018051300000006",1214.0,"1372475",4,1,"240e4bff");
//////
//        CreatePayNet createPayNet=new CreatePayNet(mContext);
//        createPayNet.setData("749940",1497.0,3,"10002100812018012400000003","10002100812018012400000003","","支付宝支付","10002100812018012400000003",prefs.getString("userShopId"),"",0,100);

//        createPayNet=new CreatePayNet(mContext);//
//        createPayNet.setData("201801241752131915",849.0,4,"175234151353","175234151353","5201521214708811","银联刷卡","175234151353","18527",bankName,2,3);

//        createPayNet=new CreatePayNet(mContext);//
//        createPayNet.setData("201801241402470344",1168.0,4,"140320031669","140320031669","6212263602008930968","银联刷卡","140320031669","18986",bankName,2,3);
    }

    private void initView() {
        //通过EventBus注册订阅事件
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        tv_show_msg= (EditText) findViewById(R.id.tv_show_msg);
        tv_refund= (TextView) findViewById(R.id.tv_refund);
        tv_wait_order_total_amount= (TextView) findViewById(R.id.tv_wait_order_total_amount);
        tv_order_create_time= (TextView) findViewById(R.id.tv_order_create_time);
        tv_caeate_cashier_staff= (TextView) findViewById(R.id.tv_caeate_cashier_staff);
        tv_wait_pay_amount= (TextView) findViewById(R.id.tv_wait_pay_amount);
        tv_nott_pay_amount=(TextView) findViewById(R.id.tv_not_pay_amount);
        tv_retail_id= (TextView) findViewById(R.id.tv_retail_id);
        ll_order_list_back= (LinearLayout) findViewById(R.id.ll_order_list_back);
        tv_instantly_pay= (TextView) findViewById(R.id.tv_instantly_pay);
        ll_again_print= (LinearLayout) findViewById(R.id.ll_again_print);
        ll_instantly_pay= (LinearLayout) findViewById(R.id.ll_instantly_pay);
        ll_order_list_back.setOnClickListener(this);
        tv_instantly_pay.setOnClickListener(this);
        tv_refund.setOnClickListener(this);
        ll_again_print.setOnClickListener(this);
    }

    //待支付订单详情回调监听
    public HttpListener<String> waitPayOrderDetailListener = new HttpListener<String>()
    {
        @Override
        public void onSucceed(int what, Response<String> response)
        {
            Log.i(Constant.TAG,response.get());
            Gson gson=new Gson();
            ResultWaitPayOrderDetailBean bean=gson.fromJson(response.get(),ResultWaitPayOrderDetailBean.class);
            if(bean.isSuccess()!=false && bean.TModel!=null){
                tv_caeate_cashier_staff.setText(bean.TModel.Retail.getRetailCashier());
                tv_retail_id.setText(bean.TModel.Retail.getRetailId()+"");
                tv_order_create_time.setText(bean.TModel.Retail.getCreateTime());

                retailCashier=bean.TModel.Retail.getRetailCashier();
                prefs.putString("retailCashier",retailCashier);
                SaleId=bean.TModel.Retail.getRetailId();
                retailCheck=bean.TModel.Retail.getRetailCheck();
                payMoney=bean.TModel.Retail.getRetailPayment();

                if(bean.TModel.Pay!=null && bean.TModel.Pay.size()!=0){
                    orderPayType=new ArrayList<>();
                    for (int i=0;i<bean.TModel.Pay.size();i++){
                        orderPayType.add(bean.TModel.Pay.get(i).getPayType());
                    }
                }

                if(retailCheck==0){//未支付
                    ll_again_print.setVisibility(View.GONE);
                    ll_instantly_pay.setVisibility(View.VISIBLE);
                    tv_refund.setVisibility(View.GONE);
//                    PosSqliteDatabaseUtils.selectData(mContext);
                }else{
                    //已支付
                    ll_instantly_pay.setVisibility(View.GONE);
                    ll_again_print.setVisibility(View.VISIBLE);
                    tv_refund.setVisibility(View.VISIBLE);
                }

                if(bean.TModel.Pay!=null && bean.TModel.Pay.size()!=0){
                    for (int i=0;i<bean.TModel.Pay.size();i++){
                        pay+=bean.TModel.Pay.get(i).getConsumeAmount();
                        traceAuditNumber=bean.TModel.Pay.get(i).getTraceAuditNumber();
                        switch (bean.TModel.Pay.get(i).getPayType()){
                            case 2:
                                payName+=" 微信支付 ";
                                break;
                            case 3:
                                payName+=" 支付宝支付 ";
                                break;
                            case 4:
                                payName+=" 银联刷卡 ";
                                break;
                            case 5:
                                payName+=" 现金支付 ";
                                break;
                        }
                    }
                }

                if(pay!=0){
                    tv_wait_pay_amount.setText("￥"+pay);
                    notPay=payMoney - pay;
                    tv_nott_pay_amount.setText(DecimalFormatUtils.decimalFormatRound(notPay)+"");
                    tv_nott_pay_amount.setTextColor(mContext.getResources().getColor(R.color.red));

                }else{
                    notPay=payMoney;
                    tv_wait_pay_amount.setText("￥"+0);

                    tv_nott_pay_amount.setText(payMoney+"");
                    tv_nott_pay_amount.setTextColor(mContext.getResources().getColor(R.color.red));
                }
                tv_wait_order_total_amount.setText("￥"+payMoney);

            }else{
                ToastUtils.getLongToast(mContext,"没有找到订单详情！");
            }
        }
        @Override
        public void onFailed(int what, Response<String> response)
        {
            Log.d(Constant.TAG,"Failed:"+response.getException());
        }
    };

    //接收银行卡信息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(ResultBankInfoBean bean) {
        try{
            if(bean.getError_code()==0){
                //获取发卡行，卡种名称
                bankName=bean.getResult().getBank();
                if(bean.getResult().getCardtype()!=null && !"".equals(bean.getResult().getCardtype())){
                    if(bean.getResult().getCardtype().contains("贷记卡")){
                        cardCategory=3;
                    }else if(bean.getResult().getCardtype().contains("信用卡")){
                        cardCategory=2;
                    }else if(bean.getResult().getCardtype().contains("借记卡")){//借记卡
                        cardCategory=1;
                    }else{
                        bankName="银行";
                        cardCategory=3;
                    }
                }else{
                    bankName="银行";
                    cardCategory=3;
                }
            }else{
                if (!TextUtils.isEmpty(cardNum)) {
                    Log.w("test", "卡号为:" + cardNum);
                    try{
                        //获取发卡行，卡种名称
                        cardName=BankUtil.getNameOfBank(primaryAccountNumber);
                        if(cardName!=null && !"".equals(cardName)){
                            bankName=cardName.substring(0, cardName.indexOf("·"));
                            if(cardName.contains("贷记卡")){
                                cardCategory=3;

                            }else if(cardName.contains("信用卡")){
                                cardCategory=2;

                            }else if(cardName.contains("借记卡")){//借记卡
                                cardCategory=1;
                            }else{
                                bankName="银行";
                                cardCategory=3;
                            }
                        }else{
                            bankName="银行";
                            cardCategory=3;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    ToastUtils.getLongToast(mContext,"获取银行卡号为空！");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //支付支付流水
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(ResultCreatePayBean bean) {
        message=bean.Message;
        if(bean.isSuccess()!=false){
            PosSqliteDatabaseUtils.updateUnionPay(mContext,3+"","订单完成支付-创建订单支付成功-创建支付流水成功"+message,"true",orderId,"1",rXiaoFei.retrievalReferenceNumber);
        }else{
            PosSqliteDatabaseUtils.updateUnionPay(mContext,3+"","订单完成支付-创建订单支付成功-创建支付流水失败"+message,"true",orderId,"1",rXiaoFei.retrievalReferenceNumber);
            if(payType==1){//积分支付退款回调

            }else if(payType==2){//微信支付退款回调
                payLog("微信支付",strJson,bean.getMessage());
            }else if(payType==3){//支付宝退款回调
                payLog("支付宝支付",strJson,bean.getMessage());
            }else if(payType==4){//银联支付退款回调
                payLog("银联支付",strJson,bean.getMessage());
            }else if(payType==5){//现金支付退款回调
                payLog("现金支付",strJson,bean.getMessage());
            }else{
                payLog("",strJson,bean.getMessage());
            }
        }
    }

    //支付信息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(PayCallbackBean bean) {
        if(bean.isSuccess()!=false){
            PosSqliteDatabaseUtils.updateUnionPay(mContext,3+"","订单完成支付-创建流水成功-创建订单支付成功"+bean.getMessage(),"true",orderId,"1",rXiaoFei.retrievalReferenceNumber);
            //回到订单页面
            Skip.mNext(mActivity,CashierMainActivity.class);
        }else{
            PosSqliteDatabaseUtils.updateUnionPay(mContext,3+"","订单完成支付-创建流水成功-创建订单支付失败："+bean.getMessage(),"true",orderId,"1",rXiaoFei.retrievalReferenceNumber);
            //记录pos 支付日志
            if(payType==1){//积分支付退款回调

            }else if(payType==2){//微信支付退款回调
                payLog("微信支付",strJson,bean.getMessage());
            }else if(payType==3){//支付宝退款回调
                payLog("支付宝支付",strJson,bean.getMessage());
            }else if(payType==4){//银联支付退款回调

                payLog("银联支付",strJson,bean.getMessage());
            }else if(payType==5){//现金支付退款回调
                payLog("现金支付",strJson,bean.getMessage());
            }else{
                payLog("",strJson,bean.getMessage());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_order_list_back://返回
                Skip.mBack(mActivity);
                break;

            case R.id.ll_again_print://重新打印
                new CBDialogBuilder(mContext)
                        .setTouchOutSideCancelable(true)
                        .showCancelButton(true)
                        .setTitle("温馨提示！")
                        .setMessage("是否确定该订单需要重新打印！")
                        .setConfirmButtonText("确定")
                        .setCancelButtonText("取消")
                        .setDialogAnimation(CBDialogBuilder.DIALOG_ANIM_SLID_BOTTOM)
                        .setButtonClickListener(true, new CBDialogBuilder.onDialogbtnClickListener() {
                            @Override
                            public void onDialogbtnClick(Context context, Dialog dialog, int whichBtn) {
                                switch (whichBtn) {
                                    case BUTTON_CONFIRM:
                                        //执行打印操作
                                        setLatticePrinter();
                                        break;
                                    case BUTTON_CANCEL:
                                        ToastUtils.show("已取消打印");
                                        break;
                                    default:
                                        break;
                                }
                            }
                        })
                        .create().show();
                break;

            case R.id.tv_refund:
                Bundle bundle=new Bundle();
                bundle.putString("OrderId",orderId);
                bundle.putString("CreateName",tv_caeate_cashier_staff.getText().toString());
                //跳转到账单详情
                Skip.mNextFroData(mActivity,CheckAccountDetailActivity.class,bundle);
                break;

            case R.id.tv_instantly_pay://立即支付
                singleCashierDialog=new CustomSingleCashierDialog(mContext, R.style.dialog, R.layout.item_custom_single_cashier_dialog);
                singleCashierDialog.show();
                singleCashierDialog.setCancelable(false);
                break;
        }
    }

    public void getPayListener(String RetrievalReferenceNumber,String TraceAuditNumber,double ConsumeAmount,String OrderId,int PayType,String OpenId,int IsPractical){
        Log.i("test","支付回调方法:"+Urls.PAY_FROM_STORE);
        Request<String> request = NoHttp.createStringRequest(Urls.PAY_FROM_STORE, RequestMethod.POST);
        request.add("RetrievalReferenceNumber",RetrievalReferenceNumber);//参考号
        request.add("TraceAuditNumber",TraceAuditNumber);//凭证号
        request.add("ConsumeAmount",ConsumeAmount);//消费金额
        request.add("RetailId",OrderId);//支付订单号
        request.add("EnCode",prefs.getString("enCode"));//设备EN号
        request.add("PayType",PayType);
        request.add("OpenId",OpenId);//
        request.add("IsPractical",IsPractical);//
        commonNet.requestNetTask(request,getPayListener,1);
    }

    //支付成功回调监听
    private HttpListener<String> getPayListener = new HttpListener<String>()
    {
        @Override
        public void onSucceed(int what, Response<String> response)
        {
            Log.d("test","支付result:"+response.get());
            PayCallbackBean callbackBean=gson.fromJson(response.get(),PayCallbackBean.class);
            if(callbackBean.isSuccess()==true){
                Skip.mNext(mActivity,CashierMainActivity.class);
            }else{
                Log.d("test","支付result:"+callbackBean.getMessage());
            }
        }
        @Override
        public void onFailed(int what, Response<String> response)
        {
//            ToastUtils.show(response.get());
        }
    };

    public List<ResultPayCategoryDetailBean.PayCategoryBea> categoryBeaList;
    public PosPayCategoryDetailAdapter posPayCategoryAdapter;
    public SwipeMenuRecyclerView rvView;
    /**
     * 自定义独立收银dialog
     */
    public class CustomSingleCashierDialog extends Dialog {
        int layoutRes;//布局文件
        Context context;
        LinearLayout ll_cancel_pos_cashier;

        /**
         * 自定义收银主题及布局的构造方法
         * @param context
         * @param theme
         * @param resLayout
         */
        public CustomSingleCashierDialog(Context context, int theme,int resLayout){
            super(context, theme);
            this.context = context;
            this.layoutRes=resLayout;
        }
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.setContentView(layoutRes);
            initView();
            initPayData();
        }

        public void initPayData(){
            try{
                if(mCache.getAsString("GetPayCategory")!=null && !"".equals(mCache.getAsString("GetPayCategory"))){
                    ResultPayCategoryDetailBean bean= JsonUtils.fromJson(mCache.getAsString("GetPayCategory"),ResultPayCategoryDetailBean.class);
                    categoryBeaList=new ArrayList<>();
                    categoryBeaList.addAll(bean.TModel);

                    posPayCategoryAdapter=new PosPayCategoryDetailAdapter(categoryBeaList,5);
                    posPayCategoryAdapter.setOnItemClickListener(onItemClickListener);
                    rvView.setAdapter(posPayCategoryAdapter);
                }else{
                    PayCategoryDetailNet payCategoryNet=new PayCategoryDetailNet(mContext,mCache);
                    payCategoryNet.setData();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public void initView(){
            ll_cancel_pos_cashier= (LinearLayout) findViewById(R.id.ll_cancel_pos_cashier);
            rvView= (SwipeMenuRecyclerView) findViewById(R.id.rv_view);
            BaseRecyclerView.initRecyclerView(getContext(),rvView,false);
            ll_cancel_pos_cashier.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(ResultPayCategoryDetailBean bean) {
        if(bean.isSuccess()!=false && bean.TModel!=null){
            categoryBeaList=new ArrayList<>();
            categoryBeaList.addAll(bean.TModel);
            posPayCategoryAdapter=new PosPayCategoryDetailAdapter(categoryBeaList,5);
            posPayCategoryAdapter.setOnItemClickListener(onItemClickListener);
            rvView.setAdapter(posPayCategoryAdapter);
        }else{
            ToastUtils.getLongToast(mContext,"获取支付方式失败！"+bean.getMessage());
        }
    }

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            switch (categoryBeaList.get(position).getCategoryType()){
                case 2://微信支付
                    bundle.putInt("Pay",2);
                    bundle.putInt("SaleId",SaleId);
                    bundle.putDouble("PayMoney",Double.parseDouble(DecimalFormatUtils.decimalToFormat(notPay)));
                    bundle.putString("OrderId",orderId);
                    bundle.putInt("IsPractical",categoryBeaList.get(position).getIsPractical());
                    bundle.putString("shopNameNick",prefs.getString("shopNameNick"));
                    bundle.putString("userName",retailCashier);
                    singleCashierDialog.dismiss();
                    Skip.mNextFroData(mActivity,WeiXinAndAliPayActivity.class,bundle);
                    break;
                case 3://支付宝
                    bundle.putInt("Pay",3);
                    bundle.putInt("SaleId",SaleId);
                    bundle.putDouble("PayMoney",Double.parseDouble(DecimalFormatUtils.decimalToFormat(notPay)));
                    bundle.putString("OrderId",orderId);
                    bundle.putString("shopNameNick",prefs.getString("shopNameNick"));
                    bundle.putString("userName",retailCashier);
                    bundle.putInt("IsPractical",categoryBeaList.get(position).getIsPractical());
                    singleCashierDialog.dismiss();
                    Skip.mNextFroData(mActivity,WeiXinAndAliPayActivity.class,bundle);
                    break;
                case 4://银联支付
                    //启动银联收银
                    IsPractical=categoryBeaList.get(position).getIsPractical();
                    payType=4;
                    double amounts=Double.parseDouble(tv_nott_pay_amount.getText().toString());
                    amount =(int) amounts*100;
                    lock[0] = LOCK_WAIT;
                    doConsumeHasTemplate(amount+"",orderId);

                    singleCashierDialog.dismiss();
                    break;
                case 5://现金支付
                    ToastUtils.getLongToast(mContext,"不支持该支付方式或已经使用过该支付方式");
                    break;
                default:
                    ToastUtils.getLongToast(mContext,"不支持该支付方式或已经使用过该支付方式");
                    break;
            }
        }
    };


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

                    showMsg("消费成功:>>>>\n卡号:" + rXiaoFei.primaryAccountNumber + "\n"
                                    + "参考号:" + rXiaoFei.retrievalReferenceNumber
                                    + "\n凭证号:" + rXiaoFei.systemTraceAuditNumber
                                    + "\n消费金额:" + rXiaoFei.amounOfTransactions);

                    amounOfTransactions=Double.parseDouble(rXiaoFei.amounOfTransactions)/100;

                    PosSqliteDatabaseUtils.updateUnionPay(mContext,3+"","订单完成支付","true",orderId,"1",rXiaoFei.retrievalReferenceNumber);

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            createPayNet=new CreatePayNet(mContext,mActivity,prefs,tv_caeate_cashier_staff.getText().toString(),orderNo,amounOfTransactions,IsPractical);
                            payFormStoreNet=new PayFormStoreNet(mContext);

                            if(rXiaoFei.retrievalReferenceNumber!=null && !"".equals(rXiaoFei.retrievalReferenceNumber)){
                                createPayNet.setData(orderId,amounOfTransactions,4,rXiaoFei.retrievalReferenceNumber,rXiaoFei.retrievalReferenceNumber,primaryAccountNumber,"银联刷卡",rXiaoFei.systemTraceAuditNumber,prefs.getString("userShopId"),bankName,0,cardCategory);
                                payFormStoreNet.setData(rXiaoFei.retrievalReferenceNumber,rXiaoFei.retrievalReferenceNumber,amounOfTransactions,orderId,4,IsPractical,prefs.getString("enCode"));
                            }else{
                                if(orderNo!=null){
                                    createPayNet.setData(orderId,amounOfTransactions,4,orderNo,orderNo,primaryAccountNumber,"银联刷卡",orderNo,prefs.getString("userShopId"),bankName,0,cardCategory);
                                    payFormStoreNet.setData(orderNo,orderNo,amounOfTransactions,orderId,4,IsPractical,prefs.getString("enCode"));
                                }else{
                                    createPayNet.setData(orderId,amounOfTransactions,4,orderId,orderNo,primaryAccountNumber,"银联刷卡",orderId,prefs.getString("userShopId"),bankName,0,cardCategory);
                                    payFormStoreNet.setData(orderId,orderId,amounOfTransactions,orderId,4,IsPractical,prefs.getString("enCode"));
                                }
                            }
                            posPayLogBean=new PosPayLogBean();
                            posPayLogBean.setOrderId(orderId);//支付订单
                            posPayLogBean.setPayAmount(DecimalFormatUtils.decimalFormatRound(notPay));//支付金额
                            posPayLogBean.setTraceAuditNumber(rXiaoFei.retrievalReferenceNumber);//支付凭证号
                            posPayLogBean.setPayType(4);//支付类型
                            posPayLogBean.setEnCode(prefs.getString("enCode"));//设备EN号
                            posPayLogBean.setRemark("零售单");
                            posPayLogBean.setAccountNumber(primaryAccountNumber);
                            strJson=gson.toJson(posPayLogBean);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    showMsg(e.getLocalizedMessage());
                    PosSqliteDatabaseUtils.deleteByNo(mContext,orderNo);
                    Log.d(Constant.TAG,"银联ERROR：="+e.getLocalizedMessage());
                }
            }
        }.start();
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

            init_params.put(PosConfig.Name_EX + "1053", prefs.getString("shopNameNick"));// 签购单小票台头

            init_params.put(PosConfig.Name_EX + "1100", "cn.weipass.cashier");// 核心APP 包名
            init_params.put(PosConfig.Name_EX + "1101", "com.wangpos.cashiercoreapp.services.CoreAppService");// 核心APP 类名
            init_params.put(PosConfig.Name_EX + "1092", "1");//  是否开启订单生成，并且上报服务器 1.开启 0.不开启
            init_params.put(PosConfig.Name_EX + "1093", "2");// 是否需要打印三联签购单 1.需要 2.不需要
            init_params.put(PosConfig.Name_EX + "1012", "1");// 华势通道

            init_params.put(PosConfig.Name_MerchantName, prefs.getString("BrandName"));

            pCore = PosCoreFactory.newInstance(this, init_params);
            callBack = new PosCallBack(pCore);
        }
    }

    private void showMsg(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_show_msg.setText(msg);
            }
        });
    }


    public void payLog(String LogType, String obj,String remark){
        //Pay Log
        PosPayLogNet posPayLogNet=new PosPayLogNet(mContext);
        posPayLogNet.setData(LogType,obj,remark);
    }

    /**
     * s设置点阵打印方法
     */
    public void setLatticePrinter(){
        try {
            // 设备可能没有打印机，open会抛异常
            latticePrinter = WeiposImpl.as().openLatticePrinter();
            //点阵打印
            if (latticePrinter == null) {
                Toast.makeText(mContext, "尚未初始化点阵打印sdk，请稍后再试", Toast.LENGTH_SHORT).show();
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
                latticePrinterOrderBean.setShopName(prefs.getString("shopNameNick"));
                latticePrinterOrderBean.setOrderId(orderId);
                latticePrinterOrderBean.setCounterName(prefs.getString("shopNameNick"));
                latticePrinterOrderBean.setShopClerkName(tv_caeate_cashier_staff.getText().toString());
                latticePrinterOrderBean.setTraceAuditNumber(traceAuditNumber);
                latticePrinterOrderBean.setOriginalPrice(payMoney);
                latticePrinterOrderBean.setDiscountPrice(payMoney);
                latticePrinterOrderBean.setTotalPrice(payMoney);
                latticePrinterOrderBean.setPayTitleName(payName);
                //打印
                CashierTicketPrinterTools.printLattice(mContext,latticePrinter,latticePrinterOrderBean,false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * pos打印显示结果信息
     * @param operInfo
     * @param titleHeader
     * @param info
     */
    private void showResultInfo(String operInfo, String titleHeader, String info) {
        try{
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
        }catch (Exception e){
            e.printStackTrace();
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
                final AlertDialog dialog = new AlertDialog.Builder(mContext).setView(view).setCancelable(false).create();
                dialog.show();

                Button btn_confirm = (Button) view.findViewById(R.id.btn_consume_confiem);
                Button btn_cancel = (Button) view.findViewById(R.id.btn_consume_cancel);
                final EditText ed_consumen_amount = (EditText) view.findViewById(R.id.ed_consume_amount);
                ed_consumen_amount.setText(DecimalFormatUtils.decimalFormatRound(notPay)+"");//设置支付金额
                ed_consumen_amount.setFocusable(false);ed_consumen_amount.setFocusableInTouchMode(false);//设置不可编辑状态；
                btn_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        synchronized (lock) {//确定支付
                            double biao=Double.parseDouble(ed_consumen_amount.getText().toString());
                            money = (int)(biao*100);

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
                    cardNum = (String) params[0];
                    if (!TextUtils.isEmpty(cardNum)) {
                        Log.w(Constant.TAG, "卡号为:" + params[0]);
                        try{
                            primaryAccountNumber=params[0]+"";
                            //获取发卡行，卡种名称
                            cardName=BankUtil.getNameOfBank(primaryAccountNumber);
                            if(cardName!=null && !"".equals(cardName)){
                                bankName=cardName.substring(0, cardName.indexOf("·"));
                                if(cardName.contains("贷记卡")){
                                    cardCategory=3;

                                }else if(cardName.contains("信用卡")){
                                    cardCategory=2;

                                }else if(cardName.contains("借记卡")){//借记卡
                                    cardCategory=1;
                                }else{
                                    //执行查询银行卡信息接口
                                    mActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            BankCardInfoNet cardInfoNet=new BankCardInfoNet(mContext,cardNum);
                                            cardInfoNet.setData();
                                        }
                                    });
                                }
                            }else{
                                //执行查询银行卡信息接口
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        BankCardInfoNet cardInfoNet=new BankCardInfoNet(mContext,cardNum);
                                        cardInfoNet.setData();
                                    }
                                });
                            }
                            showConsumeDialog(core);
                            PosPayBean posPayBean=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),tv_caeate_cashier_staff.getText().toString(),orderId,payMoney+"",refNum,"4","银联支付",IsPractical+"",1+"", AbDateUtil.getCurrentDate(),"银行卡支付",prefs.getString("enCode"),primaryAccountNumber,"false","1","0");
                            insertData(posPayBean);

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
                    refNum=(String)params[0];
                    showMsg("参考号:" + params[0]);
                    break;
                }
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
                AlertDialog.Builder dialog = new AlertDialog.Builder(OrderDetailActivity.this);
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

    @Override
    protected void onDestroy() {
        //取消EventBus订阅
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
    private void insertData(PosPayBean bean){
        PosSqliteDatabaseUtils.insterData(mContext,bean);
    }
}
