//package com.cesaas.android.pos.utils;
//
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.cesaas.android.pos.global.Constant;
//import com.wangpos.poscore.IPosCallBack;
//import com.wangpos.poscore.PosCore;
//
///**
// * ================================================
// * 作    者：FGB
// * 描    述：收银工具类
// * 创建日期：2016/10/11 16:09
// * 版    本：1.0
// * 修订历史：
// * ================================================
// */
//public class CashierUtils {
//
//    private byte[] lock = new byte[1];
//    int EVENT_NO_PAPER = 1;
//    private final int LOCK_WAIT = 0;
//    private final int LOCK_CONTINUE = 1;
//    //8583协议中的参考号
//    private String refNum;
//    private PosCore pCore;
//
//    /**
//     * 收银回调
//     */
//    class PosCallBack implements IPosCallBack {
//        private final PosCore core;
//
//        PosCallBack(PosCore core) {
//            this.core = core;
//        }
//
//        @Override
//        public void onInfo(String s) {
//            showMsg(s);
//        }
//
//        @Override
//        public void onEvent(int eventID, Object[] params) throws Exception {
//            switch (eventID) {
//                case 110:
//                    showMsg("打印票据" + params[0]);
//                    break;
//
//                case EVENT_Setting:{
//                    core.reprint(refNum);
//                    showMsg("doSetting:完成");
//                    break;
//                }
//
//                case EVENT_Task_start: {
//                    showMsg("任务进程开始执行");
//                    break;
//                }
//                case EVENT_Task_end: {
//                    showMsg("任务进程执行结束");
//                    break;
//                }
//                case EVENT_CardID_start: {
//                    showMsg("读取银行卡信息");
//                    break;
//                }
//                case EVENT_CardID_end: {
//                    String cardNum = (String) params[0];
//                    if (!TextUtils.isEmpty(cardNum)) {
//                        Log.w(Constant.TAG, "卡号为:" + params[0]);
//                        showConsumeDialog(core);
//                    }
//                    break;
//                }
//                case EVENT_Comm_start: {
//                    showMsg("开始网络通信");
//                    break;
//                }
//                case EVENT_Comm_end: {
//                    showMsg("网络通信完成");
//                    break;
//                }
//                case EVENT_DownloadPlugin_start: {
//                    showMsg("开始下载插件");
//                    break;
//                }
//                case EVENT_DownloadPlugin_end: {
//                    showMsg("插件下载完成");
//                    break;
//                }
//                case EVENT_InstallPlugin_start: {
//                    showMsg("开始安装插件");
//                    break;
//                }
//                case EVENT_InstallPlugin_end: {
//                    showMsg("插件安装完成");
//                    break;
//                }
//                case EVENT_RunPlugin_start: {
//                    showMsg("开始启动插件");
//                    break;
//                }
//                case EVENT_RunPlugin_end: {
//                    showMsg("插件启动完成");
//                    break;
//                }
//
//                case EVENT_AutoPrint_start:{
//                    showMsg("参考号:" + params[0]);
//                    break;
//                }
//
//                case IPosCallBack.ERR_InTask:{
//                    if ((Integer) params[0] == EVENT_NO_PAPER) {
////	                        showRePrintDialog();
//                    }
//                }
//
//                default: {
//                    showMsg("Event:" + eventID);
//                    break;
//                }
//            }
//
//        }
//    }
//}
