package com.bxchongdian.app.wxapi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alipay.sdk.app.PayTask;
import com.bxchongdian.app.LvAppConstants;
import com.bxchongdian.app.R;
import com.bxchongdian.app.alipay.PayResult;
import com.bxchongdian.app.utils.IntentUtils;
import com.bxchongdian.app.views.activities.AboutActivity;
import com.bxchongdian.app.views.base.LvBaseAppCompatActivity;
import com.bxchongdian.model.dagger.ApiComponentHolder;
import com.bxchongdian.model.request.WxOrderQueryRequest;
import com.bxchongdian.model.request.WxOrderRequest;
import com.bxchongdian.model.response.AliOrderResponse;
import com.bxchongdian.model.response.WalletInfoResponse;
import com.bxchongdian.model.response.WxOrderQueryResponse;
import com.bxchongdian.model.response.WxOrderResponse;
import com.bxchongdian.model.storage.AppInfosPreferences;
import com.bxchongdian.presenter.usercase.GetWalletInfoCase;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.trello.rxlifecycle.ActivityEvent;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import cn.com.leanvision.baseframe.log.DebugLog;
import cn.com.leanvision.baseframe.rx.SimpleSubscriber;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import cn.com.leanvision.baseframe.util.LvCommonUtil;
import cn.com.leanvision.baseframe.util.LvTextUtil;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import rx.Observable;
import rx.Subscriber;
/********************************
 * Created by zhaochenghu on 2018/09/11.
 ********************************/
@Route(path = "/money/recharge")
public class WXPayEntryActivity extends LvBaseAppCompatActivity implements IWXAPIEventHandler ,
        ActivityCompat.OnRequestPermissionsResultCallback{

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
    static int PAYWAY;
    private int selectedMoney = 0; // 默认充值20元
    private IWXAPI api;
    private static final int SDK_PAY_FLAG = 1;
    public final static int REQUEST_READ_PHONE_STATE = 1;

    public static void navigation() {
        ARouter.getInstance().build("/money/recharge").navigation();
    }

    @Override
    protected void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.pay_card);
