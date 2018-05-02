package com.renren0351.rrzzapp.wigets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/********************************
 * Created by lvshicheng on 2017/2/28.
 ********************************/
public class SwipeFrameLayout extends FrameLayout {

  //  private static final String TAG = SwipeFrameLayout.class.getSimpleName();
  private GestureDetector mGestureDetector;
  private OnSwipeListener listener;

  public SwipeFrameLayout(Context context) {
    super(context);
    init();
  }

  public SwipeFrameLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  private void init() {

    mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

      @Override
      public boolean onDown(MotionEvent e) {
        return false;
      }

      @Override
      public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float distX = e2.getX() - e1.getX();
        float distY = e2.getY() - e1.getY();
//        Log.e(TAG, String.format("onFling : distX【%.2f】 distY【%.2f】\nvelocityX【%f】, velocityY【%f】", distX, distY, velocityX, velocityY));
        if (distX > distY || (distX == distY && velocityX > velocityY)) {
//          Log.e(TAG, "水平滑动事件");
          if (listener != null) {
            listener.onHorSwipe();
          }
        } else if (distX < distY || (distX == distY && velocityX < velocityY)) {
//          Log.e(TAG, "竖直滑动事件");
          if (listener != null) {
            listener.onVerSwipe();
          }
        } else {
//          Log.e(TAG, "扯淡事件");
        }
        return true;
      }
    });
  }

  /**
   * @return true 表示拦截事件 false 表示继续向子控件
   */
  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    return super.onInterceptTouchEvent(ev);
  }

  /**
   * @return true 表示消费了事件 false 则事件继续向父控件传递
   */
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    mGestureDetector.onTouchEvent(event);
    return true;
  }

  public void setOnSwipeListener(OnSwipeListener listener) {
    this.listener = listener;
  }

  public interface OnSwipeListener {
    void onHorSwipe();

    void onVerSwipe();
  }
}
