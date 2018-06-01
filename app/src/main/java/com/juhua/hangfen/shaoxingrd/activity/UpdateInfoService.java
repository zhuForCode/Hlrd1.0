package com.juhua.hangfen.shaoxingrd.activity;


/**
 * Created by kuai on 2017/1/3.
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;

import com.juhua.hangfen.shaoxingrd.constants.Constants;
import com.juhua.hangfen.shaoxingrd.model.UpdateInfo;
import com.juhua.hangfen.shaoxingrd.tools.CryptoTools;
import com.juhua.hangfen.shaoxingrd.tools.FileUtils;
import com.juhua.hangfen.shaoxingrd.tools.JsonUtils;


public class UpdateInfoService {
    private ProgressDialog progressDialog;
    Handler handler;
    Context context;
    UpdateInfo updateInfo;
    JsonUtils jsonUtils = new JsonUtils();
    public UpdateInfoService(Context context){
        this.context=context;
    }

    public UpdateInfo getUpDateInfo() throws Exception {
        String path = null;
        String description = null;
        String versionName = null;
        String verify = null;
        String permission = null;
        try{
            CryptoTools cryptoTools = new CryptoTools();
            verify = cryptoTools.returnVerify();
        } catch (Exception e){
            e.printStackTrace();
        }
        String[] prtRequest= {"type", "verify"};
        String[] prtRequestPut= {"Android", Constants.VERIFY};
        String jsonStr = jsonUtils.returnData(Constants.NAME_SPACE, Constants.METHOD_GET_APP_VERSION, Constants.WSDL, prtRequest, prtRequestPut);
        UpdateInfo updateInfo = new UpdateInfo();
        updateInfo.setVersion("");
        updateInfo.setDescription("");
        updateInfo.setUrl("");
        this.updateInfo=updateInfo;
        if(jsonStr.equals("err:异常")|| jsonStr.equals("404")){
            return updateInfo;
        }else{
            String[] propreties = {"version", "description", "url", "Permission"};
            try {
                versionName = jsonUtils.Analysis(jsonStr, propreties).get("version").toString();
                description =  jsonUtils.Analysis(jsonStr, propreties).get("description").toString();
                path = jsonUtils.Analysis(jsonStr, propreties).get("url").toString();
                permission = jsonUtils.Analysis(jsonStr, propreties).get("Permission").toString();
            } catch (JSONException e){
                e.printStackTrace();
            }
            updateInfo.setVersion(versionName);
            updateInfo.setDescription(description);
            updateInfo.setUrl(path);
            updateInfo.setPermission(permission);
            this.updateInfo=updateInfo;
        }
        return updateInfo;
    }

    public int hasPermission(){
        if (updateInfo.getPermission().equals("1")){
            return 1;
        }else {
            return 0;
        }
    }
    public int isNeedUpdate(){
        String new_version = ""; // 最新版本的版本号
        String now_version= "";//获取当前版本号
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            new_version = updateInfo.getVersion();
            now_version= packageInfo.versionName;
            if (new_version.equals(now_version)) {
                return 0;//无需更新
            } else if(new_version.equals("")) {
                return 404;
            }else{
                return 1;
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return 404;
        }

    }


    public void downLoadFile(final String url,final ProgressDialog pDialog,Handler h){
        progressDialog=pDialog;
        handler=h;
        new Thread() {
            public void run() {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);
                HttpResponse response;
                try {
                    response = client.execute(get);
                    HttpEntity entity = response.getEntity();
                    int length = (int) entity.getContentLength();   //获取文件大小
                    progressDialog.setProgressNumberFormat("%1d kb/%2d kb");
                    progressDialog.setMax(length/1024);                            //设置进度条的总长度
                    InputStream is = entity.getContent();
                    FileOutputStream fileOutputStream = null;
                    if (is != null) {
                        File file = new File(
                                FileUtils.getDownloadDir(),
                                "ZjrdWebApp.apk");
                        fileOutputStream = new FileOutputStream(file);
                        //这个是缓冲区，即一次读取10个比特，我弄的小了点，因为在本地，所以数值太大一下就下载完了,
                        //看不出progressbar的效果。
                        byte[] buf = new byte[1024];
                        int ch = -1;
                        int process = 0;
                        while ((ch = is.read(buf)) != -1) {
                            fileOutputStream.write(buf, 0, ch);
                            process += ch/1024;
                            progressDialog.setProgress(process);       //这里就是关键的实时更新进度了！
                        }

                    }
                    fileOutputStream.flush();
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                    down();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }

    void down() {
        handler.post(new Runnable() {
            public void run() {
                progressDialog.cancel();
                update();
            }
        });
    }

    void update() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(FileUtils.getDownloadDir(), "app-bzrd.apk")),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }


}

