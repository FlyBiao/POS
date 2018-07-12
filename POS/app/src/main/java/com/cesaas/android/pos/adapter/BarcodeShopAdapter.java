package com.cesaas.android.pos.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.bean.DeleteEventBusMsg;
import com.cesaas.android.pos.bean.EventBusMsg;
import com.cesaas.android.pos.bean.GetByBarcodeCode;
import com.cesaas.android.pos.bean.SetNumberEventBusMsg;
import com.cesaas.android.pos.bean.SetPriceEventBusMsg;
import com.cesaas.android.pos.listview.ShopSliderView;
import com.cesaas.android.pos.utils.ToastUtils;
import com.zhl.cbdialog.CBDialogBuilder;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


/**
 * 条码商品Adapter
 * @author FGB
 *
 */
public class BarcodeShopAdapter extends BaseAdapter{

	private Context context; 
	private Activity activity;
	private LayoutInflater inflater;
	public List<GetByBarcodeCode> arr=new ArrayList<GetByBarcodeCode>();
	
	private int quantity=0;
	private double price=0.0;;

	ViewHolder holder;

	public BarcodeShopAdapter(Context context,Activity activity,List<GetByBarcodeCode> arr){
		this.context=context;
		this.activity=activity;
		this.arr=arr;
		inflater = LayoutInflater.from(context); 
	}
	
	@Override
	public int getCount() {
		return arr.size();
	}

