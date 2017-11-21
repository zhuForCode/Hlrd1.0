package com.juhua.hangfen.bzrd.util;

import android.view.View;

import com.juhua.hangfen.bzrd.enums.LoadStateEnum;


/**
 * Created by JiaJin Kuai on 2017/2/20.
 */

public class ViewUtils {

    public static void changeViewState(View loadSuccess, View loading, View loadFail, LoadStateEnum state) {
        switch (state) {
            case LOADING:
                loadSuccess.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);
                loadFail.setVisibility(View.GONE);
                break;
            case LOAD_SUCCESS:
                loadSuccess.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);
                loadFail.setVisibility(View.GONE);
                break;
            case LOAD_FAIL:
                loadSuccess.setVisibility(View.GONE);
                loading.setVisibility(View.GONE);
                loadFail.setVisibility(View.VISIBLE);
                break;
        }
    }

//    public boolean hasNavigationBar(Activity activity) {
//        DisplayMetrics dm = new DisplayMetrics();
//        Display display = activity.getWindowManager().getDefaultDisplay();
//        display.getMetrics(dm);
//        int screenWidth = dm.widthPixels;
//        int screenHeight = dm.heightPixels;
//        float density = dm.density;
//
//        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            display.getRealMetrics(realDisplayMetrics);
//        } else {
//            Class c;
//            try {
//                c = Class.forName("android.view.Display");
//                Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
//                method.invoke(display, realDisplayMetrics);
//            } catch (Exception e) {
//                realDisplayMetrics.setToDefaults();
//                e.printStackTrace();
//            }
//        }
//
//        int creenRealHeight = realDisplayMetrics.heightPixels;
//        int creenRealWidth = realDisplayMetrics.widthPixels;
//
//        float diagonalPixels = (float) Math.sqrt(Math.pow(screenWidth, 2) + Math.pow(screenHeight, 2));
//        float screenSize = (diagonalPixels / (160f * density)) * 1f;
//
//        Resources rs = activity.getResources();
//        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
//        boolean hasNavBarFun = true;
//        if (id > 0) {
//            hasNavBarFun = rs.getBoolean(id);
//        }
//        try {
//            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
//            Method m = systemPropertiesClass.getMethod("get", String.class);
//            String navBarOverride = (String)m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
//            if ("1".equals(navBarOverride)) {
//                hasNavBarFun = false;
//            } else if ("0".equals(navBarOverride)) {
//                hasNavBarFun = true;
//            }
//        } catch (Exception e) {
//            hasNavBarFun = false;
//        }
//        return hasNavBarFun;
//    }
//
//    public static boolean checkDeviceHasNavigationBar(Activity activity) {
//        DisplayMetrics dm = new DisplayMetrics();
//        Display display = activity.getWindowManager().getDefaultDisplay();
//        display.getMetrics(dm);
//        int screenWidth = dm.widthPixels;
//        int screenHeight = dm.heightPixels;
//        return (VH -screenHeight ) > 0;//screenRealHeight上面方法中有计算
//    }
}