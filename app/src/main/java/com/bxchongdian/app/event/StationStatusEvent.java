package com.bxchongdian.app.event;

import com.bxchongdian.model.response.StationStatusResponse;

/********************************
 * Created by lvshicheng on 2017/4/26.
 ********************************/
public class StationStatusEvent {

  public StationStatusResponse.StationStatus stationStatus;

  public StationStatusEvent(StationStatusResponse.StationStatus stationStatus) {
    this.stationStatus = stationStatus;
  }
}
