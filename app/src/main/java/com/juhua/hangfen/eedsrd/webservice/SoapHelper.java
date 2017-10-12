package com.juhua.hangfen.eedsrd.webservice;

import android.util.Log;

import com.google.gson.internal.LinkedTreeMap;
import com.juhua.hangfen.eedsrd.constants.Constants;
import com.juhua.hangfen.eedsrd.model.GetData;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by congj on 2017/9/13.
 */

public class SoapHelper {
    private LinkedHashMap<String, String> params;
    private String method;
    private GetData<String> result;
    private String wsdl = Constants.WSDL;;

    public  SoapHelper(){
        this.setResult(new GetData<String>());
    }

    public SoapHelper(String method){
        this.setParams(new LinkedHashMap<String, String>());
        this.setMethod(method);
        this.setResult(new GetData<String>());
    }

    public SoapHelper(LinkedHashMap<String, String> params, String method){
        this.setParams(params);
        this.setMethod(method);
        this.setResult(new GetData<String>());
    }

    public SoapHelper methodName(String method){
        this.setMethod(method);
        return this;
    }

    public SoapHelper setWsdl(String wsdl){
        this.wsdl = wsdl;
        return this;
    }


    public SoapHelper addParams(String reqKey, String reqStr){
        if (this.params == null)
        {
            this.setParams(new LinkedHashMap<String, String>());
        }
        this.params.put(reqKey, reqStr);
        return this;
    }

    public SoapHelper Request(){
        requestSoap();
        return this;
    }

    public LinkedHashMap<String, String> getParams() {
        return params;
    }

    public void setParams(LinkedHashMap<String, String> params) {
        this.params = params;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public GetData<String> getResult() {
        return result;
    }

    public void setResult(GetData<String> result) {
        this.result = result;
    }

    private void requestSoap(){
        SoapObject request = new SoapObject(Constants.NAME_SPACE, this.getMethod());
        for (String key : this.params.keySet())
        {
            request.addProperty(key, this.params.get(key));
        }
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE ht = new HttpTransportSE(this.wsdl, Constants.REQUEST_TIMEOUT);
        try {
            ht.call(Constants.NAME_SPACE+this.getMethod(), envelope);
            SoapPrimitive object = (SoapPrimitive) envelope.getResponse();
            Log.d("SoapResponse", object.toString());
            this.result.loadSuccess();
            this.result.setData(jsonString(object.toString()));
        }catch (Exception e){
            Log.d("SoapResponse", e.toString());
            this.result.setSuccess(false);
            this.result.setErrorCode(410);
            this.result.setErrorDesc(e.getMessage());
        }
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
