package com.bxchongdian.presenter.profile;

import com.bxchongdian.model.response.ProfileResponse;
import com.bxchongdian.presenter.usercase.GetProfileCase;

import java.lang.ref.WeakReference;

import cn.com.leanvision.baseframe.rx.SimpleSubscriber;

/********************************
 * Created by lvshicheng on 2017/3/6.
 ********************************/
public class ProfilePresenter implements ProfileContract.Presenter {

  private WeakReference<ProfileContract.View> wrView;

  private GetProfileCase getProfileCase;

  @Override
  public void attachView(ProfileContract.View view) {
    wrView = new WeakReference<>(view);

    getProfileCase = new GetProfileCase();
  }

  @Override
  public void detachView() {
    wrView.clear();

    getProfileCase.unSubscribe();
  }

  @Override
  public void getProfile() {
    getProfileCase.viewFilter(wrView)
        .createObservable(new SimpleSubscriber<ProfileResponse>() {
          @Override
          public void onError(Throwable e) { // ERROR 是不经过Filter
            super.onError(e);
            ProfileContract.View view = wrView.get();
            if (view != null) {
                view.showNormal();
              view.getProfileFailed(null);
            }
          }

          @Override
          public void onNext(ProfileResponse profileResponse) {
            super.onNext(profileResponse);
            ProfileContract.View view = wrView.get();
              view.showNormal();
              if (profileResponse.isSuccess()){
                  view.getProfileSuccess(profileResponse.profile);
              }else {
                  view.getProfileFailed(profileResponse.msg);
              }
          }

            @Override
            public void onStart() {
                super.onStart();
                ProfileContract.View view = wrView.get();
                if (view != null) {
                    view.showLoading(null);
                }
            }
        });
  }
}
