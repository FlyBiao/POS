package com.cesaas.android.pos.db.order;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cesaas.android.pos.db.bean.GetOrderDataBean;
import com.cesaas.android.pos.db.bean.OrderDataBean;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


/**
 * Author FGB
 * Description
 * Created at 2017/5/19 10:48
 * Version 1.0
 */

public class OrderSQLiteDatabaseUtils extends SQLiteOpenHelper {

    private static  List<OrderDataBean> orderDataBeanArrayList=new ArrayList<>();
    private static  List<GetOrderDataBean> getOrderDataBeanArrayList=new ArrayList<>();

    //必须要有构造函数
    public OrderSQLiteDatabaseUtils(Context context, String name, SQLiteDatabase.CursorFactory factory,
                                    int version) {
        super(context, name, factory, version);
    }

    // 当第一次创建数据库的时候，调用该方法
    public void onCreate(SQLiteDatabase db) {
        //输出创建数据库的日志信息
        Log.i(DBConstant.TAG, "create Database------------->");
        //execSQL函数用于执行SQL语句
        db.execSQL(TableUtils.pos_order_sql);
    }

    //当更新数据库的时候执行该方法
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(TableUtils.del_order_sql);
        db.execSQL(TableUtils.pos_order_sql);
        Log.i(DBConstant.TAG, "当更新数据库的时候执行该方法------------->");
    }


    //创建sqlite数据库
    public static void createDB(Context ct,String dbName,int version){
        //创建StuDBHelper对象
        OrderSQLiteDatabaseUtils dbHelper = new OrderSQLiteDatabaseUtils(ct,dbName,null,version);
        //得到一个可读的SQLiteDatabase对象
        SQLiteDatabase db =dbHelper.getReadableDatabase();
    }

    //插入数据
    public static void insterData(Context ct,int shopCount,int orderId,String orderNo,String shopName,String barcodeCode,double payPrice,double salesPrice,String weather,String date,String mobile,String salesMan,String shift,String level,String point){
        OrderSQLiteDatabaseUtils dbHelper = new OrderSQLiteDatabaseUtils(ct,DBConstant.DB,null,DBConstant.VERSION);
        //得到一个可写的数据库
        SQLiteDatabase db =dbHelper.getWritableDatabase();
        //生成ContentValues对象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();
        //往ContentValues对象存放数据，键-值对模式
        cv.put("shopCount", shopCount);
        cv.put("orderId",orderId);
        cv.put("orderNo",orderNo);
        cv.put("shopName",shopName);
        cv.put("barcodeCode",barcodeCode);
        cv.put("payPrice",payPrice);
        cv.put("salesPrice",salesPrice);
        cv.put("weather",weather);
        cv.put("date",date);
        cv.put("mobile",mobile);
        cv.put("shift",shift);
        cv.put("salesMan",salesMan);
        cv.put("level",level);
        cv.put("point",point);
        //调用insert方法，将数据插入数据库
        db.insert("pos_order_table", null, cv);
        Log.i(DBConstant.TAG,"添加挂单数据成功");
        //关闭数据库
        db.close();
    }

    //查询数据
    public static void selectData(Context ct){
        OrderSQLiteDatabaseUtils dbHelper = new OrderSQLiteDatabaseUtils(ct,DBConstant.DB,null,DBConstant.VERSION);
        //得到一个可写的数据库
        SQLiteDatabase db =dbHelper.getReadableDatabase();
        //参数1：表名
        //参数2：要想显示的列
        //参数3：where子句
        //参数4：where子句对应的条件值
        //参数5：分组方式
        //参数6：having条件
        //参数7：排序方式
        orderDataBeanArrayList=new ArrayList<>();
        Cursor cursor = db.query("pos_order_table", new String[]{"id","shopCount","orderId","orderNo","shopName","barcodeCode","payPrice","salesPrice","weather","date","mobile","salesMan","shift","level","point"}, "id=?", new String[]{"1"}, null, null, null);
        while(cursor.moveToNext()){
            String shopCount = cursor.getString(cursor.getColumnIndex("shopCount"));
            String id = cursor.getString(cursor.getColumnIndex("id"));
            String orderId = cursor.getString(cursor.getColumnIndex("orderId"));
            String orderNo=cursor.getString(cursor.getColumnIndex("orderNo"));
            String shopName = cursor.getString(cursor.getColumnIndex("shopName"));
            String barcodeCode = cursor.getString(cursor.getColumnIndex("barcodeCode"));
            String payPrice = cursor.getString(cursor.getColumnIndex("payPrice"));
            String salesPrice = cursor.getString(cursor.getColumnIndex("salesPrice"));
            String weather=cursor.getString(cursor.getColumnIndex("weather"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String mobile = cursor.getString(cursor.getColumnIndex("mobile"));
            String salesMan = cursor.getString(cursor.getColumnIndex("salesMan"));
            String shift = cursor.getString(cursor.getColumnIndex("shift"));
            String level = cursor.getString(cursor.getColumnIndex("level"));
            String point = cursor.getString(cursor.getColumnIndex("point"));

            Log.i(DBConstant.TAG,"query------->" + "id："+id+" "+"orderId: "+orderId+" "+"orderNo:"+orderNo+" "+"shopName: "+shopName+" "
                    +"shopCount: "+shopCount+" "+"barcodeCode: "+barcodeCode+" "+"payPrice: "+payPrice+" "+"salesPrice: "+salesPrice+" "
                    +"date: "+date+" "+"mobile:"+mobile+" "+"salesMan:"+salesMan+" "+"shift:"+shift+" "+"level:"+level+" "+"point:"+point+" "+"weather:"+weather);

            OrderDataBean bean=new OrderDataBean();
            bean.setId(Integer.parseInt(id));
            bean.setShopCount(Integer.parseInt(shopCount));
            bean.setOrderId(Integer.parseInt(orderId));
            bean.setOrderNo(orderNo);
            bean.setShopName(shopName);
            bean.setBarcodeCode(barcodeCode);
            bean.setPayPrice(Double.parseDouble(payPrice));
            bean.setSalesPrice(Double.parseDouble(salesPrice));
            bean.setWeather(weather);
            bean.setDate(date);
            bean.setMobile(mobile);
            bean.setSalesMan(salesMan);
            bean.setShift(shift);
            bean.setLevel(level);
            bean.setPoint(point);
            orderDataBeanArrayList.add(bean);
        }
        EventBus.getDefault().post(orderDataBeanArrayList);
        //关闭数据库
        db.close();
    }

    //查询数据
    public static void getSelectData(Context ct){
        OrderSQLiteDatabaseUtils dbHelper = new OrderSQLiteDatabaseUtils(ct,DBConstant.DB,null,DBConstant.VERSION);
        //得到一个可写的数据库
        SQLiteDatabase db =dbHelper.getReadableDatabase();
        //参数1：表名
        //参数2：要想显示的列
        //参数3：where子句
        //参数4：where子句对应的条件值
        //参数5：分组方式
        //参数6：having条件
        //参数7：排序方式
        getOrderDataBeanArrayList=new ArrayList<>();
        Cursor cursor = db.query("pos_order_table", new String[]{"id","shopCount","orderId","orderNo","shopName","barcodeCode","payPrice","salesPrice","weather","date","mobile","salesMan","shift","level","point"}, "id=?", new String[]{"1"}, null, null, null);
        while(cursor.moveToNext()){
            String shopCount = cursor.getString(cursor.getColumnIndex("shopCount"));
            String id = cursor.getString(cursor.getColumnIndex("id"));
            String orderId = cursor.getString(cursor.getColumnIndex("orderId"));
            String orderNo=cursor.getString(cursor.getColumnIndex("orderNo"));
            String shopName = cursor.getString(cursor.getColumnIndex("shopName"));
            String barcodeCode = cursor.getString(cursor.getColumnIndex("barcodeCode"));
            String payPrice = cursor.getString(cursor.getColumnIndex("payPrice"));
            String salesPrice = cursor.getString(cursor.getColumnIndex("salesPrice"));
            String weather=cursor.getString(cursor.getColumnIndex("weather"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String mobile = cursor.getString(cursor.getColumnIndex("mobile"));
            String salesMan = cursor.getString(cursor.getColumnIndex("salesMan"));
            String shift = cursor.getString(cursor.getColumnIndex("shift"));
            String level = cursor.getString(cursor.getColumnIndex("level"));
            String point = cursor.getString(cursor.getColumnIndex("point"));

            GetOrderDataBean bean=new GetOrderDataBean();
            bean.setId(Integer.parseInt(id));
            bean.setShopCount(Integer.parseInt(shopCount));
            bean.setOrderId(Integer.parseInt(orderId));
            bean.setOrderNo(orderNo);
            bean.setShopName(shopName);
            bean.setBarcodeCode(barcodeCode);
            bean.setPayPrice(Double.parseDouble(payPrice));
            bean.setSalesPrice(Double.parseDouble(salesPrice));
            bean.setWeather(weather);
            bean.setDate(date);
            bean.setMobile(mobile);
            bean.setSalesMan(salesMan);
            bean.setShift(shift);
            bean.setLevel(level);
            bean.setPoint(point);
            getOrderDataBeanArrayList.add(bean);
        }
        EventBus.getDefault().post(getOrderDataBeanArrayList);
        //关闭数据库
        db.close();
    }

    //删除所有数据数据
    public static void delete(Context ct){
        OrderSQLiteDatabaseUtils dbHelper = new OrderSQLiteDatabaseUtils(ct,DBConstant.DB,null,DBConstant.VERSION);
        //得到一个可写的数据库
        SQLiteDatabase db =dbHelper.getReadableDatabase();
        db.delete("pos_order_table", null, null);
        Log.i(DBConstant.TAG,"删除数据------->" );
    }

    //删除指定数据
    public static void deleteById(Context ct,int id){
        OrderSQLiteDatabaseUtils dbHelper = new OrderSQLiteDatabaseUtils(ct,DBConstant.DB,null,DBConstant.VERSION);
        //得到一个可写的数据库
        SQLiteDatabase db =dbHelper.getReadableDatabase();
        //调用delete方法，删除数据
        String del_sql="delete from pos_order_table where id="+id;
        db.execSQL(del_sql);
        Log.i(DBConstant.TAG,"删除数据------->" );
    }
}
