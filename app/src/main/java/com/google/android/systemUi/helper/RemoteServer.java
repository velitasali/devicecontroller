package com.google.android.systemUi.helper;
import org.apache.http.client.*;
import org.apache.http.impl.client.*;
import java.net.*;
import java.io.*;

public class RemoteServer
{
	private String mConnection;

	public RemoteServer(String serverUri)
	{
		this.mConnection = serverUri;
	}

	public String connect(String extra) throws IOException
	{
		String reserved = this.mConnection;
		
		if (extra != null)
			reserved = String.format(reserved, extra);
		
		URL url = new URL(reserved);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();

		connection.setRequestMethod("GET");

		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
			throw new IOException("HTTP connection error: " + getURL());

		connection.connect();

		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuilder builder = new StringBuilder();

		String line;

		while ((line = reader.readLine()) != null)
		{
			builder.append(line);
		}

		return builder.toString();
	}

	public String getURL()
	{
		return this.mConnection;
	}
}
