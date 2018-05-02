package cn.com.leanvision.baseframe.ui;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import cn.com.leanvision.baseframe.util.LvCommonUtil;

/********************************
 * Created by lvshicheng on 2016/12/9.
 ********************************/
public class LvSimpleNoticeUtil {

  public static void showToast(Context ctx, String info) {
    if (!LvCommonUtil.isBackground(ctx) && LvCommonUtil.isMainThread()) {
      Toast mToast = Toast.makeText(ctx, info, Toast.LENGTH_SHORT);
      mToast.setGravity(Gravity.CENTER, 0, 0);
      mToast.show();
    }
  }

  public static void showToast(Context ctx, int info) {
    if (!LvCommonUtil.isBackground(ctx) && LvCommonUtil.isMainThread()) {
      Toast mToast = Toast.makeText(ctx, info, Toast.LENGTH_SHORT);
      mToast.setGravity(Gravity.CENTER, 0, 0);
      mToast.show();
    }
  }

}
