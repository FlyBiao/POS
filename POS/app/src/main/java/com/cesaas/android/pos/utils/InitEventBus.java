package com.cesaas.android.pos.utils;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

/**
 * Author FGB
 * Description
 * Created at 2017/9/4 13:55
 * Version 1.0
 */

public class InitEventBus {

    public static void initEventBus(Context ct){
        //通过EventBus订阅事件
        if (!EventBus.getDefault().isRegistered(ct)) {
            EventBus.getDefault().register(ct);
        }
    }
}
