package com.juhua.hangfen.eedsrd.application;

import android.support.design.BuildConfig;
import android.util.Log;
import android.widget.Toast;

import com.juhua.hangfen.eedsrd.tools.FileUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by JiaJin Kuai on 2017/3/6.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault());

    private Thread.UncaughtExceptionHandler mDefaultHandler;

    public static CrashHandler getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static CrashHandler instance = new CrashHandler();
    }

    public void init() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!BuildConfig.DEBUG) {
          //  saveCrashInfo(ex);
        }
        saveCrashInfo(ex);
        Log.d("crash-save", ex.toString());
        mDefaultHandler.uncaughtException(thread, ex);
    }

    private void saveCrashInfo(Throwable ex) {
        String stackTrace = Log.getStackTraceString(ex);
        Date date = new Date();
        String dateStr = DATE_FORMAT.format(date);
        String filePath = FileUtils.getLogDir() + String.format("log_%s.log", dateStr);
        String time = TIME_FORMAT.format(date);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true));
            bw.write(String.format(Locale.getDefault(), "*** crash at %s *** ", time));
            bw.newLine();
            bw.write(stackTrace);
            bw.newLine();
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
