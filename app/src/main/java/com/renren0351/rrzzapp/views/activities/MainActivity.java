package com.renren0351.rrzzapp.views.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.renren0351.rrzzapp.LvAppUtils;
import com.renren0351.rrzzapp.R;
import com.renren0351.rrzzapp.utils.IntentUtils;
import com.renren0351.rrzzapp.views.base.LvBaseAppCompatActivity;
import com.renren0351.rrzzapp.views.fragments.MainFragment;
import com.renren0351.model.events.UnauthEvent;
import com.trello.rxlifecycle.ActivityEvent;

import java.io.IOException;

import cn.com.leanvision.baseframe.rx.RxBus;
import cn.com.leanvision.baseframe.rx.SimpleSubscriber;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * 首页
 */
@Route(path = "/sojo/main")
@RuntimePermissions
public class MainActivity extends LvBaseAppCompatActivity {

    public static void navigation() {
        ARouter.getInstance().build("/sojo/main").navigation();
    }

    @Override
    protected void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.aty_main);
        //加载MainFragment
        if (savedInstanceState == null) {
            loadRootFragment(R.id.fragment_container, MainFragment.newInstance());
        } else {
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (LvAppUtils.isLogin()) {
            SettingActivity.logout();
            //true
            LoginActivity.navigation(true);
        }
    }

    @Override
    protected void initView() {
        super.initView();
        //注册UnauthEvent事件，在ApiModule的provideOkHttpClient()中发送事件
        RxBus.getInstance()
            .toObservable(UnauthEvent.class)
            .compose(this.<UnauthEvent>bindUntilEvent(ActivityEvent.DESTROY))
            .subscribe(new SimpleSubscriber<UnauthEvent>() {
                @Override
                public void onNext(UnauthEvent unauthEvent) {
                    super.onNext(unauthEvent);
                    if (LvAppUtils.isLogin()) { // 只有在登录状态去处理被挤掉的问题
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
            });
        //APP升级
        LvAppUtils.appUpdate(this);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
    }

    /*
    * 扫一扫 Camera 权限
    * */
    public void turnToScan() {
        if (Build.VERSION.SDK_INT < 23) {
            try {
                checkCameraPermissions();
                turnToScanForPermission();
            } catch (IOException e) {
                showScanPermissionDialog();
            }
        } else {
            MainActivityPermissionsDispatcher.turnToScanForPermissionWithCheck(this);
        }
    }

    /**
     * 检查相机权限，如果不能打开相机则抛出异常
     */
    public static void checkCameraPermissions() throws IOException {
        try {
            Camera camera = Camera.open();
            if (camera != null) {
                camera.release();
                camera = null;
            }
        } catch (Exception e) {
            throw new IOException();
        }
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    public void turnToScanForPermission() {
        if (LvAppUtils.isCharging()) {
            return;
        }
        ScanActivity.navigation();
    }

    @OnShowRationale(Manifest.permission.CAMERA)
    public void showRationaleForScan(PermissionRequest request) {
        showScanPermissionDialog();
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    public void scanAskAgain() {
        showScanPermissionDialog();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }


    public void showScanPermissionDialog() {
        new AlertDialog.Builder(this)
            .setMessage("我们需要摄像头权限，否则无法扫描二维码，建议您在设置中开启")
            .setNegativeButton("取消", null)
            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    IntentUtils.turnToAppDetail(MainActivity.this);
                }
            })
            .setCancelable(false)
            .show();
    }
}
