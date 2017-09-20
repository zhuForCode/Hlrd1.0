package com.juhua.hangfen.eedsrd.webservice;

import android.os.AsyncTask;
import android.util.Log;

import java.util.HashMap;
import java.util.List;

/**
 * Created by congj on 2017/9/13.
 */

public class SoapAsync extends AsyncTask<String[], Void, Object> {
    private UpdateUI updateUI;
    private SoapHelper soapHelper;
    private List<SoapHelper> actionLists;
    public SoapAsync(SoapHelper soapHelper){
        this.soapHelper = soapHelper;
    }

    public SoapAsync(List<SoapHelper> soapHelpers){
        this.actionLists = soapHelpers;
    }

    public SoapAsync addSoapHelper(SoapHelper soapHelper){
        this.actionLists.add(soapHelper);
        return this;
    }
    public SoapAsync setUI(UpdateUI u){
        this.updateUI = u;
        return this;
    }
    @Override
    public void onPreExecute() {
    }
    @Override
    public Object doInBackground(String[]... params){
        HashMap<String, Object> objectLists = new HashMap<>();
        if(this.actionLists != null && this.actionLists.size() != 0){
            for (SoapHelper action: this.actionLists) {
                action.Request();
                objectLists.put(action.getMethod(), action.getResult());
            }
        }else{
            this.soapHelper.Request();
            objectLists.put(this.soapHelper.getMethod(), this.soapHelper.getResult());
        }
        return  objectLists;

    }

    @Override
    @SuppressWarnings("unchecked")
    public void onPostExecute(Object obj) {
        updateUI.onResponse(obj);
    }


}
