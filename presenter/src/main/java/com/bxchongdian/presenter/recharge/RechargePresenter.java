package com.bxchongdian.presenter.recharge;

import com.bxchongdian.model.dagger.ApiComponentHolder;
import com.bxchongdian.model.response.RechargeRecordResponse;

import java.lang.ref.WeakReference;

import cn.com.leanvision.baseframe.rx.SimpleSubscriber;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/08/11
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class RechargePresenter implements RechargeContract.Presenter<RechargeContract.View> {
	private WeakReference<RechargeContract.View> wrView;
	private RechargeContract.View view;
	@Override
	public void attachView(RechargeContract.View view) {
		wrView = new WeakReference<RechargeContract.View>(view);
	}

	@Override
	public void detachView() {
		if (wrView != null) {
			wrView.clear();
		}
	}

	@Override
	public void getRecordData() {
		ApiComponentHolder.sApiComponent
				.apiService()
				.rechargeRecord()
				.take(1)
				.compose(SchedulersCompat.<RechargeRecordResponse>applyNewSchedulers())
				.subscribe(new SimpleSubscriber<RechargeRecordResponse>() {
					@Override
					public void onError(Throwable e) {
						if (getView() != null) {
							getView().showNormal();
							getView().requestFailed(null);
						}
					}

					@Override
					public void onNext(RechargeRecordResponse rechargeRecordResponse) {
						getView().showNormal();
						if (rechargeRecordResponse.isSuccess()){
							getView().refreshRecordData(rechargeRecordResponse.content);
						}else {
							getView().requestFailed(rechargeRecordResponse.msg);
						}
					}

					@Override
					public void onStart() {
						if (getView() != null) {
							getView().showLoading(null);
						}
					}
				});
	}

	private RechargeContract.View getView(){
		if (view == null) {
			view = wrView.get();
		}
		return view;
	}
}
