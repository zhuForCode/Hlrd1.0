package com.juhua.hangfen.bzrd.model;

import android.graphics.Bitmap;
import android.util.Log;

import com.juhua.hangfen.bzrd.R;
import com.juhua.hangfen.bzrd.application.AppManager;
import com.juhua.hangfen.bzrd.application.ThisApplication;
import com.juhua.hangfen.bzrd.util.ImageUtils;

/**
 * Created by congj on 2017/9/13.
 */

public class HomeButton {
    private  int id;
    private String name;
    private String iconUrl;
    private String actionUrl;
    private int order;
    private Bitmap iconImage;
    public HomeButton(int id, String name, String iconUrl, String actionUrl){
        this.id = id;
        this.name = name;
        this.iconUrl = iconUrl;
        this.actionUrl = actionUrl;
        this.iconImage = ImageUtils.drawableToBitmap(AppManager.getContext().getResources().getDrawable(ImageUtils.getResourceByReflect(iconUrl)));


    }
    public HomeButton(String name, String iconUrl, String actionUrl){
        this.name = name;
        this.iconUrl = iconUrl;
        this.actionUrl = actionUrl;

        this.iconImage = ImageUtils.drawableToBitmap(AppManager.getContext().getResources().getDrawable(R.drawable.ic_home_default));

        this.iconImage = ImageUtils.resizeBitmap(this.iconImage, 150, 150);

    }
    public void getIconFromUrl(){
        try {
            this.iconImage = ImageUtils.getHttpBitmap(this.getIconUrl());
            this.iconImage = ImageUtils.resizeBitmap(this.iconImage, 150, 150);
        }catch (Exception e){
            Log.d("fasdfasdf", e.toString());
        }
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Bitmap getIconImage() {
        return iconImage;
    }

    public void setIconImage(Bitmap iconImage) {
        this.iconImage = iconImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
