package com.juhua.hangfen.eedsrd.application;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.juhua.hangfen.eedsrd.model.User;
import com.juhua.hangfen.eedsrd.sharedpref.Preferences;
import com.juhua.hangfen.eedsrd.sharedpref.TinyDB;
import com.juhua.hangfen.eedsrd.tools.AppException;
import com.juhua.hangfen.eedsrd.tools.CryptoTools;
import com.juhua.hangfen.eedsrd.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by JiaJin Kuai on 2017/3/22.
 */

public class AppCache {
    private Context mContext;
    private static boolean isLogged;
    private static String verify;
    private final List<Activity> mActivityStack = new ArrayList<>();
    private AppCache() {
    }

    public static boolean isLogged() {
        return isLogged;
    }

    public static void setIsLogged(Boolean bool){
        isLogged = bool;
    }

    public static String getVerify() {
        return verify;
    }

    public static void setVerify(String verify) {
        AppCache.verify = verify;
    }

    private static class SingletonHolder {
        private static AppCache sAppCache = new AppCache();
    }

    private static AppCache getInstance() {
        return SingletonHolder.sAppCache;
    }

    public static void init(Context context) {
        if (getContext() != null) {
            return;
        }
        getInstance().onInit(context);
    }

    private void onInit(Context context) {
        mContext = context.getApplicationContext();
        try{
            CryptoTools cryptoTools = new CryptoTools();
            verify = cryptoTools.returnVerify();
        } catch (Exception e){
            e.printStackTrace();
        }
        TinyDB userDB = new TinyDB(mContext);
        User user;
        try {
            user = (User) userDB.getObject("user", User.class);
            if(user.getId() != null){
                isLogged = true;
            }else{
                isLogged = false;
            }
        }catch (Exception e){
            isLogged = false;
        }
        ToastUtils.init(mContext);
        Preferences.init(mContext);
        CrashHandler.getInstance().init();
    }
    public static void updateNightMode(boolean on) {
        Resources resources = getContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.uiMode &= ~Configuration.UI_MODE_NIGHT_MASK;
        config.uiMode |= on ? Configuration.UI_MODE_NIGHT_YES : Configuration.UI_MODE_NIGHT_NO;
        resources.updateConfiguration(config, dm);
    }
    public static Context getContext() {
        return getInstance().mContext;
    }

    public static void addToStack(Activity activity) {
        getInstance().mActivityStack.add(activity);
    }

    public static void removeFromStack(Activity activity) {
        getInstance().mActivityStack.remove(activity);
    }

    public static void clearStack() {
        List<Activity> activityStack = getInstance().mActivityStack;
        for (int i = activityStack.size() - 1; i >= 0; i--) {
            Activity activity = activityStack.get(i);
            activityStack.remove(activity);
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}
