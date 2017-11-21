package com.juhua.hangfen.bzrd.executor;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.webkit.MimeTypeMap;

import com.juhua.hangfen.bzrd.R;
import com.juhua.hangfen.bzrd.application.AppCache;
import com.juhua.hangfen.bzrd.sharedpref.Preferences;
import com.juhua.hangfen.bzrd.util.FileUtils;
import com.juhua.hangfen.bzrd.util.NetworkUtils;

/**
 * Created by JiaJin Kuai on 2017/4/17.
 */

public abstract class DownloadFile implements IExecutor<Void>{
    private Activity mActivity;
    private String fileUrl;

    protected DownloadFile(Activity activity, String url) {
        mActivity = activity;
        fileUrl = url;
    }

    @Override
    public void execute() {
        checkNetwork();
    }

    private void checkNetwork() {
        boolean mobileNetworkDownload = Preferences.enableMobileNetworkDownload();
        if (NetworkUtils.isActiveNetworkMobile(mActivity) && !mobileNetworkDownload) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle(R.string.tips);
            builder.setMessage(R.string.download_tips);
            builder.setPositiveButton(R.string.download_tips_sure, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    downloadWrapper();
                }
            });
            builder.setNegativeButton(R.string.cancel, null);
            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } else {
            downloadWrapper();
        }
    }

    private void downloadWrapper() {
        onPrepare();
        downloadFile();
    }

  //  protected abstract void download();

    protected long downloadFile() {
        int idx = fileUrl.lastIndexOf("/");
        String ext = fileUrl.substring(idx);
        Uri uri = Uri.parse(fileUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalPublicDir(FileUtils.getDownloadDir(), ext);
        request.setMimeType(MimeTypeMap.getFileExtensionFromUrl(fileUrl));
        request.allowScanningByMediaScanner();
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(false);// 不允许漫游
        DownloadManager downloadManager = (DownloadManager) AppCache.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        return downloadManager.enqueue(request);
    }
}
