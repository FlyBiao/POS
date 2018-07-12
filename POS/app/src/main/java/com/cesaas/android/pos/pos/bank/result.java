package com.cesaas.android.pos.pos.bank;

import java.io.Serializable;

/**
 * Author FGB
 * Description
 * Created at 2017/12/22 16:09
 * Version 1.0
 */

public class result implements Serializable {

    /**
     * bank : 中国建设银行
     * bankcard : 4367455021552091
     * cardtype : 贷记卡
     * info :
     * kefu : 400-820-0588
     * logo : http://apiserver.qiniudn.com/jianshe.png
     * nature : 龙卡国际普通卡VISA
     */

    private String bank;
    private String bankcard;
    private String cardtype;
    private String info;
    private String kefu;
    private String logo;
    private String nature;

    public void setBank(String bank) {
        this.bank = bank;
    }

    public void setBankcard(String bankcard) {
        this.bankcard = bankcard;
    }

    public void setCardtype(String cardtype) {
        this.cardtype = cardtype;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setKefu(String kefu) {
        this.kefu = kefu;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public String getBank() {
        return bank;
    }

    public String getBankcard() {
        return bankcard;
    }

    public String getCardtype() {
        return cardtype;
    }

    public String getInfo() {
        return info;
    }

    public String getKefu() {
        return kefu;
    }

    public String getLogo() {
        return logo;
    }

    public String getNature() {
        return nature;
    }
}
