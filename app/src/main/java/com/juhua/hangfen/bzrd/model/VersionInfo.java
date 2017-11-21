package com.juhua.hangfen.bzrd.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by JiaJin Kuai on 2017/4/17.
 */

public class VersionInfo {
    @SerializedName("errorCode")
    private short errorCode;
    @SerializedName("errorDesc")
    private String errorDesc;
    @SerializedName("RESPONSECODE")
    private short RESPONSECODE;
    @SerializedName("RESPONSEDESC")
    private String RESPONSEDESC;
    @SerializedName("Permission")
    private String Permission;
    @SerializedName("version")
    private String version;
    @SerializedName("description")
    private String description;
    @SerializedName("url")
    private String url;

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

    public String getPermission() {
        return Permission;
    }

    public void setPermission(String permission) {
        Permission = permission;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
