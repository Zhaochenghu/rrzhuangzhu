package com.renren0351.rrzzapp;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.multidex.MultiDex;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.Gson;
import com.pgyersdk.crash.PgyCrashManager;
import com.renren0351.rrzzapp.utils.LvSpfEncryption;
import com.renren0351.model.bean.DaoMaster;
import com.renren0351.model.dagger.ApiComponentHolder;
import com.renren0351.model.dagger.ApiModule;
import com.renren0351.model.dagger.DaggerApiComponent;
import com.renren0351.model.storage.AptPreferencesManager;
import com.renren0351.model.storage.database.DaoSessionHolder;
import com.renren0351.model.storage.database.MimeDevOpenHelper;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.common.QueuedWork;

import java.lang.ref.WeakReference;

import cn.com.leanvision.annotation.spf.LvSpfParser;
import cn.com.leanvision.baseframe.app.LvBaseApplication;

/********************************
 * Created by lvshicheng on 2016/11/30.
 ********************************/
public class LvApplication extends LvBaseApplication {

    public static WeakReference<Activity> topActivity;
    private static Context context;

    //各个平台的配置，建议放在全局Application或者程序入口
    {
        PlatformConfig.setWeixin(LvAppConstants.WX_APP_ID, LvAppConstants.WX_APP_SECRET);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        initDebug(BuildConfig.DEBUG);
        initDagger();
        initRouter();
        initSharedPreference();
        initGreenDao();
        PgyCrashManager.register(this);
        /** 崩溃日志上传初始化 */
//        LvCrashHandler mCrashHandler = LvCrashHandler.getInstance();
//        mCrashHandler.init(this);
    /* UMENG SHARE */
        //开启debug模式，方便定位错误，具体错误检查方式可以查看http://dev.umeng.com/social/android/quick-integration的报错必看，正式发布，请关闭该模式
        MobclickAgent.setDebugMode(BuildConfig.DEBUG);
        MobclickAgent.enableEncrypt(true);
        QueuedWork.isUseThreadPool = false;
        UMShareAPI.get(this);

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                topActivity = new WeakReference<>(activity);
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void initGreenDao() {
        MimeDevOpenHelper devOpenHelper = new MimeDevOpenHelper(this, "charging-db");
        SQLiteDatabase db = devOpenHelper.getWritableDatabase();
        DaoSessionHolder.mDaoSession = new DaoMaster(db).newSession();
    }

    @Override
    protected void initSharedPreference() {
        AptPreferencesManager.init(
            this,
            new LvSpfParser() {
                @Override
                public Object deserialize(Class clazz, String text) {
                    Gson gson = new Gson();
                    return gson.fromJson(text, clazz);
                }

                @Override
                public String serialize(Object object) {
                    Gson gson = new Gson();
                    return gson.toJson(object);
                }
            },
            new LvSpfEncryption());
    }

    protected void initDagger() {
        ApiComponentHolder.sApiComponent = DaggerApiComponent.builder().apiModule(new ApiModule()).build();
    }

    // 初始化路由
    protected void initRouter() {
        ARouter.init(this);
//    ARouter.openLog();
//    ARouter.openDebug();
    }

    public static Context getContext(){
        return context;
    }
}
