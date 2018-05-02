package com.renren0351.rrzzapp.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/08/01
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class DrawableCenterTextView extends android.support.v7.widget.AppCompatTextView{
	public DrawableCenterTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public DrawableCenterTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DrawableCenterTextView(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Drawable[] drawables = getCompoundDrawables();
		if (drawables != null) {
			Drawable drawableLeft = drawables[0];
			if (drawableLeft != null) {
				float textWidth = getPaint().measureText(getText().toString().trim());
				int drawablePadding = getCompoundDrawablePadding();
				int drawableWidth = drawableLeft.getIntrinsicWidth();
				float bodyWidth = textWidth + drawableWidth + drawablePadding;
				canvas.translate((getWidth() - getPaddingLeft() - getPaddingRight() - bodyWidth) / 2, 0);
			}
		}
		super.onDraw(canvas);
	}
}
