package com.cesaas.android.pos.storedvalue.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cesaas.android.pos.R;
import com.cesaas.android.pos.activity.cashier.CashierHomeActivity;
import com.cesaas.android.pos.base.BaseActivity;
import com.cesaas.android.pos.custom.CircleImageView;
import com.cesaas.android.pos.listener.OnItemClickListener;
import com.cesaas.android.pos.net.xutils.net.value.CheckUnitNet;
import com.cesaas.android.pos.net.xutils.net.value.GetOneNet;
import com.cesaas.android.pos.net.xutils.net.value.RuleListNet;
import com.cesaas.android.pos.storedvalue.adapter.StoredValueAmountListAdapter;
import com.cesaas.android.pos.storedvalue.bean.ResultCheckUnitBean;
import com.cesaas.android.pos.storedvalue.bean.ResultRuleListBean;
import com.cesaas.android.pos.storedvalue.bean.ResultVipInfoBean;
import com.cesaas.android.pos.utils.DecimalFormatUtils;
import com.cesaas.android.pos.utils.MClearEditText;
import com.cesaas.android.pos.utils.Skip;
import com.cesaas.android.pos.utils.ToastUtils;
import com.cesaas.android.pos.view.SwipeBackLayout;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.zhl.cbdialog.CBDialogBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import scanner.CaptureActivity;

/**
 * 储值会员信息
 */
public class VipInfoActivity extends BaseActivity implements View.OnClickListener{

    private TextView tvVipName,tvVipLevel,tvBalance,tvAmount,tvDonationAmount,tvStoredValueRecord,tv_not_rule;
    private CircleImageView ivwUserIcon;
    private LinearLayout llBack;
    private SwipeMenuRecyclerView rvGridView;
    private SwipeBackLayout swipeBackLayout;

    private List<ResultRuleListBean.RuleListBean> valueAmountListBeen;
    private StoredValueAmountListAdapter valueAmountListAdapter;

