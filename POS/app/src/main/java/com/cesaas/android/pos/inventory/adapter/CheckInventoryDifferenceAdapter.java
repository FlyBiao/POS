package com.cesaas.android.pos.inventory.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.inventory.bean.CheckInventoryDifferenceBean;
import com.cesaas.android.pos.listener.OnItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Author FGB
 * Description
 * Created at 2017/8/28 9:51
 * Version 1.0
 */

public class CheckInventoryDifferenceAdapter extends SwipeMenuAdapter<CheckInventoryDifferenceAdapter.DefaultViewHolder> {

    private List<CheckInventoryDifferenceBean.Sku> skus=new ArrayList<>();
    private List<CheckInventoryDifferenceBean> titles;
    private OnItemClickListener mOnItemClickListener;



    private static Context ct;
    private  Activity activity;

    public CheckInventoryDifferenceAdapter(List<CheckInventoryDifferenceBean> titles, Context ct, Activity activity) {
        this.titles = titles;
        this.ct=ct;
        this.activity=activity;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return titles == null ? 0 : titles.size();
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_check_inventory_diff, parent, false);
    }

    @Override
    public CheckInventoryDifferenceAdapter.DefaultViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        DefaultViewHolder viewHolder = new DefaultViewHolder(realContentView);
        viewHolder.mOnItemClickListener = mOnItemClickListener;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CheckInventoryDifferenceAdapter.DefaultViewHolder holder, final int position) {
        holder.setData(titles.get(position).getNum(),titles.get(position).getDiffNum(),titles.get(position).getStyleName(),titles.get(position).getStyleNo(),titles.get(position).getImageUrl());

        holder.ll_iv_more_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ll_iv_more_down.setVisibility(View.GONE);
                holder. ll_iv_more_top.setVisibility(View.VISIBLE);
                holder. ll_inventory_goods.setVisibility(View.VISIBLE);

            }
        });
        holder. ll_iv_more_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder. ll_iv_more_top.setVisibility(View.GONE);
                holder.ll_iv_more_down.setVisibility(View.VISIBLE);
                holder. ll_inventory_goods.setVisibility(View.GONE);
            }
        });

        CheckInventoryDifferenceBean bean=titles.get(position);
        skus=new ArrayList<>();
        for (int i=0;i<bean.Sku.size();i++){
            CheckInventoryDifferenceBean.Sku sku=new CheckInventoryDifferenceBean().new Sku();
            sku=bean.Sku.get(i);
            skus.add(sku);
        }
        DiffThingsAdapter adapter = new DiffThingsAdapter( skus,ct);
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, holder.list_order_things);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = holder.list_order_things.getLayoutParams();
        params.height = totalHeight + (holder.list_order_things.getDividerHeight() * (adapter.getCount() - 1));
        ((ViewGroup.MarginLayoutParams) params).setMargins(10, 0, 10, 10);
        holder.list_order_things.setLayoutParams(params);

        holder.list_order_things.setAdapter(adapter);

    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_inventory_number,tv_inventory_no,tv_inventory_title,tv_diff_number;
        LinearLayout ll_iv_more_down,ll_iv_more_top,ll_inventory_goods;
        ImageView tv_image;
        ListView list_order_things;
        OnItemClickListener mOnItemClickListener;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tv_inventory_no= (TextView) itemView.findViewById(R.id.tv_inventory_no);
            tv_inventory_number= (TextView) itemView.findViewById(R.id.tv_inventory_number);
            tv_diff_number= (TextView) itemView.findViewById(R.id.tv_diff_number);
            tv_inventory_title= (TextView) itemView.findViewById(R.id.tv_inventory_title);
            ll_iv_more_down= (LinearLayout) itemView.findViewById(R.id.ll_iv_more_down);
            ll_iv_more_top= (LinearLayout) itemView.findViewById(R.id.ll_iv_more_top);
            ll_inventory_goods= (LinearLayout) itemView.findViewById(R.id.ll_inventory_goods);
            list_order_things= (ListView) itemView.findViewById(R.id.list_order_things);
            tv_image= (ImageView) itemView.findViewById(R.id.tv_image);
        }

        public void setData(int inventoryNumber,int diffNumber,String title,String no,String ImageUrl) {
            this.tv_inventory_no.setText(no);
            this.tv_inventory_number.setText(inventoryNumber+"");
            this.tv_diff_number.setText(diffNumber+"");
            this.tv_inventory_title.setText(title);
            this.tv_diff_number.setText(diffNumber+"");
            if(ImageUrl!=null){
                Glide.with(ct).load(ImageUrl).into(tv_image);
            }else{
                tv_image.setImageResource(R.mipmap.no_picture);
            }
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }

}
