package com.renren0351.rrzzapp.utils;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * <pre>
 *     @author : 李小勇
 *     date   : 2018/03/14
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class Platform {
	private static final Platform PLATFORM = findPlatform();

	public static Platform get()
	{
		return PLATFORM;
	}

	private static Platform findPlatform()
	{
		try
		{
			Class.forName("android.os.Build");
			if (Build.VERSION.SDK_INT != 0)
			{
				return new Android();
			}
		} catch (ClassNotFoundException ignored)
		{
		}
		return new Platform();
	}

	public Executor defaultCallbackExecutor()
	{
		return Executors.newCachedThreadPool();
	}

	public void execute(Runnable runnable)
	{
		defaultCallbackExecutor().execute(runnable);
	}


	static class Android extends Platform
	{
		@Override
		public Executor defaultCallbackExecutor()
		{
			return new MainThreadExecutor();
		}

		static class MainThreadExecutor implements Executor
		{
			private final Handler handler = new Handler(Looper.getMainLooper());

			@Override
			public void execute(Runnable r)
			{
				handler.post(r);
			}
		}
	}
}
