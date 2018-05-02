package com.renren0351.rrzzapp.views.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.renren0351.rrzzapp.R;
import com.renren0351.rrzzapp.utils.LvSpfEncryption;
import com.renren0351.rrzzapp.utils.ValidationUtils;
import com.renren0351.rrzzapp.views.base.LvBaseAppCompatActivity;
import com.renren0351.model.response.SimpleResponse;
import com.renren0351.model.storage.AppInfosPreferences;
import com.renren0351.presenter.usercase.ResetPwdCase;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cn.com.leanvision.baseframe.rx.SimpleSubscriber;

/********************************
 * Created by lvshicheng on 2017/7/4.
 ********************************/
@Route(path = "/mime/changepsd")
public class ChangePswActivity extends LvBaseAppCompatActivity {

    @BindView(R.id.et_psd_old)
    EditText etPsdOld;
    @BindView(R.id.et_psd_new)
    EditText etPsdNew;
    @BindView(R.id.et_psd_again)
    EditText etPsdAgain;
    @BindView(R.id.psd_bt_change)
    Button   psdBtChange;

    private ValidationUtils validationUtils;

    public static void navigation() {
        ARouter.getInstance()
            .build("/mime/changepsd")
            .navigation();
    }

    @Override
    protected void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.aty_change_psw);

        validationUtils = new ValidationUtils(getApplicationContext());
    }

    @Override
    protected void initView() {
        initToolbarNav("修改密码");
    }

    @OnTextChanged(value = {R.id.et_psd_old, R.id.et_psd_new}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onTextChanged() {
        psdBtChange.setEnabled(validationUtils.psdValidate(etPsdOld) && validationUtils.psdValidate(etPsdNew));
    }

    @OnClick(R.id.psd_bt_change)
    public void clickChange() {
        if (validationUtils.isContainChinese(etPsdOld) || validationUtils.isContainChinese(etPsdNew)){
            showToast("密码不能包含汉字");
            return;
        }
        if (etPsdNew.getText().toString().trim().equals(etPsdAgain.getText().toString().trim())){
            changePsw();
        }else {
            showToast("确认密码与新密码不相同");
        }

    }

    ResetPwdCase resetPwdCase;

    private void changePsw() {
        showLoadingDialog();
        if (resetPwdCase == null) {
            resetPwdCase = new ResetPwdCase();
        }

        final String pwdNew = etPsdNew.getText().toString();
        Map<String, String> request = new HashMap<>();
        request.put("oldPassword", etPsdOld.getText().toString());
        request.put("newPassword", pwdNew);

        resetPwdCase.params(request)
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
                        showToast("修改成功");
                        //清除用户数据
                        AppInfosPreferences.get().setHeaderUrl("");
                        AppInfosPreferences.get().setToken("");
                        AppInfosPreferences.get().setUid("");
                        AppInfosPreferences.get().setUserName("");
                        LvSpfEncryption.setSecretKey("");
                        //重新登录
                        LoginActivity.navigation(false);
                        finish();
                    } else {
                        showToast(response.msg);
                    }
                }
            });
    }
}
