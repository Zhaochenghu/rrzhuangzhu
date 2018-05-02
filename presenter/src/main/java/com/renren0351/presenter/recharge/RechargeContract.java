package com.renren0351.presenter.recharge;

import com.renren0351.model.response.RechargeRecordResponse;
import com.renren0351.presenter.LvIBasePresenter;
import com.renren0351.presenter.LvIBaseView;

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
