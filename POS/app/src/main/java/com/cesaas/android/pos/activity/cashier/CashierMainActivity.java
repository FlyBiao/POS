package com.cesaas.android.pos.activity.cashier;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.cesaas.android.pos.R;
import com.cesaas.android.pos.activity.order.DownOrderActivity;
import com.cesaas.android.pos.activity.user.LoginActivity;
import com.cesaas.android.pos.activity.user.SettingActivity;
import com.cesaas.android.pos.base.BaseRecyclerView;
import com.cesaas.android.pos.base.BaseUtils;
import com.cesaas.android.pos.bean.CreateActionBean;
import com.cesaas.android.pos.bean.ResultCreatePayBean;
import com.cesaas.android.pos.bean.ShopEnCodeBean;
import com.cesaas.android.pos.bean.UserInfoBean;
import com.cesaas.android.pos.base.BaseActivity;
import com.cesaas.android.pos.custom.paykey.SetUpPayKey;
import com.cesaas.android.pos.db.bean.PosPayBean;
import com.cesaas.android.pos.db.pay.PosSqliteDatabaseUtils;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.inventory.activity.InventoryMainActivity;
import com.cesaas.android.pos.listener.OnItemClickListener;
import com.cesaas.android.pos.net.xutils.net.BankCardInfoNet;
import com.cesaas.android.pos.net.xutils.net.CreateActionNet;
import com.cesaas.android.pos.net.xutils.net.CreatePayNet;
import com.cesaas.android.pos.net.xutils.net.PayCategoryNet;
import com.cesaas.android.pos.net.xutils.net.ShopEnCodeNet;
import com.cesaas.android.pos.net.xutils.net.UserInfoNet;
import com.cesaas.android.pos.pos.adapter.PosPayCategoryAdapter;
import com.cesaas.android.pos.pos.bank.ResultBankInfoBean;
import com.cesaas.android.pos.rongcloud.activity.ConversationListActivity;
import com.cesaas.android.pos.storedvalue.bean.ResultPayCategoryBean;
import com.cesaas.android.pos.storedvalue.ui.SummaryActivity;
import com.cesaas.android.pos.test.utils.SlidingMenu;
import com.cesaas.android.pos.utils.AbAppUtil;
import com.cesaas.android.pos.utils.AbDateUtil;
import com.cesaas.android.pos.utils.BankUtil;
import com.cesaas.android.pos.utils.JsonUtils;
import com.cesaas.android.pos.utils.RandomUtils;
import com.cesaas.android.pos.utils.Skip;
import com.cesaas.android.pos.utils.ToastUtils;
import com.google.gson.Gson;
import com.jauker.widget.BadgeView;
import com.wangpos.pay.UnionPay.PosConfig;
import com.wangpos.poscore.IPosCallBack;
import com.wangpos.poscore.PosCore;
import com.wangpos.poscore.impl.PosCoreFactory;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.zhl.cbdialog.CBDialogBuilder;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：POS收银首页【新版】
 * 创建日期：2016/10/26
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class CashierMainActivity extends BaseActivity implements View.OnClickListener{

    private byte[] lock = new byte[1];
    int EVENT_NO_PAPER = 1;
    private final int LOCK_WAIT = 0;
    private final int LOCK_CONTINUE = 1;

    private int REQUEST_CONTACT = 20;
    final int RESULT_CODE = 101;

    //侧滑菜单
    private SlidingMenu mMenu;
    //键盘
    private GridView gridview;
    private EditText editMemberAmount;
    private LinearLayout ll_check_accounts,ll_down_order,ll_settle_accounts,ll_inventory,llStoredValue,ll_app_setting,ll_convers,rl_exit,ll_settle_abnormal,ll_pay_list;

    private TextView tvCashier,tv_show_msg,tv_app_version;
    private TextView tvMore,textView_titleBar,tv_abnormal_count;

    private UserInfoBean.User user;

    private UserInfoNet userInfoNet;
    private PayCategoryNet payCategoryNet;
    private ShopEnCodeNet enCodeNet;

    private String shopNameNick;
    private String userName;
    private String BrandName;
    private String userShopId;
    private double payAmount=0;
    private double amounOfTransactions=0;
    private Integer money;
    private String orderNo;
    private int IsPractical=0;
    private int menuType;
    private String message;

    private CreatePayNet createPayNet;
    private String cardNum;

    private List<ResultPayCategoryBean.PayCategoryBea> categoryBeaList;
    private CustomSingleCashierDialog singleCashierDialog;

    private BadgeView badgeView;

    private CreateActionNet actionNet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashier_main);
        //通过EventBus订阅事件
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initView();
        //初始化CoreApp连接对象
        initPosCore();
        SetUpPayKey.setUpPayKeyAdapter(gridview,editMemberAmount,mContext);
        initData();
        //查询异常单
