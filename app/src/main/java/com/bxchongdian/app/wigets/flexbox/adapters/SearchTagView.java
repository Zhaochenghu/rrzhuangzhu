package com.bxchongdian.app.wigets.flexbox.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.bxchongdian.app.wigets.flexbox.BaseTagView;
import com.bxchongdian.model.bean.FlexBoxBean;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/09/13
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class SearchTagView<T extends FlexBoxBean> extends BaseTagView<T>{
	public SearchTagView(Context context) {
		this(context, null);
	}

	public SearchTagView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs, 0);
	}

	public SearchTagView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public void setItem(T item) {
		super.setItem(item);
		textView.setText(item.getContent());
	}
}
