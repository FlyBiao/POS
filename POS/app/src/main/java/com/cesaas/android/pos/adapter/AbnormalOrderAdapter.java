package com.cesaas.android.pos.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.db.bean.AbnormalOrderBean;
import com.cesaas.android.pos.db.bean.PosPayBean;
import com.cesaas.android.pos.listener.OnItemClickListener;
import com.cesaas.android.pos.pos.ResultPayCategoryBean;
import com.cesaas.android.pos.pos.adapter.PosPayCategoryAdapter;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.List;

/**
 * Author FGB
 * Description 异常单ADAPTER
 * Created at 2017/12/5 17:24
 * Version 1.0
 */

public class AbnormalOrderAdapter extends SwipeMenuAdapter<AbnormalOrderAdapter.DefaultViewHolder> {

    public static Context ct;
    public List<PosPayBean> posPayBeanList;
    private OnItemClickListener mOnItemClickListener;

    public AbnormalOrderAdapter(List<PosPayBean> categoryBeaList, Context ct){
        this.posPayBeanList=categoryBeaList;
        this.ct=ct;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return posPayBeanList == null ? 0 : posPayBeanList.size();
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_abnormal_order, parent, false);
    }

    @Override
    public AbnormalOrderAdapter.DefaultViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        AbnormalOrderAdapter.DefaultViewHolder viewHolder = new AbnormalOrderAdapter.DefaultViewHolder(realContentView);
        viewHolder.mOnItemClickListener = mOnItemClickListener;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AbnormalOrderAdapter.DefaultViewHolder holder, int position) {
        holder.setData(posPayBeanList.get(position).getOrderNo(),posPayBeanList.get(position).getCreateTime(),posPayBeanList.get(position).getOrderStatus(),posPayBeanList.get(position).getAmount());
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        OnItemClickListener mOnItemClickListener;
        TextView tv_order_no,tv_create_time,tv_order_status,tv_order_money;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tv_order_no= (TextView) itemView.findViewById(R.id.tv_order_no);
            tv_create_time= (TextView) itemView.findViewById(R.id.tv_create_time);
            tv_order_status= (TextView) itemView.findViewById(R.id.tv_order_status);
            tv_order_money= (TextView) itemView.findViewById(R.id.tv_order_money);
        }

        public void setData(String orderNo,String createTime,String status,String money) {
            this.tv_order_no.setText("单号 ("+orderNo+")");
            this.tv_create_time.setText(createTime);
            this.tv_order_money.setText("￥"+money);
            if(status.equals("0")){
                this.tv_order_status.setText("异常订单");
            }else{
                this.tv_order_status.setText("正常");
                this.tv_order_status.setTextColor(ct.getResources().getColor(R.color.green));
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
