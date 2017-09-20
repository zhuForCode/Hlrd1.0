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

import com.juhua.hangfen.eedsrd.R;
import com.juhua.hangfen.eedsrd.application.AppCache;
import com.juhua.hangfen.eedsrd.util.ScreenUtils;

/**
 * Created by congj on 2017/9/14.
 */

public class BaseActivity extends AppCompatActivity {
    protected Toolbar toolbar;

    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        AppCache.addToStack(this);

    }

    protected void onDestroy() {
        AppCache.removeFromStack(this);
        super.onDestroy();
        Log.i("J.Kuai", "onDestroy:" + getClass().getSimpleName());
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
