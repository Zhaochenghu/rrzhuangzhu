package cn.com.leanvision.baseframe.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/********************************
 * Created by lvshicheng on 2016/12/7.
 ********************************/
public class CustomView extends RelativeLayout {

  final static String MATERIAL_DESIGN_XML = "http://schemas.android.com/apk/res-auto";
  final static String ANDROID_XML         = "http://schemas.android.com/apk/res/android";

  final int disabledBackgroundColor = Color.parseColor("#E2E2E2");
  int beforeBackground;

  // Indicate if user touched this view the last time
  public boolean isLastTouch = false;

  public CustomView(Context context, AttributeSet attrs) {
    super(context, attrs, 0);
  }

  public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    if (enabled)
      setBackgroundColor(beforeBackground);
    else
      setBackgroundColor(disabledBackgroundColor);
    invalidate();
  }

  boolean animation = false;

  @Override
  protected void onAnimationStart() {
    super.onAnimationStart();
    animation = true;
  }

  @Override
  protected void onAnimationEnd() {
    super.onAnimationEnd();
    animation = false;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (animation)
      invalidate();
  }
}
