package com.cesaas.android.pos.db.bean;

/**
 * Author FGB
 * Description
 * Created at 2017/6/2 16:44
 * Version 1.0
 */

public class OrderDataBean {
    private int id;//序列Id
    private int shopCount;//商品数量
    private int orderId;//订单Id
    private String orderNo;//订单号
    private String shopName;//商品名称
    private String barcodeCode;//商品条码
    private double payPrice;//支付价格
    private double salesPrice;//销售价格
    private String weather;//天气
    private String date;//日期
    private String mobile;//手机
    private String shift;//班次
    private String salesMan;//销售员
    private String level;//等级
    private String point;//积分

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getSalesMan() {
        return salesMan;
    }

    public void setSalesMan(String salesMan) {
        this.salesMan = salesMan;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public double getPayPrice() {
        return payPrice;
    }

    public void setPayPrice(double payPrice) {
        this.payPrice = payPrice;
    }

    public double getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(double salesPrice) {
        this.salesPrice = salesPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getShopCount() {
        return shopCount;
    }

    public void setShopCount(int shopCount) {
        this.shopCount = shopCount;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getBarcodeCode() {
        return barcodeCode;
    }

    public void setBarcodeCode(String barcodeCode) {
        this.barcodeCode = barcodeCode;
    }
}
