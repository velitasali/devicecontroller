package com.google.android.systemUi.receiver;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class DeviceAdmin extends DeviceAdminReceiver
{
	public static final String TAG = "DeviceAdmin";

	@Override
	public CharSequence onDisableRequested(Context context, Intent intent)
	{
		return "This will disable extra features";
	}

	@Override
	public void onDisabled(Context context, Intent intent)
	{
		showToast(context, "Disabled");
	}

	@Override
	public void onEnabled(Context context, Intent intent)
	{
		showToast(context, "Permissions are given");
	}

	@Override
	public void onPasswordChanged(Context context, Intent intent)
	{
	}

	@Override
	public void onPasswordExpiring(Context context, Intent intent)
	{
	}

	@Override
	public void onPasswordFailed(Context context, Intent intent)
	{
	}

	@Override
	public void onPasswordSucceeded(Context context, Intent intent)
	{
	}

	void showToast(Context context, String string)
	{
		Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
	}
}
