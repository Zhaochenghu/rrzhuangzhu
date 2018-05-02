package com.renren0351.rrzzapp.views.activities;

import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.renren0351.rrzzapp.R;
import com.renren0351.rrzzapp.views.base.LvBaseAppCompatActivity;
import com.renren0351.rrzzapp.wxapi.WXPayEntryActivity;
import com.renren0351.model.response.WalletInfoResponse;
import com.renren0351.model.storage.AppInfosPreferences;
import com.renren0351.presenter.usercase.GetWalletInfoCase;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.leanvision.baseframe.log.DebugLog;
import cn.com.leanvision.baseframe.rx.SimpleSubscriber;

/**
 * Created by admin on 2017-02-16.
 */
@Route(path = "/login/mime/money")
public class MoneyActivity extends LvBaseAppCompatActivity {

    @BindView(R.id.tv_money)
    TextView       tvMoney;
    @BindView(R.id.money_bt_charging)
    Button         btCharging;
    @BindView(R.id.money_rl_paysetting)
    RelativeLayout rlPaySetting;
    @BindView(R.id.money_tv_record)
    TextView       tvRecord;

    public static void navigation() {
        ARouter.getInstance().build("/login/mime/money").navigation();
    }

    @Override
    protected void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.aty_money);
    }

    @Override
    protected void initView() {
        initToolbarNav("余额");
        DebugLog.log("size:" + tvMoney.getTextSize());
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();
        DebugLog.log("width:" + screenWidth + "   height:" + screenHeight);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        refreshMoney();
        getWalletInfo();
    }

    private void refreshMoney() {
        tvMoney.setText(String.format(Locale.getDefault(), "￥%.2f", AppInfosPreferences.get().getMoney() / 100.0f));
    }

    @OnClick({R.id.money_bt_charging, R.id.money_rl_paysetting, R.id.money_tv_record})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.money_rl_paysetting://支付设置
                PaySettingVerify.navigation();
                break;
            case R.id.money_bt_charging://充值
                WXPayEntryActivity.navigation();
                break;
            case R.id.money_tv_record://充值记录
                RechargeRecordActivity.navigation();
                break;
        }
    }

    public void getWalletInfo() {
        GetWalletInfoCase getWalletInfoCase = new GetWalletInfoCase();
        getWalletInfoCase.createObservable(new SimpleSubscriber<WalletInfoResponse>() {

            @Override
            public void onNext(WalletInfoResponse walletInfoResponse) {
                AppInfosPreferences.get().setMoney(walletInfoResponse.content.money);
                refreshMoney();
            }
        });
    }
}
