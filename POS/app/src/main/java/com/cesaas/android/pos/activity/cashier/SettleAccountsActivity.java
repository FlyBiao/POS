package com.cesaas.android.pos.activity.cashier;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.base.BaseActivity;
import com.cesaas.android.pos.bean.SettleAccountsBean;
import com.cesaas.android.pos.bean.SettleAccountsList;
import com.cesaas.android.pos.bean.printer.LatticePrinterSettleBean;
import com.cesaas.android.pos.global.Urls;
import com.cesaas.android.pos.net.nohttp.HttpListener;
import com.cesaas.android.pos.utils.AbDateUtil;
import com.cesaas.android.pos.utils.DecimalFormatUtils;
import com.cesaas.android.pos.utils.SettlePrinterTools;
import com.cesaas.android.pos.utils.SingleCashierPrinterTools;
import com.cesaas.android.pos.utils.Skip;
import com.cesaas.android.pos.utils.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wangpos.pay.UnionPay.PosConfig;
import com.wangpos.poscore.IPosCallBack;
import com.wangpos.poscore.PosCore;
import com.wangpos.poscore.impl.PosCoreFactory;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;
import com.zaaach.toprightmenu.MenuItem;
import com.zaaach.toprightmenu.TopRightMenu;
import com.zhl.cbdialog.CBDialogBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.weipass.pos.sdk.IPrint;
import cn.weipass.pos.sdk.LatticePrinter;
import cn.weipass.pos.sdk.impl.WeiposImpl;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：POS对账结算页面
 * 创建日期：2016/10/28
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class SettleAccountsActivity extends BaseActivity implements View.OnClickListener{

    private RecyclerView mRecyclerView;
    private LinearLayout ll_settle_back;
    private TextView tv_not_data,tv_consume_amount,tv_order_amount,tv_choose,tv_pos_settle,tv_order_refund_amount;

    ArrayList<SettleAccountsList> settleAccountsList=new ArrayList<>();
    private SettleAccountsAdapter adapter;
    private double payMent=0.0;
    private double refundAmount;//退款金额
    private int payOrderCount;

    //8583协议中的参考号
    private String refNum=null;
    private PosCore pCore;

    private String userName;
    private String shopName;
    private String startDate=AbDateUtil.getCurrentDate("yyyy-MM-dd");//开始时间
    private String endData=AbDateUtil.getCurrentDate("yyyy-MM-dd");//结束时间

    private TopRightMenu mTopRightMenu;

    private LatticePrinter latticePrinter;// 点阵打印
    private LatticePrinterSettleBean latticePrinterBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settle_accounts);
        initView();
        Bundle bundle=getIntent().getExtras();
        if (bundle!=null){
            shopName=bundle.getString("shopName");
            userName=bundle.getString("userName");
        }

        getStatisticsListener(AbDateUtil.getCurrentDate("yyyy-MM-dd 00:00:00"),AbDateUtil.getCurrentDate("yyyy-MM-dd 23:59:59"));

