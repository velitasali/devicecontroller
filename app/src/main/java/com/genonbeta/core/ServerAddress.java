package com.genonbeta.core;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by: veli
 * Date: 11/29/16 9:15 PM
 */

public class ServerAddress
{
	private String mAddress;
	private ArrayList<String> mGetList = new ArrayList<>();
	private ArrayList<String> mPostList = new ArrayList<>();

	public ServerAddress(String serverAddress)
	{
		this.mAddress = serverAddress;
	}

	public ServerAddress(String serverAddress, String... formats)
	{
		this.mAddress = String.format(serverAddress, (Object[]) formats);
	}

	public static String encode(String string) throws UnsupportedEncodingException
	{
		return URLEncoder.encode(string, "UTF-8");
	}

	public ServerAddress addGet(String key, String value) throws UnsupportedEncodingException
	{
		this.mGetList.add(URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8"));

		return this;
	}

	public ServerAddress addPost(String key, String value) throws UnsupportedEncodingException
	{
		this.mPostList.add(URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8"));

		return this;
	}

	public void clearAll()
	{
		mGetList.clear();
		mPostList.clear();
	}

	public boolean hasPostElements()
	{
		return getPostList().size() > 0;
	}

	public ArrayList<String> getGetList()
	{
		return this.mGetList;
	}

	public String getPostBody()
	{
		StringBuilder output = new StringBuilder();

		if (getPostList().size() > 0)
		{
			for (String post : getPostList())
			{
				if (output.length() > 0)
					output.append("&");

				output.append(post);
			}
		}

		return output.toString();
	}

	public ArrayList<String> getPostList()
	{
		return this.mPostList;
	}

	public String getFormattedAddress()
	{
		StringBuilder output = new StringBuilder();

		output.append(mAddress);

		if (mGetList.size() > 0)
			for (String get : mGetList)
			{
				if (output.length() > 0)
					output.append("&");

				output.append(get);
			}

		return output.toString();
	}

	@Override
	public String toString()
	{
		return getFormattedAddress();
	}
}
