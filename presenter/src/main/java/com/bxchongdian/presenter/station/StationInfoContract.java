package com.bxchongdian.presenter.station;

import com.bxchongdian.model.bean.StationInfoBean;
import com.bxchongdian.model.request.StartChargingRequest;
import com.bxchongdian.model.response.ChargingResponse;
import com.bxchongdian.presenter.LvIBasePresenter;
import com.bxchongdian.presenter.LvIBaseView;

/********************************
 * Created by lvshicheng on 2017/4/21.
 ********************************/
public interface StationInfoContract {

  interface View extends LvIBaseView {

    void queryStationInfoSuccess(StationInfoBean mStationInfoBean);

    void queryStationInfoFailed(String msg);

    void callBack(ChargingResponse.Charging charging);

    void startChargingFailed(String msg);
  }

  interface Presenter<R> extends LvIBasePresenter<R> {

    void queryStation(String qrCode);

    void startCharging(StartChargingRequest request);

    boolean isViewActive();

    R getView();
  }
}
