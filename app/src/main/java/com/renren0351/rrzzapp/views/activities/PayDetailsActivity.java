package com.renren0351.rrzzapp.views.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.renren0351.rrzzapp.LvAppUtils;
import com.renren0351.rrzzapp.R;
import com.renren0351.rrzzapp.views.base.LvBaseAppCompatActivity;
import com.renren0351.model.bean.ChargeRecordBean;

import butterknife.BindView;
import cn.com.leanvision.baseframe.util.LvTimeUtil;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/05/19
 *     desc   : 充电记录详情页
 *     version: 1.0
 * </pre>
 */
@Route(path = "/mime/pay_details")
public class PayDetailsActivity extends LvBaseAppCompatActivity {

  @BindView(R.id.tv_start_time)
  TextView tvStartTime;
  @BindView(R.id.tv_stop_time)
  TextView tvStopTime;
  @BindView(R.id.tv_sum_time)
  TextView tvSumTime;
  @BindView(R.id.tv_sum_power)
  TextView tvSumPower;
  @BindView(R.id.tv_electric_fee)
  TextView tvElectricFee; //电费
  @BindView(R.id.tv_service_fee)
  TextView tvServiceFee;  //服务费
  @BindView(R.id.tv_park_fee)
  TextView tvParkFee;     //停车费
  @BindView(R.id.tv_order_code)
  TextView tvOrderCode;   //订单号
  @BindView(R.id.tv_sub_name)
  TextView tvStationName; //子站名称
  @BindView(R.id.tv_pile_code)
  TextView tvPileCode;    //充电站编号
  @BindView(R.id.tv_sum_balance)
  TextView tvSumBalance;  //费用总计
  @BindView(R.id.tv_pay_type)
  TextView tvPayType;     //支付方式
  @BindView(R.id.tv_pay_cardId)
  TextView tvPayCardId;
  @BindView(R.id.ll_pay_cardId)
  LinearLayout llPayCardId;

  public static void navigation(ChargeRecordBean bean) {
    ARouter.getInstance().build("/mime/pay_details")
        .withParcelable(ChargeRecordBean.class.getSimpleName(), bean)
        .navigation();
  }

  @Override
  protected void setContentView(Bundle savedInstanceState) {
    setContentView(R.layout.aty_record_details);
  }

  @Override
  protected void initView() {
    initToolbarNav("充电详情");

    inflateData();
  }

  private void inflateData() {
    ChargeRecordBean bean = getIntent().getParcelableExtra(ChargeRecordBean.class.getSimpleName());
    if (bean.areaName.equals("")) {

      tvStartTime.setText(bean.startTime);
      tvStopTime.setText(bean.endTime);
      tvSumTime.setText(LvTimeUtil.transStampToHMS(LvAppUtils.calculateDiffDate(bean.startTime, bean.endTime)));
      tvSumPower.setText(String.format("%.1f度", bean.getTotalElectric()));
      tvElectricFee.setText(String.format("%.2f元", bean.getElecFees())); // 电费
      tvServiceFee.setText(String.format("%.2f元", bean.getServiceFees()));  // 服务费
      tvParkFee.setText("无");

      tvOrderCode.setText(bean.batch);
      tvStationName.setText(bean.substationName);
      tvPileCode.setText(bean.cpId);
      tvSumBalance.setText(String.format("%.2f元", bean.getTransamount()));
      tvPayType.setText(bean.getPayType());
      if (bean.transType == 0) {
        llPayCardId.setVisibility(View.VISIBLE);
        tvPayCardId.setText(bean.cardId);
      }
    } else {
      tvStartTime.setText(bean.startTime);
      tvStopTime.setText(bean.endTime);
      tvSumTime.setText(LvTimeUtil.transStampToHMS(LvAppUtils.calculateDiffDate(bean.startTime, bean.endTime)));
      tvSumPower.setText(String.format("%.1f度", bean.getTotalElectric()));
      tvElectricFee.setText(String.format("%.2f元", bean.getElecFees())); // 电费
      tvServiceFee.setText(String.format("%.2f元", bean.getServiceFees()));  // 服务费
      tvParkFee.setText("无");

      tvOrderCode.setText(bean.batch);
      tvStationName.setText(bean.areaName);
      tvPileCode.setText(bean.cpId);
      tvSumBalance.setText(String.format("%.2f元", bean.getTransamount()));
      tvPayType.setText(bean.getPayType());
      if (bean.transType == 0) {
        llPayCardId.setVisibility(View.VISIBLE);
        tvPayCardId.setText(bean.cardId);
      }
    }
  }
}
