package com.juhua.hangfen.eedsrd.webservice;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedHashTreeMap;
import com.juhua.hangfen.eedsrd.constants.Constants;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by JiaJin Kuai on 2017/3/22.
 */

public class KsoapHelpler<T> {
    private Class<T> mClass;
    private LinkedHashMap<String, String> paramMap;
    private String methodName;
    private String modelName;
    private Gson mGson;

    public KsoapHelpler (Class<T> clazz) {
        this.mClass = clazz;
        this.modelName = clazz.getName();
        mGson = new Gson();
    }
    public KsoapHelpler params(LinkedHashMap<String, String> params)
    {
        this.paramMap = params;
        return this;
    }
    public KsoapHelpler addParams(String reqKey, String reqStr){
        if (this.paramMap == null)
        {
            paramMap = new LinkedHashMap<>();
        }
        paramMap.put(reqKey, reqStr);
        return this;
    }
    public KsoapHelpler methodName(String mth) {
        this.methodName = mth;
        return this;
    }
    public KsoapHelpler setMethodName(String methodN){
        methodName = methodN;
        return this;
    }

    public KsoapHelpler model(String modelN) {
        this.modelName = modelN;
        return this;
    }
    public KsoapHelpler setModelName(String modeN){
        modelName = modeN;
        return this;
    }

    public T getSingleModel(){
        String jsonStr = "";
        SoapObject request = new SoapObject(Constants.NAME_SPACE, methodName);
        for (String key : paramMap.keySet())
        {
            request.addProperty(key, paramMap.get(key));
        }
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER10);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE ht = new HttpTransportSE(Constants.WSDL, 8000);
        try {
            ht.call(Constants.NAME_SPACE+methodName, envelope);
            SoapPrimitive object = (SoapPrimitive) envelope.getResponse();
            jsonStr = object.toString();
        } catch (IOException e) {
            jsonStr = "{"
                    + "\"errorCode\":404,"
                    + "\"errorDesc\":\"" + e.toString() + "\"}";
            e.printStackTrace();
        }catch (XmlPullParserException e) {
            jsonStr = "{"
                    + "\"errorCode\":401,"
                    + "\"errorDesc\":\"" + e.toString() + "\"}";
            e.printStackTrace();
        }catch (Exception e){
            jsonStr = "{"
                    + "\"errorCode\":400,"
                    + "\"errorDesc\":\"" + e.toString() + "\"}";
            e.printStackTrace();
        }
        jsonStr = jsonString(jsonStr);
        Log.d("jsonStr", jsonStr);
        try {
            return mGson.fromJson(jsonStr, mClass);
        }catch (Exception e){
            if(jsonStr != null){
                jsonStr = "{"
                        + "\"RESPONSECODE\":1,"
                        + "\"RESPONSEDESC\":\"" + jsonStr + "\"}";
            }else{
                jsonStr = "{"
                        + "\"errorCode\":400,"
                        + "\"errorDesc\":\"" + e.toString() + "\"}";
            }
            jsonStr = jsonString(jsonStr);
            return mGson.fromJson(jsonStr, mClass);
        }

    }

    public T getListModel(){
        String jsonStr = "";
        SoapObject request = new SoapObject(Constants.NAME_SPACE, methodName);
        for (String key : paramMap.keySet())
        {
            request.addProperty(key, paramMap.get(key));
        }
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER10);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE ht = new HttpTransportSE(Constants.WSDL, 8000);
        try {
            ht.call(Constants.NAME_SPACE+methodName, envelope);
            SoapPrimitive object = (SoapPrimitive) envelope.getResponse();
            jsonStr = object.toString();
            jsonStr = jsonString(jsonStr);
            jsonStr = "{\"" + modelName + "\":["
                    + jsonStr + "]}";
            Log.d("jsonStr", jsonStr);
        } catch (IOException e) {
            jsonStr = "\"" + modelName + ":[{"
                    + "\"errorCode\":404,"
                    + "\"errorDesc\":\"" + e.toString() + "\"}]";
            e.printStackTrace();
        }catch (XmlPullParserException e) {
            jsonStr = "\"" + modelName + ":[{"
                    + "\"errorCode\":401,"
                    + "\"errorDesc\":\"" + e.toString() + "\"}]";
            e.printStackTrace();
        }catch (Exception e){
            jsonStr = "\"" + modelName + ":[{"
                    + "\"errorCode\":400,"
                    + "\"errorDesc\":\"" + e.toString() + "\"}]";
            e.printStackTrace();
        }
        jsonStr = jsonString(jsonStr);
        Log.d("jsonStr", jsonStr);
        return mGson.fromJson(jsonStr, mClass);
    }

    public T getMultipleModel(){
        String jsonStr = "";
        SoapObject request = new SoapObject(Constants.NAME_SPACE, methodName);
        for (String key : paramMap.keySet())
        {
            request.addProperty(key, paramMap.get(key));
        }
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER10);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE ht = new HttpTransportSE(Constants.WSDL, 8000);
        try {
            ht.call(Constants.NAME_SPACE+methodName, envelope);
            SoapPrimitive object = (SoapPrimitive) envelope.getResponse();
            jsonStr = object.toString();
            jsonStr = jsonString(jsonStr);
            jsonStr = "{\"" + modelName + "\":["
                    + jsonStr + "]}";
            Log.d("jsonStr", jsonStr);
        } catch (IOException e) {
            jsonStr = "\"" + modelName + ":[{"
                    + "\"errorCode\":404,"
                    + "\"errorDesc\":\"" + e.toString() + "\"}]";
            jsonStr = jsonString(jsonStr);
            e.printStackTrace();
        }catch (XmlPullParserException e) {
            jsonStr = "\"" + modelName + ":[{"
                    + "\"errorCode\":401,"
                    + "\"errorDesc\":\"" + e.toString() + "\"}]";
            jsonStr = jsonString(jsonStr);
            e.printStackTrace();
        }catch (Exception e){
            jsonStr = "\"" + modelName + ":[{"
                    + "\"errorCode\":400,"
                    + "\"errorDesc\":\"" + e.toString() + "\"}]";
            jsonStr = jsonString(jsonStr);
            e.printStackTrace();
        }
        return mGson.fromJson(jsonStr, mClass);
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
