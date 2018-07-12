package com.cesaas.android.pos.inventory.bean;

import java.io.Serializable;

/**
 * Author FGB
 * Description 单个货架信息Bean
 * Created at 2017/8/31 10:39
 * Version 1.0
 */

public class ShelfDetailsBean implements Serializable {

    private String Code;
    private int StyleId;
    private String StyleName;
    private int Num;


    public class Sku implements Serializable{
        private String Sku1;
        private String Sku2;
        private String Sku3;
        private int GoodsNum;

        public String getSku1() {
            return Sku1;
        }

        public void setSku1(String sku1) {
            Sku1 = sku1;
        }

        public String getSku2() {
            return Sku2;
        }

        public void setSku2(String sku2) {
            Sku2 = sku2;
        }

        public String getSku3() {
            return Sku3;
        }

        public void setSku3(String sku3) {
            Sku3 = sku3;
        }

        public int getGoodsNum() {
            return GoodsNum;
        }

        public void setGoodsNum(int goodsNum) {
            GoodsNum = goodsNum;
        }
    }
}
