package com.cesaas.android.pos.activity.cashier;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.activity.order.CheckAccountDetailActivity;
import com.cesaas.android.pos.base.BaseActivity;
import com.cesaas.android.pos.bean.PosOrderList;
import com.cesaas.android.pos.bean.PosOrderListBean;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.global.Urls;
import com.cesaas.android.pos.menu.DividerItemDecoration;
import com.cesaas.android.pos.menu.DropdownButton;
import com.cesaas.android.pos.menu.DropdownListView;
import com.cesaas.android.pos.menu.bean.DropdownItemObject;
import com.cesaas.android.pos.net.nohttp.HttpListener;
import com.cesaas.android.pos.utils.AbDateUtil;
import com.cesaas.android.pos.utils.Skip;
import com.cesaas.android.pos.utils.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：POS查账页面【RecyclerView实现】
 * 创建日期：2016/10/10 21:50
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class CheckAccountsActivity extends BaseActivity implements DropdownListView.Container ,BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView mRecyclerView;
    private TextView tv_not_data;
    private TextView tv_pos_refund_order;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private static final int PAGE_SIZE = 50;
    private int mCurrentCounter = 0;
    private int delayMillis = 1000;
    private static final int TOTAL_COUNTER = 18;
    private boolean isErr;
    private View notLoadingView;
    View mask;

    DropdownButton chooseType;
    DropdownListView dropdownType;
    private DropdownListView currentDropdownList;
    Animation dropdown_in, dropdown_out, dropdown_mask_out;

    private List<DropdownItemObject> chooseTypeData = new ArrayList<>();//选择类型

    private CheckAccountsAdapter mAdapter;
    List<PosOrderList> orderlist;

    private LinearLayout ll_check_accounts_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_accounts);
