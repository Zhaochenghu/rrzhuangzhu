package com.renren0351.presenter.usercase;

import com.renren0351.model.dagger.ApiComponentHolder;
import com.renren0351.model.request.CardRequest;
import com.renren0351.model.response.CardResponse;

import cn.com.leanvision.baseframe.model.usercase.UserCase;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import rx.Observable;

/********************************
 * Created by lvshicheng on 2017/4/11.
 ********************************/
public class BindCardCase extends UserCase<CardResponse, CardRequest> {

  @Override
  public Observable<CardResponse> interactor(CardRequest params) {
    return ApiComponentHolder.sApiComponent.apiService()
        .bindCard(params)
        .take(1)
        .compose(SchedulersCompat.<CardResponse>applyNewSchedulers());
  }
}
