package com.juhua.hangfen.eedsrd.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.juhua.hangfen.eedsrd.R;
import com.juhua.hangfen.eedsrd.application.AppCache;
import com.juhua.hangfen.eedsrd.constants.Constants;
import com.juhua.hangfen.eedsrd.model.UserInfo;
import com.juhua.hangfen.eedsrd.model.VersionInfo;
import com.juhua.hangfen.eedsrd.sharedpref.TinyDB;
import com.juhua.hangfen.eedsrd.util.ToastUtils;
import com.juhua.hangfen.eedsrd.webservice.GetModelAsync;
import com.juhua.hangfen.eedsrd.webservice.KsoapAsync;
import com.juhua.hangfen.eedsrd.webservice.KsoapHelpler;
import com.juhua.hangfen.eedsrd.webservice.UpdateUI;

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
                .addKsoapHelpler("UserInfo", new KsoapHelpler<>(UserInfo.class)
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
                        UserInfo userInfo = (UserInfo) linkedHashMap.get("UserInfo");
                        VersionInfo versionInfo = (VersionInfo) linkedHashMap.get("VersionInfo");
                        if(userInfo.getErrorCode() != 0 || userInfo.getRESPONSECODE() != 0){
                            ToastUtils.show(userInfo.getErrorDesc() + userInfo.getRESPONSEDESC());
                        }else{
                            TinyDB infoDB = new TinyDB(AppCache.getContext());
                            infoDB.putObject("UserInfo", userInfo);
                            infoDB.putObject("VersionInfo", versionInfo);
                            ToastUtils.show(userInfo.get姓名() + ":" + versionInfo.getDescription());
                        }

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
