package com.cesaas.android.pos.inventory.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Author FGB
 * Description 获取单个货架信息Bean
 * Created at 2017/8/31 14:46
 * Version 1.0
 */

public class GetOneInfoBean implements Serializable {
    private String StyleNo;
    private String StyleName;
    private int StyleId;
    private int Num;
    private String ImageUrl;

    public List<Sku> Sku;


    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getStyleNo() {
        return StyleNo;
    }

    public void setStyleNo(String styleNo) {
        StyleNo = styleNo;
    }

    public String getStyleName() {
        return StyleName;
    }

    public void setStyleName(String styleName) {
        StyleName = styleName;
    }

    public int getStyleId() {
        return StyleId;
    }

    public void setStyleId(int styleId) {
        StyleId = styleId;
    }

    public int getNum() {
        return Num;
    }

    public void setNum(int num) {
        Num = num;
    }

    public class Sku implements Serializable{
        private String Sku1;
        private String Sku2;
        private String Sku3;
        private int GoodsNum;
        private String Code;

        public String getCode() {
            return Code;
        }

        public void setCode(String code) {
            Code = code;
        }

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
