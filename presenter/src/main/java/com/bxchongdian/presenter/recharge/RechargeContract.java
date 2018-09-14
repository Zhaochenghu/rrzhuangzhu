package com.bxchongdian.presenter.recharge;

import com.bxchongdian.model.response.RechargeRecordResponse;
import com.bxchongdian.presenter.LvIBasePresenter;
import com.bxchongdian.presenter.LvIBaseView;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/08/11
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public interface RechargeContract {
	interface View extends LvIBaseView{
		void refreshRecordData(RechargeRecordResponse.Content content);
	}

	interface Presenter<R> extends LvIBasePresenter<R>{
		void getRecordData();
	}
}
