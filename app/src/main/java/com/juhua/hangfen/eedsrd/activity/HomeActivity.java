package com.juhua.hangfen.eedsrd.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.google.gson.Gson;
import com.juhua.hangfen.eedsrd.R;
import com.juhua.hangfen.eedsrd.model.BannerPicture;
import com.juhua.hangfen.eedsrd.util.GsonUtil;
import com.juhua.hangfen.eedsrd.webservice.SSLConnection;
import com.juhua.hangfen.eedsrd.widget.NetworkImageHolderView;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by congj on 2017/9/11.
 */

public class HomeActivity  extends AppCompatActivity{
    private ConvenientBanner homeBanner;
    private List<BannerPicture> networkImages;
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bindControl();
    }

    protected  void bindControl(){
        String result = "[{\"ID\":\"1262\",\"TITLE\":\"王辉忠赴湖州调研人大工作\",\"IMGPATH\":\"https://dblz.zjrd.gov.cn/tempload/lbt/W020170602588007031989.jpg\",\"URL\":\"https://dblz.zjrd.gov.cn/WebServers/ShowLbt.aspx?ID=1262&CA=1\"},{\"ID\":\"1261\",\"TITLE\":\"毛光烈赴温州调研督察改革和治水工作\",\"IMGPATH\":\"https://dblz.zjrd.gov.cn/tempload/lbt/W020170515519184539523.jpg\",\"URL\":\"https://dblz.zjrd.gov.cn/WebServers/ShowLbt.aspx?ID=1261&CA=1\"},{\"ID\":\"1182\",\"TITLE\":\"省十二届人大五次会议胜利闭幕\",\"IMGPATH\":\"https://dblz.zjrd.gov.cn/tempload/lbt/W020170122337764379735.jpg\",\"URL\":\"https://dblz.zjrd.gov.cn/WebServers/ShowLbt.aspx?ID=1182&CA=1\"},{\"ID\":\"1181\",\"TITLE\":\"夏宝龙主持省十二届人大五次会议开幕式\",\"IMGPATH\":\"https://dblz.zjrd.gov.cn/tempload/lbt/W020170116351686568786.jpg\",\"URL\":\"https://dblz.zjrd.gov.cn/WebServers/ShowLbt.aspx?ID=1181&CA=1\"},{\"ID\":\"961\",\"TITLE\":\"省十二届人大四次会议胜利闭幕\",\"IMGPATH\":\"https://dblz.zjrd.gov.cn/tempload/lbt/W020160129394409607272.jpg\",\"URL\":\"https://dblz.zjrd.gov.cn/WebServers/ShowLbt.aspx?ID=961&CA=1\"}]";
        Gson gson = new Gson();
//        BannerPicture bannerPicture = gson.fromJson(result, BannerPicture.class);
        List<BannerPicture> list = GsonUtil.parseJsonArrayWithGson(result, BannerPicture.class);

        homeBanner = (ConvenientBanner) findViewById(R.id.home_banner);
        initImageLoader();
        networkImages= list;
        homeBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
            @Override
            public NetworkImageHolderView createHolder() {
                return new NetworkImageHolderView();
            }
        },networkImages)
                .setPageIndicator(new int[] {R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);

    }

    //初始化网络图片缓存库
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
}
