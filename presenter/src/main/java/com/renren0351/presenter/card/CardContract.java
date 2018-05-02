package com.renren0351.presenter.card;

import com.renren0351.model.request.CardRequest;
import com.renren0351.presenter.LvIBasePresenter;
import com.renren0351.presenter.LvIBaseView;

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
