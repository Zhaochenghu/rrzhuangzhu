package cn.com.leanvision.baseframe.app.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import cn.com.leanvision.baseframe.rx.RxBus;
import cn.com.leanvision.baseframe.rx.event.DialogBtnEvent;
import cn.com.leanvision.baseframe.util.LvTextUtil;

/********************************
 * Created by lvshicheng on 2016/12/14.
 ********************************/
public class LvNoticeDialog extends LvBaseDialogFragment {

  public static final String TAG = LvNoticeDialog.class.getSimpleName();

  private String msg;
  private String negative;
  private String positive;

  public static LvNoticeDialog newInstance(String msg, String negative, String positive) {
    LvNoticeDialog fragment = new LvNoticeDialog();
    Bundle bundle = new Bundle();
    bundle.putString(EXTRA_MESSAGE, msg);
    bundle.putString(BTN_NEGATIVE, negative);
    bundle.putString(BTN_POSITIVE, positive);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Bundle args = getArguments();
    msg = args.getString(EXTRA_MESSAGE);
    negative = args.getString(BTN_NEGATIVE);
    positive = args.getString(BTN_POSITIVE);
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    if (!mIsCustomDialog) {
      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
          .setMessage(msg);
      if (!LvTextUtil.isEmpty(negative)) {
        builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            RxBus.getInstance().postEvent(new DialogBtnEvent(0));
          }
        });
      }
      if (!LvTextUtil.isEmpty(positive)) {
        builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            RxBus.getInstance().postEvent(new DialogBtnEvent(1));
          }
        });
      }
      AlertDialog dialog = builder.create();
      dialog.setCanceledOnTouchOutside(false);
      dialog.setCancelable(mIsCancelable);
      return dialog;
    } else {
      return super.onCreateDialog(savedInstanceState);
    }
  }
}
