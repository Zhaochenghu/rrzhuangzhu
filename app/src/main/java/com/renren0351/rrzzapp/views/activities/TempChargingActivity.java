package com.renren0351.rrzzapp.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.renren0351.rrzzapp.R;
import com.renren0351.rrzzapp.views.base.LvBaseAppCompatActivity;
import com.renren0351.rrzzapp.wigets.ProgressCircleView;
import com.renren0351.model.dagger.ApiComponentHolder;
import com.renren0351.model.response.StationStatusResponse;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import cn.com.leanvision.baseframe.rx.SimpleSubscriber;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/04/27
 *     desc   :
 *     version: 1.0
 * </pre>
 */
@Route(path = "/login/main/tempcharging")
public class TempChargingActivity extends LvBaseAppCompatActivity {
	@BindView(R.id.btn_stop)
	Button 				btStop;
	@BindView(R.id.tv_status)
	TextView 			tvStatus;
	@BindView(R.id.tv_number)
	TextView 			tvNumber;
	@BindView(R.id.pcv)
	ProgressCircleView 	pcv;
	@BindView(R.id.tv_station_name)
	TextView 			tvStationName;
	@BindView(R.id.tv_charging_total)
	TextView 			tvChargingTotal;
	@BindView(R.id.tv_charging_time)
	TextView 			tvChargingTime;
	@BindView(R.id.tv_charging_pay)
	TextView 			tvChargingPay;
	@BindView(R.id.tv_charging_v)
	TextView 			tvChargingV;
	@BindView(R.id.tv_charging_a)
	TextView 			tvChargingA;
	@BindView(R.id.tv_charging_p)
	TextView 			tvChargingP;

	private String stationId; // 充电站ID
	private String pileCode; // 充电桩ID
	private int cpInterfaceId; // 抢口ID
	private Subscription chargingCheckSubscription;
	private Subscription chargingSubscription;

	public static void navigation(String stationId, String stationName, String pileCode, int cpInterfaceId) {
		ARouter.getInstance().build("/login/main/tempcharging")
				.withString("stationId", stationId)
				.withString("stationName", stationName)
				.withString("pileCode", pileCode)
				.withInt("cpInterfaceId", cpInterfaceId)
				.navigation();
	}

	@Override
	protected void setContentView(Bundle savedInstanceState) {
		setContentView(R.layout.aty_temp_charging);
	}

	@Override
	protected void initView() {
		initToolbarNav("充电详情");

		btStop.setVisibility(View.GONE);
		Intent intent = getIntent();
		stationId = intent.getStringExtra("stationId");
		pileCode = intent.getStringExtra("pileCode");
		cpInterfaceId = intent.getIntExtra("cpInterfaceId", 1);
		tvStationName.setText(getIntent().getStringExtra("stationName"));
		startChargingStatus();
	}

	private void startChargingStatus() {
		if (chargingCheckSubscription == null || chargingCheckSubscription.isUnsubscribed()) {
			// 每5秒中检测一次查询状态
			chargingCheckSubscription = Observable.interval(0, 5, TimeUnit.SECONDS)
					.compose(SchedulersCompat.<Long>applyNewSchedulers())
					.subscribe(new SimpleSubscriber<Long>() {

						@Override
						public void onNext(Long aLong) {
							queryPileChargingStatus();
						}
					});
		}
	}

	public void queryPileChargingStatus() {
		if (chargingSubscription != null && !chargingSubscription.isUnsubscribed()) {
			return;
		}
		chargingSubscription = createChargingObservable()
				.subscribe(new SimpleSubscriber<StationStatusResponse.StationStatus>() {

					@Override
					public void onCompleted() {
						dismissLoadingDialog();
					}

					@Override
					public void onError(Throwable e) {
						super.onError(e);
						showToast("获取信息失败");
					}

					@Override
					public void onNext(StationStatusResponse.StationStatus stationStatus) {
						setStatusShow(stationStatus);
					}
				});
	}

	private Observable<StationStatusResponse.StationStatus> createChargingObservable() {
		HashMap<String, Object> map = new HashMap<>();
		//map.put("substationId", stationId);
		map.put("cpId", pileCode);
		map.put("cpinterfaceId", cpInterfaceId);
		return ApiComponentHolder.sApiComponent.apiService()
				.getOtherStationStatus(map)
				.take(1)
				.flatMap(new Func1<StationStatusResponse, Observable<StationStatusResponse.StationStatus>>() {
					@Override
					public Observable<StationStatusResponse.StationStatus> call(StationStatusResponse stationStatusResponse) {
						return Observable.from(stationStatusResponse.contentList);
					}
				})
				.compose(SchedulersCompat.<StationStatusResponse.StationStatus>applyNewSchedulers());
	}

	public void setStatusShow(StationStatusResponse.StationStatus stationStatus) {
		int soc = stationStatus.soc;
		int outV = Integer.valueOf(stationStatus.outV);
		int outA = Integer.valueOf(stationStatus.outA);
		float outV1 = Float.valueOf(stationStatus.outV);
		float outA1 = Float.valueOf(stationStatus.outA);
		if (outA == 0 && outV == 0) {
			pcv.setText(String.format("%d%%\n已停止", soc));
			tvStatus.setText("已停止");
		} else {
			pcv.setText(String.format("%d%%\n充电中", soc));
			tvStatus.setText("充电中");
		}

		pcv.setProgress(soc / 100.0f);

//    tvStationName.setText(AppInfosPreferences.get().getChargeStationName());
		tvNumber.setText(String.format("终端编号 %s", stationStatus.cpId));
		try {
			Float electric = Integer.parseInt(stationStatus.electric) / 100f;
			tvChargingTotal.setText(String.format("%.1f 度", electric)); // 已充电
			//测试
//      tvChargingTotal.setText("4.5 度");
		} catch (Exception ignored) {

		}

		long totalTime = stationStatus.totalTime;// 单位：分钟
		long hour = totalTime / 60;
		long minute = totalTime % 60;
		if (hour <= 0) {
			tvChargingTime.setText(String.format("%s分钟", minute)); // 已充时长
		} else {
			tvChargingTime.setText(String.format("%s小时%s分钟", hour, minute)); // 已充时长
		}

		float fee = Float.valueOf(stationStatus.serviceFee);
		tvChargingPay.setText(String.format("%.2f 元", fee / 100)); // 充电费用
		tvChargingV.setText(String.format("%.2f V", outV1 / 10)); // 充电电压
		tvChargingA.setText(String.format("%.2f A", outA1 / 10)); // 充电电流
		//tvChargingP.setText(stationStatus.getPower()); // 充电功率
		tvChargingP.setText(String.format("%.2f KW", outV1 / 10 * outA1 / 10 / 1000));
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (chargingCheckSubscription != null && !chargingCheckSubscription.isUnsubscribed()) {
			chargingCheckSubscription.unsubscribe();
			chargingCheckSubscription = null;
		}

		if (chargingSubscription != null
				&& !chargingSubscription.isUnsubscribed()) {
			chargingSubscription.unsubscribe();
			chargingSubscription = null;
		}
	}
}
