package com.renren0351.rrzzapp.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.renren0351.rrzzapp.R;
import com.renren0351.rrzzapp.event.StartBrotherEvent;
import com.renren0351.rrzzapp.views.base.LvBaseFragment;

import cn.com.leanvision.baseframe.rx.RxBus;

/********************************
 * Created by lvshicheng on 2017/2/16.
 * <p>
 * 充电中
 ********************************/
public class ChargingStatueFragment extends LvBaseFragment {

  public static ChargingStatueFragment newInstance() {

    Bundle args = new Bundle();

    ChargingStatueFragment fragment = new ChargingStatueFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  protected View createView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fgmt_charging_statue, container, false);
  }

  @Override
  public void onSupportVisible() {
    super.onSupportVisible();

    StartBrotherEvent startBrotherEvent = new StartBrotherEvent(null);
    startBrotherEvent.title = "充电中";
    RxBus.getInstance().postEvent(startBrotherEvent);
  }
}
