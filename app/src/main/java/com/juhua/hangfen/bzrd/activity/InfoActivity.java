package com.juhua.hangfen.bzrd.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.juhua.hangfen.bzrd.R;
import com.juhua.hangfen.bzrd.constants.Constants;
import com.juhua.hangfen.bzrd.model.GetData;
import com.juhua.hangfen.bzrd.tools.AppManager;
import com.juhua.hangfen.bzrd.tools.JsonUtils;
import com.juhua.hangfen.bzrd.util.GsonUtil;
import com.juhua.hangfen.bzrd.util.ToastUtils;
import com.juhua.hangfen.bzrd.webservice.SoapAsync;
import com.juhua.hangfen.bzrd.webservice.SoapHelper;
import com.juhua.hangfen.bzrd.webservice.UpdateUI;

import java.util.HashMap;

/**
 * Created by congj on 2017/10/29.
 */

public class InfoActivity extends BaseActivity {
    private TextView titleTxt;
    private TextView subTitleTxt;
    private TextView contentTxt;
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        initView();
        loadData();
    }

    protected void initView(){
        backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppManager.getAppManager().finishActivity();
            }
        });
        titleTv = (TextView) findViewById(R.id.title_tv);
        titleTv.setText("信息查看");
        titleTxt = (TextView) findViewById(R.id.title_info_txt);
        subTitleTxt = (TextView) findViewById(R.id.subTitle_info_txt);
        contentTxt = (TextView) findViewById(R.id.content_info_txt);
    }

    protected void loadData(){
        SoapHelper infoSoap = new SoapHelper()
                .setTimeout(5000)
                .methodName("GetLBTDetail")
                .addParams("id", getIntent().getStringExtra("id"))
                .addParams("verify", Constants.VERIFY);
        new SoapAsync(infoSoap).setUI(new UpdateUI() {
            @Override
            @SuppressWarnings("unchecked")
            public void onResponse(Object obj) {
                try {
                    HashMap<String, Object> resultObj = (HashMap<String, Object>) obj;
                    GetData<String> result = (GetData<String>) resultObj.get("GetLBTDetail");
                    if(result.isSuccess()){
                        String jData = JsonUtils.jsonString(result.getData());
                        HashMap<String, Object> map = GsonUtil.parseJsonObject(jData);
                        String title = map.get("标题").toString();
                        String content = map.get("内容").toString();
                        String date = map.get("日期").toString();
                        if(date.length() > 9){
                           date =  date.substring(0, 9);
                        }
                       // String clickNums = map.get("点击量").toString();
                        String author = map.get("发布人").toString();
                        if(title.length() > 0){
                            titleTxt.setText(title);
                        }
                        subTitleTxt.setText("日期：" + date + "\n发布人：" + author);
                        if(content.length() > 0){
                            contentTxt.setText(content);
                        }
                    }else{
                        ToastUtils.show(result.getErrorDesc());
                    }
                }catch (Exception e){
                    ToastUtils.show("数据加载出错：" + e.getMessage());
                }
            }
        }).execute();

    }

}
