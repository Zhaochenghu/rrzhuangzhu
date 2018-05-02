package com.renren0351.rrzzapp.wigets;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.FrameLayout;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ValueAnimator;
import com.renren0351.rrzzapp.R;
import com.renren0351.model.bean.StationFilterType;

import cn.com.leanvision.baseframe.util.LvPhoneUtils;

/********************************
 * Created by lvshicheng on 2017/2/22.
 ********************************/
public class FilterView extends FrameLayout {

  private View         rlContainer;
  private SwitchCompat swcAll;
  private SwitchCompat swcIdle;
  private SwitchCompat swcDc;
  private SwitchCompat swcAc;

  private StationFilterType currentFilterType = StationFilterType.FILTER_ALL;

  public FilterView(Context context, StationFilterType type) {
    super(context);
    this.currentFilterType = type;
    initView();
  }

  public FilterView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initView();
  }

  private void initView() {
    LayoutInflater.from(getContext()).inflate(R.layout.view_filter_popu, this);
    final View.OnClickListener listener = new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (v.getId() == R.id.fl_root) {
          hide();
//          return;
        }

//        if (v.getId() == R.id.rl_container) {
//          return;
//        }

//        swcAll.setChecked(false);
//        swcIdle.setChecked(false);
//        swcDc.setChecked(false);
//        swcAc.setChecked(false);
//
//        StationFilterType filterType = null;
//        int id = v.getId();
//        if (id == R.id.swc_idle) {
//          swcIdle.setChecked(true);
//          filterType = StationFilterType.FILTER_IDLE;
//        } else if (id == R.id.swc_dc_charge) {
//          swcDc.setChecked(true);
//          filterType = StationFilterType.FILTER_DC;
//        } else if (id == R.id.swc_ac_charge) {
//          swcAc.setChecked(true);
//          filterType = StationFilterType.FILTER_AC;
//        } else if (id == R.id.swc_all) {
//          swcAll.setChecked(true);
//          filterType = StationFilterType.FILTER_ALL;
//        }
//
//        if (filterType != null && filterType != currentFilterType) {
//          FilterView.this.currentFilterType = filterType;
//          if (FilterView.this.listener != null) {
//            FilterView.this.listener.filterTypeChanged(filterType);
//          }
//        }
      }
    };

    CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          StationFilterType filterType = null;
          if (buttonView == swcAll) {
            filterType = StationFilterType.FILTER_ALL;
          } else if (buttonView == swcIdle) {
            filterType = StationFilterType.FILTER_IDLE;
          } else if (buttonView == swcAc) {
            filterType = StationFilterType.FILTER_AC;
          } else if (buttonView == swcDc) {
            filterType = StationFilterType.FILTER_DC;
          }

          if (filterType != currentFilterType) {
            FilterView.this.currentFilterType = filterType;
            if (FilterView.this.listener != null) {
              FilterView.this.listener.filterTypeChanged(filterType);
            }
          }
        }
        setSwitchDisplay();
      }
    };

    rlContainer = findViewById(R.id.rl_container);
    rlContainer.setOnClickListener(listener);
    findViewById(R.id.fl_root).setOnClickListener(listener);

    swcAll = (SwitchCompat) findViewById(R.id.swc_all);
    swcAll.setOnCheckedChangeListener(checkedChangeListener);
    swcIdle = (SwitchCompat) findViewById(R.id.swc_idle);
    swcIdle.setOnCheckedChangeListener(checkedChangeListener);
    swcDc = (SwitchCompat) findViewById(R.id.swc_dc_charge);
    swcDc.setOnCheckedChangeListener(checkedChangeListener);
    swcAc = (SwitchCompat) findViewById(R.id.swc_ac_charge);
    swcAc.setOnCheckedChangeListener(checkedChangeListener);

    setSwitchDisplay();
  }

  private void setSwitchDisplay() {
    swcAll.setChecked(currentFilterType == StationFilterType.FILTER_ALL);
    swcIdle.setChecked(currentFilterType == StationFilterType.FILTER_IDLE);
    swcDc.setChecked(currentFilterType == StationFilterType.FILTER_DC);
    swcAc.setChecked(currentFilterType == StationFilterType.FILTER_AC);
  }

  public void show(Activity _mActivity) {
    ViewGroup androidContentView = (ViewGroup) _mActivity.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
    ViewGroup.LayoutParams contentParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    androidContentView.addView(this, contentParams);
    AnimatorSet animatorSet = new AnimatorSet();
    ValueAnimator animator1 = ValueAnimator.ofFloat(0.0f, 1.0f);
    animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        float value = (float) animation.getAnimatedValue();
        rlContainer.setScaleX(value);
        rlContainer.setScaleY(value);
      }
    });
    ValueAnimator animator2 = ValueAnimator.ofFloat(-LvPhoneUtils.dip2px(getContext(), 60), 0.0f);
    animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        float value = (float) animation.getAnimatedValue();
        rlContainer.setTranslationX(value);
        rlContainer.setTranslationY(value);
      }
    });
    animatorSet.playTogether(animator1, animator2);
    animatorSet.setDuration(300);
    animatorSet.start();
  }

  public void hide() {
    ValueAnimator animator1 = ValueAnimator.ofFloat(1.0f, 0.0f);
    animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        float value = (float) animation.getAnimatedValue();
        rlContainer.setAlpha(value);
      }
    });
    animator1.addListener(new SimpleAnimatorListener() {
      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        if (FilterView.this.getParent() != null) {
          ((ViewGroup) FilterView.this.getParent()).removeView(FilterView.this);
        }
      }
    });
    animator1.setDuration(300);
    animator1.start();
  }

  private FilterTypeChangedListener listener;

  public void setListener(FilterTypeChangedListener listener) {
    this.listener = listener;
  }

  public static class SimpleAnimatorListener implements Animator.AnimatorListener {

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {

    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
  }

  public interface FilterTypeChangedListener {
    void filterTypeChanged(StationFilterType type);
  }
}
