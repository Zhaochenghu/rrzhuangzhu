package com.bxchongdian.app.services;

import android.content.Intent;

import com.bxchongdian.app.LvApplication;

/********************************
 * Created by lvshicheng on 2017/4/19.
 ********************************/
public class ServiceUtil {

  public static void startChargingQuery() {
    Intent intent = new Intent(LvApplication.getContext(), CoreService.class);
    intent.setAction(CoreService.ACTION_START_CHARGING_QUERY);
    LvApplication.getContext().startService(intent);
  }

  public static void stopChargingQuery() {
    Intent intent = new Intent(LvApplication.getContext(), CoreService.class);
    intent.setAction(CoreService.ACTION_STOP_CHARGING_QUERY);
    LvApplication.getContext().startService(intent);
  }
}
