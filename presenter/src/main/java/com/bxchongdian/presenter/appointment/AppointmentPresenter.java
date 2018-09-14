package com.bxchongdian.presenter.appointment;

import com.bxchongdian.model.dagger.ApiComponentHolder;
import com.bxchongdian.model.response.AppointmentResponse;
import com.bxchongdian.model.response.SimpleResponse;

import java.lang.ref.WeakReference;
import java.util.Map;

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

public class AppointmentPresenter implements AppointmentContract.Presenter<AppointmentContract.View> {
    private WeakReference<AppointmentContract.View> wrView;
    private AppointmentContract.View view;

    @Override
    public void attachView(AppointmentContract.View view) {
        wrView = new WeakReference<>(view);
    }

    @Override
    public void detachView() {
        wrView.clear();
    }

    @Override
    public void order(Map<String, Object> request) {
        ApiComponentHolder.sApiComponent
                .apiService()
                .chargingOrder(request)
                .take(1)
                .compose(SchedulersCompat.<AppointmentResponse>applyNewSchedulers())
                .subscribe(new SimpleSubscriber<AppointmentResponse>() {
                    @Override
                    public void onError(Throwable e) {
                        if (getView() != null) {
                            getView().showNormal();
                            getView().requestFailed(null);
                        }
                    }

                    @Override
                    public void onNext(AppointmentResponse appointmentResponse) {
                        getView().showNormal();
                        if (appointmentResponse.isSuccess()){
                            getView().orderSuccess(appointmentResponse.appointment.orderId);
                        }else {
                            getView().requestFailed(appointmentResponse.msg);
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
    public void cancelOrder(String orderId) {
        ApiComponentHolder.sApiComponent
                .apiService()
                .cancelOrder(orderId)
                .take(1)
                .compose(SchedulersCompat.<SimpleResponse>applyNewSchedulers())
                .subscribe(new SimpleSubscriber<SimpleResponse>() {
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
                            getView().cancelOrderSuccess();
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

    private AppointmentContract.View getView(){
        if (view == null) {
            view = wrView.get();
        }
        return view;
    }
}
