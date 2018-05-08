package com.renren0351.rrzzapp.views.activities;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.renren0351.rrzzapp.R;
import com.renren0351.rrzzapp.custom.TimePicker;
import com.renren0351.rrzzapp.utils.FeeUtils;
import com.renren0351.rrzzapp.views.base.LvBaseAppCompatActivity;
import com.renren0351.model.bean.FeeBean;
import com.renren0351.model.bean.StationInfoBean;
import com.renren0351.model.dagger.ApiComponentHolder;
import com.renren0351.model.response.StationInfoResponse;
import com.renren0351.presenter.appointment.AppointmentContract;
import com.renren0351.presenter.appointment.AppointmentPresenter;
import com.renren0351.presenter.fee.FeeContract;
import com.renren0351.presenter.fee.FeePresenter;
import com.trello.rxlifecycle.ActivityEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cn.com.leanvision.baseframe.rx.SimpleSubscriber;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import cn.com.leanvision.baseframe.util.LvTextUtil;
import cn.com.leanvision.baseframe.util.LvTimeUtil;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/06/21
 *     desc   : 预约分两种模式（预约充电和定时充电）
 *     version: 1.0
 * </pre>
 */
@Route(path = "/login/main/order")
public class OrderSettingActivity extends LvBaseAppCompatActivity implements AppointmentContract.View, FeeContract.View {
	@BindView(R.id.sub_name)
	TextView subName;
	@BindView(R.id.pile_code)
	TextView pileCode;
	@BindView(R.id.pile_type)
	TextView pileType;
	@BindView(R.id.pile_power)
	TextView pilePower;
	@BindView(R.id.tv_e)
	TextView tvE;   //电费
	@BindView(R.id.tv_s)
	TextView tvS;   //服务费
	@BindView(R.id.tv_p)
	TextView tvP;   //停车费
	@BindView(R.id.tv_gun)
	TextView tvGun;
	@BindView(R.id.tv_mode)
	TextView tvMode;
	@BindView(R.id.tv_type)
	TextView tvType;
	@BindView(R.id.tv_prompt)
	TextView tvPrompt;
	@BindView(R.id.et_num)
	EditText etNum;
	@BindView(R.id.rl_duration)
	RelativeLayout rlDuration;
	@BindView(R.id.tv_time)
	TextView tvTime;
	@BindView(R.id.ll_time)
	LinearLayout llTime;
	@BindView(R.id.btn_start_order)
	Button btnStartOrder;
	@BindView(R.id.ll_order)
	LinearLayout llOrder;
	@BindView(R.id.tv_pattern)
	TextView tvPattern;
	@BindView(R.id.tv_surplus_duration)
	TextView tvSurplusDuration;
	@BindView(R.id.tv_start_time)
	TextView tvStartTime;
	@BindView(R.id.tv_surplus_time)
	TextView tvSurplusTime;
	@BindView(R.id.ll_surplus_time)
	LinearLayout llSurplusTime;
	@BindView(R.id.ll_exist)
	LinearLayout llExist;

	public static final int MODE_DURATION = 381;  //预约充电模式
	public static final int MODE_TIME = 964;      //定时充电模式
	private int currentMode;
	private AppointmentPresenter presenter;
	private FeePresenter feePresenter;
	private Subscription subscription;
	private PopupWindow popWindow;
	private String qr;
	private String stationId;
	private String cpId;
	private String gunId;
	private String orderId;
	private String time;
	private String startTime;
	private String stationName;
	private String[] prompt;
	private boolean orderState;

	public static void navigation(String stationId, String cpId, int gunId, String orderId,
								  boolean orderState, int mode, String time) {
		ARouter.getInstance().build("/login/main/order")
				.withString("stationId", stationId)
				.withString("cpId", cpId)
				.withInt("gunId", gunId)
				.withString("orderId", orderId)
				.withBoolean("orderState", orderState)
				.withInt("mode", mode)
				.withString("time", time)
				.navigation();
	}

	@Override
	protected void setContentView(Bundle savedInstanceState) {
		setContentView(R.layout.aty_order_setting);
	}

