package com.renren0351.rrzzapp.views.base;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.renren0351.rrzzapp.R;
import com.renren0351.rrzzapp.custom.toast.IToast;
import com.renren0351.rrzzapp.custom.toast.ToastUtils;
import com.renren0351.rrzzapp.views.dialog.loaddialog.LoadingDialog;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.leanvision.baseframe.util.LvActivityManager;
import cn.com.leanvision.baseframe.util.LvPreconditions;
import cn.com.leanvision.fragmentation.SupportActivity;

/********************************
 * Created by lvshicheng on 2017/2/13.
 ********************************/
public abstract class LvBaseAppCompatActivity extends SupportActivity {

    @Nullable
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    protected Dialog loadingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        LvActivityManager.getInstance().pushActivity(this);
        setContentView(savedInstanceState);
        ButterKnife.bind(this);
        initPresenter();
        initView(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    protected abstract void setContentView(Bundle savedInstanceState);

    protected void initPresenter() {
    }

    protected void destroyPresenter() {

    }

    protected void initView(Bundle savedInstanceState) {
        initView();
    }

    protected void initView() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyPresenter();
        LvActivityManager.getInstance().popActivity(this);
        dismissLoadingDialog();
    }

    /**
     * --------------
     * Toolbar Settings
     * --------------
     */
    protected void initToolbarNav(int resid) {
        initToolbarNav(getString(resid), null);
    }

    protected void initToolbarNav(String title) {
        initToolbarNav(title, null);
    }

    protected void initToolbarNav(String title, View.OnClickListener listener) {
        LvPreconditions.checkNotNull(toolbar, "XML must include 'common_header' layout!");

        toolbar.setTitle(title);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_white);
        if (listener == null) {
            listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            };
        }
        toolbar.setNavigationOnClickListener(listener);
    }

    protected void initToolbarNavBlack(String title, View.OnClickListener listener) {
        LvPreconditions.checkNotNull(toolbar, "XML must include 'common_header' layout!");

        toolbar.setTitle(title);
        toolbar.setTitleTextColor(getResources().getColor(R.color.lv_black));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black);
        if (listener == null) {
            listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            };
        }
        toolbar.setNavigationOnClickListener(listener);
    }

    protected void showToast(CharSequence text) {
        //使用getApplicationContext()是为了防止CustomToast内存泄漏
        ToastUtils.getInstance(getApplicationContext()).makeTextShow(text, IToast.LENGTH_SHORT);
    }

    protected void showToast(int resId) {
        ToastUtils.getInstance(getApplicationContext()).makeTextShow(resId, IToast.LENGTH_SHORT);
    }

    //  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void showLoadingDialog() {
        showLoadingDialog("加载中...");
    }

    protected void showLoadingDialog(String msg) {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog.createLoadingDialog(this, msg);
            loadingDialog.setCancelable(true);
        } else {
            LoadingDialog.setDialogMessage(loadingDialog, msg);
        }
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    protected void dismissLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }
}