//        PosSqliteDatabaseUtils.selectByOrderStatusData(mContext,"0");

    }


//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onDataSynEvent(List<PosPayBean> msg){
//        if(msg!=null && msg.size()!=0){
//            badgeView=new BadgeView(this);
//            badgeView.setTargetView(tv_abnormal_count);
//            badgeView.setBadgeCount(msg.size());
//        }
//    }

    private void initData() {
        if(mCache.getAsString("UserInfo")!=null && !"".equals(mCache.getAsString("UserInfo"))){
            UserInfoBean bean= JsonUtils.fromJson(mCache.getAsString("UserInfo"),UserInfoBean.class);
            user=bean.TModel;
            shopNameNick=bean.TModel.getShopName();
            userName=bean.TModel.getName();
            userShopId=bean.TModel.getShopId();
            textView_titleBar.setText(shopNameNick);
        }else{
            userInfoNet=new UserInfoNet(mContext,mCache);
            userInfoNet.setData();
        }

        if(mCache.getAsString(Constant.SHOP_EN_CODE)!=null && !"".equals(mCache.getAsString(Constant.SHOP_EN_CODE))){
            ShopEnCodeBean bean=gson.fromJson(mCache.getAsString(Constant.SHOP_EN_CODE),ShopEnCodeBean.class);
            if(bean.TModel!=null){
                if(bean.TModel.indexOf(prefs.getString("enCode"))!=-1){
                    //属于当前设备encode
                    mCache.put(Constant.POS_EN_CODE,"true");
                    return;
                }else{
                    //非当前设备encode
                    mCache.put(Constant.POS_EN_CODE,"false");
                    return;
                }
            }else{
                //非当前设备encode
                mCache.put(Constant.POS_EN_CODE,"false");
            }
        }else{
            enCodeNet=new ShopEnCodeNet(mContext,mCache,prefs);
            enCodeNet.setData();
        }
        actionNet=new CreateActionNet(mContext);
        actionNet.setData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(CreateActionBean msg) {
        if(msg.isSuccess()!=false){
            showMenuType(menuType);
        }else{
            showLogin(msg.getMessage());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(UserInfoBean bean) {
        if(bean.isSuccess()!=false && bean.TModel!=null){
            user=bean.TModel;
            shopNameNick=bean.TModel.getShopName();
            userName=bean.TModel.getName();
            userShopId=bean.TModel.getShopId();
            BrandName=bean.TModel.getBrandName();
            textView_titleBar.setText(shopNameNick);
            prefs.putString("userName",userName);
            prefs.putString("BrandName",bean.TModel.getBrandName());
            prefs.putString("userShopId",userShopId);
            prefs.putString("shopNameNick",shopNameNick);
            prefs.putString("ShopAddress",bean.TModel.getShopAddress());
            prefs.putString("ShopArea",bean.TModel.getShopArea());
        }else{
            ToastUtils.getLongToast(mContext,"获取用户信息失败！"+bean.getMessage());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(ResultPayCategoryBean bean) {
        if(bean.isSuccess()!=false && bean.TModel!=null){
            categoryBeaList=new ArrayList<>();
            categoryBeaList=bean.TModel;
            singleCashierDialog=new CustomSingleCashierDialog(mContext, R.style.dialog, R.layout.item_custom_single_cashier_dialog,categoryBeaList);
            singleCashierDialog.show();
            singleCashierDialog.setCancelable(false);
        }else{
            ToastUtils.getLongToast(mContext,"获取支付方式失败！"+bean.getMessage());
        }
    }

    //接收银行卡信息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(ResultBankInfoBean bean) {
            try{
                if(bean.getError_code()==0){
                    //获取发卡行，卡种名称
                    bankName=bean.getResult().getBank();
                    if(bean.getResult().getCardtype()!=null && !"".equals(bean.getResult().getCardtype())){
                        if(bean.getResult().getCardtype().contains("贷记卡")){
                            cardCategory=3;
                        }else if(bean.getResult().getCardtype().contains("信用卡")){
                            cardCategory=2;
                        }else if(bean.getResult().getCardtype().contains("借记卡")){//借记卡
                            cardCategory=1;
                        }else{
                            bankName="银行卡";
                            cardCategory=3;
                        }
                    }else{
                        bankName="银行卡";
                        cardCategory=3;
                    }
                }else{
                    if (!TextUtils.isEmpty(cardNum)) {
                        try{
                            //获取发卡行，卡种名称
                            cardName=BankUtil.getNameOfBank(primaryAccountNumber);
                            if(cardName!=null && !"".equals(cardName)){
                                bankName=cardName.substring(0, cardName.indexOf("·"));
                                if(cardName.contains("贷记卡")){
                                    cardCategory=3;

                                }else if(cardName.contains("信用卡")){
                                    cardCategory=2;

                                }else if(cardName.contains("借记卡")){//借记卡
                                    cardCategory=1;
                                }else{
                                    bankName="银行";
                                    cardCategory=3;
                                }
                            }else{
                                bankName="银行";
                                cardCategory=3;
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        ToastUtils.getLongToast(mContext,"获取银行卡号为空！");
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
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
                    showMsg("消费成功:>>>>\n卡号:" + rXiaoFei.primaryAccountNumber + "\n"
                            + "参考号:" + rXiaoFei.retrievalReferenceNumber
                            + "\n消费金额:" + rXiaoFei.amounOfTransactions);

                    amounOfTransactions=Double.parseDouble(rXiaoFei.amounOfTransactions)/100;

                    PosSqliteDatabaseUtils.updateUnionPay(mContext,3+"","订单完成支付","true",orderNo,"1",rXiaoFei.retrievalReferenceNumber);

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(rXiaoFei.systemTraceAuditNumber!=null && !"".equals(rXiaoFei.systemTraceAuditNumber)){
                                createPayNet=new CreatePayNet(mContext,mActivity,prefs,userName,orderNo,amounOfTransactions,IsPractical);
                                createPayNet.setData(rXiaoFei.retrievalReferenceNumber,amounOfTransactions,4,rXiaoFei.retrievalReferenceNumber,rXiaoFei.retrievalReferenceNumber,primaryAccountNumber,"银联刷卡",rXiaoFei.systemTraceAuditNumber,userShopId,bankName,2,cardCategory);
                            }else{
                                if(orderNo!=null){
                                    createPayNet=new CreatePayNet(mContext,mActivity,prefs,userName,orderNo,amounOfTransactions,IsPractical);
                                    createPayNet.setData(orderNo,Double.parseDouble(rXiaoFei.amounOfTransactions),4,orderNo,rXiaoFei.retrievalReferenceNumber,primaryAccountNumber,"银联刷卡",rXiaoFei.systemTraceAuditNumber,userShopId,bankName,2,cardCategory);
                                }else{
                                    createPayNet=new CreatePayNet(mContext,mActivity,prefs,userName,orderNo,amounOfTransactions,IsPractical);
                                    createPayNet.setData(primaryAccountNumber,amounOfTransactions,4,primaryAccountNumber,rXiaoFei.retrievalReferenceNumber,primaryAccountNumber,"银联刷卡",primaryAccountNumber,userShopId,bankName,2,cardCategory);
                                }
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    showMsg(e.getLocalizedMessage());
                    PosSqliteDatabaseUtils.deleteByNo(mContext,orderNo);
                    Log.d("test","银联ERROR：="+e.getLocalizedMessage());
                }
            }
        }.start();
    }

    /**
     * 接收支付流水结果信息
     * @param bean
     */
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(ResultCreatePayBean bean) {
        message=bean.Message;
        if(bean.isSuccess()!=false){
            PosSqliteDatabaseUtils.updateUnionPay(mContext,3+"","订单完成支付-创建流水成功"+message,"true",orderNo,"1",rXiaoFei.retrievalReferenceNumber);
        }else{
            PosSqliteDatabaseUtils.updateUnionPay(mContext,3+"","订单完成支付-创建流水失败："+message,"false",orderNo,"0",rXiaoFei.retrievalReferenceNumber);
            ToastUtils.getToast(mContext,"创建支付流水失败："+message);
        }
    }

    @Override
    public void onClick(View v) {
//            if(mCache.getAsString(Constant.POS_EN_CODE)!=null && mCache.getAsString(Constant.POS_EN_CODE).equals("true")){
                switch (v.getId()){
                    case R.id.tv_cashier://收款
                        menuType=1;
                        actionNet=new CreateActionNet(mContext);
                        actionNet.setData();
                        break;
                    case R.id.ll_check_accounts://查单
                        menuType=2;
                        actionNet=new CreateActionNet(mContext);
                        actionNet.setData();
                        break;
                    case R.id.ll_settle_accounts://结算对账
                        menuType=3;
                        actionNet=new CreateActionNet(mContext);
                        actionNet.setData();
                        break;
                    case R.id.ll_pay_list://支付流水
                        menuType=4;
                        actionNet=new CreateActionNet(mContext);
                        actionNet.setData();
                        break;
                    case R.id.ll_settle_abnormal://异常单处理
                        menuType=5;
                        actionNet=new CreateActionNet(mContext);
                        actionNet.setData();
                        break;
                    case R.id.ll_down_order://下单
                        menuType=6;
                        actionNet=new CreateActionNet(mContext);
                        actionNet.setData();
                        break;
                    case R.id.ll_stored_value://充值
                        menuType=7;
                        actionNet=new CreateActionNet(mContext);
                        actionNet.setData();
                        break;
                    case R.id.ll_inventory://盘点
                        menuType=8;
                        actionNet=new CreateActionNet(mContext);
                        actionNet.setData();
                        break;
                    case R.id.ll_app_setting://设置
                        menuType=9;
                        actionNet=new CreateActionNet(mContext);
                        actionNet.setData();
                        break;
                    case R.id.ll_convers://订单消息
                        menuType=10;
                        actionNet=new CreateActionNet(mContext);
                        actionNet.setData();
                        break;
                }
//            }else{
//                ToastUtils.getToast(mContext,"请使用当前店铺设备账号进行操作！");
//            }
    }

    private void initView() {
        textView_titleBar= (TextView) findViewById(R.id.textView_titleBar);
        tv_app_version= (TextView) findViewById(R.id.tv_app_version);
        tv_app_version.setText("V"+ AbAppUtil.getAppVersion(this));
        mMenu = (SlidingMenu) findViewById(R.id.id_menu);
        editMemberAmount= (EditText) findViewById(R.id.editMemberAmount);
        editMemberAmount.setInputType(InputType.TYPE_NULL);
        gridview= (GridView) findViewById(R.id.gridview);
        tvCashier= (TextView) findViewById(R.id.tv_cashier);
        tvCashier.setOnClickListener(this);
        tvMore= (TextView) findViewById(R.id.tv_more);
        tv_show_msg= (TextView) findViewById(R.id.tv_tv_show_msg);
        tv_abnormal_count= (TextView) findViewById(R.id.tv_abnormal_count);
        ll_inventory= (LinearLayout) findViewById(R.id.ll_inventory);
        ll_down_order= (LinearLayout) findViewById(R.id.ll_down_order);
        ll_check_accounts= (LinearLayout) findViewById(R.id.ll_check_accounts);
        ll_settle_accounts= (LinearLayout) findViewById(R.id.ll_settle_accounts);
        ll_settle_abnormal= (LinearLayout) findViewById(R.id.ll_settle_abnormal);
        llStoredValue= (LinearLayout) findViewById(R.id.ll_stored_value);
        ll_app_setting= (LinearLayout) findViewById(R.id.ll_app_setting);
        ll_convers= (LinearLayout) findViewById(R.id.ll_convers);
        rl_exit= (LinearLayout) findViewById(R.id.rl_exit);
        ll_pay_list= (LinearLayout) findViewById(R.id.ll_pay_list);
        ll_inventory.setOnClickListener(this);
        ll_down_order.setOnClickListener(this);
        ll_check_accounts.setOnClickListener(this);
        ll_settle_accounts.setOnClickListener(this);
        ll_settle_abnormal.setOnClickListener(this);
        llStoredValue.setOnClickListener(this);
        ll_app_setting.setOnClickListener(this);
        ll_convers.setOnClickListener(this);
        ll_pay_list.setOnClickListener(this);
        tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenu.toggle();
            }
        });
        rl_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenu.closeMenu();
                BaseUtils.exit(mContext,mActivity,mCache,prefs);
            }
        });
    }

    /**
     * 初始化CoreApp连接对象
     *
     * @return
     */
    PosCore.RXiaoFei rXiaoFei;
    private String referenceNumber;//参考号
    private String traceAuditNumber;//凭证号
    private String primaryAccountNumber;//卡号
    private String cardName=null;//银行卡名称
    private String bankName=null;////发卡行名称
    private int cardCategory=100;//卡类型
    //8583协议中的参考号
    private String refNum=null;
    private PosCore pCore;
    private PosCallBack callBack;
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
            callBack = new PosCallBack(pCore);
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
                    cardNum = (String) params[0];
                    if (!TextUtils.isEmpty(cardNum)) {
                        Log.w(Constant.TAG, "卡号为:" + params[0]);
                        try{
                            primaryAccountNumber=params[0]+"";
                            //获取发卡行，卡种名称
                            cardName=BankUtil.getNameOfBank(primaryAccountNumber);
                            if(cardName!=null && !"".equals(cardName)){
                                bankName=cardName.substring(0, cardName.indexOf("·"));
                                if(cardName.contains("贷记卡")){
                                    cardCategory=3;

                                }else if(cardName.contains("信用卡")){
                                    cardCategory=2;

                                }else if(cardName.contains("借记卡")){//借记卡
                                    cardCategory=1;
                                }else{
                                    //执行查询银行卡信息接口
                                    mActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            BankCardInfoNet cardInfoNet=new BankCardInfoNet(mContext,cardNum);
                                            cardInfoNet.setData();
                                        }
                                    });
                                }
                            }else{
                                //执行查询银行卡信息接口
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        BankCardInfoNet cardInfoNet=new BankCardInfoNet(mContext,cardNum);
                                        cardInfoNet.setData();
                                    }
                                });
                            }
                            showConsumeDialog(core);
                            PosPayBean bean1=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),userName,orderNo,payAmount+"",refNum,"4","银联支付",IsPractical+"",1+"", AbDateUtil.getCurrentDate(),"银行卡支付",prefs.getString("enCode"),primaryAccountNumber,"false","0","0");
                            insertData(bean1);

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        ToastUtils.getLongToast(mContext,"获取银行卡号为空！");
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
                    refNum=(String)params[0];
                    showMsg("参考号:" + params[0]);
                    break;
                }
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
                final AlertDialog dialog = new AlertDialog.Builder(CashierMainActivity.this).setView(view).setCancelable(false).create();
                dialog.show();

                Button btn_confirm = (Button) view.findViewById(R.id.btn_consume_confiem);
                Button btn_cancel = (Button) view.findViewById(R.id.btn_consume_cancel);
                final EditText ed_consumen_amount = (EditText) view.findViewById(R.id.ed_consume_amount);
                ed_consumen_amount.setFocusable(false);ed_consumen_amount.setFocusableInTouchMode(false);//设置不可编辑状态；