	@Override
	protected void initView() {
		orderState = getIntent().getBooleanExtra("orderState", false);
		stationId = getIntent().getStringExtra("stationId");
		cpId = getIntent().getStringExtra("cpId");
		gunId = getIntent().getIntExtra("gunId", 0) + "";
		tvGun.setText("枪" + getIntent().getIntExtra("gunId", 0));
		pileCode.setText(cpId);
		qr = String.format("0001,0000,0000,%s,%s", cpId, gunId);
		getPileInfo();

		//查询费用
		feePresenter.queryFees(stationId);

		if (orderState) {    //预约状态
			initToolbarNav("我的预约");
			llOrder.setVisibility(View.GONE);
			llExist.setVisibility(View.VISIBLE);
			currentMode = getIntent().getIntExtra("mode", 0);
			time = getIntent().getStringExtra("time");
			orderId = getIntent().getStringExtra("orderId");
			if (currentMode == MODE_TIME) {
				tvPattern.setText("定时充电");
				tvSurplusDuration.setVisibility(View.GONE);
				llSurplusTime.setVisibility(View.VISIBLE);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					Date date = sdf.parse(time);
					SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
					startTime = sdf2.format(date);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				minuteDown(LvTimeUtil.calcMinute(time), TimeUnit.MINUTES);
				tvStartTime.setText("开始时间\n" + startTime);
				tvSurplusTime.setText("剩余时间\n" + LvTimeUtil.calcTimeDuration(time));
			} else {
				tvPattern.setText("预约充电");
				tvSurplusDuration.setVisibility(View.VISIBLE);
				llSurplusTime.setVisibility(View.GONE);
				minuteDown(Long.parseLong(time), TimeUnit.SECONDS);
				tvSurplusDuration.setText("剩余时长：" + time + "分钟");
			}
		} else {     //没有预约
			initToolbarNav("预约");
			prompt = new String[2];
			prompt[0] = "预约充电：可以预约充电桩的充电枪，但在预约期间，他人也可以使用该充电枪，有可能会影响您的使用。";
			prompt[1] = "定时充电：需要连接充电枪，设置启动充电时间，到预定时间，充电桩会自动启动充电。";
			llOrder.setVisibility(View.VISIBLE);
			llExist.setVisibility(View.GONE);
			llTime.setVisibility(View.GONE);
			rlDuration.setVisibility(View.VISIBLE);
			currentMode = MODE_DURATION;
			tvPrompt.setText(prompt[0]);
			initPopWindow();
		}

	}

	/**
	 * 查询充电桩的信息
	 */
	private void getPileInfo() {
		ApiComponentHolder.sApiComponent
				.apiService()
				.queryStationInfo(qr)
				.take(1)
				.compose(this.<StationInfoResponse>bindUntilEvent(ActivityEvent.DESTROY))
				.compose(SchedulersCompat.<StationInfoResponse>applyNewSchedulers())
				.subscribe(new SimpleSubscriber<StationInfoResponse>() {
					@Override
					public void onError(Throwable e) {
						requestFailed(null);
					}

					@Override
					public void onNext(StationInfoResponse stationInfoResponse) {
						if (stationInfoResponse.isSuccess()) {
							StationInfoBean bean = stationInfoResponse.stationInfo;
							if (bean != null) {
								stationName = bean.areaName;
								subName.setText(stationName);
								pileType.setText(bean.getCpType());
								pilePower.setText(bean.ratedPower);
							}
						} else {
							requestFailed(stationInfoResponse.msg);
						}
					}
				});
	}

