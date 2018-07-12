package com.cesaas.android.pos.custom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.gridview.BaseViewHolder;

import java.util.List;

/**
 * Author FGB
 * Description
 * Created at 2017/10/26 14:28
 * Version 1.0
 */

public class PayGridViewAdapter  extends BaseAdapter {
    private Context mContext;

    private TextView tv;
    private ImageView iv;

    private List<String> menu;
    private List<Integer> imgs;

    public PayGridViewAdapter(Context mContext,List<String> menuName,List<Integer> imgs) {
        super();
        this.mContext = mContext;
        this.menu=menuName;
        this.imgs=imgs;

    }

    @Override
    public int getCount() {
        return menu.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.grid_item_pay, parent, false);
        }
        tv = BaseViewHolder.get(convertView, R.id.tv_item);
        iv = BaseViewHolder.get(convertView, R.id.iv_item);

        tv.setText(menu.get(position));
        iv.setImageResource(imgs.get(position));

        return convertView;
    }

}
