package com.cesaas.android.pos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.cesaas.android.pos.activity.cashier.CashierHomeActivity;
import com.cesaas.android.pos.activity.cashier.CheckAccountsActivity;
import com.cesaas.android.pos.activity.order.DownOrderActivity;
import com.cesaas.android.pos.activity.qr.GetSinglePayQrCodeActivity;
import com.cesaas.android.pos.activity.user.LoginActivity;
import com.cesaas.android.pos.base.BaseActivity;
import com.cesaas.android.pos.bean.PayCallbackBean;
import com.cesaas.android.pos.bean.PosBean;
import com.cesaas.android.pos.bean.PosOrderIdBean;
import com.cesaas.android.pos.bean.ResultGetOrderBean;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.global.Gloabl;
import com.cesaas.android.pos.global.Urls;
import com.cesaas.android.pos.gridview.MyGridAdapter;
import com.cesaas.android.pos.gridview.MyGridView;
import com.cesaas.android.pos.net.nohttp.HttpListener;
import com.cesaas.android.pos.utils.RandomUtils;
import com.cesaas.android.pos.utils.Skip;
import com.cesaas.android.pos.utils.ToastUtils;
import com.wangpos.pay.UnionPay.PosConfig;
import com.wangpos.poscore.IPosCallBack;
import com.wangpos.poscore.PosCore;
import com.wangpos.poscore.PosTaskException;
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

import scanner.CaptureActivity;

public class MainActivity extends BaseActivity{

    private static final int CAMERA = 1;//相机权限

    private long exitTime = 0; // 退出点击间隔时间

    private byte[] lock = new byte[1];
    int EVENT_NO_PAPER = 1;
    private final int LOCK_WAIT = 0;
    private final int LOCK_CONTINUE = 1;

    private int REQUEST_CONTACT = 20;
    final int RESULT_CODE = 101;
    private String scanCode;
    //8583协议中的参考号
    private String refNum;
    private PosCore pCore;
    private EditText tv_show_msg;
    private PosCallBack callBack;
    private String amount;
    private Integer money;
    private double payMoney;
    private int orderStatus;
    private int curSelectedPos=0;//当前选择的收银方式

    private String refNo;
    private PosOrderIdBean idBean;

    private MyGridView gridView;//九宫格gridView

    PosCore.RXiaoFei rXiaoFei;
    private String referenceNumber;//参考号
    private String traceAuditNumber;//凭证号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initGridView();
        checkAndRequestPermission();
        //初始化CoreApp连接对象
        initPosCore();

