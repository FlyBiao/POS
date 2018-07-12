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
import com.cesaas.android.pos.activity.order.PayDetailsActivity;
import com.cesaas.android.pos.activity.order.RefundActivity;
import com.cesaas.android.pos.adapter.listviewadapter.adapterlibrary.ViewHolder;
import com.cesaas.android.pos.adapter.listviewadapter.adapterlibrary.abslistview.CommonAdapter;
import com.cesaas.android.pos.base.BaseActivity;
import com.cesaas.android.pos.bean.PayListBean;
import com.cesaas.android.pos.bean.PosOrderList;
import com.cesaas.android.pos.bean.PosOrderListBean;
import com.cesaas.android.pos.bean.ResultPayListBean;
import com.cesaas.android.pos.bean.SortBean;
import com.cesaas.android.pos.custom.LoadMoreListView;
import com.cesaas.android.pos.custom.RefreshAndLoadMoreView;
import com.cesaas.android.pos.menu.DropdownButton;
import com.cesaas.android.pos.menu.DropdownListView;
import com.cesaas.android.pos.menu.bean.DropdownItemObject;
import com.cesaas.android.pos.net.xutils.net.CheckAccountsNet;
import com.cesaas.android.pos.net.xutils.net.PayListNet;
import com.cesaas.android.pos.utils.AbDateUtil;
import com.cesaas.android.pos.utils.Skip;
import com.cesaas.android.pos.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：支付流水列表【ListView实现】
 * 创建日期：2017/12/19 14:42
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class PayListActivity extends BaseActivity implements DropdownListView.Container{

    private LoadMoreListView mLoadMoreListView;//加载更多
    private RefreshAndLoadMoreView mRefreshAndLoadMoreView;//下拉刷新
    private boolean refresh=false;
    private static int pageIndex = 1;//当前页
    private int eventId=0;

    private View mask;
    private TextView tv_not_data;
    private LinearLayout ll_check_accounts_back;

    private DropdownButton chooseType;
    private DropdownListView dropdownType;
    private DropdownListView currentDropdownList;
    Animation dropdown_in, dropdown_out, dropdown_mask_out;

    private List<DropdownItemObject> chooseTypeData = new ArrayList<>();//选择类型
    private List<PayListBean> payListBeens;
    private JSONArray dateArray;//时间数组，排序使用
    private PayListNet payListNet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_list);

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
    public void onDataSynEvent(ResultPayListBean bean) {
        payListBeens=new ArrayList<>();
        if (bean.isSuccess()==true) {
            if (bean.TModel.size() > 0&&bean.TModel.size()==50) {
                mLoadMoreListView.setHaveMoreData(true);
            } else {
                mLoadMoreListView.setHaveMoreData(false);
            }
            if(bean.TModel.size()!=0){
                payListBeens.addAll(bean.TModel);
                dateArray=new JSONArray();
                SortBean sortBean=null;
                for(int i=0;i<payListBeens.size();i++){
                    sortBean=new SortBean();
                    sortBean.setField(payListBeens.get(i).getCreateTime());
                    sortBean.setValue("desc");
                    dateArray.put(sortBean.getSort());
                }
            }

            mLoadMoreListView.setAdapter(new CommonAdapter<PayListBean>(mContext,R.layout.item_pay_list,payListBeens) {
                @Override
                public void convert(ViewHolder helper, PayListBean bean, int postion) {
                    helper.setText(R.id.tv_order_id,"流水号("+bean.getPayId()+")");
                    helper.setText(R.id.tv_pay_amount,bean.getPayment()+"");
                    helper.setText(R.id.tv_pay_date,bean.getPayDate());
                    switch (bean.getPayType()){
                        case 2:
                            helper.setText(R.id.tv_pay_type,"微信支付");
                            break;
                        case 3:
                            helper.setText(R.id.tv_pay_type,"支付宝支付");
                            break;
                        case 4:
                            helper.setText(R.id.tv_pay_type,"银联支付");
                            break;
                        case 5:
                            helper.setText(R.id.tv_pay_type,"现金支付");
                            break;
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
//        if (page == 1) {
//            orderlist.clear();
//        }
        payListNet=new PayListNet(mContext);
        if(eventId==1){//今日
            payListNet.setData(AbDateUtil.getCurrentDate("yyyy-MM-dd 00:00:00"),AbDateUtil.getCurrentDate("yyyy-MM-dd 23:59:59"),page);
        }else if(eventId==2){//昨天
            payListNet.setData(AbDateUtil.YesTerDay("yyyy-MM-dd 00:00:00"),AbDateUtil.getCurrentDate("yyyy-MM-dd 23:59:59"),page);

        }else if(eventId==3){//本周
            payListNet.setData(AbDateUtil.getFirstDayOfWeek("yyyy-MM-dd 00:00:00"),AbDateUtil.getLastDayOfWeek("yyyy-MM-dd 23:59:59"),page);

        }else if(eventId==4){//本月
            payListNet.setData(AbDateUtil.getFirstDayOfMonth("yyyy-MM-dd 00:00:00"),AbDateUtil.getLastDayOfMonth("yyyy-MM-dd 23:59:59"),page);

        }else if(eventId==5){//退款
            payListNet.setData(page,1);
        }else{//全部
            payListNet.setData("","",page);
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
                PayListBean bean=new PayListBean();
                bean.setPayType(payListBeens.get(position).getPayType());
                bean.setCreateTime(payListBeens.get(position).getCreateTime());
                bean.setEquipmentCode(payListBeens.get(position).getEquipmentCode());
                bean.setPayDate(payListBeens.get(position).getPayDate());
                bean.setPayment(payListBeens.get(position).getPayment());
                bean.setPayNo(payListBeens.get(position).getPayNo());
                bean.setTraceAudit(payListBeens.get(position).getTraceAudit());
                bean.setPayId(payListBeens.get(position).getPayId());
                bean.setTId(payListBeens.get(position).getTId());
                bean.setSheetId(payListBeens.get(position).getSheetId());
                bean.setSheetCategory(payListBeens.get(position).getSheetCategory());
                bean.setVoucherRecord(payListBeens.get(position).getVoucherRecord());
                bean.setCashier(payListBeens.get(position).getCashier());
                //跳转待支付详情页面
                Bundle bundle=new Bundle();
                bundle.putSerializable("PayList",(Serializable) bean);
                Skip.mNextFroData(mActivity,PayDetailsActivity.class,bundle);
            }
        });
    }

    /**
     * 初始化视图控件
     */
    public void initView(){

        mLoadMoreListView = (LoadMoreListView) findViewById(R.id.load_pay_list);
        mRefreshAndLoadMoreView= (RefreshAndLoadMoreView) findViewById(R.id.refresh_pay_and_load_more);

        chooseType= (DropdownButton) findViewById(R.id.chooseType);
        tv_not_data= (TextView) findViewById(R.id.tv_not_data);
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
