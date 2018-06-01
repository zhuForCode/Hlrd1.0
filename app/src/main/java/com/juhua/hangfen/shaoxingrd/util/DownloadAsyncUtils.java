package com.juhua.hangfen.shaoxingrd.util;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by JiaJin Kuai on 2017/4/17.
 */

public class DownloadAsyncUtils extends AsyncTask<String[], Void, String>{
    private ProgressDialog progressDialog;
    private String fileUrl;
    public DownloadAsyncUtils setProgress(ProgressDialog p){
        this.progressDialog = p;
        return  this;
    }
    public DownloadAsyncUtils setUrl(String url){
        this.fileUrl = url;
        return this;
    }
    @Override
    public void onPreExecute() {
    }
    @Override
    public String doInBackground(String[]... params){
        String result = "";
        try {
            int idx = fileUrl.lastIndexOf("/");
            String ext = fileUrl.substring(idx);
            File file = new File(FileUtils.getDownloadDir() + ext);
            if(!file.exists()){
                InputStream inputStream = null;
                URL url = new URL(fileUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(20000);
                if (conn.getResponseCode() == 200) {
                    inputStream = conn.getInputStream();
                    int fileLength = conn.getContentLength();   //获取文件大小
                    progressDialog.setProgressNumberFormat("%1d kb/%2d kb");
                    progressDialog.setMax(fileLength/1024);                              //设置进度条的总长度
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
    public void onPostExecute(String result) {
        progressDialog.cancel();
    }

}
