package com.renren0351.rrzzapp.views.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.renren0351.rrzzapp.LvAppUtils;
import com.renren0351.rrzzapp.LvApplication;
import com.renren0351.rrzzapp.R;
import com.renren0351.rrzzapp.custom.toast.IToast;
import com.renren0351.rrzzapp.custom.toast.ToastUtils;
import com.renren0351.rrzzapp.utils.MyItemDecoration;
import com.renren0351.rrzzapp.views.base.LvBaseAppCompatActivity;
import com.renren0351.model.bean.StationDetailBean;
import com.renren0351.model.response.OrderResponse;
import com.renren0351.presenter.appointment.QueryOrderContract;
import com.renren0351.presenter.appointment.QueryOrderPresenter;
import com.renren0351.presenter.main.StationDetailContract;
import com.renren0351.presenter.main.StationDetailPresenter;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.leanvision.baseframe.log.DebugLog;
import cn.com.leanvision.baseframe.util.LvTextUtil;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/09/14
 *     desc   :
 *     version: 1.0
 * </pre>
 */
@Route(path = "/main/pile_list")
public class PileListActivity extends LvBaseAppCompatActivity implements StationDetailContract.View, QueryOrderContract.View {

	@BindView(R.id.m_recycler_view)
	RecyclerView mRecyclerView;

	private String                 	stationId;
	private String 				   	cpId;
	private int 					gunId;
	private String					state;
	private String					stationName;
	private StationDetailPresenter 	presenter;
	private QueryOrderPresenter    	queryOrderPresenter;

	private UMShareListener        	mShareListener;
	private ShareAction 			mShareAction;
	private PopupWindow 			popWindow;
	private StationDetailBean 		detailBean;

	public static void navigation(String stationId, String stationName, double lat, double lng) {
		ARouter.getInstance().build("/main/pile_list")
				.withString("stationId", stationId)
				.withString("stationName", stationName)
				.withDouble("lat", lat)
				.withDouble("lng", lng)
				.navigation();
	}

	@Override
	protected void setContentView(Bundle savedInstanceState) {
		setContentView(R.layout.aty_station_detail);
	}

	@Override
	protected void initPresenter() {
		presenter = new StationDetailPresenter();
		presenter.attachView(this);
		queryOrderPresenter = new QueryOrderPresenter();
		queryOrderPresenter.attachView(this);
	}

	@Override
	protected void destroyPresenter() {
		presenter.detachView();
		queryOrderPresenter.detachView();
	}

	@Override
	protected void initView() {
		initToolbarNav("充电站详情");
		initPopWindow();
		stationId = getIntent().getStringExtra("stationId");
		stationName = getIntent().getStringExtra("stationName");
		presenter.getSubstationSummary(stationId);

		initShare();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		presenter.getSubstationSummary(stationId);
	}

	/**
	 * 初始化底部弹窗
	 */
	private void initPopWindow() {
		View popView = LayoutInflater.from(this).inflate(R.layout.view_pop_select, null);
		TextView tvOrder = (TextView) popView.findViewById(R.id.tv_one);
		tvOrder.setText("我要预约");
		TextView tvScan = (TextView) popView.findViewById(R.id.tv_two);
		tvScan.setText("查看充电");
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
				setBackgroundAlpha(1);
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
				case R.id.tv_one:     //跳转预约界面
					if (LvAppUtils.isLogin()) {
						queryOrderPresenter.queryOrder();
					} else {
						LoginActivity.navigation(false);
					}
					break;
				case R.id.tv_two://跳转查看充电界面
					//判断是否充电
					if ("工作".equals(state)){
						TempChargingActivity.navigation(stationId, stationName, cpId, gunId);
					}else {
						showToast("充电枪没有充电");
					}
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
	 * 初始化分享
	 */
	private void initShare() {
		mShareListener = new PileListActivity.CustomShareListener(this);
		mShareAction = new ShareAction(this).setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
				.setShareboardclickCallback(new ShareBoardlistener() {
					@Override
					public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
						switch (snsPlatform.mShowWord) {
							case "umeng_sharebutton_copy":
								showToast("复制文本按钮");
								break;
							case "umeng_sharebutton_copyurl":
								showToast("复制文本按钮");
								break;
							default:
								// FIXME: 2017/7/26 需要修改分享的网址
//                            String url = "https://mobile.umeng.com/";
								String url = "http://117.78.40.137/cs-cloud-demo/loadapp/loadapp.html";

								UMWeb web = new UMWeb(url);
								web.setTitle("欢迎使用人人桩主APP");
								web.setDescription("绿色,节能,低碳,畅享健康生活，国晶售电有限公司欢迎您!");
								web.setThumb(new UMImage(PileListActivity.this, R.mipmap.ic_launcher));
								new ShareAction(PileListActivity.this).withMedia(web)
										.setPlatform(share_media)
										.setCallback(mShareListener)
										.share();
								break;
						}
					}
				});
	}

	@OnClick(R.id.iv_share)
	public void clickShare() {
		mShareAction.open();
	}

