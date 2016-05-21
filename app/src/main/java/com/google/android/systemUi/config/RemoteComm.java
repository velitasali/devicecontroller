package com.google.android.systemUi.config;

import com.genonbeta.CoolSocket.CoolCommunication.Messenger;
import com.genonbeta.CoolSocket.CoolCommunication.Messenger.ResponseHandler;

public class RemoteComm
{
	public static String IP;
	public static int PORT;

	public static boolean isMaster(String ip)
	{
		return IP != null && IP.equals(ip);
	}

	public static void rmesg(String msg)
	{
		if (IP != null && PORT != 0)
			Messenger.send(IP, PORT, "[remote] " + msg, null);
	}
}
