package com.juhua.hangfen.eedsrd.application;

import android.app.Application;
import android.content.Context;

import com.juhua.hangfen.eedsrd.application.AppCache;
import com.juhua.hangfen.eedsrd.tools.AppException;

import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

/**
 * Created by JiaJin Kuai on 2017/3/6.
 */

public class ThisApplication extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        AppCache.init(this);
    }

    public static Context getContext(){
        return context;
    }
}
