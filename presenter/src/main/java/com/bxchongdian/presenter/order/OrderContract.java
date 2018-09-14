package com.bxchongdian.presenter.order;

import com.bxchongdian.presenter.LvIBasePresenter;

/********************************
 * Created by lvshicheng on 2017/3/8.
 ********************************/
public interface OrderContract {

  interface View {

    void showLoading();

    void showNormal();

    void getSubstationsFailed(String msg);

    void getSubstationsSucceed();

  }

  interface Presenter extends LvIBasePresenter<View> {

    void getSubstations(boolean isShowLoading);

    boolean isViewActive();

    View getView();
  }
}
