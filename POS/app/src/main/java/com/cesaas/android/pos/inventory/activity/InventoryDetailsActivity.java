package com.cesaas.android.pos.inventory.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.base.BaseActivity;
import com.cesaas.android.pos.base.BaseRecyclerView;
import com.cesaas.android.pos.inventory.adapter.InventoryDetailsAdapter;
import com.cesaas.android.pos.inventory.bean.InventoryDetailsBean;
import com.cesaas.android.pos.inventory.bean.ResultCreateInventoryBean;
import com.cesaas.android.pos.inventory.bean.ResultInventoryDetailsBean;
import com.cesaas.android.pos.inventory.net.CreateShelfNet;
import com.cesaas.android.pos.inventory.net.InventoryDetailsNet;
import com.cesaas.android.pos.listener.OnItemClickListener;
import com.cesaas.android.pos.utils.InitEventBus;
import com.cesaas.android.pos.utils.MClearEditText;
import com.cesaas.android.pos.utils.Skip;
import com.cesaas.android.pos.utils.ToastUtils;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 盘点详情
 */
public class InventoryDetailsActivity extends BaseActivity implements View.OnClickListener{

    private TextView tvTitle,tvLeftTitle,tvRightTitle;
    private TextView tv_inventory_no,tv_inventory_shop_name,tv_inventory_date,tv_inventory_user_name,tv_inventory_data,tv_submit_user,tv_submit_date,tv_sure_user,tv_sure_date,tv_inventory_type;
    private TextView tv_edit_no,tv_edit_inventory_type,tv_edit_inventory_date,tv_edit_inventory_shop_name;
    private ImageView iv_edit_inventory;
    private LinearLayout llBack,ll_create_shelf;
    private LinearLayout ll_edit_inventory,ll_inventory_details;
    private SwipeMenuRecyclerView recyclerView;
    private SwipeRefreshLayout swipeLayout;

    private String leftTitle;
    private int type=-1;
    private int inventoryType;
    private int id;

    private BaseDialog baseDialog;

    private CreateShelfNet createShelfNet;
    private InventoryDetailsNet inventoryDetailsNet;


