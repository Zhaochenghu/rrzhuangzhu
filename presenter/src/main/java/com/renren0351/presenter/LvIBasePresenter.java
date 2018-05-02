package com.renren0351.presenter;

/********************************
 * Created by lvshicheng on 2016/12/1.
 ********************************/
public interface LvIBasePresenter<R> {

  void attachView(R view);

  void detachView();
}
