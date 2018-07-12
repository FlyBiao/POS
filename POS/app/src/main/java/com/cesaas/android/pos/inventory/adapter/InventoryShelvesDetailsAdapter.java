package com.cesaas.android.pos.inventory.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.inventory.bean.GetOneInfoBean;
import com.cesaas.android.pos.inventory.net.UpdateGoodsNumNet;
import com.cesaas.android.pos.listener.OnItemClickListener;
import com.cesaas.android.pos.utils.MClearEditText;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Author FGB
 * Description
 * Created at 2017/8/28 9:51
 * Version 1.0
 */

public class InventoryShelvesDetailsAdapter extends SwipeMenuAdapter<InventoryShelvesDetailsAdapter.DefaultViewHolder> {

    private List<GetOneInfoBean> titles;
    private List<GetOneInfoBean.Sku>  skuList=new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;

    private UpdateGoodsNumNet updateGoodsNumNet;
    private BaseDialog baseDialog;
    MClearEditText et_style_code;

    private int id;
    private int ShelvesId;
    private String code;
    private int number;
    private static Context ct;
    private  Activity activity;

    public InventoryShelvesDetailsAdapter(List<GetOneInfoBean> titles, Context ct, Activity activity, int id, int ShelvesId) {
        this.titles = titles;
        this.ct=ct;
        this.activity=activity;
        this.id=id;
        this.ShelvesId=ShelvesId;
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
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shelves_details, parent, false);
    }

    @Override
    public InventoryShelvesDetailsAdapter.DefaultViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        DefaultViewHolder viewHolder = new DefaultViewHolder(realContentView);
        viewHolder.mOnItemClickListener = mOnItemClickListener;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final InventoryShelvesDetailsAdapter.DefaultViewHolder holder, final int position) {
        holder.setData(titles.get(position).getNum(),titles.get(position).getNum(),titles.get(position).getStyleName(),titles.get(position).getStyleNo(),titles.get(position).getImageUrl());

        GetOneInfoBean bean=titles.get(position);

        skuList=new ArrayList<>();
        for (int i=0;i<bean.Sku.size();i++){
            GetOneInfoBean.Sku sku=new GetOneInfoBean().new Sku();
            sku=bean.Sku.get(i);
            skuList.add(sku);
        }
        ShelvesThingsAdapter adapter = new ShelvesThingsAdapter( skuList,ct);
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, holder.lv);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = holder.lv.getLayoutParams();
        params.height = totalHeight + (holder.lv.getDividerHeight() * (adapter.getCount() - 1));
        ((ViewGroup.MarginLayoutParams) params).setMargins(10, 0, 10, 10);
        holder.lv.setLayoutParams(params);
        holder.lv.setAdapter(adapter);

        holder.lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                code=skuList.get(position).getCode();
                number=skuList.get(position).getGoodsNum();

                baseDialog = new BaseDialog(ct);
                baseDialog.mInitShow();
                baseDialog.setCancelable(false);
            }
        });

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

    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_inventory_number,tv_inventory_no,tv_inventory_title,tv_diff_number;
        LinearLayout ll_iv_more_down,ll_iv_more_top,ll_inventory_goods;
        ListView lv;
        ImageView iv_goods_img;
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
            iv_goods_img= (ImageView) itemView.findViewById(R.id.iv_goods_img);
            lv = (ListView) itemView.findViewById(R.id.list_order_things);
        }

        public void setData(int inventoryNumber,int diffNumber,String title,String no,String imageUrl) {
            this.tv_inventory_no.setText(no);
            this.tv_inventory_number.setText(inventoryNumber+"");
            this.tv_diff_number.setText(diffNumber+"");
            this.tv_inventory_title.setText(title);
            this.tv_diff_number.setText(diffNumber+"");
            if(imageUrl!=null){
                Glide.with(ct).load(imageUrl).into(iv_goods_img);
            }else{
                iv_goods_img.setImageResource(R.mipmap.no_picture);
            }
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }



    public class BaseDialog extends Dialog implements View.OnClickListener {
        TextView tvCancel,tvSure,tv_dialog_title;

        public BaseDialog(Context context) {
            this(context, R.style.dialog);
        }

        public BaseDialog(Context context, int dialog) {
            super(context, dialog);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            setContentView(R.layout.dialog_input_inventory);
            tvCancel= (TextView) findViewById(R.id.tv_cancel);
            tvCancel.setOnClickListener(this);
            tvSure= (TextView) findViewById(R.id.tv_sure);
            tvSure.setOnClickListener(this);
            tv_dialog_title= (TextView) findViewById(R.id.tv_dialog_title);
            et_style_code= (MClearEditText) findViewById(R.id.et_style_code);

            tv_dialog_title.setText("修改数量");
            et_style_code.setHint(number+"");
        }

        public void mInitShow() {
            show();
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_cancel:
                    dismiss();
                    break;
                case R.id.tv_sure:
                    updateGoodsNumNet=new UpdateGoodsNumNet(ct);
                    updateGoodsNumNet.setData(ShelvesId,id,code,Integer.parseInt(et_style_code.getText().toString()));
                    dismiss();
                    break;
            }
        }
    }

}