    private List<InventoryDetailsBean.GoodsShelves> inventoryDetailsBeanList;
    private InventoryDetailsAdapter inventoryDetailsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_details);
        InitEventBus.initEventBus(mContext);

        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            leftTitle=bundle.getString("leftTitle");
            type=bundle.getInt("type");
            id=bundle.getInt("id");
            inventoryType=bundle.getInt("inventoryType");
        }

        createShelfNet=new CreateShelfNet(mContext);
        inventoryDetailsNet=new InventoryDetailsNet(mContext);

        initView();
        initData();
    }

    private void initData(){
        inventoryDetailsNet.setData(id);

        if(inventoryType==0){//
            tv_inventory_type.setText("全盘");
            tv_inventory_type.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.button_blue_bg));
            tv_edit_inventory_type.setText("全盘");
            tv_edit_inventory_type.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.button_blue_bg));
        }else{//
            tv_inventory_type.setText("抽盘");
            tv_inventory_type.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.button_ellipse_orange_bg));
            tv_edit_inventory_type.setText("抽盘");
            tv_edit_inventory_type.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.button_ellipse_orange_bg));
        }
    }

    /**
     * 接收盘点详情数据
     * @param msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(ResultInventoryDetailsBean msg) {
        if(msg.IsSuccess!=false){
            tv_inventory_no.setText(msg.TModel.getNo());
            tv_inventory_shop_name.setText(msg.TModel.getShopName());
            tv_inventory_date.setText(msg.TModel.getInvertoryDay());
            tv_inventory_user_name.setText(msg.TModel.getCRName());
            tv_inventory_data.setText(msg.TModel.getCRDate());
            tv_submit_user.setText(msg.TModel.getSubmitName());
            tv_submit_date.setText(msg.TModel.getSubmitDate());
            tv_sure_user.setText(msg.TModel.getCheckName());
            tv_sure_date.setText(msg.TModel.getCheckDate());

            tv_edit_no.setText(msg.TModel.getNo());
            tv_edit_inventory_date.setText(msg.TModel.getInvertoryDay());
            tv_edit_inventory_shop_name.setText(msg.TModel.getShopName());

            inventoryDetailsBeanList=new ArrayList<>();
            inventoryDetailsBeanList=msg.TModel.GoodsShelves;

            inventoryDetailsAdapter=new InventoryDetailsAdapter(inventoryDetailsBeanList,mContext,mActivity);
            recyclerView.setAdapter(inventoryDetailsAdapter);
            inventoryDetailsAdapter.setOnItemClickListener(onItemClickListener);

        }else{
            ToastUtils.getLongToast(mContext,"失败！"+msg.Message);
        }
    }
    /**
     * 接收新建盘点货架结果数据
     * @param msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(ResultCreateInventoryBean msg) {
        if(msg.IsSuccess!=false){
            ToastUtils.getLongToast(mContext,"新建盘点货架成功！");
            baseDialog.dismiss();
            inventoryDetailsNet.setData(id);
        }else{
            ToastUtils.getLongToast(mContext,"新建盘点货架失败！"+msg.Message);
        }
    }

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            bundle.putString("leftTitle",tvTitle.getText().toString());
            bundle.putInt("id",id);
            bundle.putInt("shelvesId",inventoryDetailsBeanList.get(position).getShelvesId());
            Skip.mNextFroData(mActivity,InventoryShelvesDetailsActivity.class,bundle);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_base_title_back:
                Skip.mBack(mActivity);
                break;
            case R.id.tv_base_title_right:
                finish();
                break;
            case R.id.iv_edit_inventory:
                bundle.putString("leftTitle",tvTitle.getText().toString());
                bundle.putInt("id",id);
                bundle.putInt("type",2);
                Skip.mNextFroData(mActivity,CreateInventoryActivity.class,bundle);
                break;
            case R.id.ll_create_shelf:
                baseDialog = new BaseDialog(mContext);
                baseDialog.mInitShow();
                baseDialog.setCancelable(false);
                break;
        }
    }

    private void initView(){
        llBack= (LinearLayout) findViewById(R.id.ll_base_title_back);
        llBack.setOnClickListener(this);
        tvRightTitle= (TextView) findViewById(R.id.tv_base_title_right);
        tvRightTitle.setOnClickListener(this);
        tvRightTitle.setVisibility(View.GONE);
        tvRightTitle.setText("保存");
        tvTitle= (TextView) findViewById(R.id.tv_base_title);
        tvTitle.setText("盘点单明细");
        tvLeftTitle= (TextView) findViewById(R.id.tv_base_title_left);
        tvLeftTitle.setText(leftTitle);
        iv_edit_inventory= (ImageView) findViewById(R.id.iv_edit_inventory);
        iv_edit_inventory.setOnClickListener(this);
        ll_create_shelf=(LinearLayout) findViewById(R.id.ll_create_shelf);
        ll_create_shelf.setOnClickListener(this);

        tv_inventory_no= (TextView) findViewById(R.id.tv_inventory_no);
        tv_inventory_shop_name= (TextView) findViewById(R.id.tv_inventory_shop_name);
        tv_inventory_date= (TextView) findViewById(R.id.tv_inventory_date);
        tv_inventory_user_name= (TextView) findViewById(R.id.tv_inventory_user_name);
        tv_inventory_data= (TextView) findViewById(R.id.tv_inventory_data);
        tv_submit_user= (TextView) findViewById(R.id.tv_submit_user);
        tv_submit_date= (TextView) findViewById(R.id.tv_submit_date);
        tv_sure_user= (TextView) findViewById(R.id.tv_sure_user);
        tv_sure_date= (TextView) findViewById(R.id.tv_sure_date);
        tv_inventory_type= (TextView) findViewById(R.id.tv_inventory_type);

        tv_edit_no=(TextView) findViewById(R.id.tv_edit_no);
        tv_edit_inventory_type=(TextView) findViewById(R.id.tv_edit_inventory_type);
        tv_edit_inventory_date=(TextView) findViewById(R.id.tv_edit_inventory_date);
        tv_edit_inventory_shop_name=(TextView) findViewById(R.id.tv_edit_inventory_shop_name);

        ll_edit_inventory= (LinearLayout) findViewById(R.id.ll_edit_inventory);
        ll_inventory_details= (LinearLayout) findViewById(R.id.ll_inventory_details);
        if(type==0){
            ll_inventory_details.setVisibility(View.VISIBLE);
            ll_create_shelf.setVisibility(View.GONE);
        }else{
            ll_edit_inventory.setVisibility(View.VISIBLE);
            tvRightTitle.setVisibility(View.GONE);
            ll_create_shelf.setVisibility(View.VISIBLE);
        }

        recyclerView= (SwipeMenuRecyclerView) findViewById(R.id.recycler_view);
        swipeLayout= (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        BaseRecyclerView.initRecyclerView(mContext, recyclerView, swipeLayout, mOnRefreshListener, false);
    }

    /**
     * 刷新监听。
     */
    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //重新加载数据
                    swipeLayout.setRefreshing(false);
                }
            }, 2000);
        }
    };

    public class BaseDialog extends Dialog implements View.OnClickListener {
        TextView tvCancel,tvSure,tv_dialog_title;
        private MClearEditText et_style_code;

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

            tv_dialog_title.setText("新增货架");
            et_style_code.setHint("请输入货架名称");
            et_style_code.setInputType(InputType.TYPE_CLASS_TEXT);
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
                    createShelfNet.setData(et_style_code.getText().toString(),id);
                    break;
            }
        }
    }
}
