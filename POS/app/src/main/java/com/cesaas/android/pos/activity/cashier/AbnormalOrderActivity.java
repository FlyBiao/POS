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
import com.cesaas.android.pos.adapter.AbnormalOrderAdapter;
import com.cesaas.android.pos.adapter.listviewadapter.adapterlibrary.ViewHolder;
import com.cesaas.android.pos.adapter.listviewadapter.adapterlibrary.abslistview.CommonAdapter;
import com.cesaas.android.pos.base.BaseActivity;
import com.cesaas.android.pos.base.BaseRecyclerView;
import com.cesaas.android.pos.bean.PosOrderList;
import com.cesaas.android.pos.bean.PosOrderListBean;
import com.cesaas.android.pos.bean.SortBean;
import com.cesaas.android.pos.custom.LoadMoreListView;
import com.cesaas.android.pos.custom.RefreshAndLoadMoreView;
import com.cesaas.android.pos.db.bean.AbnormalOrderBean;
import com.cesaas.android.pos.db.bean.PosPayBean;
import com.cesaas.android.pos.db.pay.PosSqliteDatabaseUtils;
import com.cesaas.android.pos.listener.OnItemClickListener;
import com.cesaas.android.pos.menu.DropdownButton;
import com.cesaas.android.pos.menu.DropdownListView;
import com.cesaas.android.pos.menu.bean.DropdownItemObject;
import com.cesaas.android.pos.net.xutils.net.CheckAccountsNet;
import com.cesaas.android.pos.utils.AbDateUtil;
import com.cesaas.android.pos.utils.DecimalFormatUtils;
import com.cesaas.android.pos.utils.Skip;
import com.cesaas.android.pos.utils.ToastUtils;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：异常单处理
 * 创建日期：2017/12/5 20:42
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class AbnormalOrderActivity extends BaseActivity implements View.OnClickListener{

    private TextView tvTitle;
    private LinearLayout llBack;
    private SwipeMenuRecyclerView rvView;

    private List<PosPayBean> payBeanList;
    private AbnormalOrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abnormal_order);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initView();
        initData();
    }

    private void initData() {
        PosSqliteDatabaseUtils.selectByOrderStatusData(mContext,"0");
    }

    private void initView() {
        tvTitle= (TextView) findViewById(R.id.tv_base_title);
        tvTitle.setText("异常单");
        llBack= (LinearLayout) findViewById(R.id.ll_base_title_back);
        llBack.setOnClickListener(this);
        rvView= (SwipeMenuRecyclerView) findViewById(R.id.rv_view);
        BaseRecyclerView.initRecyclerView(mContext,rvView,false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(List<PosPayBean> msg) {
        if(msg!=null && msg.size()!=0){
            payBeanList=new ArrayList<>();
            payBeanList.addAll(msg);
            /**
             * 按照时间排序显示
             */
            Collections.sort(payBeanList, new Comparator<PosPayBean>() {
                @Override
                public int compare(PosPayBean lhs, PosPayBean rhs) {
                    Date date0=null;
                    Date date1=null;
                    for (int i = 0; i < payBeanList.size(); i++) {
                        date0=AbDateUtil.stringToDate(payBeanList.get(i).getCreateTime());
                        date1=AbDateUtil.stringToDate(payBeanList.get(i).getCreateTime());
                    }
                    // 对日期字段进行升序，如果欲降序可采用after方法
                    if (date0.after(date1)) {
                        return 1;
                    }
                    return -1;
                }
            });
            adapter=new AbnormalOrderAdapter(payBeanList,mContext);
            adapter.setOnItemClickListener(onItemClickListener);
            rvView.setAdapter(adapter);
        }else{
            ToastUtils.getLongToast(mContext,"暂无异常订单！");
        }
    }

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            bundle.putString("orderId",payBeanList.get(position).getOrderNo());
            Skip.mNextFroData(mActivity,AbnormalOrderInfoActivity.class,bundle);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_base_title_back:
                Skip.mBack(mActivity);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        //取消EventBus订阅
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
