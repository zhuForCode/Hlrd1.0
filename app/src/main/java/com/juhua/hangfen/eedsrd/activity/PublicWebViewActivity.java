package com.juhua.hangfen.eedsrd.activity;

import android.view.KeyEvent;
import android.widget.Toast;

import com.juhua.hangfen.eedsrd.tools.AppManager;


/**
 * Created by JiaJin Kuai on 2017/1/21.
 */

public class PublicWebViewActivity extends MainActivity{
    @Override
    public void loadURL(){
        try {
            webView.loadUrl(getIntent().getExtras().getString("url").toString());
        }catch (Exception e){
            webView.loadUrl("http://www.baidu.com");//加载错误页面
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();// 返回前一个页面
            return true;
        }else{
          //  finish();
            AppManager.getAppManager().finishActivity();
            return true;
        }
        //   return super.onKeyDown(keyCode, event);
    }

}
