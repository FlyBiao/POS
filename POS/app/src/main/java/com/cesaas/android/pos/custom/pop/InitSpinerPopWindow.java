package com.cesaas.android.pos.custom.pop;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cesaas.android.pos.R;


/**
 * Author FGB
 * Description 初始化  SpinerPopWindow
 * Created at 2017/7/1 15:08
 * Version 1.0
 */

public class InitSpinerPopWindow {

    private static Context ct;
    private static TextView selectTextView;

    public InitSpinerPopWindow(Context ct, TextView selectTextView){
        this.ct=ct;
        this.selectTextView=selectTextView;
    }

    /**
     * 显示PopupWindow
     */
    public View.OnClickListener  showPopupWindow(final SpinerPopWindow<String> mSpinerPopWindow){
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
//                    case R.id.tv_select_play_type:
//                        mSpinerPopWindow.setWidth(selectTextView.getWidth());
//                        mSpinerPopWindow.showAsDropDown(selectTextView);
//                        setTextImage(R.mipmap.arrow_top);
//                        break;
                    case R.id.tv_select_value:
                        mSpinerPopWindow.setWidth(selectTextView.getWidth());
                        mSpinerPopWindow.showAsDropDown(selectTextView);
                        setTextImage(R.mipmap.top_arrows);
                        break;
                }
            }
        };

        return clickListener;
    }

    /**
     * 监听popupwindow取消
     */
    public static PopupWindow.OnDismissListener dismissListener=new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
            setTextImage(R.mipmap.down_arrow);
        }
    };

    /**
     * 给TextView右边设置图片
     * @param resId
     */
    public static void setTextImage(int resId) {
        Drawable drawable = ct.getResources().getDrawable(resId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(),drawable.getMinimumHeight());// 必须设置图片大小，否则不显示
        selectTextView.setCompoundDrawables(null, null, drawable, null);
    }

}
