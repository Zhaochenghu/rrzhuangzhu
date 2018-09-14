package com.bxchongdian.app.custom.toast;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.Toast;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/09/13
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class SystemToast implements IToast {
	private Toast mToast;

	private Context mContext;

	public static IToast makeText(Context context, CharSequence text, long duration) {
		return new SystemToast(context)
				.setText(text)
				.setDuration(duration);
	}

	public static IToast makeText(Context context, @StringRes int resId, long duration)
			throws Resources.NotFoundException{
		return makeText(context, context.getResources().getText(resId), duration);
	}

	public SystemToast(Context context) {
		mContext = context;
		mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
	}

	@Override
	public void makeTextShow(CharSequence text, long duration) {
		new SystemToast(mContext)
				.setText(text)
				.setDuration(duration).show();
	}

	@Override
	public void makeTextShow(@StringRes int resId, long duration) {
		makeTextShow(mContext.getResources().getText(resId), duration);
	}

	@Override
	public IToast setGravity(int gravity, int xOffset, int yOffset) {
		mToast.setGravity(gravity, xOffset, yOffset);
		return this;
	}

	@Override
	public IToast setDuration(long durationMillis) {
		mToast.setDuration((int) durationMillis);
		return this;
	}

	/**
	 * 不能和{@link #setText(CharSequence)}一起使用，要么{@link #setView(View)} 要么{@link #setView(View)}
	 *
	 * @param view 传入view
	 * @return 自身对象
	 */
	@Override
	public IToast setView(View view) {
		mToast.setView(view);
		return this;
	}

	@Override
	public IToast setMargin(float horizontalMargin, float verticalMargin) {
		mToast.setMargin(horizontalMargin, verticalMargin);
		return this;
	}

	/**
	 * 不能和{@link #setView(View)}一起使用，要么{@link #setView(View)} 要么{@link #setView(View)}
	 *
	 * @param text 传入字符串
	 * @return 自身对象
	 */
	@Override
	public IToast setText(CharSequence text) {
		mToast.setText(text);
		return this;
	}

	@Override
	public void show() {
		if (mToast != null) {
			mToast.show();
		}
	}

	@Override
	public void cancel() {
		if (mToast != null) {
			mToast.cancel();
		}
	}
}
