package com.renren0351.presenter.usercase;

import com.renren0351.model.dagger.ApiComponentHolder;
import com.renren0351.model.request.ForgotPwdRequest;
import com.renren0351.model.response.SimpleResponse;

import cn.com.leanvision.baseframe.model.usercase.UserCase;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import rx.Observable;

/********************************
 * Created by lvshicheng on 2017/7/4.
 ********************************/
public class ForgotPwdCase extends UserCase<SimpleResponse, ForgotPwdRequest> {

    @Override
    public Observable<SimpleResponse> interactor(ForgotPwdRequest params) {
        return ApiComponentHolder.sApiComponent
            .apiService()
            .forgotPwd(params)
            .take(1)
            .compose(SchedulersCompat.<SimpleResponse>applyNewSchedulers());
    }
}
