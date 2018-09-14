package com.bxchongdian.app.views.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bxchongdian.app.LvAppConstants;
import com.bxchongdian.app.LvAppUtils;
import com.bxchongdian.app.R;
import com.bxchongdian.app.views.base.LvBaseAppCompatActivity;
import com.bxchongdian.model.dagger.ApiComponentHolder;
import com.bxchongdian.model.request.WxOrderRequest;
import com.bxchongdian.model.response.WxOrderResponse;
import com.bxchongdian.model.storage.AppInfosPreferences;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import cn.com.leanvision.baseframe.rx.SimpleSubscriber;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import cn.com.leanvision.baseframe.util.LvCommonUtil;

/********************************
 * Created by lvshicheng on 2017/4/19.
 ********************************/

public class RechargeActivity extends LvBaseAppCompatActivity {

    @BindViews({R.id.bt_money1, R.id.bt_money2, R.id.bt_money3})
    List<Button> btnMoneies;
    @BindView(R.id.tv_balance)
    TextView     tvBalance;
    @BindView(R.id.money_bt_charging)
    Button       btnPay;
    @BindView(R.id.tv_wechat)
    TextView     tvWeChat;
    @BindView(R.id.tv_alipay)
    TextView     tvAliPay;
    @BindView(R.id.et_money)
    EditText     etMoney;

    private int selectedMoney = 0;

    @Override
    protected void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.pay_card);
    }

    @Override
    protected void initView() {
        super.initView();
        initToolbarNav("在线充值");
        refreshMoney();
    }

    private void refreshMoney() {
        tvBalance.setText(String.format("￥%s", String.valueOf(AppInfosPreferences.get().getMoney())));
    }

    @OnClick({R.id.bt_money1, R.id.bt_money2, R.id.bt_money3, R.id.et_money})
    public void clickMoney(View view) {
        selectedMoney = -1;
        for (int i = 0; i < btnMoneies.size(); i++) {
            if (btnMoneies.get(i) == view) {
                selectedMoney = i;
                etMoney.setText("");
                etMoney.setFocusable(false);
                LvCommonUtil.hideSoftInput(this);
                btnMoneies.get(i).setBackgroundResource(R.drawable.btn_red_stroke);
            } else {
                btnMoneies.get(i).setBackgroundResource(R.drawable.btn_red_unfocused_stroke);
            }
        }
        if (etMoney == view) {
            LvCommonUtil.showSoftInput(this, etMoney);
        }
    }

    @OnClick(R.id.money_bt_charging)
    public void clickCharging() {
        showLoadingDialog("充值中，请等待...");
        btnPay.setEnabled(false);

        double rechargeMoney = 0;
        if (selectedMoney == 0) {
            LvAppUtils.addMoney(20);
            rechargeMoney = 20;
        } else if (selectedMoney == 1) {
            LvAppUtils.addMoney(50);
            rechargeMoney = 50;
        } else if (selectedMoney == 2) {
            LvAppUtils.addMoney(100);
            rechargeMoney = 100;
        } else {
            etMoney.getText().toString();
        }

//        RechargeCase rechargeCase = new RechargeCase();
//        rechargeCase.createObservable(new RechargeRequest(rechargeMoney))
//            .subscribe(new SimpleSubscriber<SimpleResponse>() {
//                @Override
//                public void onError(Throwable e) {
//                    super.onError(e);
//                }
//
//                @Override
//                public void onNext(SimpleResponse response) {
//                    refreshMoney();
//                    dismissLoadingDialog();
//                    showToast("充值成功！");
//                    btnPay.setEnabled(true);
//                }
//            });
        placeAnOrder(rechargeMoney);
    }

    @OnClick({R.id.tv_alipay, R.id.tv_wechat})
    public void clickPay(View view) {
        Drawable icon = getResources().getDrawable(R.drawable.ic_select);
        icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
        switch (view.getId()) {
            case R.id.tv_wechat:
                tvWeChat.setCompoundDrawables(null, null, icon, null);
                tvAliPay.setCompoundDrawables(null, null, null, null);
                break;
            case R.id.tv_alipay:
                tvWeChat.setCompoundDrawables(null, null, null, null);
                tvAliPay.setCompoundDrawables(null, null, icon, null);
                break;
        }
    }

    public volatile static String tradeNo = "";

    public void placeAnOrder(double rechargeMoney) {
        WxOrderRequest request = new WxOrderRequest();
        request.token = AppInfosPreferences.get().getToken();
        request.body = "充值0.1元";
        request.spbill_create_ip = "192.168.0.112";
        request.total_fee = String.valueOf(rechargeMoney);
        request.trade_type = "APP";

        ApiComponentHolder.sApiComponent
            .wxpayService()
            .placeAnOrder(request)
            .take(1)
            .compose(SchedulersCompat.<WxOrderResponse>applyNewSchedulers())
            .subscribe(new SimpleSubscriber<WxOrderResponse>() {
                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    dismissLoadingDialog();
                }

                @Override
                public void onNext(WxOrderResponse wxOrderResponse) {
                    dismissLoadingDialog();
                    if (wxOrderResponse.isSuccess()) {
                        tradeNo = wxOrderResponse.out_trade_no;
                        performWxLocalPay(wxOrderResponse);
                    }
                }
            });
    }

    private void performWxLocalPay(WxOrderResponse wxOrderResponse) {
        // TODO: 2017/6/26 走微信流程
        String appId = LvAppConstants.WX_APP_ID;
        final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
        boolean b = msgApi.registerApp(appId);
        if (b) {
            PayReq request = new PayReq();
            request.appId = wxOrderResponse.appid;
            request.partnerId = wxOrderResponse.partnerid; // 商户号
            request.prepayId = wxOrderResponse.prepayid; // 预支付交易会话ID
            request.packageValue = wxOrderResponse.packageStr;
            request.nonceStr = wxOrderResponse.noncestr;
            request.timeStamp = wxOrderResponse.timestamp;
            request.sign = wxOrderResponse.sign;
            msgApi.sendReq(request);
        }
    }
}
