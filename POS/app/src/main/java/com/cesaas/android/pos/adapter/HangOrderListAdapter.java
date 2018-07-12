package com.cesaas.android.pos.adapter;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.pos.OrderListBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：挂单列表Adapter
 * 创建日期：2016/11/10 09:56
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class HangOrderListAdapter extends BaseQuickAdapter<OrderListBean> {
    public HangOrderListAdapter(int layoutResId, List<OrderListBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, OrderListBean bean) {
        baseViewHolder.setText(R.id.tv_hang_order_id,bean.getBarcodeCode());
    }
}
