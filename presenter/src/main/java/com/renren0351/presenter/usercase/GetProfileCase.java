package com.renren0351.presenter.usercase;

import com.renren0351.model.dagger.ApiComponentHolder;
import com.renren0351.model.response.ProfileResponse;

import cn.com.leanvision.baseframe.model.usercase.UserCase;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import rx.Observable;

/********************************
 * Created by lvshicheng on 2017/3/1.
 ********************************/
public class GetProfileCase extends UserCase<ProfileResponse, Object> {

  @Override
  public Observable<ProfileResponse> interactor(Object params) {
    return ApiComponentHolder.sApiComponent
        .apiService()
        .getProfile()
        .take(1)
        .compose(SchedulersCompat.<ProfileResponse>applyNewSchedulers());
  }
}
