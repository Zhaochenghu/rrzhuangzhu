package com.renren0351.presenter.login;

import com.renren0351.model.request.LoginRequest;
import com.renren0351.model.response.LoginResponse;
import com.renren0351.presenter.usercase.LoginCase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Observable;
import rx.Subscriber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/********************************
 * Created by lvshicheng on 2017/4/13.
 *
 * @Mock 这个是完全自己重新实现的
 *
 * @Spy 感觉类似包裹一个类
 *
 ********************************/
public class LoginPresenterTest {

  @Mock
  private LoginContract.View view;
  @Mock
  private LoginCase          loginCase;

  private LoginResponse  loginResponse;
  private LoginPresenter loginPresenter;

  @Before
  public void setupPresenter() {
    MockitoAnnotations.initMocks(this);

    loginPresenter = new LoginPresenter();
    loginPresenter.attachView(view);
    loginPresenter.setLoginCase(loginCase);

    loginResponse = new LoginResponse();
    loginResponse.response.token = "";
    loginResponse.response.user_id = "";
  }

  /**
   * -------------------
   * 登录部分测试
   * -------------------
   */

  @Test
  public void clickOnLogin() {
    preLogin(Observable.just(loginResponse));

    assertEquals("View must be active!", true, loginPresenter.isViewActive());
    assertNotNull("View must not be null!", loginPresenter.getView());
    verify(view).showLoading("");
    verify(view).showNormal();
  }

  @Test
  public void login_successfully() {
    loginResponse.makeSuccess();
    Observable<LoginResponse> observable = Observable.just(loginResponse);
    preLogin(observable);

    verify(view).loginSuccess(anyString(), anyString());
  }

  @Test
  public void login_failed() {
    loginResponse.code = -1;
    Observable<LoginResponse> observable = Observable.just(loginResponse);
    preLogin(observable);

    verify(view).requestFailed(loginResponse.msg);
  }

  @Test
  public void login_error() {
    Observable<LoginResponse> observable = Observable.create(new Observable.OnSubscribe<LoginResponse>() {
      @Override
      public void call(Subscriber<? super LoginResponse> subscriber) {
        subscriber.onError(new Throwable("Mock error, you can ignore this!"));
      }
    });
    preLogin(observable);
    verify(view).requestFailed(null);
    verify(view).showNormal();
  }

  private void preLogin(Observable<LoginResponse> observable) {
    LoginRequest request = new LoginRequest("18610935308", "123456","0021");

    when(loginCase.params(request)).thenReturn(loginCase);
    when(loginCase.createObservable(request)).thenReturn(observable);

    loginPresenter.hpLogin(request);
  }
}
