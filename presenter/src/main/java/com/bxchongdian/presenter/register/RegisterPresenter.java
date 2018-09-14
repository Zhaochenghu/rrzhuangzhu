package com.bxchongdian.presenter.register;

import com.bxchongdian.model.request.RegisterRequest;
import com.bxchongdian.model.response.RegisterResponse;
import com.bxchongdian.model.response.SimpleResponse;
import com.bxchongdian.presenter.usercase.GetCaptchaCase;
import com.bxchongdian.presenter.usercase.RegisterCase;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import cn.com.leanvision.baseframe.rx.SimpleSubscriber;
import cn.com.leanvision.baseframe.util.LvPreconditions;

/**
 * Created by Administrator on 2017/3/1 0001.
 */

public class RegisterPresenter implements RegisterContract.presenter {

    private WeakReference<RegisterContract.View> wrView;

    private RegisterCase   registerCase;
    private GetCaptchaCase getCaptchaCase;

    @Override
    public void hpRegister(RegisterRequest request) {
        LvPreconditions.checkNotNull(registerCase, "Must call attachView method first!");
        registerCase.params(request)
            .viewFilter(wrView)
            .createObservable(new SimpleSubscriber<RegisterResponse>() {
                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    getView().requestFailed(null);
                }

                @Override
                public void onNext(RegisterResponse registerResponse) {
                    super.onNext(registerResponse);
                    if (registerResponse.isSuccess()) {
                        getView().registerSuccess();
                    } else {
                        getView().requestFailed(registerResponse.msg);
                    }
                }
            });
    }

    @Override
    public void hpCaptcha(String phoneNum) {
        HashMap<String, Object> map = new HashMap<>(2);
        map.put("phone", phoneNum);
        map.put("type", 0); // 这里严格字段类型
        getCaptchaCase.params(map)
            .viewFilter(wrView)
            .createObservable(new SimpleSubscriber<SimpleResponse>() {
                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    getView().getCaptchaFailed();
                }

                @Override
                public void onNext(SimpleResponse response) {
                    if (response.isSuccess()) {
                        getView().getCaptchaSuccess();
                    } else {
                        getView().getCaptchaFailed();
                    }
                }
            });
    }

    @Override
    public RegisterContract.View getView() {
        return wrView.get();
    }

    @Override
    public boolean isViewActive() {
        return wrView.get() != null;
    }

    @Override
    public void attachView(RegisterContract.View view) {
        wrView = new WeakReference<>(view);
        registerCase = new RegisterCase();
        getCaptchaCase = new GetCaptchaCase();
    }

    @Override
    public void detachView() {
        wrView.clear();
        registerCase.unSubscribe();
    }
}