	@OnClick(R.id.iv_error)
	public void clickErrorCorrection() {
		showErrorCorrectionDialog();
	}


	public static String[] items
			= {"地理位置错误", "位置描述错误", "停车费信息错误", "终端无法充电", "其他"};

	private void showErrorCorrectionDialog() {
		new AlertDialog.Builder(this)
				.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						DebugLog.log("which: %d", which);
						if (which == 0) {
							double lat = getIntent().getDoubleExtra("lat", 0.0f);
							double lng = getIntent().getDoubleExtra("lng", 0.0f);
							ErrorCorrectionLocationActivity.navigation(lat, lng, stationId);
						} else {
							ErrorCorrectionActivity.navigation(which, stationId);
						}
					}
				})
				.show();
	}

	@Override
	public void showLoading(String msg) {
		showLoadingDialog(msg);
	}

	@Override
	public void showNormal() {
		dismissLoadingDialog();
	}

	@Override
	public void requestFailed(String msg) {
		if (LvTextUtil.isEmpty(msg)) {
			showToast(R.string.network_not_available);
		} else {
			showToast(msg);
		}
	}

	@Override
	public void getSummarySuccess(StationDetailBean stationDetailBean) {
		detailBean = stationDetailBean;
		initRecyclerView(stationDetailBean);
	}

	private void initRecyclerView(final StationDetailBean stationDetailBean) {
		if (mRecyclerView.getAdapter() == null) {
			RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
			mRecyclerView.setLayoutManager(lm);
			mRecyclerView.setHasFixedSize(true);
			mRecyclerView.addItemDecoration(new MyItemDecoration(this, MyItemDecoration.HORIZONTAL));

			OnGunClickListener listener = new OnGunClickListener() {
				@Override
				public void onClick(String cpId, int gunId, String state) {
					if ("离线".equals(state)){
						showToast("充电桩离线了");
					}else {
						PileListActivity.this.cpId = cpId;
						PileListActivity.this.gunId = gunId;
						PileListActivity.this.state = state;
						showPopWindow();
					}
				}
			};
            //列表按编号排序
            Collections.sort(stationDetailBean.chargingPileList);
			PileListActivity.Adapter adapter = new PileListActivity.Adapter(this,stationDetailBean.chargingPileList, listener);
			mRecyclerView.setAdapter(adapter);
		} else {
            //列表按编号排序
            Collections.sort(stationDetailBean.chargingPileList);
			PileListActivity.Adapter adapter = (PileListActivity.Adapter) mRecyclerView.getAdapter();
			adapter.setDataList(stationDetailBean.chargingPileList);
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void querySuccess(OrderResponse.Order order) {
		showToast("您当前有一个预约");
	}

	@Override
	public void noOrder(String msg) {
        //列表按编号排序
        Collections.sort(detailBean.chargingPileList);
		if (detailBean != null && detailBean.chargingPileList != null) {
			OrderSettingActivity.navigation(stationId, cpId, gunId,
					null, false, 0, null);
		}
	}

	static class Adapter extends RecyclerView.Adapter<PileListActivity.Adapter.ViewHolder> {
		private OnGunClickListener listener;
		private Context context;
		private int count;

		private List<StationDetailBean.ChargingPileListEntity> dataList;

		Adapter(Context context,List<StationDetailBean.ChargingPileListEntity> dataList,
				OnGunClickListener listener) {
			this.context = context;
			this.dataList = dataList;
			this.listener = listener;
		}

		public void setDataList(List<StationDetailBean.ChargingPileListEntity> chargingPileList) {
			dataList = chargingPileList;
		}


		private Drawable getStateDrawable(StationDetailBean.ChargingPileListEntity cpe, int gunId){
			String state = cpe.getGunState(gunId);
			Drawable icon;
			if (state.contains("工作")){
				icon = context.getResources().getDrawable(R.drawable.state_working);
			}else if (state.contains("告警")){
				icon = context.getResources().getDrawable(R.drawable.state_warning);
			}else if (state.contains("完成") || state.contains("待机")){
				icon = context.getResources().getDrawable(R.drawable.state_idle);
			}else {		//离线
				icon = context.getResources().getDrawable(R.drawable.state_offline);
			}
			icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
			return icon;
		}

		@Override
		public PileListActivity.Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pile, parent, false);
			return new PileListActivity.Adapter.ViewHolder(view);
		}

		@Override
		public void onBindViewHolder(PileListActivity.Adapter.ViewHolder holder, int position) {
			final StationDetailBean.ChargingPileListEntity cpe = dataList.get(position);
			holder.shortCode.setText(cpe.getShortCode());
			holder.longCode.setText(cpe.cpId);
			holder.pileType.setText(cpe.getCpType());
			//额定功率
			if (!LvTextUtil.isEmpty(cpe.ratedPower)){
				holder.pilePower.setText(cpe.ratedPower);
			}else {
				holder.pilePower.setText(cpe.getPower());
			}

			holder.pileState.setText(cpe.getGunState(1));
			holder.itemView.setTag(cpe);

			// 以下是显示枪的逻辑
			int totalGuns = Integer.parseInt(cpe.cpinterfaceId);
//			int totalGuns = 6;
			if (totalGuns < 3 ) {
				holder.tvGun1.setText(" 枪1 ");
				holder.tvGun1.setCompoundDrawables(getStateDrawable(cpe, 1), null, null, null);
				holder.llMin.setVisibility(View.VISIBLE);
				holder.gridLayout.setVisibility(View.GONE);
				holder.tvGun2.setVisibility(View.GONE);
				holder.tvGun1.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (listener != null) {
							listener.onClick(cpe.cpId, 1, cpe.getGunState(1));
						}
					}
				});
				if (totalGuns == 2){
					holder.tvGun2.setVisibility(View.VISIBLE);
					holder.tvGun2.setText(" 枪2 ");
					holder.tvGun2.setCompoundDrawables(getStateDrawable(cpe, 2), null, null, null);
					holder.tvGun2.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							if (listener != null) {
								listener.onClick(cpe.cpId, 2, cpe.getGunState(2));
							}
						}
					});
				}
			}else {
				holder.gridLayout.removeAllViews();
				holder.llMin.setVisibility(View.GONE);
				holder.gridLayout.setVisibility(View.VISIBLE);
				int row = totalGuns / 4 == 0 ? totalGuns / 4 : totalGuns / 4 + 1;
				holder.gridLayout.setRowCount(row);
				View v ;
				TextView tvGun;
				count = 0;
				for (int i = 0; i < totalGuns; i++) {
					count = i + 1;
					v = LayoutInflater.from(context).inflate(R.layout.item_gun, null, false);
					tvGun = (TextView) v.findViewById(R.id.tv_gun);
					tvGun.setText(String.format(" 枪%d ", count));
					tvGun.setCompoundDrawables(getStateDrawable(cpe, count), null, null, null);
					holder.gridLayout.addView(v);
				}
				for (int i = 0; i < totalGuns; i++) {
					count = i + 1;
					v = holder.gridLayout.getChildAt(i);
					v.setTag(count);
					v.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							if (listener != null) {
								count = (int) v.getTag();
								listener.onClick(cpe.cpId, count, cpe.getGunState(count));
								System.out.print(v);
								DebugLog.log("------------------------" + cpe.cpId);
								DebugLog.log("------------------------" + count);
							}
						}
					});
				}
			}
		}

		@Override
		public int getItemCount() {
			return dataList == null ? 0 : dataList.size();
		}

		public class ViewHolder extends RecyclerView.ViewHolder {

			@BindView(R.id.short_code)
			TextView      shortCode;
			@BindView(R.id.long_code)
			TextView      longCode;
			@BindView(R.id.pile_type)
			TextView      pileType;
			@BindView(R.id.pile_power)
			TextView      pilePower;
			@BindView(R.id.pile_state)
			TextView      pileState;
			@BindView(R.id.tv_gun1)
			TextView	  tvGun1;
			@BindView(R.id.tv_gun2)
			TextView	  tvGun2;
			@BindView(R.id.ll_min)
			LinearLayout  llMin;
			@BindView(R.id.pile_grid)
			GridLayout gridLayout;

			public ViewHolder(View itemView) {
				super(itemView);
				ButterKnife.bind(this, itemView);
			}
		}
	}

	public interface OnGunClickListener{
		void onClick(String cpId, int gunId, String state);
	}

	//分享监听
	private static class CustomShareListener implements UMShareListener {
		private WeakReference<Activity> mActivity;

		private CustomShareListener(Activity activity) {
			mActivity = new WeakReference<>(activity);
		}

		@Override
		public void onStart(SHARE_MEDIA share_media) {
		}

		@Override
		public void onResult(SHARE_MEDIA platform) {
			ToastUtils.getInstance(LvApplication.getContext()).makeTextShow(platform + " 分享成功啦", IToast.LENGTH_SHORT);
//            ToastUtils.makeText(mActivity.get(), platform + " 分享成功啦", CustomToast.LENGTH_SHORT).show();
		}

		@Override
		public void onError(SHARE_MEDIA platform, Throwable t) {
			ToastUtils.getInstance(LvApplication.getContext()).makeTextShow(platform + " 分享失败啦", IToast.LENGTH_SHORT);
//            ToastUtils.makeText(mActivity.get(), platform + " 分享失败啦", CustomToast.LENGTH_SHORT).show();
			if (t != null) {
				DebugLog.log("throw:" + t.getMessage());
			}
		}

		@Override
		public void onCancel(SHARE_MEDIA platform) {
			ToastUtils.getInstance(LvApplication.getContext()).makeTextShow(platform + " 分享取消了", IToast.LENGTH_SHORT);
//            ToastUtils.makeText(mActivity.get(), platform + " 分享取消了", CustomToast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 设置背景透明度
	 * @param alpha
	 */
	private void setBackgroundAlpha(float alpha){
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.alpha = alpha;
		getWindow().setAttributes(params);
	}
}
