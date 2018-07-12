package com.cesaas.android.pos.rongcloud.bean;

/**
 * Author FGB
 * Description
 * Created at 2017/9/7 10:52
 * Version 1.0
 */

public class ReceiveMessageBean {

    /**
     * content : hello
     * extra : helloExtra
     */

    private String content;
    private String extra;

    public void setContent(String content) {
        this.content = content;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getContent() {
        return content;
    }

    public String getExtra() {
        return extra;
    }
}
