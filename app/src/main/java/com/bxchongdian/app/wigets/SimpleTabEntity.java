package com.bxchongdian.app.wigets;

import com.flyco.tablayout.listener.CustomTabEntity;

/********************************
 * Created by lvshicheng on 2017/2/20.
 ********************************/
public class SimpleTabEntity implements CustomTabEntity {

  private String title;
  private int    selectedIcon;
  private int    unselectedIcon;

  public SimpleTabEntity(String title, int selectedIcon, int unselectedIcon) {
    this.selectedIcon = selectedIcon;
    this.title = title;
    this.unselectedIcon = unselectedIcon;
  }

  @Override
  public String getTabTitle() {
    return title;
  }

  @Override
  public int getTabSelectedIcon() {
    return selectedIcon;
  }

  @Override
  public int getTabUnselectedIcon() {
    return unselectedIcon;
  }
}
