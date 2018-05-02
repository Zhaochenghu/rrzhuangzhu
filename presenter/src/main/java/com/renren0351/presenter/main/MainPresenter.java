package com.renren0351.presenter.main;

import com.renren0351.model.response.SimpleResponse;
import com.renren0351.model.response.StationDetailResponse;
import com.renren0351.presenter.favor.FavorContract;
import com.renren0351.presenter.usercase.FavorCase;
import com.renren0351.presenter.usercase.GetSubstationSummaryCase;
import com.renren0351.presenter.usercase.UnFavorCase;

import java.lang.ref.WeakReference;

import cn.com.leanvision.baseframe.rx.SimpleSubscriber;

/********************************
 * Created by lvshicheng on 2017/3/8.
 ********************************/
public class MainPresenter implements MainContract.Presenter {

  WeakReference<MainContract.View> wrView;

  GetSubstationSummaryCase substationSummaryCase;
  FavorCase                favorCase;
  UnFavorCase              unFavorCase;

  @Override
  public void attachView(MainContract.View view) {
    wrView = new WeakReference<>(view);

    substationSummaryCase = new GetSubstationSummaryCase();
    favorCase = new FavorCase();
    unFavorCase = new UnFavorCase();
  }

  @Override
  public void detachView() {
    wrView.clear();

    substationSummaryCase.unSubscribe();
    favorCase.unSubscribe();
    unFavorCase.unSubscribe();
  }

  @Override
  public void getSubstationSummary(String stationId) {
    substationSummaryCase.params(stationId)
        .viewFilter(wrView)
        .createObservable(new SimpleSubscriber<StationDetailResponse>() {
          @Override
          public void onError(Throwable e) {
            super.onError(e);
            MainContract.View view = wrView.get();
            if (view != null) {
              view.requestFailed(null);
            }
          }

          @Override
          public void onNext(StationDetailResponse response) {
            MainContract.View view = wrView.get();
            if (response.isSuccess()) {
              view.getSummarySuccess();
            } else {
              view.requestFailed(response.msg);
            }
          }
        });
  }

  @Override
  public void saveFavor(String stationId) {
    favorCase.params(stationId)
        .viewFilter(wrView)
        .createObservable(new SimpleSubscriber<SimpleResponse>() {
          @Override
          public void onError(Throwable e) {
            super.onError(e);
            FavorContract.View view = wrView.get();
            if (view != null) {
              view.requestFailed(null);
            }
          }

          @Override
          public void onNext(SimpleResponse simpleResponse) {
            FavorContract.View view = wrView.get();
            if (simpleResponse.isSuccess()) {
              view.saveFavorSuccess();
            } else {
                //// FIXME: 2017/9/15 暂时这样处理
                if (simpleResponse.code == 1009){
                    view.saveFavorSuccess();
                }else {
                    view.requestFailed(simpleResponse.msg);
                }
              
            }
          }
        });
  }

  @Override
  public void clearFavor(String stationId) {
    unFavorCase.params(stationId)
        .viewFilter(wrView)
        .createObservable(new SimpleSubscriber<SimpleResponse>() {
          @Override
          public void onError(Throwable e) {
            FavorContract.View view = wrView.get();
            if (view != null) {
              view.requestFailed(null);
            }
          }

          @Override
          public void onNext(SimpleResponse response) {
            FavorContract.View view = wrView.get();
            if (response.isSuccess()) {
              view.clearFavorSuccess();
            } else {
              view.requestFailed(response.msg);
            }
          }
        });
  }
}
