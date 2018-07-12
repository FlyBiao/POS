package com.cesaas.android.pos.bean.printer;

import android.os.Parcelable;

import com.wangpos.poscore.util.ParamMap;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：
 * 创建日期：2016/11/3 22:15
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public abstract class BaseBean implements Parcelable {

    /**
     * 加载来自服务器的数据
     * @param
     *
     * @throws
     */
    public abstract void loadFromServerData(ParamMap param);
}