//                if(!TextUtils.isEmpty(ed_consumen_amount.getText().toString())){
//                    ed_consumen_amount.setText("");//设置支付金额
//                }
                ed_consumen_amount.setText("");
                ed_consumen_amount.setText(editMemberAmount.getText().toString());//设置支付金额
                btn_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {//确定支付
                        synchronized (lock) {
                            money=0;
                            double payMent=Double.parseDouble(ed_consumen_amount.getText().toString());
                            money=(int)(payMent*100);
                            lock[0] = LOCK_CONTINUE;
                            lock.notify();
                            try {
                                core.setXiaoFeiAmount(money+"");//设置消费金额
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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
     * 显示重打印按钮
     */
    private boolean needRePrint;
    private void showRePrintDialog() {
        lock[0] = LOCK_WAIT;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder dialog = new AlertDialog.Builder(CashierMainActivity.this);
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

    private void insertData(PosPayBean bean){
        PosSqliteDatabaseUtils.insterData(mContext,bean);
    }

    /**
     * 自定义独立收银dialog
     */
    public class CustomSingleCashierDialog extends Dialog{
        int layoutRes;//布局文件
        Context context;
        SwipeMenuRecyclerView rvView;
        LinearLayout ll_cancel_pos_cashier;

        List<ResultPayCategoryBean.PayCategoryBea> categoryBeaList;
        PosPayCategoryAdapter posPayCategoryAdapter;

        public CustomSingleCashierDialog(Context context, int theme, int resLayout,List<ResultPayCategoryBean.PayCategoryBea> categoryBeaList) {
            super(context, theme);
            this.context = context;
            this.layoutRes = resLayout;
            this.categoryBeaList=categoryBeaList;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.setContentView(layoutRes);
            initView();
            initPayData();
        }

        public void initPayData() {
            posPayCategoryAdapter = new PosPayCategoryAdapter(categoryBeaList,100);
            posPayCategoryAdapter.setOnItemClickListener(onItemClickListener);
            rvView.setAdapter(posPayCategoryAdapter);
        }

        public void initView() {
            ll_cancel_pos_cashier = (LinearLayout) findViewById(R.id.ll_cancel_pos_cashier);
            rvView = (SwipeMenuRecyclerView) findViewById(R.id.rv_view);
            BaseRecyclerView.initRecyclerView(getContext(), rvView, false);
            ll_cancel_pos_cashier.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
        private OnItemClickListener onItemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                switch (categoryBeaList.get(position).getCategoryType()) {
                    case 2://微信支付
                        if (payAmount!=0) {//折后价格非等于null
                            bundle.putDouble("PayMoney", payAmount);
                        } else {//折后金额为空
                            bundle.putDouble("PayMoney", payAmount);
                        }
                        //随机生成6位凭证号【规则：当月+4位随机数】
                        traceAuditNumber = RandomUtils.getCurrentTimeMM() + RandomUtils.getFourRandom();
                        bundle.putInt("Pay", categoryBeaList.get(position).getCategoryType());
                        bundle.putInt("IsPractical", categoryBeaList.get(position).getIsPractical());
                        bundle.putString("userShopId", user.getShopId());
                        bundle.putString("OrderNo", traceAuditNumber);
                        bundle.putDouble("discount", 0);
                        bundle.putString("shopNameNick", user.getShopName());
                        bundle.putString("userName", user.getName());
                        bundle.putInt("point", 0);
                        bundle.putDouble("originalPrice", payAmount);
                        bundle.putDouble("discountAfter", payAmount);
                        bundle.putString("mobile", user.getMobile());
                        bundle.putBoolean("isSuccess", true);
                        Skip.mNextFroData(mActivity, WeiXinAndAliPaySingleActivity.class, bundle);
                        singleCashierDialog.dismiss();
                        break;
                    case 3://支付宝
                        if (payAmount!=0) {//折后价格非等于null
                            bundle.putDouble("PayMoney", payAmount);
                        } else {//折后金额为空
                            bundle.putDouble("PayMoney", payAmount);
                        }
                        //随机生成6位凭证号【规则：当月+4位随机数】
                        traceAuditNumber = RandomUtils.getCurrentTimeMM() + RandomUtils.getFourRandom();
                        bundle.putInt("IsPractical", categoryBeaList.get(position).getIsPractical());
                        bundle.putInt("Pay", categoryBeaList.get(position).getCategoryType());
                        bundle.putString("OrderNo", traceAuditNumber);
                        bundle.putDouble("discount", 0);
                        bundle.putString("shopNameNick", user.getShopName());
                        bundle.putString("userShopId", user.getShopId());
                        bundle.putString("userName", user.getName());
                        bundle.putInt("point", 0);
                        bundle.putDouble("originalPrice", payAmount);
                        bundle.putDouble("discountAfter", payAmount);
                        bundle.putString("mobile", user.getMobile());
                        bundle.putBoolean("isSuccess", true);
                        Skip.mNextFroData(mActivity, WeiXinAndAliPaySingleActivity.class, bundle);
                        singleCashierDialog.dismiss();
                        break;
                    case 4://银联支付
                        orderNo= RandomUtils.getCurrentTimeMM() + RandomUtils.getFourRandom()+"";
                        //启动银联收银
                        lock[0] = LOCK_WAIT;
                        Integer amount = (int)(payAmount*100);
                        doConsumeHasTemplate(amount+"", orderNo);
                        singleCashierDialog.dismiss();
                        break;
                    default:
                        ToastUtils.getLongToast(mContext,"暂不支持该支付方式！");
                        break;
                }
            }
        };
    }

    public void showMenuType(int menuSkipType){
        switch (menuSkipType){
            case 1://收款
                menuType=0;
                payAmount=0;
                payAmount=Double.parseDouble(editMemberAmount.getText().toString());
                if(payAmount!=0){
                    if(mCache.getAsString("GetPayCategory")!=null && !"".equals(mCache.getAsString("GetPayCategory"))){
                        ResultPayCategoryBean bean = JsonUtils.fromJson(mCache.getAsString("GetPayCategory"), ResultPayCategoryBean.class);
                        categoryBeaList=new ArrayList<>();
                        categoryBeaList=bean.TModel;
                        if(categoryBeaList.size()!=0){
                            try{
                                singleCashierDialog=new CustomSingleCashierDialog(mContext, R.style.dialog, R.layout.item_custom_single_cashier_dialog,categoryBeaList);
                                singleCashierDialog.show();
                                singleCashierDialog.setCancelable(false);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else{
                            ToastUtils.getLongToast(mContext,"未获取支付方式！");
                        }
                    }else{
                        payCategoryNet=new PayCategoryNet(mContext,mCache);
                        payCategoryNet.setData();
                    }
                }else{
                    ToastUtils.getLongToast(mContext,"请输入收款金额！");
                }
                break;
            case 2://查单
                menuType=0;
                mMenu.closeMenu();
                Skip.mNext(mActivity, CheckAccountsListActivity.class);
                break;
            case 3://结算对账
                menuType=0;
                mMenu.closeMenu();
                bundle.putString("shopName",shopNameNick);
                bundle.putString("userName",userName);
                Skip.mNextFroData(mActivity,SettleAccountsActivity.class,bundle);
                break;
            case 4://支付流水
                menuType=0;
                mMenu.closeMenu();
                Skip.mNext(mActivity, PayListActivity.class);
                break;
            case 5://异常单处理
                menuType=0;
                mMenu.closeMenu();
                Skip.mNext(mActivity,AbnormalOrderActivity.class);
                break;
            case 6://下单
                menuType=0;
                mMenu.closeMenu();
                bundle.putString("userName",userName);
                Skip.mNextFroData(mActivity, DownOrderActivity.class,bundle);
                break;
            case 7://充值
                menuType=0;
                mMenu.closeMenu();
                Skip.mNext(mActivity, SummaryActivity.class);
                break;
            case 8://盘点
                menuType=0;
                mMenu.closeMenu();
                Skip.mNext(mActivity,InventoryMainActivity.class);
                break;
            case 9://设置
                menuType=0;
                mMenu.closeMenu();
                Skip.mNext(mActivity,SettingActivity.class);
                break;
            case 10://订单消息
                menuType=0;
                mMenu.closeMenu();
                Skip.mNext(mActivity,ConversationListActivity.class);
                break;
        }
    }


    public void showLogin(String msg){
        new CBDialogBuilder(this)
                .setTouchOutSideCancelable(false)
                .showCancelButton(true)
                .setTitle("温馨提示！")
                .setMessage(msg)
                .setConfirmButtonText("确认")
                .setCancelButtonText("取消")
                .setDialogAnimation(CBDialogBuilder.DIALOG_ANIM_SLID_BOTTOM)
                .setButtonClickListener(true, new CBDialogBuilder.onDialogbtnClickListener() {
                    @Override
                    public void onDialogbtnClick(Context context, Dialog dialog, int whichBtn) {
                        switch (whichBtn) {
                            case BUTTON_CONFIRM:
                                prefs.cleanAll();
                                mCache.remove("GetPayCategory");
                                mCache.remove("UserInfo");
                                mCache.remove(Constant.SHOP_EN_CODE);
                                mCache.remove(Constant.POS_EN_CODE);
                                Skip.mNext(mActivity, LoginActivity.class, true);
                                break;
                            case BUTTON_CANCEL:
                                prefs.cleanAll();
                                mCache.remove("GetPayCategory");
                                mCache.remove("UserInfo");
                                mCache.remove(Constant.SHOP_EN_CODE);
                                mCache.remove(Constant.POS_EN_CODE);
                                Skip.mNext(mActivity, LoginActivity.class, true);
                                break;
                            default:
                                break;
                        }
                    }
                })
                .create().show();
    }

}
