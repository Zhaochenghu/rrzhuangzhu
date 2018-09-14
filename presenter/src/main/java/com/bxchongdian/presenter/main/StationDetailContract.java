package com.bxchongdian.presenter.main;

import com.bxchongdian.model.bean.StationDetailBean;
import com.bxchongdian.presenter.LvIBasePresenter;
import com.bxchongdian.presenter.LvIBaseView;

/********************************
 * Created by lvshicheng on 2017/4/26.
 ********************************/
public interface StationDetailContract {

  interface View extends LvIBaseView {

    void getSummarySuccess(StationDetailBean stationDetailBean);

  }

  interface Presenter<R> extends LvIBasePresenter<R> {

    void getSubstationSummary(String stationId);

    boolean isViewActive();

    R getView();
  }
}
