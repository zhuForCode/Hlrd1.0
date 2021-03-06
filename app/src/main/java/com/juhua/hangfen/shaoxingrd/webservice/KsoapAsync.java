package com.juhua.hangfen.shaoxingrd.webservice;

import android.os.AsyncTask;
import android.util.Log;

import com.juhua.hangfen.shaoxingrd.constants.Constants;
import com.juhua.hangfen.shaoxingrd.model.User;
import com.juhua.hangfen.shaoxingrd.model.VersionInfo;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by JiaJin Kuai on 2017/4/17.
 */

public class KsoapAsync extends AsyncTask<String[], Void, Object> {
    private short modelType;
    private UpdateUI updateUI;
    private KsoapHelpler ksoapHelpler;

    public KsoapAsync setModelType(short modelType) {
        this.modelType = modelType;
        return this;
    }

    public KsoapAsync setUpdateUI(UpdateUI u){
        this.updateUI = u;
        return this;
    }
    @Override
    public void onPreExecute() {
    }
    @Override
    public Object doInBackground(String[]... params){
        return getMultipleModel();
    }

    @Override
    public void onPostExecute(Object obj) {
        Log.d("onPostExecute", "result");

        updateUI.onResponse(obj);
    }
    private HashMap getMultipleModel(){
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("User", new KsoapHelpler<>(User.class)
                .setMethodName("AppLoginForZjrd")
                .addParams("UserName", "master")
                .addParams("Password", "jhit3617857")
                .addParams("type", "Android")
                .addParams("verify", Constants.VERIFY));
        map.put("VersionInfo", new KsoapHelpler<>(VersionInfo.class)
                .setMethodName("GetWebAppVersion")
                .addParams("type", "Android")
                .addParams("verify", Constants.VERIFY));

        return map;
    }

    private String jsonString(String s){//过滤双引号为中文双引号
        char[] temp = s.toCharArray();
        String newJsonStr = "";
        int n = temp.length;
        for(int i =0;i<n;i++){
            if(temp[i]==':'&&temp[i+1]=='"'){
                for(int j =i+2;j<n;j++){
                    if(temp[j]=='"'){
                        if(temp[j+1]!=',' &&  temp[j+1]!='}'){
                            temp[j]='”';
                        }else if(temp[j+1]==',' ||  temp[j+1]=='}'){
                            break ;
                        }
                    }
                }
            }
        }
        newJsonStr = String.valueOf(temp);
        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        Matcher m = p.matcher(newJsonStr);
        newJsonStr = m.replaceAll("");
        return newJsonStr;
    }
}
