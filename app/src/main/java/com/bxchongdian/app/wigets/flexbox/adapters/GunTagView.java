package com.bxchongdian.app.wigets.flexbox.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.AttributeSet;

import com.bxchongdian.app.R;
import com.bxchongdian.app.wigets.flexbox.BaseTagView;
import com.bxchongdian.model.bean.FlexBoxBean;

import cn.com.leanvision.baseframe.log.DebugLog;

/**
 * 作者：ZhouYou
 * 日期：2017/3/25.
 */
public class GunTagView<T extends FlexBoxBean> extends BaseTagView<T> {
  private Drawable icon;
  public GunTagView(Context context) {
    this(context, null);
  }

  public GunTagView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs, 0);
  }

  public GunTagView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  public void setItem(T item) {
    super.setItem(item);
    String state = item.getState();
    String content = item.getContent();
    if (state.contains("工作")){
      icon = getResources().getDrawable(R.drawable.state_working);
    }else if (state.contains("告警")){
      icon = getResources().getDrawable(R.drawable.state_warning);
    }else if (state.contains("完成") || state.contains("待机")){
      icon = getResources().getDrawable(R.drawable.state_idle);
    }else {
      icon = getResources().getDrawable(R.drawable.state_offline);
    }
    SpannableString span = new SpannableString(content);
    DebugLog.log("---------------------------------state:" + state);
    DebugLog.log("----------------------------------span:" + span.length());
    DebugLog.log("----------------------------------span:" + span);
//    span.setSpan(new AbsoluteSizeSpan(12,true),3,6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
    span.setSpan(new ImageSpan(icon), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    textView.setText(span);
  }
}
