package com.renren0351.rrzzapp.utils;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.Button;

import com.renren0351.rrzzapp.R;

/**
 * Created by Administrator on 2017/2/21 0021.
 */

public class CountDownTimerUtils extends CountDownTimer {
    private Button mButton;

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public CountDownTimerUtils(Button button, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        mButton = button;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        mButton.setClickable(false);
        mButton.setText(millisUntilFinished / 1000 + "秒后可以发送");
        mButton.setBackgroundResource(R.drawable.btn_unable);
        SpannableString span = new SpannableString(mButton.getText().toString());
        span.setSpan(new ForegroundColorSpan(Color.RED), 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mButton.setText(span);
    }

    @Override
    public void onFinish() {
        mButton.setText("获取验证码");
        mButton.setClickable(true);
        mButton.setBackgroundResource(R.drawable.btn_red_stroke);
    }
}
