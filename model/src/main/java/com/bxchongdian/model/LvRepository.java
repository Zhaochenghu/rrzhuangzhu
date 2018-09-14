package com.bxchongdian.model;

import android.support.annotation.NonNull;

import com.bxchongdian.model.bean.StationFilterType;
import com.bxchongdian.model.bean.SubstationBean;

import java.util.Collections;
import java.util.List;

import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import rx.Observable;
import rx.functions.Func1;

/********************************
 * Created by lvshicheng on 2017/3/8.
 ********************************/
public class LvRepository {

  private static final LvRepository instance = new LvRepository();

  private volatile boolean isRequestSuccess;
  private volatile boolean isSortedByDistance;

  private List<SubstationBean> substations = Collections.emptyList();

  private StationFilterType filterType = StationFilterType.FILTER_ALL;

  public static LvRepository getInstance() {
    return instance;
  }

  private LvRepository() {
  }

  public boolean isRequestSuccess() {
    return isRequestSuccess;
  }

  public Observable<SubstationBean> getSubstations() {
    return
        Observable.from(substations)
            .filter(new Func1<SubstationBean, Boolean>() {
              @Override
              public Boolean call(SubstationBean substationBeen) {
                return doFilter(substationBeen);
              }
            });
  }

  @NonNull
  public Boolean doFilter(SubstationBean substationBeen) {
    if (filterType == StationFilterType.FILTER_AC) {
      return substationBeen.hasTotalAC > 0;
    } else if (filterType == StationFilterType.FILTER_DC) {
      return substationBeen.hasTotalDC > 0;
    } else if (filterType == StationFilterType.FILTER_IDLE) {
      return substationBeen.hasRest > 0;
    }
    return true;
  }

  public boolean isSortedByDistance() {
    return isSortedByDistance;
  }

  public void sortByDistanceEnd() {
    isSortedByDistance = true;
  }

  public void refreshSubstations(List<SubstationBean> substations) {
    isRequestSuccess = true;
    isSortedByDistance = false;
    this.substations = substations;
  }

  public Observable<List<SubstationBean>> filterWithName(final String searchName) {
    return Observable.from(substations)
        .filter(new Func1<SubstationBean, Boolean>() {
          @Override
          public Boolean call(SubstationBean substationBean) {
            return substationBean.areaName.contains(searchName);
          }
        })
        .toList()
        .compose(SchedulersCompat.<List<SubstationBean>>applyNewSchedulers());
  }

  public Observable<SubstationBean> filterFavorById(final String id){
    return Observable.from(substations)
            .filter(new Func1<SubstationBean, Boolean>() {
              @Override
              public Boolean call(SubstationBean substationBean) {
                if (substationBean.areaId.equals(id)){
                  return true;
                }else {
                  return false;
                }

              }
            }).compose(SchedulersCompat.<SubstationBean>applyNewSchedulers());
  }

  /**
   * 过滤所有已经收藏的充电站
   */
  public Observable<List<SubstationBean>> filterFavorSatation() {
    return Observable.from(substations)
        .filter(new Func1<SubstationBean, Boolean>() {
          @Override
          public Boolean call(SubstationBean substationBean) {
            return substationBean.isFavorites;
          }
        })
        .toList()
        .compose(SchedulersCompat.<List<SubstationBean>>applyNewSchedulers());
  }

  public void setFilter(StationFilterType filter) {
    this.filterType = filter;
  }

  public StationFilterType getFilterType() {
    return filterType;
  }
}
