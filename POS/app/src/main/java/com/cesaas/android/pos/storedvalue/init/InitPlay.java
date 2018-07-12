package com.cesaas.android.pos.storedvalue.init;

import android.app.Activity;
import android.widget.EditText;

import com.cesaas.android.pos.storedvalue.ui.PaymentTypeActivity;
import com.cesaas.android.pos.utils.AbDateUtil;
import com.cesaas.android.pos.utils.RandomUtils;
import com.wangpos.pay.UnionPay.PosConfig;
import com.wangpos.poscore.PosCore;
import com.wangpos.poscore.impl.PosCoreFactory;

import java.util.HashMap;

/**
 * Author FGB
 * Description
 * Created at 2017/7/21 9:51
 * Version 1.0
 */

public class InitPlay {



    public static void showMsg(final String msg, Activity activity, final EditText et) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                et.setText("");
                et.setTextSize(20);
                et.setText(msg);
            }
        });
    }

}
