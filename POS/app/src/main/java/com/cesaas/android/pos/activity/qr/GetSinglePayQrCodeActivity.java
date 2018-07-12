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
import com.cesaas.android.pos.activity.cashier.WeiXinAndAliPaySingleActivity;
import com.cesaas.android.pos.base.BaseActivity;
import com.cesaas.android.pos.bean.PosBean;
import com.cesaas.android.pos.bean.ResultCreatePayBean;
import com.cesaas.android.pos.bean.printer.LatticePrinterBean;
import com.cesaas.android.pos.db.bean.PosPayBean;
import com.cesaas.android.pos.db.pay.PosSqliteDatabaseUtils;
import com.cesaas.android.pos.global.Gloabl;
import com.cesaas.android.pos.net.xutils.net.CreatePayNet;
import com.cesaas.android.pos.utils.AbDateUtil;
import com.cesaas.android.pos.utils.CreateQRImageUtils;
import com.cesaas.android.pos.utils.DecimalFormatUtils;
import com.cesaas.android.pos.utils.RandomUtils;
import com.cesaas.android.pos.utils.SingleCashierPrinterTools;
import com.cesaas.android.pos.utils.Skip;
import com.cesaas.android.pos.utils.ToastUtils;
import com.wangpos.pay.UnionPay.PosConfig;
import com.wangpos.poscore.IPosCallBack;
import com.wangpos.poscore.PosCore;
import com.wangpos.poscore.impl.PosCoreFactory;
import com.zhl.cbdialog.CBDialogBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import cn.weipass.pos.sdk.IPrint;
import cn.weipass.pos.sdk.LatticePrinter;
import cn.weipass.pos.sdk.impl.WeiposImpl;

/**
 * 独立收款二维码
 */
public class GetSinglePayQrCodeActivity extends BaseActivity {

    private ImageView iv_pay_code_img;
    private EditText ed_show_msg;
    private LinearLayout ll_qrcode_pay_back;
    private TextView tv_pay_amount;

    private PosCore pCore;
    private PosCallBack callBack;
    private String refNo;

    private int pay;
    private Integer money;
    private double payMoney;
    private String orderNo;
    private double discount;
    private String mobile;
    private String userName;//用户名【营业员】
    private String shopNameNick;//店铺名
    private int point;
    private double originalPrice;
    private double discountAfter;
    private boolean isSuccess=false;
    private int cardCategory=100;

    private String TraceAudit;
    private String systemTraceAuditNumber;
    private String primaryAccountNumber;

    private CreatePayNet createPayNet;

    private int payOrderStauts=1;//支付订单状态【0:未知,1:订单需要继续查询,用户还未完成支付;2:订单已经关闭;3:订单完成支付】

    private PosBean posBean=new PosBean();

