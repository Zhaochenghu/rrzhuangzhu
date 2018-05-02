package cn.com.leanvision.baseframe.model.usercase;

import java.lang.ref.WeakReference;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;

/********************************
 * Created by lvshicheng on 2016/12/1.
 ********************************/
public abstract class UserCase<R, T> {

  private Subscription subscription = Subscriptions.empty();

  private T             params;
  private WeakReference view;

  public UserCase<R, T> params(T params) {
    this.params = params;
    return this;
  }

  /**
   * 为了减少Presenter中繁琐的检查view是否为空
   */
  public UserCase<R, T> viewFilter(final WeakReference view) {
    this.view = view;
    return this;
  }

  public void createObservable(final Observer<R> subscriber) {
    subscription = this.interactor(params)
        .filter(new Func1<R, Boolean>() {
          @Override
          public Boolean call(R r) {
            if (view != null) {
              return view.get() != null && !subscription.isUnsubscribed();
            } else {
              return !subscription.isUnsubscribed();
            }
          }
        })
        .onBackpressureBuffer()
        .subscribe(subscriber);
  }

  @Deprecated
  public Observable<R> createObservable(T params) {
    return this.interactor(params)
        .filter(new Func1<R, Boolean>() {
          @Override
          public Boolean call(R r) {
            return !subscription.isUnsubscribed();
          }
        })
        .onBackpressureBuffer();
  }

  public void unSubscribe() {
    if (subscription != null && !subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
    if (view != null) {
      view.clear();
    }
  }

  public abstract Observable<R> interactor(T params);

  // FOR UNIT TEST
  public T getParams() {
    return params;
  }

  public void setParams(T params) {
    this.params = params;
  }

  public void setView(WeakReference view) {
    this.view = view;
  }
}
