package com.renren0351.rrzzapp.views.fragments;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;
import com.renren0351.rrzzapp.LvAppUtils;
import com.renren0351.rrzzapp.R;
import com.renren0351.rrzzapp.event.ShowNavListEvent;
import com.renren0351.rrzzapp.utils.FeeUtils;
import com.renren0351.rrzzapp.views.activities.FeeActivity;
import com.renren0351.rrzzapp.views.activities.LoginActivity;
import com.renren0351.rrzzapp.views.activities.PileListActivity;
import com.renren0351.rrzzapp.views.base.LvBaseFragment;
import com.renren0351.model.LvRepository;
import com.renren0351.model.bean.SubstationBean;
import com.renren0351.model.dagger.ApiComponentHolder;
import com.renren0351.model.response.FeeResponse;
import com.renren0351.model.response.SimpleResponse;
import com.renren0351.presenter.usercase.FavorCase;
import com.renren0351.presenter.usercase.UnFavorCase;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.leanvision.baseframe.rx.RxBus;
import cn.com.leanvision.baseframe.rx.SimpleSubscriber;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import cn.com.leanvision.baseframe.util.LvTextUtil;
import rx.functions.Action1;

/********************************
 * Created by lvshicheng on 2017/2/24.
 * modify by 赵成虎 on 2018/5/4
 ********************************/
public class OrderListFragment extends LvBaseFragment {

	@BindView(R.id.recycler_view)
	RecyclerView recyclerView;

