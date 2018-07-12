package scanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.activity.cashier.CashierHomeActivity;
import com.cesaas.android.pos.bean.ResultCreatePayBean;
import com.cesaas.android.pos.utils.CheckUtil;
import com.cesaas.android.pos.utils.DecimalFormatUtils;
import com.cesaas.android.pos.utils.Skip;
import com.cesaas.android.pos.utils.ToastUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.client.result.ResultParser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Map;

import scanner.camera.CameraManager;
import scanner.common.BitmapUtils;
import scanner.decode.BitmapDecoder;
import scanner.decode.CaptureActivityHandler;
import scanner.view.ViewfinderView;


/**
 * This activity opens the camera and does the actual scanning on a background
 * thread. It draws a viewfinder to help the user place the barcode correctly,
 * shows feedback as the image processing is happening, and then overlays the
 * results when a scan is successful.
 * 
 * 此Activity所做的事： 1.开启camera，在后台独立线程中完成扫描任务；
 * 2.绘制了一个扫描区（viewfinder）来帮助用户将条码置于其中以准确扫描； 3.扫描成功后会将扫描结果展示在界面上。
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public final class CaptureActivity extends Activity implements
		SurfaceHolder.Callback, View.OnClickListener {
	public static final String TAGS = "CaptureActivity";
	private static final String TAG = CaptureActivity.class.getSimpleName();

	private static final int REQUEST_CODE = 100;

	private static final int PARSE_BARCODE_FAIL = 300;
	private static final int PARSE_BARCODE_SUC = 200;
	
	private static final int RESULT_CODE=101;

	/**
	 * 是否有预览
	 */
	private boolean hasSurface;

	/**
	 * 活动监控器。如果手机没有连接电源线，那么当相机开启后如果一直处于不被使用状态则该服务会将当前activity关闭。
	 * 活动监控器全程监控扫描活跃状态，与CaptureActivity生命周期相同.每一次扫描过后都会重置该监控，即重新倒计时。
	 */
	private InactivityTimer inactivityTimer;

	/**
	 * 声音震动管理器。如果扫描成功后可以播放一段音频，也可以震动提醒，可以通过配置来决定扫描成功后的行为。
	 */
	private BeepManager beepManager;

	/**
	 * 闪光灯调节器。自动检测环境光线强弱并决定是否开启闪光灯
	 */
	private AmbientLightManager ambientLightManager;

	private CameraManager cameraManager;
	/**
	 * 扫描区域
	 */
	private ViewfinderView viewfinderView;

	private CaptureActivityHandler handler;

	private Result lastResult;

	private boolean isFlashlightOpen;

	/**
	 * 【辅助解码的参数(用作MultiFormatReader的参数)】 编码类型，该参数告诉扫描器采用何种编码方式解码，即EAN-13，QR
	 * Code等等 对应于DecodeHintType.POSSIBLE_FORMATS类型
	 * 参考DecodeThread构造函数中如下代码：hints.put(DecodeHintType.POSSIBLE_FORMATS,
	 * decodeFormats);
	 */
	private Collection<BarcodeFormat> decodeFormats;

	/**
	 * 【辅助解码的参数(用作MultiFormatReader的参数)】 该参数最终会传入MultiFormatReader，
	 * 上面的decodeFormats和characterSet最终会先加入到decodeHints中 最终被设置到MultiFormatReader中
	 * 参考DecodeHandler构造器中如下代码：multiFormatReader.setHints(hints);
	 */
	private Map<DecodeHintType, ?> decodeHints;

	/**
	 * 【辅助解码的参数(用作MultiFormatReader的参数)】 字符集，告诉扫描器该以何种字符集进行解码
	 * 对应于DecodeHintType.CHARACTER_SET类型
	 * 参考DecodeThread构造器如下代码：hints.put(DecodeHintType.CHARACTER_SET,
	 * characterSet);
	 */
	private String characterSet;

	private Result savedResultToShow;

	private IntentSource source;
	
	private Button capture_button_cancel;

	private TextView tv_manual_input;

	private String mScanOrderResult;
	private String mAddVipResult;
	private String mScanShopResult;
	private String mScanAliPayOrderResult;
	private String mScanWeiXinPayOrderResult;
	private String mScanRefundOrderResult;
	private String mScanAddVipResult;
	private String mAddCouponsResult;
	private String mRefundResult;
	private String TraceAuditNumber;

	private String mScanVipResult;

	private String referenceNumber;

	private CustomRefundDialog refundDialog;

	/**
	 * 图片的路径
	 */
	private String photoPath;
	

	private Handler mHandler = new MyHandler(this);

	static class MyHandler extends Handler {

		private WeakReference<Activity> activityReference;

		public MyHandler(Activity activity) {
			activityReference = new WeakReference<Activity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
				case PARSE_BARCODE_SUC: // 解析图片成功
					Toast.makeText(activityReference.get(),
							"解析成功，结果为：" + msg.obj, Toast.LENGTH_SHORT).show();
					
					/*Intent intent=new Intent();
					intent.putExtra("resultCode", msg.obj.toString());
			        setResult(RESULT_CODE, intent);*/
			        
					break;

				case PARSE_BARCODE_FAIL:// 解析图片失败

					Toast.makeText(activityReference.get(), "解析图片失败",
							Toast.LENGTH_SHORT).show();
					break;

				default:
					break;
			}

			super.handleMessage(msg);
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//取得从上一个Activity当中传递过来的Intent对象
		Intent intent = getIntent();
		mScanOrderResult=intent.getStringExtra("mScanOrderResult");
		mAddVipResult=intent.getStringExtra("mAddVipResult");
		mScanShopResult=intent.getStringExtra("mScanShopResult");
		mScanAliPayOrderResult=intent.getStringExtra("mScanAliPayOrderResult");
		mScanWeiXinPayOrderResult=intent.getStringExtra("mScanWeiXinPayOrderResult");
		mScanRefundOrderResult=intent.getStringExtra("mScanRefundOrderResult");
		mScanAddVipResult=intent.getStringExtra("mScanAddVipResult");
		mAddCouponsResult=intent.getStringExtra("mAddCouponsResult");
		mRefundResult=intent.getStringExtra("mRefundResult");
		mScanVipResult=intent.getStringExtra("mScanVipResult");
		TraceAuditNumber=intent.getStringExtra("TraceAuditNumber");

		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.capture);

		capture_button_cancel=(Button) findViewById(R.id.capture_button_cancel);
		tv_manual_input= (TextView) findViewById(R.id.tv_manual_input);
		
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		beepManager = new BeepManager(this);
		ambientLightManager = new AmbientLightManager(this);

		// 监听图片识别按钮
		findViewById(R.id.capture_scan_photo).setOnClickListener(this);

		findViewById(R.id.capture_flashlight).setOnClickListener(this);
		capture_button_cancel.setOnClickListener(this);

		tv_manual_input.setOnClickListener(this);

	}

	@Override
	protected void onResume() {
		super.onResume();

		// CameraManager must be initialized here, not in onCreate(). This is
		// necessary because we don't
		// want to open the camera driver and measure the screen size if we're
		// going to show the help on
		// first launch. That led to bugs where the scanning rectangle was the
		// wrong size and partially
		// off screen.

		// 相机初始化的动作需要开启相机并测量屏幕大小，这些操作
		// 不建议放到onCreate中，因为如果在onCreate中加上首次启动展示帮助信息的代码的 话，
		// 会导致扫描窗口的尺寸计算有误的bug
		cameraManager = new CameraManager(getApplication());

		viewfinderView = (ViewfinderView) findViewById(R.id.capture_viewfinder_view);
		viewfinderView.setCameraManager(cameraManager);

		handler = null;
		lastResult = null;

		// 摄像头预览功能必须借助SurfaceView，因此也需要在一开始对其进行初始化
		// 如果需要了解SurfaceView的原理
		// 参考:http://blog.csdn.net/luoshengyang/article/details/8661317
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.capture_preview_view); // 预览
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			// The activity was paused but not stopped, so the surface still
			// exists. Therefore
			// surfaceCreated() won't be called, so init the camera here.
			initCamera(surfaceHolder);

		}
		else {
			// 防止sdk8的设备初始化预览异常
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

			// Install the callback and wait for surfaceCreated() to init the
			// camera.
			surfaceHolder.addCallback(this);
		}

		// 加载声音配置，其实在BeemManager的构造器中也会调用该方法，即在onCreate的时候会调用一次
		beepManager.updatePrefs();

		// 启动闪光灯调节器
		ambientLightManager.start(cameraManager);

		// 恢复活动监控器
		inactivityTimer.onResume();

		source = IntentSource.NONE;
		decodeFormats = null;
		characterSet = null;
	}

	@Override
	protected void onPause() {
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		inactivityTimer.onPause();
		ambientLightManager.stop();
		beepManager.close();

		// 关闭摄像头
		cameraManager.closeDriver();
		if (!hasSurface) {
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.capture_preview_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode==KeyEvent.KEYCODE_BACK) {
//			ScanResultActivity.activity.finish();
			CaptureActivity.this.finish();
		}
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if ((source == IntentSource.NONE) && lastResult != null) { // 重新进行扫描
					restartPreviewAfterDelay(0L);
					return true;
				}
				break;
			case KeyEvent.KEYCODE_FOCUS:
			case KeyEvent.KEYCODE_CAMERA:
				// Handle these events so they don't launch the Camera app
				return true;

			case KeyEvent.KEYCODE_VOLUME_UP:
				cameraManager.zoomIn();
				return true;

			case KeyEvent.KEYCODE_VOLUME_DOWN:
				cameraManager.zoomOut();
				return true;

		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {

		if (resultCode == RESULT_OK) {
			final ProgressDialog progressDialog;
			switch (requestCode) {
				case REQUEST_CODE:

					// 获取选中图片的路径
					Cursor cursor = getContentResolver().query(
							intent.getData(), null, null, null, null);
					if (cursor.moveToFirst()) {
						photoPath = cursor.getString(cursor
								.getColumnIndex(MediaStore.Images.Media.DATA));
					}
					cursor.close();

					progressDialog = new ProgressDialog(this);
					progressDialog.setMessage("正在扫描...");
					progressDialog.setCancelable(false);
					progressDialog.show();

					new Thread(new Runnable() {

						@Override
						public void run() {

							Bitmap img = BitmapUtils
									.getCompressedBitmap(photoPath);

							BitmapDecoder decoder = new BitmapDecoder(
									CaptureActivity.this);
							Result result = decoder.getRawResult(img);

							if (result != null) {
								Message m = mHandler.obtainMessage();
								m.what = PARSE_BARCODE_SUC;
								m.obj = ResultParser.parseResult(result)
										.toString();
								mHandler.sendMessage(m);
							}
							else {
								Message m = mHandler.obtainMessage();
								m.what = PARSE_BARCODE_FAIL;
								mHandler.sendMessage(m);
							}

							progressDialog.dismiss();

						}
					}).start();

					break;

			}
		}

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == null) {
			Log.e(TAG,
					"*** WARNING *** surfaceCreated() gave us a null surface!");
		}
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		/*hasSurface = false;*/
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	/**
	 * 处理扫描条码 二维码成功的结果
	 * @param rawResult 扫描成功结果
	 * @param barcode
	 * @param scaleFactor
	 */
	public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {

		// 重新计时
		inactivityTimer.onActivity();

		lastResult = rawResult;

		// 把图片画到扫描框
		viewfinderView.drawResultBitmap(barcode);

		beepManager.playBeepSoundAndVibrate();

		//Toast.makeText(this,"识别结果:" + ResultParser.parseResult(rawResult).toString(),Toast.LENGTH_SHORT).show();
		
		Intent intent=new Intent();
		if(ResultParser.parseResult(rawResult).toString()!=null){
			if(mScanOrderResult!=null && mScanOrderResult.equals("012")){
				intent.putExtra("resultCode", ResultParser.parseResult(rawResult).toString());
				intent.putExtra("mScanOrderResult","012");
			}

			if(mRefundResult!=null && mRefundResult.equals("100")){
				intent.putExtra("resultCode", ResultParser.parseResult(rawResult).toString());
				intent.putExtra("mRefundResult","100");
			}

			if(mScanVipResult!=null && mScanVipResult.equals("1008")){
				intent.putExtra("resultCode", ResultParser.parseResult(rawResult).toString());
				intent.putExtra("mScanVipResult","1008");
			}

			if(mScanAliPayOrderResult!=null && mScanAliPayOrderResult.equals("013")){
				intent.putExtra("resultCode", ResultParser.parseResult(rawResult).toString());
				intent.putExtra("mScanAliPayOrderResult","013");
			}

			if(mScanWeiXinPayOrderResult!=null && mScanWeiXinPayOrderResult.equals("014")){
				intent.putExtra("resultCode", ResultParser.parseResult(rawResult).toString());
				intent.putExtra("mScanWeiXinPayOrderResult","014");
			}

			if(mScanRefundOrderResult!=null && mScanRefundOrderResult.equals("015")){
				intent.putExtra("resultCode", ResultParser.parseResult(rawResult).toString());
				intent.putExtra("mScanRefundOrderResult","015");
			}

			if(mScanAddVipResult!=null && mScanAddVipResult.equals("016")){
				intent.putExtra("resultCode", ResultParser.parseResult(rawResult).toString());
				intent.putExtra("mScanAddVipResult","016");
			}

			if(mAddCouponsResult!=null && mAddCouponsResult.equals("017")){
				intent.putExtra("resultCode", ResultParser.parseResult(rawResult).toString());
				intent.putExtra("mAddCouponsResult","017");
			}

			if(mAddVipResult!=null && mAddVipResult.equals("011")){
				intent.putExtra("resultCode", ResultParser.parseResult(rawResult).toString());
				intent.putExtra("mAddVipResult","011");
			}

			if(mScanShopResult!=null && mScanShopResult.equals("009")){
				intent.putExtra("resultCode", ResultParser.parseResult(rawResult).toString());
				intent.putExtra("mScanShopResult","009");
			}

		}
		Log.i(TAGS, "扫描条码:"+ ResultParser.parseResult(rawResult).toString());
        setResult(RESULT_CODE, intent);
        finish();

	}

	public void restartPreviewAfterDelay(long delayMS) {
		if (handler != null) {
			handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
		}
		resetStatusView();
	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public CameraManager getCameraManager() {
		return cameraManager;
	}

	private void resetStatusView() {
		viewfinderView.setVisibility(View.VISIBLE);
		lastResult = null;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}

		if (cameraManager.isOpen()) {
			Log.w(TAG,
					"initCamera() while already open -- late SurfaceView callback?");
			return;
		}
		try {
			cameraManager.openDriver(surfaceHolder);
			// Creating the handler starts the preview, which can also throw a
			// RuntimeException.
			if (handler == null) {
				handler = new CaptureActivityHandler(this, decodeFormats,
						decodeHints, characterSet, cameraManager);
			}
			decodeOrStoreSavedBitmap(null, null);
		}
		catch (IOException ioe) {
			Log.w(TAG, ioe);
			displayFrameworkBugMessageAndExit();
		}
		catch (RuntimeException e) {
			// Barcode Scanner has seen crashes in the wild of this variety:
			// java.?lang.?RuntimeException: Fail to connect to camera service
			Log.w(TAG, "Unexpected error initializing camera", e);
			displayFrameworkBugMessageAndExit();
		}
	}

	/**
	 * 向CaptureActivityHandler中发送消息，并展示扫描到的图像
	 * 
	 * @param bitmap
	 * @param result
	 */
	private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
		// Bitmap isn't used yet -- will be used soon
		if (handler == null) {
			savedResultToShow = result;
		}
		else {
			if (result != null) {
				savedResultToShow = result;
			}
			if (savedResultToShow != null) {
				Message message = Message.obtain(handler,
						R.id.decode_succeeded, savedResultToShow);
				handler.sendMessage(message);
			}
			savedResultToShow = null;
		}
	}

	private void displayFrameworkBugMessageAndExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage(getString(R.string.msg_camera_framework_bug));
		builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
		builder.setOnCancelListener(new FinishListener(this));
		builder.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.capture_scan_photo: // 图片识别
				// 打开手机中的相册
				Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT); // "android.intent.action.GET_CONTENT"
				innerIntent.setType("image/*");
				Intent wrapperIntent = Intent.createChooser(innerIntent,
						"选择二维码图片");
				this.startActivityForResult(wrapperIntent, REQUEST_CODE);
				break;

			case R.id.capture_flashlight:
				if (isFlashlightOpen) {
					cameraManager.setTorch(false); // 关闭闪光灯
					isFlashlightOpen = false;
				}
				else {
					cameraManager.setTorch(true); // 打开闪光灯
					isFlashlightOpen = true;
				}
				break;
			case R.id.capture_button_cancel:
