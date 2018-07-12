package com.cesaas.android.pos.db.pay;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cesaas.android.pos.db.bean.PosPayBean;
import com.cesaas.android.pos.db.bean.ResultPayLogBean;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Author FGB
 * Description
 * Created at 2017/11/20 21:12
 * Version 1.0
 */

public class PosSqliteDatabaseUtils extends SQLiteOpenHelper {
    public static final String TAG = "TestSQLite";
    private static List<PosPayBean> posPayBeanArrayList=new ArrayList<>();
    private static ResultPayLogBean resultPayLogBean=new ResultPayLogBean();
    //必须要有构造函数
    public PosSqliteDatabaseUtils(Context context, String name, SQLiteDatabase.CursorFactory factory,
                                  int version) {
        super(context, name, factory, version);
    }

    // 当第一次创建数据库的时候，调用该方法
    public void onCreate(SQLiteDatabase db) {
        try {
            //输出创建数据库的日志信息
            //execSQL函数用于执行SQL语句
//            db.execSQL(TableUtils.create_pos_scan_info_sql);
            db.execSQL(TableUtils.create_pos_pay_log_sql);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //当更新数据库的时候执行该方法
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(db.getVersion()!=DBConstant.VERSION){
            //发现新版本数据库
//            db.execSQL("DROP TABLE pos_scan_info");
//            db.execSQL("DROP TABLE pos_pay_info");

            db.execSQL(TableUtils.create_pos_pay_log_sql);
        }
    }

    //创建sqlite数据库
    public static void createDB(Context ct, String dbName, int version){
        //创建StuDBHelper对象
        PosSqliteDatabaseUtils dbHelper = new PosSqliteDatabaseUtils(ct,dbName,null,version);
        //得到一个可读的SQLiteDatabase对象
        SQLiteDatabase db =dbHelper.getReadableDatabase();
    }

    /**
     * 插入支付数据
     * @param ct
     * @param bean
     */
    public static void insterData(Context ct, PosPayBean bean){
        PosSqliteDatabaseUtils dbHelper = new PosSqliteDatabaseUtils(ct,DBConstant.DB,null,DBConstant.VERSION);
        //得到一个可写的数据库
        SQLiteDatabase db =dbHelper.getWritableDatabase();
        //生成ContentValues对象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();
        try {
            //往ContentValues对象存放数据，键-值对模式
            cv.put("CreateTime",bean.getCreateTime());
            cv.put("ShopId", bean.getShopId());
            cv.put("ShopName", bean.getShopName());
            cv.put("CreateName",bean.getCreateName());
            cv.put("OrderNo",bean.getOrderNo());
            cv.put("Amount",bean.getAmount());
            cv.put("TraceAudit",bean.getTraceAudit());
            cv.put("PayType",bean.getPayType());
            cv.put("PayName",bean.getPayName());
            cv.put("IsPractical",bean.getIsPractical());
            cv.put("PayStatus",bean.getPayStatus());
            cv.put("Message",bean.getMessage());
            cv.put("EnCode",bean.getEnCode());
            cv.put("AccountNumber",bean.getAccountNumber());
            cv.put("IsSuccess",bean.getIsSuccess());
            cv.put("OrderType",bean.getOrderType());
            cv.put("OrderStatus",bean.getOrderStatus());
            //调用insert方法，将数据插入数据库
            db.insert(TableUtils.pos_pay_log, null, cv);
        }catch (Exception e){
            e.printStackTrace();
        }
        //关闭数据库
        db.close();
    }

    /**
     * 查询所有支付数据
     * @param ct
     * @param
     */
    public static void selectData(Context ct){
        PosSqliteDatabaseUtils dbHelper = new PosSqliteDatabaseUtils(ct,DBConstant.DB,null,DBConstant.VERSION);
        //得到一个可写的数据库
        SQLiteDatabase db =dbHelper.getReadableDatabase();
        //参数1：表名
        //参数2：要想显示的列
        //参数3：where子句
        //参数4：where子句对应的条件值
        //参数5：分组方式
        //参数6：having条件
        //参数7：排序方式
        resultPayLogBean=new ResultPayLogBean();
        try {
            Cursor cursor = db.query(TableUtils.pos_pay_log, new String[]{"ShopId","ShopName","CreateName","OrderNo","Amount","TraceAudit","PayType","PayName","IsPractical","PayStatus","CreateTime","Message","EnCode","AccountNumber","IsSuccess","OrderType","OrderStatus"}, null, null, null, null, null);
            while(cursor.moveToNext()){
                String ShopId = cursor.getString(cursor.getColumnIndex("ShopId"));
                String ShopName = cursor.getString(cursor.getColumnIndex("ShopName"));
                String CreateName = cursor.getString(cursor.getColumnIndex("CreateName"));
                String OrderNo=cursor.getString(cursor.getColumnIndex("OrderNo"));
                String Amount = cursor.getString(cursor.getColumnIndex("Amount"));
                String TraceAudit = cursor.getString(cursor.getColumnIndex("TraceAudit"));
                String PayType = cursor.getString(cursor.getColumnIndex("PayType"));
                String PayName = cursor.getString(cursor.getColumnIndex("PayName"));
                String IsPractical=cursor.getString(cursor.getColumnIndex("IsPractical"));
                String PayStatus = cursor.getString(cursor.getColumnIndex("PayStatus"));
                String CreateTime = cursor.getString(cursor.getColumnIndex("CreateTime"));
                String Message = cursor.getString(cursor.getColumnIndex("Message"));
                String EnCode = cursor.getString(cursor.getColumnIndex("EnCode"));
                String AccountNumber = cursor.getString(cursor.getColumnIndex("AccountNumber"));
                String IsSuccess = cursor.getString(cursor.getColumnIndex("IsSuccess"));
                String OrderType = cursor.getString(cursor.getColumnIndex("OrderType"));
                String OrderStatus = cursor.getString(cursor.getColumnIndex("OrderStatus"));

                PosPayBean bean=new PosPayBean();
                bean.setShopId(ShopId);
                bean.setShopName(ShopName);
                bean.setCreateName(CreateName);
                bean.setOrderNo(OrderNo);
                bean.setAmount(Amount);
                bean.setTraceAudit(TraceAudit);
                bean.setPayType(PayType);
                bean.setPayName(PayName);
                bean.setIsPractical(IsPractical);
                bean.setPayStatus(PayStatus);
                bean.setCreateTime(CreateTime);
                bean.setMessage(Message);
                bean.setEnCode(EnCode);
                bean.setAccountNumber(AccountNumber);
                bean.setIsSuccess(IsSuccess);
                bean.setOrderType(OrderType);
                bean.setOrderStatus(OrderStatus);
                resultPayLogBean.getPosPayBeanArrayList().add(bean);
//                posPayBeanArrayList.add(bean);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        EventBus.getDefault().post(resultPayLogBean);
        //关闭数据库
        db.close();
    }

    /**
     * 根据订单号查询支付数据
     * @param ct
     * @param OrderId
     */
    public static void selectByOrderNoData(Context ct,String OrderId){
        PosSqliteDatabaseUtils dbHelper = new PosSqliteDatabaseUtils(ct,DBConstant.DB,null,DBConstant.VERSION);
        //得到一个可写的数据库
        SQLiteDatabase db =dbHelper.getReadableDatabase();
        //参数1：表名
        //参数2：要想显示的列
        //参数3：where子句
        //参数4：where子句对应的条件值
        //参数5：分组方式
        //参数6：having条件
        //参数7：排序方式
        try {
            Cursor cursor = db.query(TableUtils.pos_pay_log, new String[]{"ShopId","ShopName","CreateName","OrderNo","Amount","TraceAudit","PayType","PayName","IsPractical","PayStatus","CreateTime","Message","EnCode","AccountNumber","IsSuccess","OrderType","OrderStatus"},"OrderNo=?", new String[]{OrderId}, null, null, null);
            while(cursor.moveToNext()){
                String ShopId = cursor.getString(cursor.getColumnIndex("ShopId"));
                String ShopName = cursor.getString(cursor.getColumnIndex("ShopName"));
                String CreateName = cursor.getString(cursor.getColumnIndex("CreateName"));
                String OrderNo=cursor.getString(cursor.getColumnIndex("OrderNo"));
                String Amount = cursor.getString(cursor.getColumnIndex("Amount"));
                String TraceAudit = cursor.getString(cursor.getColumnIndex("TraceAudit"));
                String PayType = cursor.getString(cursor.getColumnIndex("PayType"));
                String PayName = cursor.getString(cursor.getColumnIndex("PayName"));
                String IsPractical=cursor.getString(cursor.getColumnIndex("IsPractical"));
                String PayStatus = cursor.getString(cursor.getColumnIndex("PayStatus"));
                String CreateTime = cursor.getString(cursor.getColumnIndex("CreateTime"));
                String Message = cursor.getString(cursor.getColumnIndex("Message"));
                String EnCode = cursor.getString(cursor.getColumnIndex("EnCode"));
                String AccountNumber = cursor.getString(cursor.getColumnIndex("AccountNumber"));
                String IsSuccess = cursor.getString(cursor.getColumnIndex("IsSuccess"));
                String OrderType = cursor.getString(cursor.getColumnIndex("OrderType"));
                String OrderStatus = cursor.getString(cursor.getColumnIndex("OrderStatus"));

                PosPayBean bean=new PosPayBean();
                bean.setShopId(ShopId);
                bean.setShopName(ShopName);
                bean.setCreateName(CreateName);
                bean.setOrderNo(OrderNo);
                bean.setAmount(Amount);
                bean.setTraceAudit(TraceAudit);
                bean.setPayType(PayType);
                bean.setPayName(PayName);
                bean.setIsPractical(IsPractical);
                bean.setPayStatus(PayStatus);
                bean.setCreateTime(CreateTime);
                bean.setMessage(Message);
                bean.setEnCode(EnCode);
                bean.setAccountNumber(AccountNumber);
                bean.setIsSuccess(IsSuccess);
                bean.setOrderType(OrderType);
                bean.setOrderStatus(OrderStatus);
                EventBus.getDefault().post(bean);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //关闭数据库
        db.close();
    }

    /**
     * 根据订单状态查询支付数据
     * @param ct
     * @param OrderStatus
     */
    public static void selectByOrderStatusData(Context ct,String OrderStatus){
        PosSqliteDatabaseUtils dbHelper = new PosSqliteDatabaseUtils(ct,DBConstant.DB,null,DBConstant.VERSION);
        //得到一个可写的数据库
        SQLiteDatabase db =dbHelper.getReadableDatabase();
        //参数1：表名
        //参数2：要想显示的列
        //参数3：where子句
        //参数4：where子句对应的条件值
        //参数5：分组方式
        //参数6：having条件
        //参数7：排序方式
        posPayBeanArrayList=new ArrayList<>();
        try {
            Cursor cursor = db.query(TableUtils.pos_pay_log, new String[]{"ShopId","ShopName","CreateName","OrderNo","Amount","TraceAudit","PayType","PayName","IsPractical","PayStatus","CreateTime","Message","EnCode","AccountNumber","IsSuccess","OrderType","OrderStatus"},"OrderStatus=?", new String[]{OrderStatus}, null, null, null);
            while(cursor.moveToNext()){
                String ShopId = cursor.getString(cursor.getColumnIndex("ShopId"));
                String ShopName = cursor.getString(cursor.getColumnIndex("ShopName"));
                String CreateName = cursor.getString(cursor.getColumnIndex("CreateName"));
                String OrderNo=cursor.getString(cursor.getColumnIndex("OrderNo"));
                String Amount = cursor.getString(cursor.getColumnIndex("Amount"));
                String TraceAudit = cursor.getString(cursor.getColumnIndex("TraceAudit"));
                String PayType = cursor.getString(cursor.getColumnIndex("PayType"));
                String PayName = cursor.getString(cursor.getColumnIndex("PayName"));
                String IsPractical=cursor.getString(cursor.getColumnIndex("IsPractical"));
                String PayStatus = cursor.getString(cursor.getColumnIndex("PayStatus"));
                String CreateTime = cursor.getString(cursor.getColumnIndex("CreateTime"));
                String Message = cursor.getString(cursor.getColumnIndex("Message"));
                String EnCode = cursor.getString(cursor.getColumnIndex("EnCode"));
                String AccountNumber = cursor.getString(cursor.getColumnIndex("AccountNumber"));
                String IsSuccess = cursor.getString(cursor.getColumnIndex("IsSuccess"));
                String OrderType = cursor.getString(cursor.getColumnIndex("OrderType"));
                String OrderStatuss = cursor.getString(cursor.getColumnIndex("OrderStatus"));

                PosPayBean bean=new PosPayBean();
                bean.setShopId(ShopId);
                bean.setShopName(ShopName);
                bean.setCreateName(CreateName);
                bean.setOrderNo(OrderNo);
                bean.setAmount(Amount);
                bean.setTraceAudit(TraceAudit);
                bean.setPayType(PayType);
                bean.setPayName(PayName);
                bean.setIsPractical(IsPractical);
                bean.setPayStatus(PayStatus);
                bean.setCreateTime(CreateTime);
                bean.setMessage(Message);
                bean.setEnCode(EnCode);
                bean.setAccountNumber(AccountNumber);
                bean.setIsSuccess(IsSuccess);
                bean.setOrderType(OrderType);
                bean.setOrderStatus(OrderStatuss);
                posPayBeanArrayList.add(bean);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        EventBus.getDefault().post(posPayBeanArrayList);
        //关闭数据库
        db.close();
    }

    /**
     * 根据订单号修改数据
     * @param ct
     * @param payStatus
     * @param message
     * @param isSuccess
     * @param OrderNo
     */
    public static void updateUnionPay(Context ct,String payStatus,String message,String isSuccess,String OrderNo,String OrderStatus,String TraceAudit){
        PosSqliteDatabaseUtils dbHelper = new PosSqliteDatabaseUtils(ct,DBConstant.DB,null,DBConstant.VERSION);
        //得到一个可写的数据库
        SQLiteDatabase db =dbHelper.getWritableDatabase();
        String updateSql="";
        try{
            updateSql="UPDATE pos_pay_log set PayStatus='"+payStatus +"',Message='"+message+"',IsSuccess='"+isSuccess+"',OrderStatus='"+OrderStatus+"',TraceAudit='"+TraceAudit+"' where OrderNo="+OrderNo;
            db.execSQL(updateSql);
        }catch (Exception e){
            e.printStackTrace();
        }
        //关闭数据库
        db.close();
    }

    public static void update(Context ct,String payStatus,String message,String isSuccess,String OrderNo,String OrderStatus){
        PosSqliteDatabaseUtils dbHelper = new PosSqliteDatabaseUtils(ct,DBConstant.DB,null,DBConstant.VERSION);
        //得到一个可写的数据库
        SQLiteDatabase db =dbHelper.getWritableDatabase();
        String updateSql="";
        try{
            updateSql="UPDATE pos_pay_log set PayStatus='"+payStatus +"',Message='"+message+"',IsSuccess='"+isSuccess+"',OrderStatus='"+OrderStatus+"' where OrderNo="+OrderNo;
            db.execSQL(updateSql);
        }catch (Exception e){
            e.printStackTrace();
        }
        //关闭数据库
        db.close();
    }

    /**
     * 删除支付信息所有数据数据
     * @param ct
     */
    public static void delete(Context ct){
        PosSqliteDatabaseUtils dbHelper = new PosSqliteDatabaseUtils(ct,DBConstant.DB,null,DBConstant.VERSION);
        //得到一个可写的数据库
        SQLiteDatabase db =dbHelper.getReadableDatabase();
        try {
            db.delete(TableUtils.pos_pay_log, null, null);
        }catch (Exception e){
            e.printStackTrace();
        }
        //关闭数据库
        db.close();
    }

    /**
     * 根据订单号删除指定数据
     * @param ct
     * @param OrderNo
     */
    public static void deleteByNo(Context ct,String OrderNo){
        PosSqliteDatabaseUtils dbHelper = new PosSqliteDatabaseUtils(ct,DBConstant.DB,null,DBConstant.VERSION);
        //得到一个可写的数据库
        SQLiteDatabase db =dbHelper.getReadableDatabase();
        try {
            db.delete(TableUtils.pos_pay_log, "OrderNo=?", new String[]{OrderNo});
        }catch (Exception e){
            e.printStackTrace();
        }
        //关闭数据库
        db.close();
    }
}
