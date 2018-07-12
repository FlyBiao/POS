package com.cesaas.android.pos.pos;

import android.app.Activity;
import android.content.Context;

import com.cesaas.android.pos.callback.PosCallBack;
import com.cesaas.android.pos.utils.ACache;
import com.wangpos.pay.UnionPay.PosConfig;
import com.wangpos.poscore.PosCore;
import com.wangpos.poscore.impl.PosCoreFactory;

import java.util.HashMap;

/**
 * Author FGB
 * Description 初始化
 * Created at 2017/12/21 15:55
 * Version 1.0
 */

public class InitPosCore {

    public static PosCore pCore;
//    public static PosCallBack callBack;

    public static void initPosCore(Context ct,String shopNameNick) {
        if (pCore == null) {

            // 配置数据为开发阶段的数据
            HashMap<String, String> init_params = new HashMap<String,String>();

            init_params.put(PosConfig.Name_EX + "1053", shopNameNick);// 签购单小票台头
            init_params.put(PosConfig.Name_EX + "1100", "cn.weipass.cashier");// 核心APP 包名
            init_params.put(PosConfig.Name_EX + "1101", "com.wangpos.cashiercoreapp.services.CoreAppService");// 核心APP 类名
            init_params.put(PosConfig.Name_EX + "1092", "1");//  是否开启订单生成，并且上报服务器 1.开启 0.不开启
            init_params.put(PosConfig.Name_EX + "1093", "2");// 是否需要打印三联签购单 1.需要 2.不需要
            init_params.put(PosConfig.Name_EX + "1012", "1");// 华势通道

            init_params.put(PosConfig.Name_MerchantName,shopNameNick);

//            init_params.put(PosConfig.Name_EX + "1600", "这是小票:${myOrderNo}");
            init_params.put(PosConfig.Name_EX + "1600_1", "这是第一联的小票:${1D:${myOrderNo}}");
            init_params.put(PosConfig.Name_EX + "1600_2", "这是第二联的小票:${2D:${myOrderNo}}");

            init_params.put(PosConfig.Name_EX + "1091", "0");// 交易成功，就删除冲正文件 不以小票打印是否成功 为判断点 1.不以小票打印成功为判断点 0.以小票打印成功为判断点
            init_params.put(PosConfig.Name_EX + "1054", "1");// 是否打印小票  1，可以打 0，不打
//            init_params.put(PosConfig.Name_EX + "1087", "0");//是否 关闭插卡 1.开启插卡 0.关闭插卡
            init_params.put(PosConfig.Name_EX + "1089", "1");//是否 需要获取 已开通的支付方式 1.获取 0.不获取
            init_params.put(PosConfig.Name_EX + "1085", "1");//是否支持TC校验，批上送 1，是 0，不是
            init_params.put(PosConfig.Name_EX + "1086", "1");//是否启动 电子签名 1.是 0 不是
//            init_params.put(PosConfig.Name_EX + "1088", "0");//是否 关闭批结算 1.开启批结算 0.关闭批结算
//			init_params.put(PosConfig.Name_EX + "1082", "36");// 主密钥索引

            pCore = PosCoreFactory.newInstance(ct, init_params);
//            callBack = new PosCallBack(pCore,activity,mCache,ct);
        }
    }
}
