package com.bxchongdian.app;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Environment;
import android.text.TextUtils;

import com.bxchongdian.app.utils.OkGoUpdateHttpUtils;
import com.bxchongdian.model.dagger.ApiModule;
import com.bxchongdian.model.storage.AppInfosPreferences;
import com.vector.update_app.UpdateAppBean;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.UpdateCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import cn.com.leanvision.baseframe.log.DebugLog;
import cn.com.leanvision.baseframe.util.LvTextUtil;
import cn.com.leanvision.baseframe.util.LvTimeUtil;

/********************************
 * Created by zhaochenghu on 2018/5/9.
 ********************************/
public class LvAppUtils {

    public static boolean isCharging() {
        return "1".equals(AppInfosPreferences.get().getCharging());
    }

    /**
     * @return true isLogged
     */
    public static boolean isLogin() {
//    return true;
        return !LvTextUtil.isEmpty(AppInfosPreferences.get().getToken());
    }

    public static void addMoney(double money) {
        double balance = AppInfosPreferences.get().getMoney();
        AppInfosPreferences.get().setMoney(balance + money);
    }

    public static void addSearchRecord(String searchContent) {
        // 这里还涉及一个去重
        String searchRecord = AppInfosPreferences.get().getSearchRecord();
        if (LvTextUtil.isEmpty(searchRecord)) {
            searchRecord = searchContent;
        } else {
            String[] content = searchContent.split(",");
            for (int i = 0; i < content.length; i++) {
                if (searchContent.equals(content[i])) {
                    return;
                }
            }
            searchRecord = String.format("%s,%s", searchRecord, searchContent);
        }
        AppInfosPreferences.get().setSearchRecord(searchRecord);
    }

    public static void clearSearchRecord() {
        AppInfosPreferences.get().setSearchRecord("");
    }

    private static final int RECORD_NUM = 10; // 只保留最近10条记录

    public static String[] getSearchRecord() {
        String searchRecord = AppInfosPreferences.get().getSearchRecord();
        if (LvTextUtil.isEmpty(searchRecord)) {
            return new String[0];
        }

        String[] result = searchRecord.split(",");
        if (result.length > RECORD_NUM) {
            clearSearchRecord();
            String[] temp = new String[RECORD_NUM];
            for (int i = 0; i < temp.length; i++) {
                temp[i] = result[result.length - (temp.length - i)];
                addSearchRecord(temp[i]);
            }
            result = temp;
        }
        return result;
    }

    public static long calculateDiffDate(String startDate, String endDate) {
        long diff = dateToStamp(endDate) - dateToStamp(startDate);
        return diff > 0 ? diff : 0;
    }

    /*
      * 将时间转换为时间戳
      */
    private static long dateToStamp(String s) {
        long ts = 0;
        try {
            ts = LvTimeUtil._y_M_d_Hms.parse(s).getTime();
            return ts;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ts;
    }

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     *
     * @param context
     * @return true 表示开启
     */
    public static boolean isGPSOpen(final Context context) {
        LocationManager locationManager
            = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

    public static String getCommonImageSavePath(Context context) {
//        File file = new File(context.getCacheDir().getPath());
//        DebugLog.log(file.getAbsolutePath());
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SOJO/images");
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
            if (!mkdirs) {
                DebugLog.log("创建失败");
            }
        }
        DebugLog.log(file.getAbsolutePath());
        return file.getAbsolutePath();
    }

    public static void appUpdate(final Activity activity){
        //app 存放路径
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/sojo";
        Map<String, String> params = new HashMap<>();
        params.put("appType", "bxcp");
        new UpdateAppManager
                .Builder()
                .setActivity(activity)
                .setHttpManager(new OkGoUpdateHttpUtils())
                .setUpdateUrl(ApiModule.UPDATE_URL)
                .setParams(params)
                .setTargetPath(path)
                .setTopPic(R.drawable.ic_update_top)
                .build()
                .checkNewApp(new UpdateCallback(){
                    @Override
                    protected UpdateAppBean parseJson(String json) {
                        UpdateAppBean bean = new UpdateAppBean();
                        try {
                            JSONObject object = new JSONObject(json);
                            if (object.getInt("code") == 200){
                                JSONObject content = object.getJSONObject("content");
                                String newVersionName = content.getString("versionName");
                                if (!TextUtils.isEmpty(newVersionName)){
                                    String[] newArrays = newVersionName.split("\\.");
                                    PackageManager pm = activity.getPackageManager();
                                    PackageInfo info = pm.getPackageInfo(activity.getPackageName(), 0);
                                    String versionName = info.versionName;
                                    String[] arrays = versionName.split("\\.");
                                    for (int i = 0; i < newArrays.length; i++) {
                                        if (Integer.parseInt(newArrays[i]) > Integer.parseInt(arrays[i])){
                                            bean.setUpdate("Yes");
                                            break;
                                        }
                                    }
                                    bean.setNewVersion(newVersionName);
                                    bean.setApkFileUrl(content.getString("updateUrl"));
                                    bean.setTargetSize(content.getString("versionSize") + "M");
                                    bean.setUpdateLog( "更新日志：\n" + content.getString("updateLog"));
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        return bean;
                    }

                    @Override
                    protected void hasNewApp(UpdateAppBean updateApp, UpdateAppManager updateAppManager) {
                        DebugLog.log("app has update");
                        updateAppManager.showDialogFragment();
                    }

                    @Override
                    protected void noNewApp() {
                        DebugLog.log("app no update");
                    }
                });
    }
}
