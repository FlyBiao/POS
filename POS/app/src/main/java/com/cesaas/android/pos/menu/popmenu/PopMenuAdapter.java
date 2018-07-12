package com.cesaas.android.pos.menu.popmenu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cesaas.android.pos.R;

import java.util.ArrayList;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：
 * 创建日期：2016/11/3 10:49
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class PopMenuAdapter extends BaseAdapter {

    ArrayList<String> itemList;
    Context mcon;

    public PopMenuAdapter(Context mcon,ArrayList<String> itemList){
        this.mcon=mcon;
        this.itemList=itemList;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mcon).inflate(
                    R.layout.showpoplist, null);
            holder = new ViewHolder();
            convertView.setTag(holder);

            holder.groupItem = (TextView) convertView
                    .findViewById(R.id.textview);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.groupItem.setText(itemList.get(position));

        return convertView;
    }

    private final class ViewHolder {
        TextView groupItem;
    }
}
