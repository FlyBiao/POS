package com.cesaas.android.pos.activity.user;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.base.BaseActivity;
import com.cesaas.android.pos.bean.JieSuanBean;
import com.cesaas.android.pos.bean.PayLogBean;
import com.cesaas.android.pos.db.bean.ResultPayLogBean;
import com.cesaas.android.pos.db.pay.PosSqliteDatabaseUtils;
import com.cesaas.android.pos.dialog.WaitDialog;
import com.cesaas.android.pos.net.xutils.net.PosPayLogNet;
import com.cesaas.android.pos.utils.JsonUtils;
import com.cesaas.android.pos.utils.Skip;
import com.cesaas.android.pos.utils.ToastUtils;
import com.wangpos.pay.UnionPay.PosConfig;
import com.wangpos.poscore.PosCore;
import com.wangpos.poscore.impl.PosCoreFactory;
import com.zhl.cbdialog.CBDialogBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

/**
 * 应用设置
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener{

    private TextView tvTitle;
    private LinearLayout llBack,ll_upload_log,ll_del_log,ll_jiesuan;
    private String strJson=null;
    private WaitDialog dialog;
    private boolean isJieSuan=false;

    private PosCore pCore;
    private JieSuanBean jieSuanBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        //通过EventBus订阅事件
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        dialog = new WaitDialog(mContext);
        initView();
    }

    private void initView() {
        tvTitle= (TextView) findViewById(R.id.tv_base_title);
        tvTitle.setText("设置");
        llBack= (LinearLayout) findViewById(R.id.ll_base_title_back);
        llBack.setOnClickListener(this);
        ll_jiesuan= (LinearLayout) findViewById(R.id.ll_jiesuan);
        ll_jiesuan.setOnClickListener(this);

        //查询上传日志
        ll_upload_log= (LinearLayout) findViewById(R.id.ll_upload_log);
        ll_upload_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PosSqliteDatabaseUtils.selectData(mContext);
            }
        });
        //删除所有数据
        ll_del_log= (LinearLayout) findViewById(R.id.ll_del_log);
        ll_del_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(ResultPayLogBean posPayBeanArrayList) {
        if(posPayBeanArrayList.getPosPayBeanArrayList().size()!=0){
            try {
                strJson= JsonUtils.toJson(posPayBeanArrayList.getPosPayBeanArrayList());
                payLog("PayLog",strJson,"PayLog");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else{
            ToastUtils.getLongToast(mContext,"当前没有订单记录可上传！");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(PayLogBean bean) {
        if(bean.isSuccess()!=false){
            //清除原有的数据
            PosSqliteDatabaseUtils.delete(mContext);
            ToastUtils.getLongToast(mContext,"上传日志成功！");
        }else{
            ToastUtils.getLongToast(mContext,"上传日志失败！");
        }
    }

    public void payLog(String LogType, String obj,String remark){
        //Pay Log
        PosPayLogNet posPayLogNet=new PosPayLogNet(mContext);
        posPayLogNet.setData(LogType,obj,remark);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_base_title_back:
                Skip.mBack(mActivity);
                break;
            case R.id.ll_jiesuan:
                if(isJieSuan!=true){
                    dialog.show();
                    isJieSuan=true;
                    initPosCore();
                }
                break;
        }
    }

    public void showDialog(){
        try{
            new CBDialogBuilder(mContext)
                    .setTouchOutSideCancelable(true)
                    .showCancelButton(true)
                    .setTitle("温馨提示！")
                    .setMessage("确定清除多余数据吗？")
                    .setConfirmButtonText("确定")
                    .setCancelButtonText("取消")
                    .setDialogAnimation(CBDialogBuilder.DIALOG_ANIM_SLID_BOTTOM)
                    .setButtonClickListener(true, new CBDialogBuilder.onDialogbtnClickListener() {
                        @Override
                        public void onDialogbtnClick(Context context, Dialog dialog, int whichBtn) {
                            switch (whichBtn) {
                                case BUTTON_CONFIRM:
                                    PosSqliteDatabaseUtils.delete(mContext);
                                    ToastUtils.getLongToast(mContext,"数据清理成功！");
                                    break;
                                case BUTTON_CANCEL:
                                    ToastUtils.getLongToast(mContext,"已取消数据清理！");
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

            init_params.put(PosConfig.Name_MerchantName, prefs.getString("BrandName"));

            pCore = PosCoreFactory.newInstance(this, init_params);

            doPiJieSuan();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(JieSuanBean msg) {
        if(msg!=null){
            ToastUtils.getLongToast(mContext,msg.getMsg());
            dialog.dismiss();
        }
    }

    /**
     * 批结算
     */
    private void doPiJieSuan() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PosCore.RPiJieSua rPiJieSua = pCore.piJieSuan();
                    Log.i("test","批结算:"+rPiJieSua.exInfo);
                    jieSuanBean=new JieSuanBean();
                    jieSuanBean.setMsg("设置批量结算成功");
                    EventBus.getDefault().post(jieSuanBean);
                } catch (Exception e) {
                    Log.i("test","批结算e:"+e.getLocalizedMessage());
                    jieSuanBean=new JieSuanBean();
                    jieSuanBean.setMsg(e.getLocalizedMessage());
                    EventBus.getDefault().post(jieSuanBean);
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
