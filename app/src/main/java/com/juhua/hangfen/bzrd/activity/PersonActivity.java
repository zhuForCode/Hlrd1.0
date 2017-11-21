package com.juhua.hangfen.bzrd.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.juhua.hangfen.bzrd.R;
import com.juhua.hangfen.bzrd.model.User;
import com.juhua.hangfen.bzrd.sharedpref.TinyDB;
import com.juhua.hangfen.bzrd.tools.AppManager;
import com.juhua.hangfen.bzrd.widget.SingleLinePreference;

/**
 * Created by congj on 2017/10/13.
 */

public class PersonActivity extends BaseActivity {
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        createControl();
        bindControl();
    }

    protected void createControl(){
        titleTv = (TextView) findViewById(R.id.title_tv);
        backButton = (Button)findViewById(R.id.back_button);
        getFragmentManager().beginTransaction()
                .replace(R.id.ll_fragment_container, new PersonalFragment())
                .commit();
    }

    protected void bindControl(){
        titleTv.setText("个人中心");
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppManager.getAppManager().finishActivity();
            }
        });
    }

    public static class PersonalFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
        private SingleLinePreference pUserName;
        private SingleLinePreference pModifyPassword;
        private SingleLinePreference pAbout;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_percenal_center);

            pUserName = (SingleLinePreference) findPreference("username");
            pModifyPassword =  (SingleLinePreference) findPreference("change_pwd");
            pAbout =  (SingleLinePreference) findPreference("about");
            pUserName.setPrefIconResId(R.drawable.ic_account_circle);
            TinyDB userDB = new TinyDB(AppManager.getAppManager().currentActivity());
            User user = (User) userDB.getObject("user", User.class);
            pUserName.setLeftText(user.getName());
            pUserName.setRightText("注销");

            pModifyPassword.setPrefIconResId(R.drawable.ic_lock_outline);
            pModifyPassword.setLeftText("修改密码");

            pAbout.setPrefIconResId(R.drawable.ic_info_outline);
            pAbout.setLeftText("关于");

            setListener();
        }

        private void setListener() {
            pUserName.setOnPreferenceClickListener(this);
            pModifyPassword.setOnPreferenceClickListener(this);
            pAbout.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            switch (preference.getOrder()){
                case 0:
                    AlertDialog.Builder builder = new AlertDialog.Builder(AppManager.getAppManager().currentActivity());
                    builder.setMessage("确定要退出当前账号？");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(AppManager.getAppManager().currentActivity(), LoginActivity.class);
                            AppManager.getAppManager().finishAllActivity();
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    if(!(AppManager.getAppManager().currentActivity()).isFinishing()) {
                        builder.create().show();
                    }
                    break;
                case 1:
                    Intent password = new Intent(AppManager.getAppManager().currentActivity(), ModifyPasswordActivity.class);
                    startActivity(password);
                    break;
                case 2:
                    Intent intent = new Intent(AppManager.getAppManager().currentActivity(), AboutActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
            return false;
        }
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) { // 如果返回码是可以用的
                switch (requestCode) {

                }
            }
        }

    }
}
