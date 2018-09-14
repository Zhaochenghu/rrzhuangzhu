package com.bxchongdian.presenter.order;

import com.bxchongdian.model.LvRepository;
import com.bxchongdian.model.response.SubstationsResponse;
import com.bxchongdian.presenter.usercase.GetSubstationsCase;

import java.lang.ref.WeakReference;

import cn.com.leanvision.baseframe.rx.SimpleSubscriber;

/********************************
 * Created by lvshicheng on 2017/3/8.
 ********************************/
public class OrderPresenter implements OrderContract.Presenter {

    private WeakReference<OrderContract.View> wrView;

    private GetSubstationsCase substationsCase;

    @Override
    public void attachView(OrderContract.View view) {
        wrView = new WeakReference<>(view);

        substationsCase = new GetSubstationsCase();
    }

    @Override
    public void detachView() {
        wrView.clear();

        substationsCase.unSubscribe();
    }

    private boolean isLoading;

    @Override
    public void getSubstations(boolean isShowLoading) {
        if (isLoading) {
            return;
        }
        isLoading = true;

        if (isViewActive() && isShowLoading) {
            getView().showLoading();
        }

        substationsCase.createObservable(new SimpleSubscriber<SubstationsResponse>() {
            @Override
            public void onError(Throwable e) {
                isLoading = false;
                super.onError(e);
                e.printStackTrace();
                if (isViewActive()) {
                    getView().showNormal();
                    getView().getSubstationsFailed(null);
                }
            }

            @Override
            public void onNext(SubstationsResponse response) {
                isLoading = false;
                if (isViewActive()) {
                    getView().showNormal();
                    if (response.isSuccess()) {
                        LvRepository.getInstance().refreshSubstations(response.substations);
                        getView().getSubstationsSucceed();
                    } else {
                        getView().getSubstationsFailed(response.msg);
                    }
                }
            }
        });
    }

    @Override
    public boolean isViewActive() {
        return wrView != null && wrView.get() != null;
    }

    @Override
    public OrderContract.View getView() {
        return wrView.get();
    }
}
