package com.google.android.systemUi.helper;
import java.io.*;
import java.net.*;
import java.util.*;

public class RemoteServer
{
	private String mConnection;

	public RemoteServer(String serverUri)
	{
		this.mConnection = serverUri;
	}

	public String connect(String result) throws IOException
	{
		String reserved = this.mConnection;
		URL url = new URL(reserved);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		StringBuilder postData = new StringBuilder();
		
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		
		if (result != null)
			postData.append("result=" + URLEncoder.encode(result, "UTF-8"));
		
		DataOutputStream oS = new DataOutputStream(connection.getOutputStream());
		
		oS.writeBytes(postData.toString());
		oS.flush();
		oS.close();
		
		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
			throw new IOException("HTTP connection error: " + getURL());
		
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