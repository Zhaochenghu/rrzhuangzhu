package com.renren0351.rrzzapp.views.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.renren0351.rrzzapp.BuildConfig;
import com.renren0351.rrzzapp.R;
import com.renren0351.rrzzapp.event.RefreshUserCollectionEvent;
import com.renren0351.rrzzapp.utils.LvSpfEncryption;
import com.renren0351.rrzzapp.utils.ValidationUtils;
import com.renren0351.rrzzapp.views.base.LvBaseAppCompatActivity;
import com.renren0351.model.LvRepository;
import com.renren0351.model.dagger.ApiComponentHolder;
import com.renren0351.model.request.LoginRequest;
import com.renren0351.model.response.SubstationsResponse;
import com.renren0351.model.storage.AppInfosPreferences;
import com.renren0351.presenter.login.LoginContract;
import com.renren0351.presenter.login.LoginPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cn.com.leanvision.baseframe.rx.RxBus;
import cn.com.leanvision.baseframe.rx.SimpleSubscriber;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import cn.com.leanvision.baseframe.security.MD5Helper;
import cn.com.leanvision.baseframe.util.LvTextUtil;
import rx.functions.Action1;

/********************************
 * Created by lvshicheng on 2017/2/13.
 ********************************/
@Route(path = "/mime/login")
public class LoginActivity extends LvBaseAppCompatActivity implements LoginContract.View {

    @BindView(R.id.login_et_phone)
    EditText etPhone;
    @BindView(R.id.login_et_psd)
    EditText etPsd;
    @BindView(R.id.login_bt_forgot)
    Button   btForgot;
    @BindView(R.id.login_bt_login)
    Button   btLogin;
    @BindView(R.id.login_tv_register)
    TextView tvRegister;

    private ValidationUtils validate;
    private LoginPresenter  presenter;
    private boolean isPrompt;

    public static void navigation(boolean isPrompt) {
        ARouter.getInstance().build("/mime/login")
                .withBoolean("isPrompt", isPrompt)
                .navigation();
    }

    @Override
    protected void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.aty_login);
        ButterKnife.bind(this);
    }

    @Override
    protected void initPresenter() {
        presenter = new LoginPresenter();
        presenter.attachView(this);
    }

    @Override
    protected void destroyPresenter() {
        super.destroyPresenter();
        presenter.detachView();
    }

    @Override
    protected void initView() {
        validate = new ValidationUtils(this);
        btLogin.setEnabled(false);
        isPrompt = getIntent().getBooleanExtra("isPrompt", false);
        if (isPrompt) {
            new AlertDialog.Builder(this)
                    .setTitle("温馨提示")
                    .setMessage("您的账号在其他地方登陆，若非本人操作请修改密码后重新登录！")
                    .setNegativeButton("确定", null)
                    .setCancelable(false)
                    .show();
        }

        if (BuildConfig.DEBUG) {
            etPhone.setText("15611519891");
            etPsd.setText("lxy123");
        }

    }

    @OnTextChanged(
        value = {R.id.login_et_phone, R.id.login_et_psd},
        callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged() {
        if (etPhone.getText().toString().length() >= 11){
            //手机号格式正确，登录按钮才能点击
            if (validate.phoneNumValidate(etPhone)) {
                btLogin.setEnabled(true);
            } else {
                btLogin.setEnabled(false);
            }
        }else {
            btLogin.setEnabled(false);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick({R.id.login_bt_forgot, R.id.login_bt_login, R.id.login_tv_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_bt_forgot:  //
                ForgotPsdActivity.navigation(etPhone.getText().toString());
                break;
            case R.id.login_bt_login:
                String phone = etPhone.getText().toString();
                String pwd = etPsd.getText().toString();
                if (LvTextUtil.isEmpty(pwd)) {
                    showToast("请输入密码");
                } else if (validate.isContainChinese(etPsd)){
                    showToast("密码不能包含汉字");
                    return;
                }else {
                    LoginRequest request = new LoginRequest(phone, pwd);
                    presenter.hpLogin(request);
                }
                break;
            case R.id.login_tv_register:
                RegisterActivity.navigation();
                break;
        }
    }

    /**
     * -----------------
     * LoginContract.View impl
     * ----------------
     */
    @Override
    public void loginSuccess(String token, String userId) {
        // 保存一下数据
        String username = etPhone.getText().toString();
        //设置秘钥 内部调用了AppInfosPreferences.get().setSecretKey()方法
        LvSpfEncryption.setSecretKey(MD5Helper.getMD5String(username));

        String pwd = etPsd.getText().toString();
        AppInfosPreferences.get().setUserName(username);
        AppInfosPreferences.get().setPwd(pwd);
        AppInfosPreferences.get().setToken(token);
        AppInfosPreferences.get().setUid(userId);

        getSubstations();
        // 登录成功
        finish();
    }

    @Override
    public void showLoading(String msg) {
        showLoadingDialog();
    }

    @Override
    public void showNormal() {
        dismissLoadingDialog();
    }

    @Override
    public void requestFailed(String msg) {
        if (LvTextUtil.isEmpty(msg)) {
            showToast(R.string.network_not_available);
        } else {
            showToast(msg);
        }
    }

    /**
     * 重新获取充电站数据
     */
    private void getSubstations() {
        ApiComponentHolder.sApiComponent
            .apiService()
            .getSubstation()
            .compose(SchedulersCompat.<SubstationsResponse>applyNewSchedulers())
            .doOnNext(new Action1<SubstationsResponse>() {
                @Override
                public void call(SubstationsResponse response) {

                    LvRepository.getInstance().refreshSubstations(response.substations);
                    RxBus.getInstance().postEvent(new RefreshUserCollectionEvent());
                }
            })
            .subscribe(new SimpleSubscriber<SubstationsResponse>() {
            });
    }
}
