package com.cesaas.android.pos.adapter;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.db.bean.OrderDataBean;
import com.cesaas.android.pos.pos.OrderListBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：获取挂单列表Adapter
 * 创建日期：2016/11/10 09:56
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class GetHangOrderListAdapter extends BaseQuickAdapter<OrderDataBean> {
    public GetHangOrderListAdapter(int layoutResId, List<OrderDataBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, OrderDataBean bean) {
        baseViewHolder.setText(R.id.tv_get_hang_order_id,bean.getOrderNo());
        baseViewHolder.setText(R.id.tv_get_hang_order_title,bean.getShopName());
        baseViewHolder.setText(R.id.tv_get_hang_order_date,bean.getDate());
        baseViewHolder.setText(R.id.tv_get_hang_order_quantity,bean.getShopCount()+"");
    }
}
