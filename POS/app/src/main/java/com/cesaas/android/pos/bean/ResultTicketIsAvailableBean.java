package com.cesaas.android.pos.bean;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：验证优惠券是否可以使用
 * 创建日期：2016/12/27 14:32
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class ResultTicketIsAvailableBean extends BaseBean{

    public TicketIsAvailableBean TModel;

    public class TicketIsAvailableBean{
        private boolean IsUse;//是否可以使用 true：可用，false：不可用

        public boolean isUse() {
            return IsUse;
        }

        public void setUse(boolean use) {
            IsUse = use;
        }
    }
}
