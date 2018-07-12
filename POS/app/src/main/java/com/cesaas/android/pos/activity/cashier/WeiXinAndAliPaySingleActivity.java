package com.cesaas.android.pos.activity.cashier;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.cesaas.android.pos.activity.qr.GetOrderPayQrCodeActivity;
import com.cesaas.android.pos.activity.qr.GetSinglePayQrCodeActivity;
import com.cesaas.android.pos.base.BaseActivity;
import com.cesaas.android.pos.bean.PosPayLogBean;
import com.cesaas.android.pos.bean.ResultCreatePayBean;
import com.cesaas.android.pos.bean.printer.LatticePrinterBean;
import com.cesaas.android.pos.db.bean.PosPayBean;
import com.cesaas.android.pos.db.pay.PosSqliteDatabaseUtils;
import com.cesaas.android.pos.global.Gloabl;
import com.cesaas.android.pos.net.xutils.net.CreatePayNet;
import com.cesaas.android.pos.net.xutils.net.PosPayLogNet;
import com.cesaas.android.pos.utils.AbDateUtil;
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
import scanner.CaptureActivity;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：微信支付宝独立收银扫描支付页面
 * 创建日期：2016/10/27
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class WeiXinAndAliPaySingleActivity extends BaseActivity implements View.OnClickListener{

    private TextView tv_single_pay_title,tv_single_pay_name;
    private TextView tv_single_amount;
    private TextView tv_single_qrcode_pay;
    private TextView tv_single_scan_pay;
    private ImageView iv_single_pay_logo;
    private LinearLayout ll_single_pay_type_back;

    private int IsPractical;
    private int payType;
    private Integer money;
    private double payMoney;
    private double discount;
    private String orderNo;
    private String userShopId;
    private int point;
    private double originalPrice;
    private double discountAfter;
    private String mobile;
    private String userName;//用户名【营业员】
    private String shopNameNick;//店铺名
    private boolean isSuccess=false;

    private PosCore.RXiaoFei_WX_ZFB rXiaoFei_wx_zfb;
    private PosCore pCore;
    private WxAliPosCallBack callBack;
    private EditText tv_single_show_msg;

    int EVENT_NO_PAPER = 1;
    private final int LOCK_WAIT = 0;
    private final int LOCK_CONTINUE = 1;

    private int REQUEST_CONTACT = 20;
    final int RESULT_CODE = 101;
    private String scanCode;
    private String refNo;
    private String TraceAudit;
    private String message;

    private PosPayLogBean posPayLogBean;
    private String strJson;
    private int cardCategory=100;

    private CreatePayNet createPayNet;

    private String queryOrderNo=null;
    private int payOrderStauts=1;//支付订单状态【0:未知,1:订单需要继续查询,用户还未完成支付;2:订单已经关闭;3:订单完成支付】
    private int qrType;//支付二维码类型[1:微信，2：支付宝]

    private Runnable mRunnable;
    Handler handler=new Handler();

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //停止打印线程
            mHandler.removeCallbacks(mRunnable);
            //3s后执行代码 调用pos打印机
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
                    PosSqliteDatabaseUtils.update(mContext,payOrderStauts+"","订单完成支付","true",orderNo,"1");
                    if(qrType==2){
                        createPayNet=new CreatePayNet(mContext,mActivity,prefs,userName,orderNo,payMoney,IsPractical);
                        createPayNet.setData(orderNo,payMoney,3,refNo,TraceAudit,"","支付宝支付",refNo,userShopId,"",2,cardCategory);

                    }else if(qrType==1){
                        createPayNet=new CreatePayNet(mContext,mActivity,prefs,userName,orderNo,payMoney,IsPractical);
                        createPayNet.setData(orderNo,payMoney,2,refNo,TraceAudit,"","微信支付",refNo,userShopId,"",2,cardCategory);
                    }else{
                        if(payType==3) {//支付宝回调
                            createPayNet=new CreatePayNet(mContext,mActivity,prefs,userName,orderNo,payMoney,IsPractical);
                            createPayNet.setData(orderNo,payMoney,3,refNo,TraceAudit,"","支付宝支付",refNo,userShopId,"",2,cardCategory);
                        } else {
                            createPayNet=new CreatePayNet(mContext,mActivity,prefs,userName,orderNo,payMoney,IsPractical);
                            createPayNet.setData(orderNo,payMoney,2,refNo,TraceAudit,"","微信支付",refNo,userShopId,"",2,cardCategory);
                        }
                    }
                    setLatticePrinter();
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
                showMsg("订单已经关闭:\n"+refNo);
                //停止计时器
                handler.removeCallbacks(runnable);
            }else if(payOrderStauts==1){//订单需要继续查询
                PosSqliteDatabaseUtils.update(mContext,payOrderStauts+"","订单需要继续查询","false",orderNo,"0");
                //停止计时器
                handler.removeCallbacks(runnable);
                showMsg("正在查询...");
                queryPayResult(queryOrderNo);//调用执行查询支付结果
                //每隔一秒刷新一次
                handler.postDelayed(this, 2000);
            }else{
                //订单未知。
                PosSqliteDatabaseUtils.update(mContext,payOrderStauts+"","订单未知","false",orderNo,"0");
                //停止计时器
                handler.removeCallbacks(runnable);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wei_xin_and_ali_single_pay);
        //通过EventBus订阅事件
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initWwnAliPosCore();

        Bundle bundle=getIntent().getExtras();
        payType=bundle.getInt("Pay");
        payMoney=bundle.getDouble("PayMoney");
        orderNo=bundle.getString("OrderNo");
        discount=bundle.getDouble("discount");
        shopNameNick=bundle.getString("shopNameNick");
        userName=bundle.getString("userName");
        IsPractical=bundle.getInt("IsPractical");
        point=bundle.getInt("point");
        originalPrice=bundle.getDouble("originalPrice");
        discountAfter=bundle.getDouble("discountAfter");
        mobile=bundle.getString("mobile");
        userShopId=bundle.getString("userShopId");
        initView();
        if(payMoney!=0.0){
            tv_single_amount.setText(payMoney+"");
        }else{
            tv_single_amount.setText(0+"");
        }

