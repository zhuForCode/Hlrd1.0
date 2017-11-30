package com.juhua.hangfen.bzrd.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.juhua.hangfen.bzrd.R;
import com.juhua.hangfen.bzrd.constants.Constants;
import com.juhua.hangfen.bzrd.enums.LoadStateEnum;
import com.juhua.hangfen.bzrd.model.Nav;
import com.juhua.hangfen.bzrd.application.AppManager;
import com.juhua.hangfen.bzrd.tools.FileUtils;
import com.juhua.hangfen.bzrd.util.DownloadUtil;
import com.juhua.hangfen.bzrd.util.GsonUtil;
import com.juhua.hangfen.bzrd.util.ToastUtils;
import com.juhua.hangfen.bzrd.util.ViewUtils;

import java.io.File;
import java.net.URLEncoder;

import static com.juhua.hangfen.bzrd.constants.Constants.RESULT_CONTACT_CONFIRM;

/**
 * Created by congj on 2017/9/28.
 */

public class WebActivity extends BaseActivity {
    private LinearLayout llLoading;//页面加载中
    private LinearLayout llLoadFail;//页面加载失败
    private TextView loadFailText;//页面加载失败 点击textview重新加载

    private LoadStateEnum loadState = LoadStateEnum.LOAD_SUCCESS;
    protected WebView webView;
    private ProgressDialog mLoading;
    protected LinearLayout actionbarLL;
    protected RelativeLayout rightBtnRL;
    protected Button rightBtn;//actionbar 右键
    protected ImageView rightBtnImageView;
    protected View.OnClickListener historyPageListener;

    protected ProgressDialog progressFile;//下载附件
    protected AlertDialog.Builder builder;//附件下载对话框

    private ValueCallback<Uri> mUploadMessageSingle;
    private ValueCallback<Uri[]> mUploadMessage;

    public final Handler mHandler = new Handler();

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
        rightBtnRL = (RelativeLayout)findViewById(R.id.right_btn_RL);
        rightBtn = (Button) findViewById(R.id.right_btn);
        rightBtnImageView = (ImageView)findViewById(R.id.right_btn_image);

        llLoadFail = (LinearLayout)findViewById(R.id.ll_load_fail);
        llLoading = (LinearLayout)findViewById(R.id.ll_loading);
        loadFailText = (TextView) llLoadFail.findViewById(R.id.tv_load_fail_text);

