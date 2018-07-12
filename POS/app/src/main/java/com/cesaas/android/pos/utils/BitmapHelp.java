package com.cesaas.android.pos.utils;

import android.content.Context;

import com.lidroid.xutils.BitmapUtils;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：
 * 创建日期：2016/6/7 11:51
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class BitmapHelp {
    private BitmapHelp() {
    }

    private static BitmapUtils bitmapUtils;

    /**
     * BitmapUtils不是单例的 根据需要重载多个获取实例的方法
     *
     * @param appContext
     *            application context
     * @return
     */
    public static BitmapUtils getBitmapUtils(Context appContext) {
        if (bitmapUtils == null) {
            bitmapUtils = new BitmapUtils(appContext);
        }
        return bitmapUtils;
    }
}
