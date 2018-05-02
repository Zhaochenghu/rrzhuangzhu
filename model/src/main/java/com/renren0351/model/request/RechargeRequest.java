package com.renren0351.model.request;

import java.util.Locale;

/********************************
 * Created by lvshicheng on 2017/4/26.
 ********************************/
public class RechargeRequest extends SimpleRequest {

  public RechargeRequest(double money) {
    this.money = String.format(Locale.getDefault(), "%.0f", money * 100);
  }

  public String money;
}
