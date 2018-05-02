package com.renren0351.presenter.appointment;

import com.renren0351.model.dagger.ApiComponentHolder;
import com.renren0351.model.response.OrderResponse;

import java.lang.ref.WeakReference;

import cn.com.leanvision.baseframe.rx.SimpleSubscriber;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/07/24
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class QueryOrderPresenter implements QueryOrderContract.Presenter<QueryOrderContract.View> {
  private WeakReference<QueryOrderContract.View> wrView;
  private QueryOrderContract.View view;
  @Override
  public void attachView(QueryOrderContract.View view) {
    wrView = new WeakReference<>(view);
  }

  @Override
  public void detachView() {
    wrView.clear();
  }

  @Override
  public void queryOrder() {
    ApiComponentHolder.sApiComponent
      .apiService()
      .queryOrder()
      .take(1)
      .compose(SchedulersCompat.<OrderResponse>applyNewSchedulers())
      .subscribe(new SimpleSubscriber<OrderResponse>() {
        @Override
        public void onError(Throwable e) {
          e.printStackTrace();
          if (getView() != null) {
            getView().requestFailed(null);
          }
        }

        @Override
        public void onNext(OrderResponse orderResponse) {
          if (orderResponse.isSuccess()){
            if (orderResponse.list != null && orderResponse.list.size() > 0){
              getView().querySuccess(orderResponse.list.get(0));
            }else {
              getView().noOrder(orderResponse.msg);
            }

          }else {
            getView().noOrder(orderResponse.msg);
          }
        }
      });
  }

  private QueryOrderContract.View getView(){
    if (view == null) {
      view = wrView.get();
    }
    return view;
  }
}
