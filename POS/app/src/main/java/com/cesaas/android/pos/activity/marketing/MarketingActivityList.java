package com.cesaas.android.pos.activity.marketing;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.base.BaseActivity;
import com.cesaas.android.pos.bean.MarketingActivityBean;
import com.cesaas.android.pos.bean.ResultMarketingActivityBean;
import com.cesaas.android.pos.bean.Styles;
import com.cesaas.android.pos.global.Urls;
import com.cesaas.android.pos.net.nohttp.HttpListener;
import com.cesaas.android.pos.net.xutils.net.GetActivityResultNet;
import com.cesaas.android.pos.utils.Skip;
import com.cesaas.android.pos.utils.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * 营销方案活动列表
 */
public class MarketingActivityList extends BaseActivity implements View.OnClickListener{

    private LinearLayout ll_marketing_activity_back;

    private List<MarketingActivityBean> marketingActivityBeen=new ArrayList<MarketingActivityBean>();
    private RecyclerView mRecyclerView;
    private SettleAccountsAdapter adapter;
    private GetActivityResultNet getActivityResultNet;

    private JSONArray styleJsonArray;
    public JSONArray styleArray=new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketing_activity_list);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_marketing_activity_list);
        ll_marketing_activity_back= (LinearLayout) findViewById(R.id.ll_marketing_activity_back);
        ll_marketing_activity_back.setOnClickListener(this);
        Styles styles=new Styles();
        styleJsonArray=styleArray.put(styles.getStyleArray());
        getStatisticsListener();
    }

    /**
     * POS收银
     * 注：默认查询当天数据
     */
    public void getStatisticsListener(){
        Request<String> request = NoHttp.createStringRequest(Urls.MARKETING_ACTIVITY_LIST, RequestMethod.POST);
        commonNet.requestNetTask(request,getStatisticsListener);
    }

    /**
     * POS收银数据统计回调
     */
    private HttpListener<String> getStatisticsListener = new HttpListener<String>()
    {
        @Override
        public void onSucceed(int what, Response<String> response)
        {
            ResultMarketingActivityBean bean = gson.fromJson(response.get(), ResultMarketingActivityBean.class);
            marketingActivityBeen=new ArrayList<MarketingActivityBean>();
            if(bean.isSuccess()==true){
                marketingActivityBeen.addAll(bean.TModel);
                initAdapter();
            }else{
                ToastUtils.getLongToast(mContext,bean.getMessage());
            }


        }
        @Override
        public void onFailed(int what, Response<String> response)
        {
            ToastUtils.show(response.get());
        }
    };

    public void initAdapter(){
        adapter=new SettleAccountsAdapter(R.layout.item_marketing_activity,marketingActivityBeen);
        adapter.openLoadAnimation();
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);

        //设置item点击事件
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener( ){

            @Override
            public void SimpleOnItemClick(BaseQuickAdapter adapter, View view, int position) {
                getActivityResultNet=new GetActivityResultNet(mContext);
                getActivityResultNet.setData(marketingActivityBeen.get(position).ActivityId,styleJsonArray);
//                    ToastUtils.getLongToast(mContext,"选择："+marketingActivityBeen.get(position).ActivityId);
            }
        });
    }

    /**
     * Adapter
     */
    public class SettleAccountsAdapter extends BaseQuickAdapter<MarketingActivityBean> {
        public SettleAccountsAdapter(int layoutResId, List<MarketingActivityBean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, MarketingActivityBean bean) {
            baseViewHolder.setText(R.id.tv_activity_name,bean.Description);
            baseViewHolder.setText(R.id.tv_activity_plan_name,bean.PlanName);
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_marketing_activity_back://返回
                Skip.mBack(mActivity);
                break;
        }
    }
}
