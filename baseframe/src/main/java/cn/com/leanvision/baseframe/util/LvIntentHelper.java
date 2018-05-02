package cn.com.leanvision.baseframe.util;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

/********************************
 * Created by lvshicheng on 2016/12/3.
 ********************************/
public class LvIntentHelper {

    /**
     * 创建快捷图标
     *
     * @param context 需要创建快捷图标的Activity
     */
    public static void addShortcut(Context context) {
        addShortcut(context, context.getApplicationInfo().icon);
    }

    /**
     * 创建快捷图标
     *
     * @param context
     * @param iconId
     */
    public static void addShortcut(Context context, int iconId) {
        final Intent addIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        final Parcelable icon = Intent.ShortcutIconResource.fromContext(context, iconId); // 获取快捷键的图标
        addIntent.putExtra("duplicate", false); // 设置快捷方式不能重复
        final Intent myIntent = new Intent(context, context.getClass());
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "");// 快捷方式的标题
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);// 快捷方式的图标
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, myIntent);// 快捷方式的动作
        context.sendBroadcast(addIntent);
    }
}
