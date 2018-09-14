package com.bxchongdian.presenter.profile;

import com.bxchongdian.model.bean.ProfileBean;
import com.bxchongdian.presenter.LvIBasePresenter;
import com.bxchongdian.presenter.LvIBaseView;

/********************************
 * Created by lvshicheng on 2017/3/6.
 ********************************/
public interface ProfileContract {

  interface View extends LvIBaseView{

    void getProfileSuccess(ProfileBean profileBean);

    void getProfileFailed(String msg);
  }

  interface Presenter extends LvIBasePresenter<View> {

    void getProfile();
  }
}
