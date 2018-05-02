package com.renren0351.rrzzapp.views.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.renren0351.rrzzapp.R;
import com.renren0351.rrzzapp.custom.OnPayPasswordListener;
import com.renren0351.rrzzapp.custom.PsdInputView;
import com.renren0351.rrzzapp.utils.CountDownTimerUtils;
import com.renren0351.rrzzapp.utils.ValidationUtils;
import com.renren0351.rrzzapp.views.base.LvBaseAppCompatActivity;
import com.renren0351.model.dagger.ApiComponentHolder;
import com.renren0351.model.request.PayPwdRequest;
import com.renren0351.model.response.MyWalletResponse;
import com.renren0351.model.response.SimpleResponse;
import com.renren0351.model.storage.AppInfosPreferences;
import com.renren0351.presenter.usercase.GetCaptchaCase;
import com.renren0351.presenter.usercase.MyWalletCase;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cn.com.leanvision.baseframe.rx.SimpleSubscriber;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import cn.com.leanvision.baseframe.util.LvCommonUtil;

/**
 * 2017/3/2 0002
 */
@Route(path = "/mime/pay_verify")
public class PaySettingVerify extends LvBaseAppCompatActivity {

    @BindView(R.id.tv_reset_pay)
    TextView     tvResetPay;
    @BindView(R.id.tv_pwd_pay_back)
    TextView     tvPwdPayBack;
    @BindView(R.id.payset_tv_phone)
    TextView     tvPhone;
    @BindView(R.id.payset_et_code)
    EditText     etCode;
    @BindView(R.id.payset_bt_send)
    Button       btSend;
    @BindView(R.id.payset_bt_verify)
    Button       btVerify;
    @BindView(R.id.ll_init_pay_pwd)
    LinearLayout llInitPayPwd;
    @BindView(R.id.view_set_pwd)
    PsdInputView viewSetPwd;
    @BindView(R.id.ll_set_pwd)
    View         rootViewSetPwd;
    @BindView(R.id.tv_title)
    TextView     tvTitle;
    @BindView(R.id.tv_notice)
    TextView     tvNotice;


    private ValidationUtils validate;

    public static void navigation() {
        ARouter.getInstance().build("/mime/pay_verify").navigation();
    }

