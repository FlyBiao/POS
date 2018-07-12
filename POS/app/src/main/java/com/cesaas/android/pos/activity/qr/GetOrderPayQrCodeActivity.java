package com.cesaas.android.pos.activity.qr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.activity.cashier.CashierHomeActivity;
import com.cesaas.android.pos.activity.cashier.CashierMainActivity;
import com.cesaas.android.pos.activity.cashier.CheckAccountsListActivity;
import com.cesaas.android.pos.activity.cashier.WeiXinAndAliPayActivity;
import com.cesaas.android.pos.base.BaseActivity;
import com.cesaas.android.pos.bean.PayCallbackBean;
import com.cesaas.android.pos.bean.PosBean;
import com.cesaas.android.pos.bean.PosPayLogBean;
import com.cesaas.android.pos.bean.ResultCreatePayBean;
import com.cesaas.android.pos.bean.printer.LatticePrinterBean;
import com.cesaas.android.pos.db.bean.PosPayBean;
import com.cesaas.android.pos.db.bean.ScanPayInfoBean;
import com.cesaas.android.pos.db.pay.PosSqliteDatabaseUtils;
import com.cesaas.android.pos.global.Gloabl;
import com.cesaas.android.pos.global.Urls;
import com.cesaas.android.pos.net.nohttp.HttpListener;
import com.cesaas.android.pos.net.xutils.net.CreatePayNet;
import com.cesaas.android.pos.net.xutils.net.PosPayLogNet;
import com.cesaas.android.pos.utils.AbDateUtil;
import com.cesaas.android.pos.utils.CreateQRImageUtils;
import com.cesaas.android.pos.utils.DecimalFormatUtils;
import com.cesaas.android.pos.utils.OrderCashierPrinterTools;
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

import java.util.HashMap;

