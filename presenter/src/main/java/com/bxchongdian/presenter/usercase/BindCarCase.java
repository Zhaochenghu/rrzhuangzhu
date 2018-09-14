package com.bxchongdian.presenter.usercase;

import com.bxchongdian.model.dagger.ApiComponentHolder;
import com.bxchongdian.model.response.SimpleResponse;

import java.util.HashMap;

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

public class BindCarCase extends UserCase<SimpleResponse,HashMap<String,Object>> {
    @Override
    public Observable<SimpleResponse> interactor(HashMap<String, Object> params) {
        return ApiComponentHolder.sApiComponent.apiService()
                .bindCar(params)
                .take(1)
                .compose(SchedulersCompat.<SimpleResponse>applyNewSchedulers());
    }
}
