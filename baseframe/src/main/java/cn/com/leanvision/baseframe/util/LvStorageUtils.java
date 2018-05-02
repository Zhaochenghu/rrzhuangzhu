package cn.com.leanvision.baseframe.util;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/********************************
 * Created by lvshicheng on 2016/12/22.
 ********************************/
public class LvStorageUtils {

  /**
   * 获取应用内部cache路径
   * <p>
   * /data/data/pkg/
   */
  public static File getCacheFile(Context context, String fileName) {
    File file = new File(context.getCacheDir(), fileName);
    return file;
  }

  /**
   * 判断SD卡是否可用
   *
   * @return true : 可用<br>false : 不可用
   */
  public static boolean isSDCardEnable() {
    return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
  }

  /**
   * 获取SD卡路径
   * <p>先用shell，shell失败再普通方法获取，一般是/storage/emulated/0/</p>
   *
   * @return SD卡路径
   */
  public static String getSDCardPath() {
    if (!isSDCardEnable()) return "sdcard unable!";
    String cmd = "cat /proc/mounts";
    Runtime run = Runtime.getRuntime();
    BufferedReader bufferedReader = null;
    try {
      Process p = run.exec(cmd);
      bufferedReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(p.getInputStream())));
      String lineStr;
      while ((lineStr = bufferedReader.readLine()) != null) {
        if (lineStr.contains("sdcard") && lineStr.contains(".android_secure")) {
          String[] strArray = lineStr.split(" ");
          if (strArray.length >= 5) {
            return strArray[1].replace("/.android_secure", "") + File.separator;
          }
        }
        if (p.waitFor() != 0 && p.exitValue() == 1) {
          return " 命令执行失败";
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      LvFileUtils.closeIO(bufferedReader);
    }
    return Environment.getExternalStorageDirectory().getPath() + File.separator;
  }

  /**
   * 获取SD卡data路径
   *
   * @return SD卡data路径
   */
  public static String getDataPath() {
    if (!isSDCardEnable()) return null;
    return Environment.getExternalStorageDirectory().getPath() + File.separator;
  }

  /**
   * 获取指定的存储路径
   */
  public static String getStoragePath(String path) {
    if (!isSDCardEnable()) return null;
    return Environment.getExternalStorageDirectory().getPath() + File.separator + path;
  }
}
