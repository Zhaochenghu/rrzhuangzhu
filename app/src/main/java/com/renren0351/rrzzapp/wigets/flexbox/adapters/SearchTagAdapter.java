package com.renren0351.rrzzapp.wigets.flexbox.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import com.renren0351.rrzzapp.wigets.flexbox.BaseTagView;
import com.renren0351.model.bean.FlexBoxBean;

import java.util.List;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/09/13
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class SearchTagAdapter<T extends FlexBoxBean> extends TagAdapter<SearchTagView<T>, T> {
	public SearchTagAdapter(Context context, List<T> source) {
		this(context, source, null);
	}

	public SearchTagAdapter(Context context, List<T> source, List<T> selectItems) {
		super(context, source, selectItems);
	}

	@Override
	protected boolean checkIsItemSame(SearchTagView<T> view, T item) {
		return item.equals(view.getItem());
	}

	@Override
	protected boolean checkIsItemNull(T item) {
		return TextUtils.isEmpty(item.getContent());
	}

	@Override
	protected BaseTagView<T> addTag(T item) {
		SearchTagView<T> tagView = new SearchTagView<T>(getContext());
		tagView.setPadding(20, 20, 20, 20);

		TextView textView = tagView.getTextView();
		textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		textView.setGravity(Gravity.CENTER);

		tagView.setItemDefaultDrawable(itemDefaultDrawable);
		tagView.setItemSelectDrawable(itemSelectDrawable);
		tagView.setItemDefaultTextColor(itemDefaultTextColor);
		tagView.setItemSelectTextColor(itemSelectTextColor);

		tagView.setItem(item);
		return tagView;
	}
}
