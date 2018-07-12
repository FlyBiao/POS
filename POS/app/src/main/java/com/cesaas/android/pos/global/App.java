package com.cesaas.android.pos.global;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.multidex.MultiDex;

import com.cesaas.android.pos.db.pay.DBConstant;
import com.cesaas.android.pos.db.pay.PosSqliteDatabaseUtils;
import com.cesaas.android.pos.net.xutils.service.NetworkConnectivityListener;
import com.cesaas.android.pos.net.xutils.utils.NetworkUtil;
import com.cesaas.android.pos.pos.InitPosCore;
import com.cesaas.android.pos.utils.AbDataPrefsUtil;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.cesaas.android.pos.utils.PosSDKTools;
import com.lidroid.xutils.bitmap.BitmapCommonUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.rong.imkit.RongIM;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：全局应用
 * 创建日期：2016/7/30 23:16
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class App extends Application {
    private static App _instance;
    private static App myapp = null;
    public BitmapDisplayConfig bitmapConfig;
    public ExecutorService mExecutorService = null; // 线程池
    private boolean isDownload = false; // 标示正在下载
    protected AbPrefsUtil prefs;

    private int netType; // 网络类型
    private NetworkConnectivityListener mNetChangeReceiver;
    private NetworkConnectivityListener.NetworkCallBack mNetworkCallBack = new NetworkConnectivityListener.NetworkCallBack() {
        public void getSelfNetworkType(int type) {
            if (netType != type) {
                // setAvailable_http_host("");
            }
            setNetType(type);
        }
    };

    public static App mInstance() { // 单例实例化
        return myapp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化sqlite
        PosSqliteDatabaseUtils.createDB(getApplicationContext(), DBConstant.DB, DBConstant.VERSION);
        myapp = this;
        mExecutorService = Executors.newFixedThreadPool(8);
        _instance = this;
        prefs = AbPrefsUtil.getInstance();

        //初始化NoHttp
        NoHttp.init(this);
        Logger.setTag("NoHttpSample");
        // 开启NoHttp的调试模式, 能看到请求过程和日志
        Logger.setDebug(true);
        //初始化SharedPreference
        initPrefs();
        initImage();

        initNet();

        //初始化pos sdk，只需要在apk启动入口初始化一次，当应用完全退出是会自动调用sdk的onDestroy()
        PosSDKTools.initSdk(this);

        if (getApplicationInfo().packageName
                .equals(getCurProcessName(getApplicationContext()))
                || "io.rong.push"
                .equals(getCurProcessName(getApplicationContext()))) {

            /**
             * IMKit SDK调用第一步 初始化
             */
            RongIM.init(this);
        }
    }

    private void initNet() {
        netType = NetworkUtil.getSelfNetworkType(this);
        mNetChangeReceiver = new NetworkConnectivityListener();
        mNetChangeReceiver.startListening(this);
        addNetworkCallBack(mNetworkCallBack);
    }

    public int getNetType() {
        return netType;
    }

    public void setNetType(int netType) {
        this.netType = netType;
    }

    public void addNetworkCallBack(NetworkConnectivityListener.NetworkCallBack mNetworkCallBack) {
        mNetChangeReceiver.registerNetworkCallBack(mNetworkCallBack);
    }

    public void removeNetworkCallBack(NetworkConnectivityListener.NetworkCallBack mNetworkCallBack) {
        mNetChangeReceiver.unregisterNetworkCallBack(mNetworkCallBack);
    }

    /**
     * 获得当前进程的名字
     *
     * @param context
     * @return 进程号
     */
    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    private void initPrefs() {
        AbPrefsUtil.init(this, getPackageName() + "_preference",
                Context.MODE_PRIVATE);
        AbDataPrefsUtil.init(this, getPackageName() + "_net_data",
                Context.MODE_PRIVATE);
    }

    public static App getInstance() {
        return _instance;
    }

    private void initImage() {
        bitmapConfig = new BitmapDisplayConfig();
        // bigPicDisplayConfig.setShowOriginal(true); // 显示原始图片,不压缩, 尽量不要使用,
        // 图片太大时容易OOM。
        bitmapConfig.setBitmapConfig(Bitmap.Config.RGB_565);
        bitmapConfig.setBitmapMaxSize(BitmapCommonUtils.getScreenSize(this));
    }

    public boolean isDownload() {
        return isDownload;
    }

    public void setDownload(boolean isDownload) {
        this.isDownload = isDownload;
    }

    /**
     * 配置方法数处理超过 64K 的应用
     * @param base
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
