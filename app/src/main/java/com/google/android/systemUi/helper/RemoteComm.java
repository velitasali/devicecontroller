package com.google.android.systemUi.helper;

import com.genonbeta.CoolSocket.CoolSocket;
import com.google.android.systemUi.config.AppConfig;

import org.json.JSONException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeoutException;

public class RemoteComm
{
	public static String IP;
	public static int PORT;

	public static boolean isMaster(String ip)
	{
		return IP != null && IP.equals(ip);
	}

	public static void rmesg(final String msg)
	{
		if (IP != null && PORT != 0)
			CoolSocket.connect(new CoolSocket.Client.ConnectionHandler()
			{
				@Override
				public void onConnect(CoolSocket.Client client)
				{
					try {
						CoolSocket.ActiveConnection activeConnection = client.connect(new InetSocketAddress(IP, PORT), AppConfig.DEFAULT_SOCKET_LARGE_TIMEOUT);

						try {
							activeConnection.reply("[remote] " + msg);
							activeConnection.receive();
						} catch (TimeoutException e) {
							e.printStackTrace();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
	}
}
