package cn.com.leanvision.baseframe.util;

import android.support.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/********************************
 * Created by lvshicheng on 2016/12/29.
 ********************************/
public class LvGsonUtils {

  public static JsonArray optJa(JsonObject jb, String key) {
    JsonElement je = checkNull(jb, key);
    if (je == null) {
      return null;
    }
    return je.getAsJsonArray();
  }

  public static JsonObject optJb(JsonObject jb, String key) {
    JsonElement je = checkNull(jb, key);
    if (je == null) {
      return null;
    }
    return je.getAsJsonObject();
  }

  public static String optString(JsonObject jb, String key) {
    JsonElement je = checkNull(jb, key);
    if (je == null) {
      return null;
    }
    return je.getAsString();
  }

  public static int optInt(JsonObject jb, String key) {
    JsonElement je = checkNull(jb, key);
    if (je == null) {
      return 0;
    }
    String asString = je.getAsString();
    if (LvTextUtil.isEmpty(asString)) {
      return 0;
    } else {
      return Integer.parseInt(asString);
    }
  }

  public static boolean jaIsEmpty(JsonArray ja) {
    if (ja == null || ja.size() == 0) {
      return true;
    }
    return false;
  }

  @Nullable
  private static JsonElement checkNull(JsonObject jb, String key) {
    if (jb == null)
      return null;
    JsonElement je = jb.get(key);
    if (je == null)
      return null;
    return je;
  }
}
