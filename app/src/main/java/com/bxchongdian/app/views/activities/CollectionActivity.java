package com.bxchongdian.app.views.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;
import com.bxchongdian.app.LvAppUtils;
import com.bxchongdian.app.R;
import com.bxchongdian.app.event.ShowNavListEvent;
import com.bxchongdian.app.views.base.LvBaseAppCompatActivity;
import com.bxchongdian.app.views.fragments.OrderListFragment;
import com.bxchongdian.app.views.fragments.OrderMapFragment;
import com.bxchongdian.app.wigets.MapNavListView;
import com.bxchongdian.model.LvRepository;
import com.bxchongdian.model.bean.SubstationBean;
import com.bxchongdian.model.response.SimpleResponse;
import com.bxchongdian.model.response.SubstationsResponse;
import com.bxchongdian.presenter.collection.CollectionContract;
import com.bxchongdian.presenter.collection.CollectionPresenter;
import com.bxchongdian.presenter.usercase.FavorListCase;
import com.bxchongdian.presenter.usercase.UnFavorCase;
import com.trello.rxlifecycle.ActivityEvent;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import cn.com.leanvision.baseframe.rx.RxBus;
import cn.com.leanvision.baseframe.rx.SimpleSubscriber;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import cn.com.leanvision.baseframe.util.LvTextUtil;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Administrator on 2017/2/21 0021.
 */
@Route(path = "/login/mime/collection")
public class CollectionActivity extends LvBaseAppCompatActivity implements CollectionContract.View {
  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;

  @BindView(R.id.tv_no)
  TextView tvNo;

  private CollectionPresenter presenter;

  public static void navigation() {
    ARouter.getInstance().build("/login/mime/collection").navigation();
  }

  @Override
  protected void setContentView(Bundle savedInstanceState) {
    setContentView(R.layout.aty_collection);
  }

  @Override
  protected void initPresenter() {
    presenter = new CollectionPresenter();
    presenter.attachView(this);

//    presenter.getFavorList();
  }

  @Override
  protected void destroyPresenter() {
    presenter.detachView();
  }

  @Override
  protected void initView() {
    initToolbarNav("我的收藏");

    /* 显示导航选择列表 */
    RxBus.getInstance()
        .toObservable(ShowNavListEvent.class)
        .compose(this.<ShowNavListEvent>bindUntilEvent(ActivityEvent.DESTROY))
        .compose(SchedulersCompat.<ShowNavListEvent>observeOnMainThread())
        .subscribe(new Action1<ShowNavListEvent>() {
          @Override
          public void call(ShowNavListEvent event) {
            showMapNavListView(event.lat, event.lng);
          }
        });

    initRecyclerView();
    // 查询收藏列表
    queryFavors();
  }

  private MapNavListView mapNavListView;

  private void showMapNavListView(double lat, double lng) {
    if (mapNavListView == null) {
      mapNavListView = new MapNavListView(this);
    }
    mapNavListView.setLatlng(lat, lng);
    mapNavListView.show(this);
  }

  @Override
  public void showLoading(String msg) {
    showLoadingDialog();
  }

  @Override
  public void showNormal() {
    dismissLoadingDialog();
  }

  @Override
  public void requestFailed(String msg) {

  }

  @Override
  public void getFavorListSuccess() {

  }

  private void queryFavors() {
    showLoadingDialog();

    FavorListCase favorListCase = new FavorListCase();
    HashMap<String, Object> params = new HashMap<>();
    params.put("page", 1);
    params.put("prePage", 20);
    favorListCase
        .params(params)
        .createObservable(new SimpleSubscriber<SubstationsResponse>() {
          @Override
          public void onNext(SubstationsResponse response) {
//            refreshSubstations(response.substations);
            refresh(response.substations);
          }
        });
  }

