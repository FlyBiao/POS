package com.cesaas.android.pos.net.xutils;

import android.content.Context;
import android.util.Log;

import com.cesaas.android.pos.dialog.WaitDialog;
import com.cesaas.android.pos.global.App;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.global.Urls;
import com.cesaas.android.pos.net.xutils.utils.NetworkUtil;
import com.cesaas.android.pos.utils.AbDataPrefsUtil;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.cesaas.android.pos.utils.MD5;
import com.cesaas.android.pos.utils.ToastUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

/**
 * 网络请求基类
 * 
 * @author Evan
 *
 */
public class BankBaseNet {

	private static final String TAG = "BaseNet";
	public Context mContext;
	protected String uri;
	protected JSONObject data;
	private boolean ishow;
	private WaitDialog dialog;
	protected AbDataPrefsUtil abData;

	public BankBaseNet(Context context, boolean show) {
		this.mContext = context;
		data = new JSONObject();
		abData = AbDataPrefsUtil.getInstance();
		this.ishow = show;
		if (show)
			dialog = new WaitDialog(context);
	}

	/**
	 * 获取授权
	 * @return
	 */
	public String getToken() {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append(AbPrefsUtil.getInstance().getString(Constant.SPF_TOKEN))
		.append("SW-AppAuthorizationToken")
				.append(AbPrefsUtil.getInstance().getString(Constant.SPF_TIME, ""));
		return new MD5().toMD5(sbuf.toString());
	}
	
	/**
	 * 开始请求网络
	 */
	public void mPostNet() {
		if (App.mInstance().getNetType() != NetworkUtil.NO_NETWORK) {
			RequestParams params = new RequestParams();
			try {
				params.setBodyEntity(new StringEntity(data.toString(), "UTF-8"));
				params.addHeader("Content-Type", "application/json");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			Log.i(Constant.TAG,"发送请求:" +"URL=:"+ Urls.BANK_INFO + uri + "--数据参数=:" + data);
			
			HttpUtils http = new HttpUtils();
			http.send(HttpMethod.GET, Urls.BANK_INFO + uri, params, new RequestCallBack<HttpUtils>() {
				@Override
				public void onLoading(long total, long current, boolean isUploading) {
					super.onLoading(total, current, isUploading);
					if (isUploading) {
					} else {
					}
				}

				//加载刷新
				@Override
				public void onStart() {
					super.onStart();
					if (ishow && dialog != null)
						dialog.show();
				}


				@Override
				public void onFailure(HttpException arg0, String arg1) {
					try {
						if (ishow && dialog != null)
							dialog.dismiss();
					} catch (Exception e) {
						e.printStackTrace();
					}
					mFail(arg0, arg1);
				}

				@Override
				public void onSuccess(ResponseInfo<HttpUtils> responseInfo) {
					try {
						if (ishow && dialog != null)
							dialog.dismiss();
					} catch (Exception e) {
						e.printStackTrace();
					}
					mSuccess(responseInfo.result + "");
				}
			});
		} else {
			ToastUtils.show("未Intent网络，请检查后重试！");
		}
	}

	public String getAccount() {
		return AbPrefsUtil.getInstance().getString(Constant.SPF_ACCOUNT);
	}

	

	protected void mSuccess(String rJson) {
		LogUtils.d("成功：" + rJson);
//		Log.i("com.cesaas.android.ep.ekawuyou.global.BaseNet", "rJson==:"+rJson);
	}

	protected void mFail(HttpException e, String err) {
		LogUtils.d("失败：" + err);
		ToastUtils.show( "服务器连接或返回错误！");
	}

}
