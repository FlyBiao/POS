package com.cesaas.android.pos.net.xutils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;

import com.cesaas.android.pos.activity.cashier.AbnormalOrderActivity;
import com.cesaas.android.pos.db.bean.PosPayBean;
import com.cesaas.android.pos.db.pay.PosSqliteDatabaseUtils;
import com.cesaas.android.pos.dialog.WaitDialog;
import com.cesaas.android.pos.global.App;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.global.Urls;
import com.cesaas.android.pos.net.xutils.utils.NetworkUtil;
import com.cesaas.android.pos.utils.AbDataPrefsUtil;
import com.cesaas.android.pos.utils.AbDateUtil;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.cesaas.android.pos.utils.MD5;
import com.cesaas.android.pos.utils.Skip;
import com.cesaas.android.pos.utils.ToastUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.zhl.cbdialog.CBDialogBuilder;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

/**
 * 网络请求基类
 * 
 * @author Evan
 *
 */
public class TestBaseNet {

	private static final String TAG = "BaseNet";
	public Context mContext;
	private Activity activity;
	protected String uri;
	protected JSONObject data;
	private boolean ishow;
	private WaitDialog dialog;
	protected AbDataPrefsUtil abData;

	public TestBaseNet(Context context, boolean show) {
		this.mContext = context;
		data = new JSONObject();
		abData = AbDataPrefsUtil.getInstance();
		this.ishow = show;
		if (show)
			dialog = new WaitDialog(context);
	}

	public TestBaseNet(Context context, Activity activity, boolean show) {
		this.mContext = context;
		this.activity=activity;
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
				String token = this.getToken();
				String time = AbPrefsUtil.getInstance().getString(Constant.SPF_TIME, "");
				if (!"".equals(token) && !"".equals(time)) {
					StringBuffer auth = new StringBuffer();
					auth.append("SW-Authorization ").append(token).append(";").append(time);
					params.addHeader("Authorization", auth.toString());
					
//					Log.i(TAG, "auth=="+data);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			Log.i(Constant.TAG,"发送请求:" +"URL=:"+ Urls.TEST_SERVER + uri + "--数据参数=:" + data);
			
			HttpUtils http = new HttpUtils();
			http.send(HttpMethod.POST, Urls.TEST_SERVER + uri, params, new RequestCallBack<HttpUtils>() {
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
	public void mPostNet(String orderNo,AbPrefsUtil prefs,double payMoney,int payType,int IsPractical,String PayNo,int payOrderStauts,String primaryAccountNumber) {
		if (App.mInstance().getNetType() != NetworkUtil.NO_NETWORK) {
			RequestParams params = new RequestParams();
			try {
				params.setBodyEntity(new StringEntity(data.toString(), "UTF-8"));
				params.addHeader("Content-Type", "application/json");
				String token = this.getToken();
				String time = AbPrefsUtil.getInstance().getString(Constant.SPF_TIME, "");
				if (!"".equals(token) && !"".equals(time)) {
					StringBuffer auth = new StringBuffer();
					auth.append("SW-Authorization ").append(token).append(";").append(time);
					params.addHeader("Authorization", auth.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			Log.i("test","发送请求:" +"URL=:"+ Urls.TEST_SERVER + uri + "--数据参数=:" + data);

			HttpUtils http = new HttpUtils();
			http.send(HttpMethod.POST, Urls.TEST_SERVER + uri, params, new RequestCallBack<HttpUtils>() {
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
		} else {//未获取网络
			if(payType==2){//支付宝
				PosPayBean posPayBean=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),prefs.getString("userName"),orderNo,payMoney+"",PayNo,"3","支付宝",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"未Intent网络，请检查后重试！",prefs.getString("enCode"),"","false","0","0");
				insertData(posPayBean);
			}else if(payType==1){//微信
				PosPayBean posPayBean=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),prefs.getString("userName"),orderNo,payMoney+"",PayNo,"2","微信支付",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"未Intent网络，请检查后重试！",prefs.getString("enCode"),"","false","0","0");
				insertData(posPayBean);
			}else if(payType==4){//银联
				PosPayBean posPayBean=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),prefs.getString("userName"),orderNo,payMoney+"",PayNo,"4","银联支付",IsPractical+"",1+"", AbDateUtil.getCurrentDate(),"银行卡支付",prefs.getString("enCode"),primaryAccountNumber,"false","0","0");
				insertData(posPayBean);
			}else{
				PosPayBean posPayBean=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),prefs.getString("userName"),orderNo,payMoney+"",PayNo,"3","未知",IsPractical+"",payOrderStauts+"", AbDateUtil.getCurrentDate(),"未Intent网络，请检查后重试！",prefs.getString("enCode"),"","false","0","0");
				insertData(posPayBean);
			}
			netError();
		}
	}

	protected void mSuccess(String rJson) {
		LogUtils.d("成功：" + rJson);
	}

	protected void mFail(HttpException e, String err) {
		ToastUtils.show( "服务器连接或返回错误！");
	}

	private void insertData(PosPayBean bean){
		PosSqliteDatabaseUtils.insterData(mContext,bean);
	}

	private void netError(){
		new CBDialogBuilder(mContext)
				.setTouchOutSideCancelable(true)
				.showCancelButton(true)
				.setTitle("创建流水失败")
				.setMessage("创建流水失败，未知Intent网络，请检查网络后到异常单列表重试！")
				.setConfirmButtonText("确定")
				.setCancelButtonText("取消")
				.setDialogAnimation(CBDialogBuilder.DIALOG_ANIM_SLID_BOTTOM)
				.setButtonClickListener(true, new CBDialogBuilder.onDialogbtnClickListener() {
					@Override
					public void onDialogbtnClick(Context context, Dialog dialog, int whichBtn) {
						switch (whichBtn) {
							case BUTTON_CONFIRM:
								Skip.mNext(activity, AbnormalOrderActivity.class,true);
								break;
							case BUTTON_CANCEL:
								ToastUtils.show("已放弃创建支付流水，请自行到异常单进行处理！");
								Skip.mNext(activity, AbnormalOrderActivity.class,true);
								break;
							default:
								break;
						}
					}
				})
				.create().show();
	}
}

