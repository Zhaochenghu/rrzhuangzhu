package cn.com.leanvision.baseframe.util;

/********************************
 * Created by lvshicheng on 2016/12/9.
 * <p>
 * 应用中的校验工具类
 ********************************/
public class LvVerifyCheckUtil {

  /**
   * 密码是否合法
   *
   * @return true 合法
   */
  public static boolean isPasswordVerify(String passwordString) {
    if (LvTextUtil.isEmpty(passwordString) || passwordString.length() < 6) {
      return false;
    } else {
      return true;
    }
  }
}