    private String vipMobile;
    private String vipId;
    private int shopId;
    private String title;
    private double DonationRate;
    private double BegAmount;
    private double  EndAmount;
    private double DonationAmount;
    private double UsableBalance;//可用余额
    private double GivenBalance;//赠送余额
    private double TotalBalance;//总余额
    private int Type;
    private int id;
    private BaseDialog baseDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_info);

        //通过EventBus订阅事件
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            vipMobile=bundle.getString("vipMobile");
        }
        initView();
        initData();
    }

    private void initData() {
        if(vipMobile!=null){
            //查询会员
            GetOneNet oneNet=new GetOneNet(mContext);
            oneNet.setData(vipMobile);
        }
    }

    private void initView() {
        tv_not_rule=(TextView) findViewById(R.id.tv_not_rule);
        tvVipName= (TextView) findViewById(R.id.tv_vip_name);
        tvVipLevel= (TextView) findViewById(R.id.tv_vip_level);
        tvBalance= (TextView) findViewById(R.id.tv_balance_amount);
        tvAmount= (TextView) findViewById(R.id.tv_amount);
        tvDonationAmount= (TextView) findViewById(R.id.tv_donation_amount);
        ivwUserIcon= (CircleImageView) findViewById(R.id.ivw_user_icon);
        llBack= (LinearLayout) findViewById(R.id.ll_back);
        llBack.setOnClickListener(this);
        tvStoredValueRecord= (TextView) findViewById(R.id.tv_stored_value_record);
        tvStoredValueRecord.setOnClickListener(this);

        rvGridView= (SwipeMenuRecyclerView) findViewById(R.id.rv_grid_view);
        rvGridView.setLayoutManager(new GridLayoutManager(mContext, 3));// 布局管理器。

        swipeBackLayout=(SwipeBackLayout)findViewById(R.id.swipe_back_layout);
        swipeBackLayout.setSwipeBackListener(new SwipeBackLayout.SwipeBackFinishActivityListener(this));

    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(ResultVipInfoBean bean) {
        if(bean.isSuccess()!=false){
            if(bean.TModel!=null){
                vipId=bean.TModel.getVipId();
                shopId=bean.TModel.getShopId();
                tvVipName.setText(bean.TModel.getVipName());
                tvVipLevel.setText(bean.TModel.getVipGrade());
                tvAmount.setText("￥"+bean.TModel.getAmount());
                tvDonationAmount.setText("￥"+bean.TModel.getDonationAmount());
                double balance=bean.TModel.getAmount()+bean.TModel.getDonationAmount();
                tvBalance.setText("￥"+balance);

                UsableBalance=bean.TModel.getAmount();
                GivenBalance=bean.TModel.getDonationAmount();
                TotalBalance=balance;

                if(bean.TModel.getVipIcon()!=null && !"".equals(bean.TModel.getVipIcon())){
                    Glide.with(mContext).load(bean.TModel.getVipIcon()).into(this.ivwUserIcon);
                }else {
                    ivwUserIcon.setImageResource(R.mipmap.vip2);
                }
                //店铺储值配置检查
                CheckUnitNet checkUnitNet=new CheckUnitNet(mContext);
                checkUnitNet.setData();

            }else{
                ToastUtils.getLongToast(mContext,"找不到该会员信息！");
            }
        }else{
            showCheckUnit(bean.getMessage(),vipMobile);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(ResultCheckUnitBean bean) {
        if(bean.isSuccess()!=false){
            //查询充值规则列表
            RuleListNet ruleListNet=new RuleListNet(mContext);
            ruleListNet.setData(vipId);
        }else{
            showCheckUnit(bean.getMessage(),vipMobile);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(ResultRuleListBean bean) {
        if(bean.isSuccess()!=false){
            if(bean.TModel.size()!=0){
                valueAmountListBeen=new ArrayList<>();
                valueAmountListBeen.addAll(bean.TModel);
                valueAmountListAdapter=new StoredValueAmountListAdapter(valueAmountListBeen,mContext);
                valueAmountListAdapter.setOnItemClickListener(onItemClickListener);
                rvGridView.setAdapter(valueAmountListAdapter);
            }else{
                tv_not_rule.setVisibility(View.VISIBLE);
            }
        }else{
            tv_not_rule.setVisibility(View.VISIBLE);
            ToastUtils.getLongToast(mContext,"Message:"+bean.getMessage());
        }
    }

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {

            title=valueAmountListBeen.get(position).getTitle();
            BegAmount=valueAmountListBeen.get(position).getBegAmount();
            EndAmount=valueAmountListBeen.get(position).getEndAmount();
            DonationRate=valueAmountListBeen.get(position).getDonationRate();
            Type=valueAmountListBeen.get(position).getType();
            id=valueAmountListBeen.get(position).getId();
            DonationAmount=valueAmountListBeen.get(position).getDonationAmount();

            if(valueAmountListBeen.get(position).getType()==0){//固定金额
                bundle.putString("vipMobile",vipMobile);
                bundle.putDouble("BegAmount",BegAmount);
                bundle.putDouble("DonationAmount",DonationAmount);
                bundle.putDouble("TotalBalance",TotalBalance);
                bundle.putDouble("UsableBalance",UsableBalance);
                bundle.putDouble("GivenBalance",GivenBalance);
                bundle.putString("title",title);
                bundle.putInt("shopId",shopId);
                bundle.putString("vipId",vipId);
                bundle.putInt("Type",Type);
                bundle.putInt("id",id);
                Skip.mNextFroData(mActivity,PaymentTypeActivity.class,bundle);

            }else if(valueAmountListBeen.get(position).getType()==1){//比例
                baseDialog = new BaseDialog(mContext);
                baseDialog.mInitShow();
                baseDialog.setCancelable(false);
            }else{//其他金额
                baseDialog = new BaseDialog(mContext);
                baseDialog.mInitShow();
                baseDialog.setCancelable(false);
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_back:
                Skip.mBack(mActivity);
                break;
            case R.id.tv_stored_value_record:
                bundle.putString("vipId",vipId);
                Skip.mNextFroData(mActivity,GetTradeListActivity.class,bundle);
                break;
        }
    }

    public EditText etValue;
    private TextView tvTitle;
    public class BaseDialog extends Dialog implements View.OnClickListener {

        TextView tvCancel;
        TextView tvSure;

        private String inviteCode;

        public BaseDialog(Context context) {
            this(context, R.style.dialog);
        }

        public BaseDialog(Context context, int dialog) {
            super(context, dialog);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            setContentView(R.layout.dialog_random_anount);

            tvTitle= (TextView) findViewById(R.id.tv_title);
            etValue = (EditText) findViewById(R.id.et_value);
            tvSure = (TextView) findViewById(R.id.tv_sure);
            tvSure.setOnClickListener(this);
            tvCancel = (TextView) findViewById(R.id.tv_cancel);
            tvCancel.setOnClickListener(this);

            tvTitle.setText(title);
        }

        public void mInitShow() {
            show();
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_cancel:
                    dismiss();
                    break;
                case R.id.tv_sure:
                    inviteCode = etValue.getText().toString().trim();
                    double DonationAmounts;
                    if (!TextUtils.isEmpty(inviteCode)) {
                        double scaleAmoun=Double.parseDouble(inviteCode);
                        bundle.putString("vipMobile",vipMobile);
                        bundle.putString("title",title);
                        bundle.putInt("shopId",shopId);
                        bundle.putString("vipId",vipId);
                        bundle.putInt("Type",Type);
                        bundle.putInt("id",id);
                        bundle.putDouble("TotalBalance",TotalBalance);
                        bundle.putDouble("UsableBalance",UsableBalance);
                        bundle.putDouble("GivenBalance",GivenBalance);
                        if(BegAmount!=0){
                            if(scaleAmoun>=BegAmount){
                                DonationAmounts= DecimalFormatUtils.decimalFormatRounds(scaleAmoun/DonationRate);
                                bundle.putDouble("DonationAmount",DonationAmounts);
                            }else{
                                bundle.putDouble("DonationAmount",DonationAmount);
                            }
                        }else{
                            bundle.putDouble("DonationAmount",DonationAmount);
                        }

                        if(Type==2){
                            baseDialog.dismiss();
                            bundle.putDouble("BegAmount",Double.parseDouble(inviteCode));
                            Skip.mNextFroData(mActivity,PaymentTypeActivity.class,bundle);
                        }else if(Type==1){
                            if(scaleAmoun>=BegAmount){
                                baseDialog.dismiss();
                                bundle.putDouble("BegAmount",scaleAmoun);
                                Skip.mNextFroData(mActivity,PaymentTypeActivity.class,bundle);
                            }else{
                                ToastUtils.getLongToast(mContext,"输入的金额不符合条件！");
                            }
                        }

                    } else {
                        ToastUtils.getLongToast(mContext,"请输入需要充值的金额！");
                    }
                    break;
            }
        }
    }

    private void showCheckUnit(String msg,String vipMobile){
        new CBDialogBuilder(mContext)
                .setTouchOutSideCancelable(true)
                .showCancelButton(true)
                .setTitle("温馨提示！")
                .setMessage(vipMobile+":\n"+msg)
                .setConfirmButtonText("确定")
                .setCancelButtonText("取消")
                .setDialogAnimation(CBDialogBuilder.DIALOG_ANIM_SLID_BOTTOM)
                .setButtonClickListener(true, new CBDialogBuilder.onDialogbtnClickListener() {
                    @Override
                    public void onDialogbtnClick(Context context, Dialog dialog, int whichBtn) {
                        switch (whichBtn) {
                            case BUTTON_CONFIRM:
                                finish();
                                break;
                            case BUTTON_CANCEL:
                                finish();
                                ToastUtils.show("已取消查询改会员！");
                                break;
                            default:
                                break;
                        }
                    }
                })
                .create().show();
    }
}
