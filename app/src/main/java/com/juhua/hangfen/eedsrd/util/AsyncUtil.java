package com.juhua.hangfen.eedsrd.util;

import android.os.AsyncTask;
import android.util.Log;

import com.juhua.hangfen.eedsrd.model.HomeButton;
import com.juhua.hangfen.eedsrd.webservice.SoapAsync;
import com.juhua.hangfen.eedsrd.webservice.SoapHelper;
import com.juhua.hangfen.eedsrd.webservice.TSoap;
import com.juhua.hangfen.eedsrd.webservice.UpdateUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by congj on 2017/9/14.
 */

public class AsyncUtil extends AsyncTask<String[], Void, Object> {
    private TSoap tSoap;
    private Object objLists;
    public AsyncUtil(){

    }
    public  AsyncUtil addObect(Object objLists){
        this.objLists = objLists;
        return this;
    }

    public AsyncUtil(Object objects){
        this.objLists = objects;
    }

    public AsyncUtil setEvents(TSoap tSoap){
        this.tSoap = tSoap;
        return this;
    }

    @Override
    public void onPreExecute() {
        Log.d("kuaijiajin", "8");
    }
    @Override
    public Object doInBackground(String[]... params){
        Log.d("kuaijiajin", "9");
        this.tSoap.onRequest(this.objLists);
        return this.objLists;

    }

    @Override
    @SuppressWarnings("unchecked")
    public void onPostExecute(Object obj) {
        this.tSoap.onResponse(obj);
    }
}
