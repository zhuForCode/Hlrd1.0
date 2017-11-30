package com.juhua.hangfen.bzrd.application;

/**
 * Created by congj on 2017/11/22.
 */

import java.util.Stack;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import com.juhua.hangfen.bzrd.model.User;
import com.juhua.hangfen.bzrd.sharedpref.Preferences;
import com.juhua.hangfen.bzrd.util.ToastUtils;

/**
 * 应用程序Activity管理类：用于Activity管理和应用程序退出
 * @version 1.0
 * @created 2012-3-21
 */
public class AppManager {

    private static Stack<Activity> activityStack;
    private static AppManager instance;
    private User user;

    private Context mContext;
    private AppManager(){}
    /**
     * 单一实例
     */
    public static AppManager getAppManager(){
        if(instance==null){
            instance=new AppManager();
        }
        return instance;
    }

    public static void init(Context context) {
        if (getContext() != null) {
            return;
        }
        getAppManager().onInit(context);
    }

    public static Context getContext() {
        return getAppManager().mContext;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private void onInit(Context context) {
        mContext = context.getApplicationContext();

        ToastUtils.init(mContext);
        Preferences.init(mContext);
        CrashHandler.getInstance().init();
    }
    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity){
        if(activityStack==null){
            activityStack=new Stack<Activity>();
        }
        activityStack.add(activity);
    }
    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity(){
        Activity activity=activityStack.lastElement();
        return activity;
    }
    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity(){
        Activity activity=activityStack.lastElement();
        finishActivity(activity);
    }
    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity){
        if(activity!=null){
            activityStack.remove(activity);
            activity.finish();
            activity=null;
        }
    }
    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls){
        for (Activity activity : activityStack) {
            if(activity.getClass().equals(cls) ){
                finishActivity(activity);
            }
        }
    }
    /**
     * 结束所有Activity
     */
    public void finishAllActivity(){
        for (int i = 0, size = activityStack.size(); i < size; i++){
            if (null != activityStack.get(i)){
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }
    /**
     * 结束相应数目的Activity
     */
    public void finishCountActivity(int count){
        for (int i = 0; i < count; i++){
            if (null != activityStack.get(activityStack.size()-1-i)){
                activityStack.get(activityStack.size()-1).finish();
                //    Activity activity = activityStack.get(activityStack.size()-1-i);
                activityStack.remove(activityStack.size()-1);
            }
        }
        // activityStack.clear();
    }
    /**
     * 退出应用程序
     */
    public void AppExit(Context context) {
        try {
            finishAllActivity();
            ActivityManager activityMgr= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.restartPackage(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {	}
    }
}