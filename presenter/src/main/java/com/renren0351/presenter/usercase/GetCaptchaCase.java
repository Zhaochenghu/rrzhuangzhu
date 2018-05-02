package com.renren0351.presenter.usercase;

import com.renren0351.model.dagger.ApiComponentHolder;
import com.renren0351.model.response.SimpleResponse;

import java.util.HashMap;

import cn.com.leanvision.baseframe.model.usercase.UserCase;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import rx.Observable;

/********************************
 * Created by lvshicheng on 2017/6/22.
 ********************************/
public class GetCaptchaCase extends UserCase<SimpleResponse, HashMap<String, Object>> {

    @Override
    public Observable<SimpleResponse> interactor(HashMap<String, Object> params) {
        return ApiComponentHolder.sApiComponent
            .apiService().getCaptcha(params)
            .onBackpressureBuffer()
            .take(1)
            .compose(SchedulersCompat.<SimpleResponse>applyNewSchedulers());
    }
}
