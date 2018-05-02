package cn.com.leanvision.baseframe.rx.transformers;

import java.util.concurrent.TimeUnit;

import rx.Observable;

/********************************
 * Created by lvshicheng on 2016/12/21.
 * <p>
 * 避免多次点击事件
 ********************************/
public class ForceDoubleClickCompat {

  // item 点击后的延迟，为了让ripple动画执行完
  public static final int RIPPLE_DELAY = 130;

  /**
   * 防止多次点击，同时延迟
   */
  public static final Observable.Transformer forceDoubleClickDelay = new Observable.Transformer() {
    @Override
    public Object call(Object observable) {
      return ((Observable) observable)
          .throttleFirst(1, TimeUnit.SECONDS)
          .delay(RIPPLE_DELAY, TimeUnit.MILLISECONDS)
          .compose(SchedulersCompat.observeOnMainThread());
    }
  };

  public static <T> Observable.Transformer<T, T> applyForceDoubleClickDelay() {
    return (Observable.Transformer<T, T>) forceDoubleClickDelay;
  }
}
