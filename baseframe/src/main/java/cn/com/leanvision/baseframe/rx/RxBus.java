package cn.com.leanvision.baseframe.rx;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/********************************
 * Created by lvshicheng on 2016/12/1.
 * <p>
 * 事件总线
 ********************************/
public class RxBus {

  private static RxBus instance;

  private Subject<Object, Object> rxStandardBus;
  /**
   * 粘性事件
   */
  private Subject<Object, Object> rxStickBus;

  public static RxBus getInstance() {
    if (instance == null)
      instance = RxBusMaker.mRxBus;
    return instance;
  }

  private RxBus() {
    rxStandardBus = new SerializedSubject<>(PublishSubject.create());
    rxStickBus = new SerializedSubject<>(BehaviorSubject.create());
  }

  public void postEvent(Object event) {
    if (!hasObservers())
      return;
    rxStandardBus.onNext(event);
  }

  public boolean hasObservers() {
    return rxStandardBus.hasObservers();
  }

  public <T> Observable<T> toObservable(Class<T> clazz) {
    return rxStandardBus.asObservable().onBackpressureBuffer().ofType(clazz);
  }

  public void postStickEvent(Object event) {
    rxStickBus.onNext(event);
  }

  public <T> Observable<T> toStickObservable(Class<T> clazz) {
    return rxStickBus.asObservable().share().onBackpressureBuffer().ofType(clazz);
//    return rxStickBus.asObservable().onBackpressureBuffer().ofType(clazz);
  }

  private static class RxBusMaker {
    static final RxBus mRxBus = new RxBus();
  }
}
