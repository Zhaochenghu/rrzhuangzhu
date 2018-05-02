package com.renren0351.presenter.usercase;

import com.renren0351.model.dagger.ApiComponentHolder;
import com.renren0351.model.response.StationDetailResponse;

import cn.com.leanvision.baseframe.model.usercase.UserCase;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import rx.Observable;

/********************************
 * Created by lvshicheng on 2017/3/8.
 ********************************/
public class GetSubstationSummaryCase extends UserCase<StationDetailResponse, String> {
  
  @Override
  public Observable<StationDetailResponse> interactor(String params) {
    return ApiComponentHolder.sApiComponent.apiService()
        .getSubstationSummary(params)
        .take(1)
        .compose(SchedulersCompat.<StationDetailResponse>applyNewSchedulers());
  }
}
