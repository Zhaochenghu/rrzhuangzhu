package com.bxchongdian.presenter.fee;

import com.bxchongdian.model.dagger.ApiComponentHolder;
import com.bxchongdian.model.response.FeeResponse;

import java.lang.ref.WeakReference;

import cn.com.leanvision.baseframe.rx.SimpleSubscriber;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/07/11
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class FeePresenter implements FeeContract.Presenter<FeeContract.View> {
    private WeakReference<FeeContract.View> wrView;
    private FeeContract.View view;

    @Override
    public void attachView(FeeContract.View view) {
        wrView = new WeakReference<>(view);
    }

    @Override
    public void detachView() {
        wrView.clear();
    }

    @Override
    public void queryFees(String substationId) {
        ApiComponentHolder.sApiComponent
                .apiService()
                .queryFees(substationId)
                .take(1)
                .compose(SchedulersCompat.<FeeResponse>applyNewSchedulers())
                .subscribe(new SimpleSubscriber<FeeResponse>() {
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (getView() != null) {
                            getView().showNormal();
                        }
                    }

                    @Override
                    public void onNext(FeeResponse feeResponse) {
                        getView().showNormal();
                        if (feeResponse.isSuccess()){
                            getView().getFees(feeResponse.list);
                        }else {
                            getView().requestFailed(feeResponse.msg);
                        }
                    }

                    @Override
                    public void onStart() {
                        if (getView() != null) {
                            getView().showLoading(null);
                        }
                    }
                });
    }

    private FeeContract.View getView(){
        if (view == null) {
            view = wrView.get();
        }
        return view;
    }
}
