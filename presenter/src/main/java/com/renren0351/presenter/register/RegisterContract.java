package com.renren0351.presenter.register;

import com.renren0351.model.request.RegisterRequest;
import com.renren0351.presenter.LvIBasePresenter;
import com.renren0351.presenter.LvIBaseView;

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
