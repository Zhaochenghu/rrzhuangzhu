package com.renren0351.rrzzapp.wigets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.renren0351.rrzzapp.R;

/********************************
 * Created by lvshicheng on 2017/2/21.
 ********************************/
public class MarkView extends FrameLayout {

  private ImageView ivMarker;
  private TextView  tvNum;

  public MarkView(Context context) {
    super(context);
    initView();
  }

  public MarkView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initView();
  }

  private void initView() {
    LayoutInflater.from(getContext()).inflate(R.layout.view_mark, this);

    ivMarker = (ImageView) findViewById(R.id.iv_marker);
    tvNum = (TextView) findViewById(R.id.tv_num);
  }

  public void setMarkerBackground(int resid) {
    ivMarker.setImageResource(resid);
  }

  public void setMarkerNum(int num) {
    tvNum.setText(String.valueOf(num));
  }
}
