package com.bxchongdian.presenter.card;

import com.bxchongdian.model.request.CardRequest;
import com.bxchongdian.presenter.LvIBasePresenter;
import com.bxchongdian.presenter.LvIBaseView;

/********************************
 * Created by lvshicheng on 2017/4/11.
 ********************************/
public interface CardContract {

  interface View extends LvIBaseView {

    void bindCardSuccess();

    void unbindCardSuccess();
  }

  interface Presenter<R> extends LvIBasePresenter<R> {

    void bindCard(CardRequest request);

    void unbindCard(CardRequest request);
  }
}
