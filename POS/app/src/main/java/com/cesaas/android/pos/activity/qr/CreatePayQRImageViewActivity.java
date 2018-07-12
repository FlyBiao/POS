package com.cesaas.android.pos.activity.qr;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.base.BaseActivity;
import com.cesaas.android.pos.utils.CreateQRImageUtils;
import com.cesaas.android.pos.utils.Skip;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：在线下单二维码页面
 * 创建日期：2016/10/10 21:50
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class CreatePayQRImageViewActivity extends BaseActivity {

    private ImageView mCreateView;
    private LinearLayout ll_back_pos_pay;
    private String orderNo;

    CreateQRImageUtils createQRImageUtils=new CreateQRImageUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pay_qrimage_view);

        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            orderNo=bundle.getString("orderNo");
        }

        mCreateView=(ImageView) findViewById(R.id.iv_qr_result);
        ll_back_pos_pay=(LinearLayout) findViewById(R.id.ll_back_pos_pay);
        mCreateView.setImageBitmap(createQRImageUtils.createQRImage(orderNo, 750, 750));

        ll_back_pos_pay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Skip.mBack(mActivity);

            }
        });
    }
}