  private void refresh(final List<SubstationBean> list){
    if (!LvTextUtil.isArrayEmpty(list)) {
      tvNo.setVisibility(View.GONE);
      recyclerView.setVisibility(View.VISIBLE);
    } else {
      dismissLoadingDialog();
      tvNo.setVisibility(View.VISIBLE);
      recyclerView.setVisibility(View.GONE);
      return;
    }
    LvRepository.getInstance().filterFavorSatation()
            .map(new Func1<List<SubstationBean>, List<SubstationBean>>() {
              @Override
              public List<SubstationBean> call(List<SubstationBean> substations) {
                if (substations.size() == 0){
                  substations = list;
                }
                if (!LvTextUtil.isArrayEmpty(substations)) {
                  if (!LvRepository.getInstance().isSortedByDistance()) {
                    if (OrderMapFragment.aMapLocation != null) {
                      final LatLng location = new LatLng(
                              OrderMapFragment.aMapLocation.getLatitude(),
                              OrderMapFragment.aMapLocation.getLongitude());

                      // 排序
                      Collections.sort(substations, new Comparator<SubstationBean>() {
                        @Override
                        public int compare(SubstationBean lhs, SubstationBean rhs) {
                          LatLng ldest = new LatLng(lhs.getLat(), lhs.getLng());
                          float ldist = AMapUtils.calculateLineDistance(ldest, location);
                          lhs.setDistance(ldist);
                          LatLng rdest = new LatLng(rhs.getLat(), rhs.getLng());
                          float rdist = AMapUtils.calculateLineDistance(rdest, location);
                          rhs.setDistance(rdist);
                          return (int) (ldist - rdist);
                        }
                      });
                      LvRepository.getInstance().sortByDistanceEnd();
                    }
                  }
                }
                return substations;
              }
            })
            .compose(SchedulersCompat.<List<SubstationBean>>applyNewSchedulers())
            .subscribe(new SimpleSubscriber<List<SubstationBean>>() {

              @Override
              public void onNext(List<SubstationBean> substations) {
                dismissLoadingDialog();
                OrderListFragment.OrderListAdapter adapter = (OrderListFragment.OrderListAdapter) recyclerView.getAdapter();
                adapter.setSubstations(substations);
                adapter.notifyDataSetChanged();
              }

              @Override
              public void onError(Throwable e) {
                super.onError(e);
                dismissLoadingDialog();
              }
            });
  }

  private void initRecyclerView() {
    View.OnClickListener clickListener = new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        unFavor((SubstationBean) v.getTag());
      }
    };
    LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext());
    recyclerView.setLayoutManager(lm);
    recyclerView.setHasFixedSize(true);
    OrderListFragment.OrderListAdapter adapter = new OrderListFragment.OrderListAdapter(clickListener);
    recyclerView.setAdapter(adapter);

    DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      decoration.setDrawable(getResources().getDrawable(R.drawable.item_divider, getTheme()));
    } else {
      decoration.setDrawable(getResources().getDrawable(R.drawable.item_divider));
    }
    recyclerView.addItemDecoration(decoration);
  }

  /**
   * ------------------------
   * 收藏和取消收藏
   * ------------------------
   */
  private UnFavorCase unFavorCase;

  public void unFavor(SubstationBean bean) {
    if (!LvAppUtils.isLogin()) {
      LoginActivity.navigation(false);
    } else {
      clearFavor(bean);
    }
  }

  private void clearFavor(final SubstationBean bean) {
    if (unFavorCase == null) {
      unFavorCase = new UnFavorCase();
    }
    showLoadingDialog();
    unFavorCase.params(bean.areaId)
        .createObservable(new SimpleSubscriber<SimpleResponse>() {
          @Override
          public void onError(Throwable e) {
            super.onError(e);
            dismissLoadingDialog();
            showToast(getString(R.string.network_not_available));
          }

          @Override
          public void onNext(SimpleResponse response) {
            if (response.isSuccess()) {
              bean.isFavorites = false;
              OrderListFragment.OrderListAdapter adapter = (OrderListFragment.OrderListAdapter) recyclerView.getAdapter();
              if (adapter != null) {
                adapter.removeItem(bean);
                //没有收藏
                if (adapter.getItemCount() < 1) {
                  recyclerView.setVisibility(View.GONE);
                  tvNo.setVisibility(View.VISIBLE);
                }
              }
            } else {
              showToast(response.msg);
            }
          }

          @Override
          public void onCompleted() {
            dismissLoadingDialog();
          }
        });
  }
}
