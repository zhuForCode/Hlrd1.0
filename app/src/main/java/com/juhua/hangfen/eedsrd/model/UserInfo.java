package com.juhua.hangfen.eedsrd.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by JiaJin Kuai on 2017/4/7.
 */

public class UserInfo {
    @SerializedName("errorCode")
    private short errorCode;
    @SerializedName("errorDesc")
    private String errorDesc;
    @SerializedName("RESPONSECODE")
    private short RESPONSECODE;
    @SerializedName("RESPONSEDESC")
    private String RESPONSEDESC;
    @SerializedName("USERID")
    private String USERID;
    @SerializedName("姓名")
    private String 姓名;
    @SerializedName("账号")
    private String 账号;
    @SerializedName("手机号码")
    private String 手机号码;
    @SerializedName("ACCESSTOKEN")
    private String ACCESSTOKEN;

    public short getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(short errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDesc() {
        return errorDesc;
    }

    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }

    public short getRESPONSECODE() {
        return RESPONSECODE;
    }

    public void setRESPONSECODE(short RESPONSECODE) {
        this.RESPONSECODE = RESPONSECODE;
    }

    public String getRESPONSEDESC() {
        return RESPONSEDESC;
    }

    public void setRESPONSEDESC(String RESPONSEDESC) {
        this.RESPONSEDESC = RESPONSEDESC;
    }

    public String get姓名() {
        return 姓名;
    }

    public void set姓名(String 姓名) {
        this.姓名 = 姓名;
    }

    public String getUSERID() {
        return USERID;
    }

    public void setUSERID(String USERID) {
        this.USERID = USERID;
    }

    public String get账号() {
        return 账号;
    }

    public void set账号(String 账号) {
        this.账号 = 账号;
    }

    public String get手机号码() {
        return 手机号码;
    }

    public void set手机号码(String 手机号码) {
        this.手机号码 = 手机号码;
    }

    public String getACCESSTOKEN() {
        return ACCESSTOKEN;
    }

    public void setACCESSTOKEN(String ACCESSTOKEN) {
        this.ACCESSTOKEN = ACCESSTOKEN;
    }
}
