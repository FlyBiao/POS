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
import android.widget.TextView;
import android.widget.Toast;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.base.BaseActivity;
import com.cesaas.android.pos.bean.PayListBean;
import com.cesaas.android.pos.bean.ResultCreatePayBean;
import com.cesaas.android.pos.bean.printer.LatticePrinterOrderBean;
import com.cesaas.android.pos.net.xutils.net.CreatePayNet;
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
 * 支付 流水详情
 */
public class PayDetailsActivity extends BaseActivity implements View.OnClickListener{

    private  LinearLayout ll_order_list_back,ll_again_print;
    private EditText tv_show_msg;
    private TextView tv_pay_amount,tv_pay_type,tv_pay_no,tv_voucher_record,tv_create_time,tv_pay_time,tv_shop_name,tv_refund,tv_pay_source,tv_cashier;

    //8583协议中的参考号
    private String refNum;
    private PosCore pCore;
    private PosCallBack callBack;
    private byte[] lock = new byte[1];
    int EVENT_NO_PAPER = 1;
    private final int LOCK_WAIT = 0;
    private final int LOCK_CONTINUE = 1;
    private PosCore.RXiaoFei rXiaoFei;

    private LatticePrinter latticePrinter;// 点阵打印
    private LatticePrinterOrderBean latticePrinterOrderBean;//票据打印Bean

    private PayListBean payListBean=new PayListBean();

    private int REQUEST_CONTACT = 20;
    final int RESULT_CODE = 101;
    private String scanCode;
    private double returnAmount=0;
    private boolean isRefund=false;

    private CreatePayNet createPayNet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_detail);

        Bundle bundle=getIntent().getExtras();
        payListBean=(PayListBean)bundle.getSerializable("PayList");

        //初始化CoreApp连接对象
        initPosCore();

        initView();
        initData();

    }

    public void initData(){
        tv_pay_amount.setText(payListBean.getPayment()+"");
        tv_pay_no.setText(payListBean.getPayId()+"");
        tv_voucher_record.setText(payListBean.getVoucherRecord());
        tv_create_time.setText(AbDateUtil.toDateYMD(payListBean.getCreateTime()));
        tv_pay_time.setText(payListBean.getPayDate());
        tv_cashier.setText(payListBean.getCashier());
        tv_shop_name.setText(prefs.getString("shopNameNick"));
        switch (payListBean.getPayType()){
            case 2:
                tv_pay_type.setText("微信支付");
                break;
            case 3:
                tv_pay_type.setText("支付宝支付");
                break;
            case 4:
                tv_pay_type.setText("银联支付");
                break;
            case 5:
                tv_pay_type.setText("现金支付");
                break;
        }

        //0, 零售单 1, 储值 2,独立收银
        if(payListBean.getSheetCategory()==0){
            tv_pay_source.setText("零售单");
        }else if(payListBean.getSheetCategory()==1){
            tv_pay_source.setText("会员充值");
        }else{
            tv_pay_source.setText("独立收银");
        }
    }

    private void initView() {
        //通过EventBus注册订阅事件
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        tv_show_msg= (EditText) findViewById(R.id.tv_show_msg);
        ll_order_list_back= (LinearLayout) findViewById(R.id.ll_order_list_back);
        ll_order_list_back.setOnClickListener(this);
        ll_again_print= (LinearLayout) findViewById(R.id.ll_again_print);
        ll_again_print.setOnClickListener(this);
        tv_pay_amount= (TextView) findViewById(R.id.tv_pay_amount);
        tv_pay_type= (TextView) findViewById(R.id.tv_pay_type);
        tv_pay_no= (TextView) findViewById(R.id.tv_pay_no);
        tv_voucher_record= (TextView) findViewById(R.id.tv_voucher_record);
        tv_create_time= (TextView) findViewById(R.id.tv_create_time);
        tv_pay_time= (TextView) findViewById(R.id.tv_pay_time);
        tv_shop_name= (TextView) findViewById(R.id.tv_shop_name);
        tv_refund= (TextView) findViewById(R.id.tv_refund);
        tv_refund.setOnClickListener(this);
        tv_pay_source= (TextView) findViewById(R.id.tv_pay_source);
        tv_cashier= (TextView) findViewById(R.id.tv_cashier);
    }

    //支付支付流水
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(ResultCreatePayBean bean) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_order_list_back://返回
                Skip.mBack(mActivity);
                break;
            case R.id.tv_refund:
                if(payListBean.getPayType()==4){
                    ToastUtils.show(mContext,"只支持微信支付宝退款，请选择其他路径进行退款！",ToastUtils.CENTER);
                }else{
                    if(!AbDateUtil.getStringDateShort().equals(AbDateUtil.toDateYMD(payListBean.getCreateTime()))){
                        ToastUtils.show(mContext,"请选择当天支付订单退款！",ToastUtils.CENTER);
                    }else{
                        returnAmount=payListBean.getPayment();
                        Skip.mRefundActivityResults(mActivity, CaptureActivity.class, REQUEST_CONTACT,payListBean.getPayNo());
                    }
                }
                break;
            case R.id.ll_again_print://重新打印
                new CBDialogBuilder(mContext)
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
                        .create().show();
                break;
        }
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

            init_params.put(PosConfig.Name_EX + "1053", prefs.getString("shopNameNick"));// 签购单小票台头

            init_params.put(PosConfig.Name_EX + "1100", "cn.weipass.cashier");// 核心APP 包名
            init_params.put(PosConfig.Name_EX + "1101", "com.wangpos.cashiercoreapp.services.CoreAppService");// 核心APP 类名
            init_params.put(PosConfig.Name_EX + "1092", "1");//  是否开启订单生成，并且上报服务器 1.开启 0.不开启
            init_params.put(PosConfig.Name_EX + "1093", "2");// 是否需要打印三联签购单 1.需要 2.不需要
            init_params.put(PosConfig.Name_EX + "1012", "1");// 华势通道

            init_params.put(PosConfig.Name_MerchantName, "coreApp");

            pCore = PosCoreFactory.newInstance(this, init_params);
            callBack = new PosCallBack(pCore);
