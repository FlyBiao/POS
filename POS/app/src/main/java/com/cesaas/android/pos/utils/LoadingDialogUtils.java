package com.cesaas.android.pos.utils;

import android.content.Context;
import android.graphics.Color;

import com.cesaas.android.pos.activity.cashier.CashierMainActivity;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

/**
 * Author FGB
 * Description
 * Created at 2017/12/7 14:03
 * Version 1.0
 */

public class LoadingDialogUtils {
    public static ZLoadingDialog dialog;

    public static void showLoadingDialog(Context ct,String msg){
        dialog= new ZLoadingDialog(ct);
        dialog.setLoadingBuilder(Z_TYPE.SNAKE_CIRCLE)//设置类型
        .setLoadingColor(Color.BLACK)//颜色
        .setHintText(msg)
        .setHintTextSize(14) // 设置字体大小 dp
        .setHintTextColor(Color.GRAY)// 设置字体颜色
        .setCanceledOnTouchOutside(false)
        .show();
    }

    public static void hideLoadingDialog(){
        dialog.dismiss();
    }
}
