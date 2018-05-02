package com.renren0351.model.bean;

/********************************
 * Created by lvshicheng on 2017/5/24.
 ********************************/
public class ItemStationBean extends FlexBoxBean {

  public String name;

  public ItemStationBean(String name) {
    this.name = name;
  }

  @Override
  public String getContent() {
    return name;
  }

  @Override
  public String getState() {
    return null;
  }
}
