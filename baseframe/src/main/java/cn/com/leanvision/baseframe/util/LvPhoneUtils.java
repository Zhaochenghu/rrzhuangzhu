package cn.com.leanvision.baseframe.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/********************************
 * Created by lvshicheng on 2016/12/3.
 ********************************/
public class LvPhoneUtils {

  public static String getDeviceId(Context context) {
    return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
  }

  /**
   * 获取屏幕（像素）宽高、密度<br/>
   * DisplayMetrics.widthPixels <br/>
   * DisplayMetrics.heightPixels 注意如果有导航栏会减去导航栏高度(沉浸状态时好像也包括？)<br/>
   * DisplayMetrics.densityDpi <br/>
   *
   * @param activity
   */
  public static DisplayMetrics getDisplayMetrics(Activity activity) {
    DisplayMetrics dm = new DisplayMetrics();
    Display display = activity.getWindowManager().getDefaultDisplay();
    display.getMetrics(dm);
    return dm;
  }

  /**
   * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
   */
  public static int px2dip(Context context, float pxValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (pxValue / scale + 0.5f) - 15;
  }

  public static int sp2px(Context context, float dpValue) {
    final float scale = context.getResources().getDisplayMetrics().scaledDensity;
    return (int) (dpValue * scale + 0.5f);
  }

  /**
   * 根据手机分辨率从dp转成px
   *
   * @param context
   * @param dpValue
   * @return
   */
  public static int dip2px(Context context, float dpValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (dpValue * scale + 0.5f);
  }

  /**
   * 获取本机IP地址
   *
   * @return
   */
  public static String getLocalIpAddress() {
    try {
      for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
        NetworkInterface intf = en.nextElement();
        for (Enumeration<InetAddress> enumIpAddr = intf
            .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
          InetAddress inetAddress = enumIpAddr.nextElement();
          if (!inetAddress.isLoopbackAddress()) {
            return inetAddress.getHostAddress().toString();
          }
        }
      }
    } catch (SocketException e) {
      e.printStackTrace();
    }
    return null;
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
  public static boolean isCharging(Context context) {
    Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    if (intent == null) {
      // should not happen
      return false;
    }

    // 0 is on battery
    int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
    return plugged == BatteryManager.BATTERY_PLUGGED_AC
        || plugged == BatteryManager.BATTERY_PLUGGED_USB
        || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS);
  }

  @SuppressWarnings("deprecation")
  public static boolean isIdle(Context context) {
    PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            /*
             * isDeviceIdleMode() is a very strong requirement and could cause a job
             * to be never run. isDeviceIdleMode() returns true in doze mode, but jobs
             * are delayed until the device leaves doze mode
             */
      return powerManager.isDeviceIdleMode() || !powerManager.isInteractive();
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
      return !powerManager.isInteractive();
    } else {
      return !powerManager.isScreenOn();
    }
  }
}
