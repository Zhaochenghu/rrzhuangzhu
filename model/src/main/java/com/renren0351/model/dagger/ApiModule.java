package com.renren0351.model.dagger;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.renren0351.model.ApiService;
import com.renren0351.model.WxpayService;
import com.renren0351.model.bean.CommonResponse;
import com.renren0351.model.events.UnauthEvent;
import com.renren0351.model.response.FeeResponse;
import com.renren0351.model.storage.AppInfosPreferences;
import com.renren0351.model.typeadapter.FeeBeanTypeAdapter;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import cn.com.leanvision.baseframe.log.DebugLog;
import cn.com.leanvision.baseframe.rx.RxBus;
import cn.com.leanvision.baseframe.util.LvTextUtil;
import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/********************************
 * Created by lvshicheng on 2017/2/10.
 ********************************/
@Module
public class ApiModule {
    private static final int HTTP_TIME_OUT = 30;

    // http://117.78.40.137/wx/pay/unifiedprder/cs
    public static final String UPDATE_URL = "http://117.78.40.137/cs/v1/app/version/update";
    //服务器
   private static final String USER_API_SERVER = "117.78.40.137/cs";
    private static final String PAY_API_SERVER = "117.78.40.137/wx";

 //   private static final String USER_API_SERVER = "192.168.58.92:8080/cs";
    // private static final String PAY_API_SERVER = "192.168.58.92:8080/wx";


    //test服务器
//    String USER_API_SERVER = "117.78.40.137/cs-demo";
//    String PAY_API_SERVER = "117.78.40.137/wexin-demo";

    private static final String USER_API_PORT = "";
    // http 缓存数据路径
    String HTTP_PATH     = "charging/http/";
    // 文件缓存的时长 - 默认两个小时
    long   CACHE_TIME    = 2 * 60 * 60 * 1000;

    @Named("UseForBasic")
    @Singleton
    @Provides
    public OkHttpClient provideOkHttpClient() {
        Interceptor headerInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request()
                    .newBuilder()
                    .addHeader("StationStatus-Type", "application/json")
                    .addHeader("Authorization", AppInfosPreferences.get().getToken() == null ? "" : AppInfosPreferences.get().getToken())
                    .build();

                Response response = chain.proceed(request);
                int code = response.code();
                if (code == 401) { // TODO: 2017/9/7 统一处理单点登录事件
                    RxBus.getInstance().postEvent(new UnauthEvent());
                    return new Response.Builder()
                        .request(response.request())
                        .protocol(response.protocol())
                        .code(response.code())
                        .body(null)
                        .build();
                }
                ResponseBody rb = response.body();
                String responseBody = rb.string();

                Gson gson = new Gson();
                CommonResponse commonResponse = gson.fromJson(responseBody, CommonResponse.class);

                ResponseBody body;
                if (commonResponse.isSuccess()) {
                    body = ResponseBody.create(rb.contentType(), responseBody);
                } else {
                    commonResponse.content = null;
                    body = ResponseBody.create(rb.contentType(), gson.toJson(commonResponse));
                }

                return new Response.Builder()
                    .request(response.request())
                        .protocol(response.protocol())
                        .code(response.code())
                        .message(response.message())
                        .body(body)
                        .build();
            }
        };

        // 日志工具 - Debug用
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(
            new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    DebugLog.log(message);
                }
            });
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
            .connectTimeout(HTTP_TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(HTTP_TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(HTTP_TIME_OUT, TimeUnit.SECONDS)
            .addInterceptor(headerInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .addNetworkInterceptor(new StethoInterceptor())
            .build();
    }

    @Singleton
    @Provides
    public ApiService provideApiService(@Named("UseForBasic") OkHttpClient okHttpClient) {
        Gson gson = new GsonBuilder().registerTypeAdapter(FeeResponse.class, new FeeBeanTypeAdapter()).create();
        String url;
        if (LvTextUtil.isEmpty(USER_API_PORT)) {
            url = String.format(Locale.getDefault(), "http://%s/", USER_API_SERVER);
        } else {
            url = String.format(Locale.getDefault(), "http://%s:%s/", USER_API_SERVER, USER_API_PORT);
        }
        Retrofit build = new Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();
        return build.create(ApiService.class);
    }


    @Named("UseForPay")
    @Singleton
    @Provides
    public OkHttpClient provideOkHttpClientForPay() {
        // 日志工具 - Debug用
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(
            new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    DebugLog.log(message);
                }
            });
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
            .connectTimeout(HTTP_TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(HTTP_TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(HTTP_TIME_OUT, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .addNetworkInterceptor(new StethoInterceptor())
            .build();
    }

    @Singleton
    @Provides
    public WxpayService provideWxpayService(@Named("UseForPay") OkHttpClient okHttpClientForPay) {
        Gson gson = new GsonBuilder().create();
        String url = String.format(Locale.getDefault(), "http://%s/", PAY_API_SERVER);

        Retrofit build = new Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClientForPay)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();
        return build.create(WxpayService.class);
    }
}
