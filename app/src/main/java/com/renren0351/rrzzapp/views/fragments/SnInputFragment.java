package com.renren0351.rrzzapp.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.renren0351.rrzzapp.R;
import com.renren0351.rrzzapp.event.StartBrotherEvent;
import com.renren0351.rrzzapp.views.activities.StationInfoActivity;
import com.renren0351.rrzzapp.views.base.LvBaseFragment;
import com.renren0351.model.bean.StationInfoBean;
import com.renren0351.model.response.ChargingResponse;
import com.renren0351.presenter.station.StationInfoContract;
import com.renren0351.presenter.station.StationInfoPresenter;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cn.com.leanvision.baseframe.rx.RxBus;
import cn.com.leanvision.baseframe.util.LvTextUtil;

/********************************
 * Created by lvshicheng on 2017/2/16.
 * <p>
 * 输入SN码
 * <p>
 ********************************/
public class SnInputFragment extends LvBaseFragment implements StationInfoContract.View{

  @BindView(R.id.et_pile)
  EditText etPile;
  @BindView(R.id.et_gun)
  EditText etGun;
  @BindView(R.id.btn_confirm)
  Button   btnConfirm;

  private String strSn;
  private StationInfoPresenter presenter;

  public static SnInputFragment newInstance() {
    Bundle args = new Bundle();
    SnInputFragment fragment = new SnInputFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  protected View createView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fgmt_sn_input, container, false);
  }

  @Override
  protected void initView(@Nullable Bundle savedInstanceState) {
    super.initView(savedInstanceState);
    btnConfirm.setEnabled(false);
  }

  @Override
  protected void initPresenter() {
    presenter = new StationInfoPresenter();
    presenter.attachView(this);
  }

  @Override
  protected void destroyPresenter() {
    presenter.detachView();
  }

  @Override
  public void onSupportVisible() {
    super.onSupportVisible();

    StartBrotherEvent startBrotherEvent = new StartBrotherEvent(null);
    startBrotherEvent.title = "输入SN";
    RxBus.getInstance().postEvent(startBrotherEvent);

//    etSn.setText("0001,0020,0020,00000192,00"); // FOR TEST
//    etSn.setSelection(etSn.getText().length());
  }

  @OnTextChanged(value = {R.id.et_pile, R.id.et_gun}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
  public void snChanged() {
    if (!LvTextUtil.isEmpty(etPile.getText().toString()) && !LvTextUtil.isEmpty(etGun.getText().toString())){
      btnConfirm.setEnabled(true);
      //逗号是英文状态
      strSn = "0001,0000,0000," + etPile.getText().toString().trim() + "," + etGun.getText().toString().trim();
    }
  }

  @OnClick(R.id.btn_confirm)
  public void clickConfirm() {
    if (LvTextUtil.isEmpty(etPile.getText().toString())){
      showToast("请输入充电桩编号");
      return;
    }
    if (LvTextUtil.isEmpty(etGun.getText().toString())){
      showToast("请输入充电枪编号");
      return;
    }
    presenter.queryStation(strSn);
  }

  @OnClick(R.id.btn_cancel)
  public void clickCancel() {

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
  }

  @Override
  public void queryStationInfoSuccess(StationInfoBean mStationInfoBean) {
    if (mStationInfoBean != null) {
      StationInfoActivity.navigation(mStationInfoBean, etGun.getText().toString().trim());
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
