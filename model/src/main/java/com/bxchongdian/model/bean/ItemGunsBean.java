package com.bxchongdian.model.bean;

/********************************
 * Created by lvshicheng on 2017/5/22.
 ********************************/
public class ItemGunsBean extends FlexBoxBean {

  public int position;
  public String cpId;

  public int    gunId;
  public String state;

  public ItemGunsBean(int gunId, String cpId, String state, int position) {
    this.gunId = gunId;
    this.cpId = cpId;
    this.state = state;
    this.position = position;
  }

  @Override
  public String getContent() {
//    return String.format("枪%d\n1%s", gunId, state);
    return String.format(" 枪%d ", gunId);
  }

  @Override
  public String getState() {
    return state;
  }
}
