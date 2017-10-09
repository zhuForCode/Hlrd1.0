package com.juhua.hangfen.eedsrd.widget;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.juhua.hangfen.eedsrd.R;
import com.juhua.hangfen.eedsrd.activity.AboutActivity;
import com.juhua.hangfen.eedsrd.activity.LoginActivity;
import com.juhua.hangfen.eedsrd.activity.MainActivity;
import com.juhua.hangfen.eedsrd.activity.WebActivity;
import com.juhua.hangfen.eedsrd.application.AppCache;
import com.juhua.hangfen.eedsrd.constants.Constants;
import com.juhua.hangfen.eedsrd.tools.AppManager;
import com.juhua.hangfen.eedsrd.util.GsonUtil;
import com.juhua.hangfen.eedsrd.util.ToastUtils;
import com.juhua.hangfen.eedsrd.webservice.UpdateUI;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.logging.Handler;

import static com.juhua.hangfen.eedsrd.constants.Constants.RESULT_CONTACT_CONFIRM;

/**
 * Created by congj on 2017/10/2.
 */


