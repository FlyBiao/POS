package com.cesaas.android.pos.activity.order;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.activity.cashier.CashierMainActivity;
import com.cesaas.android.pos.activity.cashier.CheckAccountsListActivity;
import com.cesaas.android.pos.adapter.listviewadapter.adapterlibrary.ViewHolder;
import com.cesaas.android.pos.adapter.listviewadapter.adapterlibrary.abslistview.CommonAdapter;
import com.cesaas.android.pos.base.BaseActivity;
import com.cesaas.android.pos.bean.PayOrderDetailBean;
import com.cesaas.android.pos.bean.PayOrderDetailBeanListBean;
import com.cesaas.android.pos.bean.PosBean;
import com.cesaas.android.pos.bean.PosOfflineRefundBean;
import com.cesaas.android.pos.bean.PosPayLogBean;
import com.cesaas.android.pos.bean.printer.LatticePrinterOrderBean;
import com.cesaas.android.pos.db.bean.PosPayBean;
import com.cesaas.android.pos.db.pay.PosSqliteDatabaseUtils;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.global.Gloabl;
import com.cesaas.android.pos.global.Urls;
import com.cesaas.android.pos.net.nohttp.HttpListener;
import com.cesaas.android.pos.net.xutils.net.CreatePayNet;
import com.cesaas.android.pos.net.xutils.net.PosPayLogNet;
import com.cesaas.android.pos.utils.AbDateUtil;
import com.cesaas.android.pos.utils.CashierTicketPrinterTools;
import com.cesaas.android.pos.utils.DecimalFormatUtils;
import com.cesaas.android.pos.utils.SingleCashierPrinterTools;
import com.cesaas.android.pos.utils.Skip;
import com.cesaas.android.pos.utils.ToastUtils;
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
 * 描    述：获取支付订单详情
 * 创建日期：2016/10/10 13:52
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class CheckAccountDetailActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout ll_pos_order_list_back;
    private TextView tv_consume_amount;
    private TextView tv_cashier_staff;
    private TextView tv_print;

    private ListView lv_account_detail;
    private EditText tv_show_pay_msg;

    private String CreateName;
    private String orderId;//订单id
    private double payAmount;//支付ine
    private double orderAmount;//订单金额
    private double returnGoodsAmount;//退货金额
    private boolean isRefund=false;

    private int payType;//支付方式
    private String traceAuditNumber;//支付凭证号
    private PosCore pCore;
    int EVENT_NO_PAPER = 1;

    private int REQUEST_CONTACT = 20;
    final int RESULT_CODE = 101;
    private String scanCode;

    private PosPayLogBean posPayLogBean;
    private PosPayLogNet posPayLogNet;
    private CreatePayNet createPayNet;

    private List<PayOrderDetailBean> orderDetails=new ArrayList<PayOrderDetailBean>();

    private LatticePrinter latticePrinter;// 点阵打印
    private LatticePrinterOrderBean latticePrinterOrderBean;//票据打印Bean

    /**
     * 8583协议中的参考号
     */
    private String refNum;
    //pos回调
    WxAliPosCallBack wxAliPosCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_account_detail_list);

        Bundle bundle=getIntent().getExtras();
        orderId=bundle.getString("OrderId");
        CreateName=bundle.getString("CreateName");
        payAmount=bundle.getDouble("PayAmount");
