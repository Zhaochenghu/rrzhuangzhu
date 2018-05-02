package cn.com.leanvision.baseframe.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.List;

/********************************
 * Created by lvshicheng on 2016/12/9.
 * <p>
 * 有些方法不知道归哪一类的，就放这里
 ********************************/
public class LvCommonUtil {

    /**
     * @author lvshicheng
     * @time 2015-1-19 18:18:44
     * @description 判断当前线程是否是主线程
     */
    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    /**
     * @param context
     * @return boolean
     * @description 判断当前应用是否在后台运行
     */
    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                return appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND;
            }
        }
        return false;
    }

    public static void hideSoftInput(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        IBinder windowToken = activity.getWindow().getDecorView().getWindowToken();
        if (windowToken != null)
            imm.hideSoftInputFromWindow(windowToken, 0);
    }

    /**
     * 动态显示软键盘
     *
     * @param edit 输入框
     */
    public static void showSoftInput(Context context, EditText edit) {
        edit.setFocusable(true);
        edit.setFocusableInTouchMode(true);
        edit.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) return;
        imm.showSoftInput(edit, 0);
    }

    /**
     * @param list
     * @param defValue
     * @param len
     */
    public static <T> void fillArrayWithDefaultValue(@NonNull List<T> list, int len, T defValue) {
        LvPreconditions.checkNotNull(list, "List must not be null.");
        for (int i = 0; i < len; i++) {
            list.add(defValue);
        }
    }
}
