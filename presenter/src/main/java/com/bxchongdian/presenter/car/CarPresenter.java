package com.bxchongdian.presenter.car;

import com.bxchongdian.model.bean.CarBean;
import com.bxchongdian.model.dagger.ApiComponentHolder;
import com.bxchongdian.model.response.CarResponse;
import com.bxchongdian.model.response.SimpleResponse;
import com.bxchongdian.presenter.usercase.SaveCarCase;

import java.lang.ref.WeakReference;

import cn.com.leanvision.baseframe.rx.SimpleSubscriber;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/06/20
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class CarPresenter implements CarContract.Presenter<CarContract.View> {
    private WeakReference<CarContract.View> wrView;
    private SaveCarCase saveCarCase;
    private CarContract.View view;
    @Override
    public void attachView(CarContract.View view) {
        wrView = new WeakReference<>(view);
    }

    @Override
    public void detachView() {
        wrView.clear();
        if (saveCarCase != null) {
            saveCarCase.unSubscribe();
        }
    }

    @Override
    public void saveCar(CarBean bean) {
        if (saveCarCase == null) {
            saveCarCase = new SaveCarCase();
        }

        saveCarCase.params(bean)
                .viewFilter(wrView)
                .createObservable(new SimpleSubscriber<SimpleResponse>() {
                    @Override
                    public void onError(Throwable e) {
                        if (getView() != null) {
                            getView().showNormal();
                            getView().requestFailed(null);
                        }
                    }

                    @Override
                    public void onNext(SimpleResponse simpleResponse) {
                        getView().showNormal();
                        if (simpleResponse.isSuccess()){
                            getView().saveCarSuccess();
                        }else {
                            getView().requestFailed(simpleResponse.msg);
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

    @Override
    public void queryCars() {
        ApiComponentHolder.sApiComponent
                .apiService()
                .queryCars()
                .take(1)
                .compose(SchedulersCompat.<CarResponse>applyNewSchedulers())
                .subscribe(new SimpleSubscriber<CarResponse>() {
                    @Override
                    public void onError(Throwable e) {
                        if (getView() != null) {
                            getView().showNormal();
                            getView().requestFailed(null);
                        }
                    }

                    @Override
                    public void onNext(CarResponse carResponse) {
                        getView().showNormal();
                        if (carResponse.isSuccess()){
                            getView().refreshCars(carResponse.cars);
                        }else {
                            getView().requestFailed(carResponse.msg);
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

    private CarContract.View getView(){
        if (view == null) {
            view = wrView.get();
        }
        return view;
    }
}
