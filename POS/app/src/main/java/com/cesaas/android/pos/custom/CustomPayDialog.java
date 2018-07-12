package com.cesaas.android.pos.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.cesaas.android.pos.R;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：自定义支付Dialog
 * 创建日期：2016/10/28 14:45
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class CustomPayDialog extends Dialog implements View.OnClickListener{
    int layoutRes;//布局文件
    Context context;
    public CustomPayDialog(Context context) {
        super(context);
        this.context = context;
    }
    /**
     * 自定义布局的构造方法
     * @param context
     * @param resLayout
     */
    public CustomPayDialog(Context context, int resLayout){
        super(context);
        this.context = context;
        this.layoutRes=resLayout;
    }

    /**
     * 自定义收银主题及布局的构造方法
     * @param context
     * @param theme
     * @param resLayout
     */
    public CustomPayDialog(Context context, int theme,int resLayout){
        super(context, theme);
        this.context = context;
        this.layoutRes=resLayout;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layoutRes);
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_weixin_pay://微信支付

                break;
            case R.id.ll_ali_pay://支付宝

                break;
            case R.id.ll_union_pay://银联支付

                break;
            case R.id.ll_cash_pay://现金支付

                break;
            default:
                break;
        }
    }
}
