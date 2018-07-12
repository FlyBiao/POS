package com.cesaas.android.pos.rongcloud.listener;

import android.content.Context;
import android.util.Log;

import com.cesaas.android.pos.utils.ToastUtils;

import io.rong.imlib.RongIMClient;

/**
 * Author FGB
 * Description 连接状态监听
 * Created at 2017/9/7 15:17
 * Version 1.0
 */

public class ConnectionStatusListener  implements RongIMClient.ConnectionStatusListener{
    public Context ct;
    public ConnectionStatusListener(Context ct){
        this.ct=ct;
    }
    @Override
    public void onChanged(ConnectionStatus connectionStatus) {
        switch (connectionStatus){
            case CONNECTED://连接成功。
                Log.i("RongCloud","连接成功:");
                break;
            case DISCONNECTED://断开连接。
                Log.i("RongCloud","断开连接:");
                break;
            case CONNECTING://连接中。
                Log.i("RongCloud","连接中:");
                break;
            case NETWORK_UNAVAILABLE://网络不可用。
                ToastUtils.getLongToast(ct,"当前网络不可用!");
                Log.i("RongCloud","网络不可用:");
                break;
            case KICKED_OFFLINE_BY_OTHER_CLIENT://用户账户在其他设备登录，本机会被踢掉线
                Log.i("RongCloud","用户账户在其他设备登录:");
                ToastUtils.getLongToast(ct,"用户账户在其他设备登录!");
                break;
        }
    }
}
