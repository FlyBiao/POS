package com.cesaas.android.pos.storedvalue.ui;

import android.app.AlertDialog;
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
import com.cesaas.android.pos.base.BaseActivity;
import com.cesaas.android.pos.bean.PosPayLogBean;
import com.cesaas.android.pos.bean.ResultCreatePayBean;
import com.cesaas.android.pos.db.bean.PosPayBean;
import com.cesaas.android.pos.db.pay.PosSqliteDatabaseUtils;
import com.cesaas.android.pos.global.Gloabl;
import com.cesaas.android.pos.net.xutils.net.CreatePayNet;
import com.cesaas.android.pos.net.xutils.net.PosPayLogNet;
import com.cesaas.android.pos.net.xutils.net.value.AddTradeNet;
import com.cesaas.android.pos.storedvalue.bean.ResultAddTradeBean;
import com.cesaas.android.pos.storedvalue.bean.StoredValueLatticePrinterBean;
import com.cesaas.android.pos.storedvalue.bean.StoredValueLatticePrinterInfoBean;
import com.cesaas.android.pos.utils.AbDateUtil;
import com.cesaas.android.pos.utils.SingleCashierPrinterTools;
import com.cesaas.android.pos.utils.Skip;
import com.cesaas.android.pos.utils.ToastUtils;
import com.wangpos.pay.UnionPay.PosConfig;
import com.wangpos.poscore.IPosCallBack;
import com.wangpos.poscore.PosCore;
import com.wangpos.poscore.impl.PosCoreFactory;

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
public class WeiXinAliPayActivity extends BaseActivity implements View.OnClickListener{

    private TextView tv_pay_title,tv_pay_name;
    private TextView tv_amount;
    private TextView tv_qrcode_pay;
    private TextView tv_scan_pay;
    private ImageView iv_pay_logo;
    private LinearLayout ll_pay_type_back;

    private int payType;
    private Integer money;
    private double payMoney;
    private String orderId;
    private String shopClerkName;//用户名【营业员】
    private String shopNameNick;//店铺名
    private String title;
    private String vipMobile;
    private String vipId;
    private int shopId;
    private int id;//规则id
    private String traceAuditNumber;//凭证号

    private double DonationAmount;
    private double UsableBalance;//可用余额
    private double GivenBalance;//赠送余额
    private double TotalBalance;//总余额

    private PosCallBack callBacks;
    private PosCore pCore;
    private EditText tv_show_msg;

    private PosPayLogBean posPayLogBean;
    private String strJson;

    private int REQUEST_CONTACT = 20;
    final int RESULT_CODE = 101;
    private String scanCode;
    private String refNo;
    private int qrType;//支付二维码类型[1:微信，2：支付宝]
    private int cardCategory=100;
    private int IsPractical;
    private int shopClerkId;

    private String queryOrderNo=null;
    private String TraceAudit;

    private AddTradeNet addTradeNet;
    private CreatePayNet createPayNet;

    //打印===========
    private LatticePrinter latticePrinter;// 点阵打印
    private StoredValueLatticePrinterBean latticePrinterBean;

    private int payOrderStauts=1;//支付订单状态【0:未知,1:订单需要继续查询,用户还未完成支付;2:订单已经关闭;3:订单完成支付】

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
                    PosSqliteDatabaseUtils.update(mContext,payOrderStauts+"","订单完成支付","true",orderId,"1");
                    createPayNet=new CreatePayNet(mContext,mActivity,prefs,shopClerkName,orderId,payMoney,IsPractical);
                    addTradeNet=new AddTradeNet(mContext);
                    if(qrType==2){
                        PosSqliteDatabaseUtils.update(mContext,payOrderStauts+"","订单完成支付-开始调用充值接口","true",orderId,"1");
                        addTradeNet.setData(Integer.parseInt(vipId),shopId,refNo,3,payMoney,id,prefs.getString("enCode"),shopClerkId);
                        createPayNet.setData(orderId,payMoney,3,refNo,TraceAudit,"","支付宝支付",refNo,shopId+"","",1,cardCategory);
                    }else if(qrType==1){
                        PosSqliteDatabaseUtils.update(mContext,payOrderStauts+"","订单完成支付-开始调用充值接口","true",orderId,"1");
                        addTradeNet.setData(Integer.parseInt(vipId),shopId,refNo,2,payMoney,id,prefs.getString("enCode"),shopClerkId);
                        createPayNet.setData(shopId+"",payMoney,2,refNo,TraceAudit,"","微信支付",refNo,shopId+"","",1,cardCategory);
                    }else{
                        PosSqliteDatabaseUtils.update(mContext,payOrderStauts+"","订单完成支付-开始调用充值接口","true",orderId,"1");
                        addTradeNet.setData(Integer.parseInt(vipId),shopId,refNo,payType,payMoney,id,prefs.getString("enCode"),shopClerkId);
                        if (payType==2){
                            createPayNet.setData(shopId+"",payMoney,2,refNo,TraceAudit,"","微信支付",refNo,shopId+"","",1,cardCategory);
                        }else{
                            createPayNet.setData(orderId,payMoney,3,refNo,TraceAudit,"","支付宝支付",refNo,shopId+"","",1,cardCategory);
                        }
                    }

