package com.cesaas.android.pos.menu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.cesaas.android.pos.R;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：
 * 创建日期：2016/10/14 14:25
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class DropdownListItemView extends TextView {
    public DropdownListItemView(Context context) {
        this(context,null);
    }

    public DropdownListItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public DropdownListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void bind(CharSequence text,boolean checked){
        setText(text);
        if (checked){
            Drawable icon = getResources().getDrawable(R.mipmap.ic_task_status_list_check);
            setCompoundDrawablesWithIntrinsicBounds(null,null,icon,null);
        }else{
            setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
        }
    }


}
