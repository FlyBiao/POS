package com.cesaas.android.pos.base;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

import com.cesaas.android.pos.R;


/**
 * ================================================
 * 作    者：FGB
 * 描    述：对话框基类
 * 创建日期：2016/6/20 09:32
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class BaseDialog extends Dialog implements View.OnClickListener {
    public Context mContext;

    public BaseDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public BaseDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
    }

    public BaseDialog(Context context, int theme, boolean wins) {
        super(context, theme);
        this.mContext = context;
        if (wins) {
            Window window = getWindow();
            window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
            window.setWindowAnimations(R.style.DialogStyle); // 添加动画
        }
    }

    public void mShow() {
        if (!isShowing()) {
            try {
                show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void mDismiss() {
        dismiss();
    }

    @Override
    public void onClick(View v) {
    }
}
