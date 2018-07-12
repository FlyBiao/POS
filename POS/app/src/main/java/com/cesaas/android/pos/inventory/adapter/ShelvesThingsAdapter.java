package com.cesaas.android.pos.inventory.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.cesaas.android.pos.R;
import com.cesaas.android.pos.inventory.bean.GetOneInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Author FGB
 * Description
 * Created at 2017/8/31 18:33
 * Version 1.0
 */

public class ShelvesThingsAdapter extends BaseAdapter {

    TextView tv_color,tv_size,tv_number,tv_ShelvesId,tv_Id;

    private String code;
    private Context context;
    private List<GetOneInfoBean.Sku>  skuList=new ArrayList<>();

    public ShelvesThingsAdapter(List<GetOneInfoBean.Sku>  skuList, Context context){
        this.skuList=skuList;
        this.context=context;
    }

    @Override
    public int getCount() {
        return skuList.size();
    }

    @Override
    public Object getItem(int position) {
        return skuList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_shelves_things, parent, false);
        tv_color=(TextView)convertView.findViewById(R.id.tv_color);
        tv_size=(TextView)convertView.findViewById(R.id.tv_size);
        tv_number=(TextView)convertView.findViewById(R.id.tv_number);
        tv_ShelvesId= (TextView) convertView.findViewById(R.id.tv_ShelvesId);
        tv_Id= (TextView) convertView.findViewById(R.id.tv_Id);

        tv_color.setText(skuList.get(position).getSku1());
        tv_size.setText(skuList.get(position).getSku2());
        tv_number.setText(skuList.get(position).getGoodsNum()+"");

        return convertView;
    }


}
