package cn.com.leanvision.baseframe.util;

import android.content.Context;

/********************************
 * Created by lvshicheng on 2016/12/28.
 ********************************/
public class LvCrashHandler implements Thread.UncaughtExceptionHandler {

  private Context mContext;
  private static LvCrashHandler INSTANCE = new LvCrashHandler();

  private LvCrashHandler() {
  }

  public static LvCrashHandler getInstance() {
    return INSTANCE;
  }

  public void init(Context ctx) {
    mContext = ctx;
    Thread.setDefaultUncaughtExceptionHandler(this);
  }

  @Override
  public void uncaughtException(Thread thread, Throwable ex) {
    handleException(ex);
  }

  /**
   * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
   *
   * @param ex
   * @return true:如果处理了该异常信息;否则返回false
   */
  private boolean handleException(final Throwable ex) {
    if (ex == null) {
      return true;
    }

    // 异常处理
    // Executors.newSingleThreadExecutor().execute(new Runnable() {
    // @Override
    // public void run() {
    // String time = new Date(System.currentTimeMillis()).toLocaleString();
    // String userid = SharedPrefHelper.getInstance().getUserId();
    // String message = "TIME : " + userid + "-" + time + "\r\n" +
    // getMobileInfo() + "\r\n" + getErrorInfo(ex);
    // ActionFileUtil.writeToSdcard(message, null);
    // LogUtil.log("异常写入成功  ： " + message);
    // }
    // });

    ex.printStackTrace();

    LvActivityManager.getInstance().popAll();
    android.os.Process.killProcess(android.os.Process.myPid());
    System.exit(0);
    return true;
  }
}
