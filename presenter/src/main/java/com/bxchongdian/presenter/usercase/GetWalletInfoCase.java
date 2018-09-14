package com.bxchongdian.presenter.usercase;

import com.bxchongdian.model.dagger.ApiComponentHolder;
import com.bxchongdian.model.response.WalletInfoResponse;

import cn.com.leanvision.baseframe.model.usercase.UserCase;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import rx.Observable;

/********************************
 * Created by lvshicheng on 2017/4/26.
 ********************************/
public class GetWalletInfoCase extends UserCase<WalletInfoResponse, String> {

  @Override
  public Observable<WalletInfoResponse> interactor(String params) {
    return ApiComponentHolder.sApiComponent.apiService()
        .getWalletInfo()
        .take(1)
        .compose(SchedulersCompat.<WalletInfoResponse>applyNewSchedulers());
  }
}
