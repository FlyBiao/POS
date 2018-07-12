package com.cesaas.android.pos.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

import com.cesaas.android.pos.activity.cashier.CashierMainActivity;
import com.cesaas.android.pos.activity.user.LoginActivity;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.utils.ACache;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.cesaas.android.pos.utils.Skip;
import com.cesaas.android.pos.utils.ToastUtils;
import com.zhl.cbdialog.CBDialogBuilder;

/**
 * Author FGB
 * Description
 * Created at 2017/10/26 13:36
 * Version 1.0
 */

public class BaseUtils {

    public static void exit(Context ct, final Activity activity, final ACache mCache,final AbPrefsUtil prefs){
        new CBDialogBuilder(ct)
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
                                mCache.remove("GetPayCategory");
                                mCache.remove("UserInfo");
                                mCache.remove(Constant.SHOP_EN_CODE);
                                mCache.remove(Constant.POS_EN_CODE);
                                Skip.mNext(activity, LoginActivity.class, true);
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
