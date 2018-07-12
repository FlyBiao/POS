package com.cesaas.android.pos.rongcloud.custom;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.activity.order.WaitPayOrderDetailActivity;
import com.cesaas.android.pos.utils.Skip;
import com.cesaas.android.pos.utils.ToastUtils;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Message;

/**
 * 自定义订单消息显示。
 * 
 * @author fgb
 * 
 */
@ProviderTag(messageContent = CustomizeOrderMessage.class)
public class CustomizeMessageOrderItemProvider extends IContainerItemProvider.MessageProvider<CustomizeOrderMessage> {

	private static final String TAG = "CustomizeMessageOrderItemProvider";
	private Context ct;
	private Activity activity;

	public CustomizeMessageOrderItemProvider(Context ct, Activity activity){
		this.ct=ct;
		this.activity=activity;
	}
	
	
	/**
	 * 初始化 View。
	 */
	@Override
	public View newView(Context context, ViewGroup group) {
		View view = LayoutInflater.from(context).inflate(R.layout.item_customize_order_message, null);
        ViewHolder holder = new ViewHolder();
        
        holder.tv_customize_orderid = (TextView) view.findViewById(R.id.tv_customize_orderid);
        holder.tv_custom_order_message_prompt=(TextView) view.findViewById(R.id.tv_custom_order_message_prompt);
        holder.ll_dg=(LinearLayout) view.findViewById(R.id.ll_dg);
        //holder.tv_customize_order_cratedate = (TextView) view.findViewById(R.id.tv_customize_order_cratedate);
        view.setTag(holder);
        
        return view;
	}
	

	/**
	 * 将数据填充 View 上。
	 */
	@Override
	public void bindView(View v, int position, CustomizeOrderMessage content,
			UIMessage message) {
		
			ViewHolder holder = (ViewHolder) v.getTag();

		if (message.getMessageDirection() == Message.MessageDirection.SEND){
			holder.ll_dg.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_right);
		}else{
			holder.ll_dg.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_left);

		}
		holder.tv_custom_order_message_prompt.setText("您收到新的支付订单！");
		holder.tv_customize_orderid.setText("订单号:"+content.OrderNo);
//		Log.i("RongCloud","新消息-数据填充:"+content.OrderNo);
	}
	
	class ViewHolder {
		TextView tv_customize_orderid;
		TextView tv_custom_order_message_prompt;
		LinearLayout ll_dg;
	}
	
	/**
	 * 该条消息为该会话的最后一条消息时，会话列表要显示的内容，通过该方法进行定义。
	 */
	@Override
	public Spannable getContentSummary(CustomizeOrderMessage arg0) {
		String TradeId=arg0.OrderNo;
		if(TradeId!=null){
			return new SpannableString(arg0.OrderNo);
		}else{
			return null;
		}
		
	}

	/**
	 * 点击该类型消息触发。
	 */
	@Override
	public void onItemClick(View v, int posion, CustomizeOrderMessage content,
			UIMessage message) {
		if(message.getContent() instanceof CustomizeOrderMessage){
			
			CustomizeOrderMessage mCustomizeMessage=(CustomizeOrderMessage) message.getContent();
			Bundle bundle = new Bundle();
			bundle.putString("OrderId", mCustomizeMessage.OrderNo);
			Log.i("RongCloud","点击该类型消息触发:"+mCustomizeMessage.OrderNo);

			bundle.putString("OrderId",mCustomizeMessage.OrderNo);
			Skip.mNextFroData(activity,WaitPayOrderDetailActivity.class,bundle);

		}
	}

	/**
	 * 长按该类型消息触发。
	 */
	@Override
	public void onItemLongClick(View v, int posion, CustomizeOrderMessage arg2,
			UIMessage arg3) {
		// TODO Auto-generated method stub
		
	}

}
