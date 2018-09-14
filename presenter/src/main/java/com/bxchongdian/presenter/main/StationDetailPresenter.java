package com.bxchongdian.presenter.main;

import com.bxchongdian.model.response.StationDetailResponse;
import com.bxchongdian.presenter.usercase.GetSubstationSummaryCase;

import java.lang.ref.WeakReference;

import cn.com.leanvision.baseframe.log.DebugLog;
import cn.com.leanvision.baseframe.rx.SimpleSubscriber;

/********************************
 * Created by lvshicheng on 2017/4/26.
 ********************************/
public class StationDetailPresenter implements StationDetailContract.Presenter<StationDetailContract.View> {

  private WeakReference<StationDetailContract.View> wrView;
  private GetSubstationSummaryCase                  substationSummaryCase;

  @Override
  public void attachView(StationDetailContract.View view) {
    wrView = new WeakReference<>(view);
    substationSummaryCase = new GetSubstationSummaryCase();
  }

  @Override
  public void detachView() {
    wrView.clear();
    substationSummaryCase.unSubscribe();
  }

  @Override
  public void getSubstationSummary(String stationId) {
    substationSummaryCase.createObservable(stationId)
        .subscribe(new SimpleSubscriber<StationDetailResponse>() {

          @Override
          public void onStart() {
            if (isViewActive()) {
              getView().showLoading("获取详细信息");
            }
          }

          @Override
          public void onError(Throwable e) {
            super.onError(e);
              e.printStackTrace();
              DebugLog.log("----------------------------------------------------出异常了");
            if (isViewActive()) {
              getView().showNormal();
              getView().requestFailed(null);
            }
          }

          @Override
          public void onNext(StationDetailResponse stationDetailResponse) {
            if (isViewActive()) {
              getView().showNormal();
              if (stationDetailResponse.isSuccess()) {
                getView().getSummarySuccess(stationDetailResponse.content);
              } else {
                getView().requestFailed(stationDetailResponse.msg);
              }
            }
          }
        });
  }

  @Override
  public boolean isViewActive() {
    return wrView.get() != null;
  }

  @Override
  public StationDetailContract.View getView() {
    return wrView.get();
  }
}
