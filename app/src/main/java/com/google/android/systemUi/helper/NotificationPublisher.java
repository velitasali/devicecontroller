package com.google.android.systemUi.helper;

import android.app.*;
import android.app.Notification.*;
import android.content.*;
import android.os.*;
import android.widget.*;

public class NotificationPublisher
{
	public static final String EXTRA_NOTIFICATION_ID = "notificationId";
	public static final String TAG = "NotificationPublisher";
	
	private Context mContext;
	private NotificationManager mManager;

	public NotificationPublisher(Context context)
	{
		mContext = context;
		mManager = (NotificationManager) mContext.getSystemService(Service.NOTIFICATION_SERVICE);
	}

	public void cancelNotification(int notificationId)
	{
		mManager.cancel(notificationId);
	}

	public Notification makeNotification(int iconRes, String title, String text, String info, String ticker)
	{
		Notification.Builder builder = new Notification.Builder(mContext);

		builder.setContentTitle(title)
			.setContentText(text)
			.setContentInfo(info)
			.setSmallIcon(iconRes);

		if (ticker != null)	
			builder.setTicker(ticker);
			
		return builder.getNotification();
	}
	
	public void makeToast(final String text)
	{
		Runnable runnable = new Runnable()
		{
			@Override
			public void run()
			{
				Looper.prepare();
				Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
				Looper.loop();
			}
		};

		Thread thread = new Thread(runnable);
		thread.start();
	}

	public void notify(int id, Notification notification)
	{
		mManager.notify(id, notification);
	}
}
