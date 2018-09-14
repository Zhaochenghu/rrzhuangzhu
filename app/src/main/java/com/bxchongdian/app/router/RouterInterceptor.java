package com.bxchongdian.app.router;

import android.content.Context;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Interceptor;
import com.alibaba.android.arouter.facade.callback.InterceptorCallback;
import com.alibaba.android.arouter.facade.template.IInterceptor;
import com.bxchongdian.app.LvAppUtils;
import com.bxchongdian.app.views.activities.LoginActivity;

import cn.com.leanvision.baseframe.log.DebugLog;

/********************************
 * Created by lvshicheng on 2017/2/23.
 ********************************/
@Interceptor(priority = 7)
public class RouterInterceptor implements IInterceptor {

  protected Context mContext;

  @Override
  public void process(Postcard postcard, InterceptorCallback callback) {
    DebugLog.log("group : %s", postcard.getGroup());
    DebugLog.log("path : %s", postcard.getPath());
    // Group 'login' must be login before enter.
    if ("login".equals(postcard.getGroup()) && !LvAppUtils.isLogin()) {
      LoginActivity.navigation(false);
    } else {
      callback.onContinue(postcard);
    }
  }

  @Override
  public void init(Context context) {
    mContext = context;
  }
}
