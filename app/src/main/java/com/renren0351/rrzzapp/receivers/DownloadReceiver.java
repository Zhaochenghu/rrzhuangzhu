package com.renren0351.rrzzapp.receivers;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.renren0351.model.storage.AppInfosPreferences;

import cn.com.leanvision.baseframe.log.DebugLog;

public class DownloadReceiver extends BroadcastReceiver {
    public DownloadReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        long downloadID = AppInfosPreferences.get().getDownloadId();
        if (id == downloadID) {
            DownloadManager dManager =
                (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            try {
                Intent install = new Intent(Intent.ACTION_VIEW);
                Uri downloadFileUri = dManager.getUriForDownloadedFile(downloadID);
                install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(install);
            } catch (Exception e) {
                DebugLog.log("未找到安装程序");
            }

        }
    }
}
