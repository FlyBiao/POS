package com.cesaas.android.pos.activity.order;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.adapter.listviewadapter.adapterlibrary.ViewHolder;
import com.cesaas.android.pos.adapter.listviewadapter.adapterlibrary.abslistview.CommonAdapter;
import com.cesaas.android.pos.base.BaseActivity;
import com.cesaas.android.pos.bean.PayOrderListBean;
import com.cesaas.android.pos.custom.LoadMoreListView;
import com.cesaas.android.pos.custom.RefreshAndLoadMoreView;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.global.Urls;
import com.cesaas.android.pos.net.nohttp.HttpListener;
import com.cesaas.android.pos.utils.Skip;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：退款Avtivity
 * 创建日期：2017/1/24
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class RefundActivity extends BaseActivity {

    private LoadMoreListView mLoadMoreListView;//加载更多
    private RefreshAndLoadMoreView mRefreshAndLoadMoreView;//下拉刷新
    private boolean refresh=false;
    private static int pageIndex = 1;//当前页

    private LinearLayout ll_refund_order_list_back;

    private List<PayOrderListBean.OrderList> orderlist=new ArrayList<PayOrderListBean.OrderList>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund);

        initView();
        mBack();
        setAdapter();
    }

    public void initView(){
        mLoadMoreListView = (LoadMoreListView) findViewById(R.id.load_more_refund_list);
        mRefreshAndLoadMoreView= (RefreshAndLoadMoreView) findViewById(R.id.refresh_refund_and_load_more);
    }

    public void mBack(){
        ll_refund_order_list_back= (LinearLayout) findViewById(R.id.ll_refund_order_list_back);
        ll_refund_order_list_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Skip.mBack(mActivity);
            }
        });
    }

    //账单列表回调监听
    public HttpListener<String> payOrderListListener = new HttpListener<String>()
    {
        @Override
        public void onSucceed(int what, Response<String> response)
        {
            PayOrderListBean bean=gson.fromJson(response.get(),PayOrderListBean.class);
            if(bean.isSuccess()==true && bean.TModel!=null) {
                if (bean.TModel.size() > 0&&bean.TModel.size()==50) {
                    mLoadMoreListView.setHaveMoreData(true);
                } else {
                    mLoadMoreListView.setHaveMoreData(false);
                }
                orderlist.addAll(bean.TModel);
            }
        }
        @Override
        public void onFailed(int what, Response<String> response)
        {
            Log.d(Constant.TAG,"Failed:"+response.getException());
        }
    };

    /**
     * 设置Adapter数据适配器
     */
    public void setAdapter(){
        mLoadMoreListView.setAdapter(new CommonAdapter<PayOrderListBean.OrderList>(mContext,R.layout.item_demo,orderlist) {

            @Override
            public void convert(ViewHolder helper, PayOrderListBean.OrderList bean, int postion) {

                helper.setText(R.id.tv_order_id,bean.RetailId);
                helper.setText(R.id.tv_accounts_pay_amount,bean.ConsumeAmount+"");
                helper.setText(R.id.tv_pay_date,bean.CreateTime);

                if(bean.PayCategory==1){
                    helper.setText(R.id.tv_pay_status,"退款");
                    ((TextView)helper.getView(R.id.tv_pay_status)).setTextColor(mContext.getResources().getColor(R.color.forestgreen));
                }else{
                    helper.setText(R.id.tv_pay_status,"支付成功");
                    ((TextView)helper.getView(R.id.tv_pay_status)).setTextColor(mContext.getResources().getColor(R.color.gray_text));
                }
            }

        });
        initData();
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
        Request<String> request = NoHttp.createStringRequest(Urls.PAY_ORDER_LIST, RequestMethod.POST);
        request.add("PayCategory",1);//0:正常支付，1：退款
        commonNet.requestNetTask(request, payOrderListListener,page);
        pageIndex = page;
    }
}
