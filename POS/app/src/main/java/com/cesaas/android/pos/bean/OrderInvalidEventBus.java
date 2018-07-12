package com.cesaas.android.pos.bean;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：
 * 创建日期：2016/12/27 09:49
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class OrderInvalidEventBus {

    private boolean isSuccess = false;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
