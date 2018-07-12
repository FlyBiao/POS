package com.cesaas.android.pos.adapter;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.bean.MultipleItemBean;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：多个Item快速适配器
 * 创建日期：2016/10/28 11:49
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class MultipleItemQuickAdapter extends BaseMultiItemQuickAdapter<MultipleItemBean> {

    public MultipleItemQuickAdapter(List<MultipleItemBean> data) {
        super(data);
        addItemType(MultipleItemBean.TEXT, R.layout.item_settle_accounts_title);
        addItemType(MultipleItemBean.TEXT2, R.layout.item_settle_accounts_content);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, MultipleItemBean multipleItemBean) {
        switch (baseViewHolder.getItemViewType()) {
            case MultipleItemBean.TEXT:
                baseViewHolder.setText(R.id.tv_title, multipleItemBean.getContent());
                break;
            case MultipleItemBean.TEXT2:
                // set img data
                break;
        }
    }
}
