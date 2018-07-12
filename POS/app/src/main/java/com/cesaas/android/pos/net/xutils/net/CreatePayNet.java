package com.cesaas.android.pos.net.xutils.net;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.cesaas.android.pos.bean.ResultCreatePayBean;
import com.cesaas.android.pos.db.bean.PosPayBean;
import com.cesaas.android.pos.db.pay.PosSqliteDatabaseUtils;
import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.net.xutils.BasePayNet;
import com.cesaas.android.pos.utils.AbDateUtil;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;


/**
 * 创建支付流水号
 * @author FGB
 *
 */
public class CreatePayNet extends BasePayNet{

	private AbPrefsUtil prefs;
	private String userName;
	private String orderNo;
	private double payMoney;
	private int IsPractical;

	public CreatePayNet(Context context,Activity activity,AbPrefsUtil prefs,String userName,String orderNo,double payMoney,int IsPractical) {
		super(context, activity,true);
		this.uri="Pos/Sw/Retail/CreatePay";
		this.prefs=prefs;
		this.userName=userName;
		this.orderNo=orderNo;
		this.payMoney=payMoney;
		this.IsPractical=IsPractical;
	}

	public CreatePayNet(Context context) {
		super(context, true);
		this.uri="Pos/Sw/Retail/CreatePay";
	}
	
	public void setData(String SheetId,double Payment,int PayType,String PayNo,String TraceAudit,String BankNo,String Remark,String VoucherRecord,String TId,String BankName,int SheetCategory,int CardCategory){
		try {
			data.put("SheetId",SheetId);//业务ID
			data.put("Payment",Payment);//支付金额
			data.put("PayType",PayType);//支付类型
			data.put("CurrencyType",0);//币种【默认0】
			data.put("PayNo",PayNo);//支付流水号
			data.put("TraceAudit",TraceAudit);//支付参考号
			data.put("BankNo",BankNo);//银行卡号
			data.put("Remark",Remark);//备注
			data.put("EnCode",AbPrefsUtil.getInstance().getString("enCode"));//设备EN号AbPrefsUtil.getInstance().getString("enCode")
			data.put("VoucherRecord",VoucherRecord);//凭证记录
			data.put("TId",TId);//店铺ID
			data.put("SheetCategory",SheetCategory);//0, 零售单 1, 储值 2,独立收银
			data.put("BankName",BankName);//交易银行名称
			data.put("CardCategory",CardCategory);//卡类型：1借记卡 2信用卡 3 贷记卡【微信，支付宝，现金100】
			data.put("UserTicket", AbPrefsUtil.getInstance().getString(Constant.SPF_TOKEN));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mPostNet(SheetId,prefs,Payment,PayType,IsPractical,PayNo,3,BankNo); // 开始请求网络
	}

    public void setData(String SheetId,String CreateTime,double Payment,int PayType,String PayNo,String TraceAudit,String BankNo,String Remark,String VoucherRecord,String TId,String BankName,int SheetCategory,int CardCategory){
        try {
            data.put("SheetId",SheetId);//业务ID
            data.put("Payment",Payment);//支付金额
            data.put("CreateTime",CreateTime);//创建时间
            data.put("PayType",PayType);//支付类型
            data.put("CurrencyType",0);//币种【默认0】
            data.put("PayNo",PayNo);//支付流水号
            data.put("TraceAudit",TraceAudit);//支付参考号
            data.put("BankNo",BankNo);//银行卡号
            data.put("Remark",Remark);//备注
            data.put("EnCode",AbPrefsUtil.getInstance().getString("enCode"));//设备EN号AbPrefsUtil.getInstance().getString("enCode")
            data.put("VoucherRecord",VoucherRecord);//凭证记录
            data.put("TId",TId);//店铺ID
            data.put("SheetCategory",SheetCategory);//0, 零售单 1, 储值 2,独立收银
            data.put("BankName",BankName);//交易银行名称
            data.put("CardCategory",CardCategory);//卡类型：1借记卡 2信用卡 3 贷记卡【微信，支付宝，现金100】
            data.put("UserTicket", AbPrefsUtil.getInstance().getString(Constant.SPF_TOKEN));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mPostNet(SheetId,prefs,Payment,PayType,IsPractical,PayNo,3,BankNo); // 开始请求网络
    }

	@Override
	protected void mSuccess(String rJson) {
		super.mSuccess(rJson);
		Gson gson=new Gson();
		ResultCreatePayBean lbean = gson.fromJson(rJson, ResultCreatePayBean.class);
		EventBus.getDefault().post(lbean);
	}

	@Override
	protected void mFail(HttpException e, String err) {
		super.mFail(e, err);
		Log.i("test","**********************=HttpException="+e+"..=err="+err);
//		PosPayBean posPayBean=new PosPayBean(prefs.getString("userShopId"),prefs.getString("shopNameNick"),userName,orderNo,payMoney+"",2+"","3","PayType",2+"",2+"", AbDateUtil.getCurrentDate(),"HttpException="+e+"..=err="+err,prefs.getString("enCode"),"","false","0","1");
//		insertData(posPayBean);
	}

	private void insertData(PosPayBean bean){
		PosSqliteDatabaseUtils.insterData(mContext,bean);
	}

}
