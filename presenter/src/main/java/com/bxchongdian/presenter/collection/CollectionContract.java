package com.bxchongdian.presenter.collection;

import com.bxchongdian.presenter.LvIBasePresenter;
import com.bxchongdian.presenter.LvIBaseView;

/********************************
 * Created by lvshicheng on 2017/4/11.
 ********************************/
public interface CollectionContract {

  interface View extends LvIBaseView {

    void getFavorListSuccess();
  }

  interface Presenter<R> extends LvIBasePresenter<R> {

    void getFavorList();
  }
}
