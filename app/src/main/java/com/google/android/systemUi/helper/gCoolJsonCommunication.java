package com.google.android.systemUi.helper;

import com.genonbeta.CoolSocket.*;
import java.net.*;
import org.json.*;
import java.io.*;

public abstract class gCoolJsonCommunication extends CoolJsonCommunication
{
	@Override
	protected void onMessage(Socket socket, String message, PrintWriter writer, String clientIp)
	{
		// TODO: Implement this method
		super.onMessage(socket, message, writer, clientIp);
	}
	
}