//        initPosCore();
    }

    public void initView(){
        tv_pos_settle= (TextView) findViewById(R.id.tv_pos_settle);
        tv_order_amount= (TextView) findViewById(R.id.tv_order_amount);
        tv_consume_amount= (TextView) findViewById(R.id.tv_consume_amount);
        tv_order_refund_amount= (TextView) findViewById(R.id.tv_order_refund_amount);
        tv_not_data= (TextView) findViewById(R.id.tv_not_settle_data);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
        ll_settle_back= (LinearLayout) findViewById(R.id.ll_settle_back);
        tv_choose= (TextView) findViewById(R.id.tv_choose);
        tv_choose.setOnClickListener(this);
        ll_settle_back.setOnClickListener(this);
        tv_pos_settle.setOnClickListener(this);
    }

    /**
     * POS收银数据统计
     * 注：默认查询当天数据
     */
    public void getStatisticsListener(String start_date,String end_date){
        Request<String> request = NoHttp.createStringRequest(Urls.POS_STATISTICS, RequestMethod.POST);
        request.add("start_date", start_date);
        request.add("end_date",end_date);
        commonNet.requestNetTask(request,getStatisticsListener);
    }

    /**
     * POS收银数据统计回调
     */
    private HttpListener<String> getStatisticsListener = new HttpListener<String>()
    {
        @Override
        public void onSucceed(int what, Response<String> response)
        {
            SettleAccountsBean bean=gson.fromJson(response.get(),SettleAccountsBean.class);

            if(bean.isSuccess()!=false){
                if(bean.TModel!=null && bean.TModel.size()!=0){
                    settleAccountsList=new ArrayList<SettleAccountsList>();
                    tv_not_data.setVisibility(View.GONE);
                    settleAccountsList.addAll(bean.TModel);

                    payMent=0.0;
                    refundAmount=0.0;
                    payOrderCount=0;

                    for (int i=0;i<settleAccountsList.size();i++){
                        if(settleAccountsList.get(i).getPayCategory()==0){
                            payMent+=settleAccountsList.get(i).getPayMent();
                        }else{
                            refundAmount+=settleAccountsList.get(i).getPayMent();
                        }
                        payOrderCount+=settleAccountsList.get(i).getOrderCount();
                    }

                    tv_consume_amount.setText(DecimalFormatUtils.decimalToFormat(payMent));
                    tv_order_refund_amount.setText(DecimalFormatUtils.decimalToFormat(refundAmount));
                    tv_order_amount.setText(payOrderCount+"");

//                rl_settle_not_data.setVisibility(View.VISIBLE);

                }else{
                    tv_consume_amount.setText("￥0.00");
                    tv_order_amount.setText("￥0.00");
                    tv_order_refund_amount.setText("￥0.00");
                    tv_not_data.setVisibility(View.VISIBLE);
//                rl_settle_not_data.setVisibility(View.GONE);
                }
                initAdapter();
            }else{
                ToastUtils.getLongToast(mContext,"Msg："+bean.getMessage());
            }
        }
        @Override
        public void onFailed(int what, Response<String> response)
        {
            ToastUtils.show(response.get());
        }
    };

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_settle_back://返回
                Skip.mBack(mActivity);
                break;

            case R.id.tv_choose://筛选
                showMore();
                break;

            case R.id.tv_pos_settle://结算打印
                if(settleAccountsList.size()!=0){
                    new CBDialogBuilder(SettleAccountsActivity.this)
                            .setTouchOutSideCancelable(true)
                            .showCancelButton(true)
                            .setTitle("打印结算清单")
                            .setMessage("是否需要打印当前结算清单？")
                            .setConfirmButtonText("确定")
                            .setCancelButtonText("取消")
                            .setDialogAnimation(CBDialogBuilder.DIALOG_ANIM_SLID_BOTTOM)
                            .setButtonClickListener(true, new CBDialogBuilder.onDialogbtnClickListener() {
                                @Override
                                public void onDialogbtnClick(Context context, Dialog dialog, int whichBtn) {
                                    switch (whichBtn) {
                                        case BUTTON_CONFIRM:
                                            //打印对账单
                                            setLatticePrinter();
//                                            doPiJieSuan();
                                            break;
                                        case BUTTON_CANCEL:
                                            ToastUtils.show("已取打印结算清单");
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            })
                            .create().show();
                }else{
                    ToastUtils.getLongToast(mContext,"没有可结算账单！");
                }
                break;
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
                    Log.i("test","批结算:"+rPiJieSua);
                } catch (Exception e) {
                    Log.i("test","批结算e:"+e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showMore(){
        mTopRightMenu = new TopRightMenu(SettleAccountsActivity.this);
        List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem(R.mipmap.today,"今天"));
        menuItems.add(new MenuItem( R.mipmap.yesterday,"昨天"));
        menuItems.add(new MenuItem(R.mipmap.week,"本周"));
        mTopRightMenu
                .setHeight(480)     //默认高度480
                .setWidth(320)      //默认宽度wrap_content
                .showIcon(true)     //显示菜单图标，默认为true
                .dimBackground(true)           //背景变暗，默认为true
                .needAnimationStyle(true)   //显示动画，默认为true
                .setAnimationStyle(R.style.TRM_ANIM_STYLE)  //默认为R.style.TRM_ANIM_STYLE
                .addMenuList(menuItems)
                .addMenuItem(new MenuItem( R.mipmap.month,"本月"))
                .setOnMenuItemClickListener(new TopRightMenu.OnMenuItemClickListener() {
                    @Override
                    public void onMenuItemClick(int position) {
                        switch (position){
                            case 0://今日
                                if(settleAccountsList.size()!=0){
                                    settleAccountsList.clear();
                                }
                                getStatisticsListener(AbDateUtil.getCurrentDate("yyyy-MM-dd 00:00:00"),AbDateUtil.getCurrentDate("yyyy-MM-dd 23:59:59"));
                                startDate=AbDateUtil.getCurrentDate("yyyy-MM-dd");
                                endData=AbDateUtil.getCurrentDate("yyyy-MM-dd");
                                break;
                            case 1://昨天
                                if(settleAccountsList.size()!=0){
                                    settleAccountsList.clear();
                                }
                                getStatisticsListener(AbDateUtil.YesTerDay("yyyy-MM-dd 00:00:00"),AbDateUtil.getCurrentDate("yyyy-MM-dd"));
                                startDate=AbDateUtil.YesTerDay("yyyy-MM-dd");
                                endData=AbDateUtil.getCurrentDate("yyyy-MM-dd");
                                break;
                            case 2://本周
                                if(settleAccountsList.size()!=0){
                                    settleAccountsList.clear();
                                }
                                getStatisticsListener(AbDateUtil.getFirstDayOfWeek("yyyy-MM-dd 00:00:00"),AbDateUtil.getLastDayOfWeek("yyyy-MM-dd 23:59:59"));
                                startDate=AbDateUtil.getFirstDayOfWeek("yyyy-MM-dd");
                                endData=AbDateUtil.getLastDayOfWeek("yyyy-MM-dd");
                                break;
                            case 3://本月
                                if(settleAccountsList.size()!=0){
                                    settleAccountsList.clear();
                                }
                                getStatisticsListener(AbDateUtil.getFirstDayOfMonth("yyyy-MM-dd 00:00:00"),AbDateUtil.getLastDayOfMonth("yyyy-MM-dd 23:59:59"));
                                startDate=AbDateUtil.getFirstDayOfMonth("yyyy-MM-dd");
                                endData=AbDateUtil.getLastDayOfMonth("yyyy-MM-dd");
                                break;
                            default:
                                break;
                        }
                    }
                })
                .showAsDropDown(tv_choose, -225, 0);
//                        .showAsDropDown(moreBtn);
    }

    /**
     * 设置点阵打印方法
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
            Toast.makeText(SettleAccountsActivity.this, "尚未初始化点阵打印sdk，请稍后再试", Toast.LENGTH_SHORT).show();
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

            //以下是设置pos打印信息
            latticePrinterBean=new LatticePrinterSettleBean();
            latticePrinterBean.setShopName(shopName);
            latticePrinterBean.setShopClerkName(userName);
            latticePrinterBean.setCurrentTurnover(Double.parseDouble(DecimalFormatUtils.decimalToFormat(payMent)));
            latticePrinterBean.setPayOrderCount(payOrderCount);
            latticePrinterBean.setRefundAmount(Double.parseDouble(DecimalFormatUtils.decimalToFormat(refundAmount)));

            SettlePrinterTools.printLattice(mContext,latticePrinter,latticePrinterBean,settleAccountsList,startDate,endData);
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
        builder.setPositiveButton("确认", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 初始化数据适配器
     */
    public void initAdapter(){
        adapter=new SettleAccountsAdapter(R.layout.item_settle_accounts,settleAccountsList);
        adapter.openLoadAnimation();
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
    }

    /**
     * 结算Adapter
     */
    public class SettleAccountsAdapter extends BaseQuickAdapter<SettleAccountsList> {
        public SettleAccountsAdapter(int layoutResId, List<SettleAccountsList> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, SettleAccountsList bean) {

            if(bean.getPayType()==2){//微信支付
                baseViewHolder.setImageResource(R.id.iv_settle_title,R.mipmap.weixin);
                baseViewHolder.setText(R.id.tv_pay_way,"微信支付");
                if(bean.getPayBizType()==13){//退款
                    baseViewHolder.setText(R.id.tv_refund_amount,DecimalFormatUtils.decimalToFormat(bean.getPayMent()));
                }else{
                    baseViewHolder.setText(R.id.tv_refund_amount,"暂无退款");
                    ((TextView)baseViewHolder.getView(R.id.tv_refund_amount)).setTextColor(mContext.getResources().getColor(R.color.c5));
                }

            }else if(bean.getPayType()==3){//支付宝
                baseViewHolder.setImageResource(R.id.iv_settle_title,R.mipmap.alipay);
                baseViewHolder.setText(R.id.tv_pay_way,"支付宝支付");
                if(bean.getPayBizType()==13){//退款
                    baseViewHolder.setText(R.id.tv_refund_amount,DecimalFormatUtils.decimalToFormat(bean.getPayMent()));
                }else{
                    baseViewHolder.setText(R.id.tv_refund_amount,"暂无退款");
                    ((TextView)baseViewHolder.getView(R.id.tv_refund_amount)).setTextColor(mContext.getResources().getColor(R.color.c5));
                }

            }else if(bean.getPayType()==4){//银联
                baseViewHolder.setImageResource(R.id.iv_settle_title,R.mipmap.unionpay);
                baseViewHolder.setText(R.id.tv_pay_way,"银联支付");
                if(bean.getPayBizType()==13){//退款
                    baseViewHolder.setText(R.id.tv_refund_amount,DecimalFormatUtils.decimalToFormat(bean.getPayMent()));
                }else{
                    baseViewHolder.setText(R.id.tv_refund_amount,"暂无退款");
                    ((TextView)baseViewHolder.getView(R.id.tv_refund_amount)).setTextColor(mContext.getResources().getColor(R.color.c5));
                }

            }else if(bean.getPayType()==5){//现金
                baseViewHolder.setImageResource(R.id.iv_settle_title,R.mipmap.cash);
                baseViewHolder.setText(R.id.tv_pay_way,"现金支付");
                if(bean.getPayBizType()==13){//退款
                    baseViewHolder.setText(R.id.tv_refund_amount,DecimalFormatUtils.decimalToFormat(bean.getPayMent()));
                }else{
                    baseViewHolder.setText(R.id.tv_refund_amount,"暂无退款");
                    ((TextView)baseViewHolder.getView(R.id.tv_refund_amount)).setTextColor(mContext.getResources().getColor(R.color.c5));
                }
            }
            baseViewHolder.setText(R.id.tv_pay_order_count,bean.getOrderCount()+"单");
            baseViewHolder.setText(R.id.tv_cashier_pay_accounts,"￥"+DecimalFormatUtils.decimalToFormat(bean.getPayMent()));
            baseViewHolder.setText(R.id.tv_payment,"￥"+DecimalFormatUtils.decimalToFormat(bean.getPayMent()));

        }
    }

}
