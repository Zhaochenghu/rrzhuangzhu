package com.bxchongdian.app.custom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lljjcoder.citypickerview.widget.CanShow;
import com.lljjcoder.citypickerview.widget.wheel.OnWheelChangedListener;
import com.lljjcoder.citypickerview.widget.wheel.WheelView;
import com.lljjcoder.citypickerview.widget.wheel.adapters.ArrayWheelAdapter;
import com.bxchongdian.app.R;

import java.util.Calendar;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/06/22
 *     desc   :时间选择控件
 *     version: 1.0
 *     延后了10分钟
 * </pre>
 */

public class TimePicker implements CanShow,OnWheelChangedListener{
    private Context context;

    private PopupWindow popwindow;

    private View popView;

    private WheelView mViewHour;

    private WheelView mViewMinute;

//    private WheelView mViewDuration;

    private RelativeLayout mRelativeTitleBg;

    private TextView mTvOK;

    private TextView mTvTitle;

    private TextView mTvCancel;

    /**
     * 当前小时
     */
    protected String mCurrentHour;

    /**
     * 当前分钟
     */
    protected String mCurrentMinute;

    /**
     * 当前时长
     */
//    protected String mCurrentDuration;

    /**
     * 当前时
     */
    protected int mCurrentHourId;

    /**
     * 当前分
     */
    private int mCurrentMinuteId;

    private String[] hours;
    private String[] minutes;
//    private String[] durations;

    private boolean isUpdate;

    private int delayed;

    private TimePicker.OnTimeItemClickListener listener;

    public interface OnTimeItemClickListener {
        void onSelected(String... timeSelected);
        void onCancel();
    }

