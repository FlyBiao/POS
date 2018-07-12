package com.cesaas.android.pos.adapter.order;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.bean.GetByBarcodeCode;
import com.cesaas.android.pos.listener.OnItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.List;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：条码订单数据适配器
 * 创建日期：2017/2/7 10:54
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class BarcodeOrderAdapter extends SwipeMenuAdapter<BarcodeOrderAdapter.DefaultViewHolder>{

    private List<GetByBarcodeCode> titles;

    private OnItemClickListener mOnItemClickListener;

    /**
     * 设置条码订单适配器构造方法
     * @param titles
     */
    public BarcodeOrderAdapter(List<GetByBarcodeCode> titles) {
        this.titles = titles;
    }

    /**
     * 设置RecyclerView 点击监听
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return titles == null ? 0 : titles.size();
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_barcode_order, parent, false);
    }

    @Override
    public BarcodeOrderAdapter.DefaultViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        return new DefaultViewHolder(realContentView);
    }

    @Override
    public void onBindViewHolder(BarcodeOrderAdapter.DefaultViewHolder holder, int position) {
        holder.setData(titles.get(position).getTitle(),titles.get(position).getShopCount()+"",titles.get(position).getPayMent(),titles.get(position).getPrice(),titles.get(position).getBarcodeCode());
        holder.setOnItemClickListener(mOnItemClickListener);
    }


    /**
     * 默认视图Holder 静态内部类
     */
    static class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTitle,tv_shop_number,tv_order_payment,tv_barcode_shop_price,tv_order_barcodecode;
        OnItemClickListener mOnItemClickListener;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_shop_title);
            tv_shop_number= (TextView) itemView.findViewById(R.id.tv_shop_number);
            tv_order_payment= (TextView) itemView.findViewById(R.id.tv_order_payment);
            tv_barcode_shop_price= (TextView) itemView.findViewById(R.id.tv_barcode_shop_price);
            tv_order_barcodecode= (TextView) itemView.findViewById(R.id.tv_order_barcodecode);
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.mOnItemClickListener = onItemClickListener;
        }

        public void setData(String title,String number,double payMent,double price,String barcodeCode) {
            this.tvTitle.setText(title);
            this.tv_shop_number.setText(number);
            tv_order_payment.setText(payMent+"");
            tv_barcode_shop_price.setText(price+"");
            tv_order_barcodecode.setText(barcodeCode);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }
}