//        createPayNet=new CreatePayNet(mContext);
//        createPayNet.setData(orderNo,242,4,"100990","10002183052018020100000014","6222083602007416608","银联刷卡","10002183052018020100000014",prefs.getString("userShopId"),"中国工商银行",2,1);
//
//          createPayNet=new CreatePayNet(mContext);
//          createPayNet.setData(orderNo,"2018-02-7 19:25:10",11.0,3,"21221222","21221222","","支付宝支付","21221222",prefs.getString("userShopId"),"",2,cardCategory);
////
//        createPayNet=new CreatePayNet(mContext);
//        createPayNet.setData(orderNo,235.0,2,"10002183052018020100000011","10002183052018020100000011","","微信支付","10002183052018020100000011",prefs.getString("userShopId"),"",2,cardCategory);
//
//        createPayNet=new CreatePayNet(mContext);
//        createPayNet.setData(orderNo,690.0,2,"10002183052018020100000015","10002183052018020100000015","","微信支付","10002183052018020100000015",prefs.getString("userShopId"),"",2,cardCategory);
//
//        createPayNet=new CreatePayNet(mContext);
//        createPayNet.setData(orderNo,700.0,4,"67095","10002518022018020600000010","6282700900145435","银联刷卡","10002518022018020600000010",prefs.getString("userShopId"),"珠海华润银行",2,1);

    }

    /**
     * 接收支付流水结果信息
     * @param bean
     */
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(ResultCreatePayBean bean) {
        message=bean.Message;
        if(bean.isSuccess()!=false){
            PosSqliteDatabaseUtils.update(mContext,payOrderStauts+"","订单完成支付-创建流水成功:"+message,"true",orderNo,"1");
            Skip.mNext(mActivity,CashierMainActivity.class,true);
        }else{
            PosSqliteDatabaseUtils.update(mContext,payOrderStauts+"","订单完成支付-创建流水失败:"+message,"false",orderNo,"0");
            ToastUtils.getToast(mContext,"创建支付流水失败："+message);
            //记录pos 支付日志
            if(payType==1){//积分支付退款回调
            }else if(payType==2){//微信支付回调
                payLog("微信支付",strJson,bean.getMessage());
            }else if(payType==3){//支付宝退款回调
                payLog("支付宝支付",strJson,bean.getMessage());
            }else if(payType==4){//银联支付退款回调
                payLog("银联支付",strJson,bean.getMessage());
            }else if(payType==5){//现金支付退款回调
                payLog("现金支付",strJson,bean.getMessage());
            }
        }
    }

    public void payLog(String LogType, String obj,String remark){
        //Pay Log
        PosPayLogNet posPayLogNet=new PosPayLogNet(mContext);
        posPayLogNet.setData(LogType,obj,remark);
    }

    public void initView(){
        tv_single_show_msg= (EditText) findViewById(R.id.tv_single_show_msg);
        tv_single_amount= (TextView) findViewById(R.id.tv_single_amount);
        tv_single_pay_title= (TextView) findViewById(R.id.tv_single_pay_title);
        tv_single_pay_name= (TextView) findViewById(R.id.tv_single_pay_name);
        tv_single_qrcode_pay= (TextView) findViewById(R.id.tv_single_qrcode_pay);
        tv_single_scan_pay= (TextView) findViewById(R.id.tv_single_scan_pay);
        iv_single_pay_logo= (ImageView) findViewById(R.id.iv_single_pay_logo);
        ll_single_pay_type_back= (LinearLayout) findViewById(R.id.ll_single_pay_type_back);

        tv_single_qrcode_pay.setOnClickListener(this);
        tv_single_scan_pay.setOnClickListener(this);
        ll_single_pay_type_back.setOnClickListener(this);

        if(payType==2){
            tv_single_pay_title.setText("微信支付");
            tv_single_pay_name.setText("微信支付");
            iv_single_pay_logo.setBackground(mContext.getResources().getDrawable(R.mipmap.weixin));
        }else{
            tv_single_pay_title.setText("支付宝");
            tv_single_pay_name.setText("支付宝支付");
            iv_single_pay_logo.setBackground(mContext.getResources().getDrawable(R.mipmap.alipay));
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
            case R.id.ll_single_pay_type_back://返回
                //停止计时器
                handler.removeCallbacks(runnable);
                showPayInfo();
                break;
            case R.id.tv_single_qrcode_pay://二维码支付
                    //停止计时器
                    handler.removeCallbacks(runnable);
                    bundle.putInt("Pay",payType);//移动支付类型
                    bundle.putDouble("PayMoney",payMoney);//支付金额
                    bundle.putString("OrderNo",orderNo);//订单号
                    bundle.putString("WxAliTraceAuditNumber",refNo);//凭证号
                    bundle.putDouble("discount",discount);//会员折扣
                    bundle.putString("shopNameNick",shopNameNick);//店铺名称
                    bundle.putString("userName",userName);//营业员
                    bundle.putInt("point",point);//积分
                    bundle.putDouble("originalPrice",originalPrice);//原价
                    bundle.putDouble("discountAfter",discountAfter);//折后j价格
                    bundle.putString("mobile",mobile);//会员手机号
                    bundle.putBoolean("isSuccess",isSuccess);//
                    Skip.mNextFroData(mActivity, GetSinglePayQrCodeActivity.class,bundle);
                break;
            case R.id.tv_single_scan_pay://扫描支付
                if(payType==2){//微信扫描
                    Skip.mScanWeiXinPayOrderActivityResult(mActivity, CaptureActivity.class, REQUEST_CONTACT);
                }else{//支付宝扫描
                    Skip.mScanAliPayOrderActivityResult(mActivity, CaptureActivity.class, REQUEST_CONTACT);
                }
                break;
        }
    }

    /**
     * 处理扫描Activity返回的数据
     */
    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {

        if(resultCode==RESULT_CODE) {

            if(data.getStringExtra("mScanAliPayOrderResult")!=null && data.getStringExtra("mScanAliPayOrderResult").equals("013")){
                scanCode= data.getStringExtra("resultCode");
                if(scanCode!=null){
                    money=(int)(payMoney*100);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                rXiaoFei_wx_zfb = pCore.xiaoFei_WX_ZFB(money+"", scanCode, callBack);
                                qrType=Integer.parseInt(rXiaoFei_wx_zfb.exInfo.get("qrType"));
                                refNo = rXiaoFei_wx_zfb.retrievalReferenceNumber;
                                TraceAudit = rXiaoFei_wx_zfb.exInfo.get(Gloabl.thirdSerialNo);
                                orderNo=RandomUtils.getCurrentTimeMM() + RandomUtils.getFourRandom();
                                payOrderStauts=Integer.parseInt(rXiaoFei_wx_zfb.exInfo.get(Gloabl.orderStauts));
                                queryOrderNo=refNo;
                                posPayLogBean=new PosPayLogBean();
                                posPayLogBean.setOrderId(orderNo);//支付订单
                                posPayLogBean.setPayAmount(payMoney);//支付金额
                                posPayLogBean.setTraceAuditNumber(refNo);//支付凭证号
                                if(qrType==2){
                                    posPayLogBean.setPayType(3);//支付宝
                                }else if(qrType==1){
                                    posPayLogBean.setPayType(2);//微信
                                }else{
                                    posPayLogBean.setPayType(3);//支付宝
                                }
                                posPayLogBean.setRemark("独立收银");
                                posPayLogBean.setEnCode(prefs.getString("enCode"));//设备EN号
                                strJson=gson.toJson(posPayLogBean);

                                if(queryOrderNo!=null && !"".equals(queryOrderNo)){
                                    if(qrType==2){//支付宝
                                        PosPayBean posPayBean=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),userName,orderNo,payMoney+"",queryOrderNo,"3","支付宝",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"启动支付查询！",prefs.getString("enCode"),"","false","0","0");
                                        insertData(posPayBean);
                                    }else if(qrType==1){//微信
                                        PosPayBean posPayBean=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),userName,orderNo,payMoney+"",queryOrderNo,"2","微信支付",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"启动支付查询！",prefs.getString("enCode"),"","false","0","0");
                                        insertData(posPayBean);
                                    }else{
                                        PosPayBean posPayBean=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),userName,orderNo,payMoney+"",queryOrderNo,"3","支付宝",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"启动支付查询！",prefs.getString("enCode"),"","false","0","0");
                                        insertData(posPayBean);
                                    }
                                }else{
                                    if(qrType==2){
                                        PosPayBean bean1=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),userName,orderNo,payMoney+"",queryOrderNo,"3","支付宝",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"获取不到支付订单号",prefs.getString("enCode"),"","false","0","0");
                                        insertData(bean1);
                                    }else if(qrType==1){
                                        PosPayBean bean1=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),userName,orderNo,payMoney+"",queryOrderNo,"2","微信支付",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"获取不到支付订单号",prefs.getString("enCode"),"","false","0","0");
                                        insertData(bean1);
                                    }else{
                                        PosPayBean bean1=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),userName,orderNo,payMoney+"",queryOrderNo,"3","支付宝",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"获取不到支付订单号",prefs.getString("enCode"),"","false","0","0");
                                        insertData(bean1);
                                    }
                                }
                                handler.postDelayed(runnable,2000);

                            } catch (Exception e) {
                                e.printStackTrace();
                                showMsg(e.getLocalizedMessage());
                                PosSqliteDatabaseUtils.deleteByNo(mContext,orderNo);
                            }
                        }
                    }).start();
                }
                else{
                    ToastUtils.show("请输入支付宝二维码！");
                }
            }

            if(data.getStringExtra("mScanWeiXinPayOrderResult")!=null && data.getStringExtra("mScanWeiXinPayOrderResult").equals("014")){
                scanCode= data.getStringExtra("resultCode");
                if(scanCode!=null){
                    money=(int)(payMoney*100);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                rXiaoFei_wx_zfb = pCore.xiaoFei_WX_ZFB(money+"", scanCode, callBack);
                                qrType=Integer.parseInt(rXiaoFei_wx_zfb.exInfo.get("qrType"));
                                refNo = rXiaoFei_wx_zfb.retrievalReferenceNumber;
                                TraceAudit = rXiaoFei_wx_zfb.exInfo.get(Gloabl.thirdSerialNo);
                                orderNo=RandomUtils.getCurrentTimeMM() + RandomUtils.getFourRandom();
                                payOrderStauts=Integer.parseInt(rXiaoFei_wx_zfb.exInfo.get(Gloabl.orderStauts));
                                queryOrderNo=refNo;
                                posPayLogBean=new PosPayLogBean();
                                posPayLogBean.setOrderId(orderNo);//支付订单
                                posPayLogBean.setPayAmount(payMoney);//支付金额
                                posPayLogBean.setTraceAuditNumber(queryOrderNo);//支付凭证号
                                if(qrType==2){//支付宝
                                    posPayLogBean.setPayType(3);//支付类型
                                }else if(qrType==1){//微信
                                    posPayLogBean.setPayType(2);//支付类型
                                }else{
                                    posPayLogBean.setPayType(2);//支付类型
                                }
                                posPayLogBean.setRemark("独立收银");
                                posPayLogBean.setEnCode(prefs.getString("enCode"));//设备EN号
                                strJson=gson.toJson(posPayLogBean);

                                if(queryOrderNo!=null && !"".equals(queryOrderNo)){
                                    if(qrType==2){//支付宝
                                        PosPayBean posPayBean=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),userName,orderNo,payMoney+"",queryOrderNo,"3","支付宝支付",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"启动支付查询！",prefs.getString("enCode"),"","false","0","0");
                                        insertData(posPayBean);
                                    }else if(qrType==1){//微信
                                        PosPayBean posPayBean=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),userName,orderNo,payMoney+"",queryOrderNo,"2","微信支付",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"启动支付查询！",prefs.getString("enCode"),"","false","0","0");
                                        insertData(posPayBean);
                                    }else{
                                        PosPayBean posPayBean=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),userName,orderNo,payMoney+"",queryOrderNo,"2","微信支付",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"启动支付查询！",prefs.getString("enCode"),"","false","0","0");
                                        insertData(posPayBean);
                                    }

                                    //启动计时器 每1秒执行一次runnable
                                    handler.postDelayed(runnable,2000);
                                }else{
                                    if(qrType==2){
                                        PosPayBean bean1=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),userName,orderNo,payMoney+"",queryOrderNo,"3","支付宝",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"获取不到支付订单号",prefs.getString("enCode"),"","false","0","0");
                                        insertData(bean1);
                                    }else if(qrType==1){
                                        PosPayBean bean1=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),userName,orderNo,payMoney+"",queryOrderNo,"2","微信支付",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"获取不到支付订单号",prefs.getString("enCode"),"","false","0","0");
                                        insertData(bean1);
                                    }else{
                                        PosPayBean bean1=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),userName,orderNo,payMoney+"",queryOrderNo,"3","支付宝",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"获取不到支付订单号",prefs.getString("enCode"),"","false","0","0");
                                        insertData(bean1);
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                showMsg(e.getLocalizedMessage());
                                PosSqliteDatabaseUtils.deleteByNo(mContext,orderNo);
                            }
                        }
                    }).start();
                }else{
                    ToastUtils.show("请输入微信付款二维码！");
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 查询支付结果
     */
    public void queryPayResult(final String refNo){
        if(refNo!=null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        PosCore.RXiaoFei_WX_ZFB chaxun_wx_zfb = pCore.chaXun_WX_ZFB(refNo, callBack);
                        payOrderStauts=chaxun_wx_zfb.orderStauts;
                        TraceAudit = chaxun_wx_zfb.retrievalReferenceNumber;

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("jsonDugInfo","QUERY_ERROR:"+e.getLocalizedMessage());
//                    showMsg(e.getLocalizedMessage());
                    }
                }
            }).start();
        }else{
            ToastUtils.getLongToast(mContext,"未获取二维码订单号！");
        }
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
                tv_single_show_msg.setText(msg);
            }
        });
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
            init_params.put(PosConfig.Name_EX + "1053", "CoreApp签购单台头");// 签购单小票台头

            init_params.put(PosConfig.Name_EX + "1100", "cn.weipass.cashier");// 核心APP 包名
            init_params.put(PosConfig.Name_EX + "1101", "com.wangpos.cashiercoreapp.services.CoreAppService");// 核心APP 类名
//            init_params.put(PosConfig.Name_EX + "1600", "这是小票:${myOrderNo}");
            init_params.put(PosConfig.Name_EX + "1600_1", "这是第一联的小票:${1D:${myOrderNo}}");
            init_params.put(PosConfig.Name_EX + "1600_2", "这是第二联的小票:${2D:${myOrderNo}}");

            init_params.put(PosConfig.Name_EX + "1091", "0");// 交易成功，就删除冲正文件 不以小票打印是否成功 为判断点 1.不以小票打印成功为判断点 0.以小票打印成功为判断点
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
                Toast.makeText(WeiXinAndAliPaySingleActivity.this, "尚未初始化点阵打印sdk，请稍后再试", Toast.LENGTH_SHORT).show();
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
                SingleCashierPrinterTools.printLattice(WeiXinAndAliPaySingleActivity.this, latticePrinter,latticePrinterBean,0);
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
            new CBDialogBuilder(WeiXinAndAliPaySingleActivity.this)
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