//        //通过EventBus订阅事件
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        initView();
        initMenu();

        //请求账本列表
        Request<String> request = NoHttp.createStringRequest(Urls.POS_ORDER_LIST, RequestMethod.POST);
        commonNet.requestNetTask(request,posOrderListener,1);
    }

    public void initView(){

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        chooseType= (DropdownButton) findViewById(R.id.chooseType);
        mRecyclerView= (RecyclerView) findViewById(R.id.mRecyclerView);
        tv_not_data= (TextView) findViewById(R.id.tv_not_data);
        tv_pos_refund_order= (TextView) findViewById(R.id.tv_pos_refund_order);
        mask=findViewById(R.id.mask);
        dropdownType= (DropdownListView) findViewById(R.id.dropdownType);

        dropdown_in = AnimationUtils.loadAnimation(this, R.anim.dropdown_in);
        dropdown_out = AnimationUtils.loadAnimation(this,R.anim.dropdown_out);
        dropdown_mask_out = AnimationUtils.loadAnimation(this,R.anim.dropdown_mask_out);

        ll_check_accounts_back= (LinearLayout) findViewById(R.id.ll_check_accounts_back);

        mask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });

        //查询退款
        tv_pos_refund_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Request<String> request = NoHttp.createStringRequest(Urls.POS_ORDER_LIST, RequestMethod.POST);
                request.add("refundStatus", 1);
                commonNet.requestNetTask(request,posOrderListener,1);
            }
        });

        //返回
        ll_check_accounts_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Skip.mBack(mActivity);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(DropdownItemObject event) {
        if(event.id==0){//全部
            Request<String> request = NoHttp.createStringRequest(Urls.POS_ORDER_LIST, RequestMethod.POST);
            commonNet.requestNetTask(request,posOrderListener,1);

        }else if(event.id==1){//今日
            Request<String> request = NoHttp.createStringRequest(Urls.POS_ORDER_LIST, RequestMethod.POST);
            request.add("start_date", AbDateUtil.getCurrentDate("yyyy-MM-dd"));
            request.add("end_date",AbDateUtil.getCurrentDate("yyyy-MM-dd"));
            commonNet.requestNetTask(request,posOrderListener,1);

        }else if(event.id==2){//本周
            Request<String> request = NoHttp.createStringRequest(Urls.POS_ORDER_LIST, RequestMethod.POST);
            request.add("start_date", AbDateUtil.getFirstDayOfWeek("yyyy-MM-dd"));
            request.add("end_date",AbDateUtil.getLastDayOfWeek("yyyy-MM-dd"));
            commonNet.requestNetTask(request,posOrderListener,1);
        }else if(event.id==3){//本月
            Request<String> request = NoHttp.createStringRequest(Urls.POS_ORDER_LIST, RequestMethod.POST);
            request.add("start_date", AbDateUtil.getFirstDayOfMonth("yyyy-MM-dd"));
            request.add("end_date",AbDateUtil.getLastDayOfMonth("yyyy-MM-dd"));
            commonNet.requestNetTask(request,posOrderListener,1);
        }
    }

     /**
     * 初始化下拉菜单
     */
    public void initMenu() {
        reset();
        chooseTypeData.add(new DropdownItemObject("全部账本", 0, "全部账本"));
        chooseTypeData.add(new DropdownItemObject("今日", 1, "今日"));
        chooseTypeData.add(new DropdownItemObject("本周", 2, "本周"));
        chooseTypeData.add(new DropdownItemObject("本月", 3, "本月"));
        dropdownType.bind(chooseTypeData, chooseType, this, 0);
        dropdown_mask_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                if (currentDropdownList == null) {
                    reset();
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 显示菜单
     * @param view
     */
    @Override
    public void show(DropdownListView view) {
        if (currentDropdownList != null) {
            currentDropdownList.clearAnimation();
            currentDropdownList.startAnimation(dropdown_out);
            currentDropdownList.setVisibility(View.GONE);
            currentDropdownList.button.setChecked(false);
        }
        currentDropdownList = view;
        mask.clearAnimation();
        mask.setVisibility(View.VISIBLE);
        currentDropdownList.clearAnimation();
        currentDropdownList.startAnimation(dropdown_in);
        currentDropdownList.setVisibility(View.VISIBLE);
        currentDropdownList.button.setChecked(true);
    }

    /**
     * 隐藏菜单
     */
    @Override
    public void hide() {
        if (currentDropdownList != null) {
            currentDropdownList.clearAnimation();
            currentDropdownList.startAnimation(dropdown_out);
            currentDropdownList.button.setChecked(false);
            mask.clearAnimation();
            mask.startAnimation(dropdown_mask_out);
        }
        currentDropdownList = null;
    }

    @Override
    public void onSelectionChanged(DropdownListView view) {
        if (view == dropdownType) {

        }
    }

    void reset() {
        chooseType.setChecked(false);
        dropdownType.setVisibility(View.GONE);
        mask.setVisibility(View.GONE);

        dropdownType.clearAnimation();
        mask.clearAnimation();
    }

    //查账回调监听
    public HttpListener<String> posOrderListener = new HttpListener<String>()
    {
        @Override
        public void onSucceed(int what, Response<String> response)
        {
                Log.d(Constant.TAG,"查账:"+response.get());
                PosOrderListBean bean=gson.fromJson(response.get(),PosOrderListBean.class);
                orderlist = new ArrayList<PosOrderList>();
                if(bean.TModel.size()!=0){
                    tv_not_data.setVisibility(View.GONE);
                    orderlist.addAll(bean.TModel);

                }else{
                    tv_not_data.setVisibility(View.VISIBLE);
                }
            //设置Adapter数据适配器
            initAdapter();
        }
        @Override
        public void onFailed(int what, Response<String> response)
        {
            Log.d(Constant.TAG,"onFailed:"+response.getException());
        }
    };

    /**
     * 设置RecyclerView属性
     */
    private void initAdapter() {
        //添加分割线
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mAdapter = new CheckAccountsAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.openLoadAnimation();
        mAdapter.openLoadMore(PAGE_SIZE);
        mCurrentCounter = mAdapter.getData().size();
        mAdapter.setOnLoadMoreListener(this);
        mRecyclerView.setAdapter(mAdapter);//设置adapter

        //设置item点击事件
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener( ){

            @Override
            public void SimpleOnItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(orderlist.get(position).getOrderStatus()==10){
                    ToastUtils.show("请支付后再查看详情!");
                }else{
                    Bundle bundle=new Bundle();
                    bundle.putString("OrderId",orderlist.get(position).getRetailId());
                    bundle.putDouble("PayAmount",orderlist.get(position).getPayAmount());
                    bundle.putInt("IsRefund",orderlist.get(position).getIsRefund());
                    //跳转到账单详情
                    Skip.mNextFroData(mActivity,CheckAccountDetailActivity.class,bundle);
                }
            }
        });

        //Item的长按事件
//        mRecyclerView.addOnItemTouchListener(new OnItemLongClickListener( ) {
//            @Override
//            public void SimpleOnItemLongClick(BaseQuickAdapter adapter, View view, int position) {
//                ToastUtils.show("Item的长按事件="+position);
//            }
//        });
    }

    /**
     * 刷新
     */
    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.setNewData(orderlist);//设置刷新数据
                mAdapter.openLoadMore(PAGE_SIZE);
                mAdapter.removeAllFooterView();
                mCurrentCounter = PAGE_SIZE;
                mSwipeRefreshLayout.setRefreshing(false);
                isErr = false;
            }
        }, delayMillis);
    }

    /**
     * 加载更多
     */
    @Override
    public void onLoadMoreRequested() {
        // 一定要在mRecyclerView.post里面更新数据。
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                if (mCurrentCounter >= TOTAL_COUNTER) {
                    // 数据全部加载完毕就调用 loadComplete
                    mAdapter.loadComplete();
                    if (notLoadingView == null) {
                        notLoadingView = getLayoutInflater().inflate(R.layout.not_loading, (ViewGroup) mRecyclerView.getParent(), false);
                    }
                    mAdapter.addFooterView(notLoadingView);
                } else {
                    if (isErr) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 如果有下一页则调用addData，不需要把下一页数据add到list里面，直接新的数据给adapter即可。
                                mAdapter.addData(orderlist);
                                mCurrentCounter = mAdapter.getData().size();
                            }
                        }, delayMillis);
                    } else {
                        isErr = true;
                        Toast.makeText(CheckAccountsActivity.this, R.string.network_err, Toast.LENGTH_LONG).show();
                        mAdapter.showLoadMoreFailedView();

                    }
                }
            }

        });
    }

    /**
     * 查账Adapter
     */
    public class CheckAccountsAdapter extends BaseQuickAdapter<PosOrderList> {
        private TextView tv_pay_status;

        public CheckAccountsAdapter() {
            super( R.layout.item_demo, orderlist);
        }

        public void convert(BaseViewHolder helper, PosOrderList bean) {
            helper.setText(R.id.tv_order_id,bean.getRetailId());
            helper.setText(R.id.tv_accounts_pay_amount,bean.getPayAmount()+"");
            helper.setText(R.id.tv_pay_date,bean.getPayDate());

            if(bean.getOrderStatus()==30 || bean.getOrderStatus()==40 || bean.getOrderStatus()==100){
                if(bean.getIsRefund()==0){
                    helper.setText(R.id.tv_pay_status,"交易成功");
                    ((TextView)helper.getView(R.id.tv_pay_status)).setTextColor(mContext.getResources().getColor(R.color.forestgreen));
                }else{
                    helper.setText(R.id.tv_pay_status,"已退款");
                    ((TextView)helper.getView(R.id.tv_pay_status)).setTextColor(mContext.getResources().getColor(R.color.gray_text));
                }
            }else{
                helper.setText(R.id.tv_pay_status,"待支付");
                ((TextView)helper.getView(R.id.tv_pay_status)).setTextColor(mContext.getResources().getColor(R.color.rgb_text_org));
            }
        }
    }

    @Override
    protected void onDestroy() {
        //取消EventBus订阅
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

}
