package com.renren0351.presenter.profile;

import com.renren0351.model.bean.ProfileBean;
import com.renren0351.presenter.LvIBasePresenter;
import com.renren0351.presenter.LvIBaseView;

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
