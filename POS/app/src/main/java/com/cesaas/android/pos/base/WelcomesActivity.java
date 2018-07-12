package com.cesaas.android.pos.base;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.activity.user.LoginActivity;
import com.cesaas.android.pos.bean.ActivePosBean;
import com.cesaas.android.pos.bean.PosDeviceInfo;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.net.xutils.BaseNet;
import com.cesaas.android.pos.pos.PosActiveActivity;
import com.cesaas.android.pos.utils.AbAppUtil;
import com.cesaas.android.pos.utils.Skip;
import com.lidroid.xutils.exception.HttpException;


import org.json.JSONException;

import cn.weipass.pos.sdk.impl.WeiposImpl;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：欢迎页面
 * 创建日期：2016/11/08
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class WelcomesActivity extends BaseActivity {

    private TextView version;
    private RelativeLayout top;

    private String enCode;//pos设备en号
    private String shopName;//店铺名称
    private String mCode;//pos店铺mCode
    private int activeStatus;
    private ActiveStatusNet statusNet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcomes);
        getDeviceInfo();
        initView();
        isActivity();
        initData();
    }

    private void isActivity(){
        statusNet=new ActiveStatusNet(mContext);
        statusNet.setData(enCode);
    }

    public void initData(){
        version.setText("版本号  V" + AbAppUtil.getAppVersion(this));
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(1800);
        top.startAnimation(alphaAnimation);

    }

    public void initView(){
        version= (TextView) findViewById(R.id.version);
        top= (RelativeLayout) findViewById(R.id.top);
    }

    /**
     * 获取旺POS设备信息
     */
    public void getDeviceInfo(){
        String deviceInfoJson = WeiposImpl.as().getDeviceInfo();
        PosDeviceInfo deviceInfo=gson.fromJson(deviceInfoJson, PosDeviceInfo.class);
        enCode=deviceInfo.getEn().replaceAll("\\s","");//pos设备EN号
        mCode=deviceInfo.getMcode();
        prefs.putString("enCode",enCode);
    }

    /**
     * 查询pos激活状态请求
     * @author FGB
     *
     */
    public class ActiveStatusNet extends BaseNet {

        public ActiveStatusNet(Context context) {
            super(context, true);
            this.uri = "Pos/Sw/Account/ActiveStatus";
        }

        public void setData(String enCode) {
            try {
                data.put("EnCode",enCode);//pos设备en号
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mPostNet(); // 开始请求网络
        }

        @Override
        protected void mSuccess(String rJson) {
            super.mSuccess(rJson);
            ActivePosBean bean=gson.fromJson(rJson,ActivePosBean.class);
            mCache.put("ActivityStatus",bean.getTModel()+"");

            if(bean.IsSuccess==true){
                activeStatus=bean.getTModel();
                if(activeStatus==0){//未激活，跳转激活页面
                    Skip.mNext(mActivity, PosActiveActivity.class);

                }else{//已激活，跳转到登录页
                    Skip.mNext(mActivity, LoginActivity.class);
                }

            }else{
//                ToastUtils.show(bean.getMessage());
            }
        }

        @Override
        protected void mFail(HttpException e, String err) {
            super.mFail(e, err);
            Log.i(Constant.TAG, "网络请求错误==="+e+"=="+err);
        }

    }
}
