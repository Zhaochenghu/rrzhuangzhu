package com.bxchongdian.app.event;

import com.bxchongdian.model.bean.SubstationBean;

import cn.com.leanvision.baseframe.rx.event.BaseBusEvent;

/********************************
 * Created by lvshicheng on 2017/2/14.
 ********************************/
public class ShowChargingInfoEvent extends BaseBusEvent {

  public boolean isShow;

  public SubstationBean substationBean;

  public ShowChargingInfoEvent(boolean isShow) {
    this.isShow = isShow;
  }

  public ShowChargingInfoEvent(boolean isShow, SubstationBean substationBean) {
    this(isShow);
    this.substationBean = substationBean;
  }
}
