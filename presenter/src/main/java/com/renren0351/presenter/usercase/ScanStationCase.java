package com.renren0351.presenter.usercase;

import com.renren0351.model.dagger.ApiComponentHolder;
import com.renren0351.model.response.StationInfoResponse;

import cn.com.leanvision.baseframe.model.usercase.UserCase;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import rx.Observable;

/********************************
 * Created by lvshicheng on 2017/4/21.
 ********************************/
public class ScanStationCase extends UserCase<StationInfoResponse, String> {

  @Override
  public Observable<StationInfoResponse> interactor(String params) {
    return
        ApiComponentHolder.sApiComponent.apiService()
            .queryStationInfo(params)
            .take(1)
            .compose(SchedulersCompat.<StationInfoResponse>applyNewSchedulers());
  }
}
