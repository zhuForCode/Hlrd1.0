package com.juhua.hangfen.bzrd.activity;

/**
 * Created by kuai on 2016/12/26.
 */

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.internal.LinkedTreeMap;
import com.juhua.hangfen.bzrd.R;
import com.juhua.hangfen.bzrd.application.AppManager;
import com.juhua.hangfen.bzrd.constants.Constants;
import com.juhua.hangfen.bzrd.model.GetData;
import com.juhua.hangfen.bzrd.model.JsonMessage;
import com.juhua.hangfen.bzrd.model.UpdateInfo;
import com.juhua.hangfen.bzrd.model.User;
import com.juhua.hangfen.bzrd.sharedpref.TinyDB;
import com.juhua.hangfen.bzrd.util.EncryptUtils;
import com.juhua.hangfen.bzrd.util.GsonUtil;
import com.juhua.hangfen.bzrd.util.PermissionReq;
import com.juhua.hangfen.bzrd.util.ToastUtils;
import com.juhua.hangfen.bzrd.webservice.SSLConnection;
import com.juhua.hangfen.bzrd.webservice.SoapAsync;
import com.juhua.hangfen.bzrd.webservice.SoapHelper;
import com.juhua.hangfen.bzrd.webservice.UpdateUI;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by congj on 2017/10/23.
 */

public class LoginActivity extends BaseActivity {
    private EditText accountEdt;
    private EditText passwordEdt;
    private Button accountClearBtn;
    private Button passwordClearBtn;
    private Button loginBtn;
    private CheckBox rememberChk;
    private TextView rememberTxt;
    private Button forgetBtn;

    private LinearLayout formLLayout;
    private LinearLayout progressLLayout;
    private ProgressBar loadingProgress;

    private TextWatcher mTextWatcher;

