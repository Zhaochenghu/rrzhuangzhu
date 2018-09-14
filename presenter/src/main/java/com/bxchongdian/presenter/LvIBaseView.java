package com.bxchongdian.presenter;

/********************************
 * Created by lvshicheng on 2016/12/7.
 ********************************/
public  interface LvIBaseView {

  void showLoading(String msg);

  void showNormal();

  void requestFailed(String msg);
}
