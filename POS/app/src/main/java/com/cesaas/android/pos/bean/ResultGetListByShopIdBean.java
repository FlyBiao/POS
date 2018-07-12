package com.cesaas.android.pos.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：按店铺查询粉丝列表
 * 创建日期：2016/10/10 15:36
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class ResultGetListByShopIdBean extends BaseBean{

    public ArrayList<FansListByShopIdBean> TModel;

    public class FansListByShopIdBean implements Serializable {

        private static final long serialVersionUID = 1L;
        public int COUNSELOR_ID;//顾问ID
        public String COUNSELOR_NAME;//顾问姓名
        public String FANS_ICON;//粉丝头像
        public String FANS_ID;//粉丝ID
        public String FANS_GRADE;//会员等级
        public int FANS_ISCANCEL;//是否取消关注，0：关注，1：取消关注
        public String FANS_MOBILE;//粉丝手机
        public String FANS_NICKNAME;//粉丝昵称
        public String FANS_OPENID;////微信粉丝ID
        public String FANS_POINT;//粉丝积分
        public String FANS_REMARK;//粉丝备注
        public String FANS_SEX;//粉丝性别
        public String FANS_SHOPID;////店铺ID，0：店员，1：店长
    }
}
