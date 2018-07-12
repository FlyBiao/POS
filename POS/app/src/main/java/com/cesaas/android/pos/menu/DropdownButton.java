package com.cesaas.android.pos.menu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cesaas.android.pos.R;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：
 * 创建日期：2016/10/14 14:18
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class DropdownButton extends RelativeLayout {
    TextView textView;
    View bottomLine;

    public DropdownButton(Context context) {
        this(context, null);
    }

    public DropdownButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropdownButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view =  LayoutInflater.from(getContext()).inflate(R.layout.dropdown_tab_button,this, true);
        textView = (TextView) view.findViewById(R.id.textView);
        bottomLine = view.findViewById(R.id.bottomLine);
    }


    public void setText(CharSequence text) {
        textView.setText(text);
    }

    public void setChecked(boolean checked) {
        Drawable icon;
        if (checked) {
            icon = getResources().getDrawable(R.mipmap.ic_dropdown_actived);
            textView.setTextColor(getResources().getColor(R.color.green));
            bottomLine.setVisibility(VISIBLE);
        } else {
            icon = getResources().getDrawable(R.mipmap.ic_dropdown_normal);
            textView.setTextColor(getResources().getColor(R.color.drop_down_unselected));
            bottomLine.setVisibility(GONE);
        }
        textView.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null);
    }


}