    protected SoapAsync soapAsync;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setSystemBarTransparent();
        avoidHttps();
        initView();
        autoFill();
        initListener();
        PermissionReq.with(this)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .result(new PermissionReq.Result() {
                    @Override
                    public void onGranted() {
                        checkVersion();
                    }
                    @Override
                    public void onDenied() {
                        ToastUtils.show(R.string.no_permission_storage);
                    }
                })
                .request();
    }

    protected void initView(){
        accountEdt = (EditText) findViewById(R.id.edt_login_account);
        passwordEdt = (EditText) findViewById(R.id.edt_login_password);
        accountClearBtn = (Button) findViewById(R.id.btn_account_clear);
        passwordClearBtn = (Button) findViewById(R.id.btn_password_clear);
        loginBtn = (Button) findViewById(R.id.btn_login);
        rememberChk = (CheckBox) findViewById(R.id.chk_login_remember);
        rememberTxt = (TextView) findViewById(R.id.txt_login_remember);
        forgetBtn = (Button) findViewById(R.id.btn_login_forget);

        formLLayout = (LinearLayout) findViewById(R.id.llayout_form);
        progressLLayout = (LinearLayout) findViewById(R.id.llayout_progress);
        loadingProgress = (ProgressBar) findViewById(R.id.progress_login);
    }

    protected void initListener(){
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    formLLayout.setVisibility(View.GONE);
                    progressLLayout.setVisibility(View.VISIBLE);
                    checkLogin();
                }
            }
        });

        rememberTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rememberChk.setChecked(!rememberChk.isChecked());
            }
        });

        forgetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(accountEdt.getText().toString().equals("")){
                    ToastUtils.show(getString(R.string.toast_empty_account));
                    return;
                }
                if(accountEdt.getText().toString().length() < 6){
                    ToastUtils.show(getString(R.string.toast_length_account));
                    return;
                }
                AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
                dialog.setMessage("新密码将以短信的形式发送到" + accountEdt.getText().toString() + "的手机上，请注意查收！");
                dialog.setPositiveButton(R.string.action_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getPasswordBack(dialogInterface);
                    }
                });
                dialog.setNegativeButton(R.string.action_cancel, null);
                dialog.create().show();
            }
        });

        mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!accountEdt.getText().toString().equals("")) {
                    accountClearBtn.setVisibility(View.VISIBLE);
                } else {
                    accountClearBtn.setVisibility(View.INVISIBLE);
                }
                if (!passwordEdt.getText().toString().equals("")) {
                    passwordClearBtn.setVisibility(View.VISIBLE);
                } else {
                    passwordClearBtn.setVisibility(View.INVISIBLE);
                }
            }
        };
        accountEdt.addTextChangedListener(mTextWatcher);
        passwordEdt.addTextChangedListener(mTextWatcher);

        accountEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    if(accountEdt.getText().toString().trim().length() > 0){
                        accountClearBtn.setVisibility(View.VISIBLE);
                    }else{
                        accountClearBtn.setVisibility(View.INVISIBLE);
                    }
                }else{
                    accountClearBtn.setVisibility(View.INVISIBLE);
                }
            }
        });

        passwordEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    if(passwordEdt.getText().toString().trim().length() > 0){
                        passwordClearBtn.setVisibility(View.VISIBLE);
                    }else{
                        passwordClearBtn.setVisibility(View.INVISIBLE);
                    }
                }else{
                    passwordClearBtn.setVisibility(View.INVISIBLE);
                }
            }
        });

        accountClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accountEdt.setText("");
            }
        });

        passwordClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordEdt.setText("");
            }
        });
    }

    protected boolean validate(){
        if(accountEdt.getText().toString().equals("")){
            ToastUtils.show(getString(R.string.toast_empty_account));
            return false;
        }
        if(passwordEdt.getText().toString().equals("")){
            ToastUtils.show(getString(R.string.toast_empty_password));
            return false;
        }
        if(accountEdt.getText().toString().length() < 6){
            ToastUtils.show(getString(R.string.toast_length_account));
            return false;
        }
        if(passwordEdt.getText().toString().length() < 6){
            ToastUtils.show(getString(R.string.toast_length_password));
            return false;
        }

        return true;
    }

    protected void checkLogin(){
        OkHttpUtils
                .post()
                .url(Constants.ASHX_URL)
                .addParams("method", "LoginApp")
                .addParams("UserName", accountEdt.getText().toString())
                .addParams("Password", passwordEdt.getText().toString())
                .addParams("type", "Android")
                .addParams("verify", Constants.VERIFY)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.show("登录失败：" + e.getMessage());
                        formLLayout.setVisibility(View.VISIBLE);
                        progressLLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JsonMessage jsonMessage = GsonUtil.parseJsonWithGson(response, JsonMessage.class);
                        if(jsonMessage.isSuccess()){
                            LinkedTreeMap userData =(LinkedTreeMap) jsonMessage.getData();
                            final User user = new User();
                            user.setId(userData.get("USERID").toString());
                            user.setName(userData.get("姓名").toString());
                            user.setAccount(accountEdt.getText().toString());
                            user.setArea(userData.get("姓名").toString());
                            user.setMobile(userData.get("手机号码").toString());
                            user.setToken(userData.get("ACCESSTOKEN").toString());
                            new TinyDB(LoginActivity.this).putObject("user", user);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    intent.putExtra("UserCurrent", user);
                                    AppManager.getAppManager().setUser(user);
                                    intent.putExtra("getUnReadMailCount", getUnReadMailCount());
                                    intent.putExtra("getBannerList", getBannerList());
                                    remember();
                                    startActivity(intent);
                                    AppManager.getAppManager().finishActivity();
                                }
                            }).start();

                        }else{
                            ToastUtils.show(jsonMessage.getMessage());
                            formLLayout.setVisibility(View.VISIBLE);
                            progressLLayout.setVisibility(View.GONE);
                        }
                    }
                });
    }

    protected  void autoFill(){
        TinyDB tinyDB = new TinyDB(this);
        rememberChk.setChecked(tinyDB.getBoolean("rememberPassword"));
        if(rememberChk.isChecked()){
            String account = tinyDB.getString("account");
            String password = tinyDB.getString("password");
            if(account != null){
                accountEdt.setText(EncryptUtils.decryptString(account));
                passwordEdt.setText(EncryptUtils.decryptString(password));
            }
        }
    }

    protected void remember(){
        TinyDB tinyDB = new TinyDB(this);
        if(rememberChk.isChecked()){
            tinyDB.putString("account", EncryptUtils.encrptString(accountEdt.getText().toString()));
            tinyDB.putString("password", EncryptUtils.encrptString(passwordEdt.getText().toString()));
            tinyDB.putBoolean("rememberPassword", true);
        }else{
            tinyDB.putBoolean("rememberPassword", false);
        }
    }

    protected void getPasswordBack(final DialogInterface dialogInterface){
        final TextView mLoadingTxt  = (TextView) progressLLayout.findViewById(R.id.txt_progress_login);
        mLoadingTxt.setText(R.string.progress_password_back);
        formLLayout.setVisibility(View.GONE);
        progressLLayout.setVisibility(View.VISIBLE);
        OkHttpUtils
                .post()
                .url(Constants.ASHX_URL)
                .addParams("method", "GetBackPassWord")
                .addParams("mobile", accountEdt.getText().toString())
                .addParams("verify", Constants.VERIFY)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dialogInterface.dismiss();
                        formLLayout.setVisibility(View.VISIBLE);
                        progressLLayout.setVisibility(View.GONE);
                        mLoadingTxt.setText(R.string.progress_login);
                        ToastUtils.show("取回密码失败：" + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        dialogInterface.dismiss();
                        formLLayout.setVisibility(View.VISIBLE);
                        progressLLayout.setVisibility(View.GONE);
                        mLoadingTxt.setText(R.string.progress_login);
                        JsonMessage jsonMessage = GsonUtil.parseJsonWithGson(response, JsonMessage.class);
                        ToastUtils.show(jsonMessage.getMessage());
                    }
                });

    }

    protected JsonMessage getUnReadMailCount(){
        try {
            Response response = OkHttpUtils
                    .post()
                    .url(Constants.ASHX_URL)
                    .addParams("method", "GetUnReadMailCount")
                    .addParams("userid", "3")
                    .addParams("verify", Constants.VERIFY)
                    .tag(LoginActivity.this)
                    .build()
                    .execute();
            return   GsonUtil.parseJsonWithGson(response.body().string(), JsonMessage.class);
        }catch (Exception e){
            return  null;
        }
    }

    protected JsonMessage getBannerList(){
        try {
            Response response = OkHttpUtils
                    .post()
                    .url(Constants.ASHX_URL)
                    .addParams("method", "GetBannerList")
                    .addParams("size", "5")
                    .addParams("verify", Constants.VERIFY)
                    .tag(LoginActivity.this)
                    .build()
                    .execute();
            return GsonUtil.parseJsonWithGson(response.body().string().replace('“','"'), JsonMessage.class);
        }catch (Exception e){
            return  null;
        }
    }

    protected void checkVersion(){
        final TextView mLoadingTxt  = (TextView) progressLLayout.findViewById(R.id.txt_progress_login);
        mLoadingTxt.setText(R.string.progress_check_version);
        formLLayout.setVisibility(View.GONE);
        progressLLayout.setVisibility(View.VISIBLE);
        OkHttpUtils
                .post()
                .url(Constants.ASHX_URL)
                .addParams("method", "GetWebAppVersion")
                .addParams("type", "Android")
                .addParams("verify", Constants.VERIFY)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        formLLayout.setVisibility(View.VISIBLE);
                        progressLLayout.setVisibility(View.GONE);
                        mLoadingTxt.setText(R.string.progress_login);
                        ToastUtils.show("检查更新失败：" + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        formLLayout.setVisibility(View.VISIBLE);
                        progressLLayout.setVisibility(View.GONE);
                        mLoadingTxt.setText(R.string.progress_login);
                        JsonMessage jsonMessage = GsonUtil.parseJsonWithGson(response, JsonMessage.class);
                        if(jsonMessage.isSuccess()){
                            try {
                                PackageManager packageManager = getPackageManager();
                                PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
                                String value = GsonUtil.beanToJSONString(jsonMessage.getData());
                                final UpdateInfo updateInfo = GsonUtil.parseJsonWithGson(value, UpdateInfo.class);
                                String newVersion = updateInfo.getVersion();
                                String currentVersion = packageInfo.versionName;
                                if (newVersion.equals(currentVersion)) {
                                    ToastUtils.show("当前已是最新版本！");
                                } else{
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
                                    dialog.setIcon(R.drawable.ic_cloud_download_black);
                                    dialog.setTitle("请升级至最新版本" + updateInfo.getVersion());
                                    dialog.setMessage(updateInfo.getDescription());
                                    dialog.setPositiveButton(R.string.action_update, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            downloadNewApk(updateInfo.getUrl());
                                        }
                                    });
                                    dialog.setNegativeButton(R.string.action_cancel, null);
                                    dialog.create().show();
                                }
                            } catch (PackageManager.NameNotFoundException e) {
                                ToastUtils.show("版本检测失败！");
                            }
                        }else{
                            ToastUtils.show(jsonMessage.getMessage());
                        }
                    }
                });

    }

    protected void downloadNewApk(String url){
        ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIcon(getResources().getDrawable(R.drawable.ic_file_download));
        progressDialog.setTitle("正在下载");
        progressDialog.setMessage("请稍候...");
        progressDialog.setProgress(0);
        progressDialog.show();
        new UpdateInfoService(this).downLoadFile(url, progressDialog, new Handler());

    }

    protected void avoidHttps(){
        SSLConnection.allowAllSSL();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}