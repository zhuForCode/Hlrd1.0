package com.juhua.hangfen.bzrd.model;

/**
 * Created by kuai on 2017/1/3.
 */

public class UpdateInfo
{
    private String version = "";
    private String description = "";
    private String url = "";
    private String permission = "";
    public String getVersion()
    {
        return version;
    }
    public void setVersion(String version)
    {
        this.version = version;
    }
    public String getDescription()
    {
        return description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }
    public String getUrl()
    {
        return url;
    }
    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}
