package cn.com.leanvision.baseframe.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/********************************
 * Created by lvshicheng on 2016/12/13.
 ********************************/
public class LvImageLoaderUtil {

  public static void display(Context context, String url, ImageView img) {
    Glide
        .with(context)
        .load(url)
        .into(img);
  }

  public static void display(Context context, String url, ImageView img, int error) {
    Glide
        .with(context)
        .load(url)
        .error(error)
        .into(img);
  }

  public static void display(Context context, String url, ImageView img, int width, int height) {
    Glide
        .with(context)
        .load(url)
        .override(width, height)
        .into(img);
  }

  public static void display(Context context, String url, ImageView img, int width, int height, int error) {
    Glide
        .with(context)
        .load(url)
        .error(error)
        .override(width, height)
        .into(img);
  }
}
