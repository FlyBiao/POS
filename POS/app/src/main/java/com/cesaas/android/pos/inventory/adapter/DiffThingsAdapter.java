package com.cesaas.android.pos.inventory.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.cesaas.android.pos.R;
import com.cesaas.android.pos.inventory.bean.CheckInventoryDifferenceBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Author FGB
 * Description
 * Created at 2017/8/31 18:33
 * Version 1.0
 */

public class DiffThingsAdapter extends BaseAdapter {

    TextView tv_color,tv_size,tv_stock_number,tv_midway_number,tv_inventory_number,tv_diff_number;

    private Context context;
    public List<CheckInventoryDifferenceBean.Sku> skus=new ArrayList<>();

    public DiffThingsAdapter(List<CheckInventoryDifferenceBean.Sku> skus, Context context){
        this.skus=skus;
        this.context=context;
    }

    @Override
    public int getCount() {
        return skus.size();
    }

    @Override
    public Object getItem(int position) {
        return skus.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_diff_things, parent, false);
        tv_color=(TextView)convertView.findViewById(R.id.tv_color);
        tv_size=(TextView)convertView.findViewById(R.id.tv_size);
        tv_stock_number=(TextView)convertView.findViewById(R.id.tv_stock_number);
        tv_midway_number=(TextView)convertView.findViewById(R.id.tv_midway_number);
        tv_inventory_number=(TextView)convertView.findViewById(R.id.tv_inventory_number);
        tv_diff_number=(TextView)convertView.findViewById(R.id.tv_diff_number);

        tv_color.setText(skus.get(position).getSku1());
        tv_size.setText(skus.get(position).getSku2());
        tv_stock_number.setText(skus.get(position).getStock()+"");
        tv_midway_number.setText(skus.get(position).getOntheway()+"");
        tv_inventory_number.setText(skus.get(position).getGoodsNum()+"");
        tv_diff_number.setText(skus.get(position).getGoodsDiffNum()+"");

        return convertView;
    }
}
