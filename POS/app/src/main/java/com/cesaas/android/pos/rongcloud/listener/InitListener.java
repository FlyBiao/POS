package com.cesaas.android.pos.rongcloud.listener;

import android.app.Activity;
import android.content.Context;

import com.cesaas.android.pos.rongcloud.custom.CustomizeMessageOrderItemProvider;
import com.cesaas.android.pos.rongcloud.custom.CustomizeOrderMessage;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

/**
 * Author FGB
 * Description 初始化监听
 * Created at 2017/9/7 15:25
 * Version 1.0
 */

public class InitListener {

    public static  void init(String userid, Context ct,Activity mActivity){
        //接收消息监听
        RongIM.setOnReceiveMessageListener(new ReceiveMessageListener());
        //接收所有未读消息消息的监听器。
        RongIM.getInstance().setOnReceiveUnreadCountChangedListener(new ReceiveUnreadCountChangedListener());
        //设置会话界面操作的监听器。
        RongIM.setConversationBehaviorListener(new MyConversationBehaviorListener());

        //注册自定义消息
        RongIM.registerMessageType(CustomizeOrderMessage.class);
        //注册自定义订单消息模板
        RongIM.getInstance().registerMessageTemplate(new CustomizeMessageOrderItemProvider(ct,mActivity));

        // 接收指定会话类型
        Conversation.ConversationType conversationType[]={
                Conversation.ConversationType.PRIVATE,
                Conversation.ConversationType.GROUP,
                Conversation.ConversationType.DISCUSSION,
                Conversation.ConversationType.SYSTEM,
                Conversation.ConversationType.PUSH_SERVICE,
                Conversation.ConversationType.CUSTOMER_SERVICE,
                Conversation.ConversationType.APP_PUBLIC_SERVICE};

        RongIM.getInstance().setOnReceiveUnreadCountChangedListener(
                new ReceiveUnreadCountChangedListener(), conversationType);
        //连接状态监听
        RongIM.setConnectionStatusListener(new ConnectionStatusListener(ct));

    }
}
