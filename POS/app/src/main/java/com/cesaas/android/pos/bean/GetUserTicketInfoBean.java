package com.cesaas.android.pos.bean;

import java.io.Serializable;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：优惠券信息Bean
 * 创建日期：2016/12/27 14:15
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class GetUserTicketInfoBean implements Serializable {

    private String String;//优惠券标题
    private String DateActive;//过期时间
    private String UseRule;//使用规则
    private String Id;//优惠券ID
    private String UniqueCode;//优惠券唯一码
    private String FansId;//优惠券所属人id
    private double Money;//优惠券面额

    public java.lang.String getString() {
        return String;
    }

    public void setString(java.lang.String string) {
        String = string;
    }

    public java.lang.String getDateActive() {
        return DateActive;
    }

    public void setDateActive(java.lang.String dateActive) {
        DateActive = dateActive;
    }

    public java.lang.String getUseRule() {
        return UseRule;
    }

    public void setUseRule(java.lang.String useRule) {
        UseRule = useRule;
    }

    public java.lang.String getId() {
        return Id;
    }

    public void setId(java.lang.String id) {
        Id = id;
    }

    public java.lang.String getUniqueCode() {
        return UniqueCode;
    }

    public void setUniqueCode(java.lang.String uniqueCode) {
        UniqueCode = uniqueCode;
    }

    public java.lang.String getFansId() {
        return FansId;
    }

    public void setFansId(java.lang.String fansId) {
        FansId = fansId;
    }

    public double getMoney() {
        return Money;
    }

    public void setMoney(double money) {
        Money = money;
    }
}
