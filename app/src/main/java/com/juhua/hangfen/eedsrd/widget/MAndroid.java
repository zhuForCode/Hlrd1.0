package com.juhua.hangfen.eedsrd.widget;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.juhua.hangfen.eedsrd.R;
import com.juhua.hangfen.eedsrd.activity.AboutActivity;
import com.juhua.hangfen.eedsrd.activity.LoginActivity;
import com.juhua.hangfen.eedsrd.activity.MainActivity;
import com.juhua.hangfen.eedsrd.activity.WebActivity;
import com.juhua.hangfen.eedsrd.application.AppCache;
import com.juhua.hangfen.eedsrd.constants.Constants;
import com.juhua.hangfen.eedsrd.tools.AppManager;
import com.juhua.hangfen.eedsrd.util.ToastUtils;

import java.net.URLEncoder;

import static com.juhua.hangfen.eedsrd.constants.Constants.RESULT_CONTACT_CONFIRM;

/**
 * Created by congj on 2017/10/2.
 */

public class MAndroid extends WebActivity{

    @JavascriptInterface
    public void toLogin(){//退回到登陆页面
        AppCache.clearStack();
        Intent intent = new Intent(AppCache.getContext(), LoginActivity.class);
        //     AppManager.getAppManager().finishAllActivity();
        //   finish();
        startActivity(intent);

    }
    @JavascriptInterface
    public void tokenOut(){//退回到登陆页面
        AlertDialog.Builder builder = new AlertDialog.Builder(AppCache.getContext());
        builder.setIcon(getResources().getDrawable(R.drawable.ic_error_outline_black));
        builder.setTitle("用户信息已过期！");
        builder.setMessage("请重新登陆！可能的原因：\n1.用户半小时内无有效操作\n2.账号在其他手机上被登陆\n3.服务器升级维护");
        builder.setCancelable(false);
        builder.setPositiveButton("重新登陆", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AppCache.clearStack();
                Intent intent = new Intent(AppCache.getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        builder.create().show();
    }
    @JavascriptInterface
    public void toAbout(){//前往关于界面
        Intent intent = new Intent(AppCache.getContext(), AboutActivity.class);
        startActivity(intent);
    }

    @JavascriptInterface//返回main.aspx,即结束指定数目的活动
    public void toMain(int count){
        //  AppManager.getAppManager().finishCountActivity(count);
    }
    @JavascriptInterface//返回上一页（活动）
    public void toLast(){
        AppManager.getAppManager().finishActivity();//这边不能直接用finish()，出栈要清除栈的记录
    }

    @JavascriptInterface
    public void forToast(String tContent){
        //   Toast toast = Toast.makeText(MainActivity.this, tContent, Toast.LENGTH_SHORT);
        ToastUtils.show(tContent);
        //   toast.show();
    }
    @JavascriptInterface
    public void backToHome(){
        AppManager.getAppManager().finishActivity();//这边不能直接用finish()，出栈要清除栈的记录
    }

    @JavascriptInterface
    public  void newWeb(String url){
        if(url.contains("nav=new")){
            Intent intent = new Intent(AppCache.getContext(), WebActivity.class);
            String u = url.split("LzptApp/")[1];
            try {
                intent.putExtra("actionUrl", URLEncoder.encode(u, "utf-8"));
            }catch (Exception e){
                intent.putExtra("actionUrl", u);
            }

            intent.putExtra("Token", AppManager.getAppManager().currentActivity().getIntent().getExtras().getString("Token"));
            AppCache.getContext().startActivity(intent);
        }

    }

    @JavascriptInterface
    public void newResponseWeb(String url, String formData, int responseCode){
        if(url.contains("nav=new")){
            Intent intent = new Intent(AppCache.getContext(), WebActivity.class);
            String u = url.split("LzptApp/")[1];
            try {
                intent.putExtra("actionUrl", URLEncoder.encode(u, "utf-8"));
            }catch (Exception e){
                intent.putExtra("actionUrl", u);
            }
            intent.putExtra("FormData", formData);
            intent.putExtra("Token", AppManager.getAppManager().currentActivity().getIntent().getExtras().getString("Token"));
            AppManager.getAppManager().currentActivity().startActivityForResult(intent, responseCode);
        }
    }

    @JavascriptInterface
    public void setContact(String contact){
        Intent intent  = new Intent();
        intent.putExtra("contact", contact);
        AppManager.getAppManager().currentActivity().setResult(RESULT_CONTACT_CONFIRM, intent);
        AppManager.getAppManager().finishActivity();
    }


}
