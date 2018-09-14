package com.bxchongdian.app.views.activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bxchongdian.app.R;
import com.bxchongdian.app.utils.CountDownTimerUtils;
import com.bxchongdian.app.utils.ValidationUtils;
import com.bxchongdian.app.views.base.LvBaseAppCompatActivity;
import com.bxchongdian.model.request.ForgotPwdRequest;
import com.bxchongdian.model.response.SimpleResponse;
import com.bxchongdian.presenter.usercase.ForgotPwdCase;
import com.bxchongdian.presenter.usercase.GetCaptchaCase;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cn.com.leanvision.baseframe.rx.SimpleSubscriber;

/**
 * Created by Administrator on 2017/2/20 0020.
 */
@Route(path = "/mime/forgotpsd")
public class ForgotPsdActivity extends LvBaseAppCompatActivity {

    @BindView(R.id.psd_et_phone)
    EditText etPhone;
    @BindView(R.id.psd_et_code)
    EditText etCode;
    @BindView(R.id.psd_bt_send)
    Button   btSend;
    @BindView(R.id.psd_et_psd)
    EditText etPsd;
    @BindView(R.id.psd_et_again)
    EditText etPsdAgain;
    @BindView(R.id.psd_bt_change)
    Button   btChange;

    private ValidationUtils validate;

    public static void navigation(String phone) {
        ARouter.getInstance()
            .build("/mime/forgotpsd")
            .withString("phone", phone)
            .navigation();
    }

    @Override
    protected void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.aty_forgot_psw);
        validate = new ValidationUtils(this);
    }

    @Override
    protected void initView() {
        initToolbarNav("忘记密码");

        btChange.setEnabled(false);
        String phone = getIntent().getStringExtra("phone");
        etPhone.setText(phone);
    }

    @OnTextChanged(value = {R.id.psd_et_phone, R.id.psd_et_code, R.id.psd_et_psd}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onTextChanged() {
        if (etPhone.getText().length() == 11 && validate.phoneNumValidate(etPhone) &&
                validate.captchaValidate(etCode) && validate.psdValidate(etPsd)){
            btChange.setEnabled(true);
        }else {
            btChange.setEnabled(false);
        }
    }

    @OnClick({R.id.psd_bt_send, R.id.psd_bt_change})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.psd_bt_change://确认密码
                if (validate.isContainChinese(etPsd)){
                    showToast("密码不能包含汉字");
                    return;
                }
                if (!etPsd.getText().toString().equals(etPsdAgain.getText().toString())){
                    showToast("两次输入的密码不相同");
                    return;
                }
                // 服务器验证
                forgotPwd();
                break;
            case R.id.psd_bt_send://发送验证码
                if (validate.phoneNumValidate(etPhone)) {
                    // 服务器发送验证码
                    CountDownTimerUtils timerUtils = new CountDownTimerUtils(btSend, 60000, 1000);
                    timerUtils.start();
                    getCaptcha(etPhone.getText().toString());
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (captchaCase != null) {
            captchaCase.unSubscribe();
        }
        if (forgotPwdCase != null) {
            forgotPwdCase.unSubscribe();
        }
    }

    GetCaptchaCase captchaCase;
    ForgotPwdCase  forgotPwdCase;

    private void getCaptcha(String phoneNum) {
        HashMap<String, Object> map = new HashMap<>(2);
        map.put("phone", phoneNum);
        map.put("type", 1); // 这里严格字段类型
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

    private void forgotPwd() {
        showLoadingDialog();

        if (forgotPwdCase == null) {
            forgotPwdCase = new ForgotPwdCase();
        }
        ForgotPwdRequest request = new ForgotPwdRequest();
        request.phone = etPhone.getText().toString();
        request.captcha = etCode.getText().toString();
        request.newPassword = etPsd.getText().toString();

        forgotPwdCase.params(request)
            .createObservable(new SimpleSubscriber<SimpleResponse>() {
                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    dismissLoadingDialog();
                    showToast(getString(R.string.network_not_available));
                }

                @Override
                public void onNext(SimpleResponse response) {
                    dismissLoadingDialog();
                    if (response.isSuccess()) {
                        showToast("修改成功，请重新登录");
                        btChange.setEnabled(false);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 2000);
                    } else {
                        showToast(response.msg);
                    }
                }
            });
    }

}
