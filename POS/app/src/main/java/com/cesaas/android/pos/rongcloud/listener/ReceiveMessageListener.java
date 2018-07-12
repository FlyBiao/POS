package com.cesaas.android.pos.rongcloud.listener;

import android.util.Log;

import com.cesaas.android.pos.rongcloud.bean.ReceiveMessageBean;

import org.greenrobot.eventbus.EventBus;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

/**
 * Author FGB
 * Description 接收消息监听
 * Created at 2017/9/7 9:59
 * Version 1.0
 */

public class ReceiveMessageListener implements RongIMClient.OnReceiveMessageListener{
    /**
     * 收到消息的处理。
     *
     * @param message 收到的消息实体。
     * @param i    剩余未拉取消息数目。
     * @return 收到消息是否处理完成，true 表示自己处理铃声和后台通知，false 走融云默认处理方式。
     */
    @Override
    public boolean onReceived(Message message, int i) {
        TextMessage textMessage= (TextMessage) message.getContent();

        Log.i("RongCloud","接收消息监听:"
                +textMessage.getContent()+"  "
                +textMessage.getExtra()+"  "
                +textMessage.getSearchableWord()+"  "

                +message.getConversationType()+"  "
                +message.getTargetId()+"  "+message.getUId()+"   "+message.getSenderUserId()
                +i);

        ReceiveMessageBean messageBean=new ReceiveMessageBean();
        messageBean.setContent(textMessage.getContent());
        messageBean.setExtra(textMessage.getExtra());
        EventBus.getDefault().post(messageBean);

        return false;
    }
}
