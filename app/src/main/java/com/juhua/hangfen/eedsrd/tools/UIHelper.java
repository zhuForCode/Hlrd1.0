package com.juhua.hangfen.eedsrd.tools;

/**
 * Created by JiaJin Kuai on 2016/9/29.
 */
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Toast;

import com.juhua.hangfen.eedsrd.activity.LoginActivity;


/**
 * 应用程序UI工具包：封装UI相关的一些操作
 *
 * @version 1.0
 * @created 2012-3-21
 */
public class UIHelper {

    public final static int LISTVIEW_ACTION_INIT = 0x01;
    public final static int LISTVIEW_ACTION_REFRESH = 0x02;
    public final static int LISTVIEW_ACTION_SCROLL = 0x03;
    public final static int LISTVIEW_ACTION_CHANGE_CATALOG = 0x04;

    public final static int LISTVIEW_DATA_MORE = 0x01;
    public final static int LISTVIEW_DATA_LOADING = 0x02;
    public final static int LISTVIEW_DATA_FULL = 0x03;
    public final static int LISTVIEW_DATA_EMPTY = 0x04;

    public final static int LISTVIEW_DATATYPE_NEWS = 0x01;
    public final static int LISTVIEW_DATATYPE_BLOG = 0x02;
    public final static int LISTVIEW_DATATYPE_POST = 0x03;
    public final static int LISTVIEW_DATATYPE_TWEET = 0x04;
    public final static int LISTVIEW_DATATYPE_ACTIVE = 0x05;
    public final static int LISTVIEW_DATATYPE_MESSAGE = 0x06;
    public final static int LISTVIEW_DATATYPE_COMMENT = 0x07;

    public final static int REQUEST_CODE_FOR_RESULT = 0x01;
    public final static int REQUEST_CODE_FOR_REPLY = 0x02;

    /** 表情图片匹配 */
    private static Pattern facePattern = Pattern
            .compile("\\[{1}([0-9]\\d*)\\]{1}");

    /** 全局web样式 */
    public final static String WEB_STYLE = "<style>* {font-size:16px;line-height:20px;} p {color:#333;} a {color:#3E62A6;} img {max-width:310px;} "
            + "img.alignleft {float:left;max-width:120px;margin:0 10px 5px 0;border:1px solid #ccc;background:#fff;padding:2px;} "
            + "pre {font-size:9pt;line-height:12pt;font-family:Courier New,Arial;border:1px solid #ddd;border-left:5px solid #6CE26C;background:#f6f6f6;padding:5px;} "
            + "a.tag {font-size:15px;text-decoration:none;background-color:#bbd6f3;border-bottom:2px solid #3E6D8E;border-right:2px solid #7F9FB6;color:#284a7b;margin:2px 2px 2px 0;padding:2px 4px;white-space:nowrap;}</style>";

    /**
     * 显示首页
     *
     * @param activity
     */
    public static void showHome(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     * 调用系统安装了的应用分享
     *
     * @param context
     * @param title
     * @param url
     */
    public static void showShareMore(Activity context, final String title,
                                     final String url) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享：" + title);
        intent.putExtra(Intent.EXTRA_TEXT, title + " " + url);
        context.startActivity(Intent.createChooser(intent, "选择分享"));
    }

    /**
     * 打开浏览器
     *
     * @param context
     * @param url
     */
    public static void openBrowser(Context context, String url) {
        try {
            Uri uri = Uri.parse(url);
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(it);
        } catch (Exception e) {
            e.printStackTrace();
            ToastMessage(context, "无法浏览此网页", 500);
        }
    }

    /**
     * 组合动态的回复文本
     *
     * @param name
     * @param body
     * @return
     */
    public static SpannableString parseActiveReply(String name, String body) {
        SpannableString sp = new SpannableString(name + "：" + body);
        // 设置用户名字体加粗、高亮
        sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0,
                name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new ForegroundColorSpan(Color.parseColor("#0e5986")), 0,
                name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sp;
    }



    /**
     * 组合消息文本
     *
     * @param name
     * @param body
     * @return
     */
    public static SpannableString parseMessageSpan(String name, String body,
                                                   String action) {
        SpannableString sp = null;
        int start = 0;
        int end = 0;
        if (StringUtils.isEmpty(action)) {
            sp = new SpannableString(name + "：" + body);
            end = name.length();
        } else {
            sp = new SpannableString(action + name + "：" + body);
            start = action.length();
            end = start + name.length();
        }
        // 设置用户名字体加粗、高亮
        sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new ForegroundColorSpan(Color.parseColor("#0e5986")), start,
                end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sp;
    }

    /**
     * 组合回复引用文本
     *
     * @param name
     * @param body
     * @return
     */
    public static SpannableString parseQuoteSpan(String name, String body) {
        SpannableString sp = new SpannableString("回复：" + name + "\n" + body);
        // 设置用户名字体加粗、高亮
        sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 3,
                3 + name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new ForegroundColorSpan(Color.parseColor("#0e5986")), 3,
                3 + name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sp;
    }

    /**
     * 弹出Toast消息
     *
     * @param msg
     */
    public static void ToastMessage(Context cont, String msg) {
        Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
    }

    public static void ToastMessage(Context cont, int msg) {
        Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
    }

    public static void ToastMessage(Context cont, String msg, int time) {
        Toast.makeText(cont, msg, time).show();
    }

    /**
     * 点击返回监听事件
     *
     * @param activity
     * @return
     */
    public static View.OnClickListener finish(final Activity activity) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                activity.finish();
            }
        };
    }

    /**
     * 主界面
     *
     * @param context
     */
    public static void showMain(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    public static void showActivity(Context context, Class cls) {
        // TODO Auto-generated method stub
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);

    }

    /**
     * 跳转首页
     *
     * @param context
     */
    public static void showIndex(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    /**
     * 清除app缓存
     *
     * @param activity
     */
    public static void clearAppCache(Activity activity) {
        final AppContext ac = (AppContext) activity.getApplication();
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    ToastMessage(ac, "缓存清除成功");
                } else {
                    ToastMessage(ac, "缓存清除失败");
                }
            }
        };
        new Thread() {
            public void run() {
                Message msg = new Message();
                try {
                    ac.clearAppCache();
                    msg.what = 1;
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = -1;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 退出程序
     *
     * @param cont
     */
    public static void Exit2(final Context cont) {
        AppManager.getAppManager().AppExit(cont);
    }

    /**
     * 退出程序
     *
     * @param cont
     */
    public static void Exit(final Context cont) {
        AlertDialog.Builder builder = new AlertDialog.Builder(cont);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle("退出");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // 退出
                AppManager.getAppManager().AppExit(cont);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

}
