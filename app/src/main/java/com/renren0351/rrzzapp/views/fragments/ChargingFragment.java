package com.renren0351.rrzzapp.views.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.renren0351.rrzzapp.LvAppUtils;
import com.renren0351.rrzzapp.R;
import com.renren0351.rrzzapp.custom.ChargingDialog;
import com.renren0351.rrzzapp.custom.DonutProgress;
import com.renren0351.rrzzapp.event.StationStatusEvent;
import com.renren0351.rrzzapp.event.StopQueryStatusEvent;
import com.renren0351.rrzzapp.services.ServiceUtil;
import com.renren0351.rrzzapp.views.activities.MainActivity;
import com.renren0351.rrzzapp.views.activities.OrderSettingActivity;
import com.renren0351.rrzzapp.views.activities.SnInputActivity;
import com.renren0351.rrzzapp.views.base.LvBaseFragment;
import com.renren0351.rrzzapp.views.dialog.NiftyDialogBuilder;
import com.renren0351.rrzzapp.wigets.ProgressCircleView;
import com.renren0351.model.dagger.ApiComponentHolder;
import com.renren0351.model.response.ChargingResponse;
import com.renren0351.model.response.OrderResponse;
import com.renren0351.model.response.SimpleResponse;
import com.renren0351.model.response.StationStatusResponse;
import com.renren0351.model.storage.AppInfosPreferences;
import com.renren0351.presenter.appointment.QueryOrderContract;
import com.renren0351.presenter.appointment.QueryOrderPresenter;
import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.FragmentEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.leanvision.baseframe.log.DebugLog;
import cn.com.leanvision.baseframe.rx.RxBus;
import cn.com.leanvision.baseframe.rx.SimpleSubscriber;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import cn.com.leanvision.baseframe.util.LvTextUtil;
import cn.com.leanvision.baseframe.util.LvTimeUtil;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/********************************
 * Created by lvshicheng on 2017/2/13.
 * <p>
 * 首页 - 充电
 * 1表示充电，0表示不充电
 * 预约和启动充电是互斥事件
 ********************************/
public class ChargingFragment extends LvBaseFragment implements QueryOrderContract.View {
	@BindView(R.id.root_default)
	LinearLayout rootDefault;
	@BindView(R.id.root_charging)
	RelativeLayout rootCharging;
	@BindView(R.id.pcv)
	ProgressCircleView pcv;

	@BindView(R.id.tv_order)
	TextView tvOrder;   //预约状态
	@BindView(R.id.tv_time)
	TextView tvTime;    //预约时间
	@BindView(R.id.ll_order)
	LinearLayout llOrder;
	@BindView(R.id.tv_scan)
	TextView tvScan;
	@BindView(R.id.tv_input_sn)
	TextView tvInputSn;
	@BindView(R.id.tv_station_name)
	TextView tvStationName;
	@BindView(R.id.tv_status)
	TextView tvStatus;
	@BindView(R.id.tv_number)
	TextView tvNumber;
	@BindView(R.id.btn_stop)
	Button btnStop;
	@BindView(R.id.tv_charging_total)
	TextView tvChargingTotal;
	@BindView(R.id.tv_charging_time)
	TextView tvChargingTime;
	@BindView(R.id.tv_charging_pay)
	TextView tvChargingPay;
	@BindView(R.id.tv_charging_v)
	TextView tvChargingV;
	@BindView(R.id.tv_charging_a)
	TextView tvChargingA;
	@BindView(R.id.tv_charging_p)
	TextView tvChargingP;

	private static final String TAG = "TAG";
	private StationStatusResponse.StationStatus stationStatus;
	private QueryOrderPresenter queryOrderPresenter;
	private Subscription timeSubscription;	//倒计时轮询
	private Subscription orderSubscription;	//预约轮询
	private Subscription querySubscription;	//启动充电轮询
	private Subscription subscription;	//停止充电轮询
	private String time;
	private int currentMode;
	private OrderResponse.Order order;
	private boolean isFirst;
	private boolean isOrder;
	private Dialog dialog;
	private ChargingDialog       chargingDialog;

