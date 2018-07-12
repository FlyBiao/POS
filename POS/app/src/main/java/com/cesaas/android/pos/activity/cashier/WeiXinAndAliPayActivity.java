package com.cesaas.android.pos.activity.cashier;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.activity.qr.GetOrderPayQrCodeActivity;
import com.cesaas.android.pos.activity.user.LoginActivity;
import com.cesaas.android.pos.base.BaseActivity;
import com.cesaas.android.pos.bean.PayCallbackBean;
import com.cesaas.android.pos.bean.PosBean;
import com.cesaas.android.pos.bean.PosPayLogBean;
import com.cesaas.android.pos.bean.ResultCreatePayBean;
import com.cesaas.android.pos.bean.printer.LatticePrinterBean;
import com.cesaas.android.pos.db.bean.PosPayBean;
import com.cesaas.android.pos.db.bean.ScanPayInfoBean;
import com.cesaas.android.pos.db.pay.PosSqliteDatabaseUtils;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.global.Gloabl;
import com.cesaas.android.pos.global.Urls;
import com.cesaas.android.pos.net.nohttp.HttpListener;
import com.cesaas.android.pos.net.xutils.net.CreatePayNet;
import com.cesaas.android.pos.net.xutils.net.PayFormStoreNet;
import com.cesaas.android.pos.net.xutils.net.PosPayLogNet;
import com.cesaas.android.pos.utils.AbDateUtil;
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

import java.util.HashMap;

