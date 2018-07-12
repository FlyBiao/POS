package com.cesaas.android.pos.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.base.BaseActivity;
import com.cesaas.android.pos.bean.ResultGetTokenBean;
import com.cesaas.android.pos.global.App;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.net.xutils.net.GetTokenNet;
import com.cesaas.android.pos.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

public class TestRongColudActivity extends BaseActivity {


    private GetTokenNet getTokenNet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_rong);

        //通过EventBus订阅事件
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
//
        getTokenNet=new GetTokenNet(mContext);
        getTokenNet.setData();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(ResultGetTokenBean msg) {
        if(msg.IsSuccess==true){
            prefs.putString("RongToken", msg.TModel.token+"");
            connect(msg.TModel.token);
        }else{
            ToastUtils.getLongToast(mContext,"获取融云ToKen失败！");
        }
    }


    /**
     * <p>连接服务器，在整个应用程序全局，只需要调用一次，需在 {@link #//init(Context)} 之后调用。</p>
     * <p>如果调用此接口遇到连接失败，SDK 会自动启动重连机制进行最多10次重连，分别是1, 2, 4, 8, 16, 32, 64, 128, 256, 512秒后。
     * 在这之后如果仍没有连接成功，还会在当检测到设备网络状态变化时再次进行重连。</p>
     *
     * @param token    从服务端获取的用户身份令牌（Token）。
     * @param //callback 连接回调。
     * @return RongIM  客户端核心类的实例。
     */
    private void connect(String token) {

        if (getApplicationInfo().packageName.equals(App.getCurProcessName(getApplicationContext()))) {

            RongIM.connect(token, new RongIMClient.ConnectCallback() {

                /**
                 * Token 错误。可以从下面两点检查 1.  Token 是否过期，如果过期您需要向 App Server 重新请求一个新的 Token
                 *                  2.  token 对应的 appKey 和工程里设置的 appKey 是否一致
                 */
                @Override
                public void onTokenIncorrect() {
                    Log.d(Constant.TAG, "--onTokenIncorrect" );
                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token 对应的用户 id
                 */
                @Override
                public void onSuccess(String userid) {
                    Log.d(Constant.TAG, "--onSuccess" + userid);
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    Log.d(Constant.TAG, "--ErrorCode" +errorCode);
                }
            });
        }
    }

}
