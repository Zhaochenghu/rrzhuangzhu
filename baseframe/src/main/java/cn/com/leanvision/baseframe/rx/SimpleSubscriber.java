package cn.com.leanvision.baseframe.rx;

import rx.Subscriber;

/********************************
 * Created by lvshicheng on 2016/12/2.
 ********************************/
public abstract class SimpleSubscriber<T> extends Subscriber<T> {

  @Override
  public void onCompleted() {
  }

  @Override
  public void onError(Throwable e) {
    e.printStackTrace();
  }

  @Override
  public void onNext(T t) {
  }
}
