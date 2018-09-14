package com.bxchongdian.presenter.fee;

import com.bxchongdian.model.bean.FeeBean;
import com.bxchongdian.presenter.LvIBasePresenter;
import com.bxchongdian.presenter.LvIBaseView;

import java.util.List;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/07/11
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public interface FeeContract {
    interface View extends LvIBaseView{
        void getFees(List<FeeBean> list);
    }

    interface Presenter<R> extends LvIBasePresenter<R>{
        void queryFees(String substationId);
    }
}
