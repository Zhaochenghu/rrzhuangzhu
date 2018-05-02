package com.renren0351.presenter.appointment;

import com.renren0351.presenter.LvIBasePresenter;
import com.renren0351.presenter.LvIBaseView;

import java.util.Map;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/07/11
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public interface AppointmentContract {
    interface View extends LvIBaseView{
        void orderSuccess(String orderId);
        void cancelOrderSuccess();
    }

    interface Presenter<R> extends LvIBasePresenter<R>{
        void order(Map<String, Object> request);
        void cancelOrder(String orderId);
    }
}
