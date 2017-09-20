package com.juhua.hangfen.eedsrd.model;

import android.app.Application;

import com.juhua.hangfen.eedsrd.application.ThisApplication;
import com.juhua.hangfen.eedsrd.tools.NetworkUtils;

/**
 * Created by congj on 2017/9/13.
 */

public class GetData<T> {
    private  boolean success;
    private  int errorCode;
    private String errorDesc;
    private T data;

    public GetData(){
        this.setSuccess(true);
        this.setErrorCode(400);
        this.setErrorDesc("未知错误！");
        if(!NetworkUtils.isNetworkAvailable(ThisApplication.getContext())){
            this.setSuccess(false);
            this.setErrorDesc("您似乎已经断开网络！");
            this.setErrorCode(401);
        }
    }

    public void loadSuccess(){
        this.setSuccess(true);
        this.setErrorCode(200);
        this.setErrorDesc("成功");
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDesc() {
        return errorDesc;
    }

    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
