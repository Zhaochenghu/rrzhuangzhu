package com.renren0351.rrzzapp.views.fragments;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.renren0351.rrzzapp.LvAppUtils;
import com.renren0351.rrzzapp.R;
import com.renren0351.rrzzapp.views.activities.LoginActivity;
import com.renren0351.rrzzapp.views.base.LvBaseFragment;
import com.renren0351.rrzzapp.wigets.flexbox.TagFlowLayout;
import com.renren0351.rrzzapp.wigets.flexbox.adapters.SearchTagAdapter;
import com.renren0351.rrzzapp.wigets.flexbox.adapters.SearchTagView;
import com.renren0351.rrzzapp.wigets.flexbox.adapters.TagAdapter;
import com.renren0351.rrzzapp.wigets.flexbox.interfaces.OnFlexboxSubscribeListener;
import com.renren0351.model.LvRepository;
import com.renren0351.model.bean.ItemStationBean;
import com.renren0351.model.bean.SubstationBean;
import com.renren0351.model.response.SimpleResponse;
import com.renren0351.presenter.usercase.FavorCase;
import com.renren0351.presenter.usercase.UnFavorCase;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.leanvision.baseframe.log.DebugLog;
import cn.com.leanvision.baseframe.rx.SimpleSubscriber;
import cn.com.leanvision.baseframe.util.LvCommonUtil;
import cn.com.leanvision.baseframe.util.LvTextUtil;

/********************************
 * Created by lvshicheng on 2017/2/22.
 * <p>
 * 搜索页
 ********************************/
public class SearchFragment extends LvBaseFragment {

  @BindView(R.id.rl_search)
  RelativeLayout rlSearch;
  @BindView(R.id.iv_1)
  ImageView      iv1;
  @BindView(R.id.et_search)
  EditText       etSearch;
  @BindView(R.id.ivb_del)
  ImageButton    ivbDel;
  @BindView(R.id.btn_cancel)
  Button         btnCancel;
  @BindView(R.id.rv_favor)
  RecyclerView   rvFavor;

  @BindView(R.id.tv_favor)
  View tvFavor;
  @BindView(R.id.btn_login)
  View btnLogin;

  @BindView(R.id.search_history)
  RelativeLayout searchHistory;
  @BindView(R.id.btn_clear)
  Button         btnClear;
  @BindView(R.id.flow_layout)
  TagFlowLayout  flowLayout;
  @BindView(R.id.rv_search_result)
  RecyclerView   recyclerView;

