package com.juhua.hangfen.eedsrd.webservice;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.juhua.hangfen.eedsrd.constants.Constants;
import com.juhua.hangfen.eedsrd.model.UserInfo;
import com.juhua.hangfen.eedsrd.model.VersionInfo;

import java.util.HashMap;
import java.util.LinkedHashMap;


/**
 * Created by JiaJin Kuai on 2017/4/1.
 */

public class GetModelAsync extends AsyncTask<String[], Void, Object>{
    private KsoapHelpler ksoapHelpler;
    private short modelType;
    private UpdateUI updateUI;
    private LinkedHashMap<String, KsoapHelpler> ksoapMap;
    public GetModelAsync setKsoapHelpler(KsoapHelpler k){
        this.ksoapHelpler = k;
        return this;
    }

    public GetModelAsync addKsoapHelpler(String reqKey, KsoapHelpler k){
        if (this.ksoapMap == null)
        {
            ksoapMap = new LinkedHashMap<>();
        }
        ksoapMap.put(reqKey, k);
        return this;
    }

    public GetModelAsync setUpdateUI(UpdateUI u){
        this.updateUI = u;
        return this;
    }
    @Override
    public void onPreExecute() {
    }
    @Override
    public Object doInBackground(String[]... params){
        switch (modelType){
            case Constants.MODEL_TYPE_SINGLE:
                return ksoapHelpler.getSingleModel();
            case Constants.MODEL_TYPE_LIST:
                return ksoapHelpler.getListModel();
            case Constants.MODEL_TYPE_MULTIPLE:
                return getMultipleModel();
            default:
                return ksoapHelpler.getSingleModel();
        }

    }

    @Override
    public void onPostExecute(Object obj) {
        Log.d("onPostExecute", "result");
        updateUI.onResponse(obj);
    }

    private LinkedHashMap getMultipleModel(){
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        for (String key : ksoapMap.keySet())
        {
            map.put(key, ksoapMap.get(key).getSingleModel());
        }
        return map;
    }

    public short getModelType() {
        return modelType;
    }

    public GetModelAsync setModelType(short modelType) {
        this.modelType = modelType;
        return this;
    }
}
