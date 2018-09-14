package com.bxchongdian.app.event;

import cn.com.leanvision.baseframe.rx.event.BaseBusEvent;

/********************************
 * Created by lvshicheng on 2017/4/12.
 ********************************/
public class ShowNavListEvent extends BaseBusEvent {

  public double lat;
  public double lng;

  public ShowNavListEvent(double lat, double lng) {
    this.lat = lat;
    this.lng = lng;
  }
}