//				ScanResultActivity.activity.finish();
				finish();
				break;
			case R.id.tv_manual_input:
				refundDialog=new CustomRefundDialog(this,R.style.dialog,R.layout.item_custom_refund_dialog);
				refundDialog.show();
				refundDialog.setCancelable(false);
				break;
			default:
				break;
		}

	}

	public class CustomRefundDialog extends Dialog implements View.OnClickListener{

		int layoutRes;//布局文件
		Context context;
		LinearLayout ll_sure_refund,ll_cancel_refund;
		EditText et_reference_number;

		public CustomRefundDialog(Context context, int theme,int resLayout){
			super(context, theme);
			this.context = context;
			this.layoutRes=resLayout;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			this.setContentView(layoutRes);
			initView();
		}

		public void initView(){
			ll_sure_refund= (LinearLayout) findViewById(R.id.ll_sure_refund);
			ll_sure_refund.setOnClickListener(this);
			ll_cancel_refund= (LinearLayout) findViewById(R.id.ll_cancel_refund);
			ll_cancel_refund.setOnClickListener(this);
			et_reference_number= (EditText) findViewById(R.id.et_reference_number);
			et_reference_number.setText(TraceAuditNumber);
		}

		@Override
		public void onClick(View v) {

			switch (v.getId()){
				case R.id.ll_sure_refund:
					referenceNumber=et_reference_number.getText().toString();
					if(referenceNumber!=null && !"".equals(et_reference_number.getText().toString())){
						Intent intent=new Intent();
						intent.putExtra("resultCode", referenceNumber);
						intent.putExtra("mRefundResult","100");
						refundDialog.dismiss();
						setResult(RESULT_CODE, intent);
						finish();
					}else{
						ToastUtils.show(CaptureActivity.this,"请输入退款凭证号！",ToastUtils.CENTER);
					}
					break;
				case R.id.ll_cancel_refund:
					refundDialog.dismiss();
					break;
			}
		}
	}

}