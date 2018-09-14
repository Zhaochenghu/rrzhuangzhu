package com.bxchongdian.app.views.activities;

import android.os.Bundle;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bxchongdian.app.R;
import com.bxchongdian.app.views.base.LvBaseAppCompatActivity;
import com.bxchongdian.app.wigets.ProgressCircleView;

import butterknife.BindView;

/********************************
 * Created by lvshicheng on 2017/2/28.
 ********************************/
@Route(path = "/unknown/charging")
public class ChargingActivity extends LvBaseAppCompatActivity {

  @BindView(R.id.pcv)
  ProgressCircleView pcv;
  @BindView(R.id.tv_money)
  TextView           tvMoney;
  @BindView(R.id.tv_time)
  TextView           tvTime;
  @BindView(R.id.tv_power)
  TextView           tvPower;
  @BindView(R.id.tv_remain)
  TextView           tvRemain;

  public static void navigation() {
    ARouter.getInstance().build("/unknown/charging").navigation();
  }

  @Override
  protected void setContentView(Bundle savedInstanceState) {
    setContentView(R.layout.aty_charging);
  }

  @Override
  protected void initView(Bundle savedInstanceState) {
    super.initView(savedInstanceState);

    initToolbarNav("充电");

    pcv.setText("35%\n充电中");
    pcv.setProgress(0.3f);
  }
}
