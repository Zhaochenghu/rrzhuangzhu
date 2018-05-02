package com.renren0351.rrzzapp.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.renren0351.rrzzapp.R;
import com.renren0351.rrzzapp.event.RefreshStationListEvent;
import com.renren0351.rrzzapp.event.RefreshUserCollectionEvent;
import com.renren0351.rrzzapp.views.activities.MainActivity;
import com.renren0351.rrzzapp.views.base.LvBaseFragment;
import com.renren0351.rrzzapp.wigets.FilterView;
import com.renren0351.model.LvRepository;
import com.renren0351.model.bean.StationFilterType;
import com.renren0351.presenter.order.OrderContract;
import com.renren0351.presenter.order.OrderPresenter;
import com.trello.rxlifecycle.FragmentEvent;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.leanvision.baseframe.rx.RxBus;
import cn.com.leanvision.baseframe.rx.SimpleSubscriber;
import cn.com.leanvision.baseframe.util.LvTextUtil;
import rx.functions.Action1;

/********************************
 * created by lvshicheng on 2017/2/13.
 ********************************/
public class OrderFragment extends LvBaseFragment implements OrderContract.View {

    @BindView(R.id.rl_search)
    RelativeLayout rlSearch;

    private OrderPresenter orderPresenter;

    private OrderMapFragment  orderMapFragment;
    private OrderListFragment orderListFragment;

    public static OrderFragment newInstance() {
        Bundle args = new Bundle();
        OrderFragment fragment = new OrderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View createView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frgm_order, container, false);
    }

    @Override
    protected void initPresenter() {
        orderPresenter = new OrderPresenter();
        orderPresenter.attachView(this);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        requestSubstationsAgain();
    }

    @Override
    protected void destroyPresenter() {
        orderPresenter.detachView();
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        if (savedInstanceState == null) {
            orderMapFragment = OrderMapFragment.newInstance();
            orderListFragment = OrderListFragment.newInstance();
            loadMultipleRootFragment(R.id.fragment, 0, orderMapFragment, orderListFragment);
        } else {
            orderMapFragment = findChildFragment(OrderMapFragment.class);
            orderListFragment = findChildFragment(OrderListFragment.class);
        }

        //暂时没有用到
        RxBus.getInstance()
            .toObservable(RefreshStationListEvent.class)
            .subscribe(new SimpleSubscriber<RefreshStationListEvent>() {
                @Override
                public void onNext(RefreshStationListEvent refreshStationListEvent) {
                    requestSubstationsAgain();
                }
            });

        //在LoginActivity的getSubstations()中发送事件
        //在SettingActivity的getSubstations()中发送事件
        //重新刷新用户收藏数据
        RxBus.getInstance()
                .toObservable(RefreshUserCollectionEvent.class)
                .compose(this.<RefreshUserCollectionEvent>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe(new Action1<RefreshUserCollectionEvent>() {
                    @Override
                    public void call(RefreshUserCollectionEvent refreshUserCollectionEvent) {
                        if (orderMapFragment != null) {
                            orderMapFragment.refreshUserStations();
                        }
                        if (orderListFragment != null) {
                            orderListFragment.refreshSubstations();
                        }
                    }
                });
    }

    public void requestSubstationsAgain() {
        if (System.currentTimeMillis() - lastRefreshTime > 5 * 60 * 1000) {
            orderPresenter.getSubstations(lastRefreshTime == 0);
        }
    }

    /**
     * ---------------
     * Click 事件
     * ---------------
     */
    /**
     * 搜索按钮点击事件
     * 跳转到搜索界面
     */
    @OnClick(R.id.rl_search)
    public void clickSearch() { // 跳转搜索页
        SearchFragment searchFragment = SearchFragment.newInstance(_mActivity);
        ((MainFragment) getParentFragment()).startWithSharedElement(searchFragment, rlSearch, getString(R.string.transition_search));
    }

    /**
     * 过滤按钮点击事件
     * 显示FilterView
     */
    @OnClick(R.id.ivb_filter)
    public void clickFilter() {
        FilterView filterView = new FilterView(_mActivity,
            LvRepository.getInstance().getFilterType());
        filterView.setListener(new FilterView.FilterTypeChangedListener() {
            @Override
            public void filterTypeChanged(StationFilterType type) {
                LvRepository.getInstance().setFilter(type);
                getSubstationsSucceed();
            }
        });
        filterView.show(_mActivity);
    }

    /**
     * 列表按钮点击事件
     * 用于MapFragment和ListFragment切换
     * @param v
     */
    @OnClick(R.id.ivb_list)
    public void clickTurnList(View v) { // 切换成列表模式
        Fragment parentFragment = getParentFragment();
        //隐藏导航
        if (parentFragment != null && parentFragment instanceof MainFragment) {
            ((MainFragment) parentFragment).beforeBack();
        }

        ImageButton ibtn = (ImageButton) v;
        if (orderListFragment.isHidden()) {
            ibtn.setImageResource(R.drawable.ic_order_location);
            showHideFragment(orderListFragment, orderMapFragment);
        } else {
            ibtn.setImageResource(R.drawable.ic_order_list);
            showHideFragment(orderMapFragment, orderListFragment);
        }
    }

    /**
     * 扫一扫按钮点击事件
     */
    @OnClick(R.id.iv_scan)
    public void clickScan() {
        ((MainActivity) _mActivity).turnToScan();
    }

    @Override
    public void showLoading() {
        showLoadingDialog();
    }

    @Override
    public void showNormal() {
        dismissLoadingDialog();
    }

    /**
     * --------------------
     * OrderContract.View impl
     * --------------------
     */
    @Override
    public void getSubstationsFailed(String msg) {
        dismissLoadingDialog();
        if (LvTextUtil.isEmpty(msg)) {
            showToast(R.string.network_not_available);
        } else {
            showToast(msg);
        }
    }

    private long lastRefreshTime;

    @Override
    public void getSubstationsSucceed() { // FIXME: 2017/5/22 暂时本地处理
        lastRefreshTime = System.currentTimeMillis();

        dismissLoadingDialog();
        orderMapFragment.needRefresh = true;
        if (orderMapFragment.isSupportVisible()) {
            orderMapFragment.refreshSubstations();
            orderMapFragment.needRefresh = false;
        }

        orderListFragment.needRefresh = true;
        if (orderListFragment.isSupportVisible()) {
            orderListFragment.refreshSubstations();
            orderListFragment.needRefresh = false;
        }
    }
}
