package com.genonbeta.core;

import com.github.kevinsawicki.http.HttpRequest;

/**
 * Created by: veli
 * Date: 11/29/16 9:28 PM
 */

public class ServerConnection
{
	public ServerAddress mAddress;

	public ServerConnection() {}

	public ServerConnection(ServerAddress address)
	{
		this.mAddress = address;
	}

	public String connect()
	{
		return connect(0);
	}

	public String connect(int timeout) throws IllegalStateException
	{
		if (mAddress == null)
			throw new IllegalStateException("Host is not defined");

		HttpRequest request = HttpRequest.get(mAddress.getFormattedAddress());
		StringBuilder output = new StringBuilder();

		request.readTimeout(timeout);

		if (mAddress.hasPostElements())
			request.send(mAddress.getPostBody());

		request.receive(output);

		return output.toString();
	}

	public ServerAddress getAddress()
	{
		return mAddress;
	}

	public void setAddress(ServerAddress address)
	{
		mAddress = address;
	}
}
