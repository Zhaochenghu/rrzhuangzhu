package com.bxchongdian.app.views.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bxchongdian.app.R;
import com.bxchongdian.app.event.FinishActivityEvent;
import com.bxchongdian.app.views.base.LvBaseAppCompatActivity;
import com.bxchongdian.lib_zxing.activity.CaptureFragment;
import com.bxchongdian.lib_zxing.activity.CodeUtils;
import com.bxchongdian.model.bean.StationInfoBean;
import com.bxchongdian.model.response.ChargingResponse;
import com.bxchongdian.presenter.station.StationInfoContract;
import com.bxchongdian.presenter.station.StationInfoPresenter;
import com.trello.rxlifecycle.ActivityEvent;

import butterknife.OnClick;
import cn.com.leanvision.baseframe.rx.RxBus;
import cn.com.leanvision.baseframe.rx.SimpleSubscriber;
import cn.com.leanvision.baseframe.util.LvTextUtil;

/********************************
 * Created by lvshicheng on 2017/2/16.
 * <p>
 * 二维码扫描
 * 二维码参数分割的逗号是英文状态。
 ********************************/
@Route(path = "/login/charging/scan")
public class ScanActivity extends LvBaseAppCompatActivity implements StationInfoContract.View{

  public static boolean isOpen = false;
  private StationInfoPresenter presenter;
  private String gunId;
  private CaptureFragment captureFragment;

  public static void navigation() {
    ARouter.getInstance().build("/login/charging/scan").navigation();
  }

  @Override
  protected void setContentView(Bundle savedInstanceState) {
    setContentView(R.layout.aty_scan);
    if (savedInstanceState == null) {
      captureFragment = CaptureFragment.newInstance();
      captureFragment.setAnalyzeCallback(analyzeCallback);
      getSupportFragmentManager().beginTransaction().replace(R.id.fl_zxing_container, captureFragment).commit();
    }
  }

  @Override
  protected void initView(Bundle savedInstanceState) {
    super.initView(savedInstanceState);
    initToolbarNav("扫一扫");

    //在StationInfoActivity的startChargingSuccess()中发送事件
    RxBus.getInstance()
        .toObservable(FinishActivityEvent.class)
        .compose(ScanActivity.this.<FinishActivityEvent>bindUntilEvent(ActivityEvent.DESTROY))
        .subscribe(new SimpleSubscriber<FinishActivityEvent>() {
          @Override
          public void onNext(FinishActivityEvent finishActivityEvent) {
            ScanActivity.this.finish();
          }
        });
  }

  @OnClick(R.id.iv_lights)
  public void clickLights() {
    if (!isOpen) {
      isOpen = true;
    } else {
      isOpen = false;
    }
    CodeUtils.isLightEnable(isOpen);
  }

  @OnClick(R.id.iv_sn)
  public void clickSn() {
    SnInputActivity.navigation();
  }

  /**
   * 二维码解析回调函数
   */
  CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
    @Override
    public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
      // 处理充电桩二维码返回的结果
      //二维码格式：x,x,x,x,x  代表平台商，运营商，子站，充电桩，充电枪id
      try {
        String[] str = result.split(",");
        gunId = str[4];
        presenter.queryStation(result);
      }catch (Exception e){
        showToast("二维码出错了");
        scanAgain();
      }
    }

    /**
     * 解析错误
     */
    @Override
    public void onAnalyzeFailed() {
      showToast("二维码出错了");
      scanAgain();
    }
  };

  /**
   * 延迟3秒后再次扫描
   */
  private void scanAgain(){
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        captureFragment.restartScan();
      }
    }, 3000);
  }

  @Override
  protected void initPresenter() {
    super.initPresenter();
    presenter = new StationInfoPresenter();
    presenter.attachView(this);
  }

  @Override
  protected void destroyPresenter() {
    super.destroyPresenter();
    presenter.detachView();
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
    if (LvTextUtil.isEmpty(msg)){
      showToast("网络异常");
    }else {
      showToast(msg);
    }
    scanAgain();
  }

  @Override
  public void queryStationInfoSuccess(StationInfoBean mStationInfoBean) {
    if (mStationInfoBean != null) {
      StationInfoActivity.navigation(mStationInfoBean, gunId);
    }
  }

  @Override
  public void queryStationInfoFailed(String msg) {
    requestFailed(msg);
  }

  @Override
  public void callBack(ChargingResponse.Charging charging) {

  }

  @Override
  public void startChargingFailed(String msg) {

  }
}
