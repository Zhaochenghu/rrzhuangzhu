package com.bxchongdian.app.wigets;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bxchongdian.app.LvApplication;
import com.bxchongdian.app.R;
import com.bxchongdian.app.custom.toast.IToast;
import com.bxchongdian.app.custom.toast.ToastUtils;
import com.bxchongdian.app.utils.IntentUtils;
import com.bxchongdian.app.views.activities.GPSNaviActivity;
import com.bxchongdian.app.views.fragments.OrderMapFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/********************************
 * Created by lvshicheng on 2017/4/10.
 ********************************/
public class MapNavListView extends FrameLayout {

  @BindView(R.id.inner_nav)
  TextView innerNav;
  @BindView(R.id.gd_nav)
  TextView gdNav;
  @BindView(R.id.baidu_nav)
  TextView baiduNav;
  @BindView(R.id.nav_cancel)
  TextView navCancel;

  private double lat;
  private double lng;

  public MapNavListView(@NonNull Context context) {
    this(context, null);
  }

  public MapNavListView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    initView();
  }

  private void initView() {
    View view = LayoutInflater.from(getContext()).inflate(R.layout.view_map_nav_list, this);
    ButterKnife.bind(this, view);

    view.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        hide();
      }
    });
  }

  @OnClick(R.id.inner_nav)
  public void clickNavInner() {
    if (OrderMapFragment.aMapLocation == null) {
      ToastUtils.getInstance(LvApplication.getContext()).makeTextShow("获取当前位置失败", IToast.LENGTH_SHORT);
//      ToastUtils.makeText(getContext(), "获取当前位置失败", CustomToast.LENGTH_SHORT).show();
      return;
    }
    hide();
    GPSNaviActivity.navigation(lat, lng);
  }

  @OnClick(R.id.gd_nav)
  public void clickNavGd() {
    hide();
    IntentUtils.startGaodeMap(getContext(), lat, lng);
  }

  @OnClick(R.id.baidu_nav)
  public void clickBaidu() {
    hide();
    IntentUtils.startBaiduMap(getContext(), lat, lng);
  }

  @OnClick(R.id.nav_cancel)
  public void clickNavCancel() {
    hide();
  }

  public void show(Activity _mActivity) {
    ViewGroup androidContentView = (ViewGroup) _mActivity.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
    ViewGroup.LayoutParams contentParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    androidContentView.addView(this, contentParams);
  }

  public void hide() {
    ((ViewGroup) MapNavListView.this.getParent()).removeView(MapNavListView.this);
  }

  public void setLatlng(double lat, double lng) {
    this.lat = lat;
    this.lng = lng;
  }
}
