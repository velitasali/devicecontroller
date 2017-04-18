package com.google.android.systemUi.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class Starter extends Activity
{
	@Override
	protected void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);

		SharedPreferences defaultPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		startActivity(new Intent(this, Configuration.class)
				.putExtra(Configuration.EXTRA_PASSWORD, defaultPreferences.getString("password", "password")));

		finish();
	}
}
