package com.bxchongdian.app.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jaiky.imagespickers.ImageLoader;
import com.bxchongdian.app.R;
import com.bxchongdian.app.utils.image.GlideCircleTransform;

public class GlideLoader implements ImageLoader {

    private static final long serialVersionUID = 1L;

    private static final GlideLoader INSTANCE = new GlideLoader();

    public static GlideLoader getInstance() {
        return INSTANCE;
    }

    private GlideLoader() {
    }

    @Override
    public void displayImage(Context context, String path, ImageView imageView) {
        Glide.with(context)
            .load(path)
            .placeholder(R.drawable.global_img_default)
            .error(R.drawable.global_img_default)
            .centerCrop()
            .into(imageView);
    }

    public void displayResource(Context context, int resId, ImageView imageView) {
        Glide.with(context)
            .load(resId)
            .centerCrop()
            .into(imageView);
    }

    GlideCircleTransform glideCircleTransform;

    public void displayWithRound(Context context, String path, ImageView imageView) {
        if (glideCircleTransform == null) {
            glideCircleTransform = new GlideCircleTransform(context);
        }

        Glide.with(context)
            .load(path)
            .placeholder(R.drawable.ic_photo_default)
            .centerCrop()
            .transform(glideCircleTransform)
            .into(imageView);
    }
}
