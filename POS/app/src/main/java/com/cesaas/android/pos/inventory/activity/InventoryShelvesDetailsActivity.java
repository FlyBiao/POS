package com.cesaas.android.pos.inventory.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.base.BaseActivity;
import com.cesaas.android.pos.base.BaseRecyclerView;
import com.cesaas.android.pos.inventory.adapter.InventoryShelvesDetailsAdapter;
import com.cesaas.android.pos.inventory.bean.GetOneInfoBean;
import com.cesaas.android.pos.inventory.bean.ResultAddInventoryGoodsBean;
import com.cesaas.android.pos.inventory.bean.ResultCreateInventoryBean;
import com.cesaas.android.pos.inventory.bean.ResultGetOneInfoBean;
import com.cesaas.android.pos.inventory.bean.ResultUpdateGoodsBean;
import com.cesaas.android.pos.inventory.net.AddGoodNet;
import com.cesaas.android.pos.inventory.net.GetOneInfoNet;
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
 * 盘点货架详情
 */
public class InventoryShelvesDetailsActivity extends BaseActivity implements View.OnClickListener{
    private TextView tvTitle,tvLeftTitle,tvRightTitle,tv_inventory_number,tv_start_inventory,tv_input_inventory,tv_input_zairu,tv_number;
    private LinearLayout llBack,ll_goods_number;
    public MClearEditText et_style_code;
    private String leftTitle;
    private SwipeMenuRecyclerView recyclerView;
    private SwipeRefreshLayout swipeLayout;

    private int id;
    private int shelvesId;
    private int updateType;
    private int inventoryNumber;
    public String title;
    public String textContent;
    private BaseDialog baseDialog;

    private AddGoodNet addGoodNet;
    private GetOneInfoNet getOneInfoNet;

    private List<GetOneInfoBean> getOneInfoBeanList;
    private InventoryShelvesDetailsAdapter shelvesDetailsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_shelves_details);
        InitEventBus.initEventBus(mContext);
        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            leftTitle=bundle.getString("leftTitle");
            id=bundle.getInt("id");
            shelvesId=bundle.getInt("shelvesId");
        }

        getOneInfoNet=new GetOneInfoNet(mContext);
        addGoodNet=new AddGoodNet(mContext);

        initView();
        initData();

    }

    private void initData(){
        getOneInfoNet.setData(id,shelvesId);
    }

    /**
     * 接收货架详情结果数据
     * @param msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(ResultUpdateGoodsBean msg) {
        if(msg.IsSuccess!=false){
                ToastUtils.getLongToast(mContext,"修改成功！");
                initData();
                baseDialog.dismiss();
        }else{
            ToastUtils.getLongToast(mContext,"失败！"+msg.Message);
        }
    }

    /**
     * 接收货架详情结果数据
     * @param msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(ResultGetOneInfoBean msg) {
        if(msg.IsSuccess!=false){

            getOneInfoBeanList=new ArrayList<>();
            getOneInfoBeanList=msg.TModel;
            inventoryNumber=0;
            for (int i=0;i<getOneInfoBeanList.size();i++){
                inventoryNumber+=getOneInfoBeanList.get(i).getNum();
            }
            tv_inventory_number.setText(inventoryNumber+"");

            shelvesDetailsAdapter=new InventoryShelvesDetailsAdapter(getOneInfoBeanList,mContext,mActivity,id,shelvesId);
            recyclerView.setAdapter(shelvesDetailsAdapter);
        }else{
            ToastUtils.getLongToast(mContext,"获取货架信息失败！"+msg.Message);
        }
    }

    /**
     * 接收新建盘点单结果数据
     * @param msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(ResultCreateInventoryBean msg) {
        if(msg.IsSuccess!=false){
            ToastUtils.getLongToast(mContext,"新建盘点单成功！");
            Skip.mNext(mActivity,InventoryMainActivity.class);
        }else{
            ToastUtils.getLongToast(mContext,"新建盘点单失败！"+msg.Message);
        }
    }

    /**
     * 接收添加盘点商品数据
     * @param msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(ResultAddInventoryGoodsBean msg) {
        if(msg.IsSuccess!=false){
            ToastUtils.getLongToast(mContext,"添加盘点商品成功！");
            getOneInfoNet=new GetOneInfoNet(mContext);
            getOneInfoNet.setData(id,shelvesId);

        }else{
            ToastUtils.getLongToast(mContext,"添加盘点商品失败！"+msg.Message);
        }
    }

    private void initView(){
        tv_inventory_number= (TextView) findViewById(R.id.tv_inventory_number);
        llBack= (LinearLayout) findViewById(R.id.ll_base_title_back);
        tvRightTitle= (TextView) findViewById(R.id.tv_base_title_right);
        tvRightTitle.setVisibility(View.GONE);
        tvRightTitle.setText("确定");
        tvTitle= (TextView) findViewById(R.id.tv_base_title);
        tvTitle.setText("货架详情");
        tvLeftTitle= (TextView) findViewById(R.id.tv_base_title_left);
        tvLeftTitle.setText(leftTitle);
        tv_start_inventory=(TextView) findViewById(R.id.tv_start_inventory);
        tv_start_inventory.setOnClickListener(this);
        ll_goods_number=(LinearLayout) findViewById(R.id.ll_goods_number);
        ll_goods_number.setOnClickListener(this);
        tv_input_inventory=(TextView) findViewById(R.id.tv_input_inventory);
        tv_input_inventory.setOnClickListener(this);
        tv_input_zairu=(TextView) findViewById(R.id.tv_input_zairu);
        tv_input_zairu.setOnClickListener(this);
        tv_number= (TextView) findViewById(R.id.tv_number);

        recyclerView= (SwipeMenuRecyclerView) findViewById(R.id.recycler_view);
        swipeLayout= (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        BaseRecyclerView.initRecyclerView(mContext, recyclerView, swipeLayout, mOnRefreshListener, false);

        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Skip.mBack(mActivity);
            }
        });
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
                    getOneInfoNet.setData(id,shelvesId);
                }
            }, 2000);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_start_inventory:
                bundle.putInt("id",id);
                bundle.putInt("shelvesId",shelvesId);
                bundle.putInt("type",0);
                bundle.putInt("number",Integer.parseInt(tv_number.getText().toString()));
                Skip.mNextFroData(mActivity, ZxingScanActivity.class,bundle);
                break;
            case R.id.ll_goods_number:
                updateType=1;
                title="修改";
                textContent="请输入商品数量";
                baseDialog = new BaseDialog(mContext);
                baseDialog.mInitShow();
                baseDialog.setCancelable(false);
                break;
            case R.id.tv_input_inventory:
                updateType=2;
                title="手动输入";
                textContent="请输入商品条码";
                baseDialog = new BaseDialog(mContext);
                baseDialog.mInitShow();
                baseDialog.setCancelable(false);
                break;
            case R.id.tv_input_zairu:
                updateType=3;
                break;
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

            tv_dialog_title.setText(title);
            et_style_code.setHint(textContent);
            if(updateType==1){
                et_style_code.setInputType(InputType.TYPE_CLASS_NUMBER);
            }else{
                et_style_code.setInputType(InputType.TYPE_CLASS_TEXT);
            }
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
                    //执行修改数量操作
                    if(updateType==1){//修改
                        tv_number.setText(Integer.parseInt(et_style_code.getText().toString())+"");
                        dismiss();
                    }else if(updateType==2){
                        addGoodNet.setData(id,shelvesId,et_style_code.getText().toString(),0,Integer.parseInt(tv_number.getText().toString()));
                    }
                    break;
            }
        }
    }
}
