package com.cesaas.android.pos.inventory.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.base.BaseActivity;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.inventory.bean.ResultAddInventoryGoodsBean;
import com.cesaas.android.pos.inventory.net.AddGoodNet;
import com.cesaas.android.pos.utils.InitEventBus;
import com.cesaas.android.pos.utils.MClearEditText;
import com.cesaas.android.pos.utils.Skip;
import com.cesaas.android.pos.utils.ToastUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

/**
 * 扫描发货页面
 * @author cebsaas
 *
 */
public class ZxingScanActivity extends BaseActivity implements QRCodeView.Delegate {

	private TextView tvTitle,tvRightTitle;
	private LinearLayout llBack;
	public MClearEditText et_style_code;

	private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 666;
	private QRCodeView mQRCodeView;

	private BaseDialog baseDialog;
	private AddGoodNet addGoodNet;

	private int id;
	private int shelvesId;
	private int number;
	private int type;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zxing_scan);
		InitEventBus.initEventBus(mContext);
		Bundle bundle=getIntent().getExtras();
		if(bundle!=null){
			id=bundle.getInt("id");
			shelvesId=bundle.getInt("shelvesId");
			number=bundle.getInt("number");
			type=bundle.getInt("type");
		}
		addGoodNet=new AddGoodNet(mContext);

		initView();
	}

	/**
	 * 接收添加盘点商品数据
	 * @param msg
	 */
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onDataSynEvent(ResultAddInventoryGoodsBean msg) {
		if(msg.IsSuccess!=false){
			ToastUtils.getLongToast(mContext,"添加盘点商品成功！");
		}else{
			ToastUtils.getLongToast(mContext,"添加盘点商品失败！"+msg.Message);
		}
	}

	private void initView(){
		llBack= (LinearLayout) findViewById(R.id.ll_base_title_back);
		tvTitle= (TextView) findViewById(R.id.tv_base_title);
		tvTitle.setText("扫描盘点");
		tvRightTitle= (TextView) findViewById(R.id.tv_base_title_right);
		tvRightTitle.setText("手动输入");
		tvRightTitle.setVisibility(View.VISIBLE);

		mQRCodeView = (ZXingView) findViewById(R.id.zxingview);
		mQRCodeView.setDelegate(this);

		tvRightTitle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//执行手动输入商品条码操作
				baseDialog = new BaseDialog(mContext);
				baseDialog.mInitShow();
				baseDialog.setCancelable(false);
			}
		});
		llBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Skip.mBack(mActivity);
			}
		});
	}
	
	/**
	 * 处理扫描Activity返回的数据
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		mQRCodeView.showScanRect();

		if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY) {
			final String picturePath = BGAPhotoPickerActivity.getSelectedImages(data).get(0);

            /*
            这里为了偷懒，就没有处理匿名 AsyncTask 内部类导致 Activity 泄漏的问题
            请开发在使用时自行处理匿名内部类导致Activity内存泄漏的问题，处理方式可参考 https://github.com/GeniusVJR/LearningNotes/blob/master/Part1/Android/Android%E5%86%85%E5%AD%98%E6%B3%84%E6%BC%8F%E6%80%BB%E7%BB%93.md
             */
			new AsyncTask<Void, Void, String>() {
				@Override
				protected String doInBackground(Void... params) {
					return QRCodeDecoder.syncDecodeQRCode(picturePath);
				}

				@Override
				protected void onPostExecute(String result) {
					if (TextUtils.isEmpty(result)) {
						Toast.makeText(ZxingScanActivity.this, "未发现二维码", Toast.LENGTH_SHORT).show();
					} else {
						addGoodNet.setData(id,shelvesId,result,type,number);
					}
				}
			}.execute();
		}
	}

	@Override
	public void onScanQRCodeOpenCameraError() {
		Log.e(Constant.TAG, "打开相机出错");
	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.open_flashlight:
				mQRCodeView.openFlashlight();
				break;
			case R.id.close_flashlight:
				mQRCodeView.closeFlashlight();
				break;
			case R.id.scan_barcode:
				mQRCodeView.changeToScanBarcodeStyle();
				break;
			case R.id.scan_qrcode:
				mQRCodeView.changeToScanQRCodeStyle();
				break;
			case R.id.choose_qrcde_from_gallery:
				startActivityForResult(BGAPhotoPickerActivity.newIntent(mContext, null, 1, null, false), REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY);
				break;
			case R.id.inventory_record:
//				bundle.putString("leftTitle",tvTitle.getText().toString());
//				Skip.mNextFroData(mActivity, InventoryRecordActivity.class,bundle);
				bundle.putInt("id",id);
				bundle.putInt("shelvesId",shelvesId);
				Skip.mNextFroData(mActivity,InventoryShelvesDetailsActivity.class,bundle);
				break;
		}
	}

	private void vibrate() {
		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		vibrator.vibrate(200);
	}

	@Override
	public void onScanQRCodeSuccess(String result) {
		vibrate();
		mQRCodeView.startSpot();

		addGoodNet.setData(id,shelvesId,result,type,number);
	}

	@Override
	protected void onStart() {
		super.onStart();
		mQRCodeView.startSpot();
		mQRCodeView.startCamera();
		mQRCodeView.showScanRect();
	}

	@Override
	protected void onStop() {
		mQRCodeView.stopCamera();
		mQRCodeView.stopSpot();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		mQRCodeView.onDestroy();
		mQRCodeView.stopSpot();
		super.onDestroy();
	}

	public class BaseDialog extends Dialog implements View.OnClickListener {
		TextView tvCancel,tvSure,tv_dialog_title;

		public BaseDialog(Context context) {
			this(context, R.style.dialog);
		}

		public BaseDialog(Context context, int dialog) {
			super(context, dialog);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setBackgroundDrawableResource(android.R.color.transparent);
			setContentView(R.layout.dialog_input_inventory);
			tvCancel= (TextView) findViewById(R.id.tv_cancel);
			tvCancel.setOnClickListener(this);
			tvSure= (TextView) findViewById(R.id.tv_sure);
			tvSure.setOnClickListener(this);
			tv_dialog_title= (TextView) findViewById(R.id.tv_dialog_title);
			et_style_code= (MClearEditText) findViewById(R.id.et_style_code);

			tv_dialog_title.setText("商品条码");
			et_style_code.setHint("请输入商品条码");
			et_style_code.setInputType(InputType.TYPE_CLASS_NUMBER);
		}

		public void mInitShow() {
			show();
		}

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
				case R.id.tv_cancel:
					dismiss();
					break;
				case R.id.tv_sure:
					//执行手动输入商品条码操作
					if(!TextUtils.isEmpty(et_style_code.getText().toString())){
						addGoodNet.setData(id,shelvesId,et_style_code.getText().toString(),type,number);
						dismiss();
					}else{
						ToastUtils.getLongToast(mContext,"请输入商品条码！");
					}

					break;
			}
		}
	}
}
