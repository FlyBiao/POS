package com.cesaas.android.pos.custom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.activity.cashier.CashierHomeActivity;
import com.cesaas.android.pos.activity.cashier.WeiXinAndAliPaySingleActivity;
import com.cesaas.android.pos.bean.CashPayBean;
import com.cesaas.android.pos.bean.PayBean;
import com.cesaas.android.pos.bean.UserInfoBean;
import com.cesaas.android.pos.custom.adapter.PayGridViewAdapter;
import com.cesaas.android.pos.gridview.MyGridView;
import com.cesaas.android.pos.net.xutils.net.CreateCashPayNet;
import com.cesaas.android.pos.net.xutils.net.CreatePayNet;
import com.cesaas.android.pos.pos.ResultPayCategoryBean;
import com.cesaas.android.pos.utils.AbDateUtil;
import com.cesaas.android.pos.utils.RandomUtils;
import com.cesaas.android.pos.utils.Skip;
import com.cesaas.android.pos.utils.ToastUtils;
import com.zhl.cbdialog.CBDialogBuilder;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Author FGB
 * Description
 * Created at 2017/10/26 13:50
 * Version 1.0
 */

public class PayDialog {

    private Context mContext;
    private Activity mActivity;
    private double amount;
    private Bundle bundle;
    private List<ResultPayCategoryBean.PayCategoryBea> categoryBeaList;
    private UserInfoBean.User user;

    private List<String> menuNames;//菜单名称
    private List<Integer> menuImages;//菜单图片

    private String traceAuditNumber;
    private String referenceNumber;
    private int IsPractical;
    private int payType;
    private double singleCashierMoney;

    private Dialog bottomDialog;
    private View dialogContentView;

    private MyGridView gv_pay_category;

    public PayDialog(Context mContext, Activity mActivity, List<ResultPayCategoryBean.PayCategoryBea> categoryBeaList, Bundle bundle, double amount, UserInfoBean.User user){
        this.mContext=mContext;
        this.mActivity=mActivity;
        this.categoryBeaList=categoryBeaList;
        this.amount=amount;
        this.bundle=bundle;
        this.user=user;
    }

    public void showDialog(){
        bottomDialog = new Dialog(mContext, R.style.BottomDialog);
        dialogContentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_content_normal, null);
        bottomDialog.setContentView(dialogContentView);
        ViewGroup.LayoutParams layoutParams = dialogContentView.getLayoutParams();
        layoutParams.width = mContext.getResources().getDisplayMetrics().widthPixels;
        dialogContentView.setLayoutParams(layoutParams);

