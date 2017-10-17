package com.juhua.hangfen.eedsrd.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juhua.hangfen.eedsrd.R;
import com.juhua.hangfen.eedsrd.application.AppCache;
import com.juhua.hangfen.eedsrd.tools.AppManager;
import com.juhua.hangfen.eedsrd.util.ScreenUtils;

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
    }

    protected void  createControl(){

    }

    protected void bindControl(){

    }



    protected void onDestroy() {
        super.onDestroy();
    }

    private void setSystemBarTransparent() {
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