    private Runnable mRunnable;
    Handler handler=new Handler();

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //停止打印线程
            mHandler.removeCallbacks(mRunnable);
            setLatticePrinter();
        }
    };
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            if(payOrderStauts==3){//订单完成支付
                try{
                    //停止计时器
                    handler.removeCallbacks(runnable);

                    if(pay==3){//支付宝回调
                        PosSqliteDatabaseUtils.update(mContext,payOrderStauts+"","订单完成支付","true",orderNo,"1");
                        createPayNet=new CreatePayNet(mContext);
                        createPayNet.setData(orderNo,payMoney,3,refNo,TraceAudit,"","支付宝支付",refNo,prefs.getString("userShopId"),"",2,cardCategory);
                    }else{//微信回调
                        PosSqliteDatabaseUtils.update(mContext,payOrderStauts+"","订单完成支付","true",orderNo,"1");
                        createPayNet=new CreatePayNet(mContext);
                        createPayNet.setData(orderNo,payMoney,2,refNo,TraceAudit,"","微信支付",refNo,prefs.getString("userShopId"),"",2,cardCategory);
                    }

                    setLatticePrinter();
                    //启动打印线程
                    mRunnable= new Runnable() {
                        @Override
                        public void run() {
                            mHandler.sendEmptyMessage(1);
                        }
                    };
                    // 在Handler中执行子线程并延迟3s。
                    mHandler .postDelayed(mRunnable, 3000);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }else if(payOrderStauts==2){//订单已经关闭
                PosSqliteDatabaseUtils.update(mContext,payOrderStauts+"","订单已经关闭","false",orderNo,"0");
                //停止计时器
                handler.removeCallbacks(runnable);
            }else if(payOrderStauts==1){//订单需要继续查询
                PosSqliteDatabaseUtils.update(mContext,payOrderStauts+"","订单需要继续查询","false",orderNo,"0");
                //停止计时器
                handler.removeCallbacks(runnable);
                queryPayResult(refNo);//调用执行查询支付结果
                //每隔一秒刷新一次
                handler.postDelayed(this, 2000);
            }else{//订单未知
                PosSqliteDatabaseUtils.update(mContext,payOrderStauts+"","订单未知","false",orderNo,"0");
                //停止计时器
                handler.removeCallbacks(runnable);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_pay_qr_code);
        initView();
        Bundle bundle=getIntent().getExtras();
        pay=bundle.getInt("Pay");
        payMoney=bundle.getDouble("PayMoney");
        orderNo=bundle.getString("OrderNo");
        point=bundle.getInt("point");
        discount=bundle.getDouble("discount");
        shopNameNick=bundle.getString("shopNameNick");
        userName=bundle.getString("userName");
        originalPrice=bundle.getDouble("originalPrice");
        discountAfter=bundle.getDouble("discountAfter");
        mobile=bundle.getString("mobile");

        tv_pay_amount.setText(payMoney+"");

        //通过EventBus订阅事件
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        initPosCore();

        initData();

    }

    /**
     * 接收支付流水结果信息
     * @param bean
     */
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(ResultCreatePayBean bean) {
        if(bean.isSuccess()!=false){
            Skip.mNext(mActivity,CashierMainActivity.class,true);
        }else{
            ToastUtils.getToast(mContext,"创建支付流水失败："+bean.getMessage());
        }
    }

    /**
     * 初始化数据
     */
    public void initData(){
        //支付金额
        money=(int)(payMoney*100);
        if(pay==2){//微信
            initPayQRCode(money+"","1");
        }else{//支付宝
            initPayQRCode(money+"","2");
        }
    }

    /**
     * 初始化视图控件
     */
    public void initView(){
        iv_pay_code_img= (ImageView) findViewById(R.id.iv_pay_code_img);
        ll_qrcode_pay_back= (LinearLayout) findViewById(R.id.ll_qrcode_pay_back);
        ed_show_msg= (EditText) findViewById(R.id.ed_show_msg);
        tv_pay_amount= (TextView) findViewById(R.id.tv_pay_amount);

        ll_qrcode_pay_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //停止计时器
//                handler.removeCallbacks(runnable);
                showPayInfo();
            }
        });
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
                        Log.d("jsonDugInfo","查询二维码支付状态"+"订单状态:" + chaxun_wx_zfb.orderStauts + "\n" + "第三方订单号:" + chaxun_wx_zfb.exInfo.get(Gloabl.thirdSerialNo));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("jsonDugInfo","QUERY_ERROR:"+e.getLocalizedMessage());
                        showMsg(e.getLocalizedMessage());
                    }
                }
            }).start();
        }
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
                    TraceAudit = rXiaoFei_wx_zfb.exInfo.get(Gloabl.thirdSerialNo);
                    primaryAccountNumber=rXiaoFei_wx_zfb.primaryAccountNumber;
                    refNo = rXiaoFei_wx_zfb.retrievalReferenceNumber;
                    orderNo= RandomUtils.getCurrentTimeMM() + RandomUtils.getFourRandom();
                    if(refNo!=null && !"".equals(refNo)){
                        if(pay==3){
                            PosPayBean payBean=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),userName,orderNo,payMoney+"",refNo,pay+"","支付宝支付","","2",AbDateUtil.getCurrentDate(),rXiaoFei_wx_zfb.orderStauts+"",prefs.getString("enCode"),"","false","0","0");
                            insertData(payBean);
                        }else{
                            PosPayBean payBean=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),userName,orderNo,payMoney+"",refNo,pay+"","微信支付","","2",AbDateUtil.getCurrentDate(),rXiaoFei_wx_zfb.orderStauts+"",prefs.getString("enCode"),"","false","0","0");
                            insertData(payBean);
                        }
                        //直接发布
                        posBean.PayQrCode=rXiaoFei_wx_zfb.primaryAccountNumber;
                        EventBus.getDefault().post(posBean);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    showMsg(e.getLocalizedMessage());
                }
            }
        }).start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(PosBean bean) {
        try {
            if (bean.PayQrCode!=null) {

                CreateQRImageUtils createQRImageUtils=new CreateQRImageUtils();
                iv_pay_code_img.setImageBitmap(createQRImageUtils.createQRImage(bean.PayQrCode, 580, 580));

                //启动计时器 每1秒执行一次runnable
                handler.postDelayed(runnable,2000);

            } else {
                Toast.makeText(GetSinglePayQrCodeActivity.this, "请输入要生成二维码的字符串",
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
            init_params.put(PosConfig.Name_EX + "1053", "CoreApp签购单台头");// 签购单小票台头

            init_params.put(PosConfig.Name_EX + "1100", "cn.weipass.cashier");// 核心APP 包名
            init_params.put(PosConfig.Name_EX + "1101", "com.wangpos.cashiercoreapp.services.CoreAppService");// 核心APP 类名
//            init_params.put(PosConfig.Name_EX + "1600", "这是小票:${myOrderNo}");
            init_params.put(PosConfig.Name_EX + "1600_1", "这是第一联的小票:${1D:${myOrderNo}}");
            init_params.put(PosConfig.Name_EX + "1600_2", "这是第二联的小票:${2D:${myOrderNo}}");

            init_params.put(PosConfig.Name_EX + "1091", "0");// 交易成功，就删除冲正文件 不以小票打印是否成功 为判断点 1.不以小票打印成功为判断点 0.以小票打印成功为判断点
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
                    Log.i("jsonDugInfo", "任务进程开始执行");
                    break;
                }
                case EVENT_Task_end: {
                    showMsg("任务进程执行结束");
                    Log.i("jsonDugInfo", "任务进程执行结束");
                    break;
                }

                case EVENT_Comm_start: {
                    showMsg("开始网络通信");
                    Log.i("jsonDugInfo", "开始网络通信");
                    break;
                }
                case EVENT_Comm_end: {
                    showMsg("网络通信完成");
                    Log.i("jsonDugInfo", "网络通信完成");
                    break;
                }
                case EVENT_DownloadPlugin_start: {
                    showMsg("开始下载插件");
                    Log.i("jsonDugInfo", "开始下载插件");
                    break;
                }
                case EVENT_DownloadPlugin_end: {
                    showMsg("插件下载完成");
                    Log.i("jsonDugInfo", "插件下载完成");
                    break;
                }
                case EVENT_InstallPlugin_start: {
                    showMsg("开始安装插件");
                    Log.i("jsonDugInfo", "开始安装插件");
                    break;
                }
                case EVENT_InstallPlugin_end: {
                    showMsg("插件安装完成");
                    Log.i("jsonDugInfo", "插件安装完成");
                    break;
                }
                case EVENT_RunPlugin_start: {
                    showMsg("开始启动插件");
                    Log.i("jsonDugInfo", "开始启动插件");
                    break;
                }
                case EVENT_RunPlugin_end: {
                    showMsg("插件启动完成");
                    Log.i("jsonDugInfo", "插件启动完成");
                    break;
                }

                default: {
                    showMsg("Event:" + eventID);
                    Log.i("jsonDugInfo", "Event:" + eventID);
                    break;
                }
            }
        }
    }

    private void showMsg(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ed_show_msg.setText(msg);
            }
        });
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
                Toast.makeText(GetSinglePayQrCodeActivity.this, "尚未初始化点阵打印sdk，请稍后再试", Toast.LENGTH_SHORT).show();
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
                latticePrinterBean.setOriginalPrice(DecimalFormatUtils.decimalFormatRound(payMoney));
                latticePrinterBean.setTotalPrice(DecimalFormatUtils.decimalFormatRound(payMoney));
                latticePrinterBean.setDiscountPrice(DecimalFormatUtils.decimalFormatRound(payMoney));
                if(pay==2){
                    latticePrinterBean.setPayTitleName("微信支付");
                }else{
                    latticePrinterBean.setPayTitleName("支付宝支付");
                }

                //订单收银打印
                SingleCashierPrinterTools.printLattice(GetSinglePayQrCodeActivity.this, latticePrinter,latticePrinterBean,0);
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

    public void showPayInfo(){
        try{
            new CBDialogBuilder(GetSinglePayQrCodeActivity.this)
                    .setTouchOutSideCancelable(true)
                    .showCancelButton(true)
                    .setTitle("温馨提示！")
                    .setMessage("订单正在支付中，确定返回取消支付吗？")
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
                                    //清除本单数据
//                                    PosSqliteDatabaseUtils.deleteByNo(mContext,orderNo);
                                    //返回收银台首页
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
