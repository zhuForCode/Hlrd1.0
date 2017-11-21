package com.juhua.hangfen.bzrd.tools;

/**
 * Created by JiaJin Kuai on 2016/9/28.
 */

import android.util.Log;

import com.juhua.hangfen.bzrd.webservice.SSLConnection;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;


/**
 * Json工具类，实现JSON与Java Bean的互相转换
 * User: jiajinkuai
 * <span style="font-family: Arial, Helvetica, sans-serif;">2015年4月3日上午10:42:19</span>
 */

public class JsonUtils {
    public String returnData(String targetNameSpace, String method,String WSDL, String[] prtRequest, String[] prtRequestPut) {
        String jsonData = "";
        SoapObject request = new SoapObject(targetNameSpace, method);
        int count = prtRequest.length;
        for (int i = 0; i < count; i++){
            request.addProperty(prtRequest[i], prtRequestPut[i]);
        }
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER12);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        SSLConnection.allowAllSSL();
        HttpTransportSE ht = new HttpTransportSE(WSDL, 8000);
        try {
            ht.call(targetNameSpace+method, envelope);
            SoapPrimitive object = (SoapPrimitive) envelope.getResponse();
            jsonData = object.toString();
        } catch (IOException e) {
            e.printStackTrace();
            jsonData = "404";
        }catch (XmlPullParserException e) {
            jsonData = "404";
            e.printStackTrace();
        }catch (Exception e){
            jsonData = "404";
            e.printStackTrace();
        }
        return jsonData;
    }

    public HashMap<String, String> returnObject(String targetNameSpace, String method,String WSDL, String[] prtRequest, String[] prtRequestPut, String[] properties) {
        HashMap<String, String> map = new HashMap<String, String>();
        SoapObject request = new SoapObject(targetNameSpace, method);
        int count = prtRequest.length;
        for (int i = 0; i < count; i++){
            request.addProperty(prtRequest[i], prtRequestPut[i]);
        }
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER10);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE ht = new HttpTransportSE(WSDL);
        try {
            ht.call(targetNameSpace+method, envelope);
            SoapObject result = (SoapObject)envelope.getResponse();
            if (result == null){
                Log.d("kjj-error","账号或密码输入错误");
            }else{
                result = (SoapObject)result.getProperty(1);
                result = (SoapObject)result.getProperty(0);
                for(int i=0;  i< result.getPropertyCount(); i++ ) {
                    SoapObject soap = (SoapObject) result.getProperty(i);
                    int mCount = properties.length;
                    for (int m = 0; m < mCount; m++){
                        map.put(properties[m], soap.getProperty(properties[m]).toString());
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return map;
    }

    public HashMap<String, Object> Analysis(String jsonStr, String[] properties)
            throws JSONException {
        /******************* 解析 ***********************/
        JSONObject jsonObject = new JSONObject(jsonStr);
        HashMap<String, Object> map = new HashMap<String, Object>();
        int count = properties.length;
        for (int i = 0; i < count; i++){
            map.put(properties[i], jsonObject.getString(properties[i]));
        }
        return map;
    }
    public static String jsonString(String s){//过滤双引号为中文双引号
        char[] temp = s.toCharArray();
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
        return new String(temp);
    }

}

