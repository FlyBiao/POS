package com.cesaas.android.pos.global;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：所有后台接口URL
 * 创建日期：2016/5/6 15:35
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class Urls {

    /**
     * 生成环境 m.cesaas.com【IP:120.78.212.177】
     */
    public static String SERVER="http://m.cesaas.com/EpApi/";//
    public static String SERVER_S="http://m.cesaas.com/EpApi/";//

    /**
     * 测试环境 a.cesaas.com
     */
//    	public static String Express_IP="http://a.cesaas.com/OpenApi/";//快递物流ip
//    	public static String SERVER = "http://a.cesaas.com/EpApi/";//25测试服务
//        public static String SERVER_S="http://a.cesaas.com/EpApi/";//


        public static String TEST_SERVER = "http://192.168.1.165/EpApi/";//本地测试
//        public static String TEST_SERVER = "http://112.74.190.229/EpApi/";//本地测试


        public static String FTP_SERVER="http://112.74.135.25/Dowlond/Android/";

        public static String BANK_INFO="http://v.juhe.cn/";

    //===============================================================================================

    // 登录
    public static String USER_LOGIN=SERVER+"User/Sw/Account/Login";
    //个人信息
    public static String USER_INFO=SERVER+"User/Sw/User/Detail";
    // 上传头像
    public static String USER_UPLOAD_PHOTO=SERVER+"User/Sw/User/UploadHeadIcon";
    // 修改昵称
    public static String USER_SET_NICK=SERVER+"User/Sw/User/ModifyName";
    // 修改密码
    public static String USER_SET_PASSWORD=SERVER+"User/Sw/Password/Reset";
    //查询会员
    public static String QUERY_VIP=SERVER+"User/SW/Fans/GetFansInfoByMobile";
    //按店铺查询粉丝
    public static String FANS_SHOP=SERVER+"User/Sw/Fans/GetListByShopId";

    //订单====================================//
    //订单详情
    public static String GET_ORDER=SERVER+"Pos/Sw/Order/GetOrder";
    //创建在线下单收银二维码
    public static String CREATE_FROM_STORE=SERVER+"Pos/Sw/StoreCashi/CreateFromStore";
    //银联支付回调
    public static String PAY_FROM_STORE=SERVER+"Pos/Sw/Retail/PayFromStore";
    //查账
    public static String POS_ORDER_LIST=SERVER+"Pos/Sw/StoreCashi/PosOrderList";
    //退款
    public static String POS_OFFLINE_REFUND=SERVER+"Pos/Sw/Retail/OfflineRefund";
    //账单详情
    public static String PAY_JOURNAL=SERVER+"Pos/Sw/Retail/PayJournalList";
    //待支付订单详情
    public static String WAIT_PAY_ORDER=SERVER+"Pos/Sw/Retail/Query";
    //账单列表
    public static String PAY_ORDER_LIST=SERVER+"Pos/Sw/Retail/PayList";
    //获取商品条码订单
    public static String GET_BY_BARCODE_CODE=SERVER_S+"Marketing/Sw/Style/GetByBarcodeCode";
    //pos对账查询【数据统计】
    public static String POS_STATISTICS=SERVER_S+"Pos/Sw/Retail/Statistics";
    //查询pos激活状态
    public static String POS_ACTIVE_STATUS=SERVER+"Pos/Sw/Account/ActiveStatus";
    //激活POS机
    public static String POS_ACTIVE=SERVER+"Pos/Sw/Account/Active";
    //POS日志记录
    public static String PAY_LOG=SERVER+"Pos/Sw/Log/LogAsync";
    //营销活动列表
    public static String MARKETING_ACTIVITY_LIST=SERVER+"Pos/Sw/PromotionActivity/GetList";
    //营销活动选择结果
    public static String GET_AVTIVITY_RESULT=SERVER+"Pos/Sw/PromotionActivity/GetActivityResult";

    //优惠券列表
    public static String GET_USER_TICKET=SERVER+"Marketing/Sw/Ticket/GetUseTicket";
    //优惠券查询
    public static String GET_USER_TICKER_INFO=SERVER+"Marketing/Sw/Ticket/GetTicketInfo";
    //验证优惠券是否可用
    public static String GET_USER_TICKET_Available=SERVER+"Marketing/Sw/Ticket/TicketIsAvailable";

    //班次
    public static String GET_WorkSHIFT=FTP_SERVER+"WorkShift.json";
//    //天气
    public static String GET_WEATHER=FTP_SERVER+"Weather.json";
    //支付方式
    public static String GET_PAY_CATEGORY=SERVER+"Pos/Sw/Retail/PayCategoryList";

}
