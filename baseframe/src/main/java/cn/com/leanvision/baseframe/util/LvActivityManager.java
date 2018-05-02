package cn.com.leanvision.baseframe.util;

import android.app.Activity;
import android.view.View;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.leanvision.baseframe.log.DebugLog;

/********************************
 * Created by lvshicheng on 16/8/9.
 ********************************/
public class LvActivityManager {
  // 用于标记上一个页面的key，暂时用CanonicalName
  public String previousKey;
  public View   previusView;

  private HashMap<String, SoftReference<Activity>> unDestroyActivityList;
  private List<String>                             keys;

  private static LvActivityManager ourInstance = new LvActivityManager();

  public static LvActivityManager getInstance() {
    return ourInstance;
  }

  private LvActivityManager() {
    unDestroyActivityList = new HashMap<>();
    keys = new ArrayList<>();
  }

  public void pushActivity(Activity activity) {
    String key = activity.getClass().getSimpleName();
    if (keys.contains(key)) {
      popActivity(activity);
      keys.remove(key);
    }
    SoftReference<Activity> softReference = new SoftReference<>(activity);
    unDestroyActivityList.put(key, softReference);
    keys.add(key);
  }

  public void popActivity(Activity activity) {
    String key = activity.getClass().getSimpleName();
    SoftReference<Activity> activitySoftReference = unDestroyActivityList.get(activity.getClass().getSimpleName());
    if (activitySoftReference != null) {
      Activity aty = activitySoftReference.get();
      if (null != aty) aty.finish();
      unDestroyActivityList.remove(key);
      keys.remove(key);
    }
  }

  public void popAll() {
    for (Map.Entry<String, SoftReference<Activity>> entry : unDestroyActivityList.entrySet()) {
      SoftReference<Activity> activitySoftReference = entry.getValue();
      if (activitySoftReference != null) {
        Activity aty = activitySoftReference.get();
        if (null != aty) aty.finish();
      }
    }
    unDestroyActivityList.clear();
    keys.clear();
  }

  public Activity getTop() {
    if (keys.size() > 0) {
      String s = keys.get(keys.size() - 1);
      SoftReference<Activity> activitySoftReference = unDestroyActivityList.get(s);
      if (null != activitySoftReference) {
        Activity activity = activitySoftReference.get();
        if (null != activity && !activity.isFinishing()) {
          return activity;
        }
      }
    }
    return null;
  }

  public int activeCount() {
    return unDestroyActivityList == null ? 0 : unDestroyActivityList.size();
  }

  public void print() {
    if (unDestroyActivityList != null) {
      DebugLog.log(unDestroyActivityList.toString());
    }
  }
}
