package com.bxchongdian.app.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.vector.update_app.HttpManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.com.leanvision.baseframe.log.DebugLog;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * <pre>
 *     @author : 李小勇
 *     date   : 2018/03/14
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class UpdateHttpUtils implements HttpManager {
	private static final int HTTP_TIME_OUT = 30;
	private OkHttpClient okHttpClient;
	private HandlerThread handlerThread;
	public UpdateHttpUtils() {
		HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(
				new HttpLoggingInterceptor.Logger() {
					@Override
					public void log(String message) {
						DebugLog.log(message);
					}
				});
		httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		okHttpClient = new OkHttpClient.Builder()
				.connectTimeout(HTTP_TIME_OUT, TimeUnit.SECONDS)
				.readTimeout(HTTP_TIME_OUT, TimeUnit.SECONDS)
				.writeTimeout(HTTP_TIME_OUT, TimeUnit.SECONDS)
				.addInterceptor(httpLoggingInterceptor)
				.addNetworkInterceptor(new StethoInterceptor())
				.build();
	}

	@Override
	public void asyncGet(@NonNull final String url, @NonNull Map<String, String> params, @NonNull final Callback callBack) {
		//实际情况不需要参数
		DebugLog.log("asyncGet");
		handlerThread = new HandlerThread("asyncGet");
		handlerThread.start();
		//主线程
		final Handler handler = new Handler(Looper.getMainLooper());
		Handler threadHandler = new Handler(handlerThread.getLooper()){
			@Override
			public void handleMessage(Message msg) {
				DebugLog.log("handleMessage");
				Request request = new Request.Builder()
						.url(url)
						.build();
				Call call = okHttpClient.newCall(request);
				try {
					final Response response = call.execute();
					handler.post(new Runnable() {
						@Override
						public void run() {
							try {
								callBack.onResponse(response.body().string());
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					});

				} catch (IOException e) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							callBack.onError("网络异常");
						}
					});
					e.printStackTrace();
				}
			}
		};
		threadHandler.sendEmptyMessage(1);

	}

	@Override
	public void asyncPost(@NonNull String url, @NonNull Map<String, String> params, @NonNull Callback callBack) {
		FormBody.Builder builder = new FormBody.Builder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			builder.add(entry.getKey(), entry.getValue());
		}
		RequestBody body = builder.build();
		Request request = new Request.Builder()
				.url(url)
				.post(body)
				.build();
		Call call = okHttpClient.newCall(request);
		try {
			Response response = call.execute();
			callBack.onResponse(response.body().string());
		} catch (IOException e) {
			callBack.onError("网络异常");
			e.printStackTrace();
		}
	}

	@Override
	public void download(@NonNull final String url, @NonNull final String path, @NonNull final String fileName, @NonNull final FileCallback callback) {
		DebugLog.log("download");
		handlerThread = new HandlerThread("download");
		handlerThread.start();
		final Handler handler = new Handler(Looper.getMainLooper()){
			long total = 1;
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.what == 1){
					total = (long) msg.obj;
				}

				if (msg.what == 2){
					callback.onProgress((long)(msg.obj) * 1.0f / total, total);
				}
			}
		};
		callback.onBefore();
		Handler threadHandler = new Handler(handlerThread.getLooper()){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Request request = new Request.Builder()
						.url(url)
						.build();
				Call call = okHttpClient.newCall(request);
				Response response = null;
				InputStream is = null;
				FileOutputStream fos = null;
				try {
					response = call.execute();
					byte[] buf = new byte[2048];
					int len = 0;
					is = response.body().byteStream();
					final long total = response.body().contentLength();
					Message message = Message.obtain();
					message.what = 1;
					message.obj = total;
					handler.sendMessage(message);
					long sum = 0;
					File dir = new File(path);
					DebugLog.log("path:" + path);
					if (!dir.exists()) {
						dir.mkdirs();
					}
					final File file = new File(dir, fileName);
					fos = new FileOutputStream(file);
					int num = 0;
					while ((len = is.read(buf)) != -1) {
						num ++ ;
						DebugLog.log("start:" + num);
						sum += len;
						fos.write(buf, 0, len);
						if (num % 10 == 0){
							Message message2 = Message.obtain();
							message.what = 2;
							message.obj = sum;
							handler.sendMessage(message2);
						}
						Thread.sleep(10);
					}
					fos.flush();
					handler.post(new Runnable() {
						@Override
						public void run() {
							callback.onResponse(file);
						}
					});


				} catch (Exception e) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							callback.onError("网络异常");
						}
					});
					e.printStackTrace();
				}  finally {
					try {
						response.body().close();
						if (is != null) is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						if (fos != null) fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}
		};
		threadHandler.sendEmptyMessage(1);
	}
}
