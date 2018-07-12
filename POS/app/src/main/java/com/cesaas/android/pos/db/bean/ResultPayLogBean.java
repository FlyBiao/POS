package com.cesaas.android.pos.db.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Author FGB
 * Description
 * Created at 2017/12/21 20:34
 * Version 1.0
 */

public class ResultPayLogBean {

    private List<PosPayBean> posPayBeanArrayList=new ArrayList<>();

    public List<PosPayBean> getPosPayBeanArrayList() {
        return posPayBeanArrayList;
    }

    public void setPosPayBeanArrayList(List<PosPayBean> posPayBeanArrayList) {
        this.posPayBeanArrayList = posPayBeanArrayList;
    }
}
