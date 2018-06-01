package com.juhua.hangfen.shaoxingrd.tools;

import android.content.Context;
import android.content.res.Resources;

/**
 * Created by kuai on 2017/1/12.
 */

public class UIUtils {

    /**
     * 得到上下文
     */


    /**
     * 得到Resource对象
     */
    public static Resources getResources(Context context) {
        return context.getResources();
    }
    /**
     * dip-->px
     *
     * @param dip
     * @return
     */
    public static int dip2px(Context context,int dip) {
        //知道px和dp转换关系
        /*
            1.px/(ppi/160)=dp
            2.px/dp = density
         */

        float density = getResources(context).getDisplayMetrics().density;
        int densityDpi = getResources(context).getDisplayMetrics().densityDpi;
        //px=dp*density
        int px = (int) (dip * density + .5f);
        return px;
    }

    /**
     * px-->dip
     *
     * @param px
     * @return
     */
    public static int px2dp(Context context,int px) {
        float density = getResources(context).getDisplayMetrics().density;
        int densityDpi = getResources(context).getDisplayMetrics().densityDpi;
        //px=dp*density
        int dip = (int) (px / density + .5f);
        return dip;
    }



}
