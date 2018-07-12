package com.cesaas.android.pos.gridview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cesaas.android.pos.R;


public class CashierGridAdapter extends BaseAdapter {
	private Context mContext;

	public String[] img_text = { "1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "0", "←"};
//public int[] img_text = { 1, 2, 3, 4, 5, 6, 7, 8, 9, R.mipmap.pos_shouyin, 0, R.mipmap.pos_shouyin};
//	public int[] imgs = {
//			R.mipmap.preferential, R.mipmap.pos_shouyin,
//			R.mipmap.get_order, R.mipmap.discount,
//			R.mipmap.hang_order, R.mipmap.hang_order};

	public CashierGridAdapter(Context mContext) {
		super();
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		return img_text.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.grid_cashier_item, parent, false);
		}
		TextView tv = BaseViewHolder.get(convertView, R.id.tv_number_item);
//		ImageView iv = BaseViewHolder.get(convertView, R.id.iv_item);
//		iv.setBackgroundResource(imgs[position]);

		tv.setText(img_text[position]);
		return convertView;
	}

}
