package com.cesaas.android.pos.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import com.cesaas.android.pos.bean.SettleAccountsList;
import com.cesaas.android.pos.bean.printer.LatticePrinterSettleBean;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;

import cn.weipass.pos.sdk.IPrint;
import cn.weipass.pos.sdk.LatticePrinter;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：Pos结算点阵打印工具
 * 创建日期：2016/11/7 15:13
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class SettlePrinterTools {

    /**
     * font_size:字体大小枚举值 SMALL:16x16大小; MEDIUM:24x24大小; LARGE:32x32大小;
     * EXTRALARGE:48x48 一行的宽度为384
     * (当宽度大小为16时可打印384/16=24个字符;为24时可打印384/24=16个字符;为32时可
     * 打印384/32=12个字符;为48时可打印384/48=8个字符（一个汉字占1个字符，一个字母 、空格或者数字占半字符）
     *
     * 标准打印示例
     *
     * @param context
     * @param printer
     */
    public static final int rowSize = 384;
    // public static final int smallSize = (int) (384/16d);
    // public static final int mediumSize = (int) (384/24d);
    // public static final int largeSize = (int) (384/32d);
    // public static final int extralargeSize = (int) (384/48d);
    public static final int smallSize = 24 * 2;
    public static final int mediumSize = 16 * 2;
    public static final int largeSize = 12 * 2;
    public static final int extralargeSize = 8 * 2;

    public static String getPrintErrorInfo(int what, String info) {
        String message = "";
        switch (what) {
            case IPrint.EVENT_CONNECT_FAILD:
                message = "连接打印机失败";
                break;
            case IPrint.EVENT_CONNECTED:
                // Log.e("subscribe_msg", "连接打印机成功");
                break;
            case IPrint.EVENT_PAPER_JAM:
                message = "打印机卡纸";
                break;
            case IPrint.EVENT_UNKNOW:
                message = "打印机未知错误";
                break;
            case IPrint.EVENT_STATE_OK:
                //打印机状态正常
                break;
            case IPrint.EVENT_OK://
                // 回调函数中不能做UI操作，所以可以使用runOnUiThread函数来包装一下代码块
                // 打印完成结束
                break;
            case IPrint.EVENT_NO_PAPER:
                message = "打印机缺纸";
                break;
            case IPrint.EVENT_HIGH_TEMP:
                message = "打印机高温";
                break;
            case IPrint.EVENT_PRINT_FAILD:
                message = "打印失败";
                break;
        }

        return message;
    }

    /**
     * 获取md5加密信息
     *
     * @param s
     * @return
     */
    public static String getStringMD5(String s) {
        // char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
        // '9',
        // 'A', 'B', 'C', 'D', 'E', 'F' };
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * \n 代表换行 点阵打印示例
     *
     * @param context
     * @param latticePrinter
     */
    public static void printLattice(Context context, LatticePrinter latticePrinter, LatticePrinterSettleBean printerBean, ArrayList<SettleAccountsList> listOrder,String startDate,String endData) {
        String mediumSpline = "";
        for (int i = 0; i < mediumSize; i++) {
            mediumSpline += "-";
        }

        //备注，点阵打印FontSize.EXTRALARGE字体不支持
        String title = "结算清单";
        int sizeTitle = largeSize - SettlePrinterTools.length(title);
        // 文字居中需要在前面补足相应空格，后面可以用换行符换行
        String titleStr = getBlankBySize((int) (sizeTitle / 2d)) + title;
        latticePrinter.printText(titleStr + "\n", LatticePrinter.FontFamily.SONG,
                LatticePrinter.FontSize.LARGE, LatticePrinter.FontStyle.BOLD);

        int feedCount = 1;
        //进纸数，1代表进一行的高度
        latticePrinter.feed(feedCount);
        // 打印分割线
        latticePrinter.printText(mediumSpline, LatticePrinter.FontFamily.SONG,
                LatticePrinter.FontSize.MEDIUM, LatticePrinter.FontStyle.BOLD);
        latticePrinter.feed(feedCount);

        String guitai = "结算柜台："+printerBean.getShopName();
        latticePrinter.printText(guitai + "\n", LatticePrinter.FontFamily.SONG, LatticePrinter.FontSize.MEDIUM,
                LatticePrinter.FontStyle.BOLD);

        String name = "当前营业额："+printerBean.getCurrentTurnover();
        latticePrinter.printText(name + "\n", LatticePrinter.FontFamily.SONG, LatticePrinter.FontSize.MEDIUM,
                LatticePrinter.FontStyle.BOLD);

        String refundAmount = "当前退款额："+printerBean.getRefundAmount();
        latticePrinter.printText(refundAmount + "\n", LatticePrinter.FontFamily.SONG, LatticePrinter.FontSize.MEDIUM,
                LatticePrinter.FontStyle.BOLD);

        String marchent = "当前订单数："+printerBean.getPayOrderCount();
        latticePrinter.printText(marchent + "\n", LatticePrinter.FontFamily.SONG,
                LatticePrinter.FontSize.MEDIUM, LatticePrinter.FontStyle.BOLD);

        String price = "操作人员："+printerBean.getShopClerkName();
        latticePrinter.printText(price + "\n", LatticePrinter.FontFamily.SONG,
                LatticePrinter.FontSize.MEDIUM, LatticePrinter.FontStyle.BOLD);

        String time = "结算时间："+AbDateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss");
        latticePrinter.printText(time+ "\n", LatticePrinter.FontFamily.SONG, LatticePrinter.FontSize.MEDIUM,
                LatticePrinter.FontStyle.BOLD);

        String scope  = "结算范围："+startDate+"至"+endData;
        latticePrinter.printText(scope , LatticePrinter.FontFamily.SONG, LatticePrinter.FontSize.MEDIUM,
                LatticePrinter.FontStyle.BOLD);

        latticePrinter.feed(feedCount);
        // 打印分割线
        latticePrinter.printText(mediumSpline, LatticePrinter.FontFamily.SONG,
                LatticePrinter.FontSize.MEDIUM, LatticePrinter.FontStyle.BOLD);
        latticePrinter.feed(feedCount);

        String list = "结算列表：";
        latticePrinter.printText(list, LatticePrinter.FontFamily.SONG, LatticePrinter.FontSize.MEDIUM,
                LatticePrinter.FontStyle.BOLD);

        latticePrinter.feed(feedCount);
        // 打印分割线
        latticePrinter.printText(mediumSpline, LatticePrinter.FontFamily.SONG,
                LatticePrinter.FontSize.MEDIUM, LatticePrinter.FontStyle.BOLD);
        latticePrinter.feed(feedCount);

        ArrayList<String> dataList = new ArrayList<String>();

        //遍历OrderItem
        for (int i=0;i<listOrder.size();i++){
            if(listOrder.get(i).getPayType()==2){//微信
                dataList.add("微信支付                "+listOrder.get(i).getPayMent());
            }else if(listOrder.get(i).getPayType()==3){//支付宝
                dataList.add("支付宝                  "+listOrder.get(i).getPayMent());
            }else if(listOrder.get(i).getPayType()==4){//银联
                dataList.add("银联支付                "+listOrder.get(i).getPayMent());
            }else{//现金
                dataList.add("现金支付                "+listOrder.get(i).getPayMent());
            }

            dataList.add("实收金额                "+listOrder.get(i).getPayMent());
            dataList.add("支付单数                 "+listOrder.get(i).getOrderCount()+"单");

            if(listOrder.get(i).getPayCategory()==1) {//退款
                dataList.add("退款金额                "+listOrder.get(i).getPayMent()+ "\n");
            }else{
                dataList.add("退款金额                 无退款\n");
            }

        }

        StringBuffer sb = new StringBuffer();
        int size = dataList.size();

        for (int i = 0; i < size; i++) {
            String item = dataList.get(i);
            if (i == 0) {
                sb.append(item + "\n");
            } else {
                //分行，预留空白内容
                sb.append(item + "\n");
            }
        }
        latticePrinter.printText(sb.toString(), LatticePrinter.FontFamily.SONG,
                LatticePrinter.FontSize.MEDIUM, LatticePrinter.FontStyle.BOLD);

        latticePrinter.feed(feedCount);
        // 打印分割线
        latticePrinter.printText( "================================", LatticePrinter.FontFamily.SONG,
                LatticePrinter.FontSize.MEDIUM, LatticePrinter.FontStyle.BOLD);
        latticePrinter.feed(feedCount);

        String test = "该结算票据作为唯一收款凭证，请妥善保管";
        latticePrinter.printText(test, LatticePrinter.FontFamily.SONG,
                LatticePrinter.FontSize.MEDIUM, LatticePrinter.FontStyle.BOLD);

        //最后进纸10行,方便撕纸
        latticePrinter.feed(7);
        // 真正提交打印事件
        latticePrinter.submitPrint();
    }

    public static boolean isLetter(char c) {
        int k = 0x80;
        return c / k == 0 ? true : false;
    }

    /**
     * 判断字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isNull(String str) {
        if (str == null || str.trim().equals("")
                || str.trim().equalsIgnoreCase("null")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为2,英文字符长度为1
     *
     *            s 需要得到长度的字符串
     * @return int 得到的字符串长度
     */
    public static int length(String s) {
        if (s == null)
            return 0;
        char[] c = s.toCharArray();
        int len = 0;
        for (int i = 0; i < c.length; i++) {
            len++;
            if (!isLetter(c[i])) {
                len++;
            }
        }
        return len;
    }

    /**
     * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为1,英文字符长度为0.5
     *
     *            s 需要得到长度的字符串
     * @return int 得到的字符串长度
     */
    public static double getLength(String s) {
        if (s == null) {
            return 0;
        }
        double valueLength = 0;
        String chinese = "[\u4e00-\u9fa5]";
        // 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
        for (int i = 0; i < s.length(); i++) {
            // 获取一个字符
            String temp = s.substring(i, i + 1);
            // 判断是否为中文字符
            if (temp.matches(chinese)) {
                // 中文字符长度为1
                valueLength += 1;
            } else {
                // 其他字符长度为0.5
                valueLength += 0.5;
            }
        }
        // 进位取整
        return Math.ceil(valueLength);
    }

    public static String getBlankBySize(int size) {
        String resultStr = "";
        for (int i = 0; i < size; i++) {
            resultStr += " ";
        }
        return resultStr;
    }

    // 将Drawable转化为Bitmap
    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    // Bitmap → byte[]
    public static byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
}