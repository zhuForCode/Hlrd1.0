package com.juhua.hangfen.bzrd.activity;

import android.Manifest;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.juhua.hangfen.bzrd.R;
import com.juhua.hangfen.bzrd.application.AppManager;
import com.juhua.hangfen.bzrd.util.PermissionReq;
import com.juhua.hangfen.bzrd.util.ToastUtils;

/**
 * Created by congj on 2017/9/14.
 */

public class BaseActivity extends AppCompatActivity {
    protected Button backButton;
    protected TextView titleTv;
    protected View.OnClickListener backListener;
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        AppManager.getAppManager().addActivity(this);
        backListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppManager.getAppManager().finishActivity();
            }
        };
        PermissionReq.with(this)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .result(new PermissionReq.Result() {
                    @Override
                    public void onGranted() {

                    }
                    @Override
                    public void onDenied() {
                        ToastUtils.show(R.string.no_permission_storage);
                    }
                })
                .request();
    }

    protected void  createControl(){

    }

    protected void bindControl(){

    }



    protected void onDestroy() {
        super.onDestroy();
    }

    protected void setSystemBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // LOLLIPOP解决方案
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // KITKAT解决方案
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

}
