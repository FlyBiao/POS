package com.cesaas.android.pos.rongcloud.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cesaas.android.pos.R;


/**
 * Created by Bob on 15/8/18.
 * 聚合会话列表
 * 什么是聚合会话列表？
 */
public class SubConversationListActivtiy extends FragmentActivity {

    private TextView tv_user_nick1;
    private RelativeLayout mBack;
    /**
     * 聚合类型
     */
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subconversationlist);

        tv_user_nick1 = (TextView) findViewById(R.id.tv_user_nick1);
        mBack = (RelativeLayout) findViewById(R.id.back);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getActionBarTitle();
    }

    /**
     * 通过 intent 中的数据，得到当前的 targetId 和 type
     */
    private void getActionBarTitle() {

        Intent intent = getIntent();

        type = intent.getData().getQueryParameter("type");

        if (type.equals("group")) {
        	tv_user_nick1.setText("聚合群组");
        } else if (type.equals("private")) {
        	tv_user_nick1.setText("聚合单聊");
        } else if (type.equals("discussion")) {
        	tv_user_nick1.setText("聚合讨论组");
        } else if (type.equals("system")) {
        	tv_user_nick1.setText("聚合系统会话");
        } else {
        	tv_user_nick1.setText("聚合");
        }

    }
}

