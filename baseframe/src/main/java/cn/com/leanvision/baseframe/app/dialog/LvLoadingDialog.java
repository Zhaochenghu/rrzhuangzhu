package cn.com.leanvision.baseframe.app.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import cn.com.leanvision.baseframe.R;

/********************************
 * Created by lvshicheng on 2016/12/8.
 * <p>
 * 加载页
 ********************************/
public class LvLoadingDialog extends LvBaseDialogFragment {

  public static final String TAG = LvLoadingDialog.class.getSimpleName();

  private String message;

  public static LvLoadingDialog newInstance(String msg) {
    LvLoadingDialog fragment = new LvLoadingDialog();
    Bundle args = new Bundle();
    args.putString(EXTRA_MESSAGE, msg);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Bundle args = getArguments();
    message = args.getString(EXTRA_MESSAGE);
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    if (!mIsCustomDialog) {
      ProgressDialog dialog = new ProgressDialog(getActivity());
      dialog.setMessage(message);
      dialog.setCanceledOnTouchOutside(false);
      dialog.setCancelable(mIsCancelable);
      return dialog;
    } else {
      return super.onCreateDialog(savedInstanceState);
    }
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    if (mIsCustomDialog) {
      View view = inflater.inflate(R.layout.fragment_loading, container, false);
      //启用窗体的扩展特性。
      getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
      return view;
    }
    return super.onCreateView(inflater, container, savedInstanceState);
  }
}