    @Override
    protected void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.aty_paysetting_verify);
        ButterKnife.bind(this);
        validate = new ValidationUtils(this);
    }

    @Override
    protected void initView() {
        initToolbarNav("设置支付密码");

        String userName = AppInfosPreferences.get().getUserName();
        tvPhone.setText(
            String.format("%s****%s", userName.substring(0, 3), userName.substring(7, 11)));

        // 2017/6/22 先检验是否已经设置过密码
        showNothing();
        checkThePayPwdHasSet();
    }

    private void showNothing() {
        llInitPayPwd.setVisibility(View.GONE);
        btVerify.setVisibility(View.GONE);

        tvResetPay.setVisibility(View.INVISIBLE);
        tvPwdPayBack.setVisibility(View.INVISIBLE);
    }

    @OnTextChanged(value = {R.id.payset_et_code}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged() {
        if (validate.inputValidate(etCode, "请输入验证码")) {
            btVerify.setEnabled(true);
        } else {
            btVerify.setEnabled(false);
        }
    }

    @OnClick({R.id.payset_bt_send, R.id.payset_bt_verify, R.id.tv_reset_pay, R.id.tv_pwd_pay_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.payset_bt_send: // 获取验证码
                CountDownTimerUtils downTimerUtils = new CountDownTimerUtils(btSend, 60000, 1000);
                downTimerUtils.start();

                getCaptcha(AppInfosPreferences.get().getUserName());
                break;
            case R.id.payset_bt_verify: // 校验验证码
                // TODO: 2017/6/22 这里应该要CHECK验证码
                showViewPwdSet();
                break;
            case R.id.tv_reset_pay: // 修改支付密码
                showViewPwdReset();
                break;
            case R.id.tv_pwd_pay_back:  //找回支付密码
                showPwdPayBack();
                break;
        }
    }

    private void showPwdPayBack() {
        llInitPayPwd.setVisibility(View.VISIBLE);
        btVerify.setVisibility(View.VISIBLE);

        tvResetPay.setVisibility(View.GONE);
        tvPwdPayBack.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressedSupport() {
        super.onBackPressedSupport();
    }

    private String firstPwd;

    private volatile boolean isOldPwdChecked;

    private String oldPayPwd;

    private void showViewPwdSet() {
        LvCommonUtil.hideSoftInput(this);

        llInitPayPwd.setVisibility(View.GONE);
        btVerify.setVisibility(View.GONE);

        rootViewSetPwd.setVisibility(View.VISIBLE);
        viewSetPwd.dismissForgot();

        viewSetPwd.setOnPayPasswordListener(new OnPayPasswordListener() {
            @Override
            public void onPasswordInputFinish(String psd) {
                if (firstPwd == null) {
                    firstPwd = psd;
                    viewSetPwd.clearAllPsd();
                    tvNotice.setVisibility(View.GONE);

                    tvTitle.setText("请再次输入支付密码");
                } else {
                    if (firstPwd.equals(psd)) {
                        if (myWalletResponse.content.hasPassword) { // 忘记支付密码
                            forgotPayPwd(firstPwd);
                        } else { // 设置支付密码
                            setPayPwd(firstPwd);
                        }
                    } else { // 两次密码不一致
                        tvTitle.setText("请设置新密码，用于支付验证");
                        firstPwd = null;
                        viewSetPwd.clearAllPsd();

                        tvNotice.setText("两次密码不一致，请重新设置");
                        tvNotice.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onForgetPassword() {

            }
        });
    }

    private void showViewPwdReset() {
        LvCommonUtil.hideSoftInput(this);

        llInitPayPwd.setVisibility(View.GONE);
        btVerify.setVisibility(View.GONE);
        tvResetPay.setVisibility(View.GONE);
        tvPwdPayBack.setVisibility(View.GONE);

        rootViewSetPwd.setVisibility(View.VISIBLE);
        viewSetPwd.dismissForgot();

        tvTitle.setText("请输入支付密码");

        viewSetPwd.setOnPayPasswordListener(new OnPayPasswordListener() {
            @Override
            public void onPasswordInputFinish(String psd) {
                if (isOldPwdChecked) {
                    if (firstPwd == null) {
                        firstPwd = psd;
                        viewSetPwd.clearAllPsd();
                        tvNotice.setVisibility(View.GONE);

                        tvTitle.setText("请再次输入密码，以确认密码");
                    } else {
                        // TODO 设置支付密码
                        if (firstPwd.equals(psd)) {
                            resetPayPwd(firstPwd);
                        } else { // 两次密码不一致
                            tvTitle.setText("请设置新密码，用于支付验证");
                            firstPwd = null;
                            viewSetPwd.clearAllPsd();

                            tvNotice.setText("两次密码不一致，请重新设置");
                            tvNotice.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    // TODO: 2017/6/22 校验旧密码
                    checkOldPayPwd(psd);
                }
            }

            @Override
            public void onForgetPassword() {

            }
        });
    }

    MyWalletCase myWalletCase;

    private MyWalletResponse myWalletResponse;

    private void checkThePayPwdHasSet() {
        showLoadingDialog();

        if (myWalletCase == null) {
            myWalletCase = new MyWalletCase();
        }

        myWalletCase.createObservable(new SimpleSubscriber<MyWalletResponse>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                dismissLoadingDialog();
                showToast(getString(R.string.network_not_available));
            }

            @Override
            public void onNext(MyWalletResponse myWalletResponse) {
                dismissLoadingDialog();
                if (myWalletResponse.isSuccess()) {
                    PaySettingVerify.this.myWalletResponse = myWalletResponse;
                    if (myWalletResponse.content.hasPassword) {
                        llInitPayPwd.setVisibility(View.GONE);
                        btVerify.setVisibility(View.GONE);

                        tvResetPay.setVisibility(View.VISIBLE);
                        tvPwdPayBack.setVisibility(View.VISIBLE);
                    } else {
//                        llInitPayPwd.setVisibility(View.VISIBLE);
//                        btVerify.setVisibility(View.VISIBLE);
//
//                        tvResetPay.setVisibility(View.GONE);
//                        tvPwdPayBack.setVisibility(View.GONE);
                        showViewPwdSet();
                    }
                } else {
                    showToast(myWalletResponse.msg);
                }
            }
        });
    }

    /**
     * 修改支付密码
     */
    private void resetPayPwd(String pwd) {
        showLoadingDialog();

        PayPwdRequest payPwdRequest = new PayPwdRequest();
        payPwdRequest.new_password = pwd;
        payPwdRequest.old_password = oldPayPwd;

        ApiComponentHolder.sApiComponent.apiService()
            .resetPayPwd(payPwdRequest)
            .take(1)
            .compose(SchedulersCompat.<SimpleResponse>applyNewSchedulers())
            .subscribe(new SimpleSubscriber<SimpleResponse>() {
                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    dismissLoadingDialog();
                    viewSetPwd.clearAllPsd();
                    showToast(R.string.network_not_available);
                }

                @Override
                public void onNext(SimpleResponse response) {
                    dismissLoadingDialog();
                    viewSetPwd.clearAllPsd();
                    if (response.isSuccess()) {
                        showToast("密码设置成功");
                        finish();
                    } else {
                        tvTitle.setText("请设置新密码，用于支付验证");
                        firstPwd = null;
                        viewSetPwd.clearAllPsd();

                        tvNotice.setText(response.msg);
                        tvNotice.setVisibility(View.VISIBLE);
                    }
                }
            });
    }

    /**
     * 检查旧密码
     */
    private void checkOldPayPwd(final String pwd) {
        this.oldPayPwd = pwd;
        showLoadingDialog();
        ApiComponentHolder.sApiComponent.apiService()
            .checkPayPwd(new PayPwdRequest(pwd))
            .take(1)
            .compose(SchedulersCompat.<SimpleResponse>applyNewSchedulers())
            .subscribe(new SimpleSubscriber<SimpleResponse>() {
                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    dismissLoadingDialog();
                    viewSetPwd.clearAllPsd();
                    showToast(R.string.network_not_available);
                }

                @Override
                public void onNext(SimpleResponse response) {
                    dismissLoadingDialog();
                    viewSetPwd.clearAllPsd();
                    if (response.isSuccess()) {
                        isOldPwdChecked = true;
                        firstPwd = null;
                        tvTitle.setText("请设置新密码，用于支付验证");
                    } else {
                        showToast(response.msg);
                    }
                }
            });
    }

    /**
     * 设置支付密码
     */
    private void setPayPwd(String pwd) {
        showLoadingDialog();
        ApiComponentHolder.sApiComponent.apiService()
            .setPayPwd(new PayPwdRequest(pwd))
            .take(1)
            .compose(SchedulersCompat.<SimpleResponse>applyNewSchedulers())
            .subscribe(new SimpleSubscriber<SimpleResponse>() {
                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    showToast(R.string.network_not_available);
                    dismissLoadingDialog();
                }

                @Override
                public void onNext(SimpleResponse response) {
                    dismissLoadingDialog();
                    if (response.isSuccess()) {
                        showToast("设置成功");
                        finish();
                    } else {
                        showToast(response.msg);
                    }
                }
            });

    }

    /**
     * 忘记支付密码
     */
    private void forgotPayPwd(String pwd) {
        showLoadingDialog();
        ApiComponentHolder.sApiComponent.apiService()
            .forgotPayPwd(new PayPwdRequest(pwd, etCode.getText().toString()))
            .take(1)
            .compose(SchedulersCompat.<SimpleResponse>applyNewSchedulers())
            .subscribe(new SimpleSubscriber<SimpleResponse>() {
                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    showToast(R.string.network_not_available);
                    dismissLoadingDialog();
                }

                @Override
                public void onNext(SimpleResponse response) {
                    dismissLoadingDialog();
                    if (response.isSuccess()) {
                        showToast("设置成功");
                        finish();
                    } else {
                        showToast(response.msg);
                    }
                }
            });
    }

    GetCaptchaCase captchaCase;

    private void getCaptcha(String phoneNum) {
        HashMap<String, Object> map = new HashMap<>(2);
        map.put("phone", phoneNum);
        map.put("type", 2); // 这里严格字段类型
        if (captchaCase == null) {
            captchaCase = new GetCaptchaCase();
        }
        captchaCase.params(map)
            .createObservable(new SimpleSubscriber<SimpleResponse>() {
                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    showToast(getString(R.string.network_not_available));
                }

                @Override
                public void onNext(SimpleResponse response) {
                    if (response.isSuccess()) {

                    } else {
                        showToast(response.msg);
                    }
                }
            });
    }
}