import cn.weipass.pos.sdk.IPrint;
import cn.weipass.pos.sdk.LatticePrinter;
import cn.weipass.pos.sdk.impl.WeiposImpl;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：订单收款二维码
 * 创建日期：2016/10/27
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class GetOrderPayQrCodeActivity extends BaseActivity {

    private ImageView iv_order_pay_code_img;
    private EditText ed_order_qrcode_show_msg;
    private LinearLayout ll_order_qrcode_pay_back;
    private TextView tv_pay_amount;

    private PosCore pCore;
    private PosCallBack callBack;
    private String refNo;
    private int cardCategory=100;

    private int pay;
    private Integer money;
    private double payMoney;
    private String orderNo;
    private String userName;
    private String shopNameNick;
    private boolean isSuccess=false;
    private double discountAfter;
    private double discount;
    private double originalPrice;
    private int SaleId;
    private int IsPractical;
    private String strJson;

    private CreatePayNet createPayNet;

    private int payOrderStauts=1;//支付订单状态【1:订单需要继续查询;2:订单已经关闭;3:订单完成支付   ,未知】

    private Runnable mRunnable;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mHandler.removeCallbacks(mRunnable);
            //3s后执行代码 调用pos打印机
            setLatticePrinter();

            PosPayLogBean posPayLogBean=new PosPayLogBean();
            posPayLogBean.setOrderId(orderNo);//支付订单
            posPayLogBean.setPayAmount(payMoney);//支付金额
            posPayLogBean.setTraceAuditNumber(refNo);//支付凭证号
            posPayLogBean.setPayType(pay);//支付类型
            posPayLogBean.setEnCode(prefs.getString("enCode"));//设备EN号
            posPayLogBean.setRemark("零售单");
            strJson=gson.toJson(posPayLogBean);

            if(pay==3){//支付宝回调
                createPayNet=new CreatePayNet(mContext);
                createPayNet.setData(SaleId+"",payMoney,3,refNo,refNo,"","支付宝支付",refNo,prefs.getString("userShopId"),"",0,cardCategory);
            }else{//微信回调
                createPayNet=new CreatePayNet(mContext);
                createPayNet.setData(SaleId+"",payMoney,2,refNo,refNo,"","微信支付",refNo,prefs.getString("userShopId"),"",0,cardCategory);
            }

            if(pay==3){//支付宝回调
                getPayListener("",refNo,payMoney,orderNo,3,"notFansVip",IsPractical);
            }else{//微信回调
                getPayListener("",refNo,payMoney,orderNo,2,"notFansVip",IsPractical);
            }
        }
    };

    Handler handler=new Handler();
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            if(payOrderStauts==3){//订单完成支付
                showMsg("订单完成支付，正在打印...");
                //停止计时器
                handler.removeCallbacks(runnable);
                PosSqliteDatabaseUtils.update(mContext,payOrderStauts+"","订单完成支付，正在打印","true",orderNo,"1");
                showMsg("订单完成支付，正在打印...");
                setLatticePrinter();
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        mHandler.sendEmptyMessage(1);
                    }
                };
                mHandler .postDelayed(mRunnable, 3000); // 在Handler中执行子线程并延迟3s。

            }else if(payOrderStauts==2){//订单已经关闭
                PosSqliteDatabaseUtils.update(mContext,payOrderStauts+"","订单已经关闭","false",orderNo,"0");
                showMsg("订单已经关闭:\n"+refNo);
                //停止计时器
                handler.removeCallbacks(runnable);
            }else if(payOrderStauts==1){//订单需要继续查询
                PosSqliteDatabaseUtils.update(mContext,payOrderStauts+"","订单需要继续查询","false",orderNo,"0");
                showMsg("正在查询...");
                //停止计时器
                handler.removeCallbacks(runnable);
                queryPayResult(refNo);//调用执行查询支付结果
                //每隔一秒刷新一次
                handler.postDelayed(this, 2000);
            }else{
                PosSqliteDatabaseUtils.update(mContext,payOrderStauts+"","订单未知","false",orderNo,"0");
                Log.i("test", "订单未知。");
                showMsg("订单未知。");
                //停止计时器
                handler.removeCallbacks(runnable);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_order_pay_qr_code);
        Bundle bundle=getIntent().getExtras();
        pay=bundle.getInt("Pay");
        IsPractical=bundle.getInt("IsPractical");
        SaleId=bundle.getInt("SaleId");
        payMoney=bundle.getDouble("PayMoney");
        orderNo=bundle.getString("OrderNo");
        userName=bundle.getString("userName");
        shopNameNick=bundle.getString("shopNameNick");
        originalPrice=bundle.getDouble("originalPrice");
        discountAfter=bundle.getDouble("discountAfter");
        discount=bundle.getDouble("discount");

        //通过EventBus订阅事件
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        initView();

        initPosCore();

        initData();

        if(payMoney!=0){
            tv_pay_amount.setText(payMoney+"");
        }else{
            tv_pay_amount.setText("0");
        }
    }

    /**
     * 查询支付结果
     */
    public void queryPayResult(final String refNos){
        if(refNos!=null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        PosCore.RXiaoFei_WX_ZFB chaxun_wx_zfb = pCore.chaXun_WX_ZFB(refNos, callBack);
                        payOrderStauts=chaxun_wx_zfb.orderStauts;
                        Log.d("test","查询二维码支付状态"+"订单状态:" + chaxun_wx_zfb.orderStauts + "\n" + "第三方订单号:" + chaxun_wx_zfb.exInfo.get(Gloabl.thirdSerialNo));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("test","QUERY_ERROR:"+e.getLocalizedMessage());
                    showMsg(e.getLocalizedMessage());
                    }
                }
            }).start();
        }
    }

    public void initData(){
        money=(int)(payMoney*100);
        if(pay==2){//微信
            initPayQRCode(money+"","1");
        }else{//支付宝
            initPayQRCode(money+"","2");
        }
    }

    public void initView(){
        iv_order_pay_code_img= (ImageView) findViewById(R.id.iv_order_pay_code_img);
        ll_order_qrcode_pay_back= (LinearLayout) findViewById(R.id.ll_order_qrcode_pay_back);
        ed_order_qrcode_show_msg= (EditText) findViewById(R.id.ed_order_qrcode_show_msg);
        tv_pay_amount= (TextView) findViewById(R.id.tv_pay_amount);

        ll_order_qrcode_pay_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(payOrderStauts==1){
                    showPayInfo();
                }else{
                    //停止计时器
                    handler.removeCallbacks(runnable);
                    Skip.mNext(mActivity, CheckAccountsListActivity.class);
                }
            }
        });
    }

    /**
     * 初始化支付二维码
     * @param amount 支付金额
     * @param payType 支付方式   1：微信，2：支付宝
     */
    public void initPayQRCode(final String amount,final String payType){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PosCore.RXiaoFei_WX_ZFB rXiaoFei_wx_zfb = pCore.xiaoFei_WX_ZFB(amount, payType, callBack);
                    refNo = rXiaoFei_wx_zfb.retrievalReferenceNumber;
                    showMsg("扫描以下付款二维码进行支付");

                    ScanPayInfoBean bean=new ScanPayInfoBean();
                    bean.setOrderNo(orderNo);
                    bean.setRetrievalReferenceNumber(refNo);
                    bean.setScanTime(AbDateUtil.getCurrentDate());
                    if(pay==3){
                        bean.setTypes("3");
                    }else{
                        bean.setTypes("2");
                    }

                    if(refNo!=null && !"".equals(refNo)){
                        if(pay==3){
                            PosPayBean payBean=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),userName,orderNo,payMoney+"",refNo,pay+"","支付宝支付",IsPractical+"","2",AbDateUtil.getCurrentDate(),rXiaoFei_wx_zfb.orderStauts+"",prefs.getString("enCode"),"","false","1","0");
                            insertData(payBean);
                        }else{
                            PosPayBean payBean=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),userName,orderNo,payMoney+"",refNo,pay+"","微信支付",IsPractical+"","2",AbDateUtil.getCurrentDate(),rXiaoFei_wx_zfb.orderStauts+"",prefs.getString("enCode"),"","false","1","0");
                            insertData(payBean);
                        }

                        //直接发布
                        PosBean posBean=new PosBean();
                        posBean.PayQrCode=rXiaoFei_wx_zfb.primaryAccountNumber;
                        EventBus.getDefault().post(posBean);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("test","===="+e.getLocalizedMessage());
                    showMsg(e.getLocalizedMessage());
                }
            }
        }).start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(ResultCreatePayBean bean) {
        if(bean.isSuccess()!=true){
            if(pay==1){//积分支付退款回调

            }else if(pay==2){//微信支付退款回调

                payLog("微信支付",strJson,bean.getMessage());
            }else if(pay==3){//支付宝退款回调

                payLog("支付宝支付",strJson,bean.getMessage());
            }else if(pay==4){//银联支付退款回调
                payLog("银联支付",strJson,bean.getMessage());
            }else if(pay==5){//现金支付退款回调
                payLog("现金支付",strJson,bean.getMessage());
            }else{
                payLog("",strJson,bean.getMessage());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(PosBean bean) {
        try {
            if (bean.PayQrCode!=null) {
                CreateQRImageUtils createQRImageUtils=new CreateQRImageUtils();
                iv_order_pay_code_img.setImageBitmap(createQRImageUtils.createQRImage(bean.PayQrCode, 650, 650));

                //启动计时器 每1秒执行一次runnable
                handler.postDelayed(runnable,2000);

            } else {
                Toast.makeText(GetOrderPayQrCodeActivity.this, "请输入要生成二维码的字符串",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化CoreApp连接对象
     *
     * @return
     */
    private void initPosCore() {
        if (pCore == null) {
            // SocketCommClient sc = new SocketCommClient(ADDR,this);

            // 配置数据为开发阶段的数据
            HashMap<String, String> init_params = new HashMap<>();

            // 渠道ID和主密钥索引,任意传入1个,则可以指定主密钥索引,如果都不传 默认为渠道ID为6669 自动分配终端主密钥索引
//            init_params.put(PosConfig.Name_EX + "1052", "1012");// 渠道号  [华势暂时使用广东银联的渠道ID来获取商户号和终端号, 2个插件共用同一个密钥索引]
            init_params.put(PosConfig.Name_EX + "1053", prefs.getString("shopNameNick"));// 签购单小票台头

            init_params.put(PosConfig.Name_EX + "1100", "cn.weipass.cashier");// 核心APP 包名
            init_params.put(PosConfig.Name_EX + "1101", "com.wangpos.cashiercoreapp.services.CoreAppService");// 核心APP 类名
//            init_params.put(PosConfig.Name_EX + "1600", "这是小票:${myOrderNo}");
            init_params.put(PosConfig.Name_EX + "1600_1", "这是第一联的小票:${1D:${myOrderNo}}");
            init_params.put(PosConfig.Name_EX + "1600_2", "这是第二联的小票:${2D:${myOrderNo}}");

            init_params.put(PosConfig.Name_EX + "1091", "0");// 交易成功，就删除冲正文件 不以小票打印是否成功 为判断点 1.不以小票打印成功为判断点 0.以小票打印成功为判断点
            init_params.put(PosConfig.Name_EX + "1092", "1");//  是否开启订单生成，并且上报服务器 1.开启 0.不开启
//            init_params.put(PosConfig.Name_EX + "1054", "0");// 是否打印小票  1，可以打 0，不打
//            init_params.put(PosConfig.Name_EX + "1087", "0");//是否 关闭插卡 1.开启插卡 0.关闭插卡
            init_params.put(PosConfig.Name_EX + "1089", "1");//是否 需要获取 已开通的支付方式 1.获取 0.不获取
            init_params.put(PosConfig.Name_EX + "1085", "1");//是否支持TC校验，批上送 1，是 0，不是
            init_params.put(PosConfig.Name_EX + "1086", "1");//是否启动 电子签名 1.是 0 不是
//            init_params.put(PosConfig.Name_EX + "1088", "0");//是否 关闭批结算 1.开启批结算 0.关闭批结算
//			init_params.put(PosConfig.Name_EX + "1082", "36");// 主密钥索引

            init_params.put(PosConfig.Name_MerchantName, "coreApp");

            pCore = PosCoreFactory.newInstance(this, init_params);
            callBack = new PosCallBack(pCore);
        }

    }

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
                ed_order_qrcode_show_msg.setText(msg);
            }
        });
    }
    @Override
    protected void onDestroy() {
        //取消EventBus订阅
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void getPayListener(String RetrievalReferenceNumber,String TraceAuditNumber,double ConsumeAmount,String OrderId,int PayType,String OpenId,int IsPractical){
        Log.i("test","收银支付回调方法:"+ Urls.PAY_FROM_STORE);
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
                Skip.mNext(mActivity,CashierMainActivity.class,true);
            }else{
                Log.d("test","支付result:"+callbackBean.getMessage());
                //记录pos 支付日志
                if(pay==1){//积分支付退款回调

                }else if(pay==2){//微信支付退款回调
                    payLog("微信支付",strJson,callbackBean.getMessage());
                }else if(pay==3){//支付宝退款回调
                    payLog("支付宝支付",strJson,callbackBean.getMessage());
                }else if(pay==4){//银联支付退款回调
                    payLog("银联支付",strJson,callbackBean.getMessage());
                }else if(pay==5){//现金支付退款回调
                    payLog("现金支付",strJson,callbackBean.getMessage());
                }else{
                    payLog("",strJson,callbackBean.getMessage());
                }
            }
        }
        @Override
        public void onFailed(int what, Response<String> response)
        {
//            ToastUtils.show(response.get());
        }
    };

    public void payLog(String LogType, String obj,String remark){
        //Pay Log
        PosPayLogNet posPayLogNet=new PosPayLogNet(mContext);
        posPayLogNet.setData(LogType,obj,remark);
    }

    private LatticePrinter latticePrinter;// 点阵打印
    private LatticePrinterBean latticePrinterBean;
    /**
     * 设置收银阵打印方法
     */
    public void setLatticePrinter(){
        try {
            // 设备可能没有打印机，open会抛异常
            latticePrinter = WeiposImpl.as().openLatticePrinter();
            //点阵打印
            if (latticePrinter == null) {
                Toast.makeText(GetOrderPayQrCodeActivity.this, "尚未初始化点阵打印sdk，请稍后再试", Toast.LENGTH_SHORT).show();
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

                if(userName!=null){
                    latticePrinterBean.setShopClerkName(userName);
                }else{
                    latticePrinterBean.setShopClerkName(prefs.getString("userName"));
                }
                latticePrinterBean.setShopName(prefs.getString("shopNameNick"));
                latticePrinterBean.setCounterName(prefs.getString("shopNameNick"));
                latticePrinterBean.setOrderId(orderNo);
                latticePrinterBean.setTraceAuditNumber(refNo);
                latticePrinterBean.setOriginalPrice(DecimalFormatUtils.decimalFormatRound(originalPrice));
                latticePrinterBean.setTotalPrice(DecimalFormatUtils.decimalFormatRound(originalPrice));
                latticePrinterBean.setDiscountPrice(DecimalFormatUtils.decimalFormatRound(discountAfter));
                if(pay==2){
                    latticePrinterBean.setPayTitleName("微信支付");
                }else{
                    latticePrinterBean.setPayTitleName("支付宝支付");
                }

                //订单收银打印
                SingleCashierPrinterTools.printLattice(GetOrderPayQrCodeActivity.this, latticePrinter,latticePrinterBean,discount);
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

    private void insertData(PosPayBean bean){
        PosSqliteDatabaseUtils.insterData(mContext,bean);
    }

    public void showPayInfo(){
        try{
            new CBDialogBuilder(GetOrderPayQrCodeActivity.this)
                    .setTouchOutSideCancelable(true)
                    .showCancelButton(true)
                    .setTitle("温馨提示！")
                    .setMessage("订单正在支付中...，确定返回取消支付吗？")
                    .setConfirmButtonText("确定")
                    .setCancelButtonText("取消")
                    .setDialogAnimation(CBDialogBuilder.DIALOG_ANIM_SLID_BOTTOM)
                    .setButtonClickListener(true, new CBDialogBuilder.onDialogbtnClickListener() {
                        @Override
                        public void onDialogbtnClick(Context context, Dialog dialog, int whichBtn) {
                            switch (whichBtn) {
                                case BUTTON_CONFIRM:
                                    //停止计时器
                                    handler.removeCallbacks(runnable);
                                    Skip.mNext(mActivity, CashierMainActivity.class,true);
                                    break;
                                case BUTTON_CANCEL:
                                        ToastUtils.getLongToast(mContext,"请继续等待支付！");
                                    break;
                                default:
                                    break;
                            }
                        }
                    })
                    .create().show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
