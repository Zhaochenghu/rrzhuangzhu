package com.renren0351.rrzzapp.services;

import android.app.DownloadManager;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.renren0351.rrzzapp.LvApplication;
import com.renren0351.rrzzapp.R;
import com.renren0351.rrzzapp.event.StationStatusEvent;
import com.renren0351.rrzzapp.event.StopQueryStatusEvent;
import com.renren0351.model.dagger.ApiComponentHolder;
import com.renren0351.model.response.StationStatusResponse;
import com.renren0351.model.storage.AppInfosPreferences;

import java.util.concurrent.TimeUnit;

import cn.com.leanvision.baseframe.log.DebugLog;
import cn.com.leanvision.baseframe.rx.RxBus;
import cn.com.leanvision.baseframe.rx.SimpleSubscriber;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

/********************************
 * Created by lvshicheng on 2017/4/19.
 ********************************/
public class CoreService extends Service {

    public static final String ACTION_START_CHARGING_QUERY = "CHARGING_QUERY_START";
    public static final String ACTION_STOP_CHARGING_QUERY  = "CHARGING_QUERY_STOP";
    public static final String ACTION_CHECK_VERSION        = "CHECK_APP_VERSION";

    private Subscription chargingCheckSubscription;
    private Subscription chargingSubscription;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleCommand(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void handleCommand(Intent intent) {
        if (intent == null) {
            return;
        }
        String action = intent.getAction();
        if (ACTION_START_CHARGING_QUERY.equals(action)) {
            startChargingStatus();
        } else if (ACTION_STOP_CHARGING_QUERY.equals(action)) {
            stopQueryChargingStatus();
        } else if (ACTION_CHECK_VERSION.equals(action)) {
            // TODO: 2017/7/19 检查APP版本信息
            String url = "http://qiniu-app.pgyer.com/dbc3c939840d02ec5d5b3e4f337af24b.apk?e=1500461087&attname=sojoCharging_2017050301_V1.0.apk&token=6fYeQ7_TVB5L0QSzosNFfw2HU8eJhAirMF5VxV9G:nNfPJu8mf1BlH3KJLrshrH1K7ko=&sign=bda221980e1b8ec12e4805864db2ee5e&t=596f381f";
            String msg = "升级提示内容";
            downLoadFile(url);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void startChargingStatus() {
        if (chargingCheckSubscription == null || chargingCheckSubscription.isUnsubscribed()) {
            // 每10秒中检测一次查询状态
            chargingCheckSubscription = Observable.interval(0, 10, TimeUnit.SECONDS)
                .compose(SchedulersCompat.<Long>applyNewSchedulers())
                .subscribe(new SimpleSubscriber<Long>() {

                    @Override
                    public void onNext(Long aLong) {
                        queryChargingStatus();
                    }
                });
        }
    }

    /* The real logic for charging. */
    private void queryChargingStatus() {
        if (chargingSubscription != null && !chargingSubscription.isUnsubscribed()) {
            return;
        }
        chargingSubscription = createChargingObservable()
            .subscribe(new SimpleSubscriber<StationStatusResponse.StationStatus>() {
                @Override
                public void onCompleted() {
                    super.onCompleted();
                    DebugLog.log("Charging query complete!");
                }

				@Override
                public void onNext(StationStatusResponse.StationStatus response) {
                    RxBus.getInstance().postEvent(new StationStatusEvent(response));
                }
            });
    }

    private Observable<StationStatusResponse.StationStatus> createChargingObservable() {
        return ApiComponentHolder.sApiComponent.apiService()
            .getStationStatus()
            .take(1)
            .flatMap(new Func1<StationStatusResponse, Observable<StationStatusResponse.StationStatus>>() {
                @Override
                public Observable<StationStatusResponse.StationStatus> call(StationStatusResponse stationStatusResponse) {
                    if (stationStatusResponse.contentList == null || stationStatusResponse.contentList.size() == 0){
                        //由于充电桩原因造成强制停止充电
                        RxBus.getInstance().postEvent(new StopQueryStatusEvent());
                    }
                    return Observable.from(stationStatusResponse.contentList);
                }
            })
            .compose(SchedulersCompat.<StationStatusResponse.StationStatus>applyNewSchedulers());
    }

    private void stopQueryChargingStatus() {
        if (chargingCheckSubscription != null && !chargingCheckSubscription.isUnsubscribed()) {
            chargingCheckSubscription.unsubscribe();
            chargingCheckSubscription = null;
        }

        if (chargingSubscription != null
            && !chargingSubscription.isUnsubscribed()) {
            chargingSubscription.unsubscribe();
            chargingSubscription = null;
        }
    }

    private void downLoadFile(String url) {
        DownloadManager downloadManager =
            (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalPublicDir("download", "jiedian.apk");
        request.setDescription("杰电应用下载中");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(true);
        request.setMimeType("application/vnd.android.package-archive");
        long enqueue = downloadManager.enqueue(request);
        AppInfosPreferences.get().setDownloadId(enqueue);
    }

    private void showDownloadDialog(final String apkUrl, String msg) {
        if (LvApplication.topActivity != null && LvApplication.topActivity.get() != null) {
            AlertDialog dialog = new AlertDialog.Builder(LvApplication.getContext(), R.style.AlterDialogTheme)
                .setTitle("版本升级").setMessage(msg)
                .setPositiveButton("升级", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downLoadFile(apkUrl);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", null)
                .create();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }
}