        initView();
        initData();

        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.setCanceledOnTouchOutside(true);
        bottomDialog.show();
    }

    private void initData() {
        menuNames=new ArrayList<>();
        menuImages=new ArrayList<>();
        for (int i=0;i<categoryBeaList.size();i++){
            menuNames.add(categoryBeaList.get(i).getDescription());
            switch (categoryBeaList.get(i).getCategoryType()){
                case 2:
                    menuImages.add(R.mipmap.weixin);
                    break;
                case 3:
                    menuImages.add(R.mipmap.alipay);
                    break;
                case 4:
                    menuImages.add(R.mipmap.unionpay);
                    break;
                case 5:
                    menuImages.add(R.mipmap.cash);
                    break;
                default:
                    menuImages.add(R.mipmap.pos_pay);
                    break;
            }
        }
        gv_pay_category.setAdapter(new PayGridViewAdapter(mContext,menuNames,menuImages));
        gv_pay_category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                switch (categoryBeaList.get(position).getCategoryType()) {
                    case 2://微信支付
                        if (amount!=0) {//折后价格非等于null
                            bundle.putDouble("PayMoney", amount);

                        } else {//折后金额为空
                            bundle.putDouble("PayMoney", amount);
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
                        bundle.putDouble("originalPrice", amount);
                        bundle.putDouble("discountAfter", amount);
                        bundle.putString("mobile", user.getMobile());
                        bundle.putBoolean("isSuccess", true);
                        Skip.mNextFroData(mActivity, WeiXinAndAliPaySingleActivity.class, bundle);
                        bottomDialog.dismiss();
                        break;
                    case 3://支付宝
                        if (amount!=0) {//折后价格非等于null
                            bundle.putDouble("PayMoney", amount);

                        } else {//折后金额为空
                            bundle.putDouble("PayMoney", amount);
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
                        bundle.putDouble("originalPrice", amount);
                        bundle.putDouble("discountAfter", amount);
                        bundle.putString("mobile", user.getMobile());
                        bundle.putBoolean("isSuccess", true);
                        Skip.mNextFroData(mActivity, WeiXinAndAliPaySingleActivity.class, bundle);
                        bottomDialog.dismiss();
                        break;
                    case 4://银联支付
                        PayBean payBean=new PayBean();
                        payBean.setPayType(4);
                        EventBus.getDefault().post(payBean);
                        bottomDialog.dismiss();
                        break;
                    case 5://现金支付
                        ToastUtils.getLongToast(mContext,"请选择其他方式支付！");
//                        new CBDialogBuilder(mContext)
//                                .setTouchOutSideCancelable(true)
//                                .showCancelButton(true)
//                                .setTitle("温馨提示！")
//                                .setMessage("请确认该订单是否使用现金支付？")
//                                .setConfirmButtonText("确定")
//                                .setCancelButtonText("取消")
//                                .setDialogAnimation(CBDialogBuilder.DIALOG_ANIM_SLID_BOTTOM)
//                                .setButtonClickListener(true, new CBDialogBuilder.onDialogbtnClickListener() {
//                                    @Override
//                                    public void onDialogbtnClick(Context context, Dialog dialog, int whichBtn) {
//                                        switch (whichBtn) {
//                                            case BUTTON_CONFIRM:
//                                                IsPractical=categoryBeaList.get(position).getIsPractical();
//                                                payType = categoryBeaList.get(position).getCategoryType();
//                                                if (amount!=0) {//折后价格非等于null
//                                                    singleCashierMoney = amount;
//
//                                                } else {//折后金额为空
//                                                    singleCashierMoney = amount;
//                                                }
//                                                //随机生成12位参考号【规则：当前时间+2位随机数】
//                                                referenceNumber = RandomUtils.getCurrentTimeAsNumber() + RandomUtils.getToFourRandom();
//                                                //随机生成6位凭证号【规则：当月+4位随机数】
//                                                traceAuditNumber = RandomUtils.getCurrentTimeMM() + RandomUtils.getFourRandom();
//                                                //getPayListener(referenceNumber, traceAuditNumber, singleCashierMoney, orderNo, 5, "notFansVip",IsPractical);
//
//                                                double money=amount;
//                                                //订单号生成规则：LS+VipId+MMddHHmm+4位随机数
//                                                String orderNo="LS"+user.getVipId()+ AbDateUtil.getCurrentTimeAsNumber()+RandomUtils.getFourRandom();
//                                                CashPayBean cashPayBean=new CashPayBean();
//                                                cashPayBean.setPayType(payType);
//                                                cashPayBean.setIsPractical(IsPractical);
//                                                cashPayBean.setMoney(money);
//                                                cashPayBean.setOrderNo(orderNo);
//                                                cashPayBean.setReferenceNumber(referenceNumber);
//                                                cashPayBean.setTraceAuditNumber(traceAuditNumber);
//                                                EventBus.getDefault().post(cashPayBean);
//                                                bottomDialog.dismiss();
//                                                bottomDialog.cancel();
//                                               // CreateCashPayNet createCashPayNet=new CreateCashPayNet(mContext);
//                                                //createCashPayNet.setData(cashPayBean.getOrderNo(),cashPayBean.getMoney(),5,cashPayBean.getReferenceNumber(),cashPayBean.getTraceAuditNumber(),"","独立收银",cashPayBean.getOrderNo(),user.getShopId(),"",2,100);
//                                                break;
//                                            case BUTTON_CANCEL:
//                                                ToastUtils.show("已取消支付");
//                                                break;
//                                        }
//                                    }
//                                })
//                                .create().show();
                        break;

                }
            }
        });
    }

    private void initView(){
        gv_pay_category= (MyGridView) dialogContentView.findViewById(R.id.gv_pay_category);
    }
}
