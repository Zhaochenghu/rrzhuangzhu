package com.bxchongdian.model.storage;

import android.content.Context;

import cn.com.leanvision.annotation.spf.LvISpfEncryption;
import cn.com.leanvision.annotation.spf.LvSpfParser;

public final class AptPreferencesManager {
  private static Context sContext;

  private static LvSpfParser sAptParser;

  private static LvISpfEncryption spfEncryption;

  private static String sUserInfo;

  public static void init(Context context, LvSpfParser aptParser, LvISpfEncryption spfEncryption) {
    sContext = context;
    sAptParser = aptParser;
    AptPreferencesManager.spfEncryption = spfEncryption;
  }

  public static Context getContext() {
    return sContext;
  }

  public static LvSpfParser getAptParser() {
    return sAptParser;
  }

  public static LvISpfEncryption getSpfEncryption() {
    return spfEncryption;
  }

  public static void setUserInfo(String userInfo) {
    sUserInfo = userInfo;
  }

  public static String getUserInfo() {
    return sUserInfo;
  }
}
