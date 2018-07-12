package com.cesaas.android.pos.pos.bank;

/**
 * Author FGB
 * Description
 * Created at 2017/12/22 16:08
 * Version 1.0
 */

public class ResultBankInfoBean {

    private String reason;
    private int error_code;

    private result result;

    public com.cesaas.android.pos.pos.bank.result getResult() {
        return result;
    }

    public void setResult(com.cesaas.android.pos.pos.bank.result result) {
        this.result = result;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }
}
