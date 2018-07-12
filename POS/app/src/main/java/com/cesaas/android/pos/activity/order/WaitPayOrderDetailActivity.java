package com.cesaas.android.pos.activity.order;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.activity.cashier.CashierHomeActivity;
import com.cesaas.android.pos.activity.cashier.CheckAccountsListActivity;
import com.cesaas.android.pos.activity.cashier.WeiXinAndAliPayActivity;
import com.cesaas.android.pos.activity.cashier.WeiXinAndAliPaySingleActivity;
import com.cesaas.android.pos.adapter.listviewadapter.adapterlibrary.ViewHolder;
import com.cesaas.android.pos.adapter.listviewadapter.adapterlibrary.abslistview.CommonAdapter;
import com.cesaas.android.pos.base.BaseActivity;
import com.cesaas.android.pos.bean.OrderInvalidEventBus;
import com.cesaas.android.pos.bean.PayCallbackBean;
import com.cesaas.android.pos.bean.PayOrderDetailBean;
import com.cesaas.android.pos.bean.PayOrderDetailBeanListBean;
import com.cesaas.android.pos.bean.PosBean;
import com.cesaas.android.pos.bean.PosOfflineRefundBean;
import com.cesaas.android.pos.bean.ResultWaitPayOrderDetailBean;
import com.cesaas.android.pos.bean.order.Item;
import com.cesaas.android.pos.bean.printer.LatticePrinterBean;
import com.cesaas.android.pos.bean.printer.LatticePrinterOrderBean;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.global.Urls;
import com.cesaas.android.pos.net.nohttp.HttpListener;
import com.cesaas.android.pos.net.xutils.net.ReturnGoodsNet;
import com.cesaas.android.pos.utils.AbDateUtil;
import com.cesaas.android.pos.utils.CashierTicketPrinterTools;
import com.cesaas.android.pos.utils.DecimalFormatUtils;
import com.cesaas.android.pos.utils.OrderCashierPrinterTools;
import com.cesaas.android.pos.utils.OrderCashierTicketPrinterTools;
import com.cesaas.android.pos.utils.RandomUtils;
import com.cesaas.android.pos.utils.SingleCashierPrinterTools;
import com.cesaas.android.pos.utils.Skip;
import com.cesaas.android.pos.utils.ToastUtils;
import com.google.gson.Gson;
import com.wangpos.pay.UnionPay.PosConfig;
import com.wangpos.poscore.IPosCallBack;
import com.wangpos.poscore.PosCore;
import com.wangpos.poscore.impl.PosCoreFactory;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;
import com.zhl.cbdialog.CBDialogBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;

import java.util.HashMap;

import cn.weipass.pos.sdk.IPrint;
import cn.weipass.pos.sdk.LatticePrinter;
import cn.weipass.pos.sdk.impl.WeiposImpl;

/**
 * 待支付订单详情
 */
