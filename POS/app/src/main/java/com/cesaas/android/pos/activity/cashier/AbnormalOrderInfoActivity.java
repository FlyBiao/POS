package com.cesaas.android.pos.activity.cashier;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.cesaas.android.pos.R;
import com.cesaas.android.pos.base.BaseActivity;
import com.cesaas.android.pos.bean.PayCallbackBean;
import com.cesaas.android.pos.bean.ResultCreatePayBean;
import com.cesaas.android.pos.db.bean.PosPayBean;
import com.cesaas.android.pos.db.pay.PosSqliteDatabaseUtils;
import com.cesaas.android.pos.global.Gloabl;
import com.cesaas.android.pos.global.Urls;
import com.cesaas.android.pos.net.nohttp.HttpListener;
import com.cesaas.android.pos.net.xutils.net.CreatePayNet;
import com.cesaas.android.pos.net.xutils.net.value.AddTradeNet;
import com.cesaas.android.pos.utils.AbDateUtil;
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

/**
 * ================================================
 * 作    者：FGB
 * 描    述：异常单 信息
 * 创建日期：2017/12/5 20:42
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class AbnormalOrderInfoActivity extends BaseActivity implements View.OnClickListener{

    private TextView tvTitle;
    private EditText tv_show_msg;
    private LinearLayout llBack,ll_abnormal_query,ll_abnormal_hander;
    private TextView tv_order_no,tv_pay_amount,tv_create_time,tv_traceAudit_number,tv_pay_type,tv_create_name,tv_shop_name,tv_order_status,tv_operation_type;

    private int payOrderStauts;
    private String IsPractical;
    private String payType;
    private String orderType;
    private String orderId;
    private String bankName=null;
    private PosCore pCore;
    private Thread queryPayThread;
    private WxAliPosCallBack callBack;
    private CreatePayNet createPayNet;
    private AddTradeNet addTradeNet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abnormal_order_info);

        Bundle bundle=getIntent().getExtras();
        orderId=bundle.getString("orderId");

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initView();
        initData();
        initWwnAliPosCore();
    }

    private void initData() {
        PosSqliteDatabaseUtils.selectByOrderNoData(mContext,orderId);
    }

    private void initView() {
        tvTitle= (TextView) findViewById(R.id.tv_base_title);
        tvTitle.setText("异常单详情");
        llBack= (LinearLayout) findViewById(R.id.ll_base_title_back);
        llBack.setOnClickListener(this);
        tv_order_no= (TextView) findViewById(R.id.tv_order_no);
        tv_pay_amount= (TextView) findViewById(R.id.tv_pay_amount);
        tv_create_time= (TextView) findViewById(R.id.tv_create_time);
        tv_traceAudit_number= (TextView) findViewById(R.id.tv_traceAudit_number);
        tv_create_name= (TextView) findViewById(R.id.tv_create_name);
        tv_shop_name= (TextView) findViewById(R.id.tv_shop_name);
        tv_pay_type= (TextView) findViewById(R.id.tv_pay_type);
        ll_abnormal_hander= (LinearLayout) findViewById(R.id.ll_abnormal_hander);
        ll_abnormal_hander.setOnClickListener(this);
        tv_show_msg= (EditText) findViewById(R.id.tv_show_msg);
        ll_abnormal_query= (LinearLayout) findViewById(R.id.ll_abnormal_query);
        ll_abnormal_query.setOnClickListener(this);
        tv_order_status= (TextView) findViewById(R.id.tv_order_status);
        tv_operation_type= (TextView) findViewById(R.id.tv_operation_type);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(PosPayBean msg) {
        if(msg!=null){
            tv_order_no.setText(msg.getOrderNo());
            tv_pay_amount.setText(msg.getAmount());
            tv_create_time.setText(msg.getCreateTime());
            tv_create_name.setText(msg.getCreateName());
            tv_shop_name.setText(msg.getShopName());
            tv_pay_type.setText(msg.getPayName());
            bankName=msg.getAccountNumber();
            orderType=msg.getOrderType();
            payType=msg.getPayType();
            IsPractical=msg.getIsPractical();
            tv_traceAudit_number.setText(msg.getTraceAudit());

            if(payType.equals("4")) {
                ll_abnormal_hander.setVisibility(View.VISIBLE);
                ll_abnormal_query.setVisibility(View.GONE);
            }else{
                ll_abnormal_hander.setVisibility(View.GONE);
                ll_abnormal_query.setVisibility(View.VISIBLE);
            }

            if(msg.getOrderStatus().equals("0")){
                tv_order_status.setText("异常订单");
                tv_order_status.setTextColor(mContext.getResources().getColor(R.color.red));
            }else{
                tv_order_status.setText("正常");
                tv_order_status.setTextColor(mContext.getResources().getColor(R.color.green));
            }

            if(msg.getOrderType().equals("0")){
                tv_operation_type.setText("独立收银");
            }else if(msg.getOrderType().equals("1")){
                tv_operation_type.setText("零售单");
            }else if(msg.getOrderType().equals("2")){
                tv_operation_type.setText("会员充值");
            }else{
                tv_operation_type.setText("收银异常单");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_base_title_back:
                Skip.mBack(mActivity);
                break;
            case R.id.ll_abnormal_query:
                queryPayResult(tv_traceAudit_number.getText().toString());
                break;
            case R.id.ll_abnormal_hander:
                if(!AbDateUtil.getStringDateShort().equals(AbDateUtil.toDateYMD(tv_create_time.getText().toString()))){
                    ToastUtils.show(mContext,"请选择当天异常订单！",ToastUtils.CENTER);
                }else{
                    if(!TextUtils.isEmpty(tv_traceAudit_number.getText().toString())){
                        showDialog();
                    }else{
                        ToastUtils.getLongToast(mContext,"未获取该订单支付凭证号，请确认是否已支付成功！");
                    }
                }
                break;
        }
    }

    public void showDialog(){
        new CBDialogBuilder(this)
                .setTouchOutSideCancelable(true)
                .showCancelButton(true)
                .setTitle("温馨提示！")
                .setMessage("是否马上处理该异常单。")
                .setConfirmButtonText("确认")
                .setCancelButtonText("取消")
                .setDialogAnimation(CBDialogBuilder.DIALOG_ANIM_SLID_BOTTOM)
                .setButtonClickListener(true, new CBDialogBuilder.onDialogbtnClickListener() {
                    @Override
                    public void onDialogbtnClick(Context context, Dialog dialog, int whichBtn) {
                        switch (whichBtn) {
                            case BUTTON_CONFIRM:
                                if(orderType.equals("0")){//独立收银
                                    if(payType.equals("3")){
                                        createPayNet=new CreatePayNet(mContext,mActivity,prefs,tv_create_name.getText().toString(),orderId,Double.parseDouble(tv_pay_amount.getText().toString()),Integer.parseInt(IsPractical));
                                        createPayNet.setData(tv_order_no.getText().toString(),Double.parseDouble(tv_pay_amount.getText().toString()),3,tv_traceAudit_number.getText().toString(),tv_traceAudit_number.getText().toString(),"","支付宝支付",tv_traceAudit_number.getText().toString(),prefs.getString("userShopId"),"",2,100);
                                    }else if(payType.equals("2")){
                                        createPayNet=new CreatePayNet(mContext,mActivity,prefs,tv_create_name.getText().toString(),orderId,Double.parseDouble(tv_pay_amount.getText().toString()),Integer.parseInt(IsPractical));
                                        createPayNet.setData(tv_order_no.getText().toString(),Double.parseDouble(tv_pay_amount.getText().toString()),2,tv_traceAudit_number.getText().toString(),tv_traceAudit_number.getText().toString(),"","微信支付",tv_traceAudit_number.getText().toString(),prefs.getString("userShopId"),"",2,100);
                                    }else{
                                        showSure();
                                    }
                                }else if(orderType.equals("1")){//订单推送
                                    if(payType.equals("3")){
                                        createPayNet=new CreatePayNet(mContext,mActivity,prefs,tv_create_name.getText().toString(),orderId,Double.parseDouble(tv_pay_amount.getText().toString()),Integer.parseInt(IsPractical));
                                        createPayNet.setData(tv_order_no.getText().toString(),Double.parseDouble(tv_pay_amount.getText().toString()),3,tv_traceAudit_number.getText().toString(),tv_traceAudit_number.getText().toString(),"","支付宝支付",tv_traceAudit_number.getText().toString(),prefs.getString("userShopId"),"",0,100);
                                        getPayListener("",tv_traceAudit_number.getText().toString(),Double.parseDouble(tv_pay_amount.getText().toString()),orderId,3,"notFansVip",Integer.parseInt(IsPractical));
                                    }else if(payType.equals("2")){
                                        createPayNet=new CreatePayNet(mContext,mActivity,prefs,tv_create_name.getText().toString(),orderId,Double.parseDouble(tv_pay_amount.getText().toString()),Integer.parseInt(IsPractical));
                                        createPayNet.setData(tv_order_no.getText().toString(),Double.parseDouble(tv_pay_amount.getText().toString()),2,tv_traceAudit_number.getText().toString(),tv_traceAudit_number.getText().toString(),"","微信支付",tv_traceAudit_number.getText().toString(),prefs.getString("userShopId"),"",0,100);
                                        getPayListener("",tv_traceAudit_number.getText().toString(),Double.parseDouble(tv_pay_amount.getText().toString()),orderId,2,"notFansVip",Integer.parseInt(IsPractical));
                                    }else{
                                        showSure();
                                    }
                                }else if(orderType.equals("2")){//会员充值
                                    if(payType.equals("3")){
                                        createPayNet=new CreatePayNet(mContext,mActivity,prefs,tv_create_name.getText().toString(),orderId,Double.parseDouble(tv_pay_amount.getText().toString()),Integer.parseInt(IsPractical));
                                        createPayNet.setData(tv_order_no.getText().toString(),Double.parseDouble(tv_pay_amount.getText().toString()),3,tv_traceAudit_number.getText().toString(),tv_traceAudit_number.getText().toString(),"","支付宝支付",tv_traceAudit_number.getText().toString(),prefs.getString("userShopId"),"",1,100);
//                                        addTradeNet=new AddTradeNet(mContext);
//                                        addTradeNet.setData(Integer.parseInt(vipId),prefs.getString("userShopId"),tv_traceAudit_number.getText().toString(),3,Double.parseDouble(tv_pay_amount.getText().toString()),id,prefs.getString("enCode"),shopClerkId);
                                    }else if(payType.equals("2")){
                                        createPayNet=new CreatePayNet(mContext,mActivity,prefs,tv_create_name.getText().toString(),orderId,Double.parseDouble(tv_pay_amount.getText().toString()),Integer.parseInt(IsPractical));
                                        createPayNet.setData(tv_order_no.getText().toString(),Double.parseDouble(tv_pay_amount.getText().toString()),2,tv_traceAudit_number.getText().toString(),tv_traceAudit_number.getText().toString(),"","微信支付",tv_traceAudit_number.getText().toString(),prefs.getString("userShopId"),"",1,100);
//                                        addTradeNet=new AddTradeNet(mContext);
//                                        addTradeNet.setData(Integer.parseInt(vipId),prefs.getString("userShopId"),tv_traceAudit_number.getText().toString(),2,Double.parseDouble(tv_pay_amount.getText().toString()),id,prefs.getString("enCode"),shopClerkId);
                                    }else{
                                        showSure();
                                    }
                                }else{
                                    ToastUtils.getLongToast(mContext,"未获取订单类型!");
                                }
                                break;
                            case BUTTON_CANCEL:
                                ToastUtils.show("已取消异常单处理。");
                                break;
                            default:
                                break;
                        }
                    }
                })
                .create().show();
    }


    public void showSure(){
        new CBDialogBuilder(this)
                .setTouchOutSideCancelable(true)
                .showCancelButton(true)
                .setTitle("温馨提示！")
                .setMessage("该订单是银联支付，请确认是否已收到银联支付票据？")
                .setConfirmButtonText("确认")
                .setCancelButtonText("取消")
                .setDialogAnimation(CBDialogBuilder.DIALOG_ANIM_SLID_BOTTOM)
                .setButtonClickListener(true, new CBDialogBuilder.onDialogbtnClickListener() {
                    @Override
                    public void onDialogbtnClick(Context context, Dialog dialog, int whichBtn) {
                        switch (whichBtn) {
                            case BUTTON_CONFIRM:
                                if(orderType.equals("0")){//独立收银
                                    createPayNet=new CreatePayNet(mContext,mActivity,prefs,tv_create_name.getText().toString(),orderId,Double.parseDouble(tv_pay_amount.getText().toString()),Integer.parseInt(IsPractical));
                                    createPayNet.setData(tv_order_no.getText().toString(),Double.parseDouble(tv_pay_amount.getText().toString()),4,tv_order_no.getText().toString(),tv_traceAudit_number.getText().toString(),bankName,"银行卡支付",tv_traceAudit_number.getText().toString(),prefs.getString("userShopId"),"",0,1);
                                }else if(orderType.equals("1")){//订单推送
                                    createPayNet=new CreatePayNet(mContext,mActivity,prefs,tv_create_name.getText().toString(),orderId,Double.parseDouble(tv_pay_amount.getText().toString()),Integer.parseInt(IsPractical));
                                    createPayNet.setData(tv_order_no.getText().toString(),Double.parseDouble(tv_pay_amount.getText().toString()),4,tv_order_no.getText().toString(),tv_traceAudit_number.getText().toString(),bankName,"银行卡支付",tv_traceAudit_number.getText().toString(),prefs.getString("userShopId"),"",1,1);
                                    getPayListener("",tv_traceAudit_number.getText().toString(),Double.parseDouble(tv_pay_amount.getText().toString()),orderId,4,"notFansVip",Integer.parseInt(IsPractical));
                                }else if(orderType.equals("2")){ //会员充值
                                    createPayNet=new CreatePayNet(mContext,mActivity,prefs,tv_create_name.getText().toString(),orderId,Double.parseDouble(tv_pay_amount.getText().toString()),Integer.parseInt(IsPractical));
                                    createPayNet.setData(tv_order_no.getText().toString(),Double.parseDouble(tv_pay_amount.getText().toString()),4,tv_order_no.getText().toString(),tv_traceAudit_number.getText().toString(),bankName,"银行卡支付",tv_traceAudit_number.getText().toString(),prefs.getString("userShopId"),"",2,1);
                                }else{
                                    ToastUtils.getLongToast(mContext,"未获取订单类型!");
                                }
                                break;
                            case BUTTON_CANCEL:
                                ToastUtils.show("已取消异常单处理。");
                                break;
                            default:
                                break;
                        }
                    }
                })
                .create().show();
    }


    /**
     * 接收支付流水结果信息
     * @param bean
     */
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(ResultCreatePayBean bean) {
        if(bean.isSuccess()!=false){
            PosSqliteDatabaseUtils.update(mContext,payOrderStauts+"","订单完成支付","true",tv_order_no.getText().toString(),"1");
            ToastUtils.getLongToast(mContext,"异常单处理完成。");
            if(orderType.equals("0")) {//独立收银
                Skip.mNext(mActivity,CashierMainActivity.class,true);
            }
        }else{
            ToastUtils.getToast(mContext,"创建支付流水失败："+bean.getMessage());
        }
    }

    public void getPayListener(String RetrievalReferenceNumber,String TraceAuditNumber,double ConsumeAmount,String OrderId,int PayType,String OpenId,int IsPractical){
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
            PayCallbackBean callbackBean=gson.fromJson(response.get(),PayCallbackBean.class);
            if(callbackBean.isSuccess()==true){
                Skip.mNext(mActivity,CashierMainActivity.class,true);

            }else{
                Log.d("test","支付result:"+callbackBean.getMessage());
                try {
                    //记录pos 支付日志
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
                        switch (payOrderStauts){
                            case 3:
                                showMsg("订单已支付完成"+  "\n" + "凭证号:" + chaxun_wx_zfb.exInfo.get(Gloabl.orderNo)+"\n" + "第三方订单号:" + chaxun_wx_zfb.exInfo.get(Gloabl.thirdSerialNo));
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ll_abnormal_hander.setVisibility(View.VISIBLE);
                                        ll_abnormal_query.setVisibility(View.GONE);
                                    }
                                });
                                break;
                            case 2:
                                showMsg("订单已关闭。");
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ll_abnormal_hander.setVisibility(View.GONE);
                                        ll_abnormal_query.setVisibility(View.VISIBLE);
                                    }
                                });
                                break;
                            case 1:
                                showMsg("订单等待支付。");
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ll_abnormal_hander.setVisibility(View.GONE);
                                        ll_abnormal_query.setVisibility(View.VISIBLE);
                                    }
                                });
                                break;
                            default:
                                showMsg("未知订单。");
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ll_abnormal_hander.setVisibility(View.GONE);
                                        ll_abnormal_query.setVisibility(View.VISIBLE);
                                    }
                                });
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        showMsg(e.getLocalizedMessage());
                    }
                }
            });
            queryPayThread.start();
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
}