        //        //通过EventBus订阅事件
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        //获取旺POS设备信息
//                    String deviceInfoJson = WeiposImpl.as().getDeviceInfo();
//                    PosDeviceInfo deviceInfo=gson.fromJson(deviceInfoJson, PosDeviceInfo.class);
//                    String en=deviceInfo.getEn().replaceAll("\\s","");//pos设备EN号
//                    String mName=deviceInfo.getMname();//店铺名称
//                    String mCode=deviceInfo.getMcode();//店铺Mcode

    }

    public void initGridView(){
        gridView= (MyGridView) findViewById(R.id.gridview);
        tv_show_msg= (EditText) findViewById(R.id.tv_show_msg);
        gridView.setAdapter(new MyGridAdapter(mContext));
        //九宫格点击事件
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                switch (position) {
                    case 0://查账
                        Skip.mNext(mActivity, CheckAccountsActivity.class);
                        break;
                    case 1://收银
//                        cashier();
                        Skip.mScanOrderActivityResult(mActivity, CaptureActivity.class, REQUEST_CONTACT);
                        break;

                    case 2://下单
                        Skip.mNext(mActivity, DownOrderActivity.class);
                        break;
                    case 3://签到
                        Skip.mNext(mActivity,GetSinglePayQrCodeActivity.class);
                        break;
                    case 4://退款
//                        Skip.mNext(mActivity, WeiXinAndAliPayActivity.class);
                        Skip.mNext(mActivity, CashierHomeActivity.class);
//                        Skip.mScanRefundOrderActivityResult(mActivity, CaptureActivity.class, REQUEST_CONTACT);
                        break;
                    case 5://退出
                        exit();
                        break;
                }
            }
        });
    }

    /**
     * 收银Dialog
     */
    public void cashier(){
        new CBDialogBuilder(MainActivity.this)
                .setTouchOutSideCancelable(false)
                .showConfirmButton(false)
                .setTitle("选择收银方式")
                .setConfirmButtonText("ok")
                .setCancelButtonText("cancel")
                .setDialogAnimation(CBDialogBuilder.DIALOG_ANIM_SLID_BOTTOM)
                .setItems(new String[]{"银联刷卡", "微信支付", "支付宝", "现金支付"}, new CBDialogBuilder.onDialogItemClickListener() {

                    @Override
                    public void onDialogItemClick(CBDialogBuilder.DialogItemAdapter ItemAdapter,
                                                  Context context, CBDialogBuilder dialogbuilder, Dialog dialog,
                                                  int position) {

                        if(position==0){//银联刷卡
                            curSelectedPos=position;
                            //启动银联收银
                            amount = "2";
                            lock[0] = LOCK_WAIT;
                            doConsumeHasTemplate(amount,scanCode);
                            dialog.dismiss();

                        }else if(position==1){//微信支付
                            curSelectedPos=position;
                            Skip.mScanWeiXinPayOrderActivityResult(mActivity, CaptureActivity.class, REQUEST_CONTACT);
                            dialog.dismiss();

                        }else if(position==2){//支付宝
                            curSelectedPos=position;
                            Skip.mScanAliPayOrderActivityResult(mActivity, CaptureActivity.class, REQUEST_CONTACT);
                            dialog.dismiss();

                        }else if(position==3){//现金支付
                            curSelectedPos=position;
                            if(orderStatus!=30 || orderStatus!=40 || orderStatus!=100) {
                                //随机生成12位参考号【规则：当前时间+2位随机数】
                                referenceNumber = RandomUtils.getCurrentTimeAsNumber() + RandomUtils.getToFourRandom();
                                //随机生成6位凭证号【规则：当月+4位随机数】
                                traceAuditNumber = RandomUtils.getCurrentTimeMM() + RandomUtils.getFourRandom();

                                getPayListener(referenceNumber,traceAuditNumber,payMoney,idBean.getOrderid(),5);
                                dialog.dismiss();
                            }else{
                                ToastUtils.show("改订单已支付！");
                                dialog.dismiss();
                            }
                        }
                        //TODO 保存选中设置
                    }
                }, curSelectedPos)
                .create().show();
    }

    /**
     * 处理扫描Activity返回的数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode==RESULT_CODE) {
            if(data.getStringExtra("mScanOrderResult")!=null && data.getStringExtra("mScanOrderResult").equals("012")){
                scanCode= data.getStringExtra("resultCode");
                if(scanCode!=null){
                    idBean =new PosOrderIdBean();
                    idBean.setOrderid(scanCode);
                    Request<String> request = NoHttp.createStringRequest(Urls.GET_ORDER, RequestMethod.POST);
                    request.add("TradeId",scanCode);
                    commonNet.requestNetTask(request,getOrderListener,1);
                }
            }

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
                                showMsg("支付宝消费成功:/n" + refNo + "状态码:/n" + rXiaoFei_wx_zfb.orderStauts+"消费金额/n"+payMoney);

                                //直接发布
                                PosBean posBean=new PosBean();
                                posBean.AliPayrefNo=refNo;
                                EventBus.getDefault().post(posBean);

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

                                PosCore.RXiaoFei_WX_ZFB rXiaoFei_wx_zfb = pCore.xiaoFei_WX_ZFB(money+"", scanCode, callBack);
                                refNo = rXiaoFei_wx_zfb.exInfo.get(Gloabl.orderNo);

                                showMsg("微信支付消费成功:/n" + refNo + "状态码:/n" + rXiaoFei_wx_zfb.orderStauts+"消费金额/n"+payMoney);

                                PosBean posBean=new PosBean();
                                posBean.WeiXinPayrefNo=refNo;
                                EventBus.getDefault().post(posBean);
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

            if(data.getStringExtra("mScanRefundOrderResult")!=null && data.getStringExtra("mScanRefundOrderResult").equals("015")){
                scanCode= data.getStringExtra("resultCode");
                if(scanCode!=null){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                PosCore.RTuiHuo rTuiHuo = pCore.tuiHuo_WX_ZFB(scanCode, "1", callBack);
                                showMsg("退款成功:" + rTuiHuo.retrievalReferenceNumber);
                            } catch (Exception e) {
                                showMsg(e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
    }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void showMsg(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_show_msg.setText(msg);
            }
        });
    }

    //getOrder回调监听
    private HttpListener<String> getOrderListener = new HttpListener<String>()
    {
        @Override
        public void onSucceed(int what, Response<String> response)
        {
            ResultGetOrderBean bean= gson.fromJson(response.get(),ResultGetOrderBean.class);
            Log.d(Constant.TAG,"result:"+response.get());
            if(bean.TModel!=null){
                for (int i=0;i<bean.TModel.size();i++){
                    payMoney=bean.TModel.get(i).PayPrice;
                    orderStatus=bean.TModel.get(i).OrderStatus;
                }
            if(orderStatus!=30 || orderStatus!=40 || orderStatus!=100){
                cashier();
            }else{
                ToastUtils.show("该订单已支付。");
            }
            }
        }
        @Override
        public void onFailed(int what, Response<String> response)
        {
            ToastUtils.show(response.getException().getMessage());
        }
    };

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

                if(curSelectedPos==3){//现金支付
                    //显示现金支付结果
                    new CBDialogBuilder(MainActivity.this)
                            .setTouchOutSideCancelable(true)
                            .showCancelButton(true)
                            .setTitle("现金消费成功")
                            .setMessage("订单号:"+idBean.getOrderid()+"\n参考号:"+referenceNumber+"\n凭证号:"+traceAuditNumber+"\n消费金额"+payMoney)
                            .setConfirmButtonText("确定")
                            .setCancelButtonText("已支付")
                            .setDialogAnimation(CBDialogBuilder.DIALOG_ANIM_SLID_BOTTOM)
                            .setButtonClickListener(true, new CBDialogBuilder.onDialogbtnClickListener() {
                                @Override
                                public void onDialogbtnClick(Context context, Dialog dialog, int whichBtn) {
                                    switch (whichBtn) {
                                        case BUTTON_CONFIRM:
                                            ToastUtils.show("已确认支付");
                                            break;
                                        case BUTTON_CANCEL:
                                            ToastUtils.show("已确认支付");
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            })
                            .create().show();
                }
            }
        }
        @Override
        public void onFailed(int what, Response<String> response)
        {
            ToastUtils.show(response.get());
        }
    };

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
            callBack = new PosCallBack(pCore);
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
                            "银联消费成功:>>>>\n卡号:" + rXiaoFei.primaryAccountNumber + "\n"
                                    + "参考号:" + rXiaoFei.retrievalReferenceNumber
                                    + "\n凭证号:" + rXiaoFei.systemTraceAuditNumber
                                    + "\n消费金额:" +payMoney);

                    refNum = rXiaoFei.retrievalReferenceNumber;

                    PosBean posBean=new PosBean();
                    posBean.AccountNumber=rXiaoFei.primaryAccountNumber;
                    posBean.ReferenceNumber=rXiaoFei.retrievalReferenceNumber;
                    posBean.TraceAuditNumber=rXiaoFei.systemTraceAuditNumber;
                    //直接发布
                    EventBus.getDefault().post(posBean);
                } catch (Exception e) {
                    Object param = ((PosTaskException) e).params[1];//获取pos状态码
                        showMsg(e.getLocalizedMessage()+"状态码:"+param);
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(PosBean bean) {
       if(bean.ReferenceNumber!=null && idBean.getOrderid()!=null){//银联回调
           Log.d(Constant.TAG,"银联回调"+bean.WeiXinPayrefNo+"="+payMoney+"="+idBean.getOrderid()+"="+4);
           getPayListener(rXiaoFei.retrievalReferenceNumber,rXiaoFei.systemTraceAuditNumber,payMoney,idBean.getOrderid(),4);

       }else if(bean.AliPayrefNo!=null && idBean.getOrderid()!=null){//支付宝回调
           Log.d(Constant.TAG,"支付宝回调"+bean.WeiXinPayrefNo+"="+payMoney+"="+idBean.getOrderid()+"="+3);
           getPayListener("",bean.AliPayrefNo,payMoney,idBean.getOrderid(),3);

       }else if(bean.WeiXinPayrefNo!=null && idBean.getOrderid()!=null){//微信回调
           Log.d(Constant.TAG,"微信回调"+bean.WeiXinPayrefNo+"="+payMoney+"="+idBean.getOrderid()+"="+2);
           getPayListener("",bean.WeiXinPayrefNo,payMoney,idBean.getOrderid(),2);
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
                final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).setView(view).setCancelable(false).create();
                dialog.show();

                Button btn_confirm = (Button) view.findViewById(R.id.btn_consume_confiem);
                Button btn_cancel = (Button) view.findViewById(R.id.btn_consume_cancel);
                final EditText ed_consumen_amount = (EditText) view.findViewById(R.id.ed_consume_amount);
                ed_consumen_amount.setFocusable(false);ed_consumen_amount.setFocusableInTouchMode(false);//设置不可编辑状态；
                if(!TextUtils.isEmpty(ed_consumen_amount.getText().toString())){
                    ed_consumen_amount.setText("");//设置支付金额
                }
                ed_consumen_amount.setText(payMoney+"");//设置支付金额

                btn_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {//确定支付

                        synchronized (lock) {
                           money=(int)(payMoney*100);
                            lock[0] = LOCK_CONTINUE;
                            lock.notify();
                        }
                        dialog.dismiss();
                    }
                });

                btn_cancel.setOnClickListener(new View.OnClickListener() {//取消支付
                    @Override
                    public void onClick(View v) {
                        synchronized (lock) {
                            ed_consumen_amount.setText("");//设置支付金额
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
     * 检查权限【Android6.0动态申请运行时权限】
     */
    private void checkAndRequestPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                Log.i(Constant.TAG, "hi! everybody, I really need ths permission for better service. thx!");

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, CAMERA);
            }
        }
    }

    /**
     *  处理授权请求回调
     使用onRequestPermissionsResult(int ,String , int[])方法处理回调，
     上面说到了根据requestPermissions()方法中的requestCode，
     就可以在回调方法中区分授权请求
     */
    @SuppressLint("Override")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(Constant.TAG, "PERMISSION_GRANTED: Thx, I will give u a better service!");
                } else {
                    Log.i(Constant.TAG, "PERMISSION_DENIED: Sorry, without this permission I can't do next work for u");
                }
                return;
            }
        }
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

    /**
     * 退出应用
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            try {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    ToastUtils.show("再按一次退出应用");
                    exitTime = System.currentTimeMillis();
                } else {
                    for (int i = 0; i < BaseActivity.activityList.size(); i++) {
                        if (null != BaseActivity.activityList.get(i)) {
                            Skip.mBack(BaseActivity.activityList.get(i));
                        }
                    }
                    Skip.mBack(this);
                }
                return true;
            } catch (Exception e) {
                Skip.mBack(this);
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 退出
     */
    public void exit(){
        new CBDialogBuilder(MainActivity.this)
                .setTouchOutSideCancelable(true)
                .showCancelButton(true)
                .setTitle("退出登录")
                .setMessage("是否退出登录，退出后将不能做任何操作！")
                .setConfirmButtonText("确定")
                .setCancelButtonText("取消")
                .setDialogAnimation(CBDialogBuilder.DIALOG_ANIM_SLID_BOTTOM)
                .setButtonClickListener(true, new CBDialogBuilder.onDialogbtnClickListener() {
                    @Override
                    public void onDialogbtnClick(Context context, Dialog dialog, int whichBtn) {
                        switch (whichBtn) {
                            case BUTTON_CONFIRM:
                                prefs.cleanAll();
                                Skip.mNext(mActivity, LoginActivity.class, true);
                                break;
                            case BUTTON_CANCEL:
                                ToastUtils.show("已取消退出");
                                break;
                            default:
                                break;
                        }
                    }
                })
                .create().show();
    }

}