	@OnClick({R.id.ll_free, R.id.tv_select, R.id.btn_start_order, R.id.btn_cancel, R.id.tv_mode})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.tv_mode:
				showPopWindow();
				break;
			case R.id.ll_free:  //查看费用
				FeeActivity.navigation(stationId, stationName);
				break;
			case R.id.tv_select:   //设置时间
				showSelectTime();
				break;
			case R.id.btn_start_order:  //预约
				HashMap<String, Object> hashMap = new HashMap<>();
				hashMap.put("substationId", stationId);
				hashMap.put("cpId", cpId);
				hashMap.put("cpinterfaceId", gunId);
				if (currentMode == MODE_DURATION) {
					String text = etNum.getText().toString().trim();
					if (!LvTextUtil.isEmpty(text)) {
						try {
							if (Integer.parseInt(String.valueOf(text)) < 5) {
								showToast("最少为5分钟");
								return;
							} else {
								hashMap.put("duration", text);
							}
						} catch (Exception e) {
							showToast("输入错误");
							etNum.setText("");
							btnStartOrder.setEnabled(false);
						}
					}
				}
				if (currentMode == MODE_TIME && !LvTextUtil.isEmpty(time)) {
					hashMap.put("appointTime", time);
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				hashMap.put("appservertime", sdf.format(new Date()));
				presenter.order(hashMap);
				break;
			case R.id.btn_cancel:   //取消预约
				presenter.cancelOrder(orderId);
				break;
//      case R.id.btn_charging: //立即充电
//        StationInfoActivity.navigation(mStationInfoBean, gunId);
//        break;
		}
	}

	@OnTextChanged(value = R.id.et_num, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
	public void textChange(CharSequence text) {
		if (!LvTextUtil.isEmpty(text)) {
			try {
				if (Integer.parseInt(String.valueOf(text)) > 60) {
					showToast("最大时长为60分钟");
					btnStartOrder.setEnabled(false);
				} else {
					btnStartOrder.setEnabled(true);
				}
			} catch (Exception e) {
				showToast("输入错误");
				etNum.setText("");
				btnStartOrder.setEnabled(false);
			}

		}

	}

	/**
	 * 初始化底部弹窗
	 * 用于选择充电模式
	 */
	private void initPopWindow() {
		View popView = LayoutInflater.from(this).inflate(R.layout.view_pop_select, null);
		TextView tvOrder = (TextView) popView.findViewById(R.id.tv_one);
		tvOrder.setText("预约充电");
		TextView tvScan = (TextView) popView.findViewById(R.id.tv_two);
		tvScan.setText("定时充电");
//        TextView tvThree = (TextView) popView.findViewById(R.id.tv_three);
//        tvThree.setVisibility(View.GONE);
		TextView tvCancel = (TextView) popView.findViewById(R.id.tv_cancel);
		tvCancel.setOnClickListener(popListener);
		tvOrder.setOnClickListener(popListener);
		tvScan.setOnClickListener(popListener);
		popWindow = new PopupWindow(popView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		popWindow.setFocusable(true);
		popWindow.setTouchable(true);
		popWindow.setOutsideTouchable(false);
		popWindow.setAnimationStyle(R.style.AnimBottom);
		popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				setBackgroundAlpha(1f);
			}
		});
	}

	/**
	 * 弹窗点击监听
	 */
	View.OnClickListener popListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.tv_one:     //预约充电
					tvMode.setText("预约充电");
					currentMode = MODE_DURATION;
					llTime.setVisibility(View.GONE);
					rlDuration.setVisibility(View.VISIBLE);
					tvTime.setText("");
					tvPrompt.setText(prompt[0]);
					btnStartOrder.setEnabled(false);
					break;
				case R.id.tv_two:      //定时充电
					tvMode.setText("定时充电");
					currentMode = MODE_TIME;
					llTime.setVisibility(View.VISIBLE);
					rlDuration.setVisibility(View.GONE);
					etNum.setText("");
					tvPrompt.setText(prompt[1]);
					btnStartOrder.setEnabled(false);
					break;
				case R.id.tv_cancel:
					break;
			}
			popWindow.dismiss();
		}
	};

	/**
	 * 显示弹窗
	 */
	private void showPopWindow() {
		if (!popWindow.isShowing()) {
			popWindow.showAtLocation(popWindow.getContentView(), Gravity.BOTTOM, 0, 0);
			setBackgroundAlpha(0.7f);
		}
	}

	/**
	 * 选择定时充电时间
	 */
	private void showSelectTime() {
		TimePicker picker = new TimePicker.Builder(this)
				.textSize(20)
				.textColor(R.color.primary_text_black)
				.titleBackgroundColor("#ed3b5d")
				.cancelTextColor("#FFFFFF")
				.confirTextColor("#FFFFFF")
				.visibleItemsCount(5)
				.itemPadding(10)
				.build();
		picker.show();
		picker.setOnTimeItemClickListener(new TimePicker.OnTimeItemClickListener() {
			@Override
			public void onSelected(String... timeSelected) {
				tvTime.setVisibility(View.VISIBLE);
				startTime = timeSelected[0] + "：" + timeSelected[1];
				tvTime.setText("开始充电时间：" + startTime);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String date = sdf.format(new Date());
				time = date + " " + timeSelected[0] + ":" + timeSelected[1] + ":00";
				btnStartOrder.setEnabled(true);
			}

			@Override
			public void onCancel() {

			}
		});
	}

	@Override
	protected void initPresenter() {
		super.initPresenter();
		presenter = new AppointmentPresenter();
		presenter.attachView(this);
		feePresenter = new FeePresenter();
		feePresenter.attachView(this);
	}

	@Override
	protected void destroyPresenter() {
		super.destroyPresenter();
		presenter.detachView();
		feePresenter.detachView();
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
		if (LvTextUtil.isEmpty(msg)) {
			showToast("网络异常");
		} else {
			showToast(msg);
		}
	}

	@Override
	public void orderSuccess(String orderId) {
		showToast("预约成功");
		this.orderId = orderId;
		refreshUI();
	}

	@Override
	public void cancelOrderSuccess() {
		showToast("预约取消成功");
		finish();
	}

	@Override
	public void getFees(List<FeeBean> list) {
		float[] fee = FeeUtils.getCurrentFree(list);
		tvE.setText(String.format("%.2f 元/度", fee[0]));
		tvS.setText(String.format("%.2f 元/度", fee[1]));
		tvP.setText("无");
	}

	private void refreshUI() {
		initToolbarNav("我的预约");
		llOrder.setVisibility(View.GONE);
		llExist.setVisibility(View.VISIBLE);
		if (currentMode == MODE_TIME) {
			tvPattern.setText("定时充电");
			tvSurplusDuration.setVisibility(View.GONE);
			llSurplusTime.setVisibility(View.VISIBLE);
			tvStartTime.setText("开始时间\n" + startTime);
			minuteDown(LvTimeUtil.calcMinute(time), TimeUnit.MINUTES);
			tvSurplusTime.setText("剩余时间\n" + LvTimeUtil.calcTimeDuration(time));
		} else {
			tvPattern.setText("预约充电");
			tvSurplusDuration.setVisibility(View.VISIBLE);
			llSurplusTime.setVisibility(View.GONE);
			minuteDown(Long.parseLong(etNum.getText().toString().trim()) * 60, TimeUnit.SECONDS);
			tvSurplusDuration.setText("剩余时间：" + etNum.getText().toString() + "分钟");
		}
	}

	public void minuteDown(final long time, TimeUnit unit) {
		subscription = Observable.interval(0, 1, unit, Schedulers.trampoline())
				.compose(this.<Long>bindUntilEvent(ActivityEvent.DESTROY))
				.compose(SchedulersCompat.<Long>applyNewSchedulers())
				.subscribe(new SimpleSubscriber<Long>() {
					@Override
					public void onNext(Long aLong) {
						super.onNext(aLong);
						refreshTime((int) (time - aLong));
						if (time - aLong <= 0) {
							subscription.unsubscribe();
						}
					}
				});
	}

	public void refreshTime(long time) {
		if (currentMode == MODE_TIME) {
			tvSurplusTime.setText("剩余时间\n" + LvTimeUtil.minute2String(time));
		} else {
			tvSurplusDuration.setText("剩余时间：" + LvTimeUtil.second2string(time));
		}
	}

	/**
	 * 设置背景透明度
	 *
	 * @param alpha
	 */
	private void setBackgroundAlpha(float alpha) {
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.alpha = alpha;
		getWindow().setAttributes(params);
	}
}
