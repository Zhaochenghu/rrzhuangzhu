package cn.com.leanvision.baseframe.app;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;

import cn.com.leanvision.baseframe.log.DebugLog;

/********************************
 * Created by lvshicheng on 2016/11/30.
 ********************************/
public abstract class LvBaseApplication extends Application {

  private static LvBaseApplication INSTANCE;

  @Override
  public void onCreate() {
    super.onCreate();
    INSTANCE = this;
  }

  protected abstract void initSharedPreference();

  public static Context getContext() {
    return INSTANCE;
  }

//  /**
//   * 用于测试JNI调用，参考[https://github.com/zhengxiaopeng/RobolectricSupportNativeLibs]
//   */
//  protected void loadNativeLibraries() {
//  }

  protected void initDebug(boolean debugable) {
    // init log util
    DebugLog.debugable(debugable);
    // init stetho
    //    if (debugable)
    Stetho.initializeWithDefaults(this);
    // init leak cannary
    if (LeakCanary.isInAnalyzerProcess(this)) {
      // This process is dedicated to LeakCanary for heap analysis.
      // You should not init your app in this process.
      return;
    }
    LeakCanary.install(this);
  }
}
