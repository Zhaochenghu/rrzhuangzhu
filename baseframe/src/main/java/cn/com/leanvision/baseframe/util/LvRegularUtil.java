package cn.com.leanvision.baseframe.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/********************************
 * Created by lvshicheng on 2016/11/30.
 * <p>
 * 关于正则的工具类
 * <p>
 * 1. 起始/结束符  ^/$
 ********************************/
public class LvRegularUtil {

  /**
   * 匹配末尾数字
   */
  public static String matchEndNum(String target) {
    // \d 表示数字 + 表示一个或多个 \b 非字符边界 => "\\d+\\b" T38 _45 -> 38/45
    // 如果要匹配结束，则必须用'$' => "[0-9]+$" T38 _45 -> 45
    Pattern compile = Pattern.compile("[0-9]+$");
    Matcher matcher = compile.matcher(target);
    if (matcher.find())
      return matcher.group();
    return "";
  }

  public static boolean isCarNumber(String text){
    //普通车牌（不包含白色车牌）
    String regex1 = "[A-Z][A-Z0-9]{4}[A-Z0-9挂学警港澳]";
    //新能源车牌（试用的，没有标准）
    String regex2 = "[a-zA-Z](([DF](?![a-zA-Z0-9]*[IO])[0-9]{4})|([0-9]{5}[DF]))";
    if (Pattern.matches(regex1, text)){
      return true;
    }else if (Pattern.matches(regex2, text)){
      return true;
    }else {
      return false;
    }
  }
  public static boolean isVIN(String text){
    String regex = "[A-Z0-9]{17}";
    return Pattern.matches(regex, text);
  }
}
