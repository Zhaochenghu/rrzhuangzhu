package cn.com.leanvision.baseframe.app.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

/********************************
 * Created by lvshicheng on 2016/12/9.
 ********************************/
public class LvBaseDialogFragment extends DialogFragment {

  protected static final String EXTRA_MESSAGE = "extra_message";
  protected static final String BTN_NEGATIVE  = "negative";
  protected static final String BTN_POSITIVE  = "positive";

  //是否是自定义dialog
  protected boolean mIsCustomDialog = false;

  protected boolean mIsCancelable = true;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setCancelable(mIsCancelable);
  }
}
