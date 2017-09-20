package com.juhua.hangfen.eedsrd.activity;

/**
 * Created by kuai on 2016/12/26.
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.juhua.hangfen.eedsrd.R;
import com.juhua.hangfen.eedsrd.application.AppCache;
import com.juhua.hangfen.eedsrd.constants.Constants;
import com.juhua.hangfen.eedsrd.model.UpdateInfo;
import com.juhua.hangfen.eedsrd.tools.AppContext;
import com.juhua.hangfen.eedsrd.tools.CryptoTools;
import com.juhua.hangfen.eedsrd.tools.DialogUtil;
import com.juhua.hangfen.eedsrd.tools.JsonUtils;
import com.juhua.hangfen.eedsrd.util.ToastUtils;
import com.juhua.hangfen.eedsrd.webservice.SSLConnection;

import org.json.JSONException;
import java.util.HashMap;

public class LoginActivity extends Activity {
    JsonUtils jsonUtils = new JsonUtils();
    AppContext appContext = new AppContext();
    private EditText edtUser;
    private EditText edtPw;
    private Button btnClear;
    private Button btnClear1;
    private CheckBox ckBRem;
    private TextView ckBTv;

    private Button btnLogin;
    private Button btnGetNewPwd;
    private ProgressDialog dialog;
    private final String FILE = "saveUserNamePwd";
    private SharedPreferences sp = null;
    private static String Verify;
    private String Token;
    private int Permission = 0;
    private static String UserName;
    private static String PassWord;
    private static Boolean isCkRemeber;
    private static String jsonStr;
    static String YES = "yes";
    static String NO = "no";

    // 更新版本要用到的一些信息
    private UpdateInfo info;
    private ProgressDialog dialogP;//检查更新等待条
    private UpdateInfoService updateInfoService;
    @Override
    protected void onDestroy() {
        AppCache.removeFromStack(this);
        super.onDestroy();
        Log.i("kjj", "onDestroy:" + getClass().getSimpleName());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCache.addToStack(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setSystemBarTransparent();
        setContentView(R.layout.activity_login);
        init();
        initData();
        if (appContext.isNetworkConnected(LoginActivity.this)) {
           checkUpdate();
        }else {
            DialogUtil.showDialog(LoginActivity.this, "请检查网络连接！", true);
        }
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    if (appContext.isNetworkConnected(LoginActivity.this)){
                        if(Permission == 1){
                            DialogUtil.showDialog(LoginActivity.this, "请更新到最新版本后再使用！", true);
                            return;
                        }
                        dialog = ProgressDialog.show(LoginActivity.this, "", "正在登录...") ;
                        new Thread(new Runnable(){
                            @Override
                            public void run() {
                                if (Checking()) {
                                    remember();
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    String[] propreties = {"USERID", "姓名", "帐号", "手机号码","固定电话","地区", "ACCESSTOKEN"};
                                    try {
                                        //  userID = jsonUtils.Analysis(jsonStr, propreties).get("USERID").toString();
                                        //   UName =  jsonUtils.Analysis(jsonStr, propreties).get("姓名").toString();
                                        Token = jsonUtils.Analysis(jsonStr, propreties).get("ACCESSTOKEN").toString();
                                    } catch (JSONException e){
                                        e.printStackTrace();
                                    }
                                    intent.putExtra("Token", Token);
                                    finish();
                                    startActivity(intent);

                                }
                            }

                        }).start();

                    }else {
                        DialogUtil.showDialog(LoginActivity.this, "请检查网络连接！", true);
                    }

                }


            }
        });

             btnGetNewPwd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                final String mobile = edtUser.getText().toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, AlertDialog.THEME_HOLO_LIGHT);
                builder.setIcon(getResources().getDrawable(R.drawable.ic_lock_open_black));
                builder.setTitle("找回密码");
                builder.setMessage("新密码将以短信的形式发送到" + mobile + "手机上，请注意查收！");
                builder.setCancelable(true);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mobile.isEmpty() || mobile.equals("")){
                            DialogUtil.showDialog(LoginActivity.this, "账号不能为空！", true);
                        }else{
                            if (appContext.isNetworkConnected(LoginActivity.this)){
                                String verify = null;
                                try{
                                    CryptoTools cryptoTools = new CryptoTools();
                                    verify = cryptoTools.returnVerify();
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                                String[] prtRequest= {"mobile", "verify"};
                                String[] prtRequestPut= {mobile, Constants.VERIFY};
                                String jsonStr = jsonUtils.returnData(Constants.NAME_SPACE, Constants.METHOD_GET_BACK_PASSWORD, Constants.WSDL, prtRequest, prtRequestPut);
                                if(jsonStr.equals("404")){
                                 //   Toast.makeText(LoginActivity.this, "服务器响应异常！", Toast.LENGTH_SHORT).show();
                                    ToastUtils.show("服务器响应异常！");
                                }else{
                                    ToastUtils.show(jsonStr);
                                  //  Toast.makeText(LoginActivity.this, jsonStr, Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                DialogUtil.showDialog(LoginActivity.this, "请检查网络连接！", true);
                            }
                        }

                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog ad = builder.create();
                ad.show();
                ad.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.holo_blue_light));
                ad.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.common_dark_shadow));
            }
        });

    }
    private void setSystemBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // LOLLIPOP解决方案
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // KITKAT解决方案
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
    protected void init(){
        if (ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    5);
            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {

            }
        }
        edtUser = (EditText) findViewById(R.id.userName_editText);
        edtPw = (EditText) findViewById(R.id.passWord_editText);
        ckBRem = (CheckBox) findViewById(R.id.rem_checkBox);
        ckBTv = (TextView) findViewById(R.id.textView);
        btnClear = (Button) findViewById(R.id.button_clear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtUser.setText("");
            }
        });
        btnClear1 = (Button) findViewById(R.id.button_clear1);
        btnClear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtPw.setText("");
            }
        });
        btnLogin = (Button) findViewById(R.id.sign_in_button);
        btnGetNewPwd = (Button) findViewById(R.id.forgetPwd_Btn);
        sp = getSharedPreferences(FILE, MODE_PRIVATE);
        String isMemory = sp.getString("isMemory", NO);
        isCkRemeber = sp.getBoolean("isCkRemeber", false);
        try {
            if (isMemory.equals(YES)) {
                CryptoTools cryptoTools = new CryptoTools();
                UserName = cryptoTools.returnDecode(sp.getString("name", ""));
                PassWord = cryptoTools.returnDecode(sp.getString("password", ""));
            //    Log.d("kjj_1",sp.getString("name", ""));
               // Log.d("kjj_2",sp.getString("password", ""));
                edtUser.setText(UserName);
                edtPw.setText(PassWord);
                ckBRem.setChecked(true);
            }
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(UserName, edtUser.toString());
            editor.putString(PassWord, edtPw.toString());
            editor.apply();

        }catch (Exception e){
            e.printStackTrace();
        }
        edtUser.addTextChangedListener(mTextWatcher);
        edtPw.addTextChangedListener(mTextWatcherPw);
        edtUser.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    // 此处为得到焦点时的处理内容
                    if (edtUser.getText().toString() != null && !edtUser.getText().toString().equals("")) {
                        btnClear.setVisibility(View.VISIBLE);
                    } else {
                        btnClear.setVisibility(View.INVISIBLE);
                    }
                } else {
                    // 此处为失去焦点时的处理内容
                    btnClear.setVisibility(View.INVISIBLE);
                }
            }
        });
        edtPw.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    // 此处为得到焦点时的处理内容
                    if (!edtPw.getText().toString().equals("")) {
                        btnClear1.setVisibility(View.VISIBLE);
                    } else {
                        btnClear1.setVisibility(View.INVISIBLE);
                    }
                } else {
                    // 此处为失去焦点时的处理内容
                    btnClear1.setVisibility(View.INVISIBLE);
                }
            }
        });

        ckBTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ckBRem.setChecked(!ckBRem.isChecked());
            }
        });

    }

    //此方法只是关闭软键盘
    private void hintKbTwo() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()&&getCurrentFocus()!=null){
            if (getCurrentFocus().getWindowToken()!=null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    TextWatcher mTextWatcher  = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!edtUser.getText().toString().equals("")) {
                btnClear.setVisibility(View.VISIBLE);
            } else {
                btnClear.setVisibility(View.INVISIBLE);
            }
        }
    };
    TextWatcher mTextWatcherPw  = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!edtPw.getText().toString().equals("")) {
                btnClear1.setVisibility(View.VISIBLE);
            } else {
                btnClear1.setVisibility(View.INVISIBLE);
            }
        }
    };
    protected void initData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                SSLConnection.httpGet();;
            }
        }).start();
    }
    // 对用户输入的用户名、密码进行校验
    private boolean validate() {
        String username = edtUser.getText().toString().trim();
        if (username.equals("")) {
            DialogUtil.showDialog(LoginActivity.this, "用户名或密码不能为空！", true);
            return false;
        }
        String pwd = edtPw.getText().toString().trim();
        if (pwd.equals("")) {
            DialogUtil.showDialog(LoginActivity.this, "用户名或密码不能为空！", true);
            return false;
        }
        return true;
    }
    private boolean Checking() {
        UserName = edtUser.getText().toString();
        PassWord = edtPw.getText().toString();
        try {
            int n = CheckLogin();
            if (n == -1) {
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.apply();
                Looper.prepare();
                DialogUtil.showDialog(this, "用户名或密码错误！", false);
                Looper.loop();
                return false;
            }else if(n == -3){
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.apply();
                Looper.prepare();
                DialogUtil.showDialog(this, "您一个小时内输入的帐号密码超过上限，请等待45分钟后再试!", false);
                Looper.loop();
                return false;
            }else if(n == -2 || n == 0){
                Looper.prepare();
                DialogUtil.showDialog(this, "服务器响应异常，请重新尝试或与管理员联系！", false);
              //  DialogUtil.showDialog(this, "服务器响应异常！请重新尝试或与管理员联系！", false);
                Looper.loop();
            }

        } catch (Exception e) {
            return false;
        }finally {
            dialog.dismiss();
        }
        return true;

    }
    //登陆校验
    public int CheckLogin() {
        int n = 0;
        String[] prtRequest= {"UserName", "Password","type","verify"};
        String[] prtRequestPut= {UserName, PassWord, "android", Constants.VERIFY};
        try {
            jsonStr = jsonUtils.returnData(Constants.NAME_SPACE, Constants.METHOD_APP_LOGIN, Constants.WSDL, prtRequest, prtRequestPut);
            if (jsonStr.equals("err:账号或密码有误！")){ //err：账号或密码有误！
                n=-1;
            }else if(jsonStr == "404" || jsonStr.equals("err:异常")) {
                n=-2; //服务器响应异常
            }else if(jsonStr.equals("err:您一个小时内输入的帐号密码超过上限，请等待45分钟后再试!")) {
                n=-3; //密码验证次数超过8次
            }else{
                n=1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            dialog.dismiss();
        }
        return n;
    }
    //SharedPerferances存储用户名和密码
    public void remember() {
        if (ckBRem.isChecked()) {
            isCkRemeber = true;
            if (sp == null) {
                sp = getSharedPreferences(FILE, MODE_PRIVATE);
            }
            SharedPreferences.Editor edit = sp.edit();
            try{
                CryptoTools cryptoTools = new CryptoTools();
                edit.putString("name", cryptoTools.returnEncode(edtUser.getText().toString()));
                edit.putString("password", cryptoTools.returnEncode(edtPw.getText().toString()));
                edit.putBoolean("isCkRemeber", isCkRemeber);
                edit.putString("isMemory", YES);
                edit.commit();

            } catch (Exception e){
                e.printStackTrace();
            }

        } else if (!ckBRem.isChecked()) {
            if (sp == null) {
                sp = getSharedPreferences(FILE, MODE_PRIVATE);
            }
            SharedPreferences.Editor edit = sp.edit();
            edit.putString("isMemory", NO);
            edit.commit();
        }
    }
    private void checkUpdate(){
        dialogP = ProgressDialog.show(LoginActivity.this, "", "正在检查更新...") ;
        // 自动检查有没有新版本 如果有新版本就提示更新
        new Thread() {
            public void run() {
                try {
                    updateInfoService = new UpdateInfoService(LoginActivity.this);
                    info = updateInfoService.getUpDateInfo();
                    handler1.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        }.start();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler1 = new Handler() {
        public void handleMessage(Message msg) {
            if (updateInfoService.isNeedUpdate() == 1) {
                dialogP.dismiss();
                Permission = updateInfoService.hasPermission();
                showUpdateDialog(Permission);
            }else if(updateInfoService.isNeedUpdate() == 0){
                dialogP.dismiss();
                ToastUtils.show("已更新到最新版本");
              //  Toast.makeText(LoginActivity.this, "已更新到最新版本", Toast.LENGTH_SHORT).show();
            }else{
                dialogP.dismiss();
                DialogUtil.showDialog(LoginActivity.this, "服务器响应异常，请重新尝试或与管理员联系！", true);
            }

        };
    };

    //显示是否要更新的对话框
    private void showUpdateDialog(int Pms) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(getResources().getDrawable(R.drawable.ic_cloud_download_black));
        builder.setTitle("请升级至最新版本" + info.getVersion());
        builder.setMessage(info.getDescription());
        builder.setCancelable(false);
        builder.setPositiveButton("更新应用", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (ContextCompat.checkSelfPermission(LoginActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(LoginActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1);
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {



                    } else {

                    }
                }else{
                    if (Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                        downFile(info.getUrl());
                    } else {
                      //  Toast.makeText(LoginActivity.this, "SD卡不可用，请插入SD卡", Toast.LENGTH_SHORT).show();
                        ToastUtils.show("SD卡不可用，请插入SD卡");
                    }
                }
            }
        });
        if(Pms == 1){
            builder.create().show();
        }else{
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }

            });
            AlertDialog ad = builder.create();
            ad.show();
            ad.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.common_dark_shadow));
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                        downFile(info.getUrl());
                    } else {
                        ToastUtils.show("SD卡不可用，请插入SD卡");
                      //  Toast.makeText(LoginActivity.this, "SD卡不可用，请插入SD卡",Toast.LENGTH_SHORT).show();
                    }

                } else {
                    ToastUtils.show("手机存储权限被拒绝，无法下载");
                    //Toast.makeText(LoginActivity.this, "手机存储权限被拒绝，无法下载", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
    void downFile(final String url) {
        ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIcon(getResources().getDrawable(R.drawable.ic_file_download));
        progressDialog.setTitle("  正在下载");
        progressDialog.setMessage("请稍候...");
        progressDialog.setProgress(0);
        progressDialog.show();
        updateInfoService.downLoadFile(url, progressDialog,handler1);
    }

}
