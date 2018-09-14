package com.bxchongdian.app.views.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bxchongdian.app.R;
import com.bxchongdian.app.utils.CountDownTimerUtils;
import com.bxchongdian.app.utils.ValidationUtils;
import com.bxchongdian.app.views.base.LvBaseAppCompatActivity;
import com.bxchongdian.model.request.RegisterRequest;
import com.bxchongdian.presenter.register.RegisterContract;
import com.bxchongdian.presenter.register.RegisterPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.leanvision.baseframe.util.LvCommonUtil;
import cn.com.leanvision.baseframe.util.LvTextUtil;

/**
 * Created by zhaochenghu on 2018/6/22 0022.
 */
@Route(path = "/mime/register")
public class RegisterActivity extends LvBaseAppCompatActivity implements RegisterContract.View {
    @BindView(R.id.reg_et_phone)
    EditText etPhone;
    @BindView(R.id.reg_et_code)
    EditText etCode;
    @BindView(R.id.reg_bt_send)
    Button   btSend;
    @BindView(R.id.reg_et_psd)
    EditText etPsd;
    @BindView(R.id.reg_et_again)
    EditText etPsdAgain;
    @BindView(R.id.reg_bt_register)
    Button   btRegister;

    private ValidationUtils   validate;
    private RegisterPresenter presenter;

    public static void navigation() {
        ARouter.getInstance().build("/mime/register").navigation();
    }

    @Override
    protected void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.aty_register);
        ButterKnife.bind(this);
        validate = new ValidationUtils(this);
    }

    @Override
    protected void initView() {
        initToolbarNav("注册");
    }

    @Override
    protected void initPresenter() {
        super.initPresenter();
        presenter = new RegisterPresenter();
        presenter.attachView(this);
    }

    @Override
    protected void destroyPresenter() {
        super.destroyPresenter();
        presenter.detachView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick({R.id.reg_bt_send, R.id.reg_bt_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reg_bt_send:
                if (!validate.phoneNumValidate(etPhone)){
                    return;
                }
                LvCommonUtil.hideSoftInput(this);
                // 服务器发送验证码
                String phone = etPhone.getText().toString();
                presenter.hpCaptcha(phone);
                // 开始倒计时
                CountDownTimerUtils timerUtils = new CountDownTimerUtils(btSend, 60000, 1000);
                timerUtils.start();
                break;
            case R.id.reg_bt_register:
                if (!validate.phoneNumValidate(etPhone)){
                    return;
                }

                if (!validate.captchaValidate(etCode)){
                    showToast("验证码错误");
                    return;
                }

                if (LvTextUtil.isEmpty(etPsd.getText().toString()) || !validate.psdValidate(etPsd)){
                    showToast("密码必须是数字和字母的组合");
                    return;
                }

                if (validate.isContainChinese(etPsd)){
                    showToast("密码不能包含汉字");
                    return;
                }

                if (!etPsd.getText().toString().equals(etPsdAgain.getText().toString())){
                    showToast("两次输入的密码不相同");
                    return;
                }
                showLoadingDialog();

                String num = etPhone.getText().toString();
                String psd = etPsd.getText().toString();
                String captcha = etCode.getText().toString();

                RegisterRequest request = new RegisterRequest(num, psd, captcha,"0023");
                presenter.hpRegister(request);
                break;
        }
    }

    @Override
    public void registerSuccess() {
        dismissLoadingDialog();
        showToast("注册成功");
        finish();
    }

    @Override
    public void getCaptchaSuccess() {


    }

    @Override
    public void getCaptchaFailed() {
        requestFailed(null);
    }

    @Override
    public void showLoading(String msg) {

    }

    @Override
    public void showNormal() {

    }

    @Override
    public void requestFailed(String msg) {
        if (LvTextUtil.isEmpty(msg)) {
            showToast(R.string.network_not_available);
        } else {
            showToast(msg);
        }
        dismissLoadingDialog();
    }
}
