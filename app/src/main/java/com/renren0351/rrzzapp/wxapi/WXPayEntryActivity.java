package com.renren0351.rrzzapp.wxapi;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.renren0351.rrzzapp.LvAppConstants;
import com.renren0351.rrzzapp.R;
import com.renren0351.rrzzapp.views.base.LvBaseAppCompatActivity;
import com.renren0351.model.dagger.ApiComponentHolder;
import com.renren0351.model.request.WxOrderQueryRequest;
import com.renren0351.model.request.WxOrderRequest;
import com.renren0351.model.response.WalletInfoResponse;
import com.renren0351.model.response.WxOrderQueryResponse;
import com.renren0351.model.response.WxOrderResponse;
import com.renren0351.model.storage.AppInfosPreferences;
import com.renren0351.presenter.usercase.GetWalletInfoCase;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.trello.rxlifecycle.ActivityEvent;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import cn.com.leanvision.baseframe.log.DebugLog;
import cn.com.leanvision.baseframe.rx.SimpleSubscriber;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import cn.com.leanvision.baseframe.util.LvCommonUtil;
import cn.com.leanvision.baseframe.util.LvTextUtil;
import rx.Observable;
import rx.Subscriber;

@Route(path = "/money/recharge")
public class WXPayEntryActivity extends LvBaseAppCompatActivity implements IWXAPIEventHandler {

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

    int  PAMONEY = 1;
    private int selectedMoney = 0; // 默认充值20元
    private IWXAPI api;

    public static void navigation() {
        ARouter.getInstance().build("/money/recharge").navigation();
    }

    @Override
    protected void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.pay_card);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    protected void initView() {
        initToolbarNav("在线充值");
        refreshMoney();

        api = WXAPIFactory.createWXAPI(this, LvAppConstants.WX_APP_ID);
        api.handleIntent(getIntent(), this);

        Observable.create(
            new Observable.OnSubscribe<View>() {
                @Override
                public void call(final Subscriber<? super View> subscriber) {
                    btnPay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            subscriber.onNext(v);
                        }
                    });
                }
            })
            .throttleFirst(1, TimeUnit.SECONDS)
            .compose(this.<View>bindUntilEvent(ActivityEvent.DESTROY))
            .subscribe(new SimpleSubscriber<View>() {
                @Override
                public void onNext(View v) {
                    // TODO: 2017/11/2 测试版本与正式版本需要修改，测试版本需注释 clickCharging()方法
                    clickCharging();
//                    showToast("这是测试版本，不能使用充值功能");
                }
            });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshMoney();
    }

    private void refreshMoney() {
        tvBalance.setText(String.format(Locale.getDefault(),"￥%.2f", AppInfosPreferences.get().getMoney() / 100f));
    }

    @OnClick({R.id.bt_money1, R.id.bt_money2, R.id.bt_money3, R.id.et_money})
    public void clickMoney(View view) {
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
            selectedMoney = -1;
            LvCommonUtil.showSoftInput(this, etMoney);
        }
    }

    double rechargeMoney = 0.0f; // 单位是分

    public void clickCharging() {
//        showLoadingDialog("充值中，请等待...");
//        btnPay.setEnabled(false);
        if (selectedMoney == 0) {
            rechargeMoney = 20_00;
        } else if (selectedMoney == 1) {
            rechargeMoney = 50_00;
        } else if (selectedMoney == 2) {
            rechargeMoney = 100_00;
        } else {
            rechargeMoney = Integer.parseInt(etMoney.getText().toString() + "00");
        }
        if (rechargeMoney > 100_000_00) {
            showToast("充值金额应小于10万元");
        } else if (rechargeMoney < 1) {
            showToast("充值金额不能为0");
        } else {
            placeAnOrder();
        }

    }

    @OnClick({R.id.tv_alipay, R.id.tv_wechat})
    public void clickPay(View view) {
        Drawable icon = getResources().getDrawable(R.drawable.ic_select);
        icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
        switch (view.getId()) {
            case R.id.tv_wechat:
                tvWeChat.setCompoundDrawables(null, null, icon, null);
                tvAliPay.setCompoundDrawables(null, null, null, null);
                PAMONEY = 1;
                break;
            case R.id.tv_alipay:
                tvWeChat.setCompoundDrawables(null, null, null, null);
                tvAliPay.setCompoundDrawables(null, null, icon, null);
                PAMONEY = 2;
                break;
        }
    }

    public String tradeNo = "";

    public void placeAnOrder() {
        if (PAMONEY == 1) {
            WXpay();
        } else {
            Alpay();
        }
    }
    private void Alpay() {
        Toast.makeText(WXPayEntryActivity.this,"加班加点开发中...",Toast.LENGTH_SHORT).show();
    }

    public void WXpay(){
        showLoadingDialog();
        WxOrderRequest request = new WxOrderRequest();
        request.token = AppInfosPreferences.get().getToken();
        request.body = String.format("充值%.00f元", rechargeMoney / 100.0f);
        request.spbill_create_ip = "192.168.0.112";
        request.total_fee = String.format("%.00f", rechargeMoney); // 充值是按照'角'来
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
        //  2017/6/26 走微信流程
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
            boolean b1 = msgApi.sendReq(request);
            if (!b1) {
                noticeNoWechatClient();
            }
        }
    }

    /**
     * ---------------------
     * 以下是检查支付结果的逻辑
     * ---------------------
     */
    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        DebugLog.log("onPayFinish, errCode = " + resp.errCode);
        getWalletInfo();
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            switch (resp.errCode) {
                case 0: // 成功支付之后才需要检查结果
                    checkPayResult();
                    break;
                case -1: // 支付失败
                    showPayResult("支付失败了，请检查网络后重试！");
                    break;
                case -2: // 支付取消
                    showPayResult("您取消了支付");
                    break;
            }
        }
    }

    private void noticeNoWechatClient() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WXPayEntryActivity.this);
        builder.setTitle(R.string.app_tip);
        builder.setMessage("微信客户端未安装");

        builder.setPositiveButton("知道了", null);
        builder.show();
    }

    private void showPayResult(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(WXPayEntryActivity.this);
        builder.setTitle(R.string.app_tip);
        if (LvTextUtil.isEmpty(msg)) {
            msg = "充值成功";
            builder.setMessage(getString(R.string.pay_result_callback_msg, msg));
        } else {
            builder.setMessage(getString(R.string.pay_result_callback_msg, msg));
        }

        builder.setNegativeButton("继续充值", null);
        builder.setPositiveButton("退出充值", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                WXPayEntryActivity.this.finish();
            }
        });
        builder.show();
    }

    private void checkPayResult() {
        showLoadingDialog("正在检查支付结果");

        WxOrderQueryRequest request = new WxOrderQueryRequest();
        request.out_trade_no = tradeNo;

        ApiComponentHolder.sApiComponent
            .wxpayService()
            .queryOrder(request)
            .take(1)
            .compose(SchedulersCompat.<WxOrderQueryResponse>applyNewSchedulers())
            .subscribe(new SimpleSubscriber<WxOrderQueryResponse>() {
                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    dismissLoadingDialog();
                }

                @Override
                public void onNext(WxOrderQueryResponse wxOrderQueryResponse) {
                    dismissLoadingDialog();
                    if (wxOrderQueryResponse.isSuccess()) {
                        showPayResult(wxOrderQueryResponse.trade_state_desc);
                    }
                }
            });
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