package com.renren0351.presenter.login;

import com.renren0351.model.request.LoginRequest;
import com.renren0351.presenter.LvIBasePresenter;
import com.renren0351.presenter.LvIBaseView;

import java.util.Map;

/********************************
 * Created by lvshicheng on 2017/2/27.
 ********************************/
public interface LoginContract {

  interface View extends LvIBaseView {

    void loginSuccess(String token, String userId);
  }

  interface Presenter extends LvIBasePresenter<View> {

    void hpLogin(LoginRequest request);

    void hpResetPwd(Map<String, String> request);
  }
}
