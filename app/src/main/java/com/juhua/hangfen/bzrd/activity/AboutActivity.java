package com.juhua.hangfen.bzrd.activity;

import android.Manifest;
import android.app.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


import com.juhua.hangfen.bzrd.R;
import com.juhua.hangfen.bzrd.constants.Constants;
import com.juhua.hangfen.bzrd.model.JsonMessage;
import com.juhua.hangfen.bzrd.model.UpdateInfo;
import com.juhua.hangfen.bzrd.tools.AppContext;
import com.juhua.hangfen.bzrd.application.AppManager;
import com.juhua.hangfen.bzrd.tools.DataCleanManager;
import com.juhua.hangfen.bzrd.tools.DialogUtil;
import com.juhua.hangfen.bzrd.tools.UIHelper;
import com.juhua.hangfen.bzrd.util.GsonUtil;
import com.juhua.hangfen.bzrd.util.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;

import okhttp3.Call;

/**
 * Created by kuai on 2017/1/3.
 */

public class AboutActivity extends Activity{
    AppContext appContext = new AppContext();
    private static final String TAG = WebActivity.class.getSimpleName();
    private static final String APP_CACAHE_DIRNAME = "/webcache";
    // 更新版本要用到的一些信息
    private UpdateInfo info;
    private String appVersionStr;
    private ProgressDialog progressDialog;//下载进度条
    private ProgressDialog dialog;//检查更新等待条
    UpdateInfoService updateInfoService;
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        AppManager.getAppManager().addActivity(this);
        Button button=(Button)findViewById(R.id.update);
        Button btnBack = (Button)findViewById(R.id.back_btn);
        Button btnShare = (Button)findViewById(R.id.share);
        Button btnClean = (Button)findViewById(R.id.clean);
        Button btnQrCode = (Button)findViewById(R.id.qrcode);
        TextView tvVersion = (TextView)findViewById(R.id.textview_version);
        try {
            PackageManager packageManager = this.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    this.getPackageName(), 0);
            appVersionStr = getResources().getString(R.string.app_name) + "履职服务平台 V " + packageInfo.versionName;
            tvVersion.setText(appVersionStr);//当前App版本
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        btnQrCode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AboutActivity.this, QRCodeActivity.class);
                if(appVersionStr != null){
                    intent.putExtra("appVersion", appVersionStr);
                }else{
                    intent.putExtra("appVersion", "");
                }
                startActivity(intent);
            }
        });
        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AppManager.getAppManager().finishActivity();
            }
        });
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appContext.isNetworkConnected(AboutActivity.this)) {
                    checkVersion();
                }else {
                    DialogUtil.showDialog(AboutActivity.this, "请检查网络连接！", true);
                }
            }
        });
        btnShare.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (appContext.isNetworkConnected(AboutActivity.this)) {
                        shareUrl();
                    }else {
                        DialogUtil.showDialog(AboutActivity.this, "请检查网络连接！", true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        btnClean.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DataCleanManager.clearAllCache(AboutActivity.this);
                clearWebViewCache();
                ToastUtils.show("清除成功");
              //  Toast.makeText(AboutActivity.this, "清除成功", Toast.LENGTH_SHORT).show();
            }
        });
    }
    protected void checkVersion(){
        dialog = ProgressDialog.show(AboutActivity.this, "", "正在检查更新...") ;
        // 自动检查有没有新版本 如果有新版本就提示更新
        OkHttpUtils
                .post()
                .url(Constants.ASHX_URL)
                .addParams("method", "GetWebAppVersion")
                .addParams("type", "Android")
                .addParams("verify", Constants.VERIFY)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dialog.dismiss();
                        ToastUtils.show("检查更新失败：" + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {;
                        dialog.dismiss();
                        JsonMessage jsonMessage = GsonUtil.parseJsonWithGson(response, JsonMessage.class);
                        if(jsonMessage.isSuccess()){
                            try {
                                PackageManager packageManager = getPackageManager();
                                PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
                                String value = GsonUtil.beanToJSONString(jsonMessage.getData());
                                final UpdateInfo updateInfo = GsonUtil.parseJsonWithGson(value, UpdateInfo.class);
                                String newVersion = updateInfo.getVersion();
                                String currentVersion = packageInfo.versionName;
                                if (newVersion.equals(currentVersion)) {
                                    ToastUtils.show("当前已是最新版本！");
                                } else{
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(AboutActivity.this);
                                    dialog.setIcon(R.drawable.ic_cloud_download_black);
                                    dialog.setTitle("请升级至最新版本" + updateInfo.getVersion());
                                    dialog.setMessage(updateInfo.getDescription());
                                    dialog.setPositiveButton(R.string.action_update, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            downloadNewApk(updateInfo.getUrl());
                                        }
                                    });
                                    dialog.setNegativeButton(R.string.action_cancel, null);
                                    dialog.create().show();
                                }
                            } catch (PackageManager.NameNotFoundException e) {
                                ToastUtils.show("版本检测失败！");
                            }
                        }else{
                            ToastUtils.show(jsonMessage.getMessage());
                        }
                    }
                });

    }

    protected void downloadNewApk(String url){
        ProgressDialog progressDialog = new ProgressDialog(AboutActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIcon(getResources().getDrawable(R.drawable.ic_file_download));
        progressDialog.setTitle("正在下载");
        progressDialog.setMessage("请稍候...");
        progressDialog.setProgress(0);
        progressDialog.show();
        new UpdateInfoService(this).downLoadFile(url, progressDialog, new Handler());

    }

    protected void shareUrl(){
        dialog = ProgressDialog.show(AboutActivity.this, "", "准备分享中...") ;
        // 自动检查有没有新版本 如果有新版本就提示更新
        OkHttpUtils
                .post()
                .url(Constants.ASHX_URL)
                .addParams("method", "GetWebAppVersion")
                .addParams("type", "Android")
                .addParams("verify", Constants.VERIFY)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dialog.dismiss();
                        ToastUtils.show("分享失败：" + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {;
                        dialog.dismiss();
                        JsonMessage jsonMessage = GsonUtil.parseJsonWithGson(response, JsonMessage.class);
                        if(jsonMessage.isSuccess()){
                            String value = GsonUtil.beanToJSONString(jsonMessage.getData());
                            final UpdateInfo updateInfo = GsonUtil.parseJsonWithGson(value, UpdateInfo.class);
                            UIHelper.showShareMore(AboutActivity.this, getResources().getString(R.string.share_app), updateInfo.getUrl());
                        }else{
                            ToastUtils.show(jsonMessage.getMessage());
                        }
                    }
                });

    }
    private void checkUpdate(){
        dialog = ProgressDialog.show(AboutActivity.this, "", "正在检查更新...") ;
        // 自动检查有没有新版本 如果有新版本就提示更新
        new Thread() {
            public void run() {
                try {
                    updateInfoService = new UpdateInfoService(AboutActivity.this);
                    info = updateInfoService.getUpDateInfo();
                    handler1.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        }.start();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler1 = new Handler() {
        public void handleMessage(Message msg) {
            // 如果有更新就提示
            if (updateInfoService.isNeedUpdate() == 1) {
                dialog.dismiss();
                showUpdateDialog();
            }else if(updateInfoService.isNeedUpdate() == 0){
                dialog.dismiss();
                ToastUtils.show("已更新到最新版本");
              //  Toast.makeText(AboutActivity.this, "已更新到最新版本", Toast.LENGTH_SHORT).show();
            }else{
                dialog.dismiss();
                DialogUtil.showDialog(AboutActivity.this, "服务器响应异常！请重新尝试或与管理员联系！", true);
            }
        };
    };

    //显示是否要更新的对话框
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(getResources().getDrawable(R.drawable.ic_cloud_download_black));
        builder.setTitle("请升级App至版本" + info.getVersion());
        builder.setMessage(info.getDescription());
        builder.setCancelable(false);
        builder.setPositiveButton("更新应用", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (ContextCompat.checkSelfPermission(AboutActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AboutActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1);
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(AboutActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    } else {
       /*                 AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setIcon(android.R.drawable.ic_dialog_info);
                        builder.setTitle("请请打开手机存储权限");
                        builder.setMessage(info.getDescription());
                        builder.setCancelable(false);
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

                                Uri uri = Uri.fromParts("package", getPackageName(), null);

                                intent.setData(uri);

                                startActivityForResult(intent, 1);
                            }
                          });*/

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                }else{
                    if (Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                        downFile(info.getUrl());
                    } else {
                        ToastUtils.show("SD卡不可用，请插入SD卡");
                       // Toast.makeText(AboutActivity.this, "SD卡不可用，请插入SD卡", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog ad = builder.create();
        ad.show();
        ad.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.common_dark_shadow));

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                        downFile(info.getUrl());
                    } else {
                      //  Toast.makeText(AboutActivity.this, "SD卡不可用，请插入SD卡", Toast.LENGTH_SHORT).show();
                        ToastUtils.show("设备存储空间不足！");
                    }

                } else {
                    ToastUtils.show("设备存储权限被拒绝，无法下载");
                  //  Toast.makeText(AboutActivity.this, "手机存储权限被拒绝，无法下载", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    void downFile(final String url) {
        progressDialog = new ProgressDialog(AboutActivity.this);    //进度条，在下载的时候实时更新进度，提高用户友好度
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("正在下载");
        progressDialog.setMessage("请稍候...");
        progressDialog.setProgress(0);
        progressDialog.show();
        updateInfoService.downLoadFile(url, progressDialog,handler1);
    }

    public void clearWebViewCache(){
        //清理Webview缓存数据库
        try {
            deleteDatabase("webview.db");
            deleteDatabase("webviewCache.db");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //WebView 缓存文件
        File appCacheDir = new File(getFilesDir().getAbsolutePath()+APP_CACAHE_DIRNAME);
        Log.e(TAG, "appCacheDir path="+appCacheDir.getAbsolutePath());

        File webviewCacheDir = new File(getCacheDir().getAbsolutePath()+"/webviewCache");
        Log.e(TAG, "webviewCacheDir path="+webviewCacheDir.getAbsolutePath());

        //删除webview 缓存目录
        if(webviewCacheDir.exists()){
            deleteFile(webviewCacheDir);
        }
        //删除webview 缓存 缓存目录
        if(appCacheDir.exists()){
            deleteFile(appCacheDir);
        }
    }
    /**
     * 递归删除 文件/文件夹
     *
     * @param file
     */
    public void deleteFile(File file) {

        Log.i(TAG, "delete file path=" + file.getAbsolutePath());

        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        } else {
            Log.e(TAG, "delete file no exists " + file.getAbsolutePath());
        }
    }
}
