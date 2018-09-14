package com.bxchongdian.presenter.main;

import com.bxchongdian.presenter.favor.FavorContract;

/********************************
 * Created by lvshicheng on 2017/3/8.
 ********************************/
public interface MainContract {

  interface View extends FavorContract.View {

    void getSummarySuccess();
  }

  interface Presenter extends FavorContract.Presenter<View> {

    void getSubstationSummary(String stationId);

  }
}
