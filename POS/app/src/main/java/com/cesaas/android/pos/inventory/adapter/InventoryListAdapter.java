package com.cesaas.android.pos.inventory.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.inventory.activity.CheckInventoryDifferenceActivity;
import com.cesaas.android.pos.inventory.activity.InventoryDetailsActivity;
import com.cesaas.android.pos.inventory.bean.InventoryListBean;
import com.cesaas.android.pos.inventory.net.ConfirmDiffNet;
import com.cesaas.android.pos.inventory.net.CreateDiffNet;
import com.cesaas.android.pos.inventory.net.DeleteInentoryNet;
import com.cesaas.android.pos.inventory.net.SubmitInventoryNet;
import com.cesaas.android.pos.listener.OnItemClickListener;
import com.cesaas.android.pos.utils.Skip;
import com.cesaas.android.pos.utils.ToastUtils;
import com.flybiao.materialdialog.MaterialDialog;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.List;


/**
 * Author FGB
 * Description
 * Created at 2017/8/28 9:51
 * Version 1.0
 */

public class InventoryListAdapter extends SwipeMenuAdapter<InventoryListAdapter.DefaultViewHolder> {

    private List<InventoryListBean> titles;
    private OnItemClickListener mOnItemClickListener;

    private static Context ct;
    private  Activity activity;
    private String leftTitle;
    private MaterialDialog materialDialog;
    private MaterialDialog materialDialog2;
    private MaterialDialog materialDialog3;
    private MaterialDialog materialDialog4;

    public InventoryListAdapter(List<InventoryListBean> titles, Context ct, Activity activity, String leftTitle, MaterialDialog materialDialog, MaterialDialog materialDialog2, MaterialDialog materialDialog3, MaterialDialog materialDialog4) {
        this.titles = titles;
        this.ct=ct;
        this.activity=activity;
        this.leftTitle=leftTitle;
        this.materialDialog=materialDialog;
        this.materialDialog2=materialDialog2;
        this.materialDialog3=materialDialog3;
        this.materialDialog4=materialDialog4;
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
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventory, parent, false);
    }

    @Override
    public InventoryListAdapter.DefaultViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        DefaultViewHolder viewHolder = new DefaultViewHolder(realContentView);
        viewHolder.mOnItemClickListener = mOnItemClickListener;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final InventoryListAdapter.DefaultViewHolder holder, final int position) {
        holder.setData(titles.get(position).getNo(),titles.get(position).getInvertoryDay(),
                titles.get(position).getShopName(),titles.get(position).getCRName()
                ,titles.get(position).getNum(),titles.get(position).getDiffNum(),titles.get(position).getInvertoryType(),titles.get(position).getStatus());

        holder.ll_iv_more_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        holder.ll_more_handle.setVisibility(View.VISIBLE);
                        holder.ll_iv_more_down.setVisibility(View.GONE);
                        holder. ll_iv_more_top.setVisibility(View.VISIBLE);

            }
        });
        holder. ll_iv_more_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        holder. ll_iv_more_top.setVisibility(View.GONE);
                        holder.ll_more_handle.setVisibility(View.GONE);
                        holder.ll_iv_more_down.setVisibility(View.VISIBLE);
            }
        });
        holder.tv_create_difference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Bundle bundle=new Bundle();
//                bundle.putString("leftTitle",leftTitle);
//                Skip.mNextFroData(activity,InventoryDifferenceActivity.class,bundle);
                if (materialDialog != null) {
                    materialDialog.setTitle("温馨提示！")
                            .setMessage("确定需要生产该盘点差异单吗？")
                            .setPositiveButton("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //执行删除操作
                                    materialDialog.dismiss();
                                    CreateDiffNet createDiffNet=new CreateDiffNet(ct);
                                    createDiffNet.setData(titles.get(position).getId());

                                }
                            }).setNegativeButton("返回", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            materialDialog.dismiss();
                        }
                    }).setCanceledOnTouchOutside(true).show();
                }
            }
        });
        holder.tv_confirm_difference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Bundle bundle=new Bundle();