        webView = (WebView) findViewById(R.id.web);
        ViewUtils.changeViewState(webView, llLoading, llLoadFail, LoadStateEnum.LOADING);
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
        ImageView loadingView = (ImageView) llLoading.findViewById(R.id.gif_content_loading);
        Glide.with(WebActivity.this)
                .load(R.drawable.loading)
                .asGif()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(loadingView);
        if(getIntent().getExtras().getString("actionName") != null){
            titleTv.setText(getIntent().getExtras().getString("actionName"));
        }
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
                if(loadState == LoadStateEnum.LOAD_SUCCESS){
                    ViewUtils.changeViewState(view, llLoading, llLoadFail, LoadStateEnum.LOAD_SUCCESS);
                }else{
                    ViewUtils.changeViewState(webView, llLoading, llLoadFail, LoadStateEnum.LOAD_FAIL);
                }
            }
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                loadState = LoadStateEnum.LOAD_FAIL;
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
                loadState = LoadStateEnum.LOAD_FAIL;
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
                if(title.contains("网页无法打开") || title.contains("error")){
                    loadState = LoadStateEnum.LOAD_FAIL;
                    titleTv.setText("出错了");
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
        final StringBuilder builder = new StringBuilder(Constants.DOMAIN_NAME);
        try {
            if(getIntent().getExtras().get("Token") == null){

            }else{
                builder.append("LzptApp/Transfer.aspx?token=");
                builder.append(getIntent().getExtras().getString("Token"));
                builder.append("&actionUrl=");
                builder.append(getIntent().getExtras().getString("actionUrl"));
                if(getIntent().getExtras().getString("actionUrl").contains("http://")){
                    webView.loadUrl(getIntent().getExtras().getString("actionUrl"));
                    WebSettings settings = webView.getSettings();
                    //支持屏幕缩放
                    settings.setSupportZoom(true);
                    settings.setBuiltInZoomControls(true);
                    settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
                    settings.setLoadWithOverviewMode(true);
                    settings.setUseWideViewPort(true);//关键点
                }else{
                    webView.loadUrl(builder.toString());
                }
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


    public  class MAndroid{

        @JavascriptInterface
        public void toLogin(){//退回到登陆页面
            AppManager.getAppManager().finishAllActivity();
            Intent intent = new Intent(AppManager.getContext(), LoginActivity.class);
            startActivity(intent);

        }
        @JavascriptInterface
        public void tokenOut(){//退回到登陆页面
            AlertDialog.Builder builder = new AlertDialog.Builder(AppManager.getContext());
            builder.setIcon(getResources().getDrawable(R.drawable.ic_error_outline_black));
            builder.setTitle("用户信息已过期！");
            builder.setMessage("请重新登陆！可能的原因：\n1.用户半小时内无有效操作\n2.账号在其他手机上被登陆\n3.服务器升级维护");
            builder.setCancelable(false);
            builder.setPositiveButton("重新登陆", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AppManager.getAppManager().finishAllActivity();
                    Intent intent = new Intent(WebActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
            builder.create().show();
        }
        @JavascriptInterface
        public void toAbout(){//前往关于界面
            Intent intent = new Intent(WebActivity.this, AboutActivity.class);
            startActivity(intent);
        }

        @JavascriptInterface//返回main.aspx,即结束指定数目的活动
        public void toMain(int count){
             AppManager.getAppManager().finishCountActivity(count);
        }
        @JavascriptInterface//返回上一页（活动）
        public void toLast(){
            AppManager.getAppManager().finishActivity();//这边不能直接用finish()，出栈要清除栈的记录
        }

        @JavascriptInterface
        public void backToHome(){
            AppManager.getAppManager().finishActivity();//这边不能直接用finish()，出栈要清除栈的记录
        }

        @JavascriptInterface
        public  void newWeb(String url){
            if(url.contains("nav=new")){
                Intent intent = new Intent(WebActivity.this, WebActivity.class);
                String u = url.split("LzptApp/")[1];
                try {
                    if(u.contains("&")){
                        u = u.replace("&", "%2526");//强行对&编码两次   & -> %26   -> %2526 因为在c#端会解码两次
                    }
                    intent.putExtra("actionUrl", u);
                }catch (Exception e){
                    intent.putExtra("actionUrl", u);
                }
                if(getIntent().getExtras().getString("Token") != null){
                    intent.putExtra("Token", getIntent().getExtras().getString("Token"));
                }
                startActivity(intent);
                return;
            }

            if(url.contains("nav=out")){//外部网站
                Intent intent = new Intent(WebActivity.this, WebActivity.class);
                intent.putExtra("actionUrl", url);
                if(getIntent().getExtras().getString("Token") != null){
                    intent.putExtra("Token", getIntent().getExtras().getString("Token"));
                }
                startActivity(intent);
            }

        }

        @JavascriptInterface
        public void newResponseWeb(String url, String formData, int responseCode){
            if(url.contains("nav=new")){
                Intent intent = new Intent(WebActivity.this, WebActivity.class);
                String u = url.split("LzptApp/")[1];
                try {
                    intent.putExtra("actionUrl", URLEncoder.encode(u, "utf-8"));
                }catch (Exception e){
                    intent.putExtra("actionUrl", u);
                }
                intent.putExtra("FormData", formData);
                intent.putExtra("Token", getIntent().getExtras().getString("Token"));
                startActivityForResult(intent, responseCode);
            }
        }

        @JavascriptInterface
        public void setContact(String contact){
            Intent intent  = new Intent();
            intent.putExtra("contact", contact);
            setResult(RESULT_CONTACT_CONFIRM, intent);
            AppManager.getAppManager().finishActivity();
        }

        @JavascriptInterface
        public void setNav(String jsonData){
            final Nav nav = GsonUtil.parseJsonWithGson(jsonData, Nav.class);
            Runnable runnableUI = new Runnable() {
                @Override
                public void run() {
                    if(nav.getPosition() == null){
                        return;
                    }
                    if (nav.getClick() != null) {
                        View.OnClickListener clickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                webView.loadUrl("javascript: " + nav.getClick() + "();");
                            }
                        };
                        if (nav.getPosition().equals("left"))
                            backButton.setOnClickListener(clickListener);
                        if (nav.getPosition().equals("center"))
                            titleTv.setOnClickListener(clickListener);
                        if (nav.getPosition().equals("right"))
                            rightBtn.setOnClickListener(clickListener);
                    }

                    if(nav.getText() != null && nav.getIcon() != null){
                        if (nav.getPosition().equals("center")) {
                            Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/iconfont.ttf");
                            String text = nav.getText() + " " + nav.getIcon();
                            titleTv.setTypeface(typeface);
                            titleTv.setText(text);
                        }

                        if (nav.getPosition().equals("right")) {
                            Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/iconfont.ttf");
                            String text = nav.getIcon() + nav.getText();
                            rightBtn.setTypeface(typeface);
                            rightBtn.setText(text);
                        }
                    }

                    if(nav.getText() != null && nav.getIcon() == null){
                        if (nav.getPosition().equals("center")) {
                            titleTv.setText(nav.getText());
                        }

                        if (nav.getPosition().equals("right")) {
                            rightBtn.setText(nav.getText());
                        }
                    }

                    if(nav.getText() == null && nav.getIcon() != null){
                        if (nav.getPosition().equals("center")) {
                            Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/iconfont.ttf");
                            String text = nav.getIcon();
                            titleTv.setTypeface(typeface);
                            titleTv.setText(text);
                        }

                        if (nav.getPosition().equals("right")) {
                            Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/iconfont.ttf");
                            String text = nav.getIcon();
                            rightBtn.setTypeface(typeface, Typeface.BOLD);
                            rightBtn.setText(text);
                            rightBtn.setTextSize(18);
                        }
                    }

                    if (nav.getVisible() != null && nav.getVisible()) {
                        rightBtnRL.setVisibility(View.VISIBLE);
                    }

                }
            };
            mHandler.post(runnableUI);


        }

        @JavascriptInterface
        public void addLoading(String message){
            mLoading = ProgressDialog.show(WebActivity.this, "", message);
        }

        @JavascriptInterface
        public void removeLoading(){
            mLoading.dismiss();
        }

        @JavascriptInterface
        public void showToast(String message){
            ToastUtils.show(message);
        }

        @JavascriptInterface
        public void toDownloadFile(final String urlD){
            builder = new AlertDialog.Builder(WebActivity.this);
            progressFile = new ProgressDialog(WebActivity.this);    //进度条，在下载的时候实时更新进度，提高用户友好度
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
            final String sdcard = FileUtils.getDownloadDir();
            int idx = urlD.lastIndexOf("/") + 1;
            final String fileName = urlD.substring(idx);
            DownloadUtil.get().download(urlD, sdcard, new DownloadUtil.OnDownloadListener() {
                @Override
                public void onDownloadSuccess() {
                    progressFile.dismiss();
                    builder.setIcon(getResources().getDrawable(R.drawable.ic_cloud_done));
                    builder.setTitle("下载成功");
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.setPositiveButton("直接打开", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                File file = new File(sdcard + fileName);
                                openFile(file);
                            }catch (Exception e){
                                ToastUtils.show("无法打开文件！");
                            }
                        }
                    });
                    builder.setMessage("文件已保存至：" + sdcard + fileName);
                    builder.setCancelable(true);
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            builder.create().show();
                        }
                    };
                    mHandler.post(runnable);
                }

                @Override
                public void onDownloading(int progress) {
                    progressFile.setProgress(progress);
                }

                @Override
                public void onDownloadFailed() {
                    progressFile.dismiss();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.show("下载失败！");
                        }
                    };
                    mHandler.post(runnable);
                }
            });
        }
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
    public void testClearDefault() {//清除设置的默认值
        PackageManager pm = this.getPackageManager();
        pm.clearPackagePreferredActivities(this.getPackageName());
    }

}
