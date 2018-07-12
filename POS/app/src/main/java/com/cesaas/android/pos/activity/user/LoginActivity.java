package com.cesaas.android.pos.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.activity.cashier.CashierHomeActivity;
import com.cesaas.android.pos.activity.cashier.CashierMainActivity;
import com.cesaas.android.pos.base.BaseActivity;
import com.cesaas.android.pos.bean.PosDeviceInfo;
import com.cesaas.android.pos.bean.UserBean;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.global.Urls;
import com.cesaas.android.pos.net.nohttp.CallServer;
import com.cesaas.android.pos.net.nohttp.HttpListener;
import com.cesaas.android.pos.test.TestActivity;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.cesaas.android.pos.utils.CheckUtil;
import com.cesaas.android.pos.utils.MD5;
import com.cesaas.android.pos.utils.Skip;
import com.cesaas.android.pos.utils.ToastUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import cn.weipass.pos.sdk.impl.WeiposImpl;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：登录页面
 * 创建日期：2016/10/09
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
@ContentView(R.layout.activity_login)
public class LoginActivity extends BaseActivity {

    @ViewInject(R.id.et_phone_num)
    private EditText et_phone_num;
    @ViewInject(R.id.et_password)
    private EditText et_password;

    private String enCode;//pos设备en号
    private String shopName;//店铺名称
    private String mCode;//pos店铺mCode

    private String account;
    private String pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getDeviceInfo();

        //如果用户已经登录 直接跳转到主页
        if (prefs.getBoolean(Constant.IS_LOGIN) )
//            Skip.mNext(mActivity, CashierHomeActivity.class, true);
            Skip.mNext(mActivity, CashierMainActivity.class, true);
//        Skip.mNext(mActivity,TestActivity.class, true);
    }

    @OnClick(R.id.bt_login)
    public void login(View v){
        switch (v.getId()){
            case R.id.bt_login:
                account = et_phone_num.getText().toString().trim();
                pwd = et_password.getText().toString().trim();

                //手机号格式及为空判断Account //密码格式及为空判断Password
                if(CheckUtil.phoneVerify(mContext,account)){
                    if (CheckUtil.passwordVerify(mContext, pwd)) {
                        Request<String> request = NoHttp.createStringRequest(Urls.USER_LOGIN, RequestMethod.POST);
                        request.add("Account",account);
                        request.add("Password",new MD5().toMD5(pwd));
                        CallServer.getRequestInstance().add(this, 0, request, loginListener, true, true);
                    }
                }
                break;
        }
    }

    //登录网络监听
    private HttpListener<String> loginListener = new HttpListener<String>()
    {
        @Override
        public void onSucceed(int what, Response<String> response)
        {
            String strJson = response.get();
            UserBean bean= gson.fromJson(strJson,UserBean.class);
            if(bean.IsSuccess==true){
                prefs.putBoolean(Constant.IS_LOGIN, bean.isSuccess());
                AbPrefsUtil.getInstance().putString(Constant.SPF_TOKEN,bean.TModel.UserTicket);
                AbPrefsUtil.getInstance().putString(Constant.SPF_TIME,String.valueOf(System.currentTimeMillis()));
                // 登录成功后跳转到CashierHomeActivity收银主页
                Intent intent = new Intent(LoginActivity.this,CashierMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                LoginActivity.this.finish();

            }else{
                ToastUtils.show("登录失败！"+bean.getMessage());
            }

        }
        @Override
        public void onFailed(int what, Response<String> response)
        {
            ToastUtils.show(response.getException().getMessage());
        }
    };


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
}
