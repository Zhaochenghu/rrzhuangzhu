package com.bxchongdian.presenter.register;

import com.bxchongdian.model.request.RegisterRequest;
import com.bxchongdian.presenter.LvIBasePresenter;
import com.bxchongdian.presenter.LvIBaseView;

/**
 * Created by Administrator on 2017/3/1 0001.
 */

public interface RegisterContract {
    interface View extends LvIBaseView {
        void registerSuccess();

        void getCaptchaSuccess();

        void getCaptchaFailed();
    }

    interface presenter extends LvIBasePresenter<View> {
        void hpRegister(RegisterRequest request);

        void hpCaptcha(String phoneNum);

        View getView();

        boolean isViewActive();
    }
}
