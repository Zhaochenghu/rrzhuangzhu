package com.renren0351.presenter.usercase;

import com.renren0351.model.dagger.ApiComponentHolder;
import com.renren0351.model.response.SimpleResponse;

import cn.com.leanvision.baseframe.model.usercase.UserCase;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import rx.Observable;

/********************************
 * Created by lvshicheng on 2017/3/8.
 ********************************/
public class UnFavorCase extends UserCase<SimpleResponse, String> {

  @Override
  public Observable<SimpleResponse> interactor(String params) {
    return ApiComponentHolder.sApiComponent.apiService()
        .unFavorSubstation(params)
        .take(1)
        .compose(SchedulersCompat.<SimpleResponse>applyNewSchedulers());
  }
}
