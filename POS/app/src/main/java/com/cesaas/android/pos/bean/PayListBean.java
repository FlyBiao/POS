package com.cesaas.android.pos.bean;

import java.io.Serializable;

/**
 * Author FGB
 * Description
 * Created at 2017/12/19 15:38
 * Version 1.0
 */

public class PayListBean implements Serializable {

    private int PayId ;
    private String SheetId ;
    private double Payment ;
    private int PayType ;
    private int CurrencyType ;
    private String CreateTime ;
    private int IsDel ;
    private String PayDate ;
    private String PayNo ;
    private String TraceAudit ;
    private String BankNo ;
    private String Remark ;
    private int PayCategory ;
    private int ShopId ;
    private String UpdateTime ;
    private String EquipmentCode ;
    private String VoucherRecord ;
    private int TId ;
    private String TerminalNo ;
    private String TradeCompany ;
    private int SheetCategory ;
    private String BankName ;
    private int  CardCategory ;
    private String Cashier;

    public String getCashier() {
        return Cashier;
    }

    public void setCashier(String cashier) {
        Cashier = cashier;
    }

    public int getPayId() {
        return PayId;
    }

    public void setPayId(int payId) {
        PayId = payId;
    }

    public String getSheetId() {
        return SheetId;
    }

    public void setSheetId(String sheetId) {
        SheetId = sheetId;
    }

    public double getPayment() {
        return Payment;
    }

    public void setPayment(double payment) {
        Payment = payment;
    }

    public int getPayType() {
        return PayType;
    }

    public void setPayType(int payType) {
        PayType = payType;
    }

    public int getCurrencyType() {
        return CurrencyType;
    }

    public void setCurrencyType(int currencyType) {
        CurrencyType = currencyType;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public int getIsDel() {
        return IsDel;
    }

    public void setIsDel(int isDel) {
        IsDel = isDel;
    }

    public String getPayDate() {
        return PayDate;
    }

    public void setPayDate(String payDate) {
        PayDate = payDate;
    }

    public String getPayNo() {
        return PayNo;
    }

    public void setPayNo(String payNo) {
        PayNo = payNo;
    }

    public String getTraceAudit() {
        return TraceAudit;
    }

    public void setTraceAudit(String traceAudit) {
        TraceAudit = traceAudit;
    }

    public String getBankNo() {
        return BankNo;
    }

    public void setBankNo(String bankNo) {
        BankNo = bankNo;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public int getPayCategory() {
        return PayCategory;
    }

    public void setPayCategory(int payCategory) {
        PayCategory = payCategory;
    }

    public int getShopId() {
        return ShopId;
    }

    public void setShopId(int shopId) {
        ShopId = shopId;
    }

    public String getUpdateTime() {
        return UpdateTime;
    }

    public void setUpdateTime(String updateTime) {
        UpdateTime = updateTime;
    }

    public String getEquipmentCode() {
        return EquipmentCode;
    }

    public void setEquipmentCode(String equipmentCode) {
        EquipmentCode = equipmentCode;
    }

    public String getVoucherRecord() {
        return VoucherRecord;
    }

    public void setVoucherRecord(String voucherRecord) {
        VoucherRecord = voucherRecord;
    }

    public int getTId() {
        return TId;
    }

    public void setTId(int TId) {
        this.TId = TId;
    }

    public String getTerminalNo() {
        return TerminalNo;
    }

    public void setTerminalNo(String terminalNo) {
        TerminalNo = terminalNo;
    }

    public String getTradeCompany() {
        return TradeCompany;
    }

    public void setTradeCompany(String tradeCompany) {
        TradeCompany = tradeCompany;
    }

    public int getSheetCategory() {
        return SheetCategory;
    }

    public void setSheetCategory(int sheetCategory) {
        SheetCategory = sheetCategory;
    }

    public String getBankName() {
        return BankName;
    }

    public void setBankName(String bankName) {
        BankName = bankName;
    }

    public int getCardCategory() {
        return CardCategory;
    }

    public void setCardCategory(int cardCategory) {
        CardCategory = cardCategory;
    }
}
