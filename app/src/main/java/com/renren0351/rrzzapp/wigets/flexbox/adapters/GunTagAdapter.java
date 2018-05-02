package com.renren0351.rrzzapp.wigets.flexbox.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import com.renren0351.model.bean.FlexBoxBean;

import java.util.List;

/**
 * 作者：ZhouYou
 * 日期：2017/3/27
 */
public class GunTagAdapter<T extends FlexBoxBean> extends TagAdapter<GunTagView<T>, T> {

  public GunTagAdapter(Context context, List<T> data) {
    this(context, data, null);
  }

  GunTagAdapter(Context context, List<T> data, List<T> selectItems) {
    super(context, data, selectItems);
  }

  /**
   * 检查item和所选item是否一样
   *
   * @param view
   * @param item
   * @return
   */
  @Override
  protected boolean checkIsItemSame(GunTagView<T> view, T item) {
    return item.equals(view.getItem());
  }

  /**
   * 检查item是否是空指针
   *
   * @return
   */
  @Override
  protected boolean checkIsItemNull(T item) {
    return TextUtils.isEmpty(item.getContent());
  }

  /**
   * 添加标签
   *
   * @param item
   * @return
   */
  @Override
  protected GunTagView<T> addTag(T item) {
    GunTagView<T> tagView = new GunTagView<>(getContext());
    tagView.setPadding(20, 10, 20, 10);

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
