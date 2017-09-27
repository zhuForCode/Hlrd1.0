package com.juhua.hangfen.eedsrd.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
import com.juhua.hangfen.eedsrd.R;
import com.juhua.hangfen.eedsrd.adapter.HomeButtonAdapter;
import com.juhua.hangfen.eedsrd.application.AppCache;
import com.juhua.hangfen.eedsrd.constants.Constants;
import com.juhua.hangfen.eedsrd.model.BannerPicture;
import com.juhua.hangfen.eedsrd.model.GetData;
import com.juhua.hangfen.eedsrd.model.HomeButton;
import com.juhua.hangfen.eedsrd.util.AsyncUtil;
import com.juhua.hangfen.eedsrd.util.GsonUtil;
import com.juhua.hangfen.eedsrd.util.ImageUtils;
import com.juhua.hangfen.eedsrd.util.ScreenUtils;
import com.juhua.hangfen.eedsrd.util.ToastUtils;

import com.juhua.hangfen.eedsrd.util.ViewUtils;
import com.juhua.hangfen.eedsrd.webservice.SoapAsync;
import com.juhua.hangfen.eedsrd.webservice.SoapHelper;
import com.juhua.hangfen.eedsrd.webservice.TSoap;
import com.juhua.hangfen.eedsrd.webservice.UpdateUI;
import com.juhua.hangfen.eedsrd.widget.NetworkImageHolderView;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by congj on 2017/9/11.
 */

public class HomeActivity  extends BaseActivity{
    private ConvenientBanner homeBanner;
    private List<BannerPicture> bannerLists;
    private List<HomeButton> buttonLists;
    private GridView gridView;
    private HomeButtonAdapter homeButtonAdapter;

