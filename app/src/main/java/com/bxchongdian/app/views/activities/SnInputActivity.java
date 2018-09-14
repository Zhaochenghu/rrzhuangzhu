package com.bxchongdian.app.views.activities;

import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bxchongdian.app.R;
import com.bxchongdian.app.event.FinishActivityEvent;
import com.bxchongdian.app.views.base.LvBaseAppCompatActivity;
import com.bxchongdian.app.views.fragments.SnInputFragment;
import com.trello.rxlifecycle.ActivityEvent;

import cn.com.leanvision.baseframe.rx.RxBus;
import cn.com.leanvision.baseframe.rx.SimpleSubscriber;

/********************************
 * Created by lvshicheng on 2017/2/16.
 * <p>
 * 输入SN码
 ********************************/
@Route(path = "/login/charging/sninput")
public class SnInputActivity extends LvBaseAppCompatActivity {

  public static void navigation() {
    ARouter.getInstance().build("/login/charging/sninput").navigation();
  }

  @Override
  protected void setContentView(Bundle savedInstanceState) {
    setContentView(R.layout.aty_sn_input);
  }

  @Override
  protected void initView(Bundle savedInstanceState) {
    if (savedInstanceState == null) {
      loadRootFragment(R.id.fl_container, SnInputFragment.newInstance());
    }
    initToolbarNav("输入SN", new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressedSupport();
      }
    });

    RxBus.getInstance()
        .toObservable(FinishActivityEvent.class)
        .compose(SnInputActivity.this.<FinishActivityEvent>bindUntilEvent(ActivityEvent.DESTROY))
        .subscribe(new SimpleSubscriber<FinishActivityEvent>() {
          @Override
          public void onNext(FinishActivityEvent finishActivityEvent) {
            SnInputActivity.this.finish();
          }
        });
  }
}
