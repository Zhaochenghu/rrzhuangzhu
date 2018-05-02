package com.renren0351.presenter.fee;

import com.renren0351.model.bean.FeeBean;
import com.renren0351.presenter.LvIBasePresenter;
import com.renren0351.presenter.LvIBaseView;

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