                    setLatticePrinter();
                    mRunnable= new Runnable() {
                        @Override
                        public void run() {
                            mHandler.sendEmptyMessage(1);
                        }
                    };
                    mHandler .postDelayed(mRunnable, 3000);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }else if(payOrderStauts==2){//订单已经关闭
                PosSqliteDatabaseUtils.update(mContext,payOrderStauts+"","订单已经关闭","false",orderId,"0");
                showMsg("订单已经关闭:\n"+refNo);
                //停止计时器
                handler.removeCallbacks(runnable);
            }else if(payOrderStauts==1){//订单需要继续查询
                PosSqliteDatabaseUtils.update(mContext,payOrderStauts+"","订单需要继续查询","false",orderId,"0");
                //停止计时器
                handler.removeCallbacks(runnable);
                showMsg("正在查询...");
                queryPayResult(queryOrderNo);//调用执行查询支付结果
                //每隔一秒刷新一次
                handler.postDelayed(this, 2000);
            }else{
                //订单未知。
                PosSqliteDatabaseUtils.update(mContext,payOrderStauts+"","订单未知","false",orderId,"0");
                //停止计时器
                handler.removeCallbacks(runnable);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wei_xin_and_ali_pay);

        //EventBus订阅事件
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        addTradeNet=new AddTradeNet(mContext);

        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            title=bundle.getString("title");
            vipMobile=bundle.getString("vipMobile");
            payType=bundle.getInt("payType");
            payMoney=bundle.getDouble("Amount");
            orderId=bundle.getString("OrderNo");
            IsPractical=bundle.getInt("IsPractical");
            shopNameNick=bundle.getString("shopNameNick");
            shopClerkName=bundle.getString("userName");
            shopId=bundle.getInt("shopId");
            shopClerkId=bundle.getInt("shopClerkId");
            vipId=bundle.getString("vipId");
            id=bundle.getInt("id");

            DonationAmount=bundle.getDouble("DonationAmount");
            TotalBalance=bundle.getDouble("TotalBalance");
            UsableBalance=bundle.getDouble("UsableBalance");
            GivenBalance=bundle.getDouble("GivenBalance");
        }
        initWwnAliPosCore();
        initView();
    }

    //接收后台接口支付成功消息
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(ResultAddTradeBean bean) {
        if(bean.isSuccess()!=false){
            PosSqliteDatabaseUtils.update(mContext,payOrderStauts+"","订单完成支付-调用充值接口成功","true",orderId,"1");
            //跳回会员首页
            bundle.putString("vipMobile",vipMobile);
            Skip.mNextFroData(mActivity,SummaryActivity.class,bundle,true);
        }else{
            PosSqliteDatabaseUtils.update(mContext,payOrderStauts+"","订单完成支付-调用充值接口失败："+bean.getMessage(),"true",orderId,"1");
            ToastUtils.getLongToast(mContext,"Message:"+bean.getMessage());
        }
    }

    /**
     * 接收支付流水结果信息
     * @param bean
     */
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(ResultCreatePayBean bean) {
        if(bean.isSuccess()!=false){
            PosSqliteDatabaseUtils.update(mContext,payOrderStauts+"","订单完成支付-调用支付流水接口成功","true",orderId,"1");
        }else{
            PosSqliteDatabaseUtils.update(mContext,payOrderStauts+"","订单完成支付-调用支付流水接口失败："+bean.getMessage(),"true",orderId,"1");
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

        if(payMoney!=0.0){
            tv_amount.setText(payMoney+"");
        }

        if(payType==2){
            tv_pay_title.setText("微信支付");
            tv_pay_name.setText("微信支付");
            iv_pay_logo.setImageResource(R.mipmap.weixin);
        }else if(payType==3){
            tv_pay_title.setText("支付宝");
            tv_pay_name.setText("支付宝支付");
            iv_pay_logo.setImageResource(R.mipmap.alipay);
        }
    }

    /**
     * 初始化CoreApp连接对象
     *
     * @return
     */
    private void initWwnAliPosCore() {
        if (pCore == null) {
            // 配置数据为开发阶段的数据
            HashMap<String, String> init_params = new HashMap<>();
            init_params.put(PosConfig.Name_EX + "1053", shopNameNick);// 签购单小票台头

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

            init_params.put(PosConfig.Name_MerchantName, shopNameNick);
            pCore = PosCoreFactory.newInstance(this, init_params);

            callBacks=new PosCallBack(pCore);
        }

    }

    /**
     * 设置收银点阵打印方法
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

            latticePrinterBean=new StoredValueLatticePrinterBean();
            if(prefs.getString("ShopAddress")!=null){
                latticePrinterBean.setCounterName(prefs.getString("ShopAddress"));
                latticePrinterBean.setShopName(prefs.getString("ShopArea")+prefs.getString("shopNameNick"));
            }else{
                latticePrinterBean.setCounterName(prefs.getString("shopNameNick"));
                latticePrinterBean.setShopName(prefs.getString("shopNameNick"));
            }
            latticePrinterBean.setShopClerkName(shopClerkName);
            latticePrinterBean.setTitle(title);
            latticePrinterBean.setAmount(payMoney);
            latticePrinterBean.setOrderId(orderId);
            latticePrinterBean.setTraceAuditNumber(refNo);
            latticePrinterBean.setVipId(vipMobile);
            latticePrinterBean.setGivenAmount(DonationAmount);
            latticePrinterBean.setUsableBalance(UsableBalance+latticePrinterBean.getAmount());
            latticePrinterBean.setGivenBalance(GivenBalance+latticePrinterBean.getGivenAmount());
            latticePrinterBean.setTotalBalance(latticePrinterBean.getGivenBalance()+latticePrinterBean.getUsableBalance());

            if(payType==2){
                latticePrinterBean.setPayType("微信支付");
            }else if(payType==3){
                latticePrinterBean.setPayType("支付宝");
            }else if(payType==4){
                latticePrinterBean.setPayType("银联刷卡");
            }else{
                latticePrinterBean.setPayType("现金");
            }

            StoredValueLatticePrinterInfoBean.printLattice(latticePrinter,latticePrinterBean);
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
                //停止计时器
                handler.removeCallbacks(runnable);
                Skip.mBack(mActivity);
                break;
            case R.id.tv_qrcode_pay://二维码支付
                bundle.putString("title",title);
                bundle.putString("vipMobile",vipMobile);
                bundle.putInt("payType",payType);
                bundle.putDouble("PayMoney",payMoney);
                bundle.putString("OrderNo",orderId);
                bundle.putString("shopNameNick",shopNameNick);
                bundle.putString("userName",prefs.getString("userName"));
                bundle.putInt("shopId",shopId);
                bundle.putString("vipId",vipId);
                bundle.putInt("id",id);
                bundle.putDouble("DonationAmount",DonationAmount);
                bundle.putDouble("TotalBalance",TotalBalance);
                bundle.putDouble("UsableBalance",UsableBalance);
                bundle.putDouble("GivenBalance",GivenBalance);

                Skip.mNextFroData(mActivity, StoredValuePayQrCodeActivity.class,bundle);
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

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                PosCore.RXiaoFei_WX_ZFB rXiaoFei_wx_zfb = pCore.xiaoFei_WX_ZFB(money+"", scanCode, callBacks);
                                refNo = rXiaoFei_wx_zfb.retrievalReferenceNumber;
                                qrType=Integer.parseInt(rXiaoFei_wx_zfb.exInfo.get("qrType"));
                                payOrderStauts=Integer.parseInt(rXiaoFei_wx_zfb.exInfo.get(Gloabl.orderStauts));
                                queryOrderNo=refNo;
                                posPayLogBean=new PosPayLogBean();
                                posPayLogBean.setOrderId(orderId);//支付订单
                                posPayLogBean.setPayAmount(payMoney);//支付金额
                                posPayLogBean.setTraceAuditNumber(refNo);//支付凭证号
                                if(qrType==2){
                                    posPayLogBean.setPayType(3);//支付宝
                                }else if(qrType==1){
                                    posPayLogBean.setPayType(2);//微信
                                }else{
                                    posPayLogBean.setPayType(3);//支付宝
                                }
                                posPayLogBean.setRemark("会员充值");
                                posPayLogBean.setEnCode(prefs.getString("enCode"));//设备EN号
                                strJson=gson.toJson(posPayLogBean);
                                if(queryOrderNo!=null && !"".equals(queryOrderNo)){
                                    if(qrType==2){//支付宝
                                        PosPayBean posPayBean=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),shopClerkName,orderId,payMoney+"",queryOrderNo,"3","支付宝支付",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"启动支付查询！",prefs.getString("enCode"),"","false","2","0");
                                        insertData(posPayBean);
                                    }else if(qrType==1){//微信
                                        PosPayBean posPayBean=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),shopClerkName,orderId,payMoney+"",queryOrderNo,"2","微信支付",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"启动支付查询！",prefs.getString("enCode"),"","false","2","0");
                                        insertData(posPayBean);
                                    }else{
                                        PosPayBean posPayBean=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),shopClerkName,orderId,payMoney+"",queryOrderNo,"2","微信支付",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"启动支付查询！",prefs.getString("enCode"),"","false","2","0");
                                        insertData(posPayBean);
                                    }

                                    //启动计时器 每1秒执行一次runnable
                                    handler.postDelayed(runnable,2000);
                                }else{
                                    if(qrType==2){
                                        PosPayBean bean1=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),shopClerkName,orderId,payMoney+"",queryOrderNo,"3","支付宝",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"获取不到支付订单号",prefs.getString("enCode"),"","false","2","0");
                                        insertData(bean1);
                                    }else if(qrType==1){
                                        PosPayBean bean1=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),shopClerkName,orderId,payMoney+"",queryOrderNo,"2","微信支付",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"获取不到支付订单号",prefs.getString("enCode"),"","false","2","0");
                                        insertData(bean1);
                                    }else{
                                        PosPayBean bean1=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),shopClerkName,orderId,payMoney+"",queryOrderNo,"3","支付宝",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"获取不到支付订单号",prefs.getString("enCode"),"","false","2","0");
                                        insertData(bean1);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                showMsg(e.getLocalizedMessage());
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
                                PosCore.RXiaoFei_WX_ZFB rXiaoFei_wx_zfb = pCore.xiaoFei_WX_ZFB(money+"", scanCode, callBacks);
                                refNo = rXiaoFei_wx_zfb.retrievalReferenceNumber;
                                qrType=Integer.parseInt(rXiaoFei_wx_zfb.exInfo.get("qrType"));
                                payOrderStauts=Integer.parseInt(rXiaoFei_wx_zfb.exInfo.get(Gloabl.orderStauts));
                                queryOrderNo=refNo;
                                posPayLogBean=new PosPayLogBean();
                                posPayLogBean.setOrderId(orderId);//支付订单
                                posPayLogBean.setPayAmount(payMoney);//支付金额
                                posPayLogBean.setTraceAuditNumber(refNo);//支付凭证号
                                if(qrType==2){
                                    posPayLogBean.setPayType(3);//支付宝
                                }else if(qrType==1){
                                    posPayLogBean.setPayType(2);//微信
                                }else{
                                    posPayLogBean.setPayType(3);//支付宝
                                }
                                posPayLogBean.setRemark("会员充值");
                                posPayLogBean.setEnCode(prefs.getString("enCode"));//设备EN号
                                strJson=gson.toJson(posPayLogBean);

                                if(queryOrderNo!=null && !"".equals(queryOrderNo)){
                                    if(qrType==2){//支付宝
                                        PosPayBean posPayBean=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),shopClerkName,orderId,payMoney+"",queryOrderNo,"3","支付宝支付",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"启动支付查询！",prefs.getString("enCode"),"","false","2","0");
                                        insertData(posPayBean);
                                    }else if(qrType==1){//微信
                                        PosPayBean posPayBean=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),shopClerkName,orderId,payMoney+"",queryOrderNo,"2","微信支付",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"启动支付查询！",prefs.getString("enCode"),"","false","2","0");
                                        insertData(posPayBean);
                                    }else{
                                        PosPayBean posPayBean=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),shopClerkName,orderId,payMoney+"",queryOrderNo,"2","微信支付",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"启动支付查询！",prefs.getString("enCode"),"","false","2","0");
                                        insertData(posPayBean);
                                    }

                                    //启动计时器 每1秒执行一次runnable
                                    handler.postDelayed(runnable,2000);
                                }else{
                                    if(qrType==2){
                                        PosPayBean bean1=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),shopClerkName,orderId,payMoney+"",queryOrderNo,"3","支付宝",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"获取不到支付订单号",prefs.getString("enCode"),"","false","2","0");
                                        insertData(bean1);
                                    }else if(qrType==1){
                                        PosPayBean bean1=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),shopClerkName,orderId,payMoney+"",queryOrderNo,"2","微信支付",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"获取不到支付订单号",prefs.getString("enCode"),"","false","2","0");
                                        insertData(bean1);
                                    }else{
                                        PosPayBean bean1=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),shopClerkName,orderId,payMoney+"",queryOrderNo,"3","支付宝",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"获取不到支付订单号",prefs.getString("enCode"),"","false","2","0");
                                        insertData(bean1);
                                    }
                                }

                            } catch (Exception e) {
                                showMsg(e.getLocalizedMessage());
                                e.printStackTrace();
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

    private void insertData(PosPayBean bean){
        PosSqliteDatabaseUtils.insterData(mContext,bean);
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
                        PosCore.RXiaoFei_WX_ZFB chaxun_wx_zfb = pCore.chaXun_WX_ZFB(refNo, callBacks);
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

    public void showMsg(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_show_msg.setText(msg);
            }
        });
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
}
