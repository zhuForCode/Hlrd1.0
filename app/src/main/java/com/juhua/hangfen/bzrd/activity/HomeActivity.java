package com.juhua.hangfen.bzrd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;

import com.google.gson.internal.LinkedTreeMap;
import com.juhua.hangfen.bzrd.R;
import com.juhua.hangfen.bzrd.adapter.HomeButtonAdapter;
import com.juhua.hangfen.bzrd.constants.Constants;
import com.juhua.hangfen.bzrd.model.BannerPicture;
import com.juhua.hangfen.bzrd.model.GetData;
import com.juhua.hangfen.bzrd.model.HomeButton;
import com.juhua.hangfen.bzrd.model.JsonMessage;
import com.juhua.hangfen.bzrd.application.AppManager;
import com.juhua.hangfen.bzrd.util.AsyncUtil;
import com.juhua.hangfen.bzrd.util.GsonUtil;
import com.juhua.hangfen.bzrd.util.ImageUtils;
import com.juhua.hangfen.bzrd.util.ScreenUtils;
import com.juhua.hangfen.bzrd.util.ToastUtils;

import com.juhua.hangfen.bzrd.webservice.SoapAsync;
import com.juhua.hangfen.bzrd.webservice.SoapHelper;
import com.juhua.hangfen.bzrd.webservice.TSoap;
import com.juhua.hangfen.bzrd.webservice.UpdateUI;
import com.juhua.hangfen.bzrd.widget.MaterialBadgeTextView;
import com.juhua.hangfen.bzrd.widget.NetworkImageHolderView;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by congj on 2017/9/11.
 */

public class HomeActivity  extends BaseActivity{
    protected RelativeLayout backButtonRl;
    private ConvenientBanner homeBanner;
    private List<BannerPicture> bannerLists;
    private List<HomeButton> buttonLists;
    private LinearLayout notifyLL;
    private TextView notifyTv;
    private MaterialBadgeTextView mailBadge;
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
        titleTv = (TextView) findViewById(R.id.title_tv);
        backButton = (Button)findViewById(R.id.back_button);
        backButtonRl = (RelativeLayout)findViewById(R.id.back_button_rl);

