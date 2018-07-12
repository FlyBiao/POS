package com.cesaas.android.pos.rongcloud.custom;

import android.os.Parcel;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.common.ParcelUtils;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * 自定义订单消息类
 * @author fgb
 *
 */
@MessageTag(value = "OrderStorePayNotify", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class CustomizeOrderMessage extends MessageContent {

	public String CreateDate;
	public String OrderNo;
	public int OrderStatus;
	public int ExpressType;
	
	public CustomizeOrderMessage(){

	}
	public static CustomizeOrderMessage obtain(String CreateDate,String TradeId,int OrderStatus,int ExpressType) {
        CustomizeOrderMessage rongRedPacketMessage = new CustomizeOrderMessage();
        rongRedPacketMessage.CreateDate=CreateDate;
        rongRedPacketMessage.OrderNo=TradeId;
        rongRedPacketMessage.OrderStatus=OrderStatus;
        rongRedPacketMessage.ExpressType=ExpressType;
        
        return rongRedPacketMessage;
    }
	/**
	 * 该方法的功能是将消息属性封装成 json 串，再将 json 串转成 byte 数组，该方法会在发消息时调用
	 */
	@Override
	public byte[] encode() {
		
		 JSONObject jsonObj = new JSONObject();

		    try {
		        jsonObj.put("CreateDate", CreateDate);
		        jsonObj.put("OrderNo", OrderNo);
//		        jsonObj.put("Price", Price);
		        jsonObj.put("OrderStatus", OrderStatus);
		        jsonObj.put("ExpressType", ExpressType);
		        
		    } catch (JSONException e) {
		        Log.e("JSONException", e.getMessage());
		    }

		    try {
		        return jsonObj.toString().getBytes("UTF-8");
		    } catch (UnsupportedEncodingException e) {
		        e.printStackTrace();
		    }

		    return null;
	}

	/**
	 * 构造方法，该方法将对收到的消息进行解析，先由 byte 转成 json 字符串，再将 json 中内容取出赋值给消息属性
	 * @param data
	 */
	public CustomizeOrderMessage(byte[] data) {
	    String jsonStr = null;

	    try {
	        jsonStr = new String(data, "UTF-8");
	    } catch (UnsupportedEncodingException e1) {

	    }

	    try {
	        JSONObject jsonObj = new JSONObject(jsonStr);

	        if (jsonObj.has("CreateDate"))
	        	CreateDate = jsonObj.optString("CreateDate");
	        
	        if (jsonObj.has("OrderNo"))
	        	OrderNo = jsonObj.optString("OrderNo");

	        if (jsonObj.has("OrderStatus"))
	        	OrderStatus = jsonObj.optInt("OrderStatus");
	        
	        if (jsonObj.has("ExpressType"))
	        	ExpressType = jsonObj.optInt("ExpressType");

	    } catch (JSONException e) {
//	        RLog.e(this, "JSONException", e.getMessage());
	    }

	}
	
	/**
	 * 给消息赋值。
	 * @param in
	 */
	public CustomizeOrderMessage(Parcel in) {
		//该类为工具类，消息属性
		CreateDate= ParcelUtils.readFromParcel(in);
	    //这里可继续增加你消息的属性
		OrderNo= ParcelUtils.readFromParcel(in);
//		Log.i("RongCloud","新消息-给消息赋值:"+OrderNo);
	  }
	
	
	 /**
	   * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
	   */
	  public static final Creator<CustomizeOrderMessage> CREATOR = new Creator<CustomizeOrderMessage>() {

	      @Override
	      public CustomizeOrderMessage createFromParcel(Parcel source) {
	          return new CustomizeOrderMessage(source);
	      }

	      @Override
	      public CustomizeOrderMessage[] newArray(int size) {
	          return new CustomizeOrderMessage[size];
	      }
	  };
	  
	
	/**
	   * 描述了包含在 Parcelable 对象排列信息中的特殊对象的类型。
	   *
	   * @return 一个标志位，表明Parcelable对象特殊对象类型集合的排列。
	   */
	@Override
	public int describeContents() {
		return 0;
	}

	
	 /**
	   * 将类的数据写入外部提供的 Parcel 中。
	   *
	   * @param dest  对象被写入的 Parcel。
	   * @param flags 对象如何被写入的附加标志。
	   */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		//该类为工具类，对消息中属性进行序列化
		ParcelUtils.writeToParcel(dest, CreateDate);
		//这里可继续增加你消息的属性。。
		ParcelUtils.writeToParcel(dest, OrderNo);
	}

}