    private String token;//用户票据
    protected long mExitTime;//按下返回键的间隔
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        createControl();
        bindControl();
    }
    protected void createControl(){
        homeBanner = (ConvenientBanner) findViewById(R.id.home_banner);
        gridView = (GridView) findViewById(R.id.home_buttons_grid);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(this.getResources().getString(R.string.app_name) + "代表履职平台");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
    }
    protected  void bindControl(){
        initImageLoader();
        token = getIntent().getExtras().getString("Token");
        getLocalButton();
        getBannerData();
   //     getButtonData();
    }

    @SuppressWarnings("unchecked")
    private  void setHomeBanner(){
        homeBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
            @Override
            public NetworkImageHolderView createHolder() {
                return new NetworkImageHolderView();
            }
        },bannerLists)
                .setPageIndicator(new int[] {R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT)
                .setCanLoop(true);
    }

    private  void getBannerData(){
        SoapHelper soapHelper = new SoapHelper()
                .setWsdl("http://58.18.251.10:8083/WebServers/AppSer.asmx?WSDL")
                .methodName("GetLBTList")
                .addParams("size", "5")
                .addParams("verify", Constants.VERIFY);
        new SoapAsync(soapHelper).setUI(new UpdateUI() {
            @Override
            @SuppressWarnings("unchecked")
            public void onResponse(Object obj) {
                HashMap<String, Object> resultObj = (HashMap<String, Object>) obj;
                GetData<String> result =(GetData<String>) resultObj.get("GetLBTList");
                if(result.isSuccess()){
                    bannerLists = GsonUtil.parseJsonArrayWithGson("[" + result.getData() + "]", BannerPicture.class);
                    setHomeBanner();
                }else{
                    ToastUtils.show(result.getErrorDesc());
                }
            }
        }).execute();

    }

    private void getLocalButton(){
        buttonLists = new ArrayList<HomeButton>();
  //      buttonLists.add(new HomeButton(0, "工作信息", "icon_m_job", "gzxxs.html"));
        buttonLists.add(new HomeButton(1, "议案建议", "icon_m_yajy", "yajys.html"));
        buttonLists.add(new HomeButton(2, "代表履职", "icon_m_dblz", "dblzs.html"));
        buttonLists.add(new HomeButton(3, "网络交流", "icon_m_wljl", "wljls.html"));
        buttonLists.add(new HomeButton(4, "民情直通车", "icon_m_mqztc", "mqztcB.html"));
        buttonLists.add(new HomeButton(5, "短信平台", "icon_m_mail", "mymailListB.html"));
        buttonLists.add(new HomeButton(6, "我的平台", "icon_m_myplatform", "myplatformB.html"));
        buttonLists.add(new HomeButton(7, "个人中心", "icon_m_grzx", "mypersonalcenterB.html"));

        toolbar.post(new Runnable() {
            @Override
            public void run() {
                int cols = 3;
                if(buttonLists.size() > 9){
                    cols = 4;
                }
                gridView.setNumColumns(cols);
                float bannerHeight = (float) 200;
                int bannerH = (int) ImageUtils.convertDpToPixel(bannerHeight, HomeActivity.this);
                int gvHeight = ScreenUtils.Height - bannerH - ScreenUtils.getNavigationBarHeight() - toolbar.getHeight() - ScreenUtils.getStatusBarHeight();
                int itemHeight = (int) Math.ceil(gvHeight/cols);
                homeButtonAdapter = new HomeButtonAdapter(HomeActivity.this, buttonLists, itemHeight);
                gridView.setAdapter(homeButtonAdapter);
                setGridView();
            }
        });

    }

    private  void getButtonData(){
        SoapHelper soapHelper = new SoapHelper()
                .setWsdl("https://dblz.zjrd.gov.cn/WebServers/ZhrdSer.asmx?WSDL")
                .methodName("GetAPPMenu")
                .addParams("userid", "19404")
                .addParams("sfid", "10108")
                .addParams("verify", Constants.VERIFY);
        new SoapAsync(soapHelper).setUI(new UpdateUI() {
            @Override
            @SuppressWarnings("unchecked")
            public void onResponse(final Object obj) {
                HashMap<String, Object> resultObj = (HashMap<String, Object>) obj;
                GetData<String> result =(GetData<String>) resultObj.get("GetAPPMenu");
                if(result.isSuccess()){
                    HashMap<String, Object> hashMap = GsonUtil.parseJsonObject(result.getData());
                 //   buttonLists = GsonUtil.parseJsonArrayWithGson(hashMap.get("menus"), HomeButton.class);
                    List<Object> list = (List<Object>) hashMap.get("menus");
                    buttonLists = new ArrayList<HomeButton>();
                    for(Object object: list)
                    {
                        LinkedTreeMap<String, String> linkedHashMap = (LinkedTreeMap<String, String>) object;
                        HomeButton homeButton = new HomeButton(linkedHashMap.get("Name"), linkedHashMap.get("Icon"), linkedHashMap.get("Url"));
                        buttonLists.add(homeButton);
                    }
                    buttonLists.remove(9);
                    new AsyncUtil(buttonLists)
                            .setEvents(new TSoap() {
                                @Override
                                @SuppressWarnings("unchecked")
                                public  void onRequest(Object object){
                                    ArrayList<HomeButton> btns = (ArrayList<HomeButton>) object;
                                    if(btns.size() != 0){
                                        for (HomeButton action: btns) {
                                            action.getIconFromUrl();
                                        }
                                    }
                                }
                                @Override
                                public void onResponse(Object obj) {
                                    buttonLists = (List<HomeButton>) obj;
                                    int cols = 3;
                                    if(buttonLists.size() > 9){
                                        cols = 4;
                                    }
                                    gridView.setNumColumns(cols);
                                    int gvHeight = ScreenUtils.Height -  homeBanner.getHeight() - ScreenUtils.getNavigationBarHeight() - ScreenUtils.getStatusBarHeight();
                                    int itemHeight = (int) Math.ceil(gridView.getHeight()/cols);
                                    homeButtonAdapter = new HomeButtonAdapter(HomeActivity.this, buttonLists, itemHeight);
                                    gridView.setAdapter(homeButtonAdapter);
                                    setGridView();
                                }
                            }).execute();

                }else{
                    ToastUtils.show(result.getErrorDesc());
                }
            }
        }).execute();
    }


    private void setGridView(){

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 8:
                        ToastUtils.show(buttonLists.get(i).getName());
                        break;
                    default:
                        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                        intent.putExtra("Token", token);
                        intent.putExtra("actionUrl", buttonLists.get(i).getActionUrl());
                        startActivity(intent);
                        break;
                }
            }
        });


    }

    private void initImageLoader(){
        //网络图片例子,结合常用的图片缓存库UIL,你可以根据自己需求自己换其他网络图片库
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().
                showImageForEmptyUri(R.drawable.ic_banner_default)
                .cacheInMemory(true).cacheOnDisk(true).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Object mHelperUtils;
                //  Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                ToastUtils.show("再按一次退出程序");
                mExitTime = System.currentTimeMillis();
            } else {
                AppCache.clearStack();
            }
        }
        return true;
        //   return super.onKeyDown(keyCode, event);
    }
}
