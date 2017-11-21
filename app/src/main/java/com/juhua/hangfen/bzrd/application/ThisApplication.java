package com.juhua.hangfen.bzrd.application;

import android.app.Application;
import android.content.Context;

import com.juhua.hangfen.bzrd.tools.AppManager;

/**
 * Created by JiaJin Kuai on 2017/3/6.
 */

public class ThisApplication extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        AppManager.init(this);
    }

    public static Context getContext(){
        return context;
    }
}
