package com.renren0351.rrzzapp.custom.toast;

import android.support.annotation.StringRes;
import android.view.View;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/09/13
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public interface IToast {
	int LENGTH_SHORT = 2000;
	int LENGTH_LONG = 3500;

	void makeTextShow(CharSequence text, long duration);

	void makeTextShow(@StringRes int resId, long duration);

	IToast setGravity(int gravity, int xOffset, int yOffset);

	IToast setDuration(long durationMillis);

	/**
	 * 不能和{@link #setText(CharSequence)}一起使用，要么{@link #setView(View)} 要么{@link #setText(CharSequence)}
	 */
	IToast setView(View view);

	IToast setMargin(float horizontalMargin, float verticalMargin);

	/**
	 * 不能和{@link #setView(View)}一起使用，要么{@link #setView(View)} 要么{@link #setText(CharSequence)}
	 */
	IToast setText(CharSequence text);

	void show();

	void cancel();
}
