package com.renren0351.rrzzapp.wigets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nineoldandroids.animation.ValueAnimator;
import com.renren0351.rrzzapp.R;
import com.renren0351.model.bean.SubstationBean;

import butterknife.BindView;
import butterknife.ButterKnife;

/********************************
 * Created by lvshicheng on 2017/2/21.
 * modify by 赵成虎 on 2018/5/4
 ********************************/
public class OrderView extends FrameLayout {

  @BindView(R.id.ivb_favor)
  ImageButton ivbFavor;
  @BindView(R.id.tv_name)
  TextView    tvName;
  @BindView(R.id.iv_rmb)
  ImageView   ivRmb;
  @BindView(R.id.tv_service)
  TextView    tvService;
  @BindView(R.id.iv_pay)
  ImageView   ivPay;
  @BindView(R.id.tv_pay_type)
  TextView    tvPayType;
  @BindView(R.id.tv_distance)
  TextView    tvDistance;
  @BindView(R.id.tv_ac)
  TextView    tvAc;
  @BindView(R.id.tv_dc)
  TextView    tvDc;
  @BindView(R.id.tv_address)
  TextView    tvAddr;

  @BindView(R.id.rl_container)
  SwipeFrameLayout rlContainer;


  private int height;
  private boolean isHiding = false;

  private SubstationBean substationBean;

  public OrderView(Context context) {
    super(context);
    initView();
  }

  public OrderView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initView();
  }

  private void initView() {
    View view = LayoutInflater.from(getContext()).inflate(R.layout.view_order_popu, this);
    ButterKnife.bind(this, view);

    this.setVisibility(View.INVISIBLE);
    // 处理手势滑动
    rlContainer.setOnSwipeListener(new SwipeFrameLayout.OnSwipeListener() {
      @Override
      public void onHorSwipe() {

      }

      @Override
      public void onVerSwipe() {
        hide();
      }
    });
  }

  public void refreshData(final SubstationBean substationBean) {

    this.substationBean = substationBean;
    tvName.setText(substationBean.areaName);
    tvDistance.setText(substationBean.distance);

    tvAc.setVisibility(substationBean.hasTotalAC > 0 ? View.VISIBLE : View.GONE);
    tvDc.setVisibility(substationBean.hasTotalDC > 0 ? View.VISIBLE : View.GONE);
    tvAc.setText(String.format("空闲 %d /共 %d", substationBean.hasRestAC, substationBean.hasTotalAC));
    tvDc.setText(String.format("空闲 %d /共 %d", substationBean.hasRestDC, substationBean.hasTotalDC));
//费用模板
   // tvService.setText("充电费:" + substationBean.chargingFee + "\r\n服务费:" + substationBean.serviceFee + "\r\n停车费:"+substationBean.stopFee);
    //地址：乐园大街\n运营时间： 6:00 - 24:00
    tvAddr.setText(String.format("地址：%s \r\n运营时间：%s", substationBean.address, substationBean.serviceTime));
    //支付方式：微信、支付宝、账户余额\n运营商：双杰\n服务电话：95588
    tvPayType.setText(String.format("支付方式：充电卡、本APP\r\n运营商：%s\r\n服务电话：%s",
            substationBean.companyName, substationBean.serviceCall));
    setFavor(substationBean.isFavorites);
  }

  public boolean show() {
    if (this.isShown()) {
      return false;
    }
    this.setVisibility(View.VISIBLE);
    height = rlContainer.getMeasuredHeight();
    rlContainer.setTranslationY(height);
    ValueAnimator animator = ValueAnimator.ofFloat(height, 0.0f);
    animator.setInterpolator(new LinearInterpolator());
    animator.setDuration(300);
    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        float value = (float) animation.getAnimatedValue();
        rlContainer.setTranslationY(value);
      }
    });
    animator.start();
    return true;
  }

  /**
   * @return true 表示执行隐藏动画
   */
  public boolean hide() {
    if (!this.isShown() || isHiding) {
      return false;
    }
    isHiding = true;
    height = rlContainer.getMeasuredHeight();
    ValueAnimator animator = ValueAnimator.ofFloat(0.0f, height);
    animator.setInterpolator(new LinearInterpolator());
    animator.setDuration(300);
    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        float value = (float) animation.getAnimatedValue();
        rlContainer.setTranslationY(value);
        if (value == height) {
          OrderView.this.setVisibility(INVISIBLE);
          isHiding = false;
        }
      }
    });
    animator.start();
    return true;
  }

  public int getBottomDialogHeight() {
    return rlContainer.getMeasuredHeight();
  }

  // 判断当前是否收藏
  boolean favor = false;

  public boolean isFavor() {
    return favor;
  }

  public void setFavor(boolean favor) {
    if (this.favor == favor) {
      return;
    }
    this.favor = favor;
    this.substationBean.isFavorites = favor;
    ivbFavor.setImageResource(favor ? R.drawable.ic_favor_selected : R.drawable.ic_favor);
  }

  public boolean isHiding(){
    return isHiding;
  }

  /**
   * 刷新费用信息
   * @param eValue 电费
   * @param sValue 服务费
   * @param pValue 停车费
   */
  public void refreshFee(float eValue, float sValue, float pValue){
    if (pValue == 0f){
      tvService.setText(String.format("充电费：%.2f 元/度\r\n服务费：%.2f 元/度\r\n停车费：无", eValue,sValue));
    }else {
      tvService.setText(String.format("充电费：%.2f 元/度\r\n服务费：%.2f 元/度\r\n停车费：%.2f 元/小时", eValue,sValue,pValue));
    }
  }
}
