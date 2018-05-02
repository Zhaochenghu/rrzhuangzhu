package cn.com.leanvision.baseframe.app.dialog;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/********************************
 * Created by lvshicheng on 2016/12/9.
 * <p>
 * 所有对话框统一管理
 ********************************/
public class LvDialogManager {

  /**
   * Dismiss dialog
   */
  public static void removeDialog(FragmentManager fm, String tag) {
    FragmentTransaction ft = fm.beginTransaction();
    Fragment prev = fm.findFragmentByTag(tag);
    if (prev != null) {
      ft.remove(prev);
    }
    ft.commit();
  }

  /**
   * Show loading fragment
   */
  public static LvLoadingDialog showLoading(FragmentManager fm, String tag, String msg) {
    FragmentTransaction ft = fm.beginTransaction();
    Fragment prev = fm.findFragmentByTag(tag);
    if (prev != null) {
      ft.remove(prev);
    }
    ft.addToBackStack(null);
    LvLoadingDialog fragment = LvLoadingDialog.newInstance(msg);
    fragment.setCancelable(false);
    fragment.show(ft, tag);
    return fragment;
  }


  /**
   * Show notice dialog.
   */
  public static LvNoticeDialog showNotice(FragmentManager fm, String tag, String msg, String positive, String negative) {
    FragmentTransaction ft = fm.beginTransaction();
    Fragment prev = fm.findFragmentByTag(tag);
    if (prev != null) {
      ft.remove(prev);
    }
    ft.addToBackStack(null);
    LvNoticeDialog fragment = LvNoticeDialog.newInstance(msg, negative, positive);
    fragment.setCancelable(false);
    fragment.show(ft, tag);
    return fragment;
  }
}