import cn.weipass.pos.sdk.IPrint;
import cn.weipass.pos.sdk.LatticePrinter;
import cn.weipass.pos.sdk.impl.WeiposImpl;
import scanner.CaptureActivity;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：微信支付宝扫描支付页面
 * 创建日期：2016/10/26
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class WeiXinAndAliPayActivity extends BaseActivity implements View.OnClickListener{

    private TextView tv_pay_title,tv_pay_name;
    private TextView tv_amount;
    private TextView tv_qrcode_pay;
    private TextView tv_scan_pay;
    private ImageView iv_pay_logo;
    private LinearLayout ll_pay_type_back;

    private int IsPractical;
    private int SaleId;
    private int payType;
    private Integer money;
    private double payMoney;
    private String orderId;
    private String goodsDiscount;
    private String userName;//用户名【营业员】
    private String shopNameNick;//店铺名
    private int cardCategory=100;//卡类型
    private String TraceAudit;
    private String message;

    private PosCore.RXiaoFei_WX_ZFB rXiaoFei_wx_zfb;
    private PosCore pCore;
    private WxAliPosCallBack callBack;
    private EditText tv_show_msg;

    int EVENT_NO_PAPER = 1;
    private final int LOCK_WAIT = 0;
    private final int LOCK_CONTINUE = 1;

    private int REQUEST_CONTACT = 20;
    final int RESULT_CODE = 101;
    private String scanCode;
    private String refNo;

    private PosPayLogBean posPayLogBean;
    private PosPayLogNet posPayLogNet;
    private String strJson;

    private CreatePayNet createPayNet;

    private String queryOrderNo=null;
    private int payOrderStauts=1;//支付订单状态【0:未知,1:订单需要继续查询,用户还未完成支付;2:订单已经关闭;3:订单完成支付】
    private int qrType;//支付二维码类型[1:微信，2：支付宝]

    private Thread wfThread;
    private Thread queryPayThread;
    private Runnable mRunnable;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //停止打印线程
            mHandler.removeCallbacks(mRunnable);
            //3s后执行代码 调用pos打印机
            setLatticePrinter();
            try {
                if(qrType==2){
                    createPayNet=new CreatePayNet(mContext,mActivity,prefs,userName,orderId,payMoney,IsPractical);
                    createPayNet.setData(SaleId+"",payMoney,3,refNo,TraceAudit,"","支付宝支付",refNo,prefs.getString("userShopId"),"",0,cardCategory);
                    getPayListener("",refNo,payMoney,orderId,3,"notFansVip",IsPractical);
                }else if( qrType==1){
                    createPayNet=new CreatePayNet(mContext,mActivity,prefs,userName,orderId,payMoney,IsPractical);
                    createPayNet.setData(SaleId+"",payMoney,2,refNo,TraceAudit,"","微信支付",refNo,prefs.getString("userShopId"),"",0,cardCategory);
                    getPayListener("",refNo,payMoney,orderId,2,"notFansVip",IsPractical);
                }else{
                    if(payType==3){//支付宝回调
                        createPayNet=new CreatePayNet(mContext,mActivity,prefs,userName,orderId,payMoney,IsPractical);
                        createPayNet.setData(SaleId+"",payMoney,3,refNo,TraceAudit,"","支付宝支付",refNo,prefs.getString("userShopId"),"",0,cardCategory);
                        getPayListener("",refNo,payMoney,orderId,3,"notFansVip",IsPractical);
                    }else{//微信回调
                        createPayNet=new CreatePayNet(mContext,mActivity,prefs,userName,orderId,payMoney,IsPractical);
                        createPayNet.setData(SaleId+"",payMoney,2,refNo,TraceAudit,"","微信支付",refNo,prefs.getString("userShopId"),"",0,cardCategory);
                        getPayListener("",refNo,payMoney,orderId,2,"notFansVip",IsPractical);
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    Handler handler=new Handler();
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            if(payOrderStauts==3){//订单完成支付
                //停止计时器
                handler.removeCallbacks(runnable);
                PosSqliteDatabaseUtils.update(mContext,payOrderStauts+"","订单完成支付，正在打印","true",orderId,"1");
                showMsg("订单完成支付，正在打印...");
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

            }else if(payOrderStauts==2){//订单已经关闭
                PosSqliteDatabaseUtils.update(mContext,payOrderStauts+"","订单已经关闭","false",orderId,"0");
                showMsg("订单已经关闭:\n"+refNo);
                //停止计时器
                handler.removeCallbacks(runnable);
            }else if(payOrderStauts==1){//订单需要继续查询
                PosSqliteDatabaseUtils.update(mContext,payOrderStauts+"","订单需要继续查询","false",orderId,"0");
                showMsg("正在查询...");
                //停止计时器
                handler.removeCallbacks(runnable);
                //调用执行查询支付结果
                queryPayResult(queryOrderNo);
                //每隔一秒刷新一次
                handler.postDelayed(runnable, 2000);

            }else{
                PosSqliteDatabaseUtils.update(mContext,payOrderStauts+"","订单未知","false",orderId,"0");
                Log.i("test", "订单未知。");
                showMsg("订单未知。");
                //停止计时器
                handler.removeCallbacks(runnable);
//                exceptionOrderHand();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wei_xin_and_ali_pay);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        Bundle bundle=getIntent().getExtras();
        payType=bundle.getInt("Pay");
        SaleId=bundle.getInt("SaleId");
        payMoney=bundle.getDouble("PayMoney");
        IsPractical=bundle.getInt("IsPractical");
        goodsDiscount=bundle.getString("goodsDiscount");
        orderId=bundle.getString("OrderId");
        shopNameNick=bundle.getString("shopNameNick");
        userName=bundle.getString("userName");

        initView();
        if(payMoney!=0.0){
            tv_amount.setText(DecimalFormatUtils.decimalToFormat(payMoney));
        }
        initWwnAliPosCore();
    }

    public void initView(){
        tv_show_msg= (EditText) findViewById(R.id.tv_show_msg);
        tv_amount= (TextView) findViewById(R.id.tv_amount);
        tv_pay_title= (TextView) findViewById(R.id.tv_pay_title);
        tv_pay_name= (TextView) findViewById(R.id.tv_pay_name);
        tv_qrcode_pay= (TextView) findViewById(R.id.tv_qrcode_pay);
        tv_scan_pay= (TextView) findViewById(R.id.tv_scan_pay);
        iv_pay_logo= (ImageView) findViewById(R.id.iv_pay_logo);
        ll_pay_type_back= (LinearLayout) findViewById(R.id.ll_pay_type_back);

        tv_qrcode_pay.setOnClickListener(this);
        tv_scan_pay.setOnClickListener(this);
        ll_pay_type_back.setOnClickListener(this);

        if(payType==2){
            tv_pay_title.setText("微信支付");
            tv_pay_name.setText("微信支付");
            iv_pay_logo.setBackground(mContext.getResources().getDrawable(R.mipmap.weixin));
        }else{
            tv_pay_title.setText("支付宝");
            tv_pay_name.setText("支付宝支付");
            iv_pay_logo.setBackground(mContext.getResources().getDrawable(R.mipmap.alipay));
        }
    }

    /**
     * 初始化CoreApp连接对象
     *
     * @return
     */
    private void initWwnAliPosCore() {
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
            init_params.put(PosConfig.Name_EX + "1054", "1");// 是否打印小票  1，可以打 0，不打
//            init_params.put(PosConfig.Name_EX + "1087", "0");//是否 关闭插卡 1.开启插卡 0.关闭插卡
            init_params.put(PosConfig.Name_EX + "1089", "1");//是否 需要获取 已开通的支付方式 1.获取 0.不获取
            init_params.put(PosConfig.Name_EX + "1085", "1");//是否支持TC校验，批上送 1，是 0，不是
            init_params.put(PosConfig.Name_EX + "1086", "1");//是否启动 电子签名 1.是 0 不是
//            init_params.put(PosConfig.Name_EX + "1088", "0");//是否 关闭批结算 1.开启批结算 0.关闭批结算
//			init_params.put(PosConfig.Name_EX + "1082", "36");// 主密钥索引

            init_params.put(PosConfig.Name_MerchantName, "coreApp");

            pCore = PosCoreFactory.newInstance(this, init_params);
            callBack = new WxAliPosCallBack(pCore);
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
            case R.id.ll_pay_type_back://返回
                if(payOrderStauts==1){
                    showPayInfo();
                }else{
                    //停止计时器
                    handler.removeCallbacks(runnable);
                    Skip.mBack(mActivity);
                }
                break;
            case R.id.tv_qrcode_pay://二维码支付
                //停止计时器
                handler.removeCallbacks(runnable);

                bundle.putInt("Pay",payType);
                bundle.putDouble("PayMoney",payMoney);
                bundle.putString("OrderNo",orderId);
                bundle.putString("shopNameNick",shopNameNick);
                bundle.putString("userName",userName);
                bundle.putDouble("originalPrice",payMoney);//原价
                bundle.putDouble("discountAfter",payMoney);//折后j价格
                bundle.putInt("SaleId",SaleId);
                bundle.putInt("IsPractical",IsPractical);
                if(goodsDiscount!=null){
                    bundle.putDouble("discount",Double.parseDouble(goodsDiscount));//会员折扣
                }
                Skip.mNextFroData(mActivity, GetOrderPayQrCodeActivity.class,bundle);
                break;
            case R.id.tv_scan_pay://扫描支付
                if(payType==2){//微信扫描
                    Skip.mScanWeiXinPayOrderActivityResult(mActivity, CaptureActivity.class, REQUEST_CONTACT);
                }else{//支付宝扫描
                    Skip.mScanAliPayOrderActivityResult(mActivity, CaptureActivity.class, REQUEST_CONTACT);
                }
                break;
        }
    }

    public void showPayInfo(){
        new CBDialogBuilder(WeiXinAndAliPayActivity.this)
                .setTouchOutSideCancelable(true)
                .showCancelButton(true)
                .setTitle("温馨提示！")
                .setMessage("确定取消本次支付吗？")
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
                                finish();
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
    }

    /**
     * 处理扫描Activity返回的数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode==RESULT_CODE) {

            if(data.getStringExtra("mScanAliPayOrderResult")!=null && data.getStringExtra("mScanAliPayOrderResult").equals("013")){
                scanCode= data.getStringExtra("resultCode");
                if(scanCode!=null){
                    money=(int)(payMoney*100);
                    wfThread=new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                rXiaoFei_wx_zfb = pCore.xiaoFei_WX_ZFB(money+"", scanCode, callBack);
                                qrType= Integer.parseInt(rXiaoFei_wx_zfb.exInfo.get("qrType"));
                                refNo = rXiaoFei_wx_zfb.retrievalReferenceNumber;
//                              payOrderStauts=Integer.parseInt(rXiaoFei_wx_zfb.exInfo.get(Gloabl.orderStauts));
                                queryOrderNo=refNo;

                                posPayLogBean=new PosPayLogBean();
                                posPayLogBean.setOrderId(orderId);//支付订单
                                posPayLogBean.setPayAmount(payMoney);//支付金额
                                posPayLogBean.setTraceAuditNumber(refNo);//支付凭证号
                                if(qrType==2){
                                    posPayLogBean.setPayType(3);//支付类型
                                }else if(qrType==1){
                                    posPayLogBean.setPayType(2);//支付类型
                                }else{
                                    posPayLogBean.setPayType(3);//支付类型
                                }
                                posPayLogBean.setEnCode(prefs.getString("enCode"));//设备EN号
                                posPayLogBean.setRemark("零售单");
                                strJson=gson.toJson(posPayLogBean);

                                if(queryOrderNo!=null && !"".equals(queryOrderNo)){
                                    if(qrType==2){//支付宝
                                        PosPayBean bean1=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),userName,orderId,payMoney+"",refNo,"3","支付宝",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"启动支付宝支付查询！",prefs.getString("enCode"),"","false","1","0");
                                        insertData(bean1);
                                    }else if(qrType==1){
                                        PosPayBean bean1=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),userName,orderId,payMoney+"",refNo,"2","微信支付",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"启动支付宝支付查询！",prefs.getString("enCode"),"","false","1","0");
                                        insertData(bean1);
                                    }else{
                                        PosPayBean bean1=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),userName,orderId,payMoney+"",refNo,"3","支付宝",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"启动支付宝支付查询！",prefs.getString("enCode"),"","false","1","0");
                                        insertData(bean1);
                                    }
                                    //启动计时器 每2秒执行一次runnable
                                    handler.postDelayed(runnable,2000);
                                }else{
                                    if(qrType==2){
                                        PosPayBean bean1=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),userName,orderId,payMoney+"",refNo,"3","支付宝",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"获取不到支付订单号",prefs.getString("enCode"),"","false","1","0");
                                        insertData(bean1);
                                    }else if(qrType==1){
                                        PosPayBean bean1=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),userName,orderId,payMoney+"",refNo,"2","微信支付",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"获取不到支付订单号",prefs.getString("enCode"),"","false","1","0");
                                        insertData(bean1);
                                    }else{
                                        PosPayBean bean1=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),userName,orderId,payMoney+"",refNo,"3","支付宝",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"获取不到支付订单号",prefs.getString("enCode"),"","false","1","0");
                                        insertData(bean1);
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                showMsg(e.getLocalizedMessage());
                                PosSqliteDatabaseUtils.deleteByNo(mContext,orderId);
                            }

                        }
                    });
                    wfThread.start();
                }
                else{
                    ToastUtils.show("请输入支付宝二维码！");
                }
            }

            if(data.getStringExtra("mScanWeiXinPayOrderResult")!=null && data.getStringExtra("mScanWeiXinPayOrderResult").equals("014")){
                scanCode= data.getStringExtra("resultCode");
                if(scanCode!=null){
                    money=(int)(payMoney*100);
                    wfThread= new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                rXiaoFei_wx_zfb = pCore.xiaoFei_WX_ZFB(money+"", scanCode, callBack);
                                qrType=Integer.parseInt(rXiaoFei_wx_zfb.exInfo.get("qrType"));
                                refNo = rXiaoFei_wx_zfb.retrievalReferenceNumber;
                                payOrderStauts=Integer.parseInt(rXiaoFei_wx_zfb.exInfo.get(Gloabl.orderStauts));
                                queryOrderNo=refNo;

                                posPayLogBean=new PosPayLogBean();
                                posPayLogBean.setOrderId(orderId);//支付订单
                                posPayLogBean.setPayAmount(payMoney);//支付金额
                                posPayLogBean.setTraceAuditNumber(refNo);//支付凭证号
                                if(qrType==2){
                                    posPayLogBean.setPayType(3);//支付类型
                                }else if(qrType==1){
                                    posPayLogBean.setPayType(2);//支付类型
                                }else{
                                    posPayLogBean.setPayType(3);//支付类型
                                }
                                posPayLogBean.setEnCode(prefs.getString("enCode"));//设备EN号
                                posPayLogBean.setRemark("零售单");
                                strJson=gson.toJson(posPayLogBean);

                                if(queryOrderNo!=null && !"".equals(queryOrderNo)){
                                    if(qrType==2){
                                        PosPayBean bean1=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),userName,orderId,payMoney+"",refNo,"3","支付宝支付",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"启动微信支付查询！",prefs.getString("enCode"),"","false","1","0");
                                        insertData(bean1);
                                    }else if(qrType==1){
                                        PosPayBean bean1=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),userName,orderId,payMoney+"",refNo,"2","微信支付",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"启动微信支付查询！",prefs.getString("enCode"),"","false","1","0");
                                        insertData(bean1);
                                    }else{
                                        PosPayBean bean1=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),userName,orderId,payMoney+"",refNo,"2","微信支付",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"启动微信支付查询！",prefs.getString("enCode"),"","false","1","0");
                                        insertData(bean1);
                                    }
                                    //启动计时器 每2秒执行一次runnable
                                    handler.postDelayed(runnable,2000);
                                }else{
                                    if(qrType==2){
                                        PosPayBean bean1=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),userName,orderId,payMoney+"",refNo,"3","支付宝",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"获取不到支付订单号",prefs.getString("enCode"),"","false","1","0");
                                        insertData(bean1);
                                    }else if(qrType==1){
                                        PosPayBean bean1=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),userName,orderId,payMoney+"",refNo,"2","微信支付",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"获取不到支付订单号",prefs.getString("enCode"),"","false","1","0");
                                        insertData(bean1);
                                    }else{
                                        PosPayBean bean1=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),userName,orderId,payMoney+"",refNo,"3","支付宝",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"获取不到支付订单号",prefs.getString("enCode"),"","false","1","0");
                                        insertData(bean1);
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                showMsg(e.getLocalizedMessage());
                                PosSqliteDatabaseUtils.deleteByNo(mContext,orderId);
                            }
                        }
                    });
                    wfThread.start();
                }else{
                    ToastUtils.show("请输入微信付款二维码！");
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(ResultCreatePayBean bean) {
        message=bean.Message;
        if(bean.isSuccess()!=true){
            PosSqliteDatabaseUtils.update(mContext,payOrderStauts+"","订单完成支付-创建订单支付成功-创建流水接口失败："+message,"true",orderId,"1");
            try {
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
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            PosSqliteDatabaseUtils.update(mContext,payOrderStauts+"","订单完成支付-创建订单支付成功-创建流水接口成功"+bean.getMessage(),"true",orderId,"1");
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
                PosSqliteDatabaseUtils.update(mContext,payOrderStauts+"","订单完成支付-创建流水成功-创建订单支付成功"+callbackBean.getMessage(),"true",orderId,"1");
                Skip.mNext(mActivity,CashierMainActivity.class,true);
            }else{
                PosSqliteDatabaseUtils.update(mContext,payOrderStauts+"","订单完成支付-创建流水成功-创建订单支付失败："+callbackBean.getMessage(),"true",orderId,"1");
                Log.d("test","支付result:"+callbackBean.getMessage());
                try {
                    //记录pos 支付日志
                    if(payType==1){//积分支付退款回调

                    }else if(payType==2){//微信支付退款回调
                        payLog("微信支付",strJson,callbackBean.getMessage());
                    }else if(payType==3){//支付宝退款回调
                        payLog("支付宝支付",strJson,callbackBean.getMessage());
                    }else if(payType==4){//银联支付退款回调
                        payLog("银联支付",strJson,callbackBean.getMessage());
                    }else if(payType==5){//现金支付退款回调
                        payLog("现金支付",strJson,callbackBean.getMessage());
                    }else{
                        payLog("",strJson,callbackBean.getMessage());
                    }
                }catch (Exception e){
                    e.printStackTrace();
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
        posPayLogNet=new PosPayLogNet(mContext);
        posPayLogNet.setData(LogType,obj,remark);
    }

    @Override
    protected void onDestroy() {
        //取消EventBus订阅
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void showMsg(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_show_msg.setText(msg);
            }
        });
    }

    /**
     * pos支付回调
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

    /**
     * 查询支付结果
     */
    public void queryPayResult(final String refNo){
        if(refNo!=null){
            queryPayThread =new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        PosCore.RXiaoFei_WX_ZFB chaxun_wx_zfb = pCore.chaXun_WX_ZFB(refNo, callBack);
                        payOrderStauts=chaxun_wx_zfb.orderStauts;
                        TraceAudit = chaxun_wx_zfb.exInfo.get(Gloabl.thirdSerialNo);
                        Log.i("test","查询二维码支付状态"+"订单状态:" + chaxun_wx_zfb.orderStauts + "\n" + "第三方订单号:" + chaxun_wx_zfb.exInfo.get(Gloabl.thirdSerialNo)+ "\n" + "凭证号:" + chaxun_wx_zfb.exInfo.get(Gloabl.orderNo));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("test","QUERY_ERROR:"+e.getLocalizedMessage());
                    showMsg(e.getLocalizedMessage());
                    }
                }
            });
            queryPayThread.start();
        }
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
                Toast.makeText(WeiXinAndAliPayActivity.this, "尚未初始化点阵打印sdk，请稍后再试", Toast.LENGTH_SHORT).show();
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
                latticePrinterBean.setOrderId(orderId);
                latticePrinterBean.setTraceAuditNumber(refNo);
                latticePrinterBean.setOriginalPrice(DecimalFormatUtils.decimalFormatRound(payMoney));
                latticePrinterBean.setTotalPrice(DecimalFormatUtils.decimalFormatRound(payMoney));
                latticePrinterBean.setDiscountPrice(DecimalFormatUtils.decimalFormatRound(payMoney));
                if(qrType==1){
                    latticePrinterBean.setPayTitleName("微信支付");
                }else if(qrType==2){
                    latticePrinterBean.setPayTitleName("支付宝支付");
                }else{
                    if(payType==2){
                        latticePrinterBean.setPayTitleName("微信支付");
                    }else{
                        latticePrinterBean.setPayTitleName("支付宝支付");
                    }
                }
                //订单收银打印
                SingleCashierPrinterTools.printLattice(WeiXinAndAliPayActivity.this, latticePrinter,latticePrinterBean,0);
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
}
