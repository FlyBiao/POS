package com.cesaas.android.pos.pos;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.activity.cashier.CashierHomeActivity;
import com.cesaas.android.pos.activity.cashier.CashierMainActivity;
import com.cesaas.android.pos.base.BaseActivity;
import com.cesaas.android.pos.bean.ActiveBean;
import com.cesaas.android.pos.bean.PosDeviceInfo;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.global.Urls;
import com.cesaas.android.pos.net.nohttp.HttpListener;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.cesaas.android.pos.utils.CheckUtil;
import com.cesaas.android.pos.utils.MD5;
import com.cesaas.android.pos.utils.Skip;
import com.cesaas.android.pos.utils.ToastUtils;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import cn.weipass.pos.sdk.impl.WeiposImpl;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：pos激活页面
 * 创建日期：2016/11/7 10:42
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class PosActiveActivity extends BaseActivity {

    private Button bt_active;
    private EditText et_phone_num,et_password,et_encode;

    private String enCode;//pos设备en号
    private String shopName;//店铺名称
    private String mCode;//pos店铺mCode

    private String account;//账号
    private String pwd;//密码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pos_active);

        getDeviceInfo();
        initView();
    }

    /**
     * 激活pos方法
     */
    public void activePos(){

        account = et_phone_num.getText().toString().trim();
        pwd = et_password.getText().toString().trim();

        //手机号格式及为空判断Account //密码格式及为空判断Password
        if(CheckUtil.phoneVerify(mContext,account)){
            if (CheckUtil.passwordVerify(mContext, pwd)) {
                if(!TextUtils.isEmpty(et_encode.getText().toString())){
                    Request<String> request = NoHttp.createStringRequest(Urls.POS_ACTIVE, RequestMethod.POST);
                    request.add("EnCode", et_encode.getText().toString());//pos设备en号
                    request.add("Account", account);
                    request.add("Password", new MD5().toMD5(pwd));
                    commonNet.requestNetTask(request, getActiveListener);

                }else{
                    ToastUtils.show(mContext,"请输入设备EN号！",ToastUtils.CENTER);
                }
            }
        }
    }


    //激活POS设备回调监听
    private HttpListener<String> getActiveListener = new HttpListener<String>()
    {
        @Override
        public void onSucceed(int what, Response<String> response)
        {
            ActiveBean bean=gson.fromJson(response.get(),ActiveBean.class);
            Log.d(Constant.TAG,"激活pos："+response.get());

            if(bean.IsSuccess==true){//激活成功
                if(bean.TModel!=null){
                    AbPrefsUtil.getInstance().putString(Constant.SPF_TOKEN,bean.TModel.getUserTicket());
                    prefs.putBoolean(Constant.IS_LOGIN, bean.isSuccess());
                    AbPrefsUtil.getInstance().putString(Constant.SPF_TIME,String.valueOf(System.currentTimeMillis()));
                    ToastUtils.show(mContext, "恭喜你！POS激活成功。", ToastUtils.CENTER);
                    //跳到收银主页
                    Skip.mNext(mActivity, CashierMainActivity.class);

                }else{
                    ToastUtils.show("用户票据为空！");
                }

            }else{
                ToastUtils.show(bean.getMessage());
            }
        }
        @Override
        public void onFailed(int what, Response<String> response)
        {
            ToastUtils.show(response.get());
        }
    };


    /**
     * 初始化视图控件
     */
    public void initView(){
        et_encode= (EditText) findViewById(R.id.et_encode);
        et_password= (EditText) findViewById(R.id.et_password);
        et_phone_num= (EditText) findViewById(R.id.et_phone_num);
        bt_active= (Button) findViewById(R.id.bt_active);
        et_encode.setFocusable(false);et_encode.setFocusableInTouchMode(false);//设置不可编辑状态；
        //设置设备EN号
        et_encode.setText(enCode);

        bt_active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //激活pos
                activePos();

            }
        });
    }

    /**
     * 获取旺POS设备信息
     */
    public void getDeviceInfo(){
        String deviceInfoJson = WeiposImpl.as().getDeviceInfo();
        PosDeviceInfo deviceInfo=gson.fromJson(deviceInfoJson, PosDeviceInfo.class);
        enCode=deviceInfo.getEn().replaceAll("\\s","");//pos设备EN号
        mCode=deviceInfo.getMcode();
    }
}
