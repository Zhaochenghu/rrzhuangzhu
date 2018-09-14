package com.bxchongdian.presenter.usercase;

import com.bxchongdian.model.dagger.ApiComponentHolder;
import com.bxchongdian.model.request.RechargeRequest;
import com.bxchongdian.model.response.SimpleResponse;

import cn.com.leanvision.baseframe.model.usercase.UserCase;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import rx.Observable;

/********************************
 * Created by lvshicheng on 2017/4/26.
 ********************************/
public class RechargeCase extends UserCase<SimpleResponse, RechargeRequest> {

  @Override
  public Observable<SimpleResponse> interactor(RechargeRequest params) {
    return ApiComponentHolder.sApiComponent.apiService()
        .walletRecharge(params)
        .take(1)
        .compose(SchedulersCompat.<SimpleResponse>applyNewSchedulers());
  }
}
