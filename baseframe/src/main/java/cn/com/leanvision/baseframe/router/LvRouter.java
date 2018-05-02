package cn.com.leanvision.baseframe.router;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import cn.com.leanvision.baseframe.log.DebugLog;
import cn.com.leanvision.baseframe.router.annotation.RouterParam;
import cn.com.leanvision.baseframe.router.annotation.RouterUri;

/********************************
 * Created by lvshicheng on 2016/12/1.
 ********************************/
public class LvRouter {

  private static Context    mContext;
  private static IRouterUri mRouterUri;

  public static void initRouter(Context context) {
    mContext = context;
    mRouterUri = create(IRouterUri.class);
  }

  public static IRouterUri routerUri() {
    return mRouterUri;
  }

  private static IRouterUri create(Class<IRouterUri> iRouterUriClass) {
    IRouterUri mIRouterUri = (IRouterUri) Proxy.newProxyInstance(iRouterUriClass.getClassLoader(),
        new Class[]{iRouterUriClass},
        new InvocationHandler() {
          @Override
          public Object invoke(Object o, Method method, Object[] objects) throws Throwable {

            StringBuilder sbr = new StringBuilder();
            RouterUri reqUrl = method.getAnnotation(RouterUri.class);
            DebugLog.log("reqUrl: %s", reqUrl.routerUri());
            sbr.append(reqUrl.routerUri());
            Annotation[][] parameterAnnotationsArray = method.getParameterAnnotations();
            int pos = 0;
            int len = parameterAnnotationsArray.length;
            for (int i = 0; i < len; i++) {
              Annotation[] annotations = parameterAnnotationsArray[i];
              if (annotations != null && annotations.length != 0) {
                if (pos == 0) {
                  sbr.append("?");
                } else {
                  sbr.append("&");
                }
                pos++;
                RouterParam reqParam = (RouterParam) annotations[0];
                sbr.append(reqParam.value());
                sbr.append("=");
                sbr.append(objects[i]);
                DebugLog.log("reqParam: %s = %s", reqParam.value(), objects[i]);
              }
            }
            openRouterUri(sbr.toString());
            return null;
          }
        });
    return mIRouterUri;
  }

  private static void openRouterUri(String url) {
    PackageManager packageManager = mContext.getPackageManager();
    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
    boolean isValid = !activities.isEmpty();
    if (isValid) {
      mContext.startActivity(intent);
    } else {
      // TODO To add default page!
      DebugLog.log("404: [%s]", url);
    }
  }
}
