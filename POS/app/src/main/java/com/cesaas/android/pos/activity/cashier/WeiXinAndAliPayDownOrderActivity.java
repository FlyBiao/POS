package com.cesaas.android.pos.activity.cashier;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.activity.qr.GetOrderPayQrCodeActivity;
import com.cesaas.android.pos.base.BaseActivity;
import com.cesaas.android.pos.bean.PayCallbackBean;
import com.cesaas.android.pos.bean.PosBean;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.global.Gloabl;
import com.cesaas.android.pos.global.Urls;
import com.cesaas.android.pos.net.nohttp.HttpListener;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import scanner.CaptureActivity;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：微信支付宝下单支付页面
 * 创建日期：2016/10/26
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class WeiXinAndAliPayDownOrderActivity extends BaseActivity implements View.OnClickListener{
    private TextView tv_pay_title,tv_pay_name;
    private TextView tv_amount;
    private TextView tv_qrcode_pay;
    private TextView tv_scan_pay;
    private ImageView iv_pay_logo;
    private LinearLayout ll_pay_type_back;

    private int pay;
    private Integer money;
    private double payMoney;
    private String orderId;
    private int orderStatus;
    private String userName;//用户名【营业员】
    private String shopNameNick;//店铺名
    private boolean isSuccess=false;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wei_xin_and_ali_pay);

        //        //通过EventBus订阅事件
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        initWwnAliPosCore();

        Bundle bundle=getIntent().getExtras();
        pay=bundle.getInt("Pay");
        payMoney=bundle.getDouble("PayMoney");
        orderStatus=bundle.getInt("OrderStatus");
        orderId=bundle.getString("OrderId");
        shopNameNick=bundle.getString("shopNameNick");
        userName=bundle.getString("userName");
        initView();

        if(payMoney!=0.0){
            tv_amount.setText(payMoney+"");
        }
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

        if(pay==2){
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

            //浙江银联插件
//            init_params.put(PosConfig.Name_EX + "1080", "com.wangpos.cashier.plug.sdk3test");// 插件包名
//            init_params.put(PosConfig.Name_EX + "1081", "com.wangpos.cashier.plug.sdk3test.PlugService");// 插件启动类
//            init_params.put(PosConfig.Name_TerminalNo, "05315812");
//            init_params.put(PosConfig.Name_MerchantNo, "898330160120021");
//            init_params.put(PosConfig.Name_MainKey, "89B56BBA679226CBDAC7ABEA520DF267");
//            init_params.put(PosConfig.Name_Server, "60.191.127.168:3999");

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
                Skip.mBack(mActivity);
                break;
            case R.id.tv_qrcode_pay://二维码支付
                bundle.putInt("Pay",pay);
                bundle.putDouble("PayMoney",payMoney);
                bundle.putString("OrderNo",orderId);
                Skip.mNextFroData(mActivity, GetOrderPayQrCodeActivity.class,bundle);
                break;
            case R.id.tv_scan_pay://扫描支付

                if(pay==2){//微信扫描
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

                                PosCore.RXiaoFei_WX_ZFB rXiaoFei_wx_zfb = pCore.xiaoFei_WX_ZFB(money+"", scanCode, callBack);
                                refNo = rXiaoFei_wx_zfb.exInfo.get(Gloabl.orderNo);
                                showMsg("支付宝消费成功:\n"+"支付凭证号"+refNo);
                                //直接发布
                                PosBean posBean=new PosBean();
                                posBean.AliPayrefNo=refNo;
                                EventBus.getDefault().post(posBean);

                            } catch (Exception e) {
                                e.printStackTrace();
//                                ToastUtils.show(e.getLocalizedMessage());
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

                                PosCore.RXiaoFei_WX_ZFB rXiaoFei_wx_zfb = pCore.xiaoFei_WX_ZFB(money+"", scanCode, callBack);
                                refNo = rXiaoFei_wx_zfb.exInfo.get(Gloabl.orderNo);
                                showMsg("微信消费成功:\n"+"支付凭证号"+refNo);
                                PosBean posBean=new PosBean();
                                posBean.WeiXinPayrefNo=refNo;
                                EventBus.getDefault().post(posBean);

                            } catch (Exception e) {
//                                ToastUtils.show(e.getLocalizedMessage());
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

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(PosBean bean) {
        if(bean.AliPayrefNo!=null && pay==3){//支付宝回调
            getPayListener("",bean.AliPayrefNo,payMoney,orderId,3);
            Log.d("info","支付宝:"+bean.AliPayrefNo+"--"+payMoney+"--"+orderId+3);

        }else if(bean.WeiXinPayrefNo!=null && pay==2){//微信回调
            getPayListener("",bean.WeiXinPayrefNo,payMoney,orderId,2);
            Log.d("info","微信回调:"+bean.WeiXinPayrefNo+"--"+payMoney+"--"+orderId+2);
        }
    }

    public void getPayListener(String RetrievalReferenceNumber,String TraceAuditNumber,double ConsumeAmount,String OrderId,int PayType){
        Request<String> request = NoHttp.createStringRequest(Urls.PAY_FROM_STORE, RequestMethod.POST);
        request.add("RetrievalReferenceNumber",RetrievalReferenceNumber);//参考号
        request.add("TraceAuditNumber",TraceAuditNumber);//凭证号
        request.add("ConsumeAmount",ConsumeAmount);//消费金额
        request.add("OrderId",OrderId);//支付订单号
        request.add("PayType",PayType);
        commonNet.requestNetTask(request,getPayListener,1);
    }

    //银联支付成功回调监听
    private HttpListener<String> getPayListener = new HttpListener<String>()
    {
        @Override
        public void onSucceed(int what, Response<String> response)
        {
            Log.d(Constant.TAG,"支付result:"+response.get());
            PayCallbackBean callbackBean=gson.fromJson(response.get(),PayCallbackBean.class);
            if(callbackBean.isSuccess()==true){
                isSuccess=true;
                bundle.putInt("mobilePayType",pay);//移动支付类型
                bundle.putDouble("mobilepayMoney",payMoney);//支付金额
                bundle.putString("mobileOrderNo",orderId);//订单号
                bundle.putString("WxAliTraceAuditNumber",refNo);//凭证号
//                bundle.putDouble("discount",discount);//会员折扣
                bundle.putString("shopNameNick",shopNameNick);//店铺名称
                bundle.putString("userName",userName);//营业员
//                bundle.putInt("point",point);//积分
                bundle.putDouble("originalPrice",payMoney);//原价
//                bundle.putDouble("discountAfter",discountAfter);//折后j价格
//                bundle.putString("mobile",mobile);//会员手机号
                bundle.putBoolean("isSuccess",isSuccess);
            }
        }
        @Override
        public void onFailed(int what, Response<String> response)
        {
            ToastUtils.show(response.get());
        }
    };

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
}