  public static SearchFragment newInstance(Activity context) {

    Bundle args = new Bundle();

    SearchFragment fragment = new SearchFragment();
    fragment.setArguments(args);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Transition transition = TransitionInflater.from(context).inflateTransition(R.transition.search_transform);
      fragment.setSharedElementEnterTransition(transition);
    }
    return fragment;
  }

  @Override
  protected View createView(LayoutInflater inflater,
                            @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
//    View view = inflater.inflate(R.layout.fgmt_search, container, false);
    return inflater.inflate(R.layout.fgmt_search, container, false);
  }

  private List<ItemStationBean> sourceData;

  @Override
  public void onResume() {
    super.onResume();

    displaySearchHistory();
  }

  private void displaySearchHistory() {
    String[] searchRecord = LvAppUtils.getSearchRecord();
    if (searchRecord.length > 0) {
      searchHistory.setVisibility(View.VISIBLE);
      if (sourceData == null) {
        sourceData = new ArrayList<>();
      }
      sourceData.clear();
      for (int i = 0; i < searchRecord.length; i++) {
        sourceData.add(new ItemStationBean(searchRecord[i]));
      }
//      TagAdapter<GunTagView<ItemStationBean>, ItemStationBean> adapter =
//          new GunTagAdapter<>(getContext(), sourceData);

      TagAdapter<SearchTagView<ItemStationBean>, ItemStationBean> adapter =
              new SearchTagAdapter<>(getContext(), sourceData);
      flowLayout.setAdapter(adapter);
      adapter.setOnSubscribeListener(new OnFlexboxSubscribeListener<ItemStationBean>() {
        @Override
        public void onSubscribe(List<ItemStationBean> selectedItem) {
          etSearch.setText(selectedItem.get(0).getContent());
          searchStation(selectedItem.get(0).getContent());
        }
      });
    } else {
      searchHistory.setVisibility(View.GONE);
    }
  }

  @Override
  protected void initView(@Nullable Bundle savedInstanceState) {
    super.initView(savedInstanceState);

    etSearch.requestFocus();
    etSearch.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }

      @Override
      public void afterTextChanged(Editable s) {
        if (s.length() > 0) {
          ivbDel.setVisibility(View.VISIBLE);
        } else {
          ivbDel.setVisibility(View.GONE);
        }
      }
    });
    etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
          String s = etSearch.getText().toString();
          if (!LvTextUtil.isEmpty(s)) {
            searchStation(s);
          } else {
            /* no-op */
          }
        }
        return false;
      }
    });
  }

  private void searchStation(String name) {
    DebugLog.log("_____ searchStation");
    LvCommonUtil.hideSoftInput(_mActivity);
    LvAppUtils.addSearchRecord(name);
    showLoadingDialog();

    LvRepository.getInstance()
        .filterWithName(name)
        .subscribe(new SimpleSubscriber<List<SubstationBean>>() {
          @Override
          public void onError(Throwable e) {
            super.onError(e);
            dismissLoadingDialog();
            showToast("未匹配到充电站");
          }

          @Override
          public void onNext(List<SubstationBean> substations) {
            dismissLoadingDialog();
            if (substations.size() > 0) {
              refreshSearchResult(substations);
            } else {
              showToast("未匹配到充电站");
            }
          }
        });
  }

  private void refreshSearchResult(List<SubstationBean> substations) {
    // TODO 显示搜索到的充电站
    if (recyclerView.getAdapter() == null) {
      View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          // TODO: 2017/5/18 收藏处理
          clickFavor((SubstationBean) v.getTag());
        }
      };
      LinearLayoutManager lm = new LinearLayoutManager(_mActivity.getApplicationContext());
      recyclerView.setLayoutManager(lm);
      recyclerView.setHasFixedSize(true);
      OrderListFragment.OrderListAdapter adapter = new OrderListFragment.OrderListAdapter(clickListener);
      adapter.setSubstations(substations);
      recyclerView.setAdapter(adapter);

      DividerItemDecoration decoration = new DividerItemDecoration(_mActivity.getApplicationContext(), DividerItemDecoration.VERTICAL);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        decoration.setDrawable(getResources().getDrawable(R.drawable.item_divider, _mActivity.getTheme()));
      } else {
        decoration.setDrawable(getResources().getDrawable(R.drawable.item_divider));
      }
      recyclerView.addItemDecoration(decoration);
    } else {
      OrderListFragment.OrderListAdapter adapter = (OrderListFragment.OrderListAdapter) recyclerView.getAdapter();
      adapter.setSubstations(substations);
      adapter.notifyDataSetChanged();
    }
  }

  FavorCase   favorCase;
  UnFavorCase unFavorCase;

  public void clickFavor(SubstationBean bean) {
    if (!LvAppUtils.isLogin()) {
      LoginActivity.navigation(false);
    } else {
      if (bean.isFavorites) { // 已收藏
        clearFavor(bean);
      } else { //收藏电站
        saveFavor(bean);
      }
    }
  }

  private void saveFavor(final SubstationBean bean) {
    if (favorCase == null) {
      favorCase = new FavorCase();
    }
    showLoadingDialog();
    favorCase.params(bean.areaId)
        .createObservable(new SimpleSubscriber<SimpleResponse>() {
          @Override
          public void onError(Throwable e) {
            super.onError(e);
            dismissLoadingDialog();
            showToast(getString(R.string.network_not_available));
          }

          @Override
          public void onNext(SimpleResponse simpleResponse) {
            if (simpleResponse.isSuccess()) {
              bean.isFavorites = true;
              notifyRecyclerViewDataChanged();
            } else {
              showToast(simpleResponse.msg);
            }
          }

          @Override
          public void onCompleted() {
            dismissLoadingDialog();
          }
        });
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
              notifyRecyclerViewDataChanged();
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

  private void notifyRecyclerViewDataChanged() {
    OrderListFragment.OrderListAdapter adapter = (OrderListFragment.OrderListAdapter) recyclerView.getAdapter();
    if (adapter != null) {
      adapter.notifyDataSetChanged();
    }
  }
  
  private void handleFavor() {
    if (rvFavor == null) {
      return;
    }
    if (LvAppUtils.isLogin()) {
      tvFavor.setVisibility(View.GONE);
      btnLogin.setVisibility(View.GONE);

      rvFavor.setVisibility(View.VISIBLE);
      // TODO 请求收藏数据
    } else {
      tvFavor.setVisibility(View.VISIBLE);
      btnLogin.setVisibility(View.VISIBLE);

      rvFavor.setVisibility(View.GONE);
    }
  }

  /**
   * ---------------
   * <p> Click events
   * ---------------
   */
  @OnClick(R.id.btn_clear)
  public void clickSearchHistoryClear() {
    LvAppUtils.clearSearchRecord();
    displaySearchHistory();
  }

  @OnClick(R.id.ivb_del)
  public void clickDel() {
    etSearch.setText("");
    etSearch.requestFocus();

    refreshSearchResult(null);
  }

  @OnClick(R.id.btn_cancel)
  public void clickCancel() {
    LvCommonUtil.hideSoftInput(_mActivity);
    pop();
  }

  @OnClick(R.id.btn_login)
  public void clickLogin() {
    LoginActivity.navigation(false);
  }
}
