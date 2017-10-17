package com.juhua.hangfen.eedsrd.widget;

/**
 * Created by congj on 2017/9/11.
 */

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.holder.Holder;
import com.google.gson.internal.LinkedTreeMap;
import com.juhua.hangfen.eedsrd.R;
import com.juhua.hangfen.eedsrd.model.BannerPicture;
import com.juhua.hangfen.eedsrd.util.ScreenUtils;
import com.juhua.hangfen.eedsrd.webservice.SSLConnection;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Dictionary;
import java.util.List;

/**
 * Created by Sai on 15/8/4.
 * 网络图片加载例子
 */
public class NetworkImageHolderView implements Holder<LinkedTreeMap<String, String>> {
    private RelativeLayout mRelativeLayout;
    private ImageView imageView;
    private TextView textView;
    private RelativeLayout.LayoutParams layoutParams;
    @Override
    public View createView(Context context) {
        //你可以通过layout文件来创建，也可以像我一样用代码创建，不一定是Image，任何控件都可以进行翻页
        mRelativeLayout = new RelativeLayout(context);
        //增加整体布局监听
/*        ViewTreeObserver vto = mRelativeLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
            @Override
            public void onGlobalLayout() {
                int height =mRelativeLayout.getHeight();
                int width =mRelativeLayout.getWidth();
                layoutParams.height = (int)(0.5*height);
                mRelativeLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });*/

        imageView = new ImageView(context);
        mRelativeLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        imageView.setLayoutParams(new  RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        mRelativeLayout.addView(imageView);
        textView = new TextView(context);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(14);
        textView.setGravity(Gravity.BOTTOM);
        textView.setPadding(ScreenUtils.dip2px(8), ScreenUtils.dip2px(7), 0, ScreenUtils.dip2px(8));
        textView.setWidth(mRelativeLayout.getWidth());
        textView.setBackgroundResource(R.drawable.bg_gradient);

        mRelativeLayout.addView(textView ,layoutParams);
        return mRelativeLayout;
    }

    @Override
    public void UpdateUI(Context context, int position, LinkedTreeMap<String, String> pic) {
        imageView.setImageResource(R.drawable.ic_banner_default);
        textView.setText(pic.get("TITLE"));
        ImageLoader.getInstance().displayImage(pic.get("IMGPATH"),imageView);
    }
}
