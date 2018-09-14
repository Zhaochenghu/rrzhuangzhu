package com.bxchongdian.presenter.card;

import com.bxchongdian.model.request.CardRequest;
import com.bxchongdian.model.response.CardResponse;
import com.bxchongdian.model.response.SimpleResponse;
import com.bxchongdian.presenter.usercase.BindCardCase;
import com.bxchongdian.presenter.usercase.UnbindCardCase;

import java.lang.ref.WeakReference;

import cn.com.leanvision.baseframe.rx.SimpleSubscriber;

/********************************
 * Created by lvshicheng on 2017/4/11.
 ********************************/
public class CardPresenter implements CardContract.Presenter<CardContract.View> {

  private WeakReference<CardContract.View> wrView;

  private BindCardCase   bindCardCase;
  private UnbindCardCase unbindCardCase;

  @Override
  public void attachView(CardContract.View view) {
    wrView = new WeakReference<>(view);
  }

  @Override
  public void detachView() {
    wrView.clear();

    if (bindCardCase != null) {
      bindCardCase.unSubscribe();
    }
    if (unbindCardCase != null) {
      unbindCardCase.unSubscribe();
    }
  }

  @Override
  public void bindCard(CardRequest request) {
    if (bindCardCase == null) {
      bindCardCase = new BindCardCase();
    }
    bindCardCase.params(request)
        .viewFilter(wrView)
        .createObservable(new SimpleSubscriber<CardResponse>() {

          @Override
          public void onStart() {
            CardContract.View view = wrView.get();
            if (view != null) {
              view.showLoading(null);
            }
          }

          @Override
          public void onError(Throwable e) {
            super.onError(e);
            CardContract.View view = wrView.get();
            if (view != null) {
              view.showNormal();
              view.requestFailed(null);
            }
          }

          @Override
          public void onNext(CardResponse response) {
            CardContract.View view = wrView.get();
            view.showNormal();
            if (response.isSuccess()) {
              view.bindCardSuccess();
            } else {
              view.requestFailed(response.msg);
            }
          }
        });
  }

  @Override
  public void unbindCard(CardRequest request) {
    if (unbindCardCase == null) {
      unbindCardCase = new UnbindCardCase();
    }

    unbindCardCase.params(request)
        .viewFilter(wrView)
        .createObservable(new SimpleSubscriber<SimpleResponse>() {

          @Override
          public void onStart() {
            CardContract.View view = wrView.get();
            if (view != null) {
              view.showLoading(null);
            }
          }

          @Override
          public void onError(Throwable e) {
            super.onError(e);
            CardContract.View view = wrView.get();
            if (view != null) {
              view.showNormal();
              view.requestFailed(null);
            }
          }

          @Override
          public void onNext(SimpleResponse response) {
            CardContract.View view = wrView.get();
            view.showNormal();
            if (response.isSuccess()) {
              view.unbindCardSuccess();
            } else {
              view.requestFailed(response.msg);
            }
          }
        });
  }
}
