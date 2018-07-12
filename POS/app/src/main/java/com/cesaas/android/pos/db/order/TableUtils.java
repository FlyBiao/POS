package com.cesaas.android.pos.db.order;

/**
 * Author FGB
 * Description
 * Created at 2017/5/31 16:21
 * Version 1.0
 */

public class TableUtils {
    //String weather,String date,String mobile,String salesMan,String shift,String level,String point
    public static final String pos_order_sql = "create table pos_order_table(id INTEGER PRIMARY KEY AUTOINCREMENT,shopCount int,orderId int,orderNo varchar(200),shopName varchar(200),barcodeCode varchar(200),payPrice double,salesPrice double,weather varchar(100),date varchar(100),mobile varchar(100),salesMan varchar(100),shift varchar(100),level varchar(100),point varchar(100))";
    public static final String del_order_sql = "DROP TABLE pos_order_table";
}
