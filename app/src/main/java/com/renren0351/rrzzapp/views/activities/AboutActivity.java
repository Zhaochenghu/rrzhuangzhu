package com.renren0351.rrzzapp.views.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.renren0351.rrzzapp.R;
import com.renren0351.rrzzapp.services.CoreService;
import com.renren0351.rrzzapp.utils.IntentUtils;
import com.renren0351.rrzzapp.views.base.LvBaseAppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/********************************
 * Created by lvshicheng on 2017/2/15.
 * <p>
 * 关于
 ********************************/
@Route(path = "/mime/about")
@RuntimePermissions
public class AboutActivity extends LvBaseAppCompatActivity {
    @BindView(R.id.tv_version)
    TextView tvVersion;

    public static void navigation() {
        ARouter.getInstance().build("/mime/about").navigation();
    }

    @Override
    protected void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.aty_about);
        ButterKnife.bind(this);
    }

    @Override
    protected void initView() {
        initToolbarNav(R.string.title_about_us);
        try {
            PackageInfo packageInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            String version = packageInfo.versionName;
            tvVersion.setText("V" + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.about_ll_about, R.id.about_ll_update,
        R.id.about_ll_suggest, R.id.about_ll_service})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.about_ll_about:   //关于我们
                AboutUsActivity.navigation();
                break;
            case R.id.about_ll_update:  //检查更新
                AboutActivityPermissionsDispatcher.startCheckingWithCheck(this);
                break;
            case R.id.about_ll_suggest: //投诉建议
                SuggestActivity.navigation();
                break;
            case R.id.about_ll_service: //联系客服
                callService();
                break;
        }
    }

    /**
     * 下载需要写存储卡权限
     */
    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void startChecking() {
//        String msg = "升级提示内容";
//        showDownloadDialog(msg);
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void showRationaleChecking(final PermissionRequest request) {
        showPermissionDialog();
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void checkingAskAgain() {
        showPermissionDialog();
    }

    public void showPermissionDialog() {
        new AlertDialog.Builder(this)
            .setMessage("下载应用需要访问您的存储卡，否则无法继续下载")
            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            })
            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    IntentUtils.turnToAppDetail(AboutActivity.this);
                }
            })
            .setCancelable(false)
            .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AboutActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    private void callService() {
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (tm != null && tm.getSimState() == TelephonyManager.SIM_STATE_READY){
            String call = "0351-837-1089";// FIXME: 2017/7/20 服务电话需要修改
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + call));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else {
            showToast("不支持拨打电话");
        }

    }

    private void showDownloadDialog(String msg) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("版本升级").setMessage(msg)
                .setPositiveButton("升级", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startService();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", null)
                .create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    private void startService(){
        Intent intent = new Intent(this, CoreService.class);
        intent.setAction(CoreService.ACTION_CHECK_VERSION);
        startService(intent);
    }
}
