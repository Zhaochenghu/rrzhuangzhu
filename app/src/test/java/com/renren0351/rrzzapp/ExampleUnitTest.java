package com.renren0351.rrzzapp;

import org.junit.Test;

import rx.Subscriber;
import rx.functions.Func1;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
  @Test
  public void addition_isCorrect() throws Exception {

    rx.Observable.create(new rx.Observable.OnSubscribe<Object>() {
      @Override
      public void call(Subscriber<? super Object> subscriber) {
        String a = null;
        String b = "";
//        a.toString();
        subscriber.onNext("什么情况");
      }
    })
        .take(1)
        .onBackpressureBuffer()
//        .compose(SchedulersCompat.<Object>applyNewSchedulers())

        .filter(new Func1<Object, Boolean>() {
          @Override
          public Boolean call(Object o) {
            System.out.println("filter!!");
            return true;
          }
        }).subscribe(new Subscriber<Object>() {

      @Override
      public void onCompleted() {
        System.out.println("onCompleted");
      }

      @Override
      public void onError(Throwable e) {
//        e.printStackTrace();
        System.out.println("onError: " + e.getMessage());
      }

      @Override
      public void onNext(Object o) {
        System.out.println("onNext ： " + o);
      }
    });

//    Thread.sleep(7000);

    assertEquals(4, 2 + 2);
  }
}