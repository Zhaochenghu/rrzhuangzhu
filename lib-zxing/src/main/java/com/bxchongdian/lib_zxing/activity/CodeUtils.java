package com.bxchongdian.lib_zxing.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.bxchongdian.lib_zxing.camera.CameraManager;
import com.bxchongdian.lib_zxing.decoding.DecodeFormatManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by aaron on 16/7/27.
 * 二维码扫描工具类
 */
public class CodeUtils {

  public static final String RESULT_TYPE    = "result_type";
  public static final String RESULT_STRING  = "result_string";
  public static final int    RESULT_SUCCESS = 1;
  public static final int    RESULT_FAILED  = 2;

  public static final String LAYOUT_ID = "layout_id";

  public static void analyzeBitmap(Context context, String path, final AnalyzeCallback analyzeCallback) {
    SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {
      @Override
      public void onResourceReady(Bitmap mBitmap, GlideAnimation<? super Bitmap> glideAnimation) {
        // 解码的参数
        Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
        // 可以解析的编码类型
        Collection<BarcodeFormat> decodeFormats = new ArrayList<>();
        // 这里设置可扫描的类型，我这里选择了都支持
        decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        // 设置继续的字符编码格式为UTF8
        // hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
        // 设置解析配置参数
        MultiFormatReader multiFormatReader = new MultiFormatReader();
        multiFormatReader.setHints(hints);

        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        int[] pixels = new int[width * height];
        mBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        Result rawResult = null;
        RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);

        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
        try {
          rawResult = multiFormatReader.decodeWithState(binaryBitmap);
        } catch (ReaderException re) {
          // continue
          re.printStackTrace();
        } finally {
          multiFormatReader.reset();
        }

        if (rawResult != null) {
          if (analyzeCallback != null) {
            analyzeCallback.onAnalyzeSuccess(mBitmap, rawResult.getText());
          }
        } else {
          if (analyzeCallback != null) {
            analyzeCallback.onAnalyzeFailed();
          }
        }

      }
    };
    Glide.with(context).load(path).asBitmap().into(target);
  }

  /**
   * 生成二维码图片
   *
   * @param text
   * @param w
   * @param h
   * @param logo
   * @return
   */
  public static Bitmap createImage(String text, int w, int h, Bitmap logo) {
    if (TextUtils.isEmpty(text)) {
      return null;
    }
    try {
      Bitmap scaleLogo = getScaleLogo(logo, w, h);

      int offsetX = w / 2;
      int offsetY = h / 2;

      int scaleWidth = 0;
      int scaleHeight = 0;
      if (scaleLogo != null) {
        scaleWidth = scaleLogo.getWidth();
        scaleHeight = scaleLogo.getHeight();
        offsetX = (w - scaleWidth) / 2;
        offsetY = (h - scaleHeight) / 2;
      }
      Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
      hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
      //容错级别
      hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
      //设置空白边距的宽度
      hints.put(EncodeHintType.MARGIN, 0);
      BitMatrix bitMatrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, w, h, hints);
      int[] pixels = new int[w * h];
      for (int y = 0; y < h; y++) {
        for (int x = 0; x < w; x++) {
          if (x >= offsetX && x < offsetX + scaleWidth && y >= offsetY && y < offsetY + scaleHeight) {
            int pixel = scaleLogo.getPixel(x - offsetX, y - offsetY);
            if (pixel == 0) {
              if (bitMatrix.get(x, y)) {
                pixel = 0xff000000;
              } else {
                pixel = 0xffffffff;
              }
            }
            pixels[y * w + x] = pixel;
          } else {
            if (bitMatrix.get(x, y)) {
              pixels[y * w + x] = 0xff000000;
            } else {
              pixels[y * w + x] = 0xffffffff;
            }
          }
        }
      }
      Bitmap bitmap = Bitmap.createBitmap(w, h,
          Bitmap.Config.ARGB_8888);
      bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
      return bitmap;
    } catch (WriterException e) {
      e.printStackTrace();
    }
    return null;
  }

  private static Bitmap getScaleLogo(Bitmap logo, int w, int h) {
    if (logo == null) return null;
    Matrix matrix = new Matrix();
    float scaleFactor = Math.min(w * 1.0f / 5 / logo.getWidth(), h * 1.0f / 5 / logo.getHeight());
    matrix.postScale(scaleFactor, scaleFactor);
    Bitmap result = Bitmap.createBitmap(logo, 0, 0, logo.getWidth(), logo.getHeight(), matrix, true);
    return result;
  }

  /**
   * 解析二维码结果
   */
  public interface AnalyzeCallback {

    public void onAnalyzeSuccess(Bitmap mBitmap, String result);

    public void onAnalyzeFailed();
  }


  /**
   * 为CaptureFragment设置layout参数
   *
   * @param captureFragment
   * @param layoutId
   */
  public static void setFragmentArgs(CaptureFragment captureFragment, int layoutId) {
    if (captureFragment == null || layoutId == -1) {
      return;
    }

    Bundle bundle = new Bundle();
    bundle.putInt(LAYOUT_ID, layoutId);
    captureFragment.setArguments(bundle);
  }

  public static void isLightEnable(boolean isEnable) {
    if (isEnable) {
      Camera camera = CameraManager.get().getCamera();
      if (camera != null) {
        Camera.Parameters parameter = camera.getParameters();
        parameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(parameter);
      }
    } else {
      Camera camera = CameraManager.get().getCamera();
      if (camera != null) {
        Camera.Parameters parameter = camera.getParameters();
        parameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(parameter);
      }
    }
  }
}
