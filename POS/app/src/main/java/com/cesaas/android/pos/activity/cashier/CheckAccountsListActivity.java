package com.cesaas.android.pos.activity.cashier;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.activity.order.OrderDetailActivity;
import com.cesaas.android.pos.activity.order.RefundActivity;
import com.cesaas.android.pos.adapter.listviewadapter.adapterlibrary.ViewHolder;
import com.cesaas.android.pos.adapter.listviewadapter.adapterlibrary.abslistview.CommonAdapter;
import com.cesaas.android.pos.base.BaseActivity;
import com.cesaas.android.pos.bean.PosOrderList;
import com.cesaas.android.pos.bean.PosOrderListBean;
import com.cesaas.android.pos.bean.SortBean;
import com.cesaas.android.pos.custom.LoadMoreListView;
import com.cesaas.android.pos.custom.RefreshAndLoadMoreView;
import com.cesaas.android.pos.menu.DropdownButton;
import com.cesaas.android.pos.menu.DropdownListView;
import com.cesaas.android.pos.menu.bean.DropdownItemObject;
import com.cesaas.android.pos.net.xutils.net.CheckAccountsNet;
import com.cesaas.android.pos.utils.AbDateUtil;
import com.cesaas.android.pos.utils.Skip;
import com.cesaas.android.pos.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：查账列表【ListView实现】
 * 创建日期：2016/11/6 20:42
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class CheckAccountsListActivity extends BaseActivity implements DropdownListView.Container{

    private LoadMoreListView mLoadMoreListView;//加载更多
    private RefreshAndLoadMoreView mRefreshAndLoadMoreView;//下拉刷新
    private boolean refresh=false;
    private static int pageIndex = 1;//当前页
    private int eventId=0;

    private View mask;
    private TextView tv_not_data,tv_pos_refund_order;
    private LinearLayout ll_check_accounts_back;

    private DropdownButton chooseType;
    private DropdownListView dropdownType;
    private DropdownListView currentDropdownList;
    Animation dropdown_in, dropdown_out, dropdown_mask_out;

    private List<DropdownItemObject> chooseTypeData = new ArrayList<>();//选择类型
    private List<PosOrderList> orderlist;
    private JSONArray dateArray;//时间数组，排序使用
    private JSONArray jsonDateArray;//时间数组，排序使用
    private CheckAccountsNet accountsNet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_accounts_list);

        //通过EventBus订阅事件
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initView();
        initMenu();
        setAdapter();
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(DropdownItemObject event) {
        eventId=event.id;
        setAdapter();
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(PosOrderListBean bean) {
        orderlist=new ArrayList<>();
        if (bean.isSuccess()==true) {
            if (bean.TModel.size() > 0&&bean.TModel.size()==50) {
                mLoadMoreListView.setHaveMoreData(true);
            } else {
                mLoadMoreListView.setHaveMoreData(false);
            }
            if(bean.TModel.size()!=0){
                orderlist.addAll(bean.TModel);
                dateArray=new JSONArray();
                SortBean sortBean=null;
                for(int i=0;i<orderlist.size();i++){
                    sortBean=new SortBean();
                    sortBean.setField(orderlist.get(i).getCreateTime());
                    sortBean.setValue("desc");
                    dateArray.put(sortBean.getSort());
                }
                jsonDateArray=dateArray;
            }

            mLoadMoreListView.setAdapter(new CommonAdapter<PosOrderList>(mContext,R.layout.item_demo,orderlist) {
                @Override
                public void convert(ViewHolder helper, PosOrderList bean, int postion) {
                    helper.setText(R.id.tv_order_id,"("+bean.getRetailId()+")");
                    helper.setText(R.id.tv_accounts_pay_amount,bean.getPayAmount()+"");
                    helper.setText(R.id.tv_pay_date,bean.getCreateTime());

                    if(bean.getRetailFrom()==1){
                        helper.setText(R.id.tv_order_retailfrom,"POS订单");
                    }else{
                        helper.setText(R.id.tv_order_retailfrom,"PC订单");
                    }

                    if(bean.getRetailCheck()==1){
                        helper.setText(R.id.tv_pay_status,"支付成功");
                        ((TextView)helper.getView(R.id.tv_pay_status)).setTextColor(mContext.getResources().getColor(R.color.forestgreen));
                    }else{
                        helper.setText(R.id.tv_pay_status,"未支付");
                        ((TextView)helper.getView(R.id.tv_pay_status)).setTextColor(mContext.getResources().getColor(R.color.gray_text));
                    }
                }
            });

            mRefreshAndLoadMoreView.setRefreshing(false);
            mLoadMoreListView.onLoadComplete();
        }else{
            ToastUtils.getLongToast(mContext,bean.getMessage());
        }
    }

    /**
     * 设置Adapter数据适配器
     */
    public void setAdapter(){
        initData();
        initItemClickListener();
    }

    /**
     * 初始化数据
     */
    public void initData() {
        loadData(1);

        mRefreshAndLoadMoreView.setLoadMoreListView(mLoadMoreListView);
        mLoadMoreListView.setRefreshAndLoadMoreView(mRefreshAndLoadMoreView);
        // 设置下拉刷新监听
        mRefreshAndLoadMoreView
                .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        refresh=true;
                        pageIndex = 1;
                        eventId=100;
                        loadData(pageIndex);
                    }
                });
        // 设置加载监听
        mLoadMoreListView
                .setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
                    @Override
                    public void onLoadMore() {
                        refresh=false;
                        loadData(pageIndex + 1);
                    }
                });
    }

    /**
     * 加载数据
     * @param page 当前页
     */
    protected void loadData(final int page) {
        accountsNet=new CheckAccountsNet(mContext);
        if(eventId==1){//今日
            accountsNet.setData(AbDateUtil.getCurrentDate("yyyy-MM-dd 00:00:00"),AbDateUtil.getCurrentDate("yyyy-MM-dd 23:59:59"),page);
        }else if(eventId==2){//昨天
            accountsNet.setData(AbDateUtil.YesTerDay("yyyy-MM-dd 00:00:00"),AbDateUtil.getCurrentDate("yyyy-MM-dd 23:59:59"),page);

        }else if(eventId==3){//本周
            accountsNet.setData(AbDateUtil.getFirstDayOfWeek("yyyy-MM-dd 00:00:00"),AbDateUtil.getLastDayOfWeek("yyyy-MM-dd 23:59:59"),page);

        }else if(eventId==4){//本月
            accountsNet.setData(AbDateUtil.getFirstDayOfMonth("yyyy-MM-dd 00:00:00"),AbDateUtil.getLastDayOfMonth("yyyy-MM-dd 23:59:59"),page);

        }else if(eventId==5){//退款
            accountsNet.setData(page,1);
        }else{//全部
            accountsNet.setData("","",page);
        }
        pageIndex = page;
    }

    /**
     * 初始化ListView Item监听
     */
    public void initItemClickListener(){
        mLoadMoreListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if(orderlist.get(position).getRetailCheck()!=1){
                    //跳转待支付详情页面
                    Bundle bundle=new Bundle();
                    bundle.putString("OrderId",orderlist.get(position).getRetailId());
//                    Skip.mNextFroData(mActivity,WaitPayOrderDetailActivity.class,bundle);
                    Skip.mNextFroData(mActivity,OrderDetailActivity.class,bundle);

                }else{
                    Bundle bundle=new Bundle();
                    bundle.putString("OrderId",orderlist.get(position).getRetailId());
                    bundle.putString("CreateName",orderlist.get(position).getCreateName());
                    bundle.putDouble("PayAmount",orderlist.get(position).getPayAmount());
                    bundle.putInt("IsRefund",orderlist.get(position).getIsRefund());
                    //跳转到账单详情
//                    Skip.mNextFroData(mActivity,WaitPayOrderDetailActivity.class,bundle);
                    Skip.mNextFroData(mActivity,OrderDetailActivity.class,bundle);
//                    Skip.mNextFroData(mActivity,CheckAccountDetailActivity.class,bundle);
                }
            }
        });
    }

    /**
     * 初始化视图控件
     */
    public void initView(){

        mLoadMoreListView = (LoadMoreListView) findViewById(R.id.load_more_accounts_list);
        mRefreshAndLoadMoreView= (RefreshAndLoadMoreView) findViewById(R.id.refresh_accounts_and_load_more);

        chooseType= (DropdownButton) findViewById(R.id.chooseType);
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
//                eventId=5;
//                setAdapter();
                Skip.mNext(mActivity, RefundActivity.class);
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

    /**
     * 初始化下拉菜单
     */
    public void initMenu() {
        reset();
        chooseTypeData.add(new DropdownItemObject("全部订单", 0, "全部订单"));
        chooseTypeData.add(new DropdownItemObject("今日", 1, "今日"));
        chooseTypeData.add(new DropdownItemObject("昨天", 2, "昨天"));
        chooseTypeData.add(new DropdownItemObject("本周", 3, "本周"));
        chooseTypeData.add(new DropdownItemObject("本月",4, "本月"));
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

    @Override
    protected void onDestroy() {
        //取消EventBus订阅
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
