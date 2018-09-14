package com.bxchongdian.presenter;

/********************************
 * Created by lvshicheng on 2016/12/15.
 * <p>
 * 有加载内容的页面可以实现该接口
 ********************************/
public interface LvContentView {

  void showError();

  void showEmpty();

  void showLoading();

  void showNormal();
}
