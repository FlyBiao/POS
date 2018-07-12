package com.cesaas.android.pos.bean;

import java.io.Serializable;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：店铺VIP
 * 创建日期：2016/10/31 17:37
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class ShopVipBean extends  BaseBean{

    public ShopVip TModel;

    public class ShopVip implements Serializable {

        private String FANS_SHOPNAME;
        private int FANS_SHOPID;
        private int FANS_POINT;
        private String FANS_NICKNAME;
        private String FANS_NAME;
        private String FANS_MOBILE;
        private String FANS_OPENID;
        private int FANS_ID;
        private String FANS_GRADE;
        private int FANS_GRADEID;
        private int MEMBER_ID;
        private double FANS_DISCOUNT;

        public int getMEMBER_ID() {
            return MEMBER_ID;
        }

        public void setMEMBER_ID(int MEMBER_ID) {
            this.MEMBER_ID = MEMBER_ID;
        }

        public String getFANS_OPENID() {
            return FANS_OPENID;
        }

        public void setFANS_OPENID(String FANS_OPENID) {
            this.FANS_OPENID = FANS_OPENID;
        }

        public int getFANS_GRADEID() {
            return FANS_GRADEID;
        }

        public void setFANS_GRADEID(int FANS_GRADEID) {
            this.FANS_GRADEID = FANS_GRADEID;
        }

        public String getFANS_SHOPNAME() {
            return FANS_SHOPNAME;
        }

        public void setFANS_SHOPNAME(String FANS_SHOPNAME) {
            this.FANS_SHOPNAME = FANS_SHOPNAME;
        }

        public int getFANS_SHOPID() {
            return FANS_SHOPID;
        }

        public void setFANS_SHOPID(int FANS_SHOPID) {
            this.FANS_SHOPID = FANS_SHOPID;
        }

        public int getFANS_POINT() {
            return FANS_POINT;
        }

        public void setFANS_POINT(int FANS_POINT) {
            this.FANS_POINT = FANS_POINT;
        }

        public String getFANS_NICKNAME() {
            return FANS_NICKNAME;
        }

        public void setFANS_NICKNAME(String FANS_NICKNAME) {
            this.FANS_NICKNAME = FANS_NICKNAME;
        }

        public String getFANS_NAME() {
            return FANS_NAME;
        }

        public void setFANS_NAME(String FANS_NAME) {
            this.FANS_NAME = FANS_NAME;
        }

        public String getFANS_MOBILE() {
            return FANS_MOBILE;
        }

        public void setFANS_MOBILE(String FANS_MOBILE) {
            this.FANS_MOBILE = FANS_MOBILE;
        }

        public int getFANS_ID() {
            return FANS_ID;
        }

        public void setFANS_ID(int FANS_ID) {
            this.FANS_ID = FANS_ID;
        }

        public String getFANS_GRADE() {
            return FANS_GRADE;
        }

        public void setFANS_GRADE(String FANS_GRADE) {
            this.FANS_GRADE = FANS_GRADE;
        }

        public double getFANS_DISCOUNT() {
            return FANS_DISCOUNT;
        }

        public void setFANS_DISCOUNT(double FANS_DISCOUNT) {
            this.FANS_DISCOUNT = FANS_DISCOUNT;
        }

    }
}
