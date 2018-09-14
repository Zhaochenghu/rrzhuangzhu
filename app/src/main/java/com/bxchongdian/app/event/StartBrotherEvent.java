package com.bxchongdian.app.event;

import cn.com.leanvision.baseframe.rx.event.BaseBusEvent;
import cn.com.leanvision.fragmentation.SupportFragment;

/********************************
 * Created by lvshicheng on 2016/12/14.
 * <p>
 * 跳转下一个fragment事件
 ********************************/
public class StartBrotherEvent extends BaseBusEvent {

  public String          title;
  public SupportFragment targetFragment;

  public StartBrotherEvent(SupportFragment targetFragment) {
    this.targetFragment = targetFragment;
  }
}
