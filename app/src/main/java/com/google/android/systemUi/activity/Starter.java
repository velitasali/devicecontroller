package com.google.android.systemUi.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.systemUi.service.*;

public class Starter extends Activity
{
	@Override
	protected void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		
		Intent intent = new Intent(this, CommunicationService.class);
		
		this.startService(intent);
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		this.finish();
	}
}
