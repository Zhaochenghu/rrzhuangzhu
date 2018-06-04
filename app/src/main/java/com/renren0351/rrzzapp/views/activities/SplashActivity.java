package com.renren0351.rrzzapp.views.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ValueAnimator;
import com.renren0351.rrzzapp.LvAppUtils;
import com.renren0351.rrzzapp.LvApplication;
import com.renren0351.rrzzapp.R;
import com.renren0351.rrzzapp.utils.IntentUtils;
import com.renren0351.rrzzapp.views.base.LvBaseAppCompatActivity;
import com.renren0351.rrzzapp.wigets.FilterView;

import butterknife.BindView;
import cn.com.leanvision.baseframe.log.DebugLog;
import cn.com.leanvision.baseframe.util.LvPhoneUtils;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/********************************
 * Created by lvshicheng on 2017/2/20.
 ********************************/
@RuntimePermissions
public class SplashActivity extends LvBaseAppCompatActivity{

    @BindView(R.id.iv_splash)
    ImageView ivSplash;

    private volatile boolean isAnimated = false;

    @Override
    protected void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.aty_splash);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isAnimated) {
            SplashActivityPermissionsDispatcher.destroySplashWithCheck(this);
        } else {
            startAnimation();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DebugLog.log("onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SplashActivityPermissionsDispatcher.destroySplashWithCheck(this);
        DebugLog.log("onRestart");
    }

    /**
     * 底部imageview动画
     */
    private void startAnimation() {
        long duration = 800;
        float distY = LvPhoneUtils.dip2px(SplashActivity.this, 60);
        AnimatorSet animatorSet = new AnimatorSet();
        ValueAnimator animator1 = ValueAnimator.ofFloat(20.0f, -distY);
        animator1.setDuration(duration);
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                ivSplash.setTranslationY(value);
            }
        });
        ValueAnimator animator2 = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator2.setDuration(duration);
        animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                ivSplash.setAlpha(value);
            }
        });
        animatorSet.playTogether(animator1, animator2);
        animatorSet.start();
        animatorSet.addListener(new FilterView.SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //动画完成后，核查GPS权限
                isAnimated = true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    SplashActivityPermissionsDispatcher.destroySplashWithCheck(SplashActivity.this);
                }else {
                    checkGps();
                }

            }
        });
    }

    /**
     * --- 定位权限
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SplashActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    public void destroySplash() {
        checkGps();
    }

    @OnShowRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
    public void showRationaleForLocation(final PermissionRequest request) {
        showPermissionDialog();
    }

    @OnNeverAskAgain(Manifest.permission.ACCESS_COARSE_LOCATION)
    public void locationAskAgain() {
        showPermissionDialog();
    }

    public void showPermissionDialog() {
        DebugLog.log("showPermissionDialog");
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = new AlertDialog.Builder(this)
            .setMessage("取消授权定位功能，应用将无法使用，建议您在设置中开启")
            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            })
            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    IntentUtils.turnToAppDetail(SplashActivity.this);
                }
            })
            .setCancelable(false)
            .show();
    }

    AlertDialog dialog;

    /**
     * 核查GPS权限
     */
    private void checkGps() {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        if (!LvAppUtils.isGPSOpen(getApplicationContext())) {
            //如果没有权限，显示申请GPS权限对话框
            try {
                dialog = new AlertDialog.Builder(SplashActivity.this, R.style.AlterDialogTheme)
                        .setTitle("GPS提示")
                        .setMessage("请打开GPS开关，以便精确的找到充电站")
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        MainActivity.navigation();
//                    }
//                })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                IntentUtils.turnToGps(SplashActivity.this);
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }catch (Exception e){
                dialog = new AlertDialog.Builder(LvApplication.getContext(), R.style.AlterDialogTheme)
                        .setTitle("GPS提示")
                        .setMessage("请打开GPS开关，以便精确的找到充电站")
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        MainActivity.navigation();
//                    }
//                })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                IntentUtils.turnToGps(SplashActivity.this);
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }

        } else {
            //如果有权限，跳转到MainActivity
            MainActivity.navigation();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}