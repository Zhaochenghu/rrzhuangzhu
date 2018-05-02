package cn.com.leanvision.baseframe.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.v4.net.ConnectivityManagerCompat;
import android.telephony.TelephonyManager;

/********************************
 * Created by lvshicheng on 2016/12/12.
 ********************************/
public class LvNetWorkUtil {

  // 得到接入点的BSSID
  public static String getBSSID(Context context) {
    WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
    return (mWifiInfo == null) ? "" : mWifiInfo.getBSSID();
  }

  @NonNull
  public static NetworkType getNetworkType(Context context) {
    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
    if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
      return NetworkType.ANY;
    }

    TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    if (telephonyManager != null && telephonyManager.isNetworkRoaming()) {
      return NetworkType.CONNECTED;
    }

    boolean metered = ConnectivityManagerCompat.isActiveNetworkMetered(connectivityManager);
    return metered ? NetworkType.NOT_ROAMING : NetworkType.UNMETERED;
  }

  public enum NetworkType {
    /**
     * Network must not be connected.
     */
    ANY,
    /**
     * Network must be connected.
     */
    CONNECTED,
    /**
     * Network must be connected and unmetered.
     */
    UNMETERED,
    /**
     * Network must be connected and not roaming, but can be metered.
     */
    NOT_ROAMING
  }

}