//                bundle.putString("leftTitle",leftTitle);
//                Skip.mNextFroData(activity,InventoryDifferenceActivity.class,bundle);
                if(titles.get(position).getStatus()==10){
                    if (materialDialog2 != null) {
                        materialDialog2.setTitle("温馨提示！")
                                .setMessage("确定需要确认该差异盘点单吗？")
                                .setPositiveButton("确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //执行删除操作
                                        materialDialog2.dismiss();
                                        ConfirmDiffNet confirmDiffNet=new ConfirmDiffNet(ct);
                                        confirmDiffNet.setData(titles.get(position).getId());

                                    }
                                }).setNegativeButton("返回", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                materialDialog2.dismiss();
                            }
                        }).setCanceledOnTouchOutside(true).show();
                    }
                }else{
                    ToastUtils.getLongToast(ct,"请先生成差异再确定差异！");
                }
            }
        });
        holder.tv_edit_difference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(titles.get(position).getStatus()==0){
                    Bundle bundle=new Bundle();
                    bundle.putString("leftTitle",leftTitle);
                    bundle.putInt("type",1);
                    bundle.putInt("inventoryType",titles.get(position).getInvertoryType());
                    bundle.putInt("id",titles.get(position).getId());
                    Skip.mNextFroData(activity,InventoryDetailsActivity.class,bundle);
                }else{
                    showStatus(titles.get(position).getStatus());
                }
            }
        });

        holder.tv_check_difference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(titles.get(position).getStatus()!=0){
                    Bundle bundle=new Bundle();
                    bundle.putString("leftTitle",leftTitle);
                    bundle.putInt("id",titles.get(position).getId());
                    Skip.mNextFroData(activity,CheckInventoryDifferenceActivity.class,bundle);
                }else{
                    ToastUtils.getLongToast(ct,"请先生成盘点差异单！");
                }
            }
        });
        holder.tv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (materialDialog3 != null) {
                    materialDialog3.setTitle("温馨提示！")
                            .setMessage("确定删除该盘点单吗？")
                            .setPositiveButton("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //执行删除操作
                                    materialDialog3.dismiss();
                                    DeleteInentoryNet deleteInentoryNet=new DeleteInentoryNet(ct);
                                    deleteInentoryNet.setData(titles.get(position).getId());

                                }
                            }).setNegativeButton("返回", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            materialDialog3.dismiss();
                        }
                    }).setCanceledOnTouchOutside(true).show();
                }
            }
        });
        holder.tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(titles.get(position).getStatus()==40){
                    if (materialDialog4 != null) {
                        materialDialog4.setTitle("温馨提示！")
                                .setMessage("确定马上提交该盘点单吗？")
                                .setPositiveButton("确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //执行提交操作
                                        materialDialog4.dismiss();
                                        SubmitInventoryNet deleteInentoryNet=new SubmitInventoryNet(ct);
                                        deleteInentoryNet.setData(titles.get(position).getId());

                                    }
                                }).setNegativeButton("返回", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                materialDialog4.dismiss();
                            }
                        }).setCanceledOnTouchOutside(true).show();
                    }
                }else{
                    ToastUtils.getLongToast(ct,"请先确认差异再提交！");
                }
            }
        });
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView inventory_no,create_date,shop_name,tv_inventory_number,tv_inventory_user,tv_difference_number;
        TextView tv_create_difference,tv_confirm_difference,tv_edit_difference,tv_check_difference;
        TextView tv_submit,tv_del,tv_inventory_type,tv_diff_type;
        LinearLayout ll_more_handle,ll_iv_more_down,ll_iv_more_top;
        OnItemClickListener mOnItemClickListener;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            tv_check_difference= (TextView) itemView.findViewById(R.id.tv_check_difference);
            tv_edit_difference= (TextView) itemView.findViewById(R.id.tv_edit_difference);
            tv_confirm_difference= (TextView) itemView.findViewById(R.id.tv_confirm_difference);
            tv_create_difference= (TextView) itemView.findViewById(R.id.tv_create_difference);
            inventory_no= (TextView) itemView.findViewById(R.id.inventory_no);
            create_date= (TextView) itemView.findViewById(R.id.create_date);
            shop_name= (TextView) itemView.findViewById(R.id.shop_name);
            tv_inventory_number= (TextView) itemView.findViewById(R.id.tv_inventory_number);
            tv_inventory_user= (TextView) itemView.findViewById(R.id.tv_inventory_user);
            tv_difference_number= (TextView) itemView.findViewById(R.id.tv_difference_number);
            ll_iv_more_down= (LinearLayout) itemView.findViewById(R.id.ll_iv_more_down);
            ll_iv_more_top= (LinearLayout) itemView.findViewById(R.id.ll_iv_more_top);
            ll_more_handle= (LinearLayout) itemView.findViewById(R.id.ll_more_handle);
            tv_inventory_type=(TextView) itemView.findViewById(R.id.tv_inventory_type);
            tv_diff_type= (TextView) itemView.findViewById(R.id.tv_diff_type);

            tv_del= (TextView) itemView.findViewById(R.id.tv_del);
            tv_submit= (TextView) itemView.findViewById(R.id.tv_submit);

        }

        public void setData(String no,String date,String shopName,String inventoryUser,int inventoryNumber,int differenceNumber,int typpe,int Status) {
            this.inventory_no.setText(no);
            this.create_date.setText(date);
            this.tv_inventory_user.setText(inventoryUser);
            this.tv_inventory_number.setText(inventoryNumber+"");
            this.tv_difference_number.setText(differenceNumber+"");

//            0:制单(未生差异)10:生成差异20:确认差异30:提交40:确认50:驳回

            if(Status==0){
                tv_diff_type.setText("未生成差异");
                tv_diff_type.setTextColor(ct.getResources().getColor(R.color.base_color));
            }else if(Status==10){
                tv_diff_type.setText("已生成差异");
                tv_diff_type.setTextColor(ct.getResources().getColor(R.color.text_color_btn));
            }else if(Status==20){
                tv_diff_type.setText("确认差异");
                tv_diff_type.setTextColor(ct.getResources().getColor(R.color.green_pressed));
            }else if(Status==30){
                tv_diff_type.setText("已提交");
                tv_diff_type.setTextColor(ct.getResources().getColor(R.color.rgb_text_org));
            }else if(Status==40){
                tv_diff_type.setText("已确认");
                tv_diff_type.setTextColor(ct.getResources().getColor(R.color.green_pressed));
            }else if(Status==50){
                tv_diff_type.setText("已驳回");
                tv_diff_type.setTextColor(ct.getResources().getColor(R.color.red));
            }

            if(typpe==0){//全盘
                tv_inventory_type.setText("全盘");
                tv_inventory_type.setBackgroundDrawable(ct.getResources().getDrawable(R.drawable.button_blue_bg));
            }else{//抽盘
                tv_inventory_type.setText("抽盘");
                tv_inventory_type.setBackgroundDrawable(ct.getResources().getDrawable(R.drawable.button_ellipse_orange_bg));
            }

            if(shopName!=null && shopName!=""){
                this.shop_name.setText(shopName);
            }else{
                this.shop_name.setText("null");
            }
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }

    public void showStatus(int status){
        if(status==1220){
            ToastUtils.getLongToast(ct,"该盘点单未生成差异，不能编辑！");

        }else if(status==10){
            ToastUtils.getLongToast(ct,"该盘点单已生成差异，不能编辑！");
        }else if(status==20){
            ToastUtils.getLongToast(ct,"该盘点单已确认差异，不能编辑！");
        }else if(status==30){
            ToastUtils.getLongToast(ct,"该盘点单已提交，不能编辑！");
        }else if(status==40){
            ToastUtils.getLongToast(ct,"该盘点单已确认，不能编辑！");
        }else if(status==50){
            ToastUtils.getLongToast(ct,"该盘点单已驳回，不能编辑！");
        }
    }

}
