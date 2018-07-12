package com.cesaas.android.pos.test;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.activity.cashier.CashierHomeActivity;
import com.cesaas.android.pos.bean.PosBean;
import com.cesaas.android.pos.global.Constant;
import com.wangpos.pay.UnionPay.PosConfig;
import com.wangpos.poscore.IPosCallBack;
import com.wangpos.poscore.PosCore;
import com.wangpos.poscore.impl.PosCoreFactory;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

public class TestActivity extends AppCompatActivity {

    private EditText tv_show_amount;
    private Button btn_kaishi;


    private byte[] lock = new byte[1];
    int EVENT_NO_PAPER = 1;
    private final int LOCK_WAIT = 0;
    private final int LOCK_CONTINUE = 1;

    //8583协议中的参考号
    private String refNum;
    private PosCore.RXiaoFei rXiaoFei;
    private PosCore pCore;
    private PosCallBack callBack;
    private String amount;

    private double money=0.0;
    private double payMoney=0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        tv_show_amount= (EditText) findViewById(R.id.tv_show_amount);
        btn_kaishi= (Button) findViewById(R.id.btn_kaishi);

        //初始化CoreApp连接对象
        initPosCore();

        btn_kaishi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //启动银联收银
                amount = "2";
                lock[0] = LOCK_WAIT;
                doConsumeHasTemplate(amount,"8945223190");
            }
        });
    }

    private void initPosCore() {
        if (pCore == null) {

            // 配置数据为开发阶段的数据
            HashMap<String, String> init_params = new HashMap<String,String>();

            init_params.put(PosConfig.Name_EX + "1053", "签购单小票台头");// 签购单小票台头

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

                    refNum = rXiaoFei.retrievalReferenceNumber;

                    PosBean posBean=new PosBean();
                    posBean.AccountNumber=rXiaoFei.primaryAccountNumber;
                    posBean.ReferenceNumber=rXiaoFei.retrievalReferenceNumber;
                    posBean.TraceAuditNumber=rXiaoFei.systemTraceAuditNumber;
                    //直接发布
                    EventBus.getDefault().post(posBean);

                } catch (Exception e) {
                    e.printStackTrace();
                    showMsg(e.getLocalizedMessage());
                    Log.d(Constant.TAG,"银联ERROR：="+e.getLocalizedMessage());
                }
            }
        }.start();
    }


    public void showMsg(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_show_amount.setText("");
                tv_show_amount.setTextSize(20);
                tv_show_amount.setText(msg);
            }
        });
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
                final AlertDialog dialog = new AlertDialog.Builder(TestActivity.this).setView(view).setCancelable(false).create();
                dialog.show();

                Button btn_confirm = (Button) view.findViewById(R.id.btn_consume_confiem);
                Button btn_cancel = (Button) view.findViewById(R.id.btn_consume_cancel);
                final EditText ed_consumen_amount = (EditText) view.findViewById(R.id.ed_consume_amount);
                if(!TextUtils.isEmpty(ed_consumen_amount.getText().toString())){
                    ed_consumen_amount.setText("");//设置支付金额
                }

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
                case 110:
//                    showMsg("打印空白" + (int) params[0]);
                    break;

                case EVENT_Setting:{
//                    HashMap<String, String> param = core.getParam("payTypeList");
//                    showMsg(param.toString());

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
                        showConsumeDialog(core);
                    }
                    // core.setXiaoFeiAmount("101");
                    break;
                    // Thread.sleep(9999999);
                    // throw new IOException("我的网络连接失败");
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
//                        showRePrintDialog();
                    }
                }

                default: {
                    showMsg("Event:" + eventID);
                    break;
                }
            }

        }
    }

}
