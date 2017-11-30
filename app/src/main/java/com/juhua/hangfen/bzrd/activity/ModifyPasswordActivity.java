package com.juhua.hangfen.bzrd.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.internal.LinkedTreeMap;
import com.juhua.hangfen.bzrd.R;
import com.juhua.hangfen.bzrd.application.AppManager;
import com.juhua.hangfen.bzrd.constants.Constants;
import com.juhua.hangfen.bzrd.model.GetData;
import com.juhua.hangfen.bzrd.model.JsonMessage;
import com.juhua.hangfen.bzrd.model.User;
import com.juhua.hangfen.bzrd.sharedpref.TinyDB;
import com.juhua.hangfen.bzrd.util.GsonUtil;
import com.juhua.hangfen.bzrd.util.ToastUtils;
import com.juhua.hangfen.bzrd.webservice.SoapAsync;
import com.juhua.hangfen.bzrd.webservice.SoapHelper;
import com.juhua.hangfen.bzrd.webservice.UpdateUI;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;

import okhttp3.Call;

/**
 * Created by congj on 2017/10/15.
 */

public class ModifyPasswordActivity extends BaseActivity {
    private ProgressDialog mLoading;

    protected EditText mOriginalEdt;
    protected EditText mNewEdt;
    protected EditText mAgainEdt;
    protected Button mConfirmBtn;

    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        createControl();
        bindControl();
    }

    protected void createControl(){
        titleTv = (TextView) findViewById(R.id.title_tv);
        backButton = (Button)findViewById(R.id.back_button);

        mOriginalEdt = (EditText) findViewById(R.id.original_pwd);
        mNewEdt = (EditText) findViewById(R.id.new_pwd);
        mAgainEdt = (EditText) findViewById(R.id.again_pwd);
        mConfirmBtn = (Button) findViewById(R.id.confirm_modify_btn);

    }

    protected void bindControl(){
        titleTv.setText("修改密码");
        backButton.setOnClickListener(backListener);

        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    mLoading = ProgressDialog.show(ModifyPasswordActivity.this, "", "修改中");
                    TinyDB userDB = new TinyDB(ModifyPasswordActivity.this);
                    User user = (User) userDB.getObject("user", User.class);
                    OkHttpUtils
                            .post()
                            .url(Constants.ASHX_URL)
                            .addParams("method", "UpdatePassWord")
                            .addParams("userid", user.getId())
                            .addParams("Password", mAgainEdt.getText().toString())
                            .addParams("oldpassword", mOriginalEdt.getText().toString())
                            .addParams("UserName", user.getAccount())
                            .addParams("verify", Constants.VERIFY)
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    mLoading.dismiss();
                                    ToastUtils.show("网络连接似乎有点问题。");
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    mLoading.dismiss();
                                    JsonMessage jsonMessage = GsonUtil.parseJsonWithGson(response, JsonMessage.class);
                                    ToastUtils.show(jsonMessage.getMessage());
                                }
                            });
                }
            }
        });
    }

    protected boolean validate(){
        if(mOriginalEdt.getText().toString().length() == 0 || mNewEdt.getText().toString().length() == 0 || mAgainEdt.getText().toString().length() == 0){
            ToastUtils.show("密码不能为空！");
            return false;
        }
        if(mOriginalEdt.getText().toString().length() < 6 || mNewEdt.getText().toString().length() < 6 || mAgainEdt.getText().toString().length() < 6 ){
            ToastUtils.show("密码长度不能小于六位！");
            return false;
        }

        if(!mNewEdt.getText().toString().equals(mAgainEdt.getText().toString())){
            ToastUtils.show("两次密码输入不一致！");
            return false;
        }
        return true;
    }

}
