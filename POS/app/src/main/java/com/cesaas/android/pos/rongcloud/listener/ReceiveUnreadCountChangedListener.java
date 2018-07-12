package com.cesaas.android.pos.rongcloud.listener;

import android.util.Log;

import io.rong.imkit.RongIM;

/**
 * Author FGB
 * Description 接收未读消息的监听器
 * Created at 2017/9/7 10:03
 * Version 1.0
 */

public class ReceiveUnreadCountChangedListener implements RongIM.OnReceiveUnreadCountChangedListener{
    @Override
    public void onMessageIncreased(int i) {
        Log.i("RongCloud","接收未读消息的监听器:"+i);
    }
}
