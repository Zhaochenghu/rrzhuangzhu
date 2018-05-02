package cn.com.leanvision.baseframe.log;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Locale;

/********************************
 * Created by lvshicheng on 2016/12/1.
 ********************************/
public class DebugLog {
  /**
   * Log输出所在类
   */
  private static String className;
  /**
   * Log输出所在方法
   */
  private static String methodName;
  /**
   * Log输出所行号
   */
  private static int    lineNumber;
  private static boolean debugable = true;

  private DebugLog() {
  }

  /**
   * 是否可Debug状态
   *
   * @return
   */
  public static boolean isDebuggable() {
    return DebugLog.debugable;
  }

  public static void debugable(boolean debugable) {
    DebugLog.debugable = debugable;
  }

  /**
   * 创建Log输出的基本信息
   *
   * @param log
   * @return
   */
  private static String createLog(String log) {
    StringBuffer buffer = new StringBuffer();
    buffer.append("[");
    buffer.append(methodName);
    buffer.append("()");
    buffer.append(" line:");
    buffer.append(lineNumber);
    buffer.append("] ");
    buffer.append(log);
    return buffer.toString();
  }

  /**
   * 取得输出所在位置的信息 className methodName lineNumber
   *
   * @param sElements
   */
  private static void getMethodNames(StackTraceElement[] sElements) {
    className = sElements[1].getFileName().split("\\.")[0];
    methodName = sElements[1].getMethodName();
    lineNumber = sElements[1].getLineNumber();
  }

  public static void log(String message) {
    if (!isDebuggable())
      return;

    getMethodNames(new Throwable().getStackTrace());
    Log.e(className, createLog(message));
  }

  public static void log(@NonNull String message, Object... args) {
    if (!isDebuggable())
      return;

    getMethodNames(new Throwable().getStackTrace());
    Log.e(className, createLog(String.format(Locale.getDefault(), message, args)));
  }
}