//        isRefund=bundle.getInt("IsRefund");

        initPosCore();

        initView();
        setOnClickListener();

        if(orderId!=null){
            Request<String> request = NoHttp.createStringRequest(Urls.PAY_JOURNAL, RequestMethod.POST);
            request.add("RetailId",orderId);
            commonNet.requestNetTask(request, payOrderDetailListener);
        }else{
            ToastUtils.show("订单号不能为空!");
        }
    }

    /**
     * 初始化控件
     */
    public void initView(){
        lv_account_detail= (ListView) findViewById(R.id.lv_account_detail);
        tv_show_pay_msg= (EditText) findViewById(R.id.tv_show_pay_msg);
        ll_pos_order_list_back= (LinearLayout) findViewById(R.id.ll_pos_order_list_back);
        tv_consume_amount= (TextView) findViewById(R.id.tv_consume_amount);
        tv_cashier_staff= (TextView) findViewById(R.id.tv_cashier_staff);
        tv_print= (TextView) findViewById(R.id.tv_print);
    }

    //设置点击监听
    public void setOnClickListener(){
        ll_pos_order_list_back.setOnClickListener(this);
        tv_print.setOnClickListener(this);
    }

    //银联支付成功回调监听
    public HttpListener<String> payOrderDetailListener = new HttpListener<String>()
    {
        @Override
        public void onSucceed(int what, Response<String> response)
        {
            Log.d(Constant.TAG,"账本详情:"+response.get());
            PayOrderDetailBeanListBean bean=gson.fromJson(response.get(),PayOrderDetailBeanListBean.class);
            if(bean.isSuccess()==true && bean.TModel!=null) {
                tv_cashier_staff.setText(CreateName);
                orderDetails.addAll(bean.TModel);
                setAdapter();
            }
            for(int i=0;i<bean.TModel.size();i++){
                orderAmount+=bean.TModel.get(i).getConsumeAmount();
            }
            tv_consume_amount.setText(Double.parseDouble(DecimalFormatUtils.decimalToFormat(orderAmount))+"");
        }
        @Override
        public void onFailed(int what, Response<String> response)
        {
            Log.d(Constant.TAG,"Failed:"+response.getException());
        }
    };

    /**
     * 设置订单详情数据适配器
     */
    public void setAdapter(){
        lv_account_detail.setAdapter(new CommonAdapter<PayOrderDetailBean>(mContext,R.layout.activity_check_account_detail,orderDetails) {
            @Override
            public void convert(ViewHolder holder, final PayOrderDetailBean payOrderDetailBean, final int postion) {
                holder.setText(R.id.tv_order_create_date,payOrderDetailBean.getCreateTime());
                holder.setText(R.id.tv_order_detail_id,payOrderDetailBean.getRetailId());
                holder.setText(R.id.tv_order_amount,payOrderDetailBean.getConsumeAmount()+"");

                if(payOrderDetailBean.getPayCategory()!=0){
//                    holder.getView(R.id.tv_return_goods).setVisibility(View.GONE);
                    holder.setText(R.id.tv_order_status,"已退款");
                    holder.setTextColor(R.id.tv_order_status,mContext.getResources().getColor(R.color.red));
                    holder.getView(R.id.iv_pay_refund).setVisibility(View.GONE);
                }else{
                    holder.setText(R.id.tv_order_status,"正常");
                }

                if(payOrderDetailBean.getTraceAuditNumber()!=null){
//                    holder.setText(R.id.tv_audit_number,payOrderDetailBean.getTraceAuditNumber().substring(0, payOrderDetailBean.getTraceAuditNumber().length()-1));
                    holder.setText(R.id.tv_audit_number,payOrderDetailBean.getTraceAuditNumber());
                    traceAuditNumber=payOrderDetailBean.getTraceAuditNumber();
                }
                //判断支付方式
                payType=payOrderDetailBean.getPayType();
                holder.setText(R.id.tv_pay_type,payOrderDetailBean.getDescription());
                if(payType==2 || payType==3 || payType==4){
                    holder.getView(R.id.iv_pay_refund).setVisibility(View.VISIBLE);
                }else{
                    holder.getView(R.id.iv_pay_refund).setVisibility(View.GONE);
                }

                switch (payOrderDetailBean.getPayType()){
                    case 1:
                        holder.setText(R.id.tv_pay_type,"积分支付");
                        break;
                    case 2:
                        holder.setText(R.id.tv_pay_type,"微信");
//                        holder.getView(R.id.iv_pay_type_logo).setBackgroundDrawable(getResources().getDrawable(R.mipmap.weixin));
                        holder.getView(R.id.iv_pay_refund).setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        holder.setText(R.id.tv_pay_type,"支付宝");
                        break;
                    case 4:
                        holder.setText(R.id.tv_pay_type,"银联支付");
                        break;
                    case 5:
                        holder.setText(R.id.tv_pay_type,"现金支付");
                        break;
                    case 6:
                        holder.setText(R.id.tv_pay_type,"优惠券支付");
                        break;
                    case 7:
                        holder.setText(R.id.tv_pay_type,"订金支付");
                        break;
                    case 8:
                        holder.setText(R.id.tv_pay_type,"代金券支付");
                        break;
                    case 9:
                        holder.setText(R.id.tv_pay_type,"储值卡");
                        break;
                    case 10:
                        holder.setText(R.id.tv_pay_type,"定做费");
                        break;
                    case 98:
                        holder.setText(R.id.tv_pay_type,"非实销");
                        break;
                }

                holder.setOnClickListener(R.id.iv_pay_refund, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(payOrderDetailBean.getPayType()==4){
                            ToastUtils.show(mContext,"只支持微信支付宝退款，请选择其他路径进行退款！",ToastUtils.CENTER);
                        }else{
                            if(payOrderDetailBean.getPayCategory()!=0){
                                ToastUtils.show(mContext,"该订单已退款！",ToastUtils.CENTER);
                            }else{
                                if(!AbDateUtil.getStringDateShort().equals(AbDateUtil.toDateYMD(payOrderDetailBean.getCreateTime()))){
                                    ToastUtils.show(mContext,"请选择当天支付订单退款！",ToastUtils.CENTER);
                                }else{
                                    returnGoodsAmount=orderDetails.get(postion).getConsumeAmount();
                                    Skip.mRefundActivityResults(mActivity, CaptureActivity.class, REQUEST_CONTACT,payOrderDetailBean.getTraceAuditNumber());
                                }
                            }
                        }
                    }
                });
            }
        });
    }

    //银联支付log回调监听
    public HttpListener<String> payLogListener = new HttpListener<String>()
    {
        @Override
        public void onSucceed(int what, Response<String> response)
        {
            Log.d(Constant.TAG,"PayLog::"+response.get());

        }
        @Override
        public void onFailed(int what, Response<String> response)
        {
            Log.d(Constant.TAG,"Failed:"+response.getException());
        }
    };

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_pos_order_list_back://返回
                Skip.mBack(mActivity);
                break;
            case R.id.tv_print://重新打印票据
                    ToastUtils.getLongToast(mContext,"重新打印票据");
                break;
        }
    }


    /**
     * 处理扫描Activity返回的数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_CODE) {
            if(data.getStringExtra("mRefundResult")!=null && data.getStringExtra("mRefundResult").equals("100")){
                scanCode= data.getStringExtra("resultCode");
                Log.i("test","扫描信息:"+scanCode);
                //执行退款操作
                new CBDialogBuilder(mContext)
                        .setTouchOutSideCancelable(true)
                        .showCancelButton(true)
                        .setTitle("温馨提示！")
                        .setMessage("请确认该订单是否需要退款？")
                        .setConfirmButtonText("确定")
                        .setCancelButtonText("取消")
                        .setDialogAnimation(CBDialogBuilder.DIALOG_ANIM_SLID_BOTTOM)
                        .setButtonClickListener(true, new CBDialogBuilder.onDialogbtnClickListener() {
                            @Override
                            public void onDialogbtnClick(Context context, Dialog dialog, int whichBtn) {
                                switch (whichBtn) {
                                    case BUTTON_CONFIRM:
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    final int amount=(int)(Double.parseDouble(DecimalFormatUtils.decimalToFormat(returnGoodsAmount))*100);
                                                    final PosCore.RTuiHuo rTuiHuo = pCore.tuiHuo_WX_ZFB(scanCode, amount+"", wxAliPosCallBack);
                                                    showMsg("退款成功:\n凭证号：" + rTuiHuo.retrievalReferenceNumber);
                                                    traceAuditNumber=rTuiHuo.retrievalReferenceNumber;

                                                    PosPayBean bean=new PosPayBean(prefs.getString("userShopId"),"",prefs.getString("retailCashier"),orderId,returnGoodsAmount+"",rTuiHuo.retrievalReferenceNumber,"","退款成功","","",AbDateUtil.getCurrentDate(),"进入退款成功回调",prefs.getString("enCode"),"","true","1","1");
                                                    insertData(bean);

                                                    isRefund=true;
                                                    //执行打印操作
                                                    setLatticePrinter();

                                                    mActivity.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try{
                                                                if(payType==2){
                                                                    createPayNet=new CreatePayNet(mContext);
                                                                    createPayNet.setData(orderId,-returnGoodsAmount,2,rTuiHuo.retrievalReferenceNumber,"","","微信退款",rTuiHuo.retrievalReferenceNumber,prefs.getString("userShopId"),"",0,100);
                                                                }else if(payType==3){
                                                                    createPayNet=new CreatePayNet(mContext);
                                                                    createPayNet.setData(orderId,-returnGoodsAmount,3,rTuiHuo.retrievalReferenceNumber,"","","支付宝退款",rTuiHuo.retrievalReferenceNumber,prefs.getString("userShopId"),"",0,100);
                                                                }else {
                                                                    createPayNet=new CreatePayNet(mContext);
                                                                    createPayNet.setData(orderId,-returnGoodsAmount,payType,rTuiHuo.retrievalReferenceNumber,"","","退款",rTuiHuo.retrievalReferenceNumber,prefs.getString("userShopId"),"",0,100);
                                                                }
                                                            }catch (Exception e){
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });

                                                } catch (final Exception e) {
                                                    showMsg(e.getLocalizedMessage());
                                                    e.printStackTrace();
                                                }
                                            }
                                        }).start();
                                        break;
                                    case BUTTON_CANCEL:
                                        ToastUtils.show("已取消退款");
                                        break;
                                    default:
                                        break;
                                }
                            }
                        })
                        .create().show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void insertData(PosPayBean bean){
        PosSqliteDatabaseUtils.insterData(mContext,bean);
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
                latticePrinterOrderBean.setShopClerkName(prefs.getString("retailCashier"));
                latticePrinterOrderBean.setTraceAuditNumber(traceAuditNumber);
                latticePrinterOrderBean.setOriginalPrice(returnGoodsAmount);
                latticePrinterOrderBean.setDiscountPrice(returnGoodsAmount);
                latticePrinterOrderBean.setTotalPrice(returnGoodsAmount);
                switch (payType){
                    case 2:
                        latticePrinterOrderBean.setPayTitleName("微信支付");
                        break;
                    case 3:
                        latticePrinterOrderBean.setPayTitleName("支付宝支付");
                        break;
                    case 4:
                        latticePrinterOrderBean.setPayTitleName("银联支付");
                        break;
                    case 5:
                        latticePrinterOrderBean.setPayTitleName("现金支付");
                        break;
                }
                //打印
                CashierTicketPrinterTools.printLattice(mContext,latticePrinter,latticePrinterOrderBean,isRefund);
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
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
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
     * //Pay Log
     * @param LogType
     * @param obj
     * @param remark
     */
    public void payLog(String LogType, String obj,String remark){
        posPayLogNet=new PosPayLogNet(mContext);
        posPayLogNet.setData(LogType,obj,remark);
    }

    /**
     * 微信支付pos回调
     */
    class WxAliPosCallBack implements IPosCallBack {
        private final PosCore core;

        WxAliPosCallBack(PosCore core) {
            this.core = core;
        }

        @Override
        public void onInfo(String s) {
            showMsg(s);
        }

        @Override
        public void onEvent(int eventID, Object[] params) throws Exception {
            switch (eventID) {
                case EVENT_Task_start: {
                    showMsg("任务进程开始执行");
                    break;
                }
                case EVENT_Task_end: {
                    showMsg("任务进程执行结束");
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

                default: {
                    showMsg("Event:" + eventID);
                    break;
                }
            }
        }
    }

    private void showMsg(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_show_pay_msg.setText(msg);
            }
        });
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
            wxAliPosCallBack=new WxAliPosCallBack(pCore);
        }
    }

    @Override
    protected void onDestroy() {
        //取消EventBus订阅
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
