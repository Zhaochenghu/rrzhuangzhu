package cn.com.leanvision.baseframe.router;

import cn.com.leanvision.baseframe.router.annotation.RouterParam;
import cn.com.leanvision.baseframe.router.annotation.RouterUri;

/********************************
 * Created by lvshicheng on 2016/12/1.
 ********************************/
public interface IRouterUri {

  @RouterUri(routerUri = "CKR://leanvision:8888/signIn")
  void jumpToSignIn();

  @RouterUri(routerUri = "CKR://leanvision:8888/main")
  void jumpToMain();

  @RouterUri(routerUri = "CKR://leanvision:8888/web")
  void jumpToWeb(@RouterParam("url") String url, @RouterParam("action") String action);

  @RouterUri(routerUri = "CKR://leanvision:8888/time_setting")
  void jumpToTimeSetting(@RouterParam("devId") String devId);

  @RouterUri(routerUri = "CKR://leanvision:8888/power")
  void jumpToPower(@RouterParam("devId") String devId);
}
