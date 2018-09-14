package com.bxchongdian.app.utils;

import android.support.annotation.Nullable;

import com.bxchongdian.app.LvAppConstants;
import com.bxchongdian.model.storage.AppInfosPreferences;

import cn.com.leanvision.annotation.spf.LvISpfEncryption;
import cn.com.leanvision.baseframe.security.DES3Helper;
import cn.com.leanvision.baseframe.security.MD5Helper;
import cn.com.leanvision.baseframe.util.LvTextUtil;

/********************************
 * Created by lvshicheng on 2016/12/7.
 * <p>
 * 用于管理SharedPreference保存中间加解密过程
 ********************************/
public class LvSpfEncryption implements LvISpfEncryption {

  private static String SECRET_KEY;
  private static String IV;

  @Nullable
  @Override
  public String encodeStr(String src) {
    if (LvTextUtil.isEmpty(src))
      return src;
    String secretKey = getSecretKey();
    String iv = getIv();
//    DebugLog.log("src: %s, key: %s, iv: %s", src, secretKey, iv);
    if (LvTextUtil.isEmpty(secretKey)) {
      new RuntimeException("找不到本地加密秘钥！").printStackTrace();
      return null; // 秘钥都找不到了，就别存了。
    }
    try {
      return DES3Helper.encode(src, secretKey, iv);
    } catch (Exception e) {
      e.printStackTrace();
      new RuntimeException("加密保存失败了！").printStackTrace();
      return null;
    }
  }

  @Nullable
  @Override
  public String decodeStr(String src) {
    if (LvTextUtil.isEmpty(src))
      return src;
    String secretKey = getSecretKey();
    String iv = getIv();
//    DebugLog.log("src: %s, key: %s, iv: %s", src, secretKey, iv);
    if (LvTextUtil.isEmpty(secretKey)) {
      new RuntimeException("找不到本地加密秘钥！").printStackTrace();
      return null; // 秘钥都找不到了，就当做不存在这个值。
    }
    try {
      return DES3Helper.decode(src, secretKey, iv);
    } catch (Exception e) {
      e.printStackTrace();
      new RuntimeException("解密失败了！").printStackTrace();
      return null;
    }
  }

  /**
   * 登录成功就需要保存一次秘钥，后面加密需要
   */
  public static void setSecretKey(String key) {
    if (LvTextUtil.isEmpty(key)) {
      AppInfosPreferences.get().setSecretKey(key);
    } else {
      String temp = null;
      for (int i = 0; i < 2; i++) { // 连续三次加密
        temp = MD5Helper.getMD5StringWithSalt(key, LvAppConstants.MD5_SALT);
        key = temp;
      }
      AppInfosPreferences.get().setSecretKey(temp);
    }
    SECRET_KEY = null;
    IV = null;
  }

  /**
   * 取28位秘钥
   */
  private static String getSecretKey() {
    if (LvTextUtil.isEmpty(SECRET_KEY)) {
      String secretKey = AppInfosPreferences.get().getSecretKey();
      if (LvTextUtil.isEmpty(secretKey)) {
        SECRET_KEY = null;
      } else {
        int start = 2;
        int end = 30;
        SECRET_KEY = secretKey.substring(start, end);
      }
    }
    return SECRET_KEY;
  }

  /**
   * 取8位向量
   */
  private static String getIv() {
    if (LvTextUtil.isEmpty(IV)) {
      String secretKey = AppInfosPreferences.get().getSecretKey();
      if (LvTextUtil.isEmpty(secretKey)) {
        IV = null;
      } else {
        int length = secretKey.length();
        int start = length - 10;
        int end = length - 2;
        IV = secretKey.substring(start, end);
      }
    }
    return IV;
  }
}
