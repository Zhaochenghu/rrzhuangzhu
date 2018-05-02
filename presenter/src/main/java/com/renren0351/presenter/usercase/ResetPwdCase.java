package com.renren0351.presenter.usercase;

import com.renren0351.model.dagger.ApiComponentHolder;
import com.renren0351.model.response.SimpleResponse;

import java.util.Map;

import cn.com.leanvision.baseframe.model.usercase.UserCase;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import rx.Observable;

/********************************
 * Created by lvshicheng on 2017/3/1.
 ********************************/
public class ResetPwdCase extends UserCase<SimpleResponse, Map<String, String>> {

  @Override
  public Observable<SimpleResponse> interactor(Map<String, String> params) {
    return ApiComponentHolder.sApiComponent
        .apiService()
        .resetPwd(params)
        .take(1)
        .compose(SchedulersCompat.<SimpleResponse>applyNewSchedulers());
  }
}
