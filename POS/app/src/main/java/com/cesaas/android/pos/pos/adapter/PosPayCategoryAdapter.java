package com.cesaas.android.pos.pos.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.listener.OnItemClickListener;
import com.cesaas.android.pos.storedvalue.bean.ResultPayCategoryBean;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.List;

/**
 * Author FGB
 * Description
 * Created at 2017/9/12 9:37
 * Version 1.0
 */

public class PosPayCategoryAdapter extends SwipeMenuAdapter<PosPayCategoryAdapter.DefaultViewHolder> {

    public List<ResultPayCategoryBean.PayCategoryBea> categoryBeaList;
    private OnItemClickListener mOnItemClickListener;
    public static int payType;

    public PosPayCategoryAdapter(List<ResultPayCategoryBean.PayCategoryBea> categoryBeaList,int payTypes){
        this.categoryBeaList=categoryBeaList;
        this.payType=payTypes;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return categoryBeaList == null ? 0 : categoryBeaList.size();
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pos_pay_category, parent, false);
    }

    @Override
    public DefaultViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        DefaultViewHolder viewHolder = new DefaultViewHolder(realContentView);
        viewHolder.mOnItemClickListener = mOnItemClickListener;
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(DefaultViewHolder holder, int position) {
        holder.setData(categoryBeaList.get(position).getDescription(),categoryBeaList.get(position).getCategoryType());
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        OnItemClickListener mOnItemClickListener;
        TextView tv_pay_description;
        ImageView menuImages;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tv_pay_description= (TextView) itemView.findViewById(R.id.tv_pay_description);
            menuImages= (ImageView) itemView.findViewById(R.id.menuImages);
        }

        public void setData(String description,int CategoryType) {
            if(payType!=5){
                this.tv_pay_description.setText(description);
            }else if(payType==5){
                if(CategoryType!=5 ){
                    this.tv_pay_description.setText(description);
                }
            }
            switch (CategoryType){
                case 2:
                    this.menuImages.setImageResource(R.mipmap.weixin);
                    break;
                case 3:
                    this.menuImages.setImageResource(R.mipmap.alipay);
                    break;
                case 4:
                    this.menuImages.setImageResource(R.mipmap.unionpay);
                    break;
                case 5:
                    this.menuImages.setImageResource(R.mipmap.cash);
                    break;
                default:
                    this.menuImages.setImageResource(R.mipmap.pos_pay);
                    break;
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
