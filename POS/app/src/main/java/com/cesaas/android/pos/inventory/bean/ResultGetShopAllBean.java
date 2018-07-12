package com.cesaas.android.pos.inventory.bean;

import com.cesaas.android.pos.bean.BaseBean;

import java.io.Serializable;
import java.util.List;

/**
 * Author FGB
 * Description
 * Created at 2017/8/31 18:06
 * Version 1.0
 */

public class ResultGetShopAllBean extends BaseBean {

    public List<GetShopAllBean> TModel;

    public class GetShopAllBean implements Serializable{
        /**
         * AreaId : 2523
         * AreaName : 华南区
         * OrganizationId : 9
         * OrganizationName : 总公司
         * ShopId : 16178
         * ShopName : 东湖店
         */

        private int AreaId;
        private String AreaName;
        private int OrganizationId;
        private String OrganizationName;
        private int ShopId;
        private String ShopName;

        public void setAreaId(int AreaId) {
            this.AreaId = AreaId;
        }

        public void setAreaName(String AreaName) {
            this.AreaName = AreaName;
        }

        public void setOrganizationId(int OrganizationId) {
            this.OrganizationId = OrganizationId;
        }

        public void setOrganizationName(String OrganizationName) {
            this.OrganizationName = OrganizationName;
        }

        public void setShopId(int ShopId) {
            this.ShopId = ShopId;
        }

        public void setShopName(String ShopName) {
            this.ShopName = ShopName;
        }

        public int getAreaId() {
            return AreaId;
        }

        public String getAreaName() {
            return AreaName;
        }

        public int getOrganizationId() {
            return OrganizationId;
        }

        public String getOrganizationName() {
            return OrganizationName;
        }

        public int getShopId() {
            return ShopId;
        }

        public String getShopName() {
            return ShopName;
        }
    }


}