    public void setOnTimeItemClickListener(TimePicker.OnTimeItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * Default text color
     */
    public static final int DEFAULT_TEXT_COLOR = 0xFF585858;

    /**
     * Default text size
     */
    public static final int DEFAULT_TEXT_SIZE = 18;

    // Text settings
    private int textColor = DEFAULT_TEXT_COLOR;

    private int textSize = DEFAULT_TEXT_SIZE;

    /**
     * 滚轮显示的item个数
     */
    private static final int DEF_VISIBLE_ITEMS = 5;

    // Count of visible items
    private int visibleItems = DEF_VISIBLE_ITEMS;

    /**
     * 小时滚轮是否循环滚动
     */
    private boolean isHourCyclic = false;

    /**
     * 分钟滚轮是否循环滚动
     */
    private boolean isDurationCyclic = true;

    /**
     * 时长滚轮是否循环滚动
     */
    private boolean isMinuteCyclic = true;

    /**
     * item间距
     */
    private int padding = 5;

    /**
     * Color.BLACK
     */
    private String cancelTextColorStr = "#000000";

    /**
     * Color.BLUE
     */
    private String confirmTextColorStr = "#0000FF";

    /**
     * 标题背景颜色
     */
    private String titleBackgroundColorStr = "#E9E9E9";
    /**
     * 标题颜色
     */
    private String titleTextColorStr = "#E9E9E9";

    /**
     * 标题
     */
    private String mTitle = "选择时间";

    /**
     * 设置popwindow的背景
     */
    private int backgroundPop = 0xa0000000;

    private int nowHour;
    private int nowMinute;

    private TimePicker(Builder builder) {

        delayed = 5;
        this.textColor = builder.textColor;
        this.textSize = builder.textSize;
        this.visibleItems = builder.visibleItems;
        this.isHourCyclic = builder.isHourCyclic;
        this.isMinuteCyclic = builder.isMinuteCyclic;
        this.isDurationCyclic = builder.isDurationCyclic;
        this.context = builder.mContext;
        this.padding = builder.padding;
        this.mTitle = builder.mTitle;
        this.titleBackgroundColorStr = builder.titleBackgroundColorStr;
        this.confirmTextColorStr = builder.confirmTextColorStr;
        this.cancelTextColorStr = builder.cancelTextColorStr;

        this.backgroundPop = builder.backgroundPop;
        this.titleTextColorStr = builder.titleTextColorStr;

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        popView = layoutInflater.inflate(R.layout.view_time_picker, null);

        mViewHour = (WheelView) popView.findViewById(R.id.wheel_hour);
        mViewMinute = (WheelView) popView.findViewById(R.id.wheel_minute);
//        mViewDuration= (WheelView) popView.findViewById(R.id.wheel_duration);
        mRelativeTitleBg = (RelativeLayout) popView.findViewById(R.id.rl_title);
        mTvOK = (TextView) popView.findViewById(R.id.tv_confirm);
        mTvTitle = (TextView) popView.findViewById(R.id.tv_title);
        mTvCancel = (TextView) popView.findViewById(R.id.tv_cancel);

        popwindow = new PopupWindow(popView, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        popwindow.setBackgroundDrawable(new ColorDrawable(backgroundPop));
        popwindow.setAnimationStyle(R.style.AnimBottom);
        popwindow.setTouchable(true);
        popwindow.setOutsideTouchable(false);
        popwindow.setFocusable(true);


        /**
         * 设置标题背景颜色
         */
        if (!TextUtils.isEmpty(this.titleBackgroundColorStr)) {
            mRelativeTitleBg.setBackgroundColor(Color.parseColor(this.titleBackgroundColorStr));
        }

        /**
         * 设置标题
         */
        if (!TextUtils.isEmpty(this.mTitle)) {
            mTvTitle.setText(this.mTitle);
        }

        //设置确认按钮文字颜色
        if (!TextUtils.isEmpty(this.titleTextColorStr)) {
            mTvTitle.setTextColor(Color.parseColor(this.titleTextColorStr));
        }

        //设置确认按钮文字颜色
        if (!TextUtils.isEmpty(this.confirmTextColorStr)) {
            mTvOK.setTextColor(Color.parseColor(this.confirmTextColorStr));
        }

        //设置取消按钮文字颜色
        if (!TextUtils.isEmpty(this.cancelTextColorStr)) {
            mTvCancel.setTextColor(Color.parseColor(this.cancelTextColorStr));
        }

        Calendar nowCalendar = Calendar.getInstance();
        nowHour = nowCalendar.get(Calendar.HOUR_OF_DAY);
        //延长分钟
        nowMinute = nowCalendar.get(Calendar.MINUTE) + delayed;

//        initDurationData();
        initHourData();
        initMinuteData();

        // 添加change事件
        mViewHour.addChangingListener(this);
        // 添加change事件
        mViewMinute.addChangingListener(this);
        // 添加change事件
//        mViewDuration.addChangingListener(this);
        // 添加onclick事件
        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCancel();
                hide();
            }
        });
        mTvOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSelected(mCurrentHour, mCurrentMinute);
                hide();
            }
        });

    }

    public static class Builder {
        /**
         * Default text color
         */
        public static final int DEFAULT_TEXT_COLOR = 0xFF585858;

        /**
         * Default text size
         */
        public static final int DEFAULT_TEXT_SIZE = 18;

        // Text settings
        private int textColor = DEFAULT_TEXT_COLOR;

        private int textSize = DEFAULT_TEXT_SIZE;

        /**
         * 滚轮显示的item个数
         */
        private static final int DEF_VISIBLE_ITEMS = 5;

        // Count of visible items
        private int visibleItems = DEF_VISIBLE_ITEMS;

        /**
         * 小时滚轮是否循环滚动
         */
        private boolean isHourCyclic = false;

        /**
         * 市滚轮是否循环滚动
         */
        private boolean isDurationCyclic = true;

        /**
         * 区滚轮是否循环滚动
         */
        private boolean isMinuteCyclic = true;

        private Context mContext;

        /**
         * item间距
         */
        private int padding = 5;

        /**
         * Color.BLACK
         */
        private String cancelTextColorStr = "#000000";

        /**
         * Color.BLUE
         */
        private String confirmTextColorStr = "#0000FF";

        /**
         * 标题背景颜色
         */
        private String titleBackgroundColorStr = "#E9E9E9";

        /**
         * 标题颜色
         */
        private String titleTextColorStr = "#E9E9E9";

        /**
         * 标题
         */
        private String mTitle = "选择时间";

        /**
         * 设置popwindow的背景
         */
        private int backgroundPop = 0xa0000000;

        public Builder(Context context) {
            this.mContext = context;
        }

        /**
         * 设置popwindow的背景
         *
         * @param backgroundPopColor
         * @return
         */
        public TimePicker.Builder backgroundPop(int backgroundPopColor) {
            this.backgroundPop = backgroundPopColor;
            return this;
        }

        /**
         * 设置标题背景颜色
         *
         * @param colorBg
         * @return
         */
        public TimePicker.Builder titleBackgroundColor(String colorBg) {
            this.titleBackgroundColorStr = colorBg;
            return this;
        }

        /**
         * 设置标题背景颜色
         *
         * @param titleTextColorStr
         * @return
         */
        public TimePicker.Builder titleTextColor(String titleTextColorStr) {
            this.titleTextColorStr = titleTextColorStr;
            return this;
        }

        /**
         * 设置标题
         *
         * @param mtitle
         * @return
         */
        public TimePicker.Builder title(String mtitle) {
            this.mTitle = mtitle;
            return this;
        }

        /**
         * 确认按钮文字颜色
         *
         * @param color
         * @return
         */
        public TimePicker.Builder confirTextColor(String color) {
            this.confirmTextColorStr = color;
            return this;
        }

        //        /**
        //         * 取消按钮文字颜色
        //         * @param color
        //         * @return
        //         */
        //        public Builder cancelTextColor(int color) {
        //            this.cancelTextColor = color;
        //            return this;
        //        }

        /**
         * 取消按钮文字颜色
         *
         * @param color
         * @return
         */
        public TimePicker.Builder cancelTextColor(String color) {
            this.cancelTextColorStr = color;
            return this;
        }

        /**
         * item文字颜色
         *
         * @param textColor
         * @return
         */
        public TimePicker.Builder textColor(int textColor) {
            this.textColor = textColor;
            return this;
        }

        /**
         * item文字大小
         *
         * @param textSize
         * @return
         */
        public TimePicker.Builder textSize(int textSize) {
            this.textSize = textSize;
            return this;
        }

        /**
         * 滚轮显示的item个数
         *
         * @param visibleItems
         * @return
         */
        public TimePicker.Builder visibleItemsCount(int visibleItems) {
            this.visibleItems = visibleItems;
            return this;
        }

        /**
         * 省滚轮是否循环滚动
         *
         * @param isHourCyclic
         * @return
         */
        public TimePicker.Builder hourCyclic(boolean isHourCyclic) {
            this.isHourCyclic = isHourCyclic;
            return this;
        }

        /**
         * 市滚轮是否循环滚动
         *
         * @param isDurationCyclic
         * @return
         */
        public TimePicker.Builder minuteCyclic(boolean isDurationCyclic) {
            this.isDurationCyclic = isDurationCyclic;
            return this;
        }

        /**
         * 区滚轮是否循环滚动
         *
         * @param isMinuteCyclic
         * @return
         */
        public TimePicker.Builder durationCyclic(boolean isMinuteCyclic) {
            this.isMinuteCyclic = isMinuteCyclic;
            return this;
        }

        /**
         * item间距
         *
         * @param itemPadding
         * @return
         */
        public TimePicker.Builder itemPadding(int itemPadding) {
            this.padding = itemPadding;
            return this;
        }

        public TimePicker build() {
            TimePicker TimePicker = new TimePicker(this);
            return TimePicker;
        }

    }

    /**
     * 初始化时
     */
    protected void initHourData() {
        if (nowMinute > 59){
            nowHour++;
        }
        hours = new String[24 - nowHour];
        mCurrentHourId = 0;
        for (int i = 0; i <= 23 - nowHour; i++) {
            if (i + nowHour < 10){
                hours[i] = "0" + (i + nowHour);
            }else {
                hours[i] = i + nowHour + "";
            }
        }

        mCurrentHour = hours[mCurrentHourId];
        ArrayWheelAdapter<String> hourAdapter = new ArrayWheelAdapter<>(context,hours);
        mViewHour.setViewAdapter(hourAdapter);
        mViewHour.setCurrentItem(mCurrentHourId);
        mViewHour.setVisibleItems(visibleItems);
        mViewHour.setCyclic(isHourCyclic);
        hourAdapter.setPadding(padding);
        hourAdapter.setTextColor(textColor);
        hourAdapter.setTextSize(textSize);
    }

    protected void initMinuteData(){
        isUpdate = false;
        if (nowMinute > 59){
            nowMinute -= 60;
        }
        minutes = new String[60 - nowMinute];
        mCurrentMinuteId = 0;
        for (int i = 0; i <= 59 - nowMinute; i++) {
            if (i + nowMinute< 10){
                minutes[i] = "0" + (i + nowMinute);
            }else {
                minutes[i] = i +  nowMinute + "";
            }
        }

        mCurrentMinute = minutes[mCurrentMinuteId];
        ArrayWheelAdapter<String> minuteAdapter = new ArrayWheelAdapter<>(context,minutes);
        mViewMinute.setViewAdapter(minuteAdapter);
        mViewMinute.setCurrentItem(mCurrentMinuteId);
        mViewMinute.setVisibleItems(visibleItems);
        mViewMinute.setCyclic(isMinuteCyclic);
        minuteAdapter.setPadding(padding);
        minuteAdapter.setTextColor(textColor);
        minuteAdapter.setTextSize(textSize);
    }

    protected void upMinuteDate(){
        int start;
        if (mCurrentHourId == 0){
            isUpdate = false;
            start = nowMinute;
            if (mCurrentMinuteId > 59 - nowMinute){
                mCurrentMinuteId = 0;
            }
        }else {
            start = 0;
            isUpdate = true;
        }
        minutes = new String[60 - start];
        for (int i = 0; i <= 59 - start; i++) {
            if (i + start < 10){
                minutes[i] = "0" + (i + start);
            }else {
                minutes[i] = i + start + "";
            }
        }
        mCurrentMinute = minutes[mCurrentMinuteId];
        ArrayWheelAdapter<String> minuteAdapter = new ArrayWheelAdapter<>(context,minutes);
        mViewMinute.setViewAdapter(minuteAdapter);
        mViewMinute.setCurrentItem(mCurrentMinuteId);
        mViewMinute.setVisibleItems(visibleItems);
        mViewMinute.setCyclic(isMinuteCyclic);
        minuteAdapter.setPadding(padding);
        minuteAdapter.setTextColor(textColor);
        minuteAdapter.setTextSize(textSize);

    }

