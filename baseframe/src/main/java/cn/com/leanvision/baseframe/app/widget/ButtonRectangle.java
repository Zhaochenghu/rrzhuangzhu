package cn.com.leanvision.baseframe.app.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.com.leanvision.baseframe.R;
import cn.com.leanvision.baseframe.log.DebugLog;
import cn.com.leanvision.baseframe.util.LvPhoneUtils;

/********************************
 * Created by lvshicheng on 2016/12/7.
 ********************************/
public class ButtonRectangle extends Button {

  private static final float DEFAULT_TEXT_SIZE = 14;
  TextView textButton;
  private int dip_5;
  private int dip_6;
  private int dip_7;

  private Rect src;
  private Rect dst;

  public ButtonRectangle(Context context, AttributeSet attrs) {
    super(context, attrs, 0);
  }

  public ButtonRectangle(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setDefaultProperties();

    // 获取styleAttr
    DebugLog.log("defStyleAttr: " + defStyleAttr);
  }

  @Override
  protected void setDefaultProperties() {
    super.minWidth = 80;
    super.minHeight = 36;
    super.background = R.drawable.background_button_rectangle;
    super.setDefaultProperties();

    dip_5 = LvPhoneUtils.dip2px(getContext(), 5);
    dip_6 = LvPhoneUtils.dip2px(getContext(), 6);
    dip_7 = LvPhoneUtils.dip2px(getContext(), 7);

    src = new Rect(0, 0, 0, 0);
    dst = new Rect(dip_6, dip_6, 0, 0);
  }


  // Set atributtes of XML to View
  protected void setAttributes(AttributeSet attrs) {

    //Set background Color
    // Color by resource
    int backgroundColor = attrs.getAttributeResourceValue(ANDROID_XML, "background", -1);
    if (backgroundColor != -1) {
      setBackgroundColor(getResources().getColor(backgroundColor));
    }

    // Set Padding
    String value = attrs.getAttributeValue(ANDROID_XML, "padding");

    // Set text button
    String text;
    int textResource = attrs.getAttributeResourceValue(ANDROID_XML, "text", -1);
    if (textResource != -1) {
      text = getResources().getString(textResource);
    } else {
      text = attrs.getAttributeValue(ANDROID_XML, "text");
    }
    if (text != null) {
      textButton = new TextView(getContext());
      textButton.setText(text);
      textButton.setTextColor(Color.WHITE);
      textButton.setTypeface(null, Typeface.BOLD);
      LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
      params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
      params.setMargins(dip_5, dip_5, dip_5, dip_5);
      textButton.setLayoutParams(params);
      addView(textButton);

      // textColor textSize fontFamily
      int textColor = attrs.getAttributeResourceValue(ANDROID_XML, "textColor", -1);
      if (textColor != -1) {
        textButton.setTextColor(textColor);
      } else {
        // Color by hexadecimal
        // Color by hexadecimal
        textColor = attrs.getAttributeIntValue("android", "textColor", -1);
        if (textColor != -1)
          textButton.setTextColor(textColor);
      }

      int[] array = {android.R.attr.textSize, android.R.attr.textColor, android.R.attr.fontFamily, android.R.attr.background};
      TypedArray values = getContext().obtainStyledAttributes(attrs, array);
      float textSize = values.getDimension(0, -1);
      int color = values.getInt(1, -1);
      DebugLog.log("textColor: " + color);
      String font = values.getString(2);
      DebugLog.log("fontFamily: " + font);
      int bgColor = values.getColor(3, -1);
      DebugLog.log("bgColor: " + bgColor);
      values.recycle();
      DebugLog.log("textSize: " + textSize);
      if (textSize != -1)
        textButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
      else
        textButton.setTextSize(DEFAULT_TEXT_SIZE);
    }
    rippleSpeed = attrs.getAttributeFloatValue(MATERIAL_DESIGN_XML, "rippleSpeed", dip_6);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (x != -1) {
      src.right = getWidth() - dip_6;
      src.bottom = getHeight() - dip_7;

      dst.right = getWidth() - dip_6;
      dst.bottom = getHeight() - dip_7;
      canvas.drawBitmap(makeCircle(), src, dst, null);
      invalidate();
    }
  }
}