	public static OrderListFragment newInstance() {

		Bundle args = new Bundle();

		OrderListFragment fragment = new OrderListFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	protected View createView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fgmt_order_list, container, false);
	}

	@Override
	public void onLazyInitView(@Nullable Bundle savedInstanceState) {
		super.onLazyInitView(savedInstanceState);
		initRecyclerView();
	}

	public boolean needRefresh;

	@Override
	public void onSupportVisible() {
		if (needRefresh) {
			refreshSubstations();
			needRefresh = false;
		} else {
			notifyRecyclerViewDataChanged();
		}
	}

	public void refreshSubstations() {
		if (!LvRepository.getInstance().isSortedByDistance()) {
			showLoadingDialog();
		}

		LvRepository.getInstance().getSubstations()
				.toList()
				.doOnNext(new Action1<List<SubstationBean>>() {
					@Override
					public void call(List<SubstationBean> substations) {
						if (!LvTextUtil.isArrayEmpty(substations)
								|| !LvRepository.getInstance().isSortedByDistance()) {
							if (OrderMapFragment.aMapLocation != null) {

								final LatLng location = new LatLng(
										OrderMapFragment.aMapLocation.getLatitude(),
										OrderMapFragment.aMapLocation.getLongitude());

								// 排序
								Collections.sort(substations, new Comparator<SubstationBean>() {
									@Override
									public int compare(SubstationBean lhs, SubstationBean rhs) {
										LatLng ldest = new LatLng(lhs.getLat(), lhs.getLng());
										float ldist = AMapUtils.calculateLineDistance(ldest, location);
										lhs.setDistance(ldist);
										LatLng rdest = new LatLng(rhs.getLat(), rhs.getLng());
										float rdist = AMapUtils.calculateLineDistance(rdest, location);
										rhs.setDistance(rdist);
										return (int) (ldist - rdist);
									}
								});
								LvRepository.getInstance().sortByDistanceEnd();
							}
						}
					}
				}).compose(SchedulersCompat.<List<SubstationBean>>applyNewSchedulers())
				.subscribe(new SimpleSubscriber<List<SubstationBean>>() {
					@Override
					public void onNext(List<SubstationBean> substations) {
						OrderListAdapter adapter = (OrderListAdapter) recyclerView.getAdapter();
						adapter.setSubstations(substations);
						adapter.notifyDataSetChanged();
					}

					@Override
					public void onError(Throwable e) {
						dismissLoadingDialog();
					}

					@Override
					public void onCompleted() {
						dismissLoadingDialog();
					}
				});
	}

	private void initRecyclerView() {
		//收藏监听
		View.OnClickListener clickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clickFavor((SubstationBean) v.getTag());
			}
		};

		LinearLayoutManager lm = new LinearLayoutManager(_mActivity.getApplicationContext());
		recyclerView.setLayoutManager(lm);
		recyclerView.setHasFixedSize(true);
		OrderListAdapter adapter = new OrderListAdapter(clickListener);
		recyclerView.setAdapter(adapter);

		DividerItemDecoration decoration = new DividerItemDecoration(_mActivity.getApplicationContext(), DividerItemDecoration.VERTICAL);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			decoration.setDrawable(getResources().getDrawable(R.drawable.item_divider, _mActivity.getTheme()));
		} else {
			decoration.setDrawable(getResources().getDrawable(R.drawable.item_divider));
		}
		recyclerView.addItemDecoration(decoration);
	}

	/**
	 * ------------------------
	 * 收藏和取消收藏
	 * ------------------------
	 */
	FavorCase favorCase;
	UnFavorCase unFavorCase;

	/**
	 * 处理收藏事件
	 * @param bean 电站信息
	 */
	public void clickFavor(SubstationBean bean) {
		if (!LvAppUtils.isLogin()) {
			LoginActivity.navigation(false);
		} else {
			if (bean.isFavorites) { // 已收藏
				clearFavor(bean);
			} else { //收藏电站
				saveFavor(bean);
			}
		}
	}

	/**
	 * 发送收藏网络请求
	 * @param bean 电站信息
	 */
	private void saveFavor(final SubstationBean bean) {
		if (favorCase == null) {
			favorCase = new FavorCase();
		}
		showLoadingDialog();
		favorCase.params(bean.areaId)
				.createObservable(new SimpleSubscriber<SimpleResponse>() {
					@Override
					public void onError(Throwable e) {
						super.onError(e);
						dismissLoadingDialog();
						showToast(getString(R.string.network_not_available));
					}

					@Override
					public void onNext(SimpleResponse simpleResponse) {
						if (simpleResponse.isSuccess()) {
							bean.isFavorites = true;
							notifyRecyclerViewDataChanged();
						} else {
							showToast(simpleResponse.msg);
						}
					}

					@Override
					public void onCompleted() {
						dismissLoadingDialog();
					}
				});
	}

	/**
	 * 发送取消收藏网络请求
	 * @param bean 电站信息
	 */
	private void clearFavor(final SubstationBean bean) {
		if (unFavorCase == null) {
			unFavorCase = new UnFavorCase();
		}
		showLoadingDialog();
		unFavorCase.params(bean.areaId)
				.createObservable(new SimpleSubscriber<SimpleResponse>() {
					@Override
					public void onError(Throwable e) {
						super.onError(e);
						dismissLoadingDialog();
						showToast(getString(R.string.network_not_available));
					}

					@Override
					public void onNext(SimpleResponse response) {
						if (response.isSuccess()) {
							bean.isFavorites = false;
							notifyRecyclerViewDataChanged();
						} else {
							showToast(response.msg);
						}
					}

					@Override
					public void onCompleted() {
						dismissLoadingDialog();
					}
				});
	}

	private void notifyRecyclerViewDataChanged() {
		OrderListAdapter adapter = (OrderListAdapter) recyclerView.getAdapter();
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}

	public static class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {

		private final View.OnClickListener listener;
		private List<SubstationBean> substations;

		public OrderListAdapter(View.OnClickListener clickListener) {
			this.listener = clickListener;
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_station, parent, false);
			return new ViewHolder(view);
		}

		@Override
		public void onBindViewHolder(final ViewHolder holder, int position) {
			final SubstationBean substationBean = substations.get(position);
//      Log.i("TAG", "onBindViewHolder: " + substationBean.name + " " + substationBean.isFavorites);
			//加载费用信息，费用信息是实时数据
			/*ApiComponentHolder.sApiComponent.apiService()
					.queryFees(substationBean.areaId)
					.take(1)
					.compose(SchedulersCompat.<FeeResponse>applyNewSchedulers())
					.subscribe(new SimpleSubscriber<FeeResponse>() {
						@Override
						public void onError(Throwable e) {
						}

						@Override
						public void onNext(FeeResponse feeResponse) {
							if (feeResponse.isSuccess()) {
								float[] fee = FeeUtils.getCurrentFree(feeResponse.list);
//                      holder.tvService.setText(String.format("充电费：%.2f 元/度\r\n服务费：%.2f 元/度\r\n停车费：无", fee[0],fee[1]));
								holder.tvE.setText(String.format("充电费：%.2f 元/度", fee[0]));
								holder.tvS.setText(String.format("服务费：%.2f 元/度", fee[1]));
								holder.tvP.setText("停车费：无");

							} else {
//                    ToastUtils.makeText(holder.itemView.getContext(),feeResponse.msg, CustomToast.LENGTH_SHORT).show();
							}
						}
					});*/

			//电价显示
			holder.tvE.setText(substationBean.chargingFee);
			holder.tvE.setSelected(true);
			holder.tvS.setText(substationBean.serviceFee);
			holder.tvS.setSelected(true);
			holder.tvP.setText(substationBean.stopFee);
			holder.tvP.setSelected(true);
			//电站名
			holder.colItemStation.setText(substationBean.areaName);
			//这里设置是为了实现跑马灯效果
			holder.colItemStation.setSelected(true);
			//电站地址
			holder.colItemAddress.setText("地址：" + substationBean.address);
			holder.colItemTime.setText("运营时间：" + substationBean.serviceTime);
			if (OrderMapFragment.aMapLocation != null && LvTextUtil.isEmpty(substationBean.distance)) {
				LatLng dest = new LatLng(substationBean.getLat(), substationBean.getLng());
				LatLng location = new LatLng(OrderMapFragment.aMapLocation.getLatitude(), OrderMapFragment.aMapLocation.getLongitude());
				substationBean.setDistance(AMapUtils.calculateLineDistance(dest, location));
			}
			//距离
			holder.colItemDistance.setText(substationBean.distance);

			holder.tvAc.setVisibility(substationBean.hasTotalAC > 0 ? View.VISIBLE : View.GONE);
			holder.tvDc.setVisibility(substationBean.hasTotalDC > 0 ? View.VISIBLE : View.GONE);
			holder.tvAc.setText(String.format("空闲 %d /共 %d",
					substationBean.hasRestAC, substationBean.hasTotalAC));
			holder.tvDc.setText(String.format("空闲 %d /共 %d",
					substationBean.hasRestDC, substationBean.hasTotalDC));

//      holder.tvPayType.setText(String.format("支付方式：充电卡、本APP\r\n运营商：%s\r\n服务电话：%s",
//              substationBean.companyName, substationBean.serviceCall));
			//运营商
			holder.tvCompany.setText(substationBean.companyName);
			holder.tvCompany.setSelected(true);
			holder.tvCall.setText("服务电话：" + substationBean.serviceCall);
			holder.tvPayType.setText("支付方式：充电卡、本APP");
			Drawable icon = holder.itemView.getResources().getDrawable(substationBean.isFavorites ?
					R.drawable.ic_favor_selected : R.drawable.ic_favor);
			holder.ivFavor.setImageDrawable(icon);
			holder.ivFavor.setTag(substationBean);
			holder.ivFavor.setOnClickListener(listener);

			//跳转充电桩详情
			holder.llDetail.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//这里应该是substationId，不能写id
//          StationDetailActivity.navigation(substationBean.substationId, substationBean.name,
//                  substationBean.getLat(), substationBean.getLng());
					PileListActivity.navigation(substationBean.areaId, substationBean.areaName,
							substationBean.getLat(), substationBean.getLng());
				}
			});

			//跳转电价界面
			/*holder.llFree.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					FeeActivity.navigation(substationBean.areaId, substationBean.areaName);
				}
			});*/

			holder.colItemDistance.setTag(position);
			//导航
			holder.colItemDistance.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					SubstationBean bean = substations.get((int) v.getTag());
					RxBus.getInstance().postEvent(new ShowNavListEvent(bean.getLat(), bean.getLng()));
				}
			});
		}

		public void setSubstations(List<SubstationBean> substations) {
			this.substations = substations;
		}

		@Override
		public int getItemCount() {
			return substations == null ? 0 : substations.size();
		}

		public void removeItem(SubstationBean bean) {
			if (getItemCount() > 0) {
				substations.remove(bean);
				notifyDataSetChanged();
			}
		}

		class ViewHolder extends RecyclerView.ViewHolder {

			@BindView(R.id.col_item_station)
			TextView colItemStation;
			@BindView(R.id.col_item_address)
			TextView colItemAddress;
			@BindView(R.id.col_item_distance)
			TextView colItemDistance;
			@BindView(R.id.col_item_time)
			TextView colItemTime;

			@BindView(R.id.tv_electric_fee)
			TextView tvE;
			@BindView(R.id.tv_service_fee)
			TextView tvS;
			@BindView(R.id.tv_park_fee)
			TextView tvP;
			@BindView(R.id.tv_pay_type)
			TextView tvPayType;
			@BindView(R.id.tv_companyName)
			TextView tvCompany;
			@BindView(R.id.tv_serviceCall)
			TextView tvCall;
			//      @BindView(R.id.tv_service)
//      TextView tvService;
//      @BindView(R.id.tv_pay_type)
//      TextView tvPayType;
			@BindView(R.id.ll_detail)
			LinearLayout llDetail;
			@BindView(R.id.iv_favor)
			ImageView ivFavor;
			@BindView(R.id.ll_free)
			LinearLayout llFree;
			@BindView(R.id.tv_ac)
			TextView tvAc;
			@BindView(R.id.tv_dc)
			TextView tvDc;

			ViewHolder(View itemView) {
				super(itemView);
				ButterKnife.bind(this, itemView);
			}
		}
	}
}