        homeBanner = (ConvenientBanner) findViewById(R.id.home_banner);
        notifyLL = (LinearLayout)findViewById(R.id.ll_notify);
        notifyTv = (TextView) findViewById(R.id.tv_notify_text);
        gridView = (GridView) findViewById(R.id.home_buttons_grid);
    }
    @SuppressWarnings("unchecked")
    protected  void bindControl(){
        backButtonRl.setVisibility(View.INVISIBLE);
        titleTv.setText(this.getResources().getString(R.string.home_title));
        token = AppManager.getAppManager().getUser().getToken();
        try {
            initImageLoader();
            getLocalButton();
            JsonMessage banners = (JsonMessage) getIntent().getExtras().get("getBannerList");
            JsonMessage unread = (JsonMessage) getIntent().getExtras().get("getUnReadMailCount");
            if(banners != null ){
                setBannerData(banners);
            }
            if (unread != null){
                setUnReadMailNum(unread);
            }
            setGridViewItemHeight(false);
        }catch (Exception e){
            setGridViewItemHeight(false);
        }
    }

    @SuppressWarnings("unchecked")
    private  void setHomeBanner(){
        homeBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
            @Override
            public NetworkImageHolderView createHolder() {
                return new NetworkImageHolderView();
            }
        },bannerLists)
                .setPageIndicator(new int[] {R.drawable.ic_radio_button_unchecked, R.drawable.ic_lens_black})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT)
                .setCanLoop(true);
    }

    private  void setBannerData(JsonMessage jsonMessage){
        if(jsonMessage.isSuccess()){
            String value = GsonUtil.beanToJSONString(jsonMessage.getData());
            bannerLists = GsonUtil.parseJsonArrayWithGson(value, BannerPicture.class);
            setHomeBanner();
        }else{
            ToastUtils.show(jsonMessage.getMessage());
        }
    }

    private void getLocalButton(){
        buttonLists = new ArrayList<HomeButton>();
        buttonLists.add(new HomeButton(0, "代表履职", "icon_m_job", "Duty/DutyMenu.aspx?nav=show"));
        buttonLists.add(new HomeButton(1, "议案建议", "icon_m_yajy", "Proposal/ProposalMenu.aspx?nav=show"));
        buttonLists.add(new HomeButton(2, "政务信息", "icon_m_mqztc", "Duty/GovInfoMenu.aspx?nav=show"));
        buttonLists.add(new HomeButton(3, "人事任免", "icon_m_hr", "People/ArticleList.aspx?SortId=70&nav=show"));
        buttonLists.add(new HomeButton(4, "在线学习", "icon_m_zlk", "Duty/ConsultSort.aspx?nav=show"));
        buttonLists.add(new HomeButton(5, "电子文档", "icon_m_filerecv", "Duty/CongressFileSort.aspx?nav=show"));
        buttonLists.add(new HomeButton(6, "我的邮箱", "icon_m_mail", "Mailbox/MailList.aspx?nav=show"));
        buttonLists.add(new HomeButton(7, "代表数据库", "icon_m_myplatform", "People/Database.aspx?nav=show"));
        buttonLists.add(new HomeButton(8, "个人中心", "icon_m_grzx", "mypersonalcenterB.html"));
        setGridView();
    }
    private void setGridViewItemHeight(boolean showNotify){
        int cols = 3;
        if(buttonLists.size() > 9){
            cols = 4;
        }
        gridView.setNumColumns(cols);
        float bannerHeight = (float) 190;
        float actionbarHeight = (float) 48;
        float notifyHeight = (float) (showNotify ? 30 : 0);
        int bannerH = (int) ImageUtils.convertDpToPixel(bannerHeight, HomeActivity.this);
        int actionbarH =  (int) ImageUtils.convertDpToPixel(actionbarHeight, HomeActivity.this);
        int notifyH = (int) ImageUtils.convertDpToPixel(notifyHeight, HomeActivity.this);
        int gvHeight = ScreenUtils.Height - bannerH - actionbarH - notifyH - ScreenUtils.getStatusBarHeight();
        int itemHeight = (int) Math.ceil(gvHeight/cols);
        homeButtonAdapter = new HomeButtonAdapter(HomeActivity.this, buttonLists, itemHeight);
        gridView.setAdapter(homeButtonAdapter);
    }
    //动态获取菜单button
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
                        Intent personIntent = new Intent(HomeActivity.this, PersonActivity.class);
                        startActivity(personIntent);
                        break;
                    default:
                        if(buttonLists.get(i).getActionUrl().contains("nav=")){
                            Intent intent = new Intent(HomeActivity.this, WebActivity.class);
                            intent.putExtra("Token", token);
                            intent.putExtra("actionUrl", buttonLists.get(i).getActionUrl());
                            intent.putExtra("actionName", buttonLists.get(i).getName());
                            startActivity(intent);
                        }else{
                            Intent intent = new Intent(HomeActivity.this, WebActivity.class);
                            intent.putExtra("Token", token);
                            intent.putExtra("actionUrl", buttonLists.get(i).getActionUrl());
                            startActivity(intent);
                        }
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

    private void setNotify(GetData<String> result){
        if(result.isSuccess()){//设备请求服务器成功与否
            JsonMessage jsonMessage = GsonUtil.parseJsonWithGson(result.getData(), JsonMessage.class);
            if(jsonMessage.isSuccess()){//服务器请求数据库成功与否
                if(jsonMessage.getCode() == 200){
                    notifyTv.setText(Html.fromHtml(jsonMessage.getMessage()));
                    notifyLL.setVisibility(View.VISIBLE);
                    setGridViewItemHeight(true);
                }else{
                    setGridViewItemHeight(false);
                }
            }else{
                ToastUtils.show(jsonMessage.getMessage());
                setGridViewItemHeight(false);
            }
        }else{
            ToastUtils.show(result.getErrorDesc());
            setGridViewItemHeight(false);
        }
    }
    private void setUnReadMailNum(JsonMessage jsonMessage){
        if(jsonMessage.isSuccess()){//服务器请求数据库成功与否
            final int num = Integer.parseInt(jsonMessage.getMessage());
            gridView.post(new Runnable() {
                @Override
                public void run() {
                    try{
                        View itemView = gridView.getChildAt(6);
                        mailBadge = (MaterialBadgeTextView) itemView.findViewById(R.id.unread_mail_badge);
                        mailBadge.setBadgeCount(num);
                        mailBadge.setVisibility(View.VISIBLE);
                    }catch (Exception e){

                    }
                }
            });

        }
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
                AppManager.getAppManager().finishAllActivity();
            }
        }
        return true;
        //   return super.onKeyDown(keyCode, event);
    }
}