//            Log.i("test","初始化CoreApp连接对象");
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
                latticePrinterOrderBean.setOrderId(payListBean.getSheetId());
                latticePrinterOrderBean.setCounterName(prefs.getString("shopNameNick"));
                latticePrinterOrderBean.setShopClerkName(payListBean.getCashier());
                latticePrinterOrderBean.setTraceAuditNumber(payListBean.getPayNo());
                latticePrinterOrderBean.setOriginalPrice(payListBean.getPayment());
                latticePrinterOrderBean.setDiscountPrice(payListBean.getPayment());
                latticePrinterOrderBean.setTotalPrice(payListBean.getPayment());
                switch (payListBean.getPayType()){
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
                    Log.w("test", "卡号为:" + params[0]);
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
                case EVENT_AutoPrint_end://打印结束
                    break;

                case IPosCallBack.ERR_InTask:{
                    if ((Integer) params[0] == EVENT_NO_PAPER) {
	                        showRePrintDialog();
                    }
                }

                default: {
                    showMsg("Event:" + eventID);
                    break;
                }
            }

        }
    }


    private boolean needRePrint;

    /**
     * 显示重打印按钮
     */
    private void showRePrintDialog() {
        lock[0] = LOCK_WAIT;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder dialog = new AlertDialog.Builder(PayDetailsActivity.this);
                dialog.setMessage("打印机缺纸");
                dialog.setPositiveButton("重打印", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        synchronized (lock) {
                            needRePrint = true;
                            lock[0] = LOCK_CONTINUE;
                            lock.notify();
                        }
                    }
                });

                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        synchronized (lock) {
                            needRePrint = false;
                            lock[0] = LOCK_CONTINUE;
                            lock.notify();
                        }
                    }
                });
                dialog.setCancelable(false);
                dialog.show();
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
        try {
            pCore.printContinue(needRePrint);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        //取消EventBus订阅
        EventBus.getDefault().unregister(this);
        super.onDestroy();
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
                if(!TextUtils.isEmpty(tv_voucher_record.getText().toString())){
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
                                                        final int amount=(int)(Double.parseDouble(DecimalFormatUtils.decimalToFormat(returnAmount))*100);
                                                        PosCore.RTuiHuo rTuiHuo = pCore.tuiHuo_WX_ZFB(scanCode, amount+"", callBack);
                                                        showMsg("退款成功:\n凭证号：" + rTuiHuo.retrievalReferenceNumber);
                                                        isRefund=true;
                                                        //执行打印操作
                                                        setLatticePrinter();
                                                        mActivity.runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                try{
                                                                    if(payListBean.getPayType()==2){
                                                                        createPayNet=new CreatePayNet(mContext);
                                                                        createPayNet.setData(payListBean.getSheetId(),-payListBean.getPayment(),payListBean.getPayType(),tv_voucher_record.getText().toString(),"","","微信退款",tv_voucher_record.getText().toString(),payListBean.getTId()+"","",payListBean.getSheetCategory(),100);
                                                                    }else if(payListBean.getPayType()==3){
                                                                        createPayNet=new CreatePayNet(mContext);
                                                                        createPayNet.setData(payListBean.getSheetId(),-payListBean.getPayment(),payListBean.getPayType(),tv_voucher_record.getText().toString(),"","","支付宝退款",tv_voucher_record.getText().toString(),payListBean.getTId()+"","",payListBean.getSheetCategory(),100);
                                                                    }else {
                                                                        createPayNet=new CreatePayNet(mContext);
                                                                        createPayNet.setData(payListBean.getSheetId(),-payListBean.getPayment(),payListBean.getPayType(),tv_voucher_record.getText().toString(),"","","退款",tv_voucher_record.getText().toString(),payListBean.getTId()+"","",payListBean.getSheetCategory(),100);
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
                }else{
                    ToastUtils.getLongToast(mContext,"为获取支付凭证号！");
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
