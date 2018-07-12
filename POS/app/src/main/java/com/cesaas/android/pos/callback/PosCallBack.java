package com.cesaas.android.pos.callback;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;

import com.cesaas.android.pos.activity.cashier.CashierMainActivity;
import com.cesaas.android.pos.db.bean.PosPayBean;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.utils.ACache;
import com.cesaas.android.pos.utils.AbDateUtil;
import com.cesaas.android.pos.utils.BankUtil;
import com.cesaas.android.pos.utils.ToastUtils;
import com.wangpos.poscore.IPosCallBack;
import com.wangpos.poscore.PosCore;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

/**
 * Author FGB
 * Description POS回调
 * Created at 2017/12/21 10:55
 * Version 1.0
 */

public class PosCallBack implements IPosCallBack {
    public final PosCore core;
    public Activity mActivity;
    public Context mContext;
    public ACache mCache;
    public static ZLoadingDialog dialog;

    private byte[] lock = new byte[1];
    int EVENT_NO_PAPER = 1;
    private final int LOCK_WAIT = 0;
    private final int LOCK_CONTINUE = 1;

    //8583协议中的参考号
    public String refNum;
    public static String referenceNumber;//参考号
    public static String traceAuditNumber;//凭证号
    public static String primaryAccountNumber;//卡号
    public static String cardName=null;//银行卡名称
    public static String bankName=null;////发卡行名称
    public static int cardCategory=100;//卡类型

    public PosCallBack(PosCore core,Activity mActivity,ACache mCache,Context mContext) {
        this.core = core;
        this.mActivity=mActivity;
        this.mCache=mCache;
        this.mContext=mContext;
    }

    @Override
    public void onInfo(String s) throws Exception {
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
                    try{
                        primaryAccountNumber=params[0]+"";
                        //获取发卡行，卡种名称
                        cardName= BankUtil.getNameOfBank(primaryAccountNumber);
                        if(cardName!=null && !"".equals(cardName)){
                            bankName=cardName.substring(0, cardName.indexOf("·"));
                            if(cardName.contains("贷记卡")){
                                cardCategory=3;

                            }else if(cardName.contains("信用卡")){
                                cardCategory=2;

                            }else{//借记卡
                                cardCategory=1;
                            }
                        }else{
                            bankName="银行";
                            cardCategory=1;
                        }
//                        showConsumeDialog(core);
//
//                        PosPayBean bean1=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),userName,orderNo,payAmount+"",orderNo,"4","银联支付",IsPractical+"",1+"", AbDateUtil.getCurrentDate(),"银行卡支付",prefs.getString("enCode"),primaryAccountNumber,"false","0","0");
//                        insertData(bean1);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    showMsg("获取银行卡号为空！");
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
                setCachePayInfo(params[0]+"");
                break;
            }
            case EVENT_AutoPrint_end://打印完成

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

    public void showMsg(final String msg) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //提示执行消息
                dialog= new ZLoadingDialog(mContext);
                dialog.setLoadingBuilder(Z_TYPE.SNAKE_CIRCLE)//设置类型
                        .setLoadingColor(Color.BLACK)//颜色
                        .setHintText(msg)
                        .setHintTextSize(16) // 设置字体大小 dp
                        .setHintTextColor(Color.GRAY)  // 设置字体颜色
                        .setCanceledOnTouchOutside(false)
                        .show();
            }
        });
    }

    /**
     * 显示重打印按钮
     */
    private boolean needRePrint;
    private void showRePrintDialog() {
        lock[0] = LOCK_WAIT;
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
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
            core.printContinue(needRePrint);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 缓存支付信息
     * @param refNum
     */
    private void setCachePayInfo(String refNum){
        if(mCache.getAsString("PayInfo")!=null && !"".equals(mCache.getAsString("PayInfo"))){
            mCache.remove("PayInfo");
            mCache.put("PayInfo",refNum);
        }else{
            mCache.put("PayInfo",refNum);
        }
    }
}
