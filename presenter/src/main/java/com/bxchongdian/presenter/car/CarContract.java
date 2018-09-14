package com.bxchongdian.presenter.car;

import com.bxchongdian.model.bean.CarBean;
import com.bxchongdian.presenter.LvIBasePresenter;
import com.bxchongdian.presenter.LvIBaseView;

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
