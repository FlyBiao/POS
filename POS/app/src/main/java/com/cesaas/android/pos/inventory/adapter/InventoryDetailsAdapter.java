package com.cesaas.android.pos.inventory.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
;
import com.cesaas.android.pos.R;
import com.cesaas.android.pos.inventory.bean.InventoryDetailsBean;
import com.cesaas.android.pos.listener.OnItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.List;


/**
 * Author FGB
 * Description
 * Created at 2017/8/28 9:51
 * Version 1.0
 */

public class InventoryDetailsAdapter extends SwipeMenuAdapter<InventoryDetailsAdapter.DefaultViewHolder> {

    private List<InventoryDetailsBean.GoodsShelves> titles;
    private OnItemClickListener mOnItemClickListener;

    private Context ct;
    private  Activity activity;

    public InventoryDetailsAdapter(List<InventoryDetailsBean.GoodsShelves> titles, Context ct, Activity activity) {
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
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventory_details, parent, false);
    }

    @Override
    public InventoryDetailsAdapter.DefaultViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        DefaultViewHolder viewHolder = new DefaultViewHolder(realContentView);
        viewHolder.mOnItemClickListener = mOnItemClickListener;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final InventoryDetailsAdapter.DefaultViewHolder holder, final int position) {
        holder.setData(titles.get(position).getName(),titles.get(position).getNum());


    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_inventory_number,tv_shelf_goods;
        OnItemClickListener mOnItemClickListener;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tv_shelf_goods= (TextView) itemView.findViewById(R.id.tv_shelf_goods);
            tv_inventory_number= (TextView) itemView.findViewById(R.id.tv_inventory_number);
        }

        public void setData(String Shelf,int Number) {
            this.tv_shelf_goods.setText(Shelf);
            this.tv_inventory_number.setText(Number+"");
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }

}
