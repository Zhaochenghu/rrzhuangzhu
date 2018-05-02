package cn.com.leanvision.baseframe.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/********************************
 * Created by lvshicheng on 2016/12/12.
 ********************************/
public class LvTimeUtil {

  public static final SimpleDateFormat yMdHms = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());

  public static final SimpleDateFormat y_M_d_Hms = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault());

  public static final SimpleDateFormat _y_M_d_Hms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

  public static String transStampToHMS(long stamp) {
    String time = "";
    long totalTime = stamp / 1000;// s 3750
    long hour = totalTime / (60 * 60);
    long minute = (totalTime % (60 * 60)) / 60;
    if (hour == 0){
      time = minute + "分钟";
    }else {
      if (minute == 0){
        time = hour + "小时";
      }else {
        time = hour + "小时" + minute + "分钟";
      }

    }
    return time;
  }

  /**
   * 根据时间段获得对应时间
   * 时间间隔为15分钟
   * @param value
   * @return
   */
  public static String getTime(int value){
    int totalTime = value * 15;
    int hour = totalTime / 60;
    int minute = totalTime % 60;
    return String.format("%s时%s分",
            hour < 10 ? "0" + hour : "" + hour,
            minute < 10 ? "0" + minute : "" + minute);
  }

  /**
   * 根据当前时间获得时间段
   * 时间间隔为15分钟
   * @return
   */
  public static int getDuration(){
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    int minute = calendar.get(Calendar.MINUTE);
    int totalMinute = hour * 60 + minute;
    return totalMinute / 15 + 1;
  }

  public static String calcTimeDuration(String time){
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String duration = "" ;
    try {
      Date date = sdf.parse(time);
      long[] times = getDistanceTimes(date);
      if (times[0] > 0){
        duration = times[0] + "小时";
      }
      duration = duration + times[1] + "分钟";
    } catch (ParseException e) {
      return duration;
    }
    return duration;
  }

  public static Long calcMinute(String time){
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    long minute = 0;
    try {
      Date date = sdf.parse(time);
      long diff = date.getTime() - System.currentTimeMillis();
      if (diff > 0){
        minute = diff / 1000 / 60;
      }
    } catch (ParseException e) {
      return minute;
    }
    return minute;
  }

  public static String minute2String(long time){
    long hour = time / 60;
    long minute = time % 60;
    String str = "";
    if (hour <= 0){
      str = minute + "分钟";
    }else {
      if (minute == 0){
        str = hour + "小时";
      }else {
        str = hour + "小时" + minute + "分钟";
      }
    }
    return str;
  }

  private static long[] getDistanceTimes(Date date) {
    long hour = 0;
    long min = 0;
    long sec = 0;
    long diff;
    diff = date.getTime() - System.currentTimeMillis();
    if (diff > 0){
      hour = diff / (60 * 60 * 1000);
      min =  diff / (60 * 1000) - hour * 60;
      sec = diff / 1000 - hour * 60 * 60 - min * 60;
    }
    long[] times = {hour, min, sec};
    return times;
  }

  public static String second2string(long time){
    if (time <= 0){
      return "0:00";
    }
    long second = 0;
    long minute = 0;
    minute = time / 60;
    second = time % 60;
    if (second < 10){
      return minute + ":0" + second;
    }else {
      return minute + ":" + second;
    }
  }
}
