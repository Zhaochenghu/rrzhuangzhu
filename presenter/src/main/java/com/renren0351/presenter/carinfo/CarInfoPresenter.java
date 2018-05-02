package com.renren0351.presenter.carinfo;

import com.renren0351.model.bean.CarCardResponse;
import com.renren0351.model.dagger.ApiComponentHolder;
import com.renren0351.model.response.SimpleResponse;
import com.renren0351.presenter.usercase.BindCarCase;
import com.renren0351.presenter.usercase.UnbindCarCase;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import cn.com.leanvision.baseframe.rx.SimpleSubscriber;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/07/07
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class CarInfoPresenter implements CarInfoContract.Presenter<CarInfoContract.View> {
    private WeakReference<CarInfoContract.View> wrView;
    private CarInfoContract.View view;

    private BindCarCase bindCarCase;
    private UnbindCarCase unbindCarCase;

    private CarInfoContract.View getView(){
        if (view == null){
            view = wrView.get();
        }
        return view;
    }

    @Override
    public void attachView(CarInfoContract.View view) {
        wrView = new WeakReference<>(view);
    }

    @Override
    public void detachView() {
        wrView.clear();
        if (bindCarCase != null) {
            bindCarCase.unSubscribe();
        }

        if (unbindCarCase != null){
            unbindCarCase.unSubscribe();
        }
    }

    @Override
    public void queryBindCard(String carId) {
        ApiComponentHolder.sApiComponent
                .apiService()
                .queryBindCard(carId)
                .take(1)
                .compose(SchedulersCompat.<CarCardResponse>applyNewSchedulers())
                .subscribe(new SimpleSubscriber<CarCardResponse>() {
                    @Override
                    public void onError(Throwable e) {
                        if (getView() != null){
                            getView().showNormal();
                            getView().requestFailed(null);
                        }
                    }

                    @Override
                    public void onNext(CarCardResponse carCardResponse) {
                        getView().showNormal();
                        if (carCardResponse.isSuccess()){
                            getView().refreshCard(carCardResponse.list);
                        }else {
                            getView().requestFailed(carCardResponse.msg);
                        }
                    }

                    @Override
                    public void onStart() {
                        if (getView() != null){
                            getView().showLoading(null);
                        }
                    }
                });
    }

    @Override
    public void deleteCar(HashMap<String, Object> request) {
        ApiComponentHolder.sApiComponent
                .apiService()
                .deleteCar(request)
                .take(1)
                .compose(SchedulersCompat.<SimpleResponse>applyNewSchedulers())
                .subscribe(new SimpleSubscriber<SimpleResponse>() {
                    @Override
                    public void onError(Throwable e) {
                        if (getView() != null){
                            getView().showNormal();
                            getView().requestFailed(null);
                        }
                    }

                    @Override
                    public void onNext(SimpleResponse simpleResponse) {
                        getView().showNormal();
                        if (simpleResponse.isSuccess()){
                            getView().deleteSuccess();
                        }else {
                            getView().requestFailed(simpleResponse.msg);
                        }
                    }

                    @Override
                    public void onStart() {
                        if (getView() != null){
                            getView().showLoading(null);
                        }
                    }
                });
    }

    @Override
    public void bindCar(HashMap<String, Object> request) {
        if (bindCarCase == null) {
            bindCarCase = new BindCarCase();
        }
        bindCarCase.params(request)
                .viewFilter(wrView)
                .createObservable(new SimpleSubscriber<SimpleResponse>() {
                    @Override
                    public void onStart() {
                        if (getView() != null) {
                            getView().showLoading(null);
                        }
                    }

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
                            getView().bindCarSuccess();
                        }else {
                            getView().requestFailed(simpleResponse.msg);
                        }
                    }
                });
    }

    @Override
    public void unbindCar(int  bindId) {
        if (unbindCarCase == null){
            unbindCarCase = new UnbindCarCase();
        }

        unbindCarCase.params(bindId)
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
                            getView().unbindCarSuccess();
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
}
