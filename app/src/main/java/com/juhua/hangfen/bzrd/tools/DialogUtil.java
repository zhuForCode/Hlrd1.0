package com.juhua.hangfen.bzrd.tools;

/**
 * Created by JiaJin Kuai on 2016/9/23.
 */
/**
 *
 */
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;

import com.juhua.hangfen.bzrd.R;


/**
 * Description:显示指定组件的对话框,并跳转至指定的Activity
 * @author  maoyun0903@163.com
 * @version  1.0
 */
public class DialogUtil
{
    // 定义一个显示消息的对话框
    public static void showDialog(final Context ctx
            , String msg , boolean goHome)
    {
        // 创建一个AlertDialog.Builder对象
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setMessage(msg).setCancelable(false);
                builder.setPositiveButton("确定", null);
            builder.setNegativeButton("取消", null);
        builder.show();
    }
    // 定义一个显示指定组件的对话框
    public static void showDialog(Context ctx , View view)
    {
        new AlertDialog.Builder(ctx)
                .setView(view).setCancelable(false)
                .setPositiveButton("确定", null)
                .create()
                .show();
    }

    public static void showCustomDialog(final Context context, String title, String msg, int resId, DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg).setCancelable(false);
        builder.setIcon(resId);
        builder.setPositiveButton("确定", listener);
        builder.setNegativeButton("取消", null);
        AlertDialog ad = builder.create();
        ad.show();
        ad.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
    }

    public static void showErrorDialog(final Context context, String title, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setIcon(R.drawable.ic_error_outline_black);
        builder.setMessage(msg).setCancelable(false);
        builder.setPositiveButton("确定", null);
        builder.setNegativeButton("取消", null);
        AlertDialog ad = builder.create();
        ad.show();
        ad.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
        ad.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.GRAY);
    }
}
