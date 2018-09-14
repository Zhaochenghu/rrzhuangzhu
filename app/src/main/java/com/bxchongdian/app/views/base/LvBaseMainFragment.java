package com.bxchongdian.app.views.base;

import com.bxchongdian.app.R;

/********************************
 * Created by lvshicheng on 2017/2/13.
 ********************************/
public abstract class LvBaseMainFragment extends LvBaseFragment {

  // 再点一次退出程序时间设置
  private static final long WAIT_TIME  = 2000L;
  private              long TOUCH_TIME = 0;

  /**
   * 处理回退事件
   */
  @Override
  public boolean onBackPressedSupport() {
    if (beforeBack()) {
      return true;
    }
    if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
      _mActivity.finish();
    } else {
      TOUCH_TIME = System.currentTimeMillis();
      showToast(R.string.press_again_exit);
    }
    return true;
  }

  protected boolean beforeBack() {
    return false;
  }
}
