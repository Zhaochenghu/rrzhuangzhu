package cn.com.leanvision.baseframe.util;

import android.support.annotation.Nullable;

import java.util.List;
import java.util.Map;

/********************************
 * Created by lvshicheng on 2016/12/7.
 ********************************/
public class LvTextUtil {

  /**
   * Returns true if the string is null or 0-length.
   *
   * @param str the string to be examined
   * @return true if str is null or zero length
   */
  public static boolean isEmpty(@Nullable CharSequence str) {
    if (str == null || str.length() == 0)
      return true;
    return false;
  }

  public static boolean isArrayEmpty(@Nullable List list) {
    if (list == null || list.isEmpty())
      return true;
    return false;
  }

  public static boolean isMapEmpty(@Nullable Map list) {
    if (list == null || list.isEmpty())
      return true;
    return false;
  }

  /**
   * 如果i小于10，添�?后生成string
   */
  public static String addZreoIfLessThanTen(int i) {
    String string;
    int ballNum = i;
    if (ballNum < 10) {
      string = "0" + ballNum;
    } else {
      string = ballNum + "";
    }
    return string;
  }

}