	@Override
	public Object getItem(int position) {
		return arr.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ShopSliderView slideView = (ShopSliderView) convertView;
		if (slideView == null) {
			View itemView = inflater.inflate(R.layout.item_barcode_shop_info, null);

			slideView = new ShopSliderView(context);
			slideView.setContentView(itemView);
			holder = new ViewHolder(slideView);
			slideView.setTag(holder);
		} else {
			holder = (ViewHolder) slideView.getTag();
		}
		
		GetByBarcodeCode bean=arr.get(position);
		slideView.shrink();

		holder.tv_shop_number.setText(bean.getShopCount()+"");
		holder.tv_barcode_shop_price.setText(bean.getPrice()+"");
//		holder.tv_lists.setText(position+1+"");
		holder.tv_lists.setText(bean.getPayMent()+"");
		holder.tv_shop_barcode_code.setText(bean.getTitle());
		holder.tv_shift.setText(bean.getBarcodeCode());
		
		//删除
		holder.deleteHolder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new CBDialogBuilder(context)
						.setTouchOutSideCancelable(true)
						.showCancelButton(true)
						.setTitle("温馨提示")
						.setMessage("是否删除该订单，删除将不能恢复！")
						.setConfirmButtonText("确定")
						.setCancelButtonText("取消")
						.setDialogAnimation(CBDialogBuilder.DIALOG_ANIM_SLID_BOTTOM)
						.setButtonClickListener(true, new CBDialogBuilder.onDialogbtnClickListener() {
							@Override
							public void onDialogbtnClick(Context context, Dialog dialog, int whichBtn) {
								switch (whichBtn) {
									case BUTTON_CONFIRM:
										//发送删除商品消息
										DeleteEventBusMsg msg=new DeleteEventBusMsg();
										msg.setSuccess(true);
										msg.setPosition(position);
//
										EventBus.getDefault().post(msg);
										break;
									case BUTTON_CANCEL:
										ToastUtils.show("已取消删除");
										break;
									default:
										break;
								}
							}
						})
						.create().show();
			}
		});
		//数量
		holder.shop_number.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new SetNumberDialog(context,activity,arr.get(position).getBarcodeId()).mInitShow();
			}
		});
		//价格
		holder.shop_peice.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new SetPriceDialog(context,arr.get(position).getBarcodeId()).mInitShow();
			}
		});
		//赠品
		holder.tv_gifts.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new CBDialogBuilder(context)
						.setTouchOutSideCancelable(true)
						.showCancelButton(true)
						.setTitle("温馨提示")
						.setMessage("是否把改商品设置为赠品")
						.setConfirmButtonText("确定")
						.setCancelButtonText("取消")
						.setDialogAnimation(CBDialogBuilder.DIALOG_ANIM_SLID_BOTTOM)
						.setButtonClickListener(true, new CBDialogBuilder.onDialogbtnClickListener() {
							@Override
							public void onDialogbtnClick(Context context, Dialog dialog, int whichBtn) {
								switch (whichBtn) {
									case BUTTON_CONFIRM:
										notifyDataSetChanged();

										holder.tv_barcode_shop_price.setVisibility(View.GONE);
										holder.tv_gifts_shop.setVisibility(View.VISIBLE);

										EventBusMsg msg=new EventBusMsg();
										msg.setStyleType(3);
										msg.setBarcodeId(arr.get(position).getBarcodeId());
										msg.setGiftsPrice(arr.get(position).getPrice());
										msg.setSuccess(true);
										EventBus.getDefault().post(msg);
										break;

									case BUTTON_CANCEL:
										ToastUtils.show("已取消设置赠品");
										break;
									default:
										break;
								}
							}
						})
						.create().show();
			}
		});
		
		return slideView;
	}
	
	private static class ViewHolder {
		TextView tv_lists;
		TextView tv_shop_barcode_code;
		TextView tv_shift;
		TextView tv_shop_number;
		TextView tv_barcode_shop_price;
		TextView tv_gifts_shop;
		TextView deleteHolder;
		TextView shop_number;
		TextView shop_peice;
		TextView tv_gifts;
		
		ViewHolder(View view) {
			tv_lists=(TextView) view.findViewById(R.id.tv_lists);
			tv_shop_barcode_code=(TextView) view.findViewById(R.id.tv_shop_barcode_code);
			tv_shift=(TextView) view.findViewById(R.id.tv_shift);
			tv_shop_number=(TextView) view.findViewById(R.id.tv_shop_number);
			tv_barcode_shop_price=(TextView) view.findViewById(R.id.tv_barcode_shop_price);
			tv_gifts_shop=(TextView)view.findViewById(R.id.tv_gifts_shop);
			deleteHolder = (TextView) view.findViewById(R.id.delete);
			tv_gifts=(TextView)view.findViewById(R.id.tv_gifts);
			shop_number = (TextView) view.findViewById(R.id.shop_number);
			shop_peice = (TextView) view.findViewById(R.id.shop_peice);
		}
	}

	/**
	 * 设置商品数量dialog
	 *
	 * @author FGB
	 *
	 */
	public class SetNumberDialog extends Dialog implements View.OnClickListener {

		private Button btn_confirm_set_shop_number;
		private EditText et_barcode_shop_number;
		private Activity activity;
		private String BarcodeId;

		public SetNumberDialog(Context context,Activity activity,String BarcodeId) {
			this(context, R.style.dialog);
			this.activity=activity;
			this.BarcodeId=BarcodeId;
		}

		public SetNumberDialog(Context context, int dialog) {
			super(context, dialog);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setBackgroundDrawableResource(android.R.color.transparent);
			setContentView(R.layout.set_shop_number_dialog);

			initView();
		}

		public void initView(){
			btn_confirm_set_shop_number=(Button) findViewById(R.id.btn_confirm_set_shop_number);
			et_barcode_shop_number=(EditText) findViewById(R.id.et_barcode_shop_number);
			btn_confirm_set_shop_number.setOnClickListener(this);
		}

		public void mInitShow() {
			show();
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.btn_confirm_set_shop_number://确定添加
					quantity=Integer.parseInt(et_barcode_shop_number.getText().toString());
					SetNumberEventBusMsg msg=new SetNumberEventBusMsg();
					msg.setShopCount(quantity);
					msg.setBarcodeId(BarcodeId);
					EventBus.getDefault().post(msg);
					cancel();
					break;

				default:
					break;
			}
		}
	}

	/**
	 * 设置商品价格dialog
	 *
	 * @author FGB
	 *
	 */
	public class SetPriceDialog extends Dialog implements View.OnClickListener {

		private Button btn_confirm_set_shop_price;
		private EditText et_barcode_shop_price;
		private String BarcodeId;

		public SetPriceDialog(Context context,String BarcodeId) {
			this(context, R.style.dialog);
			this.BarcodeId=BarcodeId;
		}

		public SetPriceDialog(Context context, int dialog) {
			super(context, dialog);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setBackgroundDrawableResource(android.R.color.transparent);
			setContentView(R.layout.set_price_dialog);

			initView();
		}

		public void initView(){
			btn_confirm_set_shop_price=(Button) findViewById(R.id.btn_confirm_set_shop_price);
			et_barcode_shop_price=(EditText) findViewById(R.id.et_barcode_shop_price);
			btn_confirm_set_shop_price.setOnClickListener(this);
		}

		public void mInitShow() {
			show();
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.btn_confirm_set_shop_price://确定添加
					price=Double.parseDouble(et_barcode_shop_price.getText().toString());
					SetPriceEventBusMsg msg=new SetPriceEventBusMsg();
					msg.setPrice(price);
					msg.setBarcodeId(BarcodeId);
					EventBus.getDefault().post(msg);
					cancel();
					break;

				default:
					break;
			}
		}
	}
}
