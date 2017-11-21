package com.juhua.hangfen.bzrd.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.juhua.hangfen.bzrd.R;
import com.juhua.hangfen.bzrd.tools.AppManager;

/**
 * Created by JiaJin Kuai on 2017/2/19.
 */

public class QRCodeActivity extends Activity{
    private Button btnBack;
    private TextView aboutTxv;
    private TextView versionTxv;
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        AppManager.getAppManager().addActivity(this);
        init();
        initData();

    }
    protected void init(){
        btnBack = (Button)findViewById(R.id.back_btn);
        aboutTxv = (TextView) findViewById(R.id.about_title);
        versionTxv = (TextView)findViewById(R.id.textview_version);
    }
    protected void initData(){
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppManager.getAppManager().finishActivity();
            }
        });
        aboutTxv.setText("二维码");
        if(getIntent().getExtras().getString("appVersion") != null){
            versionTxv.setText(getIntent().getExtras().getString("appVersion"));
        }
    }
}
