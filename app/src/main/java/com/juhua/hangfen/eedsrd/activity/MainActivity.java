package com.juhua.hangfen.eedsrd.activity;
/**
 * Created by kuai on 2016/12/26.
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.juhua.hangfen.eedsrd.R;
import com.juhua.hangfen.eedsrd.application.AppCache;
import com.juhua.hangfen.eedsrd.constants.Constants;
import com.juhua.hangfen.eedsrd.tools.AppManager;
import com.juhua.hangfen.eedsrd.tools.FileUtils;
import com.juhua.hangfen.eedsrd.util.ToastUtils;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class MainActivity extends Activity {
    protected WebView webView;
    protected long mExitTime;
    protected Context mContext;
    private String firsturl;
    public String cookies;
    protected ProgressDialog progressFile;//进度条
    protected AlertDialog.Builder builder;//对话框
    private ValueCallback<Uri> mUploadMessageSingle;
    private ValueCallback<Uri[]> mUploadMessage;
    private final static int FILECHOOSER_RESULTCODE=1;

    @Override
    protected void onDestroy() {
        AppCache.removeFromStack(this);
        super.onDestroy();
        Log.i("kjj", "onDestroy:" + getClass().getSimpleName());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        AppCache.addToStack(this);
        setContentView(R.layout.activity_wvmain);
   //     setSystemBarTransparent();
        AppManager.getAppManager().addActivity(this);
        init();
    }
    private void setSystemBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // LOLLIPOP解决方案
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // KITKAT解决方案
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
    protected void init(){
        webView = (WebView) findViewById(R.id.main_wv);
        webView.getSettings().setJavaScriptEnabled(true);
           webView.getSettings().setBuiltInZoomControls(false);
        // 开启 DOM storage API 功能
        webView.getSettings().setDomStorageEnabled(true);
        //开启 database storage API 功能
        webView.getSettings().setDatabaseEnabled(true);
        String cacheDirPath = getFilesDir().getAbsolutePath()+"/webCache";
        Log.d("kjj-mu", "cacheDirPath="+cacheDirPath);
        //设置数据库缓存路径
        webView.getSettings().setDatabasePath(cacheDirPath);
        //设置  Application Caches 缓存目录
        webView.getSettings().setAppCachePath(cacheDirPath);
        //开启 Application Caches 功能
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setLayoutAlgorithm ( WebSettings.LayoutAlgorithm.SINGLE_COLUMN );
        webView.getSettings().setLoadWithOverviewMode ( true );
        webView.getSettings().setDefaultTextEncodingName("GBK");
        webView.setDownloadListener(new MyWebViewDownLoadListener());
        webView.addJavascriptInterface(new MainToOther(), "MainToOther");
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d("kjj", view.getUrl() + view.getTitle());
         //       CookieManager cm = CookieManager.getInstance();
         //       cookies = cm.getCookie(url);
//                Log.d("cookiesStart",cookies);
            }

            @Override
            public void onPageFinished(WebView view, String url) {

            }
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                switch(errorCode)
                {
                    case HttpStatus.SC_NOT_FOUND:
                        view.loadUrl("file:///android_asset/error.html");
                        break;
                    case WebViewClient.ERROR_HOST_LOOKUP:

                        break;
                }
                view.loadUrl("javascript:$.hideLoading();$.toast('网络异常', 'forbidden');");
                if(!failingUrl.contains("ZjrdAppHandler.ashx")){
                    view.loadUrl("file:///android_asset/error.html");
                }

            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
                switch(rerr.getErrorCode())
                {
                    case HttpStatus.SC_NOT_FOUND:
                        view.loadUrl("file:///android_asset/error.html");
                        break;
                }
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
            /*在新Webview中打开页面
                if(url!=""){
                    if(url.indexOf("GetToken") < 0 && url.indexOf("mainB") < 0){
                        Intent intent = new Intent(MainActivity.this, PublicWebViewActivity.class);
                        intent.putExtra("url", url);
                        startActivity(intent);
                        Log.d("urlSencond", url);
                    }else{
                        view.loadUrl(url);
                        Log.d("urlFirst", url);
                    }
                }
                */
                if(!url.equals("")){
                    view.loadUrl(url);
                    Log.d("urlFirst", url);
                }
                return true;
            }
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
                handler.proceed(); //忽略证书错误
            }
        });
        webView.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
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
                        i.setType(fileChooserParams.getAcceptTypes()[0]);
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
        loadURL();
    }

    public void loadURL(){
        try {
            if(getIntent().getExtras().get("Token") == null){
                webView.loadUrl("file:///android_asset/error.html");//加载错误页面
            }else{
                firsturl = Constants.DOMAIN_NAME + "/ZjrdApp/main.aspx?token=" +   getIntent().getExtras().get("Token").toString();
                webView.loadUrl(firsturl);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            Log.d("urlNow", webView.getUrl());
            WebBackForwardList listBackForward = webView.copyBackForwardList();
            for(int i = 0 ; i < listBackForward.getSize() ; i++) {
                Log.d("list", listBackForward.getItemAtIndex(i).getUrl());
            }
            if(webView.getUrl().contains(Constants.DOMAIN_NAME + "/ZjrdApp/gzxxs.html")
                    || webView.getUrl().contains(Constants.DOMAIN_NAME + "/ZjrdApp/yajys.html")
                    || webView.getUrl().contains(Constants.DOMAIN_NAME + "/ZjrdApp/dblzs.html")
                    || webView.getUrl().contains(Constants.DOMAIN_NAME + "/ZjrdApp/mymaillistB.html")
                    || webView.getUrl().contains(Constants.DOMAIN_NAME + "/ZjrdApp/wljls.html")){
                webView.loadUrl(Constants.DOMAIN_NAME + "/ZjrdApp/main.aspx");
             //   webView.clearHistory();
              //  webView.goBackOrForward(listBackForward.getSize() - 2*listBackForward.getSize() + 1);
            }else if(webView.getUrl().contains(Constants.DOMAIN_NAME + "/ZjrdApp/wljltypemenu.html")){
                webView.loadUrl(Constants.DOMAIN_NAME + "/ZjrdApp/wljls.html");
             //   webView.clearHistory();
            }else if (webView.getUrl().contains(Constants.DOMAIN_NAME + "/ZjrdApp/main.aspx")){
              //  webView.goBack();// 返回前一个页面
                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                    Object mHelperUtils;
                    ToastUtils.show("再按一次退出程序");
                  //  Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    mExitTime = System.currentTimeMillis();

                } else {
                    AppCache.clearStack();
                }
            }else {
                webView.goBack();// 返回前一个页面
            }
            return true;
        }else{
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Object mHelperUtils;
              //  Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                ToastUtils.show("再按一次退出程序");
                mExitTime = System.currentTimeMillis();

            } else {
                AppCache.clearStack();
            }
            return true;
        }
     //   return super.onKeyDown(keyCode, event);
    }

        /**
         * 同步一下cookie
         */
