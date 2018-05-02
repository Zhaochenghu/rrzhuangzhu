package com.renren0351.presenter.collection;

import com.renren0351.presenter.LvIBasePresenter;
import com.renren0351.presenter.LvIBaseView;

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
