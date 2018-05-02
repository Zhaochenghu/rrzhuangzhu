package com.renren0351.rrzzapp.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ValueAnimator;
import com.renren0351.rrzzapp.R;
import com.renren0351.rrzzapp.views.activities.MainActivity;
import com.renren0351.rrzzapp.views.base.LvBaseFragment;
import com.renren0351.rrzzapp.wigets.FilterView;

import butterknife.BindView;
import cn.com.leanvision.baseframe.util.LvPhoneUtils;

/********************************
 * Created by lvshicheng on 2017/2/28.
 ********************************/

public class SplashFragment extends LvBaseFragment {

    @BindView(R.id.iv_splash)
    ImageView ivSplash;

    private boolean isAnimationOver = false;

    public static SplashFragment newInstance() {

        Bundle args = new Bundle();

        SplashFragment fragment = new SplashFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View createView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.aty_splash, container, false);
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        startAnimation();
    }

    private void startAnimation() {
        long duration = 800;
        float distY = LvPhoneUtils.dip2px(_mActivity, 60);
        AnimatorSet animatorSet = new AnimatorSet();
        ValueAnimator animator1 = ValueAnimator.ofFloat(20.0f, -distY);
        animator1.setDuration(duration);
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                if (ivSplash != null) {
                    ivSplash.setTranslationY(value);
                }
            }
        });
        ValueAnimator animator2 = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator2.setDuration(duration);
        animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                if (ivSplash != null) {
                    ivSplash.setAlpha(value);
                }
            }
        });
        animatorSet.playTogether(animator1, animator2);
        animatorSet.start();
        animatorSet.addListener(new FilterView.SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimationOver = true;
                if (_mActivity instanceof MainActivity) {
//                    MainActivity mainAty = (MainActivity) _mActivity;
//                    mainAty.dismissSplash();
                }
            }
        });
    }

    @Override
    public boolean onBackPressedSupport() {
        return super.onBackPressedSupport();
    }

//    private void checkEnv() {
//        boolean garented = checkPermission();
//        if (garented) {
//            checkGps();
//        }
//    }
//
//    public static final int requestCode = 0x0001;
//
//    private boolean checkPermission() {
//        if (Build.VERSION.SDK_INT >= 23) {
//            int i = ActivityCompat.checkSelfPermission(_mActivity, Manifest.permission_group.LOCATION);
//            DebugLog.log("checkPermission: " + i);
//            if (PackageManager.PERMISSION_DENIED == i) {
//                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);
//            }
//            return PackageManager.PERMISSION_GRANTED == i;
//        }
//        return true;
//    }
//
//    AlertDialog dialog;
//
//    private void checkGps() {
//        if (!LvAppUtils.isGPSOpen(_mActivity.getApplicationContext())) {
//            //  2017/7/4 弹窗提示，强制退出
//            if (dialog == null || !dialog.isShowing()) {
//                dialog = new AlertDialog.Builder(_mActivity)
//                    .setTitle("GPS提示")
//                    .setMessage("请打开GPS开关，以便精确的找到充电站")
//                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            _mActivity.finish();
//                        }
//                    })
//                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            //  2017/7/4 跳转GPS开启页面
//                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                            //此为设置完成后返回到获取界面
//                            startActivity(intent);
//                        }
//                    })
//                    .setCancelable(false)
//                    .show();
//            }
//        } else {
//            pop();
//        }
//    }

}
