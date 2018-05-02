package com.renren0351.presenter.login;

import com.renren0351.model.request.LoginRequest;
import com.renren0351.model.response.LoginResponse;
import com.renren0351.model.response.SimpleResponse;
import com.renren0351.presenter.usercase.LoginCase;
import com.renren0351.presenter.usercase.ResetPwdCase;

import java.lang.ref.WeakReference;
import java.util.Map;

import cn.com.leanvision.baseframe.rx.SimpleSubscriber;
import cn.com.leanvision.baseframe.util.LvPreconditions;
import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/********************************
 * Created by lvshicheng on 2017/3/1.
 ********************************/
public class LoginPresenter implements LoginContract.Presenter {

  private WeakReference<LoginContract.View> wrView;

  private LoginCase    loginCase;
  private ResetPwdCase resetPwdCase;

  private CompositeSubscription mSubscriptions;

  @Override
  public void hpLogin(final LoginRequest request) {
    LvPreconditions.checkNotNull(loginCase, "Must call attachView method first!");

    Observable<LoginResponse> observable = loginCase.createObservable(request);
    Subscription subscriber = observable.subscribe(new SimpleSubscriber<LoginResponse>() {
      @Override
      public void onStart() {
        super.onStart();
        if (isViewActive()) {
          getView().showLoading(null);
        }
      }

      @Override
      public void onError(Throwable e) {
        super.onError(e);
        if (isViewActive()) {
          getView().requestFailed(null);
          getView().showNormal();
        }
      }

      @Override
      public void onNext(LoginResponse loginResponse) {
        if (loginResponse.isSuccess()) {
          getView().loginSuccess(loginResponse.response.token, loginResponse.response.user_id);
        } else {
          getView().requestFailed(loginResponse.msg);
        }
      }

      @Override
      public void onCompleted() {
        if (isViewActive()) {
          getView().showNormal();
        }
      }
    });
    mSubscriptions.add(subscriber);
  }

  @Override
  public void hpResetPwd(Map<String, String> request) {
    resetPwdCase.params(request)
        .viewFilter(wrView)
        .createObservable(new SimpleSubscriber<SimpleResponse>() {

			@Override
          public void onNext(SimpleResponse simpleResponse) {
            super.onNext(simpleResponse);
          }
        });
  }

  @Override
  public void attachView(LoginContract.View view) {
    wrView = new WeakReference<>(view);

    mSubscriptions = new CompositeSubscription();

    loginCase = new LoginCase();
    resetPwdCase = new ResetPwdCase();
  }

  @Override
  public void detachView() {
    wrView.clear();

    mSubscriptions.clear();

    loginCase.unSubscribe();
    resetPwdCase.unSubscribe();
  }

  public boolean isViewActive() {
    return wrView.get() != null;
  }

  public LoginContract.View getView() {
    return wrView.get();
  }

  // FOT TEST
  public void setLoginCase(LoginCase loginCase) {
    this.loginCase = loginCase;
  }
}