//    private void initDurationData(){
//        durations = new String[56];
//        for (int i = 0; i <= 55; i++){
//            durations[i] = (i + 5) + "";
//        }
//
//        mCurrentDuration = durations[0];
//        ArrayWheelAdapter<String> durationAdapter = new ArrayWheelAdapter<>(context,durations);
//        mViewDuration.setViewAdapter(durationAdapter);
//        mViewDuration.setCurrentItem(0);
//        mViewDuration.setVisibleItems(visibleItems);
//        mViewDuration.setCyclic(isDurationCyclic);
//        durationAdapter.setPadding(padding);
//        durationAdapter.setTextColor(textColor);
//        durationAdapter.setTextSize(textSize);
//
//    }

    @Override
    public void setType(int type) {
    }

    @Override
    public void show() {
        if (!isShow()) {
            popwindow.showAtLocation(popView, Gravity.BOTTOM, 0, 0);
        }
    }

    @Override
    public void hide() {
        if (isShow()) {
            popwindow.dismiss();
        }
    }

    @Override
    public boolean isShow() {
        return popwindow.isShowing();
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == mViewHour) {
            mCurrentHourId = mViewHour.getCurrentItem();
            mCurrentHour = hours[mCurrentHourId];
            if (mCurrentHourId == 0){
                upMinuteDate();
            }

            if (mCurrentHourId != 0 && !isUpdate){
                upMinuteDate();
            }
        } else if (wheel == mViewMinute) {
            mCurrentMinuteId = mViewMinute.getCurrentItem();
            mCurrentMinute = minutes[mCurrentMinuteId];
        }
    }
}
