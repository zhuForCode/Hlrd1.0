package com.juhua.hangfen.shaoxingrd.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.juhua.hangfen.shaoxingrd.R;
import com.juhua.hangfen.shaoxingrd.adapter.RoleAdapter;
import com.juhua.hangfen.shaoxingrd.model.User;
import com.juhua.hangfen.shaoxingrd.sharedpref.TinyDB;
import com.juhua.hangfen.shaoxingrd.application.AppManager;
import com.juhua.hangfen.shaoxingrd.widget.SingleLinePreference;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

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

    public static class PersonalFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener{
        private SingleLinePreference pUserName;
        private SingleLinePreference pChangeRole;
        private SingleLinePreference pModifyPassword;
        private SingleLinePreference pAbout;
        private TinyDB mTinyDB;
        private User mUser;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_percenal_center);
            mTinyDB  = new TinyDB(getActivity());
            mUser = (User) mTinyDB.getObject("user", User.class);

            pUserName = (SingleLinePreference) findPreference("username");
            pChangeRole = (SingleLinePreference) findPreference("change_role");
            pModifyPassword =  (SingleLinePreference) findPreference("change_pwd");
            pAbout =  (SingleLinePreference) findPreference("about");

            pUserName.setPrefIconResId(R.drawable.ic_account_circle);
            pUserName.setLeftText(mUser.getName());
            pUserName.setRightText("注销");


            pChangeRole.setPrefIconResId(R.drawable.ic_change_role);
            pChangeRole.setRightText("切换");
            pChangeRole.setLeftText(mUser.getRoleName());

            pModifyPassword.setPrefIconResId(R.drawable.ic_lock_outline);
            pModifyPassword.setLeftText("修改密码");

            pAbout.setPrefIconResId(R.drawable.ic_info_outline);
            pAbout.setLeftText("关于");

            setListener();
        }

        private void setListener() {
            pUserName.setOnPreferenceClickListener(this);
            pChangeRole.setOnPreferenceClickListener(this);
            pModifyPassword.setOnPreferenceClickListener(this);
            pAbout.setOnPreferenceClickListener(this);

        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            switch (preference.getOrder()){
                case 0:
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("确定要退出当前账号？");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            AppManager.getAppManager().finishAllActivity();
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    if(!getActivity().isFinishing()) {
                        builder.create().show();
                    }
                    break;
                case 1:
                    ShowRolesList();
                    break;
                case 2:
                    Intent password = new Intent(getActivity(), ModifyPasswordActivity.class);
                    startActivity(password);
                    break;
                case 3:
                    Intent intent = new Intent(getActivity(), AboutActivity.class);
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

        private void ShowRolesList()
        {
            Gson gson = new Gson();
            String value = mTinyDB.getString("roles");
            Type type = new TypeToken<List<HashMap<String, Object>>>() {
            }.getType();
            final    List<HashMap<String, Object>> roles = gson.fromJson(value, type);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            //builder.setIcon(R.drawable.ic_launcher);
            builder.setTitle("选择身份");
            final RoleAdapter adapter = new RoleAdapter(getActivity(), roles, mUser.getRoleId());
            //    设置一个下拉的列表选择项
            adapter.notifyDataSetChanged();

            builder.setAdapter(adapter, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    mUser.setRoleId(roles.get(which).get("id").toString());
                    mUser.setRoleName(roles.get(which).get("name").toString());
                    mTinyDB.putObject("user", mUser);
                    pChangeRole.setLeftText(mUser.getRoleName());
                }
            });
            builder.show();
        }
    }
}
