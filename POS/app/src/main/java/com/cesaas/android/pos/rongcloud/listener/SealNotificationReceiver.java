package com.cesaas.android.pos.rongcloud.listener;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.cesaas.android.pos.rongcloud.bean.ReceiveMessageBean;

import org.greenrobot.eventbus.EventBus;

import io.rong.push.notification.PushMessageReceiver;
import io.rong.push.notification.PushNotificationMessage;

/**
 * Author FGB
 * Description Push 消息监听
 * Created at 2017/9/7 10:01
 * Version 1.0
 */

public class SealNotificationReceiver extends PushMessageReceiver {

    /* push 通知到达事件*/
    @Override
    public boolean onNotificationMessageArrived(Context context, PushNotificationMessage message) {
        // 返回 false, 会弹出融云 SDK 默认通知; 返回 true, 融云 SDK 不会弹通知, 通知需要由您自定义。
        Log.i("RongCloud","push通知到达事件:"
                +message.getPushContent()
                +"  "+message.getConversationType()
                +"  "+ message.getPushData()
                +" "+message.getExtra()
                +" "+message.getObjectName()
                +" "+message.getSenderPortrait()
                +" "+message.getSenderName()
                +" "+message.getReceivedTime()
                +""+message.getPushTitle());

        ReceiveMessageBean messageBean=new ReceiveMessageBean();
        messageBean.setContent(message.getPushContent());
        messageBean.setExtra(message.getExtra());
        EventBus.getDefault().post(messageBean);


        return false;
    }

    /* push 通知点击事件 */
    @Override
    public boolean onNotificationMessageClicked(Context context, PushNotificationMessage message) {
        // 返回 false, 会走融云 SDK 默认处理逻辑, 即点击该通知会打开会话列表或会话界面; 返回 true, 则由您自定义处理逻辑。
        Log.i("RongCloud","push通知点击事件:"+message.getPushContent()+"  "+message.getConversationType());
        return false;
    }
}
