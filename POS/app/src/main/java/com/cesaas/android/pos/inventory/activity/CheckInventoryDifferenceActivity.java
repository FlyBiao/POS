package com.cesaas.android.pos.inventory.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.base.BaseActivity;
import com.cesaas.android.pos.base.BaseRecyclerView;
import com.cesaas.android.pos.inventory.adapter.CheckInventoryDifferenceAdapter;
import com.cesaas.android.pos.inventory.bean.CheckInventoryDifferenceBean;
import com.cesaas.android.pos.inventory.bean.ResultCheckInventoryDifferenceBean;
import com.cesaas.android.pos.inventory.net.GetInventorySumNet;
import com.cesaas.android.pos.listener.OnItemClickListener;
import com.cesaas.android.pos.utils.InitEventBus;
import com.cesaas.android.pos.utils.Skip;
import com.cesaas.android.pos.utils.ToastUtils;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


/**
 * 查看盘点差异
 */
public class CheckInventoryDifferenceActivity extends BaseActivity implements View.OnClickListener{

    private TextView tvTitle,tvLeftTitle;
    private LinearLayout llBack;
    private SwipeMenuRecyclerView recyclerView;
    private SwipeRefreshLayout swipeLayout;

    private GetInventorySumNet getInventorySumNet;

    private String leftTitle;
    private int id;

    private List<CheckInventoryDifferenceBean> inventoryDifferenceBeanList;
    private CheckInventoryDifferenceAdapter checkInventoryDifferenceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_inventory_difference);
        InitEventBus.initEventBus(mContext);

        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            leftTitle=bundle.getString("leftTitle");
            id=bundle.getInt("id");
        }

        initView();
        initData();
    }

    /**
     * 接收新建盘点单结果数据
     * @param msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(ResultCheckInventoryDifferenceBean msg) {
        if(msg.IsSuccess!=false){
            if(msg.TModel.size()!=0){
                inventoryDifferenceBeanList=new ArrayList<>();
                inventoryDifferenceBeanList=msg.TModel;

                checkInventoryDifferenceAdapter=new CheckInventoryDifferenceAdapter(inventoryDifferenceBeanList,mContext,mActivity);
                recyclerView.setAdapter(checkInventoryDifferenceAdapter);
                checkInventoryDifferenceAdapter.setOnItemClickListener(onItemClickListener);
            }
        }else{
            ToastUtils.getLongToast(mContext,"获取信息失败！"+msg.Message);
        }
    }

    private void initData(){
        getInventorySumNet=new GetInventorySumNet(mContext);
        getInventorySumNet.setData(id);
    }

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {

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
                Skip.mNextFroData(mActivity,CreateInventoryActivity.class,bundle);
                break;
        }
    }

    private void initView(){
        llBack= (LinearLayout) findViewById(R.id.ll_base_title_back);
        llBack.setOnClickListener(this);

        tvTitle= (TextView) findViewById(R.id.tv_base_title);
        tvTitle.setText("盘点单差异");
        tvLeftTitle= (TextView) findViewById(R.id.tv_base_title_left);
        tvLeftTitle.setText(leftTitle);

        recyclerView= (SwipeMenuRecyclerView) findViewById(R.id.recycler_view);
        swipeLayout= (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        BaseRecyclerView.initRecyclerView(mContext, recyclerView, swipeLayout, mOnRefreshListener, true);
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
}
