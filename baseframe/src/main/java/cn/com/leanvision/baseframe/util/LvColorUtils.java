package cn.com.leanvision.baseframe.util;

import android.graphics.Color;

/********************************
 * Created by lvshicheng on 2016/12/3.
 ********************************/
public class LvColorUtils {

  /**
   * RGB颜色加深
   *
   * @param RGBValue RGB颜色的值
   * @param value    加深的程度值0.0 ~ 1，建议0.1
   * @return 加深后的RGB值
   */
  public static int colorBurn(int RGBValue, int value) {
    int alpha = RGBValue >> 24;
    int red = RGBValue >> 16 & 0xFF;
    int green = RGBValue >> 8 & 0xFF;
    int blue = RGBValue & 0xFF;
    int v = 1 - value;
    red = (int) Math.floor(red * v);
    green = (int) Math.floor(green * v);
    blue = (int) Math.floor(blue * v);
    return Color.argb(alpha, red, green, blue);
  }

}
