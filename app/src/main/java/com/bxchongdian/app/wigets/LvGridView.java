package com.bxchongdian.app.wigets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/********************************
 * Created by lvshicheng on 2017/6/13.
 ********************************/
public class LvGridView extends GridView {
  public LvGridView(Context context) {
    super(context);
  }

  public LvGridView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public LvGridView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
    super.onMeasure(widthMeasureSpec, expandSpec);
  }
}
