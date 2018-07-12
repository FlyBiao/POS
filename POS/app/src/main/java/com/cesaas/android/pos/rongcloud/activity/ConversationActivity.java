package com.cesaas.android.pos.rongcloud.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.cesaas.android.pos.R;
import com.cesaas.android.pos.global.App;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.cesaas.android.pos.utils.ToastUtils;

import java.util.Locale;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.message.TextMessage;

/**
 * Created by fgb
 * 会话页面
 * 1，设置 ActionBar title
 * 2，加载会话页面
 * 3，push 和 通知 判断
 */
public class ConversationActivity extends FragmentActivity {
	
	private TextView tv_user_nick1;
    private RelativeLayout mBack;

    private String mTargetId;
    private String userNick;
    
    private AbPrefsUtil abpUtil;
    private String RongToken;
    
    private ConversationActivity activity;
    
    /**
     * 刚刚创建完讨论组后获得讨论组的id 为targetIds，需要根据 为targetIds 获取 targetId
     */
    private String mTargetIds;

    /**
     * 会话类型
     */
    private Conversation.ConversationType mConversationType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation);
        abpUtil = AbPrefsUtil.getInstance();
        
        activity=this;
        
        tv_user_nick1 = (TextView) findViewById(R.id.tv_user_nick1);
        mBack = (RelativeLayout) findViewById(R.id.back);
        
        Intent intent = getIntent();
        setActionBar();

        getIntentDate(intent);

        isReconnect(intent);
        
        RongToken=abpUtil.getString("RongToken");
        reconnect(RongToken);
        
    }

    /**
     * 展示如何从 Intent 中得到 融云会话页面传递的 Uri
     */
    private void getIntentDate(Intent intent) {

        mTargetId = intent.getData().getQueryParameter("targetId");//获取用户id
        userNick=intent.getData().getQueryParameter("title");//获取用户昵称
        mTargetIds = intent.getData().getQueryParameter("targetIds");
        intent.getData().getLastPathSegment();//获得当前会话类型
        mConversationType = Conversation.ConversationType.valueOf(intent.getData().getLastPathSegment().toUpperCase(Locale.getDefault()));

        enterFragment(mConversationType, mTargetId);//加载会话页面
        setActionBarTitle(userNick);//设置ActionBar Title
    }


    /**
     * 加载会话页面 ConversationFragment
     *
     * @param mConversationType
     * @param mTargetId
     */
    @SuppressLint("NewApi")
	private void enterFragment(Conversation.ConversationType mConversationType, String mTargetId) {

        ConversationFragment fragment = (ConversationFragment) getSupportFragmentManager().findFragmentById(R.id.conversation);

        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话非聚合显示
                  .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")//设置群组会话聚合显示
                  .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//设置讨论组会话非聚合显示
                  .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//设置系统会话非聚合显示
                .appendQueryParameter("targetId", mTargetId).build();

        fragment.setUri(uri);
    }

    /**
     * 发送消息。
     *
     * @param type        会话类型。
     * @param targetId    目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @param content     消息内容。
     * @param pushContent push 时提示内容，为空时提示文本内容。
     * @param callback    发送消息的回调。
     * @return
     */
    @SuppressWarnings("deprecation")
	public void userSendMessage(String targetId,String content){
    	
    	RongIM.getInstance().getRongIMClient().sendMessage(Conversation.ConversationType.PRIVATE,
    			targetId, TextMessage.obtain(content), "", "",
    			
    			new RongIMClient.SendMessageCallback() {
    	    @Override
    	    public void onError(Integer messageId, RongIMClient.ErrorCode e) {
//    	    	ToastFactory.show(getApplicationContext(), "发送失败"+e.getValue(), ToastFactory.CENTER);
    	    }

    	    @Override
    	    public void onSuccess(Integer integer) {
    	    	
    	    }
    	});
    }

    /**
     * 判断消息是否是 push 消息
     */
    private void isReconnect(Intent intent) {

        //push或通知过来
        if (intent != null && intent.getData() != null && intent.getData().getScheme().equals("rong")) {

            //通过intent.getData().getQueryParameter("push") 为true，判断是否是push消息
            if (intent.getData().getQueryParameter("push") != null
                    && intent.getData().getQueryParameter("push").equals("true")) {

                reconnect(RongToken);
            } else {
                //程序切到后台，收到消息后点击进入,会执行这里
                if (RongIM.getInstance() == null || RongIM.getInstance().getRongIMClient() == null) {

                    reconnect(RongToken);
                } else {
                    enterFragment(mConversationType, mTargetId);//加载会话页面 
                }
            }
        }
    }

    /**
     * 设置 actionbar 事件
     */
    private void setActionBar() {

    	tv_user_nick1 = (TextView) findViewById(R.id.tv_user_nick1);
        mBack = (RelativeLayout) findViewById(R.id.back);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    /**
     * 设置 actionbar title
     */
    private void setActionBarTitle(String targetid) {

    	if(targetid!=null){
    		tv_user_nick1.setText("正在和"+targetid+"聊天");
    	}else{
    		tv_user_nick1.setText("正在聊天中...");
    	}
    	
    }

    /**
     * 重连
     *
     * @param token
     */
    @SuppressLint("NewApi")
	private void reconnect(String token) {

        if (getApplicationInfo().packageName.equals(App.getCurProcessName(getApplicationContext()))) {

            RongIM.connect(token, new RongIMClient.ConnectCallback() {
                @Override
                public void onTokenIncorrect() {
//                	ToastUtils.show(getApplicationContext(), "onTokenIncorrect");
                    Log.i("RongCloud","onTokenIncorrect:");
                }

				@Override
                public void onSuccess(String s) {

                    enterFragment(mConversationType, mTargetId);//加载会话页面 
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
//                	ToastFactory.show(getApplicationContext(), ""+errorCode.getValue(), ToastFactory.CENTER);
                }
            });
        }
    }
}
