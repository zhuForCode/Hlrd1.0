package com.juhua.hangfen.bzrd.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.juhua.hangfen.bzrd.R;
import com.juhua.hangfen.bzrd.application.AppCache;
import com.juhua.hangfen.bzrd.constants.Constants;
import com.juhua.hangfen.bzrd.model.User;
import com.juhua.hangfen.bzrd.model.VersionInfo;
import com.juhua.hangfen.bzrd.sharedpref.TinyDB;
import com.juhua.hangfen.bzrd.util.ToastUtils;
import com.juhua.hangfen.bzrd.webservice.GetModelAsync;
import com.juhua.hangfen.bzrd.webservice.KsoapHelpler;
import com.juhua.hangfen.bzrd.webservice.UpdateUI;

import java.util.LinkedHashMap;

/**
 * Created by JiaJin Kuai on 2017/1/22.
 */

public class WelcomeActivity extends Activity {

    private Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 2秒后跳转到主界面
        mHandler.postDelayed(toMainActivityRunnable(), 2000);
        super.onCreate(savedInstanceState);
        setSystemBarTransparent();
        setContentView(R.layout.activity_splash);
       // getUserInfo();
       // getUserAndVerision();
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
    private Runnable toMainActivityRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                // 退出程序的时候不在经过欢迎界面
                startActivityForResult(intent, 0);
            }
        };
    }
    private void getUserAndVerision(){
        new GetModelAsync()
                .setModelType(Constants.MODEL_TYPE_MULTIPLE)
                .addKsoapHelpler("User", new KsoapHelpler<>(User.class)
                        .setMethodName("AppLoginForZjrd")
                        .addParams("UserName", "master")
                        .addParams("Password", "jhit361785")
                        .addParams("type", "Android")
                        .addParams("verify", Constants.VERIFY))
                .addKsoapHelpler("VersionInfo", new KsoapHelpler<>(VersionInfo.class)
                        .setMethodName("GetWebAppVersion")
                        .addParams("type", "Android")
                        .addParams("verify", Constants.VERIFY))
                .setUpdateUI(new UpdateUI() {
                    @Override
                    public void onResponse(Object obj) {
                        LinkedHashMap linkedHashMap = (LinkedHashMap) obj;
                        User user = (User) linkedHashMap.get("User");
                        VersionInfo versionInfo = (VersionInfo) linkedHashMap.get("VersionInfo");
                        TinyDB infoDB = new TinyDB(AppCache.getContext());
                        infoDB.putObject("User", user);
                        infoDB.putObject("VersionInfo", versionInfo);
                        ToastUtils.show(user.getName() + ":" + versionInfo.getDescription());

                    }
                })
                .execute();
    }
    // 展示欢迎界面的时候，不允许其他操作（例如按返回键退出）
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0){
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