	public static ChargingFragment newInstance() {
		Bundle args = new Bundle();
		ChargingFragment fragment = new ChargingFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	protected View createView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.frgm_charging, container, false);
	}

	@Override
	protected void initPresenter() {
		super.initPresenter();
		queryOrderPresenter = new QueryOrderPresenter();
		queryOrderPresenter.attachView(this);
	}

	@Override
	protected void destroyPresenter() {
		super.destroyPresenter();
		queryOrderPresenter.detachView();
	}

	@Override
	protected void initView(@Nullable Bundle savedInstanceState) {
		super.initView(savedInstanceState);
		llOrder.setEnabled(false);

		//刷新充电数据，
		RxBus.getInstance()
				.toObservable(StationStatusEvent.class)
				.compose(this.<StationStatusEvent>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
				.compose(SchedulersCompat.<StationStatusEvent>observeOnMainThread())
				.subscribe(new SimpleSubscriber<StationStatusEvent>() {
					@Override
					public void onNext(StationStatusEvent stationStatusEvent) {
						if (stationStatusEvent.stationStatus == null) {
							//由于充电桩原因造成强制停止充电
							AppInfosPreferences.get().setCharging("0");
							refreshStatus();
							ServiceUtil.stopChargingQuery();
						} else {
							setStatusShow(stationStatusEvent.stationStatus);
						}
					}
				});
		//监听停止充电
		RxBus.getInstance()
				.toObservable(StopQueryStatusEvent.class)
				.compose(this.<StopQueryStatusEvent>bindUntilEvent(FragmentEvent.DESTROY))
				.compose(SchedulersCompat.<StopQueryStatusEvent>observeOnMainThread())
				.subscribe(new Action1<StopQueryStatusEvent>() {
					@Override
					public void call(StopQueryStatusEvent stopQueryStatusEvent) {
						AppInfosPreferences.get().setChargeStationName("");
						AppInfosPreferences.get().setCharging("0");
						//充电桩停止充电，删除充电状态
					//	deleteStatus();
						refreshStatus();
						ServiceUtil.stopChargingQuery();
					}
				});
	}

	/**
	 * fragment可见时回调
	 */
	@Override
	public void onSupportVisible() {
		refreshStatus();
		isFirst = true;
		isOrder = false;
		if (LvAppUtils.isLogin()) {
			//查询预约
			queryOrderPresenter.queryOrder();
		}else {
			llOrder.setEnabled(false);
			tvOrder.setText("没有预约");
			tvTime.setVisibility(View.INVISIBLE);
		}

	}

	/**
	 * fragment不可见时回调
	 */
	@Override
	public void onSupportInvisible() {
		ServiceUtil.stopChargingQuery();
		if (orderSubscription != null && !orderSubscription.isUnsubscribed()) {
			orderSubscription.unsubscribe();
		}
		if (timeSubscription != null && !timeSubscription.isUnsubscribed()) {
			timeSubscription.unsubscribe();
		}
	}

	public void refreshStatus() {
		//  这里要判断当前的状态来显示充电或者充电状态页面
		if (LvAppUtils.isCharging()) {
			rootDefault.setVisibility(View.GONE);
			rootCharging.setVisibility(View.VISIBLE);
			//启动成功后。关闭启动轮询
			closeSubscription();
			ServiceUtil.startChargingQuery();
		} else {
			rootDefault.setVisibility(View.VISIBLE);
			rootCharging.setVisibility(View.GONE);
		}
	}

	/**
	 * 刷新充电过程数据
	 *
	 * @param stationStatus
	 */
	public void setStatusShow(StationStatusResponse.StationStatus stationStatus) {
		this.stationStatus = stationStatus;
		int outV = Integer.valueOf(stationStatus.outV);
		int outA = Integer.valueOf(stationStatus.outA);
		float outV1 = Float.valueOf(stationStatus.outV);
		float outA1 = Float.valueOf(stationStatus.outA);
		//判断充电桩是否充电
		if (outA == 0 && outV == 0 && !"0003".equals(stationStatus.workstate)) {
			AppInfosPreferences.get().setChargeStationName("");
			AppInfosPreferences.get().setCharging("0");
			//deleteStatus();
			ServiceUtil.stopChargingQuery();
			refreshStatus();
		}
//        if ("0005".equals(stationStatus.workstate)){
//            AppInfosPreferences.get().setCharging("0");
//            ServiceUtil.stopChargingQuery();
//            refreshStatus();
//        }
		int soc = stationStatus.soc;
		pcv.setText(String.format("%d%%\n充电中", soc));
		pcv.setProgress(soc / 100.0f);

		tvStationName.setText(AppInfosPreferences.get().getChargeStationName());
		tvNumber.setText(String.format("终端编号 %s", stationStatus.cpId));

		try {
			Float electric = Integer.parseInt(stationStatus.electric) / 100f;
			tvChargingTotal.setText(String.format("%.1f 度", electric)); // 已充电
		} catch (Exception ignored) {

		}

		long totalTime = stationStatus.totalTime;// 单位：分钟
		long hour = totalTime / 60;
		long minute = totalTime % 60;
		if (hour <= 0) {
			tvChargingTime.setText(String.format("%s 分钟", minute)); // 已充时长
		} else {
			tvChargingTime.setText(String.format("%s 小时 %s 分钟", hour, minute)); // 已充时长
		}

		float fee = Float.valueOf(stationStatus.serviceFee);
		tvChargingPay.setText(String.format("%.2f 元", fee / 100)); // 充电费用

		tvChargingV.setText(String.format("%.2f V", outV1 / 10)); // 充电电压
		tvChargingA.setText(String.format("%.2f A", outA1 / 10)); // 充电电流
		//tvChargingP.setText(stationStatus.getPower()); // 充电功率
		tvChargingP.setText(String.format("%.2f KW", outV1 / 10 * outA1 / 10 / 1000));
	}

	/**
	 * 判断当前用户是否正在使用APP充电
	 */
	private void queryStatus() {
		ApiComponentHolder.sApiComponent
				.apiService()
				.getStationStatus()
				.take(1)
				.compose(this.<StationStatusResponse>bindUntilEvent(FragmentEvent.DESTROY))
				.compose(SchedulersCompat.<StationStatusResponse>applyNewSchedulers())
				.subscribe(new SimpleSubscriber<StationStatusResponse>() {
					@Override
					public void onError(Throwable e) {
						showToast("网络异常");
					}

					@Override
					public void onNext(StationStatusResponse stationStatusResponse) {
						if (stationStatusResponse.isSuccess()) {
							if (stationStatusResponse.contentList != null && stationStatusResponse.contentList.size() > 0
									&& "0003".equals(stationStatusResponse.contentList.get(0).workstate)) {
								AppInfosPreferences.get().setCharging("1");
							} else {
								AppInfosPreferences.get().setCharging("0");
							}
							refreshStatus();
						} else {
							showToast(stationStatusResponse.msg);
						}
					}
				});
	}

	/**
	 * 判断充电桩是否停止充电
	 * 根据状态位判断 0003-->工作
	 */
	private void stopStatus() {
		Log.i(TAG, "stopStatus: running");
		HashMap<String, Object> map = new HashMap<>();
		map.put("cpId", stationStatus.cpId);
		map.put("cpinterfaceId", stationStatus.cpinterfaceId);
		ApiComponentHolder.sApiComponent
				.apiService()
				.getOtherStationStatus(map)
				.take(1)
				.compose(this.<StationStatusResponse>bindUntilEvent(FragmentEvent.DESTROY))
				.compose(SchedulersCompat.<StationStatusResponse>applyNewSchedulers())
				.subscribe(new SimpleSubscriber<StationStatusResponse>() {
					@Override
					public void onError(Throwable e) {
						closeSubscription();
						showNormal();
						showToast("网络异常");
					}

					@Override
					public void onNext(StationStatusResponse stationStatusResponse) {
						if (stationStatusResponse.isSuccess()) {
							if (stationStatusResponse.contentList != null && stationStatusResponse.contentList.size() > 0) {
								if (!"0003".equals(stationStatusResponse.contentList.get(0).workstate)) {//非工作状态
									Log.i(TAG, "onNext: //非工作状态");
									showNormal();
									//app停止充电 删除状态
									//deleteStatus();
									closeSubscription();
									//设置为停止状态
									closeChargingDialog();
									AppInfosPreferences.get().setCharging("0");
									refreshStatus();
									Toast.makeText(getActivity(), "桩停止后，请拔枪，否则会影响您下次正常充电", Toast.LENGTH_LONG).show();
								}
							} else {//没有充电数据
								Log.i(TAG, "onNext: 没有充电数据");
								showNormal();
								closeSubscription();
								closeChargingDialog();
								AppInfosPreferences.get().setCharging("0");
								refreshStatus();
							}
						} else {//出现错误
							showNormal();
							closeChargingDialog();
							showToast(stationStatusResponse.msg);
						}
					}
				});

	}

	/**
	 * 停止充电
	 */
	private void stopCharging() {
		//停止刷新数据
		ServiceUtil.stopChargingQuery();
		//发送停止充电请求
		final HashMap<String, Object> request = new HashMap<>();
		request.put("cpId", stationStatus.cpId);
		request.put("cpinterfaceId", stationStatus.cpinterfaceId);
		ApiComponentHolder.sApiComponent
				.apiService()
				.stopCharging(request)
				.take(1)
				.compose(SchedulersCompat.<ChargingResponse>applyNewSchedulers())
				.subscribe(new SimpleSubscriber<ChargingResponse>() {
					@Override
					public void onNext(ChargingResponse response) {
						DebugLog.log("----------------------->" + response.charging.res);
						if ("0".equals(response.charging.res)) {	//开始轮询
							//showLoading("正在停止...");
							showChargingDialog();
							subscription = Observable.interval(0, 3, TimeUnit.SECONDS, Schedulers.trampoline())
									.compose(SchedulersCompat.<Long>applyNewSchedulers())
									.subscribe(new SimpleSubscriber<Long>() {
										@Override
										public void onNext(Long aLong) {
											super.onNext(aLong);
											stopStatus();
											if (aLong == 10) {
												closeSubscription();
												showNormal();
												showToast("APP无法停止充电，请手动停止充电");
											}
										}
									});
						}
					}
				});
	}

	//倒计时（预约充电）
	public void minuteDown(final long second) {
		timeSubscription = Observable.interval(0, 1, TimeUnit.SECONDS, Schedulers.trampoline())
				.compose(this.<Long>bindUntilEvent(FragmentEvent.STOP)) //这里是stop
				.compose(SchedulersCompat.<Long>applyNewSchedulers())
				.subscribe(new SimpleSubscriber<Long>() {
					@Override
					public void onNext(Long aLong) {
						super.onNext(aLong);
						long interval = second - aLong;
						time = interval + "";
						tvTime.setText(LvTimeUtil.second2string(interval));
						if (interval <= 0) {
							timeSubscription.unsubscribe();
						}
					}
				});
	}


	/**
	 * -----------------
	 * 页面点击事件处理
	 * -----------------
	 */
	//预约
	@OnClick(R.id.ll_order)
	public void clickOrder() {
		OrderSettingActivity.navigation(order.substationId, order.cpId, Integer.parseInt(order.cpinterfaceId),
				order.orderId, true, currentMode, time);
	}

	//扫码
	@OnClick(R.id.tv_scan)
	public void clickScan() {
		((MainActivity) _mActivity).turnToScan();
	}

	//输入SN
	@OnClick(R.id.tv_input_sn)
	public void clickInputSn() {
		SnInputActivity.navigation();
	}

	//停止充电
	@OnClick(R.id.btn_stop)
	public void clickStopCharging() {
//		stopCharging();
		showOk();
	}

	/**
	 * 确认停止充电Dialog
	 */
	private void showOk(){
		final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(getContext());
		builder.withTitle("提示")
				.withMessage("您确认要停止充电吗？")
				.setButtonCancelClick(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						builder.dismiss();
					}
				})
				.setButtonOkClick(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						stopCharging();
						builder.dismiss();
					}
				})
				.show();
	}

	@Override
	public void showLoading(String msg) {;
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

	/**
	 * 有预约回调方法
	 * @param order
	 */
	@Override
	public void querySuccess(OrderResponse.Order order) {
		this.order = order;
		if (order.status == 1){//取消预约
			closeSubscription();
		}
		if (order.status == 2){//预约启动
			closeSubscription();
			if (!LvTextUtil.isEmpty(order.appointTime)){
				showToast("定时充电启动成功");
			}
			isOrder = false;
			queryStatus();
		}
		//如果是第一次，启动预约轮询，刷新数据
		if (isFirst) {
			isOrder = true;
			orderSubscription = Observable.interval(0, 5, TimeUnit.SECONDS, Schedulers.trampoline())
					.compose(this.<Long>bindUntilEvent(FragmentEvent.DESTROY))
					.compose(SchedulersCompat.<Long>applyNewSchedulers())
					.subscribe(new Action1<Long>() {
						@Override
						public void call(Long aLong) {
//							Log.i(TAG, "call: 查询预约轮询" + aLong);
							queryOrderPresenter.queryOrder();
						}
					});
			isFirst = false;
			llOrder.setEnabled(true);
			tvTime.setVisibility(View.VISIBLE);
			if (!LvTextUtil.isEmpty(order.appointTime)) {      //定时充电模式
				currentMode = OrderSettingActivity.MODE_TIME;
				tvOrder.setText("定时充电");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					Date date = sdf.parse(order.appointTime);
					SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
					tvTime.setText(sdf2.format(date));
					time = order.appointTime;
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else {                                          //预约充电模式
				currentMode = OrderSettingActivity.MODE_DURATION;
				tvOrder.setText("预约充电");
//				time = order.duration;
//				tvTime.setText(order.duration + "分钟");
				minuteDown(order.available_time);
			}
		}
	}

	/**
	 * 没有预约回调方法
	 * @param msg
	 */
	@Override
	public void noOrder(String msg) {
		llOrder.setEnabled(false);
		tvOrder.setText("没有预约");
		tvTime.setVisibility(View.INVISIBLE);
		//关闭未完成预约轮询
		closeSubscription();
		//预约取消后，充电桩处于启动中，进行状态轮询
		if (isOrder){
			isOrder = false;
			querySubscription = Observable.interval(0, 5, TimeUnit.SECONDS, Schedulers.trampoline())
					.compose(this.<Long>bindUntilEvent(FragmentEvent.PAUSE))
					.compose(SchedulersCompat.<Long>applyNewSchedulers())
					.subscribe(new Action1<Long>() {
						@Override
						public void call(Long aLong) {
//							Log.i(TAG, "call: 查询状态轮询" + aLong);
							queryStatus();
							if (aLong == 24){
//								Log.i(TAG, "call: 结束状态轮询" + aLong);
								querySubscription.unsubscribe();
							}
						}
					});
		}else {//查询当前是否处于充电状态
			queryStatus();
		}

	}

	private void closeSubscription() {
		if (subscription != null && !subscription.isUnsubscribed()) {
			subscription.unsubscribe();
		}
		if (orderSubscription != null && !orderSubscription.isUnsubscribed()) {
			orderSubscription.unsubscribe();
		}
		if (timeSubscription != null && !timeSubscription.isUnsubscribed()) {
			timeSubscription.unsubscribe();
		}
		if (querySubscription != null && !querySubscription.isUnsubscribed()) {
			querySubscription.unsubscribe();
		}
	}

	/**
	 * 去除充电状态
	 */
	private void deleteStatus(){
		if (stationStatus != null) {
			HashMap<String,String> map = new HashMap<>();
			map.put("cpId", stationStatus.cpId);
			map.put("cpinterfaceId", stationStatus.cpinterfaceId);
			ApiComponentHolder.sApiComponent
					.apiService()
					.statusDelete(map)
					.take(1)
					.compose(this.<SimpleResponse>bindUntilEvent(FragmentEvent.DESTROY))
					.compose(SchedulersCompat.<SimpleResponse>applyNewSchedulers())
					.subscribe(new SimpleSubscriber<SimpleResponse>() {
						@Override
						public void onNext(SimpleResponse simpleResponse) {
							super.onNext(simpleResponse);
						}
					});
		}

	}

	/**
	 * 充电桩启动充电加载Dialog
	 */
	private void showChargingDialog(){
		if (dialog == null) {
			chargingDialog = new ChargingDialog();
			dialog = chargingDialog.createDialog(getActivity(),"充电桩正在停止中...");
			chargingDialog.getDonutProgress().setOnTimeFinishedListener(new DonutProgress.OnTimeFinishedListener() {
				@Override
				public void onFinished() {
					closeChargingDialog();
					closeSubscription();
					showToast("充电桩停止失败");
				}
			});
			dialog.setCancelable(false);
			dialog.show();
		}else if ( !dialog.isShowing()){
			dialog.show();
		}
	}

	/**
	 * 关闭启动充电Dialog
	 */
	public void closeChargingDialog(){
		if (dialog != null && dialog.isShowing()){
			chargingDialog.close();
			chargingDialog = null;
			dialog.dismiss();
			dialog = null;
		}
	}
}