public class WaitPayOrderDetailActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout ll_order_list_back,ll_instantly_pay,ll_again_print;
    private LinearLayout ll_single_weixin_pay,ll_single_ali_pay,ll_single_union_pay,ll_single_cash_pay,ll_cancel_cashier;
    private TextView tv_wait_pay_amount,tv_caeate_cashier_staff,tv_retail_id,tv_order_create_time,tv_wait_order_total_amount;
    private TextView tv_refund;
    private ListView lv_wait_account_detail;
    private EditText tv_show_msg;

    //8583协议中的参考号
    private String refNum;
    private PosCore pCore;
    private PosCallBack callBack;
    private byte[] lock = new byte[1];
    int EVENT_NO_PAPER = 1;
    private final int LOCK_WAIT = 0;
    private final int LOCK_CONTINUE = 1;
    private PosCore.RXiaoFei rXiaoFei;

    private String amount;
    private int retailCheck;//0：未支付，1：已支付
    private int payType;//订单支付类型
    private String referenceNumber;//参考号
    private String traceAuditNumber;//凭证号
    private LatticePrinter latticePrinter;// 点阵打印
    private LatticePrinterOrderBean latticePrinterOrderBean;//票据打印Bean

    private String TraceAuditNumber;
    private int PayType;

    private String orderId;
    private double payMoney;
    private Integer money;
    private String retailCashier;
    private String shopNameNick;

    private CustomSingleCashierDialog singleCashierDialog;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //3s后执行代码
            //调用pos打印机
            setLatticePrinter();

            //跳转到查账列表
            Skip.mNext(mActivity, CheckAccountsListActivity.class);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_pay_order_detail);

        //初始化CoreApp连接对象
        initPosCore();

        Bundle bundle=getIntent().getExtras();
        orderId=bundle.getString("OrderId");
        shopNameNick=prefs.getString("shopNameNick");

        initView();
        initData();

    }

    private void initView() {
        //通过EventBus订阅事件
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        tv_show_msg= (EditText) findViewById(R.id.tv_show_msg);
        tv_refund= (TextView) findViewById(R.id.tv_refund);
        tv_wait_order_total_amount= (TextView) findViewById(R.id.tv_wait_order_total_amount);
        tv_order_create_time= (TextView) findViewById(R.id.tv_order_create_time);
        lv_wait_account_detail= (ListView) findViewById(R.id.lv_wait_account_detail);
        tv_caeate_cashier_staff= (TextView) findViewById(R.id.tv_caeate_cashier_staff);
        tv_wait_pay_amount= (TextView) findViewById(R.id.tv_wait_pay_amount);
        tv_retail_id= (TextView) findViewById(R.id.tv_retail_id);
        ll_order_list_back= (LinearLayout) findViewById(R.id.ll_order_list_back);
        ll_instantly_pay= (LinearLayout) findViewById(R.id.ll_instantly_pay);
        ll_again_print= (LinearLayout) findViewById(R.id.ll_again_print);
        ll_order_list_back.setOnClickListener(this);
        ll_instantly_pay.setOnClickListener(this);
        tv_refund.setOnClickListener(this);
        ll_again_print.setOnClickListener(this);
    }


    public void initData(){
        Request<String> request = NoHttp.createStringRequest(Urls.WAIT_PAY_ORDER, RequestMethod.POST);
        request.add("RetailId",orderId);
        commonNet.requestNetTask(request, waitPayOrderDetailListener);

        Request<String> requestOrder = NoHttp.createStringRequest(Urls.PAY_JOURNAL, RequestMethod.POST);
        requestOrder.add("RetailId",orderId);
        commonNet.requestNetTask(requestOrder, payOrderDetailListener);
    }

    public HttpListener<String> payOrderDetailListener = new HttpListener<String>()
    {
        @Override
        public void onSucceed(int what, Response<String> response)
        {
            PayOrderDetailBeanListBean bean=gson.fromJson(response.get(),PayOrderDetailBeanListBean.class);
            if(bean.isSuccess()==true && bean.TModel!=null) {
                for(int i=0;i<bean.TModel.size();i++){
                    TraceAuditNumber=bean.TModel.get(i).getTraceAuditNumber();
                    PayType=bean.TModel.get(i).getPayType();
//                    Log.i(Constant.TAG,"getTraceAuditNumber=="+TraceAuditNumber+"==PayType:"+PayType);
                }
            }

        }
        @Override
        public void onFailed(int what, Response<String> response)
        {
            Log.d(Constant.TAG,"Failed:"+response.getException());
        }
    };


    //待支付订单详情回调监听
    public HttpListener<String> waitPayOrderDetailListener = new HttpListener<String>()
    {
        @Override
        public void onSucceed(int what, Response<String> response)
        {Log.d(Constant.TAG,"待支付订单详情回调:"+response.get());
            Gson gson=new Gson();
            ResultWaitPayOrderDetailBean bean=gson.fromJson(response.get(),ResultWaitPayOrderDetailBean.class);
            if(bean.isSuccess()!=false && bean.TModel!=null){
                tv_wait_order_total_amount.setText(bean.TModel.Retail.getRetailTotal()+"");
                tv_wait_pay_amount.setText(bean.TModel.Retail.getRetailPayment()+"");
                tv_caeate_cashier_staff.setText(bean.TModel.Retail.getCreateName());
                tv_retail_id.setText(bean.TModel.Retail.getRetailId()+"");
                tv_order_create_time.setText(bean.TModel.Retail.getCreateTime());

                retailCheck=bean.TModel.Retail.getRetailCheck();
                payMoney=bean.TModel.Retail.getRetailPayment();

                if(retailCheck==0){//未支付
                    ll_instantly_pay.setVisibility(View.VISIBLE);
                }else{
                    //已支付
                    ll_again_print.setVisibility(View.VISIBLE);
                }
                lv_wait_account_detail.setAdapter(new CommonAdapter<Item>(mContext,R.layout.item_wait_order_detail,bean.TModel.Item){

                    @Override
                    public void convert(ViewHolder holder, final Item itemEntity, int postion) {
                        holder.setText(R.id.tv_order_name,itemEntity.getStyleName());
                        holder.setText(R.id.tv_order_count,itemEntity.getQuantity()+"");
                        holder.setText(R.id.tv_order_sale_price,itemEntity.getSellPrice()+"");
                        holder.setText(R.id.tv_order_pay_price,itemEntity.getPayMent()+"");

                        //退换货
                        holder.setOnClickListener(R.id.tv_return_goods, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new CBDialogBuilder(WaitPayOrderDetailActivity.this)
                                        .setTouchOutSideCancelable(true)
                                        .showCancelButton(true)
                                        .setTitle("订单退货")
                                        .setMessage("是否确定该订单需要退货！")
                                        .setConfirmButtonText("确定")
                                        .setCancelButtonText("取消")
                                        .setDialogAnimation(CBDialogBuilder.DIALOG_ANIM_SLID_BOTTOM)
                                        .setButtonClickListener(true, new CBDialogBuilder.onDialogbtnClickListener() {
                                            @Override
                                            public void onDialogbtnClick(Context context, Dialog dialog, int whichBtn) {
                                                switch (whichBtn) {
                                                    case BUTTON_CONFIRM:
                                                        JSONArray arr=new JSONArray();
                                                        arr.put(itemEntity.getRetailSubId());
                                                        ReturnGoodsNet returnGoodsNet=new ReturnGoodsNet(mContext);
                                                        String tr=TraceAuditNumber+ AbDateUtil.getCurrentTimeAsNumber()+RandomUtils.getFourRandom();
                                                        returnGoodsNet.setData(tr,itemEntity.getPayMent(),itemEntity.getRetailId()+"",payType,prefs.getString("enCode"),arr);
                                                        break;
                                                    case BUTTON_CANCEL:
                                                        ToastUtils.show("已取消退货");
                                                        break;
                                                    default:
                                                        break;
                                                }
                                            }
                                        })
                                        .create().show();
                            }
                        });
                    }
                });
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

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(PosOfflineRefundBean msg) {
        if(msg.isSuccess()==true){
            ToastUtils.getLongToast(mContext,"退货成功！");
            //重新刷新数据
           Skip.mNext(mActivity,CheckAccountsListActivity.class);
        }else{
            ToastUtils.getLongToast(mContext,"退货失败！"+msg.getMessage());
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_order_list_back://返回
                Skip.mBack(mActivity);
                break;

            case R.id.ll_again_print://重新打印
                new CBDialogBuilder(WaitPayOrderDetailActivity.this)
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
                        .create().show();;
                break;

            case R.id.tv_refund:
                Bundle bundle=new Bundle();
                bundle.putString("OrderId",orderId);
//                bundle.putString("CreateName",orderlist.get(position).getCreateName());
//                bundle.putDouble("PayAmount",orderlist.get(position).getPayAmount());
//                bundle.putInt("IsRefund",orderlist.get(position).getIsRefund());
                //跳转到账单详情
                Skip.mNextFroData(mActivity,CheckAccountDetailActivity.class,bundle);
                break;

            case R.id.ll_instantly_pay://立即支付
                singleCashierDialog=new CustomSingleCashierDialog(mContext, R.style.dialog, R.layout.item_custom_single_cashier_dialog);
                singleCashierDialog.show();
                singleCashierDialog.setCancelable(false);
                break;
        }
    }



    /**
     * 自定义独立收银dialog
     */
    public class CustomSingleCashierDialog extends Dialog implements View.OnClickListener{
        int layoutRes;//布局文件
        Context context;

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
        }

        public void initView(){

            ll_single_weixin_pay= (LinearLayout) findViewById(R.id.ll_single_weixin_pay);
            ll_single_ali_pay= (LinearLayout) findViewById(R.id.ll_single_ali_pay);
            ll_single_union_pay= (LinearLayout) findViewById(R.id.ll_single_union_pay);
            ll_single_cash_pay= (LinearLayout) findViewById(R.id.ll_single_cash_pay);
            ll_cancel_cashier= (LinearLayout) findViewById(R.id.ll_cancel_cashier);

            ll_single_weixin_pay.setOnClickListener(this);
            ll_single_ali_pay.setOnClickListener(this);
            ll_single_union_pay.setOnClickListener(this);
            ll_single_cash_pay.setOnClickListener(this);
            ll_cancel_cashier.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ll_single_weixin_pay://微信支付

                    bundle.putInt("Pay",2);
                    bundle.putDouble("PayMoney",payMoney);
                    bundle.putString("OrderId",orderId);
                    bundle.putString("shopNameNick",shopNameNick);
                    bundle.putString("userName",retailCashier);

                    Skip.mNextFroData(mActivity,WeiXinAndAliPayActivity.class,bundle);

                    singleCashierDialog.dismiss();

                    break;
                case R.id.ll_single_ali_pay://支付宝
                    bundle.putInt("Pay",3);
                    bundle.putDouble("PayMoney",payMoney);
                    bundle.putString("OrderId",orderId);
                    bundle.putString("shopNameNick",shopNameNick);
                    bundle.putString("userName",retailCashier);
                    Skip.mNextFroData(mActivity,WeiXinAndAliPayActivity.class,bundle);

                    singleCashierDialog.dismiss();
                    break;
                case R.id.ll_single_union_pay://银联支付
                    //启动银联收银
                    payType=4;
                    amount = "2";
                    lock[0] = LOCK_WAIT;
                    doConsumeHasTemplate(amount,orderId);

                    singleCashierDialog.dismiss();
                    break;
                case R.id.ll_single_cash_pay://现金支付
                    //随机生成12位参考号【规则：当前时间+2位随机数】
                    referenceNumber = RandomUtils.getCurrentTimeAsNumber() + RandomUtils.getToFourRandom();
                    //随机生成6位凭证号【规则：当月+4位随机数】
                    traceAuditNumber = RandomUtils.getCurrentTimeMM() + RandomUtils.getFourRandom();
                    payType=5;

                    getPayListener(referenceNumber,traceAuditNumber,payMoney,orderId,payType);

                    singleCashierDialog.dismiss();
                    break;

                case R.id.ll_cancel_cashier://取消返回
                    singleCashierDialog.dismiss();
                    break;
                default:
                    break;
            }
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
                    showMsg(
                            "消费成功:>>>>\n卡号:" + rXiaoFei.primaryAccountNumber + "\n"
                                    + "参考号:" + rXiaoFei.retrievalReferenceNumber
                                    + "\n凭证号:" + rXiaoFei.systemTraceAuditNumber
                                    + "\n消费金额:" + rXiaoFei.amounOfTransactions);
                    refNum = rXiaoFei.retrievalReferenceNumber;
                } catch (Exception e) {
                    e.printStackTrace();
                    showMsg(e.getLocalizedMessage());
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

            init_params.put(PosConfig.Name_EX + "1053", shopNameNick);// 签购单小票台头

            init_params.put(PosConfig.Name_EX + "1100", "cn.weipass.cashier");// 核心APP 包名
            init_params.put(PosConfig.Name_EX + "1101", "com.wangpos.cashiercoreapp.services.CoreAppService");// 核心APP 类名
            init_params.put(PosConfig.Name_EX + "1093", "2");// 是否需要打印三联签购单 1.需要 2.不需要
            init_params.put(PosConfig.Name_EX + "1012", "1");// 华势通道

            init_params.put(PosConfig.Name_MerchantName, "coreApp");

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


    /**
     * 收银支付回调方法
     * @param RetrievalReferenceNumber
     * @param TraceAuditNumber
     * @param ConsumeAmount
     * @param OrderId
     * @param PayType
     */
    public void getPayListener(String RetrievalReferenceNumber,String TraceAuditNumber,double ConsumeAmount,String OrderId,int PayType){
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

    //支付成功回调监听
    private HttpListener<String> getPayListener = new HttpListener<String>()
    {
        @Override
        public void onSucceed(int what, Response<String> response)
        {
            PayCallbackBean callbackBean=gson.fromJson(response.get(),PayCallbackBean.class);
            if(callbackBean.isSuccess()==true){
                ToastUtils.getLongToast(mContext,"支付成功，票据打印中...");
                setLatticePrinter();

                Runnable mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        mHandler.sendEmptyMessage(1);
                    }
                };
                mHandler .postDelayed(mRunnable, 3000); // 在Handler中执行子线程并延迟3s。

                //跳转到查账列表
//                Skip.mNext(mActivity, CheckAccountsListActivity.class);
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
            Toast.makeText(WaitPayOrderDetailActivity.this, "尚未初始化点阵打印sdk，请稍后再试", Toast.LENGTH_SHORT).show();
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
            latticePrinterOrderBean.setShopClerkName(retailCashier);
            latticePrinterOrderBean.setTraceAuditNumber(traceAuditNumber);
            switch (payType){
                case 5://现金
                    latticePrinterOrderBean.setPayTitleName("现金支付");
                    break;
                case 4://银联
                    latticePrinterOrderBean.setPayTitleName("银联支付");
                    break;
                case 3://支付宝
                    latticePrinterOrderBean.setPayTitleName("支付宝");
                    break;
                case 2://微信
                    latticePrinterOrderBean.setPayTitleName("微信支付");
                    break;
            }
            //打印
            CashierTicketPrinterTools.printLattice(WaitPayOrderDetailActivity.this,latticePrinter,latticePrinterOrderBean,false);
            //刷新当前页面
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
                final AlertDialog dialog = new AlertDialog.Builder(WaitPayOrderDetailActivity.this).setView(view).setCancelable(false).create();
                dialog.show();

                Button btn_confirm = (Button) view.findViewById(R.id.btn_consume_confiem);
                Button btn_cancel = (Button) view.findViewById(R.id.btn_consume_cancel);
                final EditText ed_consumen_amount = (EditText) view.findViewById(R.id.ed_consume_amount);
                ed_consumen_amount.setText(payMoney+"");//设置支付金额
                btn_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        synchronized (lock) {//确定支付
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

    @Override
    protected void onDestroy() {
        //取消EventBus订阅
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

}
