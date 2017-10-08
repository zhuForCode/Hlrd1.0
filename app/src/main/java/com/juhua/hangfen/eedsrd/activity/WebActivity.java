package com.juhua.hangfen.eedsrd.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juhua.hangfen.eedsrd.R;
import com.juhua.hangfen.eedsrd.application.AppCache;
import com.juhua.hangfen.eedsrd.constants.Constants;
import com.juhua.hangfen.eedsrd.tools.AppManager;
import com.juhua.hangfen.eedsrd.tools.FileUtils;
import com.juhua.hangfen.eedsrd.util.ToastUtils;
import com.juhua.hangfen.eedsrd.widget.MAndroid;

import org.apache.http.HttpStatus;

import java.io.File;

/**
 * Created by congj on 2017/9/28.
 */

public class WebActivity extends BaseActivity {
    protected WebView webView;
    protected LinearLayout actionbarLL;
    protected View.OnClickListener historyPageListener;

    private ValueCallback<Uri> mUploadMessageSingle;
    private ValueCallback<Uri[]> mUploadMessage;
    private final static int FILECHOOSER_RESULTCODE=1;

    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        createControl();
        bindControl();

    }

    @SuppressLint("SetJavaScriptEnabled")
    protected void createControl(){
        historyPageListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(webView.canGoBack()){
                    webView.goBack();
                }else{
                    AppManager.getAppManager().finishActivity();
                }
            }
        };
        titleTv = (TextView) findViewById(R.id.title_tv);
        backButton = (Button)findViewById(R.id.back_button);
        actionbarLL = (LinearLayout)findViewById(R.id.mAction_bar);

        webView = (WebView) findViewById(R.id.web);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        String cacheDirPath = getFilesDir().getAbsolutePath()+"/webCache";
        webView.getSettings().setDatabasePath(cacheDirPath);
        webView.getSettings().setAppCachePath(cacheDirPath);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setLayoutAlgorithm ( WebSettings.LayoutAlgorithm.SINGLE_COLUMN );
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.addJavascriptInterface(new MAndroid(), "MAndroid");
    }

    protected  void bindControl(){
        titleTv.setText(getIntent().getExtras().getString("actionName"));
        backButton.setOnClickListener(historyPageListener);
        loadURL();
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(url.contains("nav=hide")){
                    actionbarLL.setVisibility(View.GONE);
                }
                String formData = getIntent().getStringExtra("FormData");
                if(formData != null){
                    webView.loadUrl("javascript: prepareData('" + formData + "');");
                }
            }
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);// 使用当前WebView处理跳转
                return true;
            }
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
                handler.proceed();
            }
        });
        webView.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                Log.d("title", title);
                if(title.contains("#")){
                    String[] titleArray = title.split("#");
                    titleTv.setText(titleArray[0] + "\n" + titleArray[1]);
                }else{
                    titleTv.setText(title);
                }

            }
            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> valueCallback) {
                mUploadMessageSingle= valueCallback;
                openFileChooserActivity();
            }

            // For Android  >= 3.0
            public void openFileChooser(ValueCallback valueCallback, String acceptType) {
                mUploadMessageSingle= valueCallback;
                openFileChooserActivity();
            }
            //For Android  >= 4.1
            public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
                mUploadMessageSingle = valueCallback;
                openFileChooserActivity();
            }
            private void openFileChooserActivity() {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
            }

            //Android 5.0+支持调用文件选择器
            @Override
            @SuppressLint("NewApi")
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                if (mUploadMessage != null) {
                    mUploadMessage.onReceiveValue(null);
                }

                mUploadMessage = filePathCallback;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_DEFAULT);
                if (fileChooserParams != null && fileChooserParams.getAcceptTypes() != null
                        && fileChooserParams.getAcceptTypes().length > 0) {
                    if(!fileChooserParams.getAcceptTypes()[0].equals("")){
                        String[] mimeArray = fileChooserParams.getAcceptTypes()[0].split(",");
                        for (String mime:
                             mimeArray) {

                        }
                        i.setType("*/*");
                    }else{
                        i.setType("*/*");
                    }
                } else {
                    i.setType("*/*");
                }
                try {
                    startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
                }catch (Exception e){
                    ToastUtils.show("未安装相关程序");
                    // Toast.makeText(MainActivity.this, "未安装相关程序", Toast.LENGTH_SHORT).show();
                }
                return true;
            }

            /**
             * 覆盖默认的window.alert展示界面，避免title里显示为“：来自file:////”
             */
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setIcon(getResources().getDrawable(R.drawable.ic_error_outline_black));
                builder.setTitle("提示")
                        .setMessage(message)
                        .setPositiveButton("确定", null);

                // 不需要绑定按键事件
                // 屏蔽keycode等于84之类的按键
                builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        Log.v("onJsAlert", "keyCode==" + keyCode + "event="+ event);
                        return true;
                    }
                });
                // 禁止响应按back键的事件
                builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                dialog.show();
                result.confirm();// 因为没有绑定事件，需要强行confirm,否则页面会变黑显示不了内容。
                return true;
            }
            public boolean onJsConfirm(WebView view, String url, String message,
                                       final JsResult result) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setIcon(getResources().getDrawable(R.drawable.ic_error_outline_black));
                builder.setTitle("提示：")
                        .setMessage(message)
                        .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {
                                result.confirm();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                result.cancel();
                            }
                        });
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        result.cancel();
                    }
                });

                // 屏蔽keycode等于84之类的按键，避免按键后导致对话框消息而页面无法再弹出对话框的问题
                builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode,KeyEvent event) {
                        Log.v("onJsConfirm", "keyCode==" + keyCode + "event="+ event);
                        return true;
                    }
                });
                // 禁止响应按back键的事件
                // builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.common_dark_shadow));
                return true;
            }

        });

        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }});
    }

    public void loadURL(){
        StringBuilder builder = new StringBuilder(Constants.DOMAIN_NAME);
        try {
            if(getIntent().getExtras().get("Token") == null){
                webView.loadUrl("file:///android_asset/error.html");//加载错误页面
            }else{
                builder.append("LzptApp/Transfer.aspx?token=");
                builder.append(getIntent().getExtras().getString("Token"));
                builder.append("&actionUrl=");
                builder.append(getIntent().getExtras().getString("actionUrl"));
                webView.loadUrl(builder.toString());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy(){
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
        Log.i("kjj", "onDestroy:" + getClass().getSimpleName());
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }else{
            AppManager.getAppManager().finishActivity();
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage && null == mUploadMessageSingle) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (result == null) {
                mUploadMessage.onReceiveValue(null);
                mUploadMessage = null;
                return;
            }

            String path =  FileUtils.getPath(this, result);
            if (TextUtils.isEmpty(path)) {
                mUploadMessage.onReceiveValue(null);
                mUploadMessage = null;
                return;
            }
            Uri uri = Uri.fromFile(new File(path));
            //     CLog.i("UPFILE", "onActivityResult after parser uri:" + uri.toString());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mUploadMessage.onReceiveValue(new Uri[]{uri});
            } else {
                mUploadMessage.onReceiveValue(null);
                mUploadMessageSingle.onReceiveValue(result);
            }

            mUploadMessage = null;
            mUploadMessageSingle = null;
        }

        switch (resultCode){
            case 10:
                Log.d("contact", data.getStringExtra("contact"));
                webView.loadUrl("javascript: setContact('" + data.getStringExtra("contact") + "');");

                break;
            case 11:
                break;
            default:
                break;
        }
    }

}
