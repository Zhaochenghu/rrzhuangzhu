package com.bxchongdian.presenter.login;

import com.bxchongdian.model.request.LoginRequest;
import com.bxchongdian.presenter.LvIBasePresenter;
import com.bxchongdian.presenter.LvIBaseView;

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