/*    public void synCookies(Context context, String url) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
//        cookieManager.removeSessionCookie();//移除
     cookieManager.setCookie(url, cookies);
        CookieSyncManager.getInstance().sync();
    }*/
    public class MainToOther{
        @JavascriptInterface
        public void toLogin(){//退回到登陆页面
            AppCache.clearStack();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
       //     AppManager.getAppManager().finishAllActivity();
         //   finish();
            startActivity(intent);

        }
        @JavascriptInterface
        public void tokenOut(){//退回到登陆页面
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setIcon(getResources().getDrawable(R.drawable.ic_error_outline_black));
            builder.setTitle("用户信息已过期！");
            builder.setMessage("请重新登陆！可能的原因：\n1.用户半小时内无有效操作\n2.账号在其他手机上被登陆\n3.服务器升级维护");
            builder.setCancelable(false);
            builder.setPositiveButton("重新登陆", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AppCache.clearStack();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
            builder.create().show();
        }
        @JavascriptInterface
        public void toAbout(){//前往关于界面
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        }
        @JavascriptInterface//返回main.aspx,即结束指定数目的活动
        public void toMain(int count){
          //  AppManager.getAppManager().finishCountActivity(count);
        }
        @JavascriptInterface//返回上一页（活动）
        public void toLast(){
            AppManager.getAppManager().finishActivity();//这边不能直接用finish()，出栈要清除栈的记录
        }

        @JavascriptInterface
        public void forToast(String tContent){
         //   Toast toast = Toast.makeText(MainActivity.this, tContent, Toast.LENGTH_SHORT);
            ToastUtils.show(tContent);
         //   toast.show();
        }

        @JavascriptInterface
        public void forDownloading(String urlD){
            imgurl = urlD;
            new Thread() {
                public void run() {
                    try {
                        new SaveImage().execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };
            }.start();
        }
        @JavascriptInterface
        public void forDownloadFile(String urlD){
            imgurl = urlD;
            builder = new AlertDialog.Builder(MainActivity.this);
            progressFile = new ProgressDialog(MainActivity.this);    //进度条，在下载的时候实时更新进度，提高用户友好度
            progressFile.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressFile.setIcon(getResources().getDrawable(R.drawable.ic_file_download));
            progressFile.setTitle("  正在下载");
            progressFile.setMessage("请稍候...");
            progressFile.setProgress(0);
            progressFile.setCancelable(false);
            progressFile.setButton(DialogInterface.BUTTON_NEGATIVE, "后台下载",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progressFile.dismiss();
                        }
                    });
            progressFile.show();
            new Thread() {
                public void run() {
                    try {
                        new SaveFile().execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };
            }.start();
        }
    }
    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
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
    }


    private String imgurl = "";
  //  private String outPutFilePath = "";
    private File outputFile = null;

    /***
     * 功能：用线程保存图片
     *
     * @author wangyp
     */
    private class SaveImage extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                String sdcard = Environment.getExternalStorageDirectory().toString();
                File file = new File(sdcard + "/Download");
                if (!file.exists()) {
                    file.mkdirs();
                }
                int idx = imgurl.lastIndexOf("/");
                String ext = imgurl.substring(idx);
                file = new File(sdcard + "/Download/" + ext);
                if(!file.exists()){
                    InputStream inputStream = null;
                    URL url = new URL(imgurl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(20000);
                    if (conn.getResponseCode() == 200) {
                        inputStream = conn.getInputStream();
                    }
                    byte[] buffer = new byte[4096];
                    int len = 0;
                    FileOutputStream outStream = new FileOutputStream(file);
                    while ((len = inputStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, len);
                    }
                    outStream.close();
                    result = "图片已保存至：" + file.getAbsolutePath();
                }else{
                    result = "图片已存在！";
                }

            } catch (Exception e) {
                result = "保存失败！" + e.getLocalizedMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
          //  Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
            ToastUtils.show(result);
        }
    }
    /***
     * 功能：用线程保存文件然后打开
     *
     * @author wangyp
     */
    private class SaveFile extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                String sdcard = FileUtils.getDownloadDir();
                File file = new File(sdcard);
                if (!file.exists()) {
                    file.mkdirs();
                }
                int idx = imgurl.lastIndexOf("/");
                String ext = imgurl.substring(idx);
                file = new File(sdcard + ext);
                outputFile = file;
                if(!file.exists()){
                    InputStream inputStream = null;
                    URL url = new URL(imgurl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(20000);
                //    int fileLength = conn.getContentLength();  //获取文件大小
                         //设置进度条的总长度
                    if (conn.getResponseCode() == 200) {
                        inputStream = conn.getInputStream();
                        int fileLength = conn.getContentLength();   //获取文件大小
                        progressFile.setProgressNumberFormat("%1d kb/%2d kb");
                        progressFile.setMax(fileLength/1024);                              //设置进度条的总长度
                    }
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    int process = 0;
                    FileOutputStream outStream = new FileOutputStream(file);
                    while ((len = inputStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, len);
                        process += len/1024;
                        progressFile.setProgress(process);
                    }
                    outStream.close();
                    result = "文件已保存至：" + file.getAbsolutePath();
                    builder.setIcon(getResources().getDrawable(R.drawable.ic_cloud_done));
                    builder.setTitle("下载成功");
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                }else{
                    result = "文件已存在！";
                    builder.setIcon(getResources().getDrawable(R.drawable.ic_folder_open));
                    builder.setTitle("文件已下载");
                    builder.setNegativeButton("重新下载", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(outputFile.delete()){
                                progressFile = new ProgressDialog(MainActivity.this);    //进度条，在下载的时候实时更新进度，提高用户友好度
                                progressFile.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                progressFile.setIcon(getResources().getDrawable(R.drawable.ic_file_download));
                                progressFile.setTitle("  正在下载");
                                progressFile.setMessage("请稍候...");
                                progressFile.setProgress(0);
                                progressFile.setCancelable(false);
                                progressFile.setButton(DialogInterface.BUTTON_NEGATIVE, "后台下载",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                progressFile.dismiss();
                                            }
                                        });
                                progressFile.show();
                                new Thread() {
                                    public void run() {
                                        try {
                                            new SaveFile().execute();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    };
                                }.start();
                            }else{
                                ToastUtils.show("删除原文件出错，请检查后重新尝试！");
                               // Toast.makeText(MainActivity.this, "删除原文件出错，请检查后重新尝试！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                builder.setPositiveButton("直接打开", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //   Intent intent = getPdfFileIntent(outPutFilePath);
                        //   startActivity(intent);
                        try {
                            openFile(outputFile);
                        }catch (Exception e){
                            ToastUtils.show("无法打开文件！");
                          //  Toast.makeText(MainActivity.this, "无法打开文件！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (Exception e) {
                result = "保存失败！" + e.toString();
                builder.setIcon(getResources().getDrawable(R.drawable.ic_sync_disabled));
                builder.setTitle("下载失败");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
          //  Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();\
            progressFile.cancel();
            showAlertD(result);

        }

    }

    protected void showAlertD(String ret){
        //显示是否要更新的对话框
     //   builder = new AlertDialog.Builder(this);
        builder.setMessage(ret);
        builder.setCancelable(true);
        builder.create().show();
    }

    /**
     * android获取一个用于打开PDF文件的intent
     * @param path 要打开的文件的绝对路径
     * @return
     */
    public Intent getPdfFileIntent(String path)
    {

        Intent intent = new Intent(Intent.ACTION_VIEW);//Intent.ACTION_VIEW = "android.intent.action.VIEW"

        intent.addCategory(Intent.CATEGORY_DEFAULT);//Intent.CATEGORY_DEFAULT = "android.intent.category.DEFAULT"

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Uri uri = Uri.fromFile(new File(path ));

        intent.setDataAndType(uri, "application/pdf");

        return intent;

    }
    public void testClearDefault() {//清除设置的默认值
        PackageManager pm = this.getPackageManager();
        pm.clearPackagePreferredActivities(this.getPackageName());
    }

    /**
     * 获得文件的mimeType
     * @param file
     * @return
     */
    public static String getMIMEType(File file) {
        String type = "*";
        if(file == null) return type;
        String fName = file.getName();
        // 取得扩展名
        String end = fName.substring(fName.lastIndexOf("."),
                fName.length()).toLowerCase();
        if (end.equals("")) return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }

        return type;
    }

    private void openFile(File file){
        //Uri uri = Uri.parse("file://"+file.getAbsolutePath());
        testClearDefault();
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        //获取文件file的MIME类型
        String type = getMIMEType(file);
        //设置intent的data和Type属性。
        intent.setDataAndType(Uri.fromFile(file), type);
        //跳转
        startActivity(intent);
    }

    private static String[][] MIME_MapTable = {
            //{后缀名， MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".xls", "application/msword"},
            {".xlsx", "application/msword"},
            {".docx", "application/msword"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.ms-powerpoint"},
            {".prop", "text/plain"},
            {".rar", "application/x-rar-compressed"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            //{".xml", "text/xml"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/zip"},
            {"", "*/*"}
    };
}
