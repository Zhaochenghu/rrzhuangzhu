package com.renren0351.presenter.main;

import com.renren0351.model.bean.StationDetailBean;
import com.renren0351.presenter.LvIBasePresenter;
import com.renren0351.presenter.LvIBaseView;

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
