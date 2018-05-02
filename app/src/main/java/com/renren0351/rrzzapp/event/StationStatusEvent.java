package com.renren0351.rrzzapp.event;

import com.renren0351.model.response.StationStatusResponse;

/********************************
 * Created by lvshicheng on 2017/4/26.
 ********************************/
public class StationStatusEvent {

  public StationStatusResponse.StationStatus stationStatus;

  public StationStatusEvent(StationStatusResponse.StationStatus stationStatus) {
    this.stationStatus = stationStatus;
  }
}
