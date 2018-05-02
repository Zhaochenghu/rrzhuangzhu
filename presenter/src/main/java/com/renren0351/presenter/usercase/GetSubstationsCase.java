package com.renren0351.presenter.usercase;

import com.renren0351.model.bean.SubstationBean;
import com.renren0351.model.dagger.ApiComponentHolder;
import com.renren0351.model.response.SubstationsResponse;

import java.util.Collections;
import java.util.Comparator;

import cn.com.leanvision.baseframe.model.usercase.UserCase;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import cn.com.leanvision.baseframe.util.LvTextUtil;
import rx.Observable;
import rx.functions.Action1;

/********************************
 * Created by lvshicheng on 2017/3/8.
 ********************************/
public class GetSubstationsCase extends UserCase<SubstationsResponse, Void> {

  @Override
  public Observable<SubstationsResponse> interactor(Void params) {

    return ApiComponentHolder.sApiComponent
        .apiService()
        .getSubstation()
        .doOnNext(new Action1<SubstationsResponse>() {
          @Override
          public void call(SubstationsResponse response) {
            if (response.isSuccess() && !LvTextUtil.isArrayEmpty(response.substations)) {
              // 按距离排个序
              Collections.sort(response.substations, new Comparator<SubstationBean>() {
                @Override
                public int compare(SubstationBean lhs, SubstationBean rhs) {

                  return 0;
                }
              });
            }
          }
        })
        .take(1)
        .compose(SchedulersCompat.<SubstationsResponse>applyNewSchedulers());
  }

}
