package com.cesaas.android.pos.inventory.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.base.BaseActivity;
import com.cesaas.android.pos.base.BaseRecyclerView;
import com.cesaas.android.pos.inventory.adapter.InventoryListAdapter;
import com.cesaas.android.pos.inventory.bean.InventoryListBean;
import com.cesaas.android.pos.inventory.bean.ResultConfirmDiffBean;
import com.cesaas.android.pos.inventory.bean.ResultCreateDiffBean;
import com.cesaas.android.pos.inventory.bean.ResultDeleteInventoryBean;
import com.cesaas.android.pos.inventory.bean.ResultInventoryListBean;
import com.cesaas.android.pos.inventory.bean.ResultSubmitInventoryBean;
import com.cesaas.android.pos.inventory.net.InventoryListNet;
import com.cesaas.android.pos.listener.OnItemClickListener;
import com.cesaas.android.pos.utils.InitEventBus;
import com.cesaas.android.pos.utils.Skip;
import com.cesaas.android.pos.utils.ToastUtils;
import com.flybiao.materialdialog.MaterialDialog;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 盘点主页面
 */
public class InventoryMainActivity extends BaseActivity implements View.OnClickListener{

    private TextView tvTitle,tv_not_data;
    private ImageView ivScan;
    private LinearLayout llBack,ll_create_inventory;
    private SwipeMenuRecyclerView recyclerView;
    private SwipeRefreshLayout swipeLayout;
    private MaterialDialog materialDialog;
    private MaterialDialog materialDialog2;
    private MaterialDialog materialDialog3;
    private MaterialDialog materialDialog4;

    private int pageIndex=1;

    private InventoryListNet inventoryListNet;
    private List<InventoryListBean> inventoryListBeen;
    private InventoryListAdapter inventoryListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_main);
        InitEventBus.initEventBus(mContext);

        materialDialog = new MaterialDialog(mContext);
        materialDialog2 = new MaterialDialog(mContext);
        materialDialog3 = new MaterialDialog(mContext);
        materialDialog4 = new MaterialDialog(mContext);

        initView();
        initData();

    }

    private void initData(){
        inventoryListNet=new InventoryListNet(mContext);
        inventoryListNet.setData(pageIndex);
    }

    /**
     * 接收生成差异数据
     * @param msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(ResultCreateDiffBean msg) {
        if(msg.IsSuccess!=false){
            ToastUtils.getLongToast(mContext,"确认生成差异成功！");
            inventoryListNet=new InventoryListNet(mContext);
            inventoryListNet.setData(pageIndex);
        }else{
            ToastUtils.getLongToast(mContext,"确认生成差异失败！"+msg.Message);
        }
    }

    /**
     * 接收确认差异数据
     * @param msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(ResultConfirmDiffBean msg) {
        if(msg.IsSuccess!=false){
            ToastUtils.getLongToast(mContext,"确认差异成功！");
            inventoryListNet=new InventoryListNet(mContext);
            inventoryListNet.setData(pageIndex);
        }else{
            ToastUtils.getLongToast(mContext,"确认差异失败！"+msg.Message);
        }
    }

    /**
     * 接收提交盘点数据
     * @param msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(ResultSubmitInventoryBean msg) {
        if(msg.IsSuccess!=false){
            ToastUtils.getLongToast(mContext,"提交成功！");
            inventoryListNet=new InventoryListNet(mContext);
            inventoryListNet.setData(pageIndex);
        }else{
            ToastUtils.getLongToast(mContext,"提交失败！"+msg.Message);
        }
    }

    /**
     * 接收删除数据
     * @param msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(ResultDeleteInventoryBean msg) {
        if(msg.IsSuccess!=false){
            ToastUtils.getLongToast(mContext,"删除成功！");
            inventoryListNet=new InventoryListNet(mContext);
            inventoryListNet.setData(pageIndex);
        }else{
            ToastUtils.getLongToast(mContext,"删除失败！"+msg.Message);
        }
    }

    /**
     * 接收盘点单列表数据
     * @param msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(ResultInventoryListBean msg) {
        if(msg.IsSuccess!=false){
            if(msg.TModel.size()!=0){
                inventoryListBeen=new ArrayList<>();
                inventoryListBeen=msg.TModel;

                swipeLayout.setVisibility(View.VISIBLE);
                tv_not_data.setVisibility(View.GONE);

                inventoryListAdapter=new InventoryListAdapter(inventoryListBeen,mContext,mActivity,tvTitle.getText().toString(),materialDialog,materialDialog2,materialDialog3,materialDialog4);
                recyclerView.setAdapter(inventoryListAdapter);
                inventoryListAdapter.setOnItemClickListener(onItemClickListener);
            }else{
                swipeLayout.setVisibility(View.GONE);
                tv_not_data.setVisibility(View.VISIBLE);
            }

        }else{
            ToastUtils.getLongToast(mContext,"获取失败！"+msg.Message);
            swipeLayout.setVisibility(View.GONE);
            tv_not_data.setVisibility(View.VISIBLE);
        }
    }

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            bundle.putString("leftTitle",tvTitle.getText().toString());
            bundle.putInt("type",0);
            bundle.putInt("inventoryType",inventoryListBeen.get(position).getInvertoryType());
            bundle.putInt("id",inventoryListBeen.get(position).getId());
            Skip.mNextFroData(mActivity,InventoryDetailsActivity.class,bundle);
        }
    };

    private void initView(){
        llBack= (LinearLayout) findViewById(R.id.ll_base_title_back);
        llBack.setOnClickListener(this);
        tvTitle= (TextView) findViewById(R.id.tv_base_title);
        tvTitle.setText("盘点");
        ivScan= (ImageView) findViewById(R.id.iv_add_module);
        ivScan.setVisibility(View.VISIBLE);
        ivScan.setOnClickListener(this);
        ivScan.setImageResource(R.mipmap.scan_s);
        ll_create_inventory= (LinearLayout) findViewById(R.id.ll_create_inventory);
        ll_create_inventory.setOnClickListener(this);
        recyclerView= (SwipeMenuRecyclerView) findViewById(R.id.recycler_view);
        swipeLayout= (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        tv_not_data= (TextView) findViewById(R.id.tv_not_data);
        tv_not_data.setOnClickListener(this);

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
                    inventoryListNet=new InventoryListNet(mContext);
                    inventoryListNet.setData(pageIndex);
                }
            }, 2000);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_base_title_back:
                Skip.mBack(mActivity);
                break;
            case R.id.ll_create_inventory:
                bundle.putString("leftTitle",tvTitle.getText().toString());
                Skip.mNextFroData(mActivity,CreateInventoryActivity.class,bundle);
                break;
            case R.id.iv_add_module:
//                Skip.mNext(mActivity, ZxingScanActivity.class);
                break;
            case R.id.tv_not_data:
                inventoryListNet=new InventoryListNet(mContext);
                inventoryListNet.setData(pageIndex);
                break;
        }
    }
}
