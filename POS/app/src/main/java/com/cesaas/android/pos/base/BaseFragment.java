package com.cesaas.android.pos.base;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.global.App;
import com.cesaas.android.pos.net.CommonNet;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.cesaas.android.pos.utils.BitmapHelp;
import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapCommonUtils;

public abstract class BaseFragment extends Fragment {

	protected BitmapUtils bitmapUtils;
	protected AbPrefsUtil abpUtil;
	protected View view;
	protected CommonNet commonNet;
	protected Gson gson;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		abpUtil = AbPrefsUtil.getInstance();
		commonNet=new CommonNet(getContext());
		gson=new Gson();

		bitmapUtils = BitmapHelp.getBitmapUtils(getContext().getApplicationContext());
		bitmapUtils.configDefaultBitmapMaxSize(BitmapCommonUtils.getScreenSize(getContext()).scaleDown(3));
		bitmapUtils.configDefaultLoadFailedImage(R.mipmap.ic_launcher);
	}

	/* 取资源值 */
	protected String getRstring(int r) {
		return App.mInstance().getResources().getString(r);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void onEventMainThread(Message msg) {
	}

	public void onEventBackgroundThread(Message msg) {
	}

	public void showMessageDialog(int title, CharSequence message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton(R.string.know, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}

}
