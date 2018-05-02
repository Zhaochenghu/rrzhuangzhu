package com.renren0351.rrzzapp.views.base;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.renren0351.rrzzapp.LvApplication;
import com.renren0351.rrzzapp.R;
import com.renren0351.rrzzapp.custom.toast.IToast;
import com.renren0351.rrzzapp.custom.toast.ToastUtils;
import com.renren0351.rrzzapp.views.dialog.loaddialog.LoadingDialog;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.com.leanvision.fragmentation.SupportFragment;
import cn.com.leanvision.fragmentation.anim.FragmentAnimator;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/********************************
 * Created by lvshicheng on 2017/2/13.
 ********************************/
public abstract class LvBaseFragment extends SupportFragment {

  private Dialog loadingDialog;

  private Unbinder bind;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = createView(inflater, container, savedInstanceState);
    bind = ButterKnife.bind(this, view);
    initPresenter();
    initView(savedInstanceState);
    doLogic();
    return view;
  }

  @Override
  public void onDestroyView() {
    destroyPresenter();
    clearSubscription();
    super.onDestroyView();
    if (bind != null) {
      bind.unbind();
    }
    dismissLoadingDialog();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    _mActivity = null;
  }

  @Override
  protected FragmentAnimator onCreateFragmentAnimator() {
    return super.onCreateFragmentAnimator();
  }

  protected abstract View createView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

  protected void initPresenter() {
  }

  protected void initView(@Nullable Bundle savedInstanceState) {

  }

  protected void doLogic() {
  }

  protected void destroyPresenter() {
  }

  private CompositeSubscription compositeSubscription;

  protected void addSubscription(Subscription subscription) {
    if (compositeSubscription == null)
      compositeSubscription = new CompositeSubscription();
    compositeSubscription.add(subscription);
  }

  protected void clearSubscription() {
    if (compositeSubscription != null && !compositeSubscription.isUnsubscribed()) {
      compositeSubscription.unsubscribe();
      compositeSubscription = null;
    }
  }

  private static final String TAG = "Fragmentation";

  protected void initToolbarMenu(Toolbar toolbar) {
    toolbar.inflateMenu(R.menu.hierarchy);
    toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
          case R.id.action_hierarchy:
            _mActivity.showFragmentStackHierarchyView();
            _mActivity.logFragmentStackHierarchy(TAG);
            break;
        }
        return true;
      }
    });
  }

  protected void showToast(CharSequence text) {
    ToastUtils.getInstance(LvApplication.getContext()).makeTextShow(text, IToast.LENGTH_SHORT);
  }

  protected void showToast(int resId) {
    ToastUtils.getInstance(LvApplication.getContext()).makeTextShow(resId, IToast.LENGTH_SHORT);
  }

  //  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  protected void showLoadingDialog() {
    if (loadingDialog == null) {
      loadingDialog = LoadingDialog.createLoadingDialog(_mActivity, "请等待...");
      loadingDialog.setCancelable(true);
      loadingDialog.show();
    } else {
//      loadingDialog.createObservable();
      loadingDialog.show();
    }
  }

  protected void dismissLoadingDialog() {
    if (loadingDialog != null && loadingDialog.isShowing()) {
      loadingDialog.dismiss();
      loadingDialog = null;
    }
  }
}
