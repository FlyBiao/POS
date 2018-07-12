/*
 * Copyright © YOLANDA. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cesaas.android.pos.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.net.CommonNet;
import com.cesaas.android.pos.utils.ACache;
import com.cesaas.android.pos.utils.AbDataPrefsUtil;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.cesaas.android.pos.utils.BitmapHelp;
import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapCommonUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import io.rong.imkit.RongIM;


/**
 * ================================================
 * 作    者：FGB
 * 描    述：BaseActivity
 * 创建日期：2016/5/29
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public abstract class BaseActivity extends FragmentActivity {

    /* Activity集合，便于管理 */
    public static ArrayList<Activity> activityList = new ArrayList<Activity>();
    protected Activity mActivity;
    protected Context mContext;
    //缓存
    protected  ACache mCache;
    protected AbPrefsUtil prefs;
    protected AbDataPrefsUtil dataPrefs;
    protected CommonNet commonNet;
    protected BitmapUtils bitmapUtils;
    protected Gson gson;
    protected Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);//注入view和事件

        activityList.add(this);
        mActivity = this;
        mContext = this;
        mCache = ACache.get(this);
        prefs = AbPrefsUtil.getInstance();
        dataPrefs = AbDataPrefsUtil.getInstance();
        gson=new Gson();
        bundle=new Bundle();
        commonNet=new CommonNet(mContext);


        bitmapUtils = BitmapHelp.getBitmapUtils(mContext.getApplicationContext());
        bitmapUtils.configDefaultBitmapMaxSize(BitmapCommonUtils.getScreenSize(mContext).scaleDown(3));
        bitmapUtils.configDefaultLoadFailedImage(R.mipmap.ic_launcher);

        //注册广播
//        registerReceiver(mHomeKeyEventReceiver, new IntentFilter(
//                Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }

    private View.OnClickListener mBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    private View.OnClickListener mMenuButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onRightButtonClick(v.getId());
        }
    };
    protected void onRightButtonClick(int what) {
    }

    @Override
    protected void onDestroy() {
        //取消EventBus订阅
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 监听是否点击了home键将客户端推到后台
     */
//    private BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {
//        String SYSTEM_REASON = "reason";
//        String SYSTEM_HOME_KEY = "homekey";
//        String SYSTEM_HOME_KEY_LONG = "recentapps";
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
//                String reason = intent.getStringExtra(SYSTEM_REASON);
//                if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
//                    //表示按了home键,程序到了后台
//                    //断开与融云服务器的连接。当调用此接口断开连接后，仍然可以接收 Push 消息
//                    RongIM.getInstance().disconnect();
//                }else if(TextUtils.equals(reason, SYSTEM_HOME_KEY_LONG)){
//                    //表示长按home键,显示最近使用的程序列表
//                }
//            }
//        }
//    };

}
