package com.juhua.hangfen.bzrd.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by JiaJin Kuai on 2017/3/22.
 */

public class ToastUtils {
    private static Context sContext;
    private static Toast sToast;

    public static void init(Context context) {
        sContext = context.getApplicationContext();
    }

    public static void show(int resId) {
        show(sContext.getString(resId));
    }

    public static void show(String text) {
        if (sToast == null) {
            sToast = Toast.makeText(sContext, text, Toast.LENGTH_SHORT);
        } else {
            sToast.setText(text);
        }
        sToast.show();
    }

    public static void showLong(int resId) {
        showLong(sContext.getString(resId));
    }

    public static void showLong(String text) {
        if (sToast == null) {
            sToast = Toast.makeText(sContext, text, Toast.LENGTH_LONG);
        } else {
            sToast.setText(text);
        }
        sToast.show();
    }
}
