package com.renren0351.presenter.car;

import com.renren0351.model.bean.CarBean;
import com.renren0351.presenter.LvIBasePresenter;
import com.renren0351.presenter.LvIBaseView;

import java.util.List;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/06/20
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public interface CarContract {
    interface View extends LvIBaseView{
        void saveCarSuccess();

        void refreshCars(List<CarBean> cars);
    }

    interface Presenter<R> extends LvIBasePresenter<R>{

        void saveCar(CarBean bean);

        void queryCars();
    }


}
