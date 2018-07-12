package com.cesaas.android.pos.test;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cesaas.android.pos.R;

import java.util.ArrayList;

import cn.weipass.pos.sdk.Scanner;
import cn.weipass.pos.sdk.impl.WeiposImpl;

public class ScanerActivity extends Activity implements View.OnClickListener{

    private Scanner scanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scaner);

        TextView topTitle = (TextView) findViewById(R.id.page_top_title);
        topTitle.setText("调用扫描二维码和条码");
        findViewById(R.id.btn_return).setOnClickListener(this);

        findViewById(R.id.btn_scaner_one).setOnClickListener(this);
        findViewById(R.id.btn_scaner_two).setOnClickListener(this);

        // 调用扫码二维码的activity需要在Manifest文件中配置android:exported="true"属性
        //获取sdk扫描对象
        scanner = WeiposImpl.as().openScanner();
    }


    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_return:
                onBackPressed();
                break;
            case R.id.btn_scaner_one:
                // 调用扫码二维码的activity需要在Manifest文件中配置android:exported="true"属性
                if (scanner==null) {
                    Toast.makeText(ScanerActivity.this, "SDK扫描对象为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                /**
                 * 扫码类型，二维码
                 * TYPE_QR = 1;
                 *
                 * 扫描类型，条码
                 *  TYPE_BAR = 2;
                 */
                scanner.scan(Scanner.TYPE_QR, new Scanner.OnResultListener() {

                    @Override
                    public void onResult(int what, String in) {
                        // TODO Auto-generated method stub

                        final String info = in;
                        // 回调函数中不能做UI操作，所以可以使用runOnUiThread函数来包装一下代码块
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Log.i("test","扫码结果"+ info);

                            }
                        });
                    }
                });
                break;
            case R.id.btn_scaner_two:
                // 调用扫码二维码的activity需要在Manifest文件中配置android:exported="true"属性
                if (scanner==null) {
                    Toast.makeText(ScanerActivity.this, "SDK扫描对象为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                /**
                 * 扫码类型，二维码
                 * TYPE_QR = 1;
                 *
                 * 扫描类型，条码
                 *  TYPE_BAR = 2;
                 */
                scanner.scan(Scanner.TYPE_BAR, new Scanner.OnResultListener() {

                    @Override
                    public void onResult(int what, String in) {
                        // TODO Auto-generated method stub

                        final String info = in;
                        // 回调函数中不能做UI操作，所以可以使用runOnUiThread函数来包装一下代码块
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Log.i("test","扫码结果"+ info);
                            }
                        });
                    }
                });
                break;
        }
    }
}
