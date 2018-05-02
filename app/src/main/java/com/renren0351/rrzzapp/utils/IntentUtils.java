package com.renren0351.rrzzapp.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import com.renren0351.rrzzapp.LvApplication;
import com.renren0351.rrzzapp.custom.toast.IToast;
import com.renren0351.rrzzapp.custom.toast.ToastUtils;

import java.io.File;
import java.util.Locale;

/********************************
 * Created by lvshicheng on 2017/2/14.
 ********************************/
public class IntentUtils {

    /**
     * ----------------
     * 外部页面跳转
     * ----------------
     */

    /**
     * reference - http://lbsyun.baidu.com/index.php?title=uri/api/android
     */
    public static void startBaiduMap(Context context, double lat, double lng) {
//    double lat = 40.071857;
//    double lng = 116.360286;
        double[] res = GPSUtil.gcj02_To_Bd09(lat, lng);

        try {
            String uri = String.format(Locale.getDefault(), "baidumap://map/navi?location=%f,%f", res[0], res[1]);
            Intent intent = Intent.parseUri(uri, 0);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.getInstance(LvApplication.getContext()).makeTextShow("设备未安装百度地图", IToast.LENGTH_SHORT);
        }
    }

    /**
     * reference - http://lbs.amap.com/api/amap-mobile/guide/android/navigation
     */
    public static void startGaodeMap(Context context, double lat, double lng) {
        String softName = "sojo导航";

//    double lat = 40.071857;
//    double lng = 116.360286;

        try {
            String uri = String.format(Locale.getDefault(), "androidamap://navi?sourceApplication=%s&lat=%f&lon=%f&dev=0", softName, lat, lng);
            Intent intent = Intent.parseUri(uri, 0);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.getInstance(LvApplication.getContext()).makeTextShow("设备未安装高德地图", IToast.LENGTH_SHORT);
//            CustomToast.makeText(context, "设备未安装高德地图", Toast.LENGTH_SHORT).show();
        }
    }

    public static void turnToAppDetail(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        context.startActivity(intent);
    }

    public static void turnToGps(Context context) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        //此为设置完成后返回到获取界面
        context.startActivity(intent);
    }

    private static boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }
}
