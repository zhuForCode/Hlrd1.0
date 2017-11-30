package com.juhua.hangfen.bzrd.application;

import android.app.Application;
import android.content.Context;

import com.juhua.hangfen.bzrd.application.AppManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.https.HttpsUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by JiaJin Kuai on 2017/3/6.
 */

public class ThisApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppManager.init(this);
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);
    }

}
