package com.cesaas.android.pos.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：多个Item
 * 创建日期：2016/10/28 11:47
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class MultipleItemBean implements MultiItemEntity {

    public static final int TEXT = 1;
    public static final int TEXT2 = 2;
    public static final int BIG_IMG_SPAN_SIZE = 3;
    public static final int TEXT_SPAN_SIZE = 3;
    public static final int IMG_SPAN_SIZE = 1;
    private int itemType;
    private int spanSize;

    public MultipleItemBean(int itemType, int spanSize, String content) {
        this.itemType = itemType;
        this.spanSize = spanSize;
        this.content = content;
    }

    public MultipleItemBean(int itemType, int spanSize) {
        this.itemType = itemType;
        this.spanSize = spanSize;
    }

    public int getSpanSize() {
        return spanSize;
    }

    public void setSpanSize(int spanSize) {
        this.spanSize = spanSize;
    }

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    @Override
    public int getItemType() {
        return 0;
    }
}
