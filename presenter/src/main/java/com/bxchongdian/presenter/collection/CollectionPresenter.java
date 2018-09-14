package com.bxchongdian.presenter.collection;

import com.bxchongdian.model.response.SubstationsResponse;
import com.bxchongdian.presenter.usercase.FavorListCase;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import cn.com.leanvision.baseframe.rx.SimpleSubscriber;

/********************************
 * Created by lvshicheng on 2017/4/11.
 ********************************/
public class CollectionPresenter implements CollectionContract.Presenter<CollectionContract.View> {

  private WeakReference<CollectionContract.View> wrView;

  private FavorListCase favorListCase;

  @Override
  public void attachView(CollectionContract.View view) {
    wrView = new WeakReference<>(view);

    favorListCase = new FavorListCase();
  }

  @Override
  public void detachView() {
    wrView.clear();

    favorListCase.unSubscribe();
  }

  @Override
  public void getFavorList() {

    HashMap<String, Object> request = new HashMap<>();
    request.put("page", "1");
    request.put("prePage", 20);
    favorListCase.params(request)
        .viewFilter(wrView)
        .createObservable(new SimpleSubscriber<SubstationsResponse>() {

          @Override
          public void onStart() {
            CollectionContract.View view = wrView.get();
            if (view != null) {
              view.showLoading(null);
            }
          }

          @Override
          public void onError(Throwable e) {
            CollectionContract.View view = wrView.get();
            if (view != null) {
              view.requestFailed(null);
            }
          }

          @Override
          public void onNext(SubstationsResponse response) {
            CollectionContract.View view = wrView.get();
            view.showNormal();
            if (response.isSuccess()) {
              view.getFavorListSuccess();
            } else {
              view.requestFailed(response.msg);
            }
          }
        });
  }
}
