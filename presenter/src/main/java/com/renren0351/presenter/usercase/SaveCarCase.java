package com.renren0351.presenter.usercase;

import com.renren0351.model.bean.CarBean;
import com.renren0351.model.dagger.ApiComponentHolder;
import com.renren0351.model.response.SimpleResponse;

import cn.com.leanvision.baseframe.model.usercase.UserCase;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import rx.Observable;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/06/20
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class SaveCarCase extends UserCase<SimpleResponse,CarBean> {
    @Override
    public Observable<SimpleResponse> interactor(CarBean params) {
        return ApiComponentHolder.sApiComponent.apiService()
                .saveCar(params)
                .take(1)
                .compose(SchedulersCompat.<SimpleResponse>applyNewSchedulers());
    }
}
