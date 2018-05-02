package com.renren0351.presenter.station;

import com.renren0351.model.bean.StationInfoBean;
import com.renren0351.model.request.StartChargingRequest;
import com.renren0351.model.response.ChargingResponse;
import com.renren0351.presenter.LvIBasePresenter;
import com.renren0351.presenter.LvIBaseView;

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