//        支付宝沙盒测试
//        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
//        检查是否获取了联系人权限
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            //TODO
        }
        PAYWAY = 1;
    }
    //    重新onRequestPermissionsResult方法调用联系人
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //TODO
                } else {
                    showPermissionDialog();
                }
                break;
            default:
                break;
        }
    }

    public void showPermissionDialog() {
        new AlertDialog.Builder(this)
                .setMessage("支付宝需要获取您的联系人权限，否则无法正常支付")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        finish();
                        Toast.makeText(WXPayEntryActivity.this,"您已取消授权，支付宝无法正常使用",Toast.LENGTH_LONG).show();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        IntentUtils.turnToAppDetail(WXPayEntryActivity.this);
                    }
                })
                .setCancelable(false)
                .show();
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
        tvBalance.setText(String.format("￥%.2f", AppInfosPreferences.get().getMoney() / 100f));
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
                PAYWAY=1;
                break;
            case R.id.tv_alipay:
                tvWeChat.setCompoundDrawables(null, null, null, null);
                tvAliPay.setCompoundDrawables(null, null, icon, null);
                PAYWAY=2;
                break;
        }
    }

    public String tradeNo = "";

    public void placeAnOrder() {
        // Toast.makeText(this,""+etMoney.getText().toString(),Toast.LENGTH_SHORT).show();
        if(PAYWAY==1){
            Wxpay();
        }else {
            payV2();
//            alipay();
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    showLoadingDialog("正在检查支付结果");
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    WxOrderQueryRequest request = new WxOrderQueryRequest();
                    request.result = resultInfo;
                    ApiComponentHolder.sApiComponent
                            .wxpayService()
                            .queryOrderAlipay(request)
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
                                    if (wxOrderQueryResponse.pay.result == 1) {
                                        Toast.makeText(WXPayEntryActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                                        WXPayEntryActivity.this.finish();
                                    } else  {
                                        Toast.makeText(WXPayEntryActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                                    }
//                                        if (wxOrderQueryResponse.result == 1) {
//                                            Toast.makeText(WXPayEntryActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
//                                        } else {
//                                            Toast.makeText(WXPayEntryActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
//                                        }
//                                        WXPayEntryActivity.this.finish();
                                }
                            });
//                    String resultStatus = payResult.getResultStatus();
//                    // 判断resultStatus 为9000则代表支付成功
//                    if (TextUtils.equals(resultStatus, "9000")) {
//                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
//                        Toast.makeText(WXPayEntryActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
//                    } else {
//                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
//                        Toast.makeText(WXPayEntryActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
//                        //如果服务端调用成功 支付宝支付失败则调用服务端的扣除余额的接口  扣除的数据为刚刚充值的数据
//                    }
////                    WXPayEntryActivity.this.finish();
//                    break;
                }
                break;
            }
        }
    };

    //支付宝支付
    private void payV2() {
        showLoadingDialog();
        WxOrderRequest request = new WxOrderRequest();
        request.token = AppInfosPreferences.get().getToken();
        request.body = String.format("deposit%.00f yuan", rechargeMoney / 100.0f);
        request.spbill_create_ip = "192.168.0.112";
        request.total_fee = String.format("%.00f", rechargeMoney); // 充值是按照'角'来
        request.trade_type = "APP";

        ApiComponentHolder.sApiComponent
                .wxpayService()
                .placeAnOrderAlipay(request)
                .take(1)
                .compose(SchedulersCompat.<AliOrderResponse>applyNewSchedulers())
                .subscribe(new SimpleSubscriber<AliOrderResponse>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        dismissLoadingDialog();
                    }

                    @Override
                    public void onNext(AliOrderResponse aliOrderResponse) {
                        dismissLoadingDialog();
                        final String orderstring = aliOrderResponse.order_string;
                        Runnable payRunnable = new Runnable() {
                            @Override
                            public void run() {
                                PayTask alipay = new PayTask(WXPayEntryActivity.this);
                                Map<String, String> result = alipay.payV2(orderstring, true);
                                Log.i("msp", result.toString());

                                Message msg = new Message();
                                msg.what = SDK_PAY_FLAG;
                                msg.obj = result;
                                mHandler.sendMessage(msg);
                            }
                        };
                        Thread payThread = new Thread(payRunnable);
                        if(serverPay()){
                            payThread.start();
                        }else{
                            Toast.makeText(WXPayEntryActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
    }

    //支付宝测试支付
//    private void alipay(){
//        if (TextUtils.isEmpty(ALIAPPID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))) {
//            new android.app.AlertDialog.Builder(this).setTitle("警告").setMessage("需要配置APPID | RSA_PRIVATE")
//                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialoginterface, int i) {
//                            //
//                            finish();
//                        }
//                    }).show();
//            return;
//        }
//
//        /**
//         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
//         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
//         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
//         *
//         * orderInfo的获取必须来自服务端；
//         */
//        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
//        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(ALIAPPID, rsa2,rechargeMoney/100);
//        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);
//
//        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
//        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
//        final String orderInfo = orderParam + "&" + sign;
//        Log.e("orderInfo" , orderInfo.toString());
//        Runnable payRunnable = new Runnable() {
//
//            @Override
//            public void run() {
//                PayTask alipay = new PayTask(WXPayEntryActivity.this);
//                Map<String, String> result = alipay.payV2(orderInfo, true);
//                Log.i("msp", result.toString());
//
//                Message msg = new Message();
//                msg.what = SDK_PAY_FLAG;
//                msg.obj = result;
//                mHandler.sendMessage(msg);
//            }
//        };
//
//        Thread payThread = new Thread(payRunnable);
//        if(serverPay()){
//            payThread.start();
//        }else{
//            Toast.makeText(WXPayEntryActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
//        }
//    }

    private boolean serverPay() { return true; }
    //微信支付
    private void Wxpay() {
        showLoadingDialog();
        WxOrderRequest request = new WxOrderRequest();
        request.token = AppInfosPreferences.get().getToken();
        request.body = String.format("充值%.00f 元", rechargeMoney / 100.0f);
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