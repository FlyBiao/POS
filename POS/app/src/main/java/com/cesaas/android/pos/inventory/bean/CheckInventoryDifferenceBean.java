package com.cesaas.android.pos.inventory.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Author FGB
 * Description
 * Created at 2017/8/28 16:53
 * Version 1.0
 */

public class CheckInventoryDifferenceBean implements Serializable {

    private int DiffNum;
    private int Num;
    private int StyleId;
    private String StyleName;
    private String StyleNo;
    private String ImageUrl;
    public List<Sku> Sku;

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public int getDiffNum() {
        return DiffNum;
    }

    public void setDiffNum(int diffNum) {
        DiffNum = diffNum;
    }

    public int getNum() {
        return Num;
    }

    public void setNum(int num) {
        Num = num;
    }

    public int getStyleId() {
        return StyleId;
    }

    public void setStyleId(int styleId) {
        StyleId = styleId;
    }

    public String getStyleName() {
        return StyleName;
    }

    public void setStyleName(String styleName) {
        StyleName = styleName;
    }

    public String getStyleNo() {
        return StyleNo;
    }

    public void setStyleNo(String styleNo) {
        StyleNo = styleNo;
    }

    public class Sku implements Serializable{

        /**
         * Code : 12331212
         * GoodsDiffNum : 0
         * GoodsNum : 4
         * Ontheway : 0
         * Sku1 : 黄
         * Sku2 : s
         * Stock : 0
         */

        private String Code;
        private int GoodsDiffNum;
        private int GoodsNum;
        private int Ontheway;
        private String Sku1;
        private String Sku2;
        private int Stock;

        public void setCode(String Code) {
            this.Code = Code;
        }

        public void setGoodsDiffNum(int GoodsDiffNum) {
            this.GoodsDiffNum = GoodsDiffNum;
        }

        public void setGoodsNum(int GoodsNum) {
            this.GoodsNum = GoodsNum;
        }

        public void setOntheway(int Ontheway) {
            this.Ontheway = Ontheway;
        }

        public void setSku1(String Sku1) {
            this.Sku1 = Sku1;
        }

        public void setSku2(String Sku2) {
            this.Sku2 = Sku2;
        }

        public void setStock(int Stock) {
            this.Stock = Stock;
        }

        public String getCode() {
            return Code;
        }

        public int getGoodsDiffNum() {
            return GoodsDiffNum;
        }

        public int getGoodsNum() {
            return GoodsNum;
        }

        public int getOntheway() {
            return Ontheway;
        }

        public String getSku1() {
            return Sku1;
        }

        public String getSku2() {
            return Sku2;
        }

        public int getStock() {
            return Stock;
        }
    }
}
