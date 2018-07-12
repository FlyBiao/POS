package com.cesaas.android.pos.bean;

import java.io.Serializable;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：优惠券列表Bean
 * 创建日期：2017/2/9 10:41
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class GetUserTicketListBean implements Serializable{
    public String DateActive;
    public String Id;
    public String Title;
    public String UniqueCode;
    public String UseRule;
    public double Money;
    public int IsUsed;// 0 未使用 1 已使用
}
