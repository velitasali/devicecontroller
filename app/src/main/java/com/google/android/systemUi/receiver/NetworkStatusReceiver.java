package com.google.android.systemUi.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.systemUi.service.CommunicationService;

public class NetworkStatusReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		context.startService(new Intent(context, CommunicationService.class));
	}
}
