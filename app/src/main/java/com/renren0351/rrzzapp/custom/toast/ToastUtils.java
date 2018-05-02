package com.renren0351.rrzzapp.custom.toast;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/08/16
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class ToastUtils {
	private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
	private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

	private int mCheckNotification = -1;
	private volatile static ToastUtils newInstance;

	private IToast mIToast;

	private ToastUtils(Context context) {
		if (isNotificationEnabled(context)) {
			Log.i("TAG", "ToastUtils: --------------------->SystemToast");
			mIToast = new SystemToast(context);
		} else {
			Log.i("TAG", "ToastUtils: --------------------->CustomToast");
			mIToast = new CustomToast(context);
		}
	}

	public static IToast getInstance(Context context) {
		if (newInstance == null) {
			synchronized (ToastUtils.class) {
				if (newInstance == null) {
					newInstance = new ToastUtils(context);
				}
			}
		}
		return newInstance.mIToast;
	}

	private static boolean isNotificationEnabled(Context context) {

		AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
		ApplicationInfo appInfo = context.getApplicationInfo();

		String pkg = context.getApplicationContext().getPackageName();

		int uid = appInfo.uid;

		Class appOpsClass = null; /* Context.APP_OPS_MANAGER */

		try {

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				appOpsClass = Class.forName(AppOpsManager.class.getName());
				Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE,
						Integer.TYPE, String.class);

				Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
				int value = (int) opPostNotificationValue.get(Integer.class);
				return ((int) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager
						.MODE_ALLOWED);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
