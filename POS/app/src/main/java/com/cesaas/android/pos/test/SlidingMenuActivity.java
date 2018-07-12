package com.cesaas.android.pos.test;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.cesaas.android.pos.R;
import com.cesaas.android.pos.test.utils.SlidingMenu;

public class SlidingMenuActivity extends Activity {

    private SlidingMenu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sliding_menu);
        mMenu = (SlidingMenu) findViewById(R.id.id_menu);
    }

    public void toggleMenu(View view)
    {
        mMenu.toggle();
    }
